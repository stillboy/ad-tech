package com.deali.adtech.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Column(name="STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;

    @OneToOne(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private ExposeCount exposeCount;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private List<AdvertisementImage> images = new ArrayList<>();

    @Builder
    public Advertisement(String title, Integer winningBid, LocalDateTime createdAt, LocalDateTime expiryDate) {
        this.title = title;
        this.winningBid = winningBid;
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
        this.expiryDate = expiryDate;
        this.status = AdvertisementStatus.WAITING;
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

    }
}
