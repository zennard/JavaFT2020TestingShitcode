package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.dao.TextLabelDao;
import org.example.domain.Site;
import org.example.domain.TextLabel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextLabelServiceTest {

    private static final String KEY = "testKey";
    private static final String TEXT_LABEL_VALUE_WITH_SITE = "testValueWithSite";
    private static final String TEXT_LABEL_VALUE_WITH_NO_SITE = "testValueWithNoSite";

    @Mock
    private TextLabelDao textLabelDao;

    @Mock
    private SiteService siteService;

    @Mock
    private Site site;

    @Mock
    private TextLabel textLabelWithSite;

    @Mock
    private TextLabel textLabelWithNoSite;

    private Map<String, Object> paramsWithSite;

    private Map<String, Object> paramsWithNoSite;

    private TextLabelService textLabelService = new TextLabelService();

    @Before
    public void setUp() {
        when(siteService.getCurrentSite()).thenReturn(site);
        when(textLabelWithSite.getValue(Locale.ENGLISH)).thenReturn(TEXT_LABEL_VALUE_WITH_SITE);
        when(textLabelWithNoSite.getValue(Locale.ENGLISH)).thenReturn(TEXT_LABEL_VALUE_WITH_NO_SITE);

        textLabelService.setSiteService(siteService);
        textLabelService.setTextLabelDao(textLabelDao);

        paramsWithSite = createParams(site);
        when(textLabelDao.find(paramsWithSite, Locale.ENGLISH)).thenReturn(Arrays.asList(textLabelWithSite));

        paramsWithNoSite = createParams(null);
        when(textLabelDao.find(paramsWithNoSite, Locale.ENGLISH)).thenReturn(Arrays.asList(textLabelWithNoSite));
    }

    @Test
    public void shouldReturnOptionalTextLabelValue() {
        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.of(TEXT_LABEL_VALUE_WITH_SITE));
    }

    @Test
    public void shouldFallbackToNoSiteWhenNoSiteMessageFound() {
        when(textLabelDao.find(paramsWithSite, Locale.ENGLISH)).thenReturn(Collections.emptyList());

        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.of(TEXT_LABEL_VALUE_WITH_NO_SITE));
    }

    @Test
    public void shouldFallbackToNoSiteWhenTextLabelValueIsEmpty() {
        when(textLabelWithSite.getValue(Locale.ENGLISH)).thenReturn(StringUtils.EMPTY);

        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.of(TEXT_LABEL_VALUE_WITH_NO_SITE));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenTextLabelValueIsEmpty() {
        when(textLabelDao.find(paramsWithSite, Locale.ENGLISH)).thenReturn(Collections.emptyList());
        when(textLabelWithNoSite.getValue(Locale.ENGLISH)).thenReturn(StringUtils.EMPTY);

        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenNoTextLabelsFound() {
        when(textLabelDao.find(paramsWithSite, Locale.ENGLISH)).thenReturn(Collections.emptyList());
        when(textLabelDao.find(paramsWithNoSite, Locale.ENGLISH)).thenReturn(Collections.emptyList());

        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReplaceEmptyValuePlaceholderWithEmptyString() {
        when(textLabelWithSite.getValue(Locale.ENGLISH))
                .thenReturn(TextLabelService.EMPTY_MESSAGE_PLACEHOLDER);

        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.of(StringUtils.EMPTY));
    }

    @Test
    public void shouldFallbackToNoSiteWhenNoCurrentBaseSite() {
        when(siteService.getCurrentSite()).thenReturn(null);

        Optional<String> actual = textLabelService.getMessage(KEY, Locale.ENGLISH);

        assertThat(actual).isEqualTo(Optional.of(TEXT_LABEL_VALUE_WITH_NO_SITE));
    }

    private Map<String, Object> createParams(Site site) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", KEY);
        params.put("site", site);
        return params;
    }
}
