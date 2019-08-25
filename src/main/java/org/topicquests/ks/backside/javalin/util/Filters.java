package org.topicquests.ks.backside.javalin.util;

import static org.topicquests.ks.backside.javalin.util.RequestUtil.*;

import io.javalin.http.Handler;

public class Filters {

    // Locale change can be initiated from any page
    // The locale is extracted from the request and saved to the user's session
    public static Handler handleLocaleChange = ctx -> {
        if (getQueryLocale(ctx) != null) {
            ctx.sessionAttribute("locale", getQueryLocale(ctx));
            ctx.redirect(ctx.path());
        }
    };

}
