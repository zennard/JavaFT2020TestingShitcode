package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.dao.TextLabelDao;
import org.example.domain.Site;

import java.util.*;
import java.util.stream.Stream;

public class TextLabelService {

    protected static final String EMPTY_MESSAGE_PLACEHOLDER = "{EMPTY_MESSAGE}";
    private static final String EMPTY_STRING = "";
    public static final Site DEFAULT_SITE_VALUE = null;

    private SiteService siteService;
    private TextLabelDao textLabelDao;

    public Optional<String> getMessage(String key, Locale locale) {
        Site site = siteService.getCurrentSite();
        return getMessageOrDefault(key, locale, site);
    }

    private Optional<String> getMessageOrDefault(String key, Locale locale, Site site) {
        return Stream.of(site, DEFAULT_SITE_VALUE)
                .distinct()
                .map(s -> getSiteSpecificMessage(key, locale, s))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::replaceEmptyMessagePlaceholder)
                .findFirst();
    }

    private Optional<String> getSiteSpecificMessage(String key, Locale locale, Site site) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("site", site);
        return textLabelDao.find(params, locale).stream()
                .map(textLabelModel -> textLabelModel.getValue(locale))
                .filter(StringUtils::isNotEmpty)
                .findAny();
    }

    private String replaceEmptyMessagePlaceholder(String string) {
        if (EMPTY_MESSAGE_PLACEHOLDER.equals(string)) {
            return EMPTY_STRING;
        }
        return string;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setTextLabelDao(TextLabelDao textLabelDao) {
        this.textLabelDao = textLabelDao;
    }

}
