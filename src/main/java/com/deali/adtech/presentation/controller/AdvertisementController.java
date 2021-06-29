package com.deali.adtech.presentation.controller;

import com.deali.adtech.application.AdvertisementService;
import com.deali.adtech.infrastructure.repository.JpaAdvertisementSearchDao;
import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.deali.adtech.presentation.dto.RequestEditAdvertisement;
import com.deali.adtech.presentation.dto.ResponseAdvertisement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/core/v1/creative")
public class AdvertisementController {
    private final AdvertisementService advertisementService;
    private final JpaAdvertisementSearchDao advertisementSearchDao;

    @GetMapping
    public ModelAndView home(ModelAndView modelAndView) {
        modelAndView.setViewName("home");

        return modelAndView;
    }

    @GetMapping("/post")
    public ModelAndView createAdvertisementView(ModelAndView modelAndView) {
        modelAndView.setViewName("creativeForm");

        return modelAndView;
    }

    @PostMapping("/post")
    public String createAdvertisement(@Valid @ModelAttribute RequestCreateAdvertisement requestCreateAdvertisement,
                                 ModelAndView modelAndView) {

        advertisementService.createAdvertisement(requestCreateAdvertisement);

        return "redirect:/core/v1/creative";
    }

    @GetMapping("/{advertisementId}")
    public ModelAndView getAdvertisementDetails(@PathVariable(name = "advertisementId") Long id,
                                           ModelAndView modelAndView) throws IOException {

        ResponseAdvertisement result =  advertisementSearchDao.findAdvertisementById(id);
        modelAndView.addObject("creative", result);

        modelAndView.setViewName("detail");

        return modelAndView;
    }

    @PostMapping("/update/{advertisementId}")
    public String editAdvertisement(@PathVariable(name="advertisementId") Long advertisementId,
            @ModelAttribute RequestEditAdvertisement requestEditAdvertisement) {

        requestEditAdvertisement.setId(advertisementId);
        advertisementService.editAdvertisement(requestEditAdvertisement);

        return "redirect:/core/v1/creative/" + advertisementId;
    }

    @GetMapping("/list")
    public ModelAndView getAdvertisementList(@RequestParam(name = "pageNumber") int pageNumber,
                                        @RequestParam(name = "pageSize") int pageSize,
                                        ModelAndView modelAndView) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ResponseAdvertisement> page =
                advertisementSearchDao.searchAdvertisement(pageable, null);

        modelAndView.addObject("page", page);
        modelAndView.addObject("creativeList", page.getContent());
        modelAndView.setViewName("creativeList");

        return modelAndView;
    }

    @PostMapping("/{advertisementId}/delete")
    public String deleteAdvertisement(@PathVariable(name = "advertisementId") Long id) {
        advertisementService.removeAdvertisement(id);

        return "redirect:/core/v1/creative";
    }

    private String convertFile(String path) throws IOException {
        File file = new File(path);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private String getExtension(String path) {
        int lastDot = path.lastIndexOf(".");
        return path.substring(lastDot+1, path.length());
    }
}
