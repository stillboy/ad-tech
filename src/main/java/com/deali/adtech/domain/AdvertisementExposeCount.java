package com.deali.adtech.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Table(name = "advertisement_expose_count")
@Entity
public class AdvertisementExposeCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="EXPOSE_COUNT", nullable = false)
    private Long exposeCount;
    @OneToOne
    @JoinColumn(name = "ADVERTISEMENT_ID")
    private Advertisement advertisement;
}
