package net.diezus.fed.repo;

import java.util.Vector;

import net.diezus.fed.model.ServiceInstance;

public class RunningServicesImpl implements RunningServices {
	
	private  Vector<ServiceInstance> runningInstaces;
	
	

	public RunningServicesImpl() {
		super();
		this.runningInstaces=new Vector<ServiceInstance>();
	}

	@Override
	public int save(ServiceInstance servInst) {
		boolean exist= exists(servInst);
		if(!exist) {
			this.runningInstaces.add(servInst);
			return 1;
		}
		return -1;
	}

	@Override
	public ServiceInstance find(int id) {
		
		for(int i=0; i<this.runningInstaces.size();i++) {
			if(runningInstaces.get(i).getId()==id)
				return runningInstaces.get(i);
		}
		return null;
	}

	@Override
	public Vector<ServiceInstance> findAll() {
	
		return this.runningInstaces;
	}

	@Override
	public ServiceInstance update(ServiceInstance servInst) {
		for(int i=0; i<this.runningInstaces.size();i++) {
			if(runningInstaces.get(i).equals(servInst))
			{
				runningInstaces.set(i, servInst);
				return servInst;
			}
		}
		
				
		return null;
	}

	@Override
	public ServiceInstance delete(int id) {
		
		for(int i=0; i<this.runningInstaces.size();i++) {
			if(runningInstaces.get(i).getId()==id)
			{
				ServiceInstance foundObj=runningInstaces.get(i);
				runningInstaces.remove(i);
				return foundObj;
			}
		}
		return null;
	}

	@Override
	public int getSize() {
		
		return this.runningInstaces.size();
	}

	@Override
	public boolean exists(ServiceInstance servInst) {
		for(int i=0; i<this.runningInstaces.size();i++) {
			if(runningInstaces.get(i).equals(servInst))
				return true;
		}
		return false;
	}

	public ServiceInstance get(int i) {
		// TODO Auto-generated method stub
		return runningInstaces.get(i);
	}

}
