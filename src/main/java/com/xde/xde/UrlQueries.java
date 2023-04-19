package com.xde.xde;

import com.xde.model.XDESettings;
import lombok.Getter;


public class UrlQueries {
    private static String urlToken;

    private static String urlHistory;
    private static String urlGetTitleOrReceiptAccept;
    private static String urlGetTitleOrReceiptReject;

    public static void setAllUrl(String mainUrl) {
        urlToken = mainUrl + "/token";
        urlHistory = mainUrl + "/v3/statuses/history";
        urlGetTitleOrReceiptAccept = mainUrl + "/document/accept/content";
        urlGetTitleOrReceiptReject = mainUrl + "/document/reject/content";
    }

    public static String getUrlToken() {
        return urlToken;
    }

    public static String getUrlHistory() {
        return urlHistory;
    }

    public static String getUrlGetTitleOrReceiptAccept() {
        return urlGetTitleOrReceiptAccept;
    }

    public static String getUrlGetTitleOrReceiptReject() {
        return urlGetTitleOrReceiptReject;
    }
}
