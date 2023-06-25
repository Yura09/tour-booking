package com.touramigo.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.touramigo.service.model.PartnerModel;

public class PartnerHistoryMapperUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static PartnerModel getPartnerModel(Object object) {
        return objectMapper.convertValue(object, PartnerModel.class);
    }
}

