package com.example.gamesales.repository;

import com.example.gamesales.entity.CsvImportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvImportJobRepository extends JpaRepository<CsvImportJob, Long> {
}