package com.songwie.util.datatable;

import java.io.Serializable;
import java.util.List;

public class DataTablesBack<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String iTotalRecords;//一共有多少条数据
	private String iTotalDisplayRecords;//要显示多少条
	private String sEcho;//
	private List<T> aaData;//传回的数据
	
	
	
	
	public String getiTotalRecords() {
		return iTotalRecords;
	}
	public void setiTotalRecords(String iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}
	public String getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}
	public void setiTotalDisplayRecords(String iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}
	public String getsEcho() {
		return sEcho;
	}
	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}
	public List<T> getAaData() {
		return aaData;
	}
	public void setAaData(List<T> aaData) {
		this.aaData = aaData;
	}
	
	public DataTablesBack(String iTotalRecords,String iTotalDisplayRecords,String sEcho,List<T> aaData){
		this.aaData=aaData;
		this.iTotalDisplayRecords=iTotalDisplayRecords;
		this.iTotalRecords=iTotalRecords;
		this.sEcho=sEcho;
	}
	public DataTablesBack(){}

}
