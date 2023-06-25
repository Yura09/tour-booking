package com.touramigo.service.controller;

import com.touramigo.service.model.history.PartnerHistoryViewModel;
import com.touramigo.service.service.PartnerHistoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class PartnerHistoryController {
    private final PartnerHistoryService partnerHistoryService;

    @GetMapping
    public List<PartnerHistoryViewModel> findAllByPartner(@RequestParam UUID partnerId){
        return partnerHistoryService.findAllByPartner(partnerId);
    }
}
