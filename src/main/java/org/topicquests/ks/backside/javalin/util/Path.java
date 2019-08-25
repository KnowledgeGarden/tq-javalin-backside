package org.topicquests.ks.backside.javalin.util;

public class Path {

    public static class Web {
        public static final String INDEX = "/index";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String BOOKS = "/books";
        public static final String ONE_BOOK = "/books/:isbn";
        // a json REST feed
        public static final String PKM = "/pkm";
        // a json REST feed for the WordGramViewer
        public static final String WGV = "/wgv";
        // a json REST for hypothesis harvester
        public static final String HYP = "/hyp/all/:offset/:count";
        // full text search
        public static final String HYP_TEXT = "/hyp/text/:text/:offset/:count";
        // individual views -- not sure these are needed
        public static final String HYP_ONE_R = "/hyp/oner/:res/:offset/:count";
        public static final String HYP_ONE_U = "/hyp/oneU/:usr/:offset/:count";
        public static final String HYP_ONE_T = "/hyp/onet/:tag/:offset/:count";
        // pivot objects
        public static final String HYP_RESOURCES = "/hyp/resources/:offset/:count";
        public static final String HYP_USERS = "/hyp/users/:offset/:count";
        public static final String HYP_TAGS = "/hyp/tags/:offset/:count";
        // pivot views
        public static final String HYP_TAG_PIV = "/hyp/tagpiv/:tag/:offset/:count";
        public static final String HYP_USR_PIV = "/hyp/usrpiv/:user/:offset/:count";
        public static final String HYP_RES_PIV = "/hyp/respiv/:resource/:offset/:count";

        public static final String HYP_PIV_2 = "/hyp/piv2/:featureA/featureB/:offset/:count";

    }

    public static class Template {
        public static final String INDEX = "/velocity/index/index.vm";
        public static final String LOGIN = "/velocity/login/login.vm";
        public static final String BOOKS_ALL = "/velocity/book/all.vm";
        public static final String BOOKS_ONE = "/velocity/book/one.vm";
        public static final String NOT_FOUND = "/velocity/notFound.vm";
    }

}
