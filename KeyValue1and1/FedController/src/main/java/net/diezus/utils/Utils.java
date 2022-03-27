package net.diezus.utils;





import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import net.diezus.fed.model.Key;
import net.diezus.fed.model.ServiceInstance;





public class Utils {
	
	
	
	/*
	 * We expect an encoded string like {"key": "key100", "value": "value100"}
	 */
	public static String getKey(String json_key) throws ParseException{
		
		String key="null";
		JSONParser parser = new JSONParser();
		
			JSONObject jsonObj=((JSONObject)parser.parse(json_key));
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
	
	/*
	 * We expect an encoded string like {"id": "id_value", "ip": "ip_value",port:"port_value"}
	 */
	public static ServiceInstance getServiceInstanceObject(String json_body) throws ParseException {
		ServiceInstance keyObj = new ServiceInstance("-1","null","null");
		
		JSONParser parser = new JSONParser();
	
		JSONObject jsonObj=((JSONObject)parser.parse(json_body));
		
			String id = jsonObj.get("id").toString();
			String ip = jsonObj.get("ip").toString();
			String port = jsonObj.get("port").toString();
			
			
			keyObj=new ServiceInstance(id,ip,port);
			
		return keyObj;
	}
	
	
	/*
	 * We expect an encoded string like {"id": "id_value", "ip": "ip_value",port:"port_value"}
	 */
	public static ServiceInstance getSourceServiceInstance(String json_body) throws ParseException {
		ServiceInstance keyObj = new ServiceInstance("-1","null","null");
		
		JSONParser parser = new JSONParser();
	
		JSONObject jsonObj=((JSONObject)parser.parse(json_body));
		
		
		JSONObject sourceObject =  (JSONObject) jsonObj.get("source");
		
		if(sourceObject!=null && !sourceObject.isEmpty()) {
			
		
			String id = "0";
			String ip = sourceObject.get("ip").toString();
			String port = sourceObject.get("port").toString();
			
			
			keyObj=new ServiceInstance(id,ip,port);
		}	
		return keyObj;
	}
	
	public static Map<String, Object> getKeys(String json_body) throws ParseException {
		
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj=((JSONObject)parser.parse(json_body));	
			
		return (Map<String, Object>) jsonObj.get("keys");
	}
	
	public static ServiceInstance getSource(String json_body) throws ParseException {
		
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj=((JSONObject)parser.parse(json_body));
		
		JSONObject jsonsrv = (JSONObject) jsonObj.get("Source"); 
		
	
		return new ServiceInstance("0", jsonsrv.get("ip").toString(), jsonsrv.get("port").toString());
	}
	
	

}
