package mocent.kx.util;

import java.util.ArrayList;
import java.util.List;

public class PageUtil<T> {
	private int curPage=1;
	private List<T> data=new ArrayList<T>();
	private boolean success=true;
	private int totalRows=1;
	
	public PageUtil() {}
	public PageUtil(int curPage,int totalRows) {
		this.curPage = curPage;
		this.totalRows = totalRows;
	}
	public int getCurPage() {
		return curPage;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
}
