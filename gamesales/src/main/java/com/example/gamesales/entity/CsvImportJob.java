package com.example.gamesales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "csv_import_jobs")
@Data
@NoArgsConstructor
public class CsvImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImportStatus status;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "processed_rows")
    private Integer processedRows = 0;

    @Column(name = "failed_rows")
    private Integer failedRows = 0;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "error_message")
    private String errorMessage;

    public enum ImportStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    public CsvImportJob(String fileName) {
        this.fileName = fileName;
        this.status = ImportStatus.PENDING;
        this.startTime = new Timestamp(System.currentTimeMillis());
    }
}