package org.example.service;

import org.example.domain.Site;

public class SiteService {

    private Site currentSite = new Site(1);

    public Site getCurrentSite() {
        return currentSite;
    }

}
