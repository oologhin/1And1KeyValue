package net.diezus.srv.keyvalue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import net.diezus.client.ConnectionTools;
import net.diezus.client.HttpClient;
import net.diezus.client.OutSyncClient;
import net.diezus.utils.Utils;
import net.diezus.utils.json.repo.KeyRepositoryImpl;
import net.diezus.utils.model.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;



@SpringBootApplication
public class KeyValueServiceJsonApplication implements ApplicationListener<ApplicationReadyEvent> {

	private static final Log log = LogFactory.getLog(KeyValueServiceJsonApplication.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	String ip;
	String port;
	
	
	public static void main(String[] args) {
		SpringApplication.run(KeyValueServiceJsonApplication.class, args);
		
		
		//KeyRepositoryImpl keyRepo = new KeyRepositoryImpl();
    	
		/*log.info("--Current Application has the controller:");
		log.info("server:"+federationProperties.getIp());
		log.info("port:"+federationProperties.getPort());
   	 	*/
    	/*for(int i=1;i<2000;i++) {
    		
    		System.out.println("i="+i);
    		Key key= new Key("key"+i, "value"+i);
    		keyRepo.save(key);
    		
    	}*/
    	
    	
    	/*for(int i=1;i<1000;i=i+25) {
    		
    		log.info("Size of the hash: "+keyRepo.getSize());
    		log.info("----existis key"+i+"?    ---> "+keyRepo.exists("key"+i));
    		log.info("----get---> "+keyRepo.find("key"+i));
    		log.info("----update key"+i+" with Ballonnes"+i+" ---> "+keyRepo.update(new Key("key"+i,"Ballones"+i)));
    		log.info("----delete key"+i+" with Ballonnes"+i+" ---> "+keyRepo.delete("key"+i));
    		
    	}*/
		
	}
	
	 /*
	  * After the application is ready this is going to run to subscribe the service to federation controller*/
	 @Override
	 public void onApplicationEvent(ApplicationReadyEvent event) {
		
	    }

}

