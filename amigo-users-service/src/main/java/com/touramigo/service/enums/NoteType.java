package com.touramigo.service.enums;

import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import java.util.stream.Stream;

public enum NoteType {
    SUPPLIER,
    CONTACT,
    PARTNER,
    RULE;

    public static NoteType getByValue(String type) {
        return Stream.of(NoteType.values())
            .filter(x -> x.name().equalsIgnoreCase(type))
            .findAny().orElseThrow(() ->
                new InvalidDataException(UserServiceErrorMessages.NOTE_PARENT_TYPE_NOT_FOUND));
    }
}
