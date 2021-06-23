package com.deali.adtech.presentation.controller;

import com.deali.adtech.application.AdvertisementPoolService;
import com.deali.adtech.infrastructure.repository.AdvertisementDocumentRepository;
import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/dsp/v1")
public class AdvertisementPoolController {
    private final AdvertisementPoolService advertisementPoolService;

    @GetMapping("/display")
    public ModelAndView getTop10Creative(ModelAndView modelAndView) {
        List<ResponseCreative> results = advertisementPoolService.getTop10Advertisement();

        modelAndView.addObject("creativeList", results);
        modelAndView.setViewName("creativePoolList");

        return modelAndView;
    }
}
