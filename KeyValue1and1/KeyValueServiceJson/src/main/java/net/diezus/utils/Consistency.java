package net.diezus.utils;

public class Consistency {
	
	public enum Policy {ANY,EXISTING};
	
	private Policy policy;

	public Consistency(Policy policy) {
		super();
		this.policy = policy;
	
	}
	public Consistency(String policy) {
		super();
		switch (policy){
		case "ANY" : this.policy=Policy.ANY;break;
		case "EXISTING" : this.policy=Policy.EXISTING;break;
		}
	
	}

	public Policy getPolicy() {
		return policy;
	}
	
	
}
