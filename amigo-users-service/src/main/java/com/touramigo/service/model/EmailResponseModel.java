package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailResponseModel {

    private boolean success;

    private String emailTo;

    private String subject;

    private String errorReason;

}
