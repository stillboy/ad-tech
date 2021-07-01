package com.deali.adtech.presentation.controller;

import com.deali.adtech.application.AdvertisementService;
import com.deali.adtech.infrastructure.repository.JpaAdvertisementSearchDao;
import com.deali.adtech.infrastructure.util.mapper.AdvertisementMapper;
import com.deali.adtech.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/core/v1/creative")
public class AdvertisementRestController {
    private final AdvertisementService advertisementService;
    private final JpaAdvertisementSearchDao advertisementSearchDao;

    @GetMapping("/{creativeId}")
    public ResponseEntity getAdvertisement(@PathVariable("creativeId") Long advertisementId) {
        ResponseAdvertisement response =
                advertisementSearchDao.findAdvertisementById(advertisementId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping
    public ResponseEntity getAdvertisementList(Pageable pageable) {
        Page<ResponseAdvertisement> results =
                advertisementSearchDao.searchAdvertisement(pageable, null);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(results);
    }

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

    @DeleteMapping("/{creativeId}")
    public ResponseEntity deleteAdvertisement(@PathVariable(name = "creativeId")
                                                          Long advertisementId) {
        advertisementService.removeAdvertisement(advertisementId);

        ResponseDeleteAdvertisement response =
                AdvertisementMapper.INSTANCE.toDeletedResponse(
                        ResponseMessage.ADVERTISEMENT_DELETE);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
