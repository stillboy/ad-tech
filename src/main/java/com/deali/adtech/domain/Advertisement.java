package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.*;
import com.deali.adtech.infrastructure.util.event.AdvertisementChangedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementPostponedEvent;
import com.deali.adtech.infrastructure.util.event.AdvertisementRemovedEvent;
import com.deali.adtech.infrastructure.util.event.Events;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    //TODO:: 광고 노출 기간이라는 Value로 보고 클래스를 따로 정의, 광고 노출 관련 로직은 해당 클래스에서 다루는 걸로...
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

    //TODO:: 광고 노출 기간 관련해서는 꼭 value로 따로 분리해서 로직 처리 하기
    public void editAdvertisement(String title, Integer winningBid, LocalDateTime exposureDate,
                                  LocalDateTime expiryDate) {

        if(status == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        editTitle(title);
        changeWinningBid(winningBid);
        changeAdvertisingDuration(exposureDate, expiryDate);
        updateModifiedAt();

        Events.raise(new AdvertisementChangedEvent(this));
    }

    protected void changeAdvertisingDuration(LocalDateTime exposureDate, LocalDateTime expiryDate) {
        //TODO::유효성 검증 따로 분리
        if(expiryDate == null || exposureDate == null || expiryDate.isBefore(exposureDate) ||
        exposureDate.isBefore(this.exposureDate)) {
            throw new InvalidChangeDurationException();
        }

        if(exposureDate.equals(this.exposureDate) && expiryDate.equals(this.exposureDate)) return ;

        LocalDateTime currentTime = LocalDateTime.now();

        if(expiryDate.isBefore(currentTime)) {
            throw new InvalidChangeDurationException();
        }

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
                this.exposureDate = newExposureDate;
                break;
            case ADVERTISING:
                this.exposureDate = newExposureDate;
                status = AdvertisementStatus.WAITING;
                Events.raise(new AdvertisementPostponedEvent(this));
                break;
            case EXPIRED:
            case DELETED:
            default:
                //TODO:: 새로 추가되는 상태값들에 대한 런타임 익셉션 따로 정의
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
                this.expiryDate = newExpiryDate;
                break;
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
