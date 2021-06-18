package com.deali.adtech.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
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
}
