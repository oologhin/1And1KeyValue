package net.diezus.client;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

import net.diezus.utils.model.Key;



public class OutSyncClient {
	
	private static final Log log = LogFactory.getLog(OutSyncClient.class);
	
	public  enum Action {CREATE,UPDATE,DELETE};
	
	public static void sendKeys(Map<String, Object> keys, String fedserver, String fedport, String sourceIP, String sourcePort) throws Exception {
		//insert source in json object
		JSONObject jsonobject= new JSONObject();
		
		JSONObject sourceObject = new JSONObject();
		sourceObject.put("ip", sourceIP);
		sourceObject.put("port", sourcePort);
		
		jsonobject.put("source", sourceObject);
		
		JSONObject keysObject = new JSONObject();
		keysObject.putAll(keys);
		
		jsonobject.put("keys",keysObject);
		log.info("Sending to "+fedserver+":"+fedport+" values:\n"+jsonobject.toString());
		
		//send to controller
		HttpClient.sendPut("http://"+fedserver+":"+fedport+"/synckeys",jsonobject.toString());

	}
	
	
	
	public static void sendKey(Action action,Key key, String fedserver, String fedport, String sourceIP, String sourcePort) throws Exception {
		//insert source in json object
		JSONObject jsonobject= new JSONObject();
		
		JSONObject sourceObject = new JSONObject();
		sourceObject.put("ip", sourceIP);
		sourceObject.put("port", sourcePort);
		
		jsonobject.put("source", sourceObject);
		
		
		JSONObject keyObject = new JSONObject();
		keyObject.put(key.getKey(), key.getValue());
		
		jsonobject.put("keys",keyObject);
	
		
		log.info("Sending to "+fedserver+":"+fedport+" values:"+jsonobject.toString());
		
		//send to controller
		
		switch(action) {
		
		case CREATE: HttpClient.sendPut("http://"+fedserver+":"+fedport+"/synckeys",jsonobject.toString());break;
		case DELETE: HttpClient.sendDelete("http://"+fedserver+":"+fedport+"/syncdelkeys",jsonobject.toString());break;
		case UPDATE: HttpClient.sendPost("http://"+fedserver+":"+fedport+"/synckeys",jsonobject.toString());break;
		}
	}
	
	
}
