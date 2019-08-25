/**
 * 
 */
package org.topicquests.ks.backside.javalin.pkm;

import org.topicquests.ks.backside.javalin.AppEnvironment;

import io.javalin.http.Handler;

/**
 * @author jackpark
 *
 */
public class PKMController {
	private AppEnvironment environment;
    private PKMDao pkmDao;

	/**
	 * 
	 */
	public PKMController(AppEnvironment env) {
		environment = env;
		pkmDao = environment.getPKMDao();
	}
	
	public Handler handleGet = ctx -> {
		//TODO
	};

	public Handler handlePost = ctx -> {
		//TODO
	};

}
