/**
 * 
 */
package org.topicquests.ks.backside.javalin.hyp;

import org.topicquests.es.ProviderEnvironment;
import org.topicquests.es.api.IClient;
import org.topicquests.es.api.IQueryDSL;
import org.topicquests.es.util.TextQueryUtil;
import org.topicquests.ks.backside.javalin.AppEnvironment;
import org.topicquests.ks.backside.javalin.book.Book;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.sql.ResultSet;
import java.util.*;
/**
 * @author jackpark
 *
 */
public class HypothesisDao {
	private AppEnvironment environment;
	//gain access to Hypothesis Harvester by way of ElasticSearch
	private ProviderEnvironment esProvider;  // Elasticsearch provider
	private PostgresConnectionFactory pgProvider;
	private IClient esClient;
	private Object [] emptyObject = new Object[0];
	private final String 
		INDEX = "annotations",
		PG_NAME = "tagomizer_database",
		PG_SCHEMA = "tq_tagomizer";
	private IQueryDSL dsl; // for full text


	/**
	 * 
	 */
	public HypothesisDao(AppEnvironment env) {
		environment = env;
		esProvider = new ProviderEnvironment();
		esClient = esProvider.getProvider();
		pgProvider = new PostgresConnectionFactory(PG_NAME, PG_SCHEMA);
		dsl = esProvider.getQueryDSL();
	}

	/**
	 * Fetch general annotation hits
	 * @param offset
	 * @param count
	 * @return
	 */
	public List<JSONObject> getAllHits(String _offset, String _count) {
		SearchRequest query = new SearchRequest(INDEX);
		SearchSourceBuilder bldr = new SearchSourceBuilder();
		int offset = 0;
		int count = 100;
		if (_offset != null)
			offset = Integer.parseInt(_offset);
		if (_count != null)
			count = Integer.parseInt(_count);
		bldr = bldr.from(offset);
		bldr = bldr.size(count);
		query = query.source(bldr);
		IResult r = esClient.listSearch(query, INDEX);
		List<JSONObject> lx = (List<JSONObject>)r.getResultObject();
        return lx;
    }
	
	///////////////////////////////////////////////////////
	//THESE WORK for bulk fetching
	public JSONArray getResources(String _offset, String _count) {
		String sql = "select url, title from tq_tagomizer.document ORDER BY title ASC LIMIT '"+_count+"' OFFSET '"+_offset+"'";
		
		return doSQL(sql, "url", "title");
	}

	public JSONArray getUsers(String _offset, String _count) {
		String sql = "SELECT hyp_uid FROM tq_tagomizer.huser ORDER BY hyp_uid ASC LIMIT '"+_count+"' OFFSET '"+_offset+"'";
		return doSQL(sql, "hyp_uid", null);
	}
	
	public JSONArray getTags(String _offset, String _count) {
		String sql = "SELECT name FROM tq_tagomizer.tag ORDER BY name ASC LIMIT '"+_count+"' OFFSET '"+_offset+"'";
		return doSQL(sql, "name", null);
	}
	
	JSONArray doSQL(String sql, String key1, String key2) {
		JSONArray result = new JSONArray();
	
		IPostgresConnection conn = null;
		IResult r = new ResultPojo();
		try {
			conn = pgProvider.getConnection();
		    conn.setProxyRole(r);
		    conn.executeSelect(sql, r);
			ResultSet rs = (ResultSet)r.getResultObject();
			environment.logDebug("HypDAO-1 "+conn+" | "+r.getErrorString()+" | "+rs+" | "+sql);
			String x;
			JSONObject jo;
			if (rs != null) {
				while(rs.next()) {
					if (key2 == null) {
						x = rs.getString(key1);
						if (!result.contains(x))
							result.add(x);
					} else {
						jo = new JSONObject();
						jo.put(key1, rs.getString(key1));
						jo.put(key2, rs.getString(key2));
						if (!result.contains(jo))
							result.add(jo);
					}
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			conn.closeConnection(r);
		}

		return result;
	}
	//TODO wire these utilities
	////////////////////////////////////////////////////////
	// Utilities
	//
	JSONArray getResourcesByUser(IPostgresConnection conn, IResult r, 
			String user, int offset, int count) throws Exception {
		JSONArray result = new JSONArray();
		String sql = "SELECT DISTINCT tq_tagomizer.document.url, tq_tagomizer.document.title FROM tq_tagomizer.document "+
				"JOIN tq_tagomizer.reference ON tq_tagomizer.document.id = tq_tagomizer.reference.document_id "+
				"JOIN tq_tagomizer.huser ON tq_tagomizer.reference.user_id = tq_tagomizer.huser.id "+
				"WHERE tq_tagomizer.huser.hyp_uid = ? " +
				"ORDER BY tq_tagomizer.document.title ASC LIMIT ? OFFSET ?";
			environment.logDebug("HypDao.getResourceByUser- "+sql);
			Object [] obj = new Object[3];
			obj[0] = user;
			obj[1] = count;
			obj[2] = offset;
			conn.executeSelect(sql, r, obj);
			ResultSet rs = (ResultSet)r.getResultObject();
			JSONObject jo;
			if (rs != null) {
				while (rs.next()) {
					jo = new JSONObject();
					jo.put("url", rs.getString(1));
					jo.put("title", rs.getString(2));
					result.add(jo);
				}
			}
			environment.logDebug("HypDao.getResourceByUser+ "+result);
		return result;
	}
	JSONArray getResourcesByTag(IPostgresConnection conn, IResult r,
			String tag, int offset, int count) throws Exception {
		JSONArray result = new JSONArray();
		String sql = "SELECT DISTINCT tq_tagomizer.document.url, tq_tagomizer.document.title FROM tq_tagomizer.document "+
				"JOIN tq_tagomizer.reference ON tq_tagomizer.document.id = tq_tagomizer.reference.document_id "+
				"JOIN tq_tagomizer.tag_ref ON tq_tagomizer.reference.hyp_id = tq_tagomizer.tag_ref.ref_id "+
				"JOIN tq_tagomizer.tag ON tq_tagomizer.tag_ref.tag_id = tq_tagomizer.tag.id "+
				"WHERE tq_tagomizer.tag.name = ? " +
				"ORDER BY tq_tagomizer.document.title ASC LIMIT ? OFFSET ?";
			environment.logDebug("HypDao.getResourceByTag- "+sql);
			Object [] obj = new Object[3];
			obj[0] = tag;
			obj[1] = count;
			obj[2] = offset;
			conn.executeSelect(sql, r, obj);
			ResultSet rs = (ResultSet)r.getResultObject();
			JSONObject jo;
			if (rs != null) {
				while (rs.next()) {
					jo = new JSONObject();
					jo.put("url", rs.getString(1));
					jo.put("title", rs.getString(2));
					result.add(jo);
				}
			}
			environment.logDebug("HypDao.getResourceByTag+ "+result);
		return result;
	}
	JSONArray getUsersByResource(IPostgresConnection conn, IResult r,
			String url, int offset, int count) throws Exception {
		JSONArray result = new JSONArray();
		String sql = "SELECT DISTINCT tq_tagomizer.huser.hyp_uid FROM tq_tagomizer.huser "+
			"JOIN tq_tagomizer.reference ON tq_tagomizer.huser.id = tq_tagomizer.reference.user_id "+
			"JOIN tq_tagomizer.document ON tq_tagomizer.reference.document_id = tq_tagomizer.document.id "+
			"WHERE tq_tagomizer.document.url = ? "+
			"ORDER BY tq_tagomizer.huser.hyp_uid ASC LIMIT ? OFFSET ?";
		environment.logDebug("HypDao.getUsersByResource- "+sql);
		Object [] obj = new Object[3];
		obj[0] = url;
		obj[1] = count;
		obj[2] = offset;
		conn.executeSelect(sql, r, obj);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null) {
			while (rs.next())
				result.add(rs.getString(1));
		}
		environment.logDebug("HypDao.getUsersByResource+ "+result);
		return result;
	}
	JSONArray getUsersByTag(IPostgresConnection conn, IResult r,
			String tag, int offset, int count) throws Exception {
		JSONArray result = new JSONArray();
		String sql = "SELECT DISTINCT tq_tagomizer.huser.hyp_uid FROM tq_tagomizer.huser "+
				"JOIN tq_tagomizer.reference ON tq_tagomizer.huser.id = tq_tagomizer.reference.user_id "+
				"JOIN tq_tagomizer.document ON tq_tagomizer.reference.document_id = tq_tagomizer.document.id "+
				"JOIN tq_tagomizer.tag_ref ON tq_tagomizer.reference.hyp_id = tq_tagomizer.tag_ref.ref_id "+
				"JOIN tq_tagomizer.tag ON tq_tagomizer.tag_ref.tag_id = tq_tagomizer.tag.id "+
				"WHERE tq_tagomizer.tag.name = ? "+
				"ORDER BY tq_tagomizer.huser.hyp_uid ASC LIMIT ? OFFSET ?";
			environment.logDebug("HypDao.getUsersByResource- "+sql);
			Object [] obj = new Object[3];
			obj[0] = tag;
			obj[1] = count;
			obj[2] = offset;
			conn.executeSelect(sql, r, obj);
			ResultSet rs = (ResultSet)r.getResultObject();
			if (rs != null) {
				while (rs.next())
					result.add(rs.getString(1));
			}
			environment.logDebug("HypDao.getUsersByResource+ "+result);		return result;
	}
	JSONArray getTagsByUser(IPostgresConnection conn, IResult r,
			String user, int offset, int count) throws Exception {
		JSONArray result = new JSONArray();
		String sql = "SELECT DISTINCT tq_tagomizer.tag.name FROM tq_tagomizer.tag "+
				"JOIN tq_tagomizer.tag_ref ON tq_tagomizer.tag.id = tq_tagomizer.tag_ref.tag_id "+
				"JOIN tq_tagomizer.reference ON tq_tagomizer.tag_ref.ref_id = tq_tagomizer.reference.hyp_id "+
				"JOIN tq_tagomizer.huser ON tq_tagomizer.reference.user_id = tq_tagomizer.huser.id "+
				"WHERE tq_tagomizer.huser.hyp_uid = ? "+
				"ORDER BY tq_tagomizer.tag.name ASC LIMIT ? OFFSET ?";
		environment.logDebug("HypDao.getTagsByUser- "+sql);
		Object [] obj = new Object[3];
		obj[0] = user;
		obj[1] = count;
		obj[2] = offset;
		conn.executeSelect(sql, r, obj);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null) {
			while (rs.next())
				result.add(rs.getString(1));
		}
		environment.logDebug("HypDao.getTagsByUser+ "+result);
		return result;
	}
	JSONArray getTagsByResource(IPostgresConnection conn, IResult r,
			String url, int offset, int count) throws Exception {
		JSONArray result = new JSONArray();
		String sql = "SELECT DISTINCT tq_tagomizer.tag.name FROM tq_tagomizer.tag "+
				"JOIN tq_tagomizer.tag_ref ON tq_tagomizer.tag.id = tq_tagomizer.tag_ref.tag_id "+
				"JOIN tq_tagomizer.reference ON tq_tagomizer.tag_ref.ref_id = tq_tagomizer.reference.hyp_id "+
				"JOIN tq_tagomizer.document ON tq_tagomizer.reference.document_id = tq_tagomizer.document.id "+
				"WHERE tq_tagomizer.document.url = ? "+
				"ORDER BY tq_tagomizer.tag.name ASC LIMIT ? OFFSET ?";
		environment.logDebug("HypDao.getTagsByResource- "+sql);
		Object [] obj = new Object[3];
		obj[0] = url;
		obj[1] = count;
		obj[2] = offset;
		conn.executeSelect(sql, r, obj);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (rs != null) {
			while (rs.next())
				result.add(rs.getString(1));
		}
		environment.logDebug("HypDao.getTagsByResource+ "+result);
		return result;
	}
	//
	////////////////////////////////////////////////////////

	public JSONObject getTagPivot(String tag, String _offset, String _count) {
		IResult r = new ResultPojo();
		IPostgresConnection conn = null;
		JSONObject result = new JSONObject();
		int offset = 0;
		int count = 100;
		if (_offset != null)
			offset = Integer.parseInt(_offset);
		if (_count != null)
			count = Integer.parseInt(_count);
		try {
			conn = pgProvider.getConnection();
		    conn.setProxyRole(r);
		    JSONArray ja = this.getUsersByTag(conn, r, tag, offset, count);
		    result.put("usr", ja);
		    ja = this.getResourcesByTag(conn, r, tag, offset, count);
		    result.put("resource", ja);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			conn.closeConnection(r);
		}		
		environment.logDebug("HypDao.tagPivot+ "+result);
		return result;
	}
	
	public JSONObject getUserPivot(String user, String _offset, String _count) {
		IResult r = new ResultPojo();
		IPostgresConnection conn = null;
		JSONObject result = new JSONObject();
		int offset = 0;
		int count = 100;
		if (_offset != null)
			offset = Integer.parseInt(_offset);
		if (_count != null)
			count = Integer.parseInt(_count);
		try {
			conn = pgProvider.getConnection();
		    conn.setProxyRole(r);
		    JSONArray ja = this.getTagsByUser(conn, r, user, offset, count);
		    result.put("tag", ja);
		    ja = this.getResourcesByUser(conn, r, user, offset, count);
		    result.put("resource", ja);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			conn.closeConnection(r);
		}		
		environment.logDebug("HypDao.userPivot+ "+result);
		return result;

	}
	
	public JSONObject getResourcePivot(String url, String _offset, String _count) {
		JSONObject result = new JSONObject();
		IResult r = new ResultPojo();
		IPostgresConnection conn = null;
		JSONArray ja;
		int offset = 0;
		int count = 100;
		if (_offset != null)
			offset = Integer.parseInt(_offset);
		if (_count != null)
			count = Integer.parseInt(_count);
		try {
			conn = pgProvider.getConnection();
		    conn.setProxyRole(r);
		    ja = this.getUsersByResource(conn, r, url, offset, count);
		    result.put("usr", ja);
		    ja = this.getTagsByResource(conn, r, url, offset, count);
		    result.put("tag", ja);
		    String sql = "SELECT tq_tagomizer.reference.hyp_id from tq_tagomizer.reference "+
		    		"JOIN tq_tagomizer.document ON tq_tagomizer.reference.document_id = tq_tagomizer.document.id "+
		    		"WHERE tq_tagomizer.document.url = ?";
		    environment.logDebug("HypDao.getResourcePivot "+sql);
		    Object [] obj = new Object[1];
		    obj[0] = url;
		    conn.executeSelect(sql, r, obj);
			ResultSet rs = (ResultSet)r.getResultObject();
			if (rs != null) {
				JSONObject jo = null;
				String id = null;
				List<String>ids = null;
				List<JSONObject> jol = new ArrayList<JSONObject>();
				while (rs.next()) {
					if (id == null)
						id = rs.getString("hyp_id");
					else {
						if (ids == null) {
							ids = new ArrayList<String>();
							ids.add(id);
						} else
							ids.add(rs.getString("hyp_id"));
					}
				}
				if (ids == null) {
					jo = fetchES(id);
					jol.add(jo);
					result.put("text", jol);
				} else {
					Iterator<String>itr = ids.iterator();
					String iax = null;
					while(itr.hasNext()) {
						if (id == null) {
							id = itr.next();
						} else {
							iax = itr.next();
						}
						if (iax == null) {
							jo = fetchES(id);
							jol.add(jo);
						} else {
							if (!iax.equals(id)) {
								id = iax;
								iax = null;
								jo = fetchES(id);
								jol.add(jo);
							}
						}
					}
					result.put("text", jol);
					
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			conn.closeConnection(r);
		}		

		return result;
	}

	
	JSONObject fetchES(String id) {
		IResult r = esClient.get(id, INDEX);
		JSONObject t = (JSONObject)r.getResultObject();
		environment.logDebug("HypDaoFetchES-1 "+id+" "+t);
		JSONObject jo = new JSONObject();
		jo.put("title", t.getAsString("title"));
		jo.put("text", t.getAsString("text"));
		jo.put("annotation", t.getAsString("annotation"));
		environment.logDebug("HypDaoFetchES-2 "+id+" "+jo);
		return jo;
	}
	
	JSONObject doSQL(String sql, String id, String _offset, String _count, String key1, String key2, String key3) {
		JSONObject result = new JSONObject();
		environment.logDebug("HypDAO.doSQL "+id+" | "+_offset+" | "+_count);
		int offset = Integer.parseInt(_offset);
		int count = Integer.parseInt(_count);
		Object [] obj = new Object[3];
		obj[0] = id;
		obj[1] = count;
		obj[2] = offset;
		IResult r = new ResultPojo();
		IPostgresConnection conn = null;
		try {
			conn = pgProvider.getConnection();
		    conn.setProxyRole(r);
		    conn.executeSelect(sql, r, obj);
			ResultSet rs = (ResultSet)r.getResultObject();
			environment.logDebug("HypDAO-1 "+conn+" | "+r.getErrorString()+" | "+rs+" | "+sql);
			if (rs != null) {
				Set<String> l1 = new HashSet<String>();
				Set<String> l2 = new HashSet<String>();
				Set<String> l3 = new HashSet<String>();
				while(rs.next()) {
					l1.add(rs.getString(key1));
					l2.add(rs.getString(key2));
					if (key3 != null)
						l3.add(rs.getString(key3));
				}
				List<String> x = new ArrayList<String>();
				x.addAll(l1);
				result.put(key1, x);
				x = new ArrayList<String>();
				x.addAll(l2);
				result.put(key2, x);
				if (!l3.isEmpty()) {
					x = new ArrayList<String>();
					x.addAll(l3);
					result.put(key3, x);
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			conn.closeConnection(r);
		}		
		
		return result;
	}
	

	/**
	 * Fetch annotations based on a full text query
	 * @param textQuery
	 * @param offset
	 * @param count
	 * @return
	 */
	public List<JSONObject> listTextQuery(String textQuery, String _offset, String _count) {
		int offset = 0;
		int count = 100;
		if (_offset != null)
			offset = Integer.parseInt(_offset);
		if (_count != null)
			count = Integer.parseInt(_count);
		SearchRequest query = getFullTextSearch(textQuery, offset, count);
		IResult r = esClient.listSearch(query, INDEX);
		System.out.println("TextQuery "+r.getErrorString()+" | "+r.getResultObject());
		List<JSONObject> lx = (List<JSONObject>)r.getResultObject();
        return lx;
	}
	
	SearchRequest getFullTextSearch(String textQuery, int start, int count ) {
		String[] indices = new String[1];
		indices[0] = INDEX;
		String [] fields = new String[3];
		fields[0] = "annotation";
		fields[1] = "title";
		fields[2] = "text";
		return dsl.getTextQueryString(textQuery, start, count, indices, fields);
	}

	/**
	 * This makes separate queries against two features and packages the results according to those pivots
	 * 
	 * @param featureA
	 * @param featureB
	 * @param _count
	 * @param _offset
	 * @return
	 */
	public JSONObject getPivot2(String featureA, String featureB, String _count, String _offset) {
		JSONObject result = new JSONObject();
		//TODO
		return result;
	}
}
