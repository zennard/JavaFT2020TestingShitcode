package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.dao.TextLabelDao;
import org.example.domain.Site;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class TextLabelService {

    protected static final String EMPTY_MESSAGE_PLACEHOLDER = "{EMPTY_MESSAGE}";

    private SiteService siteService;

    private TextLabelDao textLabelDao;

    public Optional<String> getMessage(String key, Locale locale) {
        Site site = siteService.getCurrentSite();
        return getMessageWithFallbackToNoSite(key, locale, site);
    }

    private Optional<String> getMessageWithFallbackToNoSite(String key, Locale locale, Site site) {
        return Stream.of(site, null)
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
            return "";
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
