package com.deali.adtech.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
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

}
