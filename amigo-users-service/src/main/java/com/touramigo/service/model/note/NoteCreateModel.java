package com.touramigo.service.model.note;

import com.touramigo.service.enums.NoteType;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NoteCreateModel {
    @NotNull
    private UUID parentId;

    @NotNull
    private NoteType parentType;

    @NotNull
    private String content;
}
