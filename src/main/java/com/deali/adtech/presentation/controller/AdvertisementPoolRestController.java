package com.deali.adtech.presentation.controller;

import com.deali.adtech.application.AdvertisementPoolService;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/dsp/v1/creative")
@RestController
public class AdvertisementPoolRestController {
    private final AdvertisementPoolService advertisementPoolService;

    @GetMapping
    public ResponseEntity getTop10Creative() {
        List<ResponseCreative> results = advertisementPoolService.getTop10Advertisement();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(results);
    }
}
