package net.diezus.srv.keyvalue.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;






@RestController
public class RootController {
	
	private static final Log log = LogFactory.getLog(RootController.class);
	
	@Autowired
	Environment environment;

	//@Autowired
	//FederationPropoerties fedProp;
	@Value("${federation_controller.ip}")
	private String controllerIp;
	
	@Value("${federation_controller.port}")
	private String controllerPort;	
	
	public RootController() {
		super();
		log.info("Fed ip"+controllerIp);
		log.info("Fed port"+controllerPort);
		
	}
	
	@RequestMapping("/")
	public String home() {
		return "<h1>Key ValueService Json Project:</h1>"
				+ "<li>Key Value implementation Root call of the process!"
				+ "<li> Try /keys"
				+ "<li> Try /init"
				+ "<li> Try /help"
				+ "<h3>Spring boot is working!</h3>";
	}
	@RequestMapping("/help")
	public String help() {
		return "<h1>HELP FOR Key ValueService Json Project:</h1>"
				+ "<li>Key Value implementation Root call of the process!"
				+ "<li> The folowing represets api requests"
				+ "<li> Try /init?size=100"
				+ "<li> Try /keys"
				+ "<li> Try /keys/size"
				+ "<li> Try /keys?key=calue"
				+ "<li> Try /keys/{keyid}"
				+ "<li> Try /keys/findall"
				+ "<h3>Spring boot is working!</h3>";
	}
	
	@RequestMapping("/controller")
	public String getcontroller() {
		
		return "<h1>CONTROLLER SETTINGS</h1>"
				+"server:"+""+controllerIp
				+"<br>port:"+controllerPort;
	}
	
	@RequestMapping("/environment")
	public String getEnvironment() {
		
		String hostAddreess="";
		String hostName="";
		try {
			hostAddreess = InetAddress.getLocalHost().getHostAddress();
			hostName=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "<h1>This instance environement</h1>"
				+"<br>,server:"+hostAddreess
				+"<br>,server name :"+hostName
				+"<br>port:"+environment.getProperty("local.server.port");

	}

}
