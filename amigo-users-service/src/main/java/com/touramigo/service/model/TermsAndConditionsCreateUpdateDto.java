package com.touramigo.service.model;

import com.touramigo.service.entity.supplier.TermsAndConditionsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermsAndConditionsCreateUpdateDto implements Serializable {

    private UUID id;

    @NotNull
    private TermsAndConditionsType type;

    private String description;

    @NotBlank
    private String content;

}
