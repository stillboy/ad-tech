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

@RequiredArgsConstructor
@RestController
@RequestMapping("/core/v1/creative")
public class AdvertisementRestController {
    private final AdvertisementService advertisementService;
    private final JpaAdvertisementSearchDao advertisementSearchDao;
    private final AdvertisementMapper advertisementMapper;

    @GetMapping("/{creativeId}")
    public ResponseEntity getAdvertisement(@PathVariable("creativeId") Long advertisementId) {
        ResponseAdvertisement response =
                advertisementSearchDao.findAdvertisementById(advertisementId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping
    public ResponseEntity getAdvertisementList(Pageable pageable,
                                               AdvertisementSearchCondition searchCondition) {

        System.out.println(searchCondition.getTitle());
        System.out.println(searchCondition.getStatus());
        Page<ResponseAdvertisement> results =
                advertisementSearchDao.searchAdvertisement(pageable, searchCondition);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(results);
    }

    @PostMapping
    public ResponseEntity createAdvertisement(@ModelAttribute @Valid RequestCreateAdvertisement request) {
        Long advertisementId = advertisementService.createAdvertisement(request);

        ResponseCreateAdvertisement response
                = advertisementMapper.toCreatedResponse(advertisementId, ResponseMessage.ADVERTISEMENT_CREATED);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{creativeId}")
    public ResponseEntity editAdvertisement(@PathVariable("creativeId") Long advertisementId,
            @ModelAttribute @Valid RequestEditAdvertisement request) {
        request.setId(advertisementId);

        advertisementService.editAdvertisement(request);

        ResponseEditAdvertisement response =
                new ResponseEditAdvertisement(ResponseMessage.ADVERTISEMENT_EDITED.getMessage(),
                        advertisementId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{creativeId}")
    public ResponseEntity pauseAdvertisement(@PathVariable("creativeId") Long advertisementId) {
        advertisementService.pauseAdvertisement(advertisementId);

        ResponsePauseAdvertisement response
                = new ResponsePauseAdvertisement(ResponseMessage.ADVERTISEMENT_PAUSED.getMessage()
                , advertisementId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{creativeId}")
    public ResponseEntity deleteAdvertisement(@PathVariable(name = "creativeId")
                                                          Long advertisementId) {
        advertisementService.removeAdvertisement(advertisementId);

        ResponseDeleteAdvertisement response
                = advertisementMapper.toDeletedResponse(ResponseMessage.ADVERTISEMENT_DELETE);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
