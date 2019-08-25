/**
 * 
 */
package org.topicquests.ks.backside.javalin.wgv;

import org.topicquests.ks.backside.javalin.AppEnvironment;

import io.javalin.http.Handler;

/**
 * @author jackpark
 *
 */
public class WGVController {
	private AppEnvironment environment;
    private WGVDao wgvDao;

	/**
	 * 
	 */
	public WGVController(AppEnvironment env) {
		environment = env;
		wgvDao = environment.getWGVDao();
	}

	public Handler handleGet = ctx -> {
		//TODO
	};

	public Handler handlePost = ctx -> {
		//DO NOTHING
	};
}
