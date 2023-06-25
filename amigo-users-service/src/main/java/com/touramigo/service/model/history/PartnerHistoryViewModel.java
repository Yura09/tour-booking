package com.touramigo.service.model.history;

import com.touramigo.service.entity.history.PartnerHistory;
import com.touramigo.service.model.PartnerModel;
import com.touramigo.service.util.DateUtil;
import com.touramigo.service.util.PartnerHistoryMapperUtil;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartnerHistoryViewModel {
    private UUID id;
    private UUID modifiedBy;
    private String fullName;
    private String dateLog;
    private UUID partnerId;
    private PartnerModel data;

    public static PartnerHistoryViewModel create(PartnerHistory history) {
        PartnerHistoryViewModel viewModel = new PartnerHistoryViewModel();
        if (Objects.nonNull(history.getId())) {
            viewModel.setId(history.getId());
        }
        if (Objects.nonNull(history.getModifiedBy())) {
            viewModel.setModifiedBy(history.getModifiedBy());
        }
        if (Objects.nonNull(history.getDateLog())) {

            viewModel.setDateLog(DateUtil.format(history.getDateLog()));
        }
        if (Objects.nonNull(history.getData())) {
            viewModel.setData(PartnerHistoryMapperUtil.getPartnerModel(history.getData()));
        }
        if (Objects.nonNull(history.getPartner())) {
            viewModel.setPartnerId(history.getPartner().getId());
        }
        return viewModel;
    }
}
