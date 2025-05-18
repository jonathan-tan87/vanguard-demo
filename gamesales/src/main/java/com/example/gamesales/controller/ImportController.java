package com.example.gamesales.controller;

import com.example.gamesales.dto.ImportStatusResponseDto;
import com.example.gamesales.entity.CsvImportJob;
import com.example.gamesales.repository.CsvImportJobRepository;
import com.example.gamesales.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImportController {

    private final CsvImportService csvImportService;
    private final CsvImportJobRepository csvImportJobRepository;

    @PostMapping("/import")
    public ResponseEntity<ImportStatusResponseDto> importCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (file.getContentType() != null && !file.getContentType().equals("text/csv") && !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
             return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                 .body(new ImportStatusResponseDto());
        }

        CsvImportJob job = csvImportService.initiateImport(file);
        csvImportService.processImport(job.getId(), file);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                             .body(ImportStatusResponseDto.fromEntity(job));
    }

    @GetMapping("/import/status/{jobId}")
    public ResponseEntity<ImportStatusResponseDto> getImportStatus(@PathVariable Long jobId) {
        CsvImportJob job = csvImportJobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Import job not found"));
        return ResponseEntity.ok(ImportStatusResponseDto.fromEntity(job));
    }
}