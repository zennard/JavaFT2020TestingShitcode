package org.example.domain;

import java.util.Locale;

public class TextLabel {

    private String value = "some text";

    public TextLabel() {
    }

    public TextLabel(String value) {
        this.value = value;
    }

    public String getValue(Locale locale) {
        return value;
    }
}
