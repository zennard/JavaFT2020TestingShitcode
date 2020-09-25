package org.example.dao;

import org.example.domain.TextLabel;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TextLabelDao {


    public List<TextLabel> find(Map<String, Object> params, Locale locale) {
        return List.of(new TextLabel());
    }


}
