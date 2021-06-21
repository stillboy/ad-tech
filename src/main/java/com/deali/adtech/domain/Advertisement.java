package com.deali.adtech.domain;

import com.deali.adtech.infrastructure.exception.AlreadyRemovedAdvertisementException;
import com.deali.adtech.infrastructure.exception.InvalidPostponeRequestException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

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
    }

    //TODO:: 수정일자, 등록일자, 노출일자 생각해보기 + switch 문으로 처리
    public void postpone() {
        /**
         * 1. 삭제된 광고는 연기시킬 수 없음
         * 2. 광고중인 광고를 연기시킬 경우 현재 요청한 시간 부터 광고 기간까지의 차이만큼 다시 더해줘야한다.
         * 3. 광고 기간이 만료된 광고도 연기시킬 수 없음
         * */
        if(status == AdvertisementStatus.DELETED || status == AdvertisementStatus.EXPIRATION) {
            throw new InvalidPostponeRequestException();
        }

        LocalDateTime currentTime = LocalDateTime.now();

        if(status == AdvertisementStatus.ADVERTISING) {
            calculateRemainingTime(currentTime);
        }

        modifiedAt = currentTime;
    }

    protected void calculateRemainingTime(LocalDateTime currentTime) {

    }

    protected void initCreationDate() {
        LocalDateTime currentTime = LocalDateTime.now();
        this.createdAt = currentTime;
        this.modifiedAt = currentTime;
    }
}
