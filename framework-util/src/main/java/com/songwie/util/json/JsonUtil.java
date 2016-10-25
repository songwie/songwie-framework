package com.songwie.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class JsonUtil implements java.io.Serializable {

	private static final long serialVersionUID = -8872078079583695100L;
	private static ThreadLocal<JsonUtil> jsonUtilLocal = new ThreadLocal<>();

	private JsonUtil() {
	}

	public static JsonUtil getInstance() {
		if (jsonUtilLocal.get() == null) {
			jsonUtilLocal.set(new JsonUtil());
		}
		return jsonUtilLocal.get();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> object2Map(Object obj,SerializerFeature... serializerFeature) {
		if(obj == null){
			return new HashMap<>();
		}
		String json = object2JSON(obj,SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.WriteNullNumberAsZero,SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteMapNullValue);
	    Map<String, Object>  map =  (Map<String, Object>) JSONObject.parse(json);
		
		return map;
	}
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> Objects2List(Object obj,SerializerFeature... serializerFeature) {
		if(obj == null){
			return new ArrayList<Map<String, Object>>();
		}
		String json = object2JSON(obj);
		List<Map<String, Object>> rows =  (List<Map<String, Object>>) JSONObject.parse(json);
		
		return rows;
	}

	public String object2JSON(Object obj,SerializerFeature... serializerFeature) {
		if(obj == null){
			return "{}";
		}
		return JSON.toJSONString(obj,serializerFeature);
	}
	
	public String object2JSON(Object obj) {
		if(obj == null){
			return "{}";
		}
		return JSON.toJSONString(obj,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.QuoteFieldNames,SerializerFeature.WriteNullListAsEmpty,SerializerFeature.WriteNullStringAsEmpty);
	}
	
	public JSONObject object2JSONObject(Object obj) {
        return JSON.parseObject(object2JSON(obj));
	    
	}

	public <T>  T json2Object(String json,Class<T> clazz) {
		if(json == null || json.isEmpty()){
			return null;
		}
		return JSON.parseObject(json, clazz);
	}
	
	public <T> T json2Reference(String json, TypeReference<T> reference){
		if(json == null || json.isEmpty()){
			return null;
		}
		return JSON.parseObject(json, reference);
	}
	
	public <T> T json2Reference(String json, TypeReference<T> reference, Feature... features){
		if(json == null || json.isEmpty()){
			return null;
		}
		return JSON.parseObject(json, reference, features);
	}
	
	public <T> T map2Object(Map<String,Object> map,Class<T> clazz){
		return this.json2Object(JSON.toJSONString(map), clazz);
	}
	
	public JSONObject string2JSON(String str){
		return JSON.parseObject(str);
	}
	
    public JSONArray string2JSONArray(String str){
        return JSON.parseArray(str);
    }
}