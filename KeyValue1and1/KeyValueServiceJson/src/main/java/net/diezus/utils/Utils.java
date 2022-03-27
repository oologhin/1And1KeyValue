package net.diezus.utils;





import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


import net.diezus.utils.model.Key;


public class Utils {
	

	
	@Autowired
	public static Environment environment;
	
	public static int getRandomIntegerBetweenRange(int min, int max){
	    int x = (int)(Math.random()*((max-min)+1))+min;
	    return x;
	}

	
	/*
	 * We expect an encoded string like {"key": "key100", "value": "value100"}
	 */
	public static String getKey(String json_key) throws ParseException{
		
		String key="null";
		JSONParser parser = new JSONParser();
		
			JSONObject jsonObj=((JSONObject)parser.parse(json_key));
			/*Iterator iter = jsonObj.keySet().iterator();
			while(iter.hasNext()) {
				String keyName = (String) iter.next();
				Object value = jsonObj.get("key");
		        
			}*/
		key= jsonObj.get("key").toString();	
	
		return key;
	}
	

	/*
	 * We expect an encoded string like {"key": "key100", "value": "value100"}
	 */
	public static String getValue(String json_key) throws ParseException {
		String value = "null";
		
		JSONParser parser = new JSONParser();
		
			JSONObject jsonObj=((JSONObject)parser.parse(json_key));
			/*Iterator iter = jsonObj.keySet().iterator();
			while(iter.hasNext()) {
				String key = (String) iter.next();
				value = jsonObj.get(key).toString();
		        
			}*/
		value= jsonObj.get("value").toString();	
		
	
		return value;
	}
	
	/*
	 * We expect an encoded string like {"key": "key100", "value": "value100"}
	 */
	public static Key getKeyObject(String json_key) throws ParseException {
		Key keyObj = new Key("null","null");
		
		JSONParser parser = new JSONParser();
	
		JSONObject jsonObj=((JSONObject)parser.parse(json_key));
		//Iterator iter = jsonObj.keySet().iterator();
		//while(iter.hasNext()) {				
			String key = jsonObj.get("key").toString();
			String value = jsonObj.get("value").toString();
			keyObj=new Key(key,value);
		//}
	
		return keyObj;
	}
	
	
	
	public static String getLocalServerAddress() throws UnknownHostException {
		
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	public static String getLocalServerPort(){
			
		return environment.getProperty("local.server.port");
	}
	

	public static Map<String, Object> getKeys(String json_body) throws ParseException {
		
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj=((JSONObject)parser.parse(json_body));	
			
		return (Map<String, Object>) jsonObj.get("keys");
	}
	
	
	

}
