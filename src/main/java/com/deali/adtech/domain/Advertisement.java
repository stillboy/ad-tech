package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "advertisement")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="TITLE", nullable = false)
    private String title;

    @Column(name="WINNING_BID", nullable = false)
    private Integer winningBid;

    @Column(name="CREATED_AT", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ssZ")
    private LocalDateTime createdAt;

    @Column(name="MODIFIED_AT", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ssZ")
    private LocalDateTime modifiedAt;

    @Column(name="EXPIRY_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ssZ")
    private LocalDateTime expiryDate;

    @Column(name="EXPOSURE_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ssZ")
    private LocalDateTime exposureDate;

    @Column(name="STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;

    @OneToOne(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private AdvertisementExposeCount advertisementExposeCount;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private List<AdvertisementImage> images;

    @Builder
    public Advertisement(String title, Integer winningBid, LocalDateTime expiryDate,
                         LocalDateTime exposureDate) {
        this.title = title;
        this.winningBid = winningBid;
        this.expiryDate = expiryDate;
        this.exposureDate = exposureDate;
        this.status = AdvertisementStatus.WAITING;
        initCreationDate();
    }

    public void editTitle(String title) {
        if(title == null || title.trim().length() == 0) return;
        this.title = title;
    }

    public void changeWinningBid(Integer winningBid) {
        if(winningBid == null || winningBid < 1 || winningBid > 10) {
            throw new InvalidWinningBidException();
        }

        this.winningBid = winningBid;
    }

    public void updateModifiedAt(LocalDateTime modifiedAt) {
        if(modifiedAt == null || modifiedAt.isBefore(this.modifiedAt)) return;
        this.modifiedAt = modifiedAt;
    }

    public void editAdvertisement(String title, Integer winningBid) {
        if(status == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        editTitle(title);
        changeWinningBid(winningBid);
        updateModifiedAt(getCurrentTimeWithAsiaTimeZone());
    }

    public void postpone(LocalDateTime newExposureDate) {
        switch (status) {
            case WAITING:
            case ADVERTISING:
                calculateRemainingTime(newExposureDate);
                status = AdvertisementStatus.WAITING;
                break;
            case EXPIRED:
            case DELETED:
                throw new InvalidPostponeRequestException();
        }
    }

    public void extend(LocalDateTime newExpiryDate) {
        if(newExpiryDate.isBefore(exposureDate) || newExpiryDate.isBefore(expiryDate)) {
            throw new RuntimeException();
        }

        switch (status) {
            case WAITING:
            case ADVERTISING:
                this.expiryDate = newExpiryDate;
                break;
            case EXPIRED:
                this.expiryDate = newExpiryDate;
                this.status = AdvertisementStatus.WAITING;
                break;
            case DELETED:
                throw new InvalidExposureDateException();
        }
    }

    public void remove() {
        if(status == AdvertisementStatus.DELETED) {
            throw new InvalidRemoveRequestException();
        }

        status = AdvertisementStatus.DELETED;
    }

    private Duration calculateRemainingTime(LocalDateTime newExposureDate) {
        if(newExposureDate.isBefore(exposureDate)) {
            throw new InvalidExposureDateException();
        }

        Duration duration = Duration.between(exposureDate, expiryDate);
        this.exposureDate = newExposureDate;
        this.expiryDate = newExposureDate.plus(duration);

        return duration;
    }

    //TODO::java 타임존 관련 이슈 해결필요
    protected void initCreationDate() {
        LocalDateTime currentTime = getCurrentTimeWithAsiaTimeZone();

        this.createdAt = currentTime;
        this.modifiedAt = currentTime;
    }

    //TODO::파라미터로 String을 넣고 거기서 타임존 판별해서 가져오도록 수정
    private LocalDateTime getCurrentTimeWithAsiaTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return now.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}
