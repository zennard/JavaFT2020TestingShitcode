package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.dao.TextLabelDao;
import org.example.domain.Site;
import org.example.domain.TextLabel;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextLabelService {

    protected static final String EMPTY_MESSAGE_PLACEHOLDER = "{EMPTY_MESSAGE}";
    private static final String EMPTY_STRING = "";

    private SiteService siteService;

    private TextLabelDao textLabelDao;

    public Optional<String> getMessage(String key, Locale locale) {
        Site site = siteService.getCurrentSite();
        return getSiteSpecificMessage(key, locale, site);
    }

    /*
    * returns Optional of label value with this locale, key and for specific site,
    * if label value is empty placeholder - changes it on empty string
    * */
    private Optional<String> getSiteSpecificMessage(String key, Locale locale, Site site) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("site", site);

        //from many duplicate labels for thi site we are getting exactly one???
        List<TextLabel> textLabels = textLabelDao.find(params, locale);

        return getSpecificMessage(textLabels, locale);
    }

    private Optional<String> getSpecificMessage(List<TextLabel> textLabels, Locale locale) {
        List<String> localizedMessages = textLabels.stream()
                .map(label -> label.getValue(locale))
                .collect(Collectors.toList());

        List<String> filteredMessages = filterMessages(localizedMessages, StringUtils::isNotEmpty);
        List<String> transformedMessages = transformMessages(filteredMessages, this::transformEmptyMessage);

        return transformedMessages.stream().findAny();
    }

    private List<String> transformMessages(List<String> messages, UnaryOperator<String> function) {
        return messages.stream()
                .map(function)
                .collect(Collectors.toList());
    }

    private List<String> filterMessages(List<String> messages, Predicate<? super String> predicate) {
        return messages.stream().filter(predicate).collect(Collectors.toList());
    }

    private String transformEmptyMessage(String message) {
        boolean isPlaceholderEmpty = EMPTY_MESSAGE_PLACEHOLDER.equals(message);
        if (isPlaceholderEmpty) {
            return EMPTY_STRING;
        }
        return message;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setTextLabelDao(TextLabelDao textLabelDao) {
        this.textLabelDao = textLabelDao;
    }
}
