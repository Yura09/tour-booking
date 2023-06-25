package com.touramigo.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractAuditingModel {
    private UUID createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
}
