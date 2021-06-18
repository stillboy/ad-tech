package com.deali.adtech.domain;


import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name="ADVERTISEMENT_IMAGE")
public class AdvertisementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="NAME", nullable = false)
    private String name;

    @Column(name="EXTENSION", nullable = false)
    private String extension;

    @Column(name="SIZE", nullable = false)
    private Long size;

    @Column(name="PATH", nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADVERTISEMENT_ID", nullable = false)
    private Advertisement advertisement;
}
