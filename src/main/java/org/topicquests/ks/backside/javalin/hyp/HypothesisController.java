/**
 * 
 */
package org.topicquests.ks.backside.javalin.hyp;

import java.util.Map;

import org.topicquests.ks.backside.javalin.AppEnvironment;
import org.topicquests.ks.backside.javalin.util.ViewUtil;
import org.topicquests.pg.api.IPostgresConnection;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import io.javalin.http.Handler;
import java.util.*;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class HypothesisController {
	private AppEnvironment environment;
	private HypothesisDao hypDao;
	/**
	 * 
	 */
	public HypothesisController(AppEnvironment env) {
		environment = env;
		hypDao = environment.getHypothesisDao();
	}

	/////////////////////////
	// NOTE
	// Path Params are named in org.topicquests.ks.backside.javalin.util.Path.java
	////////////////////////
	/**
	 * get/:offset/:count
	 */
	public Handler handleGetAll = ctx -> {
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		
        List<JSONObject>hits = hypDao.getAllHits(offset, count);
        //ship JSON
        ctx.json(hits);
	};
	
	public Handler handleGetResources = ctx -> {
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		JSONArray hits = hypDao.getResources(offset, count);
        //ship JSON
        ctx.json(hits);
	};
	public Handler handleGetUsers = ctx -> {
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
        JSONArray hits = hypDao.getUsers(offset, count);
        //ship JSON
        ctx.json(hits);
		
	};
	public Handler handleGetTags = ctx -> {
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		JSONArray hits = hypDao.getTags(offset, count);
        //ship JSON
        ctx.json(hits);
	};

	public Handler handleGetTagPivot = ctx -> {
		String id = ctx.pathParam("tag");
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		JSONObject jo = hypDao.getTagPivot(id, offset, count);
		ctx.json(jo);
	};
	public Handler handleGetUserPivot = ctx -> {
		String id = ctx.pathParam("user");
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		JSONObject jo = hypDao.getUserPivot(id, offset, count);
		ctx.json(jo);		
	};
	public Handler handleGetResourcePivot = ctx -> {
		String id = ctx.pathParam("resource");
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		JSONObject jo = hypDao.getResourcePivot(id, offset, count);
		ctx.json(jo);
	};

	/**
	 * get/:resource/:offset/:count  (for the pivots) 
	 */
	public Handler handleGetOneResource = ctx -> {
		//TODO
	};
	
	/**
	 * get/:tag:/:offset/:count  (for the pivots)
	 */
	public Handler handleGetOneTag = ctx -> {
		//TODO
	};
	
	/**
	 * get/:user/:offset/:count  (for the pivots)
	 */
	public Handler handleGetOneUser = ctx -> {
		//TODO
	};

	/**
	 * get/:text/:offset/:count
	 */
	public Handler handleGetText = ctx -> {
		String query = ctx.pathParam("text");
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		
        List<JSONObject>hits = hypDao.listTextQuery(query, offset, count);
        //ship JSON
        ctx.json(hits);
		
	};
	
	public Handler handleGetPivot2 = ctx -> {
		String featureA = ctx.pathParam("featureA");
		String featureB = ctx.pathParam("featureB");
		String offset = ctx.pathParam("offset");
		String count = ctx.pathParam("count");
		JSONObject jo = hypDao.getPivot2(featureA, featureB , offset, count);
		ctx.json(jo);
	};

	public Handler handlePost = ctx -> {
		//DO NOTHING
	};

}
