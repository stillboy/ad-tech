package com.deali.adtech.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "advertisement_expose_count")
public class ExposeCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="EXPOSE_COUNT", nullable = false)
    private Long exposeCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADVERTISEMENT_ID", nullable = false)
    private Advertisement advertisement;

    @Builder
    public ExposeCount(Advertisement advertisement) {
        this.exposeCount = 0L;
        bindAdvertisement(advertisement);
    }

    public void bindAdvertisement(Advertisement advertisement) {
        if(this.advertisement != null) return;
        this.advertisement = advertisement;
    }
}
