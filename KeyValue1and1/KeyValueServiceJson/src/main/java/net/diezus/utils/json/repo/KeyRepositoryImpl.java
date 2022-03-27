package net.diezus.utils.json.repo;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.ParseException;

import net.diezus.utils.model.Key;
import net.diezus.srv.keyvalue.KeyValueServiceJsonApplication;
import net.diezus.utils.json.JsonSimpleReader;
import net.diezus.utils.json.JsonSimpleWriter;

public class KeyRepositoryImpl implements KeyRepository {
	
	private static final String REPO_NAME = "KeyValue";
	private JsonSimpleWriter jsonWriter;
	private JsonSimpleReader jsonReader;
	
	private static final Log log = LogFactory.getLog(KeyRepositoryImpl.class);
	
	//TODO you should make a singleton patern. If file already created just append to it
	public KeyRepositoryImpl() {
		try{
		this.jsonWriter= new JsonSimpleWriter(REPO_NAME);
		//this.jsonWriter.Init();
		this.jsonReader = new JsonSimpleReader(REPO_NAME);
		}
		catch(IOException e) {
			log.error(e);
			System.err.println(e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error("The file could not be initialy parsed " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	public void save(Key key) {
		try {
		this.jsonWriter.create(key);
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}

	public Key find(String key) {
			try {
				return (Key)this.jsonReader.getJsonObject(key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	}

	public Map<String, Object> findAll() {
		
		try {
			return this.jsonReader.getAllKeys();
		} catch (IOException | ParseException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	public Key update(Key key) {
		Object oldkey=new Object();
		try {
			oldkey = this.jsonWriter.update(key.getKey(), key.getValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Key)oldkey;

	}

	public Key delete(String keyValue) {
		Object oldkey=null;
		try {
			oldkey = this.jsonWriter.delete(keyValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Key)oldkey;

	}

	public int getSize() {
		try {
			return this.jsonReader.getSize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public boolean exists(String keyValue) {
		try {
			return this.jsonReader.existsKey(keyValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void forceStore() throws IOException {
		this.jsonWriter.savefile();
	}


	@Override
	public void dropAllKeys() {
		//erase file from disc
		try {
			this.jsonWriter.dropFile();
			this.jsonWriter= new JsonSimpleWriter(REPO_NAME);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

}
