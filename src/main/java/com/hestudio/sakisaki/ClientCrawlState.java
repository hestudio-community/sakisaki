package com.hestudio.sakisaki;

public final class ClientCrawlState {
    private static boolean crawling;

    private ClientCrawlState() {
    }

    public static boolean isCrawling() {
        return crawling;
    }

    public static void setCrawling(boolean value) {
        crawling = value;
    }
}
