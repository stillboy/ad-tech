package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.domain.ExposedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExposedLogRepository extends JpaRepository<ExposedLog, Long> {
}
