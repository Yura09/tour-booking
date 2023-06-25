package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUpdateConnectionModel {

    @NotNull
    private UUID supplierConnectionKeyId;

    private UUID connectionValueId;

    @NotNull
    private String value;
}
