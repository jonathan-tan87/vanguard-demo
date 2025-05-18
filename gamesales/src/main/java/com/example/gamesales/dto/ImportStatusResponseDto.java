package com.example.gamesales.dto;

import com.example.gamesales.entity.CsvImportJob;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ImportStatusResponseDto {
    private Long jobId;
    private String fileName;
    private CsvImportJob.ImportStatus status;
    private Integer totalRows;
    private Integer processedRows;
    private Integer failedRows;
    private Timestamp startTime;
    private Timestamp endTime;
    private String errorMessage;

    public static ImportStatusResponseDto fromEntity(CsvImportJob job) {
        ImportStatusResponseDto dto = new ImportStatusResponseDto();
        dto.setJobId(job.getId());
        dto.setFileName(job.getFileName());
        dto.setStatus(job.getStatus());
        dto.setTotalRows(job.getTotalRows());
        dto.setProcessedRows(job.getProcessedRows());
        dto.setFailedRows(job.getFailedRows());
        dto.setStartTime(job.getStartTime());
        dto.setEndTime(job.getEndTime());
        dto.setErrorMessage(job.getErrorMessage());
        return dto;
    }
}