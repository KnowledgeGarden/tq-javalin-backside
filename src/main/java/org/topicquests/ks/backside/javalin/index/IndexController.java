package org.topicquests.ks.backside.javalin.index;

import io.javalin.http.Handler;

import static org.topicquests.ks.backside.javalin.Main.*;

import java.util.Map;

import org.topicquests.ks.backside.javalin.AppEnvironment;
import org.topicquests.ks.backside.javalin.book.BookDao;
import org.topicquests.ks.backside.javalin.user.UserDao;
import org.topicquests.ks.backside.javalin.util.Path;
import org.topicquests.ks.backside.javalin.util.ViewUtil;

public class IndexController {
	private AppEnvironment environment;
    private BookDao bookDao;
    private UserDao userDao;

	public IndexController(AppEnvironment env) {
		environment = env;
		bookDao = environment.getBookDao();
		userDao = environment.getUserDao();
	}
	
    public Handler serveIndexPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", userDao.getAllUserNames());
        model.put("book", bookDao.getRandomBook());
        ctx.render(Path.Template.INDEX, model);
    };
}
