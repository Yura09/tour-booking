package com.touramigo.service.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.touramigo.service.util.LocalDateTimeSerializer;
import com.touramigo.service.entity.supplier.TermsAndConditionsState;
import com.touramigo.service.entity.supplier.TermsAndConditionsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermsAndConditionsViewDto implements Serializable {

    private UUID id;
    private UUID supplierId;
    private String supplierName;
    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    private String publishedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime publishedAt;
    private int version;
    private TermsAndConditionsState state;
    private TermsAndConditionsType type;
    private String description;
    private String content;

}
