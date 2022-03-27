package net.diezus.srv.keyvalue.controller;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.diezus.client.ConnectionTools;
import net.diezus.client.HttpClient;
import net.diezus.client.OutSyncClient;
import net.diezus.client.OutSyncClient.Action;
import net.diezus.utils.Consistency;
import net.diezus.utils.Consistency.Policy;
import net.diezus.utils.Utils;
import net.diezus.utils.json.repo.KeyRepositoryImpl;
import net.diezus.utils.model.Key;




@RestController
public class KeyController {
	
	private static final Log log = LogFactory.getLog(KeyController.class);


    private KeyRepositoryImpl keyRepository=new KeyRepositoryImpl();
	
	@Autowired
	Environment environment;

	@PostConstruct
	public void initialize() {
		 log.info("###################################   PostContruct   #################################");
		 
	        try {
	            String ip = InetAddress.getLocalHost().getHostAddress();
	            String port = environment.getProperty("server.port", String.class, "8080");
	            String fedserver = environment.getProperty("federation_controller.ip", String.class, "localhost");
	            String fedport = environment.getProperty("federation_controller.port", String.class, "8000");
	            
	            String maxServId =  environment.getProperty("servid.max", String.class, "9999");
	            
	            Consistency consistency = new Consistency(environment.getProperty("consistency.policy", String.class, "ANY"));
	            
	           
	           log.info(String.format("Current consistency policy is  %s", consistency.getPolicy())); 
	           log.info(String.format("Current server is  %s:%s", ip, port));
	           log.info(String.format("Fedserver is %s:%s",fedserver,fedport));
	           if(Integer.parseInt(port)<0) return;
	           	
	           	//In case federation controller is up
	    		if (ConnectionTools.pingHost(fedserver, Integer.parseInt(fedport), ConnectionTools.TIMEOUT)) {
		            String body="";
		    		
		    		body = "{\"id\":\""+Utils.getRandomIntegerBetweenRange(1, Integer.parseInt(maxServId))
		    		+"\",\"ip\":\""+ip+"\",\""
		    		+"port\":\""+port+"\"}";
		    		
		    		log.info("-----subscibe---->"+ HttpClient.sendPut("http://"+fedserver+":"+fedport+"/services",body));
		    		
		    		
		    		//In have values any value is good
		    		if(keyRepository.getSize()>0 && consistency.getPolicy()==Policy.ANY) 
		    		{
		    			OutSyncClient.sendKeys(keyRepository.findAll(), fedserver, fedport, ip, port);
		    		}
		    		
		    		
		    		
		    		
		    		//GET your any values in uptime-value
		    		String response = HttpClient.sendGet("http://"+fedserver+":"+fedport+"/synckeys?ip="+ip+"&port="+port);
		    		
		    		log.info("Initializing database::get fedetaed keys:"+ response);
		    		
		    		if(response !=null && !response.isEmpty())
		    		{
		    			
		    			if(keyRepository.getSize()>0 && consistency.getPolicy()==Policy.EXISTING) {
			    			//need to erase the existing values to get the values from running services
		    				keyRepository.dropAllKeys();
			    		}
		    			
			    		JSONParser parser = new JSONParser();
			    		JSONObject jsonObject= (JSONObject)parser.parse(response);
			    		
			    		if(!jsonObject.isEmpty()) {
			    			
			    			Iterator iter= jsonObject.entrySet().iterator();
			    			while(iter.hasNext()) {
			    				Object obj= iter.next();
			    				log.info("Initializing value::next iterator "+obj.toString());
			    				String[] tokens = obj.toString().split("=");
			    				keyRepository.save(new Key(tokens[0], tokens[1]));
			    				
			    			}
		    			
			    		}	
		    		}
	    		}

	    		else {
	    			log.info(String.format(" Federation controler %s:%s request time out", fedserver,fedport));
	    		}
	    		
	    		
	        } catch (Exception e) {
	        	log.error(e.getStackTrace());
	        }
		
	}
	
	
	/*
	 * Quick initialising local key-value database.
	 * generates keyi:valuei i<size
	 */
	@RequestMapping("/init")
	public String init(@RequestParam(value="size", defaultValue="100") int size) {
		
			for(int i=1;i<=size;i++) {
    		
    		//System.out.println("i="+i);
    		Key key= new Key("key"+i, "value"+i);
    		keyRepository.save(key);
    		
		}

		return "<h1>Initializing the database</h1>"
				+"<b>Size of the initial set:"+size+"</b>"
				+ "<br>Key has ben initialised. "
				+ "<br>Job Done!";
	}
	
	@RequestMapping("/keys")
	public String findAll() {
		Map<String, Object> keys = keyRepository.findAll();
		
		return keys.toString();
	}
	
	@RequestMapping("/keys/find")
	public Key getKeyValue(@RequestParam(value="key") String key) {
		return keyRepository.find(key);
		
	}
	
	@RequestMapping("/keys/{keyId}")
	public Key getKeyforId(@PathVariable String keyId) {
		log.info("the get keys/"+keyId+" was requested");
		return keyRepository.find(keyId);
		
	}
	
	@RequestMapping("/keys/size")
	public int getSize() {
		int size = keyRepository.getSize();
		log.info("The size was requested and is :"+ size);		
		return size;
		
	}
	
	@RequestMapping(value = "/keys/{key}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("key") String key) {
		log.info("attempt to delete "+key);	
	    if (! keyRepository.exists(key)) {
	       
	    	log.info("No entry for "+key);	
	        return "items/error:no such key in the storage";   
	    }
	    
		Key oldkey = keyRepository.delete(key);
		
		try {
		String createjsonInput="{\"key\":\""+key+"\",\"value\": \"null\"}";
		Key keytosync= Utils.getKeyObject(createjsonInput);//new Key( key,"null");
		sendToFederationContoller(keytosync, RequestMethod.DELETE);
		}
		catch (ParseException e) {
			return "Parse Exception:: we expect a single key json body string, e.g.  {\"key\": \"my-key\", \"value\": \"my-val\"}";   
		}
		
	    return ""+oldkey+" Permanentely deleted!";
	}
	
	
	
	/* 
	 * PUT implies putting a resource - completely replacing whatever is available at the given URL with a different thing. By definition, a PUT is idempotent. Do it as many times as you like, and the result is the same. x=5 is idempotent. You can PUT a resource whether it previously exists, or not (eg, to Create, or to Update)!
	 * POST updates a resource, adds a subsidiary resource, or causes a change. A POST is not idempotent, in the way that x++ is not idempotent.
	 */
	@RequestMapping(value = "/keys", method = RequestMethod.PUT)
	public String create(@RequestBody  String json_body) {
	  
		Key keyObj= new Key("null","null");
		log.info("Attempt ot create  "+json_body);
		//PARSE EXCEPTION
		try {
			 keyObj= Utils.getKeyObject(json_body);
		} catch (ParseException e) {
			return "Parse Exception:: we expect a single key json body string, e.g.  {\"key\": \"my-key\", \"value\": \"my-val\"}";   
		}
		
		//Key already exists, we do not create on post
	    if (keyRepository.exists(keyObj.getKey())) {
	       
	        return "items/error:this key allready exists. Try post HTTP action POST to update existing keys";   
	    }
	    //1.Save key localy
		keyRepository.save(keyObj);
		//2. Send key to Federation Controller
		//{key:="key",value="value"}=> needs to be transformed in {key:value}
		sendToFederationContoller(keyObj,RequestMethod.PUT);
		
	    return keyObj+" Permanentely created!";
	}
	
	/* 
	 * PUT implies putting a resource - completely replacing whatever is available at the given URL with a different thing. By definition, a PUT is idempotent. Do it as many times as you like, and the result is the same. x=5 is idempotent. You can PUT a resource whether it previously exists, or not (eg, to Create, or to Update)!
	 * POST updates a resource, adds a subsidiary resource, or causes a change. A POST is not idempotent, in the way that x++ is not idempotent.
	 */
	
	@RequestMapping(value = "/keys", method = RequestMethod.POST)
	public String update(@RequestBody  String json_body) {
	  
		Key keyObj= new Key("null","null");
		
		//PARSE EXCEPTION
		try {
			 keyObj= Utils.getKeyObject(json_body);
		} catch (ParseException e) {
			return "Parse Exception:: we expect a single key json body string, e.g.  {\"key\": \"my-key\", \"value\": \"my-val\"}";   
		}
		
		//Key already exists, we do not create on post
	    if (keyRepository.exists(keyObj.getKey())) {
	       
	        keyRepository.update(keyObj);
	    	sendToFederationContoller(keyObj,RequestMethod.POST);
	    	
	    	return "key allready existed and was updated with the new value :" + keyObj.getValue(); 
	    }
	    
	    //1.Save key localy
	  	keyRepository.save(keyObj);
	  	//2. Send key to Federation Controller
	  	sendToFederationContoller(keyObj,RequestMethod.PUT);
	  	
	    return keyObj+" Permanentely created as it didn't existided!";
	}
	

	/* 
	 * Called by the controller to create keys from other subscribed services 
	 */
	//Ex of jsonbody {source:{ip:"127.0.0.1", port: 8080},keys:{"key1":"value1","key2":"value2"}}
	@RequestMapping(value = "/insync", method = RequestMethod.PUT)
	public String inSyncPUT(@RequestBody  String json_body) {
		//get all values from other services and create them if they do not exist
		log.info(json_body);
		
		Map<String, Object> keys;
		try {
			keys = Utils.getKeys(json_body);
			log.info("InSync Map is "+keys.toString());
			Iterator iter = keys.entrySet().iterator();
			while(iter.hasNext()) {
				Object obj= iter.next();
				log.info("Insync Obj------->"+obj.toString());
				String[] tokens = obj.toString().split("=");
				keyRepository.save(new Key(tokens[0], tokens[1]));
				
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return json_body;
		
	}
	
	
	//Ex of jsonbody {source:{ip:"127.0.0.1", port: 8080},keys:{"key1":"value1","key2":"value2"}}
		@RequestMapping(value = "/indelsync", method = RequestMethod.DELETE)
		public String inSyncDelete(@RequestBody  String json_body) {
			
			//get all values from other services and create them if they do not exist
			log.info("Delete jsonbody: "+json_body);
			
			Map<String, Object> keys;
			try {
				keys = Utils.getKeys(json_body);
				log.info("Map is "+keys.toString());
				Iterator iter = keys.entrySet().iterator();
				while(iter.hasNext()) {
					Object obj= iter.next();
					log.info("Obj------->"+obj.toString());
					String[] tokens = obj.toString().split("=");
					log.info("t0="+tokens[0]);
					log.info("t1="+tokens[1]);
					keyRepository.delete(tokens[0]);
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return json_body;
			
		}
		
		

		//Ex of jsonbody {source:{ip:"127.0.0.1", port: 8080},keys:{"key1":"value1","key2":"value2"}}
			@RequestMapping(value = "/insync", method = RequestMethod.POST)
			public String inSyncUpdate(@RequestBody  String json_body) {
				
				//get all values from other services and create them if they do not exist
				log.info("Delete jsonbody: "+json_body);
				
				Map<String, Object> keys;
				try {
					keys = Utils.getKeys(json_body);
					log.info("Map is "+keys.toString());
					Iterator iter = keys.entrySet().iterator();
					while(iter.hasNext()) {
						Object obj= iter.next();
						log.info("Obj------->"+obj.toString());
						String[] tokens = obj.toString().split("=");
						log.info("t0="+tokens[0]);
						log.info("t1="+tokens[1]);
						keyRepository.update(new Key(tokens[0], tokens[1]));
					}
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return json_body;
				
			}
		
		
		/*
		 * Send key to Federation Controller	
		 */
	private void sendToFederationContoller(Key keyObj,RequestMethod method) {
		 
	    String fedserver = environment.getProperty("federation_controller.ip", String.class, "localhost");
	    String fedport = environment.getProperty("federation_controller.port", String.class, "8000");
	    
		if (ConnectionTools.pingHost(fedserver, Integer.parseInt(fedport), ConnectionTools.TIMEOUT)) {
			
			try {
				String ip = InetAddress.getLocalHost().getHostAddress();
		        String port = environment.getProperty("server.port", String.class, "8080");
		         
	           log.info(String.format("Send to Federation from Server %s:%s fedserver %s:%s", ip, port,fedserver,fedport));
	           
	           switch(method) {
	   		
	           case PUT : OutSyncClient.sendKey(Action.CREATE,keyObj, fedserver, fedport, ip, port);break;
	           case DELETE: OutSyncClient.sendKey(Action.DELETE,keyObj, fedserver, fedport, ip, port);break;
	           case POST : OutSyncClient.sendKey(Action.UPDATE,keyObj, fedserver, fedport, ip, port);break;	 
	           default : log.warn("NO action to switch for method= "+ method.toString());break;
	           }
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
		}
		else {
			log.warn(String.format(" Federation controller %s:%s request time out", fedserver,fedport));
		}
	}	

	
	

}
