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
    public AdvertisementImage(String name, Long size, String path) {
        splitNameAndExtension(name);
        this.size = size;
        this.path = path;
    }

    public void bindAdvertisement(Advertisement advertisement) {
        if(this.advertisement != null) return;
        this.advertisement = advertisement;
    }

    public void editNameAndExtension(String fileName) {
        if(fileName == null || fileName.trim().length() == 0) return;
        splitNameAndExtension(fileName);
    }

    public void changeSize(Long size) {
        if(size == null || size <= 0) return;
        this.size = size;
    }

    public String getFullPathName() {
        return path + name + "." + extension;
    }

    private void splitNameAndExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        this.name = fileName.substring(0, lastDot);
        this.extension = fileName.substring(lastDot+1, fileName.length());
    }
}
