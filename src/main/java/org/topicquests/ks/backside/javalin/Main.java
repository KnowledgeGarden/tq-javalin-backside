package org.topicquests.ks.backside.javalin;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import org.topicquests.ks.backside.javalin.book.BookController;
import org.topicquests.ks.backside.javalin.book.BookDao;
import org.topicquests.ks.backside.javalin.hyp.HypothesisController;
import org.topicquests.ks.backside.javalin.index.IndexController;
import org.topicquests.ks.backside.javalin.login.LoginController;
import org.topicquests.ks.backside.javalin.pkm.PKMController;
import org.topicquests.ks.backside.javalin.user.UserDao;
import org.topicquests.ks.backside.javalin.util.Filters;
import org.topicquests.ks.backside.javalin.util.HerokuUtil;
import org.topicquests.ks.backside.javalin.util.Path;
import org.topicquests.ks.backside.javalin.util.ViewUtil;
import org.topicquests.ks.backside.javalin.wgv.WGVController;


public class Main {
	private AppEnvironment environment;
	private IndexController indexController;
	private BookController bookController;
	private LoginController loginController;
	private PKMController pkmController;
	private WGVController wgvController;
	private HypothesisController hypController;

    
    public Main () {
       	environment = new AppEnvironment();
       	indexController = environment.getIndexController();
       	bookController = environment.getBookController();
       	loginController = environment.getLoginController();
       	pkmController = environment.getPKMController();
       	wgvController = environment.getWGVController();
       	hypController = environment.getHypothesisController();
       	
        Javalin app = Javalin.create(config -> {
        	config.enableCorsForAllOrigins();
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }).start(7070);

        app.routes(() -> {
            before(Filters.handleLocaleChange);
            before(LoginController.ensureLoginBeforeViewingBooks);
            get(Path.Web.INDEX, indexController.serveIndexPage);
            get(Path.Web.BOOKS, bookController.fetchAllBooks);
            get(Path.Web.ONE_BOOK, bookController.fetchOneBook);
            get(Path.Web.LOGIN, loginController.serveLoginPage);
            post(Path.Web.LOGIN, loginController.handleLoginPost);
            post(Path.Web.LOGOUT, loginController.handleLogoutPost);
            post(Path.Web.PKM, pkmController.handlePost);
            get(Path.Web.PKM, pkmController.handleGet); //TODO handle post
            get(Path.Web.WGV, wgvController.handleGet); //No posts to this
            get(Path.Web.HYP, hypController.handleGetAll); //No posts to this
            get(Path.Web.HYP_ONE_R, hypController.handleGetOneResource); //No posts to this
            get(Path.Web.HYP_ONE_T, hypController.handleGetOneTag); //No posts to this
            get(Path.Web.HYP_ONE_U, hypController.handleGetOneUser); //No posts to this
            get(Path.Web.HYP_TEXT, hypController.handleGetText); //No posts to this
            get(Path.Web.HYP_RESOURCES, hypController.handleGetResources); //No posts to this
            get(Path.Web.HYP_USERS, hypController.handleGetUsers); //No posts to this
            get(Path.Web.HYP_TAGS, hypController.handleGetTags); //No posts to this
            get(Path.Web.HYP_TAG_PIV, hypController.handleGetTagPivot); //No posts to this
            get(Path.Web.HYP_USR_PIV, hypController.handleGetUserPivot); //No posts to this
            get(Path.Web.HYP_RES_PIV, hypController.handleGetResourcePivot); //No posts to this

            get(Path.Web.HYP_PIV_2, hypController.handleGetPivot2);
        });

        app.error(404, ViewUtil.notFound);
    }

    public static void main(String[] args) {
    	new Main();
    }

}
