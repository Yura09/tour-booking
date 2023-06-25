package com.touramigo.service.model.note;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NoteUpdateModel {

    @NotNull
    private String content;
}
