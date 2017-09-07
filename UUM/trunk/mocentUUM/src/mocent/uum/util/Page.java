package mocent.uum.util;

import java.util.List;

/**
 * 
 * @author jiang
 * @date 2016年9月28日  下午5:48:56
 * @Desc 分页实体类，用于定义分页属性
 */
public class Page<T> {
	private int totalData =0;
	private int pageSize = 10;
	private int currentPage =1;
	private int startIndex= 1;//用于limit分页
	private int endIndex;
	private List<T> result;
	
	
	public Page(){}
	
	public Page(int pageSize, int currentPage,int totalData) {
		this.pageSize = pageSize;
		this.currentPage = currentPage;
		this.totalData=totalData;
	}

	public int getTotalData() {
		return totalData;
	}

	public void setTotalData(int totalData) {
		this.totalData = totalData;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	
	public int getStartIndex() {
		return (currentPage-1)*pageSize;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return totalData%pageSize == 0 ? totalData/pageSize : (totalData/pageSize)+1;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
}
