package net.diezus.fed.controller;


import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.diezus.client.ConnectionTools;
import net.diezus.client.HttpClient;
import net.diezus.fed.model.ServiceInstance;
import net.diezus.fed.repo.RunningServicesImpl;
import net.diezus.utils.Utils;

@RestController
public class FederationController {
	
	private RunningServicesImpl runningServices = new RunningServicesImpl();
	
	private static final Log log = LogFactory.getLog(FederationController.class);

	public RunningServicesImpl getRunningServices() {
		return runningServices;
	}

	@RequestMapping("/")
	public String home() {
		return "<h1>Federation controller:</h1>"
				+ "<li>this is the root path!"
				+ "<li> any key value will access /instance"
				+ "<h3>Spring boot is working!</h3>";
	}
	
	@RequestMapping("/services/count")
	public String count() {
		return ""+runningServices.getSize();
    }
	
	@RequestMapping(value="/services", method = RequestMethod.PUT)
	public String subscribe(@RequestBody  String json_body) {
		try {
			ServiceInstance newsrv=Utils.getServiceInstanceObject(json_body);
			if (runningServices.save(newsrv)>0)
			return "Successfully subscriobed services"
					+ "<br>body is "+json_body;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "Could not subscribed sevice"
				+ "<br>body is "+json_body
				+ "We expect an encoded string like {\"id\": \"id_value\", \"ip\": \"ip_value\",port:\"port_value\"}";
    }
	
	@RequestMapping("/services")
	public String findAll() {
		Vector <ServiceInstance> services = runningServices.findAll();
		
		return services.toString();
	}
	
	
	//Ex of jsonbody {source:{ip:"127.0.0.1", port: 8080},keys:{"key1":"value1","key2":"value2"}}
	//get the caller ip/port of the process
	//get all the values from affiliated services
	//spread them to all the affiliated services but the requester
	@RequestMapping(value = "/synckeys", method = RequestMethod.PUT)
	public String spreadKeys(@RequestBody  String json_body) {
		log.info("Body in synckeys "+json_body);
		return syncKeys(json_body, RequestMethod.PUT);
	}
	
	//Ex of jsonbody {source:{ip:"127.0.0.1", port: 8080},keys:{"key1":"value1","key2":"value2"}}
	//get the caller ip/port of the process
	//get all the values from first afilieated service
	//share it with the requester
	@RequestMapping(value = "/synckeys", method = RequestMethod.GET)
	public String getKeys(@RequestParam(value="ip") String  ip,@RequestParam(value="port") String port) {
		
		return getExistingKeys( ip,  port, RequestMethod.GET);

	}
	
	
	@RequestMapping(value = "/synckeys", method = RequestMethod.POST)
	public String updateKeys(@RequestBody  String json_body) {
		
		return syncKeys(json_body, RequestMethod.POST);

	}
	
	@RequestMapping(value = "/syncdelkeys", method = RequestMethod.DELETE)
	public String deleteKeys(@RequestBody  String json_body) {
		
		log.info("Body in synckeys "+json_body);
		return syncKeys(json_body, RequestMethod.DELETE);
	}
	
	

	private String syncKeys(String json_body, RequestMethod method ) {
		
		log.info("body of "+method+" : " +json_body);
		String retString="";
		try {
			Map<String,Object> keys = Utils.getKeys(json_body);
			log.info("Map is "+keys.toString());
			
			ServiceInstance source = Utils.getSourceServiceInstance(json_body);
			log.info("Source  is "+source);
			for(int i=0;i<runningServices.getSize();i++) {
				ServiceInstance srvInst = runningServices.get(i);
				
				if(!srvInst.equals(source) && !eraseUnrespondingService(srvInst)) {
					String url ="http://"+srvInst.getIp()+":"+srvInst.getPort();
					
					JSONObject jsonobject= new JSONObject();
					
					JSONObject sourceObject = new JSONObject();
					sourceObject.put("ip", source.getIp());
					sourceObject.put("port", source.getPort());
					
					jsonobject.put("source", sourceObject);
					
					JSONObject keysObject = new JSONObject();
					keysObject.putAll(keys);
					
					jsonobject.put("keys",keysObject);
					log.info("jsonObject to send is "+jsonobject.toJSONString());
					try {
							
							switch(method) {
								case DELETE: retString = HttpClient.sendDelete(url+"/indelsync", jsonobject.toJSONString());break;
								case PUT: retString = HttpClient.sendPut(url+"/insync", jsonobject.toJSONString());break;
								case POST: retString = HttpClient.sendPOST(url+"/insync", jsonobject.toJSONString());break;
							}
						} catch (Exception e) {
							log.error(e.toString());
							retString = "Error on Controller syncKeys method "+method+"in Federation Controller sending to "+url;
						}
					
				}
			}
			
		} catch (ParseException e) {
			log.error(e.getStackTrace());
			retString = "Error on syncKeys method+"+method+"parsing the input "+json_body;
		}
		
		return retString;
	}

	private String getExistingKeys(String ip, String port,RequestMethod method) {

		String retString ="";
		ServiceInstance source= new ServiceInstance("0", ip, port);
		
			log.info("Source  is ip"+source);
	
			for(int i=0;i<runningServices.getSize();i++) {
				ServiceInstance srvInst = runningServices.get(i);
				
				if(!srvInst.equals(source)) {
					//get all the values of the running service	
						String url = "http://"+srvInst.getIp()+":"+srvInst.getPort()+"/keys";
					try {
						if(!eraseUnrespondingService(srvInst))
							retString = HttpClient.sendGet(url);
					} catch (Exception e) {
						log.error(e.toString());
						retString = "Error on Controller getKeys in Federation Controller could not get keys ";
					}
					
					//Make sure you iterate only once untill you any running service
					return retString;
				}
			}
		
		return retString;
	}
	
	private boolean eraseUnrespondingService(ServiceInstance srvInst) {
		
		if (!ConnectionTools.pingHost(srvInst.getIp(),Integer.parseInt(srvInst.getPort()), ConnectionTools.TIMEOUT)) {
			runningServices.delete(srvInst.getId());
			return true;
		}
		return false;
	}
}
