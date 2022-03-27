package net.diezus.utils.json;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import net.diezus.utils.model.Key;


public class JsonSimpleWriter {

	private static final Log log = LogFactory.getLog(JsonSimpleWriter.class);
	
	private FileWriter file;
	private JSONObject jsonObj;// = new JSONObject();
	private String fileName;
	
	public JsonSimpleWriter(String fileName) throws IOException, ParseException{
		this.fileName = fileName+".json";
		
		this.file= new FileWriter(this.fileName,true);
		
		init();
		
		log.info("file "+fileName+" ready for json");
	}
	
	public  void init() throws IOException, ParseException {
		
		File file = new File(this.fileName);
		log.info("File length is "+file.length());
		if(file.length()>0) {
			
			JSONParser parser = new JSONParser();
			this.jsonObj = (JSONObject) parser.parse(new FileReader(this.fileName));
		}
		else {
			this.jsonObj= new JSONObject();
		}
	}
	
	public void savefile() throws IOException {
		
		//this is not optimised....I need to make sure I save only when I need it
		//mai bine dau la finalul fisierului
		//ma duc sterg ultima }
		//apendez cheia si ultima acoloada...la delete si update nu merge
		
		File file = new File(this.fileName);
		log.debug("File Length"+file.length());
		if(file.length()>0) {
			//open it the first time
			this.file= new FileWriter(this.fileName);
			this.file.write(this.jsonObj.toJSONString());
			this.file.flush();
			this.file.close();
		}
		else {
			this.file.write(this.jsonObj.toJSONString());
			this.file.flush();
			this.file.close();
			System.out.print(this.jsonObj.toJSONString());
			
			
		}
		//log.info("File Saved!File closed");
	}
	
	@SuppressWarnings("unchecked")
	public  boolean create(Object keyvalue) throws IOException {
		
			Key key = (Key)keyvalue;
			this.jsonObj.put(key.getKey(),key.getValue());

			savefile();
	
			
		return true;
	}
	
	/*Returns null if the key is not found*/
	public  Object delete(String key) throws IOException {
		
			Object obj= this.jsonObj.remove(key);
			if(obj!=null) {
				savefile();
				return (Object)new Key(key,(String)obj);
			}
			 
		return obj;
	}
	
	
	/*Returns null if the key is not found*/
	@SuppressWarnings("unchecked")
	public  Object update(String key, String newValue) throws IOException {
		
			Object obj= this.jsonObj.replace(key, newValue);
			if(obj!=null) {
				savefile();
				return (Object)new Key(key,(String)obj);
			}
			 
		return obj;
	}

	public void dropFile() throws IOException{
		
		File file = new File(this.fileName);
		if(file.isFile() && file.length()> 0 && file.exists()) 
			file.delete();
		
		log.info("File delete!");
		
	}

}

