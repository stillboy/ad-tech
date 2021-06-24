package com.deali.adtech.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name="exposed_log")
public class ExposedLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="SCORE", nullable = false)
    private Double score;
    @Column(name="MODIFIED_AT", nullable = false)
    private LocalDateTime modifiedAt;
    @Column(name="EXPOSED_AT", nullable = false)
    private LocalDateTime exposedAt;
    @Column(name="ADVERTISEMENT_ID")
    private Long advertisementId;

    @Builder
    public ExposedLog(Double score, LocalDateTime modifiedAt, Long advertisementId) {
        this.score = score;
        this.modifiedAt = modifiedAt;
        initExposedTime();
        this.advertisementId = advertisementId;
    }

    private void initExposedTime() {
        this.exposedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}
