package com.touramigo.service.service;

import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.model.PartnerModel;
import com.touramigo.service.model.history.PartnerHistoryViewModel;
import java.util.List;
import java.util.UUID;

public interface PartnerHistoryService {
    List<PartnerHistoryViewModel> findAllByPartner(UUID id);

    void create(PartnerModel partnerModel);
}
