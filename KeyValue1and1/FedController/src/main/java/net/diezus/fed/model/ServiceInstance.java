package net.diezus.fed.model;

/**
 * @author ovidiu.loghin
 *
 */
public class ServiceInstance {
	private int id;
	private String ip;
	private String port;
	
	public ServiceInstance(String id, String ip, String port) {
		super();
		this.id = Integer.parseInt(id);
		this.ip = ip;
		this.port = port;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	public boolean equals(ServiceInstance obj) {
		if (obj == null) {
            return false;
        }
		if (!ServiceInstance.class.isAssignableFrom(obj.getClass())) {
	        return false;     
		}
		if ((this.ip == null) ? (obj.ip != null) : !this.ip.equals(obj.ip)) {
            return false;
        }
		else {
			if ((this.port == null) ? (obj.port != null) : !this.port.equals(obj.port)) {
	            return false;
	        }
		}
		return true;
	} 
	
	public String toString() {
		return"{\"id\":\""+this.id+"\",\"ip\":\""+this.ip+"\",\"port\":\""+this.port+ "\"}";
	}
	

}
