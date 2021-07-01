package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.*;
import com.deali.adtech.infrastructure.util.event.AdvertisementRemovedEvent;
import com.deali.adtech.infrastructure.util.event.Events;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    @Column(name="MODIFIED_AT", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name="EXPIRY_DATE", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name="EXPOSURE_DATE", nullable = false)
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
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.from(this.createdAt);
        initExposureDate(exposureDate);
        initExpiryDate(expiryDate);
        this.status = AdvertisementStatus.WAITING;
    }

    public void editTitle(String title) {
        if(title == null || title.trim().length() == 0) {
            throw new InvalidTitleException();
        }
        this.title = title;
    }

    public void changeWinningBid(Integer winningBid) {
        if(winningBid == null || winningBid < 1 || winningBid > 10) {
            throw new InvalidWinningBidException();
        }

        this.winningBid = winningBid;
    }

    public void updateModifiedAt() {
        LocalDateTime currentTime = LocalDateTime.now();

        if(currentTime == null || currentTime.isBefore(this.modifiedAt)) {
            throw new InvalidModifiedTimeException();
        }

        this.modifiedAt = currentTime;
    }

    public void editAdvertisement(String title, Integer winningBid, LocalDateTime exposureDate,
                                  LocalDateTime expiryDate) {

        if(status == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        editTitle(title);
        changeWinningBid(winningBid);
        changeAdvertisingDuration(exposureDate, expiryDate);
        updateModifiedAt();
    }

    //TODO::spring fsm 으로 변경 가능할 듯
    protected void changeAdvertisingDuration(LocalDateTime exposureDate, LocalDateTime expiryDate) {
        //TODO::유효성 검증 따로 분리
        if(expiryDate == null || exposureDate == null || expiryDate.isBefore(exposureDate) ||
        exposureDate.isBefore(this.exposureDate)) {
            throw new InvalidChangeDurationException();
        }

        if(exposureDate.equals(this.exposureDate) && expiryDate.equals(this.exposureDate)) return ;

        if(expiryDate.isAfter(this.expiryDate)) {
            extend(expiryDate);
        }

        if(exposureDate.isAfter(this.exposureDate)) {
            postpone(exposureDate);
        }

        if(expiryDate.isBefore(this.expiryDate)) {
            reduce(expiryDate);
        }
    }

    protected void postpone(LocalDateTime newExposureDate) {
        if(newExposureDate == null || newExposureDate.isBefore(exposureDate)) {
            throw new InvalidExposureDateException();
        }

        switch (status) {
            case WAITING:
            case ADVERTISING:
                status = AdvertisementStatus.WAITING;
                //TODO::이벤트 발행해서 광고 풀에서 제거해야함
                break;
            case EXPIRED:
            case DELETED:
            default:
                throw new InvalidExposureDateException();
        }
    }

    protected void extend(LocalDateTime newExpiryDate) {
        if(newExpiryDate == null
                || newExpiryDate.isBefore(exposureDate)
                || newExpiryDate.isBefore(expiryDate)) {
            throw new InvalidExpiryDateException();
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
            default:
                throw new InvalidExpiryDateException();
        }
    }

    protected void reduce(LocalDateTime newExpiryDate) {
        if(newExpiryDate == null || newExpiryDate.isAfter(this.expiryDate)) {
            throw new InvalidExpiryDateException();
        }

        switch (status) {
            case WAITING:
            case ADVERTISING:

            case EXPIRED:
            case DELETED:
            default:
                throw  new InvalidExpiryDateException();
        }
    }

    public void remove() {
        if(status == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        status = AdvertisementStatus.DELETED;
        Events.raise(new AdvertisementRemovedEvent(this));
    }

    protected Duration calculateRemainingTime(LocalDateTime newExposureDate) {
        if(newExposureDate == null) {
            throw new InvalidExposureDateException();
        }

        Duration duration = Duration.between(exposureDate, expiryDate);
        this.exposureDate = newExposureDate;
        this.expiryDate = newExposureDate.plus(duration);

        return duration;
    }

    protected void initExposureDate(LocalDateTime exposureDate) {
        if(exposureDate == null || exposureDate.isBefore(this.createdAt)) {
            throw new InvalidExposureDateException();
        }

        this.exposureDate = exposureDate;
    }

    protected void initExpiryDate(LocalDateTime expiryDate) {
        if(expiryDate == null || expiryDate.isBefore(this.createdAt)) {
            throw new InvalidExpiryDateException();
        }

        if(this.exposureDate == null || expiryDate.isBefore(this.exposureDate)) {
            throw new InvalidExpiryDateException();
        }

        this.expiryDate = expiryDate;
    }
}
