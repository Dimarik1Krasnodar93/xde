package com.xde.xde;

import com.xde.model.XDESettings;
import lombok.Getter;


/**
 * Строки запросов к xDE. Формируются 1 раз при загрузке для исключения лишних действий
 * */
public class UrlQueries {
    private static String urlToken;

    private static String urlHistory;
    private static String urlGetTitleOrReceiptAccept;
    private static String urlGetTitleOrReceiptReject;
    private static String urlGetLinkForContentAccept;
    private static String urlGetLinkForContentReject;
    private static String urlGetArchive;

    private static String urlSign;

    public static void setAllUrl(String mainUrl) {
        urlToken = mainUrl + "/token";
        urlHistory = mainUrl + "/v3/statuses/history";
        urlGetTitleOrReceiptAccept = mainUrl + "/document/accept/content";
        urlGetTitleOrReceiptReject = mainUrl + "/document/reject/content";
        urlGetLinkForContentAccept = mainUrl + "/v2/document/accept/content/tasks/";
        urlGetLinkForContentReject = mainUrl + "/v2/document/reject/content/tasks/";

    }
    public static void setAllCryptoUrl(String mainUrl) {
        urlSign = mainUrl + "/v2/sign/";
    }
    public static void setArchiveUrl(String mainUrl) {
        urlGetArchive = mainUrl + "/archive/";
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

    public static String getUrlGetLinkForContentAccept() {
        return urlGetLinkForContentAccept;
    }

    public static String getUrlGetLinkForContentReject() {
        return urlGetLinkForContentReject;
    }

    public static String getUrlGetArchive() {
        return urlGetArchive;
    }

    public static String getUrlSign() {
        return urlSign;
    }
}
