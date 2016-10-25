package com.songwie.util.datatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
public class DataTablesUtil {
	/**
	 * 获取ajax请求的参数
	 * @param JsonArgument
	 * @param argumentName
	 * @return
	 */
	public  static String getRequestArgumentVal(String JsonArgument, String argumentName) {
		try {
			JSONArray jsonarray = new JSONArray(JsonArgument);
			for (int i = 0; i < jsonarray.length(); i++) // 从传递参数里面选出待用的参数
			{
				JSONObject obj = (JSONObject) jsonarray.get(i);

				if (obj.get("name").equals(argumentName)) {
                      return obj.get("value").toString();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 返回组装后的DataTable Map对象，方便序列化成JQuery的支持的对象数组,用于CardWeb项目
	 * @param iTotalDisplayRecords 显示多少条
	 * @param list 一个Map的List集合
	 * @returns
	 */
	public static Map getDataTablesMap(Integer iTotalDisplayRecords,List list ){
		  Map<String,Object> dataTablesBack=new HashMap();
		     dataTablesBack.put("aaData",list );
		     dataTablesBack.put("iTotalDisplayRecords", iTotalDisplayRecords);
		     dataTablesBack.put("iTotalRecords",iTotalDisplayRecords);
		     return dataTablesBack;
	}
}
