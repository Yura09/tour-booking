package com.touramigo.util

import java.beans.PropertyEditorSupport
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateEditor extends PropertyEditorSupport{

    @Override
    public void setAsText(String text) throws IllegalArgumentException{
        if (text != null && !text.empty) {
            setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern(LocalDateFormatter.INPUT_DATE_FORMAT_STRING)))
        }
    }

    @Override
    public String getAsText() throws IllegalArgumentException {
        def currentValue = (LocalDate) getValue()
        if (currentValue != null) {
            return currentValue.format(LocalDateFormatter.inputDateFormatter);
        }
        return null;
    }
}
