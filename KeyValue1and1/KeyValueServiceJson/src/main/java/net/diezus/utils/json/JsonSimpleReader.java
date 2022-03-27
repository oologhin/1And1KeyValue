package net.diezus.utils.json;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.diezus.utils.model.Key;

public class JsonSimpleReader {

	private JSONParser parser;
	private Reader reader;
	private JSONObject jsonObj;
	private String fileName;
	
		public JsonSimpleReader(String fileName) throws IOException, ParseException{
			this.fileName= fileName+".json";
			this.parser = new JSONParser();
			this.reader= new FileReader(this.fileName);
			this.jsonObj = this.parse();
		
		}
		
		private JSONObject parse () throws IOException, ParseException {
			
			JSONObject jsonO = new JSONObject();
			File file = new File(this.fileName);
			
			
			if(file.length()>0) {
				this.reader= new FileReader(this.fileName);
				jsonO=(JSONObject)this.parser.parse(this.reader);
			}
			
			return jsonO;
			
		}
		
		public Object getJsonObject(String key) throws IOException, ParseException {
			this.jsonObj = this.parse();
			String value =(String)jsonObj.get(key);
			return (Object)new Key(key,value);
		}
		
		public Key retriveKey(String key) throws IOException, ParseException {
			this.jsonObj = this.parse();
			String value =(String)jsonObj.get(key);
			return new Key(key,value);
		}
		
		public String retriveKeyValue(String key) throws IOException, ParseException {
			this.jsonObj = this.parse();
			return (String)jsonObj.get(key); 
		}
		
		public int getSize() throws IOException, ParseException {
			this.jsonObj = this.parse();
			return this.jsonObj.size();
		}
		
		public boolean existsKey(String keyValue) throws IOException, ParseException {
			this.jsonObj = this.parse();
			return this.jsonObj.containsKey(keyValue);
		}
		
		@SuppressWarnings("unchecked")
		public Map<String, Object> getAllKeys() throws IOException, ParseException {
			this.jsonObj = this.parse();	
			return (Map<String,Object>)this.jsonObj;
		}
	
}
