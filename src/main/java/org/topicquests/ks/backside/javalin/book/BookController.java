package org.topicquests.ks.backside.javalin.book;

import io.javalin.http.Handler;

import static org.topicquests.ks.backside.javalin.Main.*;
import static org.topicquests.ks.backside.javalin.util.RequestUtil.*;

import java.util.Map;

import org.topicquests.ks.backside.javalin.AppEnvironment;
import org.topicquests.ks.backside.javalin.util.Path;
import org.topicquests.ks.backside.javalin.util.ViewUtil;

public class BookController {
	private AppEnvironment environment;
    private BookDao bookDao;

	public BookController(AppEnvironment env) {
		environment = env;
		bookDao = environment.getBookDao();
	}

    public Handler fetchAllBooks = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("books", bookDao.getAllBooks());
        ctx.render(Path.Template.BOOKS_ALL, model);
    };

    public Handler fetchOneBook = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("book", bookDao.getBookByIsbn(getParamIsbn(ctx)));
        ctx.render(Path.Template.BOOKS_ONE, model);
    };
}
