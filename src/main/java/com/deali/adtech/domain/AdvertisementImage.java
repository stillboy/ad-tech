package com.deali.adtech.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
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

    @Builder
    public AdvertisementImage(String name, String extension, Long size, String path) {
        this.name = name;
        this.extension = extension;
        this.size = size;
        this.path = path;
    }

    public void bindAdvertisement(Advertisement advertisement) {
        if(this.advertisement != null) return;
        this.advertisement = advertisement;
    }
}
