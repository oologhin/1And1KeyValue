package net.diezus.fed.repo;

import java.util.Vector;

import net.diezus.fed.model.ServiceInstance;

public interface RunningServices {
	int save(ServiceInstance servInst);
	ServiceInstance find(int id);
	Vector<ServiceInstance> findAll();
	ServiceInstance update(ServiceInstance keyValue);
	ServiceInstance delete(int id);	
	int getSize();
	boolean exists(ServiceInstance servInst);
}
