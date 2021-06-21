package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;
import com.deali.adtech.infrastructure.exception.InvalidPostponeRequestException;
import com.deali.adtech.infrastructure.exception.InvalidExposureDateException;
import com.deali.adtech.infrastructure.exception.InvalidRemoveRqeustException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:zz")
    private LocalDateTime createdAt;

    @Column(name="MODIFIED_AT", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:zz")
    private LocalDateTime modifiedAt;

    @Column(name="EXPIRY_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime expiryDate;

    @Column(name="EXPOSURE_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime exposureDate;

    @Column(name="STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;

    @OneToOne(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private ExposeCount exposeCount;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private List<AdvertisementImage> images = new ArrayList<>();

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
        if(winningBid == null || winningBid < 1 || winningBid > 10) return;
        this.winningBid = winningBid;
    }

    public void updateModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    //TODO::수정일이 소재를 수정하면 갱신이 되는건지 아니면 따로 수정일을 갱신하는 건지
    public void editAdvertisement(String title, Integer winningBid) {
        if(status == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        editTitle(title);
        changeWinningBid(winningBid);
        this.modifiedAt = LocalDateTime.now();
    }

    public void postpone(LocalDateTime newExposureDate) {
        switch (status) {
            case EXPIRATION:
            case DELETED:
                throw new InvalidPostponeRequestException();
            case WAITING:
            case ADVERTISING:
                calculateRemainingTime(newExposureDate);
                break;
        }
    }

    public void extend(LocalDateTime newExpiryDate) {
        switch (status) {
            case EXPIRATION:
            case DELETED:
                throw new RuntimeException();
            case WAITING:
            case ADVERTISING:

                break;
        }
    }

    public void remove() {
        if(status == AdvertisementStatus.DELETED) {
            throw new InvalidRemoveRqeustException();
        }

        status = AdvertisementStatus.DELETED;
    }

    //TODO:: 광고 연기, 갱신에 대한 중복 로직 처리 개선방안
    private Duration calculateRemainingTime(LocalDateTime newExposureDate) {
        if(newExposureDate.isBefore(exposureDate)) {
            throw new InvalidExposureDateException();
        }

        Duration duration = Duration.between(exposureDate, expiryDate);
        this.exposureDate = newExposureDate;
        this.expiryDate = newExposureDate.plus(duration);

        return duration;
    }

    protected void initCreationDate() {
        LocalDateTime currentTime = LocalDateTime.now();
        this.createdAt = currentTime;
        this.modifiedAt = currentTime;
    }
}
