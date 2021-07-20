package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.*;
import com.deali.adtech.infrastructure.util.event.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
        editTitle(title);
        changeWinningBid(winningBid);
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.from(this.createdAt);
        initExposureDate(exposureDate);
        initExpiryDate(expiryDate);
        this.status = AdvertisementStatus.WAITING;
    }

    public void editTitle(String title) {
        if(title == null) {
            throw new InvalidTitleException();
        }

        title = title.replaceAll("^\\s+","");
        title = title.replaceAll("\\s+$","");

        if(title.length() < 2 || title.length() > 255) {
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

        if(currentTime.isBefore(this.modifiedAt)) {
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
        changeDuration(exposureDate, expiryDate);
        updateModifiedAt();

        Events.raise(new AdvertisementChangedEvent(this));
    }

    public void postpone() {
        this.status = AdvertisementStatus.WAITING;
    }

    public void updateExpiredDuration() {
        this.status = AdvertisementStatus.WAITING;
    }

    public void remove() {
        if(status == AdvertisementStatus.DELETED) {
            throw new AlreadyRemovedAdvertisementException();
        }

        status = AdvertisementStatus.DELETED;
        Events.raise(new AdvertisementRemovedEvent(this));
    }

    protected void initExposureDate(LocalDateTime exposureDate) {
        if(exposureDate == null || exposureDate.isBefore(this.createdAt)) {
            throw new InvalidExposureDateException();
        }

        this.exposureDate = exposureDate;
    }

    protected void initExpiryDate(LocalDateTime expiryDate) {
        if(expiryDate == null || expiryDate.isBefore(this.createdAt)
                || expiryDate.equals(exposureDate)) {
            throw new InvalidExpiryDateException();
        }

        if(this.exposureDate == null || expiryDate.isBefore(this.exposureDate)) {
            throw new InvalidExpiryDateException();
        }

        this.expiryDate = expiryDate;
    }

    public void pause() {
        if(!(this.status == AdvertisementStatus.ADVERTISING
                || this.status == AdvertisementStatus.WAITING)) {
            throw new InvalidPausedRequestException();
        }

        this.status = AdvertisementStatus.PAUSED;
        Events.raise(new AdvertisementPausedEvent(this));
    }

    public void unPause() {
        if(this.status != AdvertisementStatus.PAUSED) {
            throw new StatusMismatchException();
        }

        LocalDateTime currentTime = LocalDateTime.now();

        if(this.exposureDate.isBefore(currentTime) && this.expiryDate.isAfter(currentTime)) {
            this.status = AdvertisementStatus.ADVERTISING;
            Events.raise(new AdvertisementUnPausedEvent(this));
        } else {
            this.status = AdvertisementStatus.WAITING;
        }
    }

    public void changeDuration(LocalDateTime exposureDate, LocalDateTime expiryDate) {
        if(this.exposureDate.equals(exposureDate) && this.expiryDate.equals(expiryDate)) {
            return;
        }

        status.changeDuration(this, exposureDate, expiryDate);
    }

    public void changeExposureDate(LocalDateTime exposureDate) {
        this.exposureDate = exposureDate;
    }

    public void changeExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
