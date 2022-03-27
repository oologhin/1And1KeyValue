package net.diezus.utils.json.repo;



import java.util.Map;

import net.diezus.utils.model.Key;

public interface KeyRepository {
	
	void save(Key key);
	Key find(String key);
	Map<String,Object> findAll();
	Key update(Key keyValue);
	Key delete(String keyValue);	
	int getSize();
	void dropAllKeys();
	boolean exists(String key);

}
