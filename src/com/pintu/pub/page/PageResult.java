package com.pintu.pub.page;

import java.util.ArrayList;
import java.util.List;

public class PageResult {
	private List list = new ArrayList();
	private int pageNo = 0;//当前页码
	private int pageSize = 0;//每页显示条数
	private int pageTotal = 0;//总页数
	private int recTotal = 0;//总记录数

	public int getPageNo() {
		return this.pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRecTotal() {
		return this.recTotal;
	}

	public void setRecTotal(int recTotal) {
		this.recTotal = recTotal;
	}

	public int getPageTotal() {
		return this.pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public List getList() {
		return this.list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public int getBeginRec() {
		int tempi = (this.pageNo - 1) * this.pageSize + 1;
		if (tempi <= 0) {
			return 1;
		} else {
			return tempi;
		}
	}

	public int getEndRec() {
		int tempi = this.getBeginRec() + this.pageSize - 1;
		if (tempi <= 0) {
			tempi = 1;
		} else if (tempi > this.recTotal) {
			tempi = this.recTotal;
		}
		return tempi;
	}
}
