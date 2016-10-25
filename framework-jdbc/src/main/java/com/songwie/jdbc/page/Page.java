package com.songwie.jdbc.page;

import java.io.Serializable;

public class Page<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int showCount = 20; // 每页显示记录数
	private int totalPage; // 总页数
	private int totalResult; // 总记录数
	private int currentPage; // 当前页, 从1开始
	private int currentResult; // 当前记录起始索引, 从0开始
 
	public int getTotalPage() {
		totalPage = totalResult % showCount == 0 ? totalResult / showCount : totalResult / showCount + 1;
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalResult() {
		return totalResult;
	}


	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public int getCurrentPage() {
		if (currentPage <= 0)
			currentPage = 1;
		if (currentPage > getTotalPage())
			currentPage = getTotalPage();
		return currentPage;
	}

	public int getShowCount() {
		return showCount;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
 
	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}

	public int getCurrentResult() {
		if(currentResult==0){
			currentResult = (getCurrentPage() - 1) * getShowCount();
			if (currentResult < 0)
				currentResult = 0;
		}
		return currentResult;
	}

	public void setCurrentResult(int currentResult) {
		this.currentResult = currentResult;
	}

	@Override
	public String toString() {
		return "Page [showCount=" + showCount + ", totalPage=" + totalPage + ", totalResult=" + totalResult
				+ ", currentPage=" + currentPage + ", currentResult=" + currentResult + "]";
	}
 
	 
 
	
}