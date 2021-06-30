package com.deali.adtech.presentation.controller;

import com.deali.adtech.application.AdvertisementService;
import com.deali.adtech.infrastructure.repository.JpaAdvertisementSearchDao;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementMapper;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.ResponseCreateAdvertisement;
import com.deali.adtech.presentation.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/core/v1/creative")
public class AdvertisementRestController {
    private final AdvertisementService advertisementService;
    private final JpaAdvertisementSearchDao advertisementSearchDao;

    @PostMapping
    public ResponseEntity createAdvertisement(@ModelAttribute @Valid RequestCreateAdvertisement request) {

        Long advertisementId = advertisementService.createAdvertisement(request);

        ResponseCreateAdvertisement response =
                AdvertisementMapper.INSTANCE.toCreatedResponse(advertisementId,
                        ResponseMessage.ADVERTISEMENT_CREATED);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
