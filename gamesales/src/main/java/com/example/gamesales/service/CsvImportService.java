package com.example.gamesales.service;

import com.example.gamesales.dto.CsvGameSaleDto;
import com.example.gamesales.entity.CsvImportJob;
import com.example.gamesales.entity.GameSale;
import com.example.gamesales.repository.CsvImportJobRepository;
import com.example.gamesales.repository.GameSaleRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportService {

    private final CsvImportJobRepository importJobRepository;
    private final GameSaleRepository gameSaleRepository;
    private final Validator validator;
    private final AggregationService aggregationService;

    private static final int BATCH_SIZE = 100000;
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("M/d/yy H:mm")
    };
     private static final SimpleDateFormat CSV_DATE_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public CsvImportJob initiateImport(MultipartFile file) {
        CsvImportJob job = new CsvImportJob(file.getOriginalFilename());
        return importJobRepository.save(job);
    }

    @Async
    @Transactional
    public void processImport(Long jobId, MultipartFile file) {
        CsvImportJob job = importJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        job.setStatus(CsvImportJob.ImportStatus.PROCESSING);
        importJobRepository.save(job);

        long startTime = System.currentTimeMillis();
        log.info("Starting CSV import for job ID: {}, file: {}", jobId, file.getOriginalFilename());

        List<GameSale> batch = new ArrayList<>(BATCH_SIZE);
        AtomicInteger totalRowsCounter = new AtomicInteger(0);
        AtomicInteger processedRowsCounter = new AtomicInteger(0);
        AtomicInteger failedRowsCounter = new AtomicInteger(0);
        StringBuilder errorMessages = new StringBuilder();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<CsvGameSaleDto> csvToBean = new CsvToBeanBuilder<CsvGameSaleDto>(reader)
                    .withType(CsvGameSaleDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1)
                    .build();

            for (CsvGameSaleDto csvDto : csvToBean) {
                totalRowsCounter.incrementAndGet();
                try {
                    GameSale gameSale = parseAndValidateDto(csvDto);
                    batch.add(gameSale);

                    if (batch.size() >= BATCH_SIZE) {
                        saveBatch(batch, job, processedRowsCounter, failedRowsCounter, errorMessages);
                        batch.clear();
                    }
                } catch (Exception e) {
                    failedRowsCounter.incrementAndGet();
                    errorMessages.append("Row ")
                                 .append(totalRowsCounter.get())
                                 .append(": Invalid data - ")
                                 .append(e.getMessage())
                                 .append("\n");
                    log.warn("Skipping invalid row {}: {}", totalRowsCounter.get(), e.getMessage());
                }
                 if (totalRowsCounter.get() % (BATCH_SIZE * 10) == 0) {
                    log.info("Job ID: {} - Processed {} rows so far.", jobId, totalRowsCounter.get());
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch, job, processedRowsCounter, failedRowsCounter, errorMessages);
            }

            job.setStatus(CsvImportJob.ImportStatus.COMPLETED);
            log.info("CSV import completed for job ID: {}. Processed: {}, Failed: {}. Total time: {} ms",
                    jobId, processedRowsCounter.get(), failedRowsCounter.get(), (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            log.error("Error during CSV import for job ID: {}", jobId, e);
            job.setStatus(CsvImportJob.ImportStatus.FAILED);
            job.setErrorMessage("Import failed: " + e.getMessage() + "\nPartial errors:\n" + errorMessages.toString());
        } finally {
            job.setTotalRows(totalRowsCounter.get());
            job.setProcessedRows(processedRowsCounter.get());
            job.setFailedRows(failedRowsCounter.get());
            job.setEndTime(new Timestamp(System.currentTimeMillis()));
            if (errorMessages.length() > 0 && job.getErrorMessage() == null) {
                job.setErrorMessage("Completed with errors. See details:\n" + errorMessages.substring(0, Math.min(errorMessages.length(), 1000))); // Truncate if too long
            }
            importJobRepository.save(job);
        }
    }

    private void saveBatch(List<GameSale> batch, CsvImportJob job,
                           AtomicInteger processedCounter, AtomicInteger failedCounter, StringBuilder errorCollector) {
        if (batch.isEmpty()) return;
        try {
        	List<GameSale> savedSales = gameSaleRepository.saveAll(batch);
        	gameSaleRepository.flush();
        	aggregationService.updateAggregatesAsync(new ArrayList<>(savedSales));
            processedCounter.addAndGet(batch.size());
            job.setProcessedRows(processedCounter.get());
        } catch (Exception e) {
            log.error("Error saving batch for job {}: {}", job.getId(), e.getMessage(), e);
            failedCounter.addAndGet(batch.size());
            errorCollector.append("Batch failed to save. Size: ").append(batch.size()).append(". Error: ").append(e.getMessage()).append("\n");
            job.setFailedRows(failedCounter.get());
        }
    }


    private GameSale parseAndValidateDto(CsvGameSaleDto dto) throws Exception {
        GameSale gameSale = new GameSale();
        List<String> errors = new ArrayList<>();

        try {
            gameSale.setCsvRowId(Integer.parseInt(dto.getIdStr()));
        } catch (NumberFormatException e) { errors.add("Invalid ID: " + dto.getIdStr()); }

        try {
            dto.gameNo = Integer.parseInt(dto.getGameNoStr());
            gameSale.setGameNo(dto.gameNo);
        } catch (NumberFormatException e) { errors.add("Invalid Game No: " + dto.getGameNoStr());}

        gameSale.setGameName(dto.getGameName());
        gameSale.setGameCode(dto.getGameCode());

        try {
            dto.type = Integer.parseInt(dto.getTypeStr());
            gameSale.setType(dto.type);
        } catch (NumberFormatException e) { errors.add("Invalid Type: " + dto.getTypeStr());}

        try {
            dto.costPrice = new BigDecimal(dto.getCostPriceStr());
            gameSale.setCostPrice(dto.costPrice);
        } catch (NumberFormatException e) { errors.add("Invalid Cost Price: " + dto.getCostPriceStr());}

        try {
            dto.tax = new BigDecimal(dto.getTaxStr());
            gameSale.setTax(dto.tax);
        } catch (NumberFormatException e) { errors.add("Invalid Tax: " + dto.getTaxStr());}

        try {
            dto.salePrice = new BigDecimal(dto.getSalePriceStr());
            gameSale.setSalePrice(dto.salePrice);
        } catch (NumberFormatException e) { errors.add("Invalid Sale Price: " + dto.getSalePriceStr());}
        
        try {
            LocalDateTime parsedDateOfSales = null;
            for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
                try {
                    parsedDateOfSales = LocalDateTime.parse(dto.getDateOfSaleStr(), formatter);
                    break;
                } catch (DateTimeParseException ignored) {}
            }
            if (parsedDateOfSales == null) {
                 try {
                    parsedDateOfSales = CSV_DATE_SDF.parse(dto.getDateOfSaleStr()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                 } catch (java.text.ParseException e) {
                    errors.add("Invalid Date of Sale format: " + dto.getDateOfSaleStr());
                 }
            }
            if (parsedDateOfSales != null) {
                gameSale.setDateOfSale(Timestamp.valueOf(parsedDateOfSales));
            }
        } catch (Exception e) { errors.add("Invalid Date of Sale: " + dto.getDateOfSaleStr() + " - " + e.getMessage()); }


        Set<ConstraintViolation<CsvGameSaleDto>> violationsDto = validator.validate(dto);
         if (!violationsDto.isEmpty()) {
            errors.addAll(violationsDto.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.toList()));
        }

        Set<ConstraintViolation<GameSale>> violationsEntity = validator.validate(gameSale);
        if (!violationsEntity.isEmpty()) {
            errors.addAll(violationsEntity.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.toList()));
        }
        
        if (!errors.isEmpty()) {
            throw new Exception(String.join(", ", errors));
        }
        return gameSale;
    }
}