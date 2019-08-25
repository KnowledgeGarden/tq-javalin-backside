/**
 * 
 */
package org.topicquests.ks.backside.javalin;

import org.topicquests.ks.backside.javalin.book.BookController;
import org.topicquests.ks.backside.javalin.book.BookDao;
import org.topicquests.ks.backside.javalin.hyp.HypothesisController;
import org.topicquests.ks.backside.javalin.hyp.HypothesisDao;
import org.topicquests.ks.backside.javalin.index.IndexController;
import org.topicquests.ks.backside.javalin.login.LoginController;
import org.topicquests.ks.backside.javalin.pkm.PKMController;
import org.topicquests.ks.backside.javalin.pkm.PKMDao;
import org.topicquests.ks.backside.javalin.user.UserController;
import org.topicquests.ks.backside.javalin.user.UserDao;
import org.topicquests.ks.backside.javalin.wgv.WGVController;
import org.topicquests.ks.backside.javalin.wgv.WGVDao;
import org.topicquests.support.RootEnvironment;

/**
 * @author jackpark
 *
 */
public class AppEnvironment extends RootEnvironment {
    private BookDao bookDao;
    private UserDao userDao;
    private PKMDao pkmDao;
    private WGVDao wgvDao;
	private HypothesisDao hypDao;

	private IndexController indexController;
	private BookController bookController;
	private UserController userController;
	private LoginController loginController;
	private PKMController pkmController;
	private WGVController wgvController;
	private HypothesisController hypController;
	/**
	 */
	public AppEnvironment() {
		super("config-props.xml", "logger.properties");
		bookDao = new BookDao(this);
		userDao = new UserDao(this);
		pkmDao = new PKMDao(this);
		wgvDao = new WGVDao(this);
		hypDao = new HypothesisDao(this);
		indexController = new IndexController(this);
		bookController = new BookController(this);
		userController = new UserController(this);
		loginController = new LoginController(this);
		pkmController = new PKMController(this);
		wgvController = new WGVController(this);
		hypController = new HypothesisController(this);
	}
	public HypothesisDao getHypothesisDao() {
		return hypDao;
	}
	public WGVDao getWGVDao() {
		return wgvDao;
	}
	public PKMDao getPKMDao() {
		return pkmDao;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}
	
	public BookDao getBookDao() {
		return bookDao;
	}
	public BookController getBookController() {
		return bookController;
	}
	public IndexController getIndexController() {
		return indexController;
	}

	public UserController getUserController() {
		return userController;
	}
	
	public LoginController getLoginController() {
		return loginController;
	}
	
	public PKMController getPKMController() {
		return pkmController;
	}
	
	public WGVController getWGVController() {
		return wgvController;
	}
	
	public HypothesisController getHypothesisController() {
		return hypController;
	}
	/* (non-Javadoc)
	 * @see org.topicquests.support.RootEnvironment#shutDown()
	 */
	@Override
	public void shutDown() {
		// TODO Auto-generated method stub

	}

}
