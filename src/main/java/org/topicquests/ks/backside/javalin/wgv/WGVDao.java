/**
 * 
 */
package org.topicquests.ks.backside.javalin.wgv;

import org.topicquests.asr.wordgrams.WGVEnvironment;
import org.topicquests.asr.wordgrams.WGVModel;
import org.topicquests.ks.backside.javalin.AppEnvironment;

/**
 * @author jackpark
 *
 */
public class WGVDao {
	private AppEnvironment environment;
	private WGVEnvironment wgvEnv;
	private WGVModel model;

	/**
	 * 
	 */
	public WGVDao(AppEnvironment env) {
		environment = env;
		wgvEnv = new WGVEnvironment();
		model = wgvEnv.getModel();
	}

}
