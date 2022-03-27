package net.diezus.fed.model;

import java.io.Serializable;


public class Key implements Serializable{
	
	/**
	 * {"key": "my-key", "value": "my-val"}
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;		
	private String value;
	
	public Key(String key, String value) {		
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	//{"key": "my-key", "value": "my-val"}
	@Override
	public String toString() {
		return String.format("{Key:'%s', value:'%s'}",key,value);
	}
	

}
