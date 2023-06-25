package com.touramigo.service.controller;

import com.touramigo.service.service.TemporalLinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/temporal-link")
public class TemporalLinkController {
    private final TemporalLinkService temporalLinkService;

    public TemporalLinkController(TemporalLinkService temporalLinkService) {
        this.temporalLinkService = temporalLinkService;
    }

    @GetMapping
    public Boolean isTokenExist(@RequestParam("token") String token) {
        return temporalLinkService.findNotExpiredByToken(token).isPresent();
    }
}
