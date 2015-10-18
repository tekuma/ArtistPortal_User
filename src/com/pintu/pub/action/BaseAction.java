package com.pintu.pub.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import com.pintu.pub.page.PageResult;

@SuppressWarnings("rawtypes")
public class BaseAction extends ActionSupport implements ServletResponseAware,
		ServletRequestAware {
	private static final long serialVersionUID = 1L;
	public static final String MAINPAGE = "mainpage";
	public static final String LOGINPAGE = "loginpage";
	public static final String ERRORPAGE = "errorpage";
	public static final String MSGASKPAGE = "msgask";
	public HttpServletResponse response;
	public HttpServletRequest request;
	public PageResult pageResult;
	public Map sessionMap;
	public int pageNum = 1;
	public int numPerPage = 20;
	public String message = "";//提示信息
	public String skipActionStr = "";//表单action属性字符串
	public String inputParameName = "";//表单中隐藏的参数input名字 
	public String inputParameValue = "";//表单中隐藏的参数input值
	public String askBtnType = "";//提示信息中确定按钮的类型
	public String modalJavascipt="";//返回提示信息字符串

	public BaseAction() {
		this.message = null;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPageNum() {
		return this.pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getNumPerPage() {
		return this.numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setSession(Map map) {
		this.sessionMap = map;
	}

	public HttpServletRequest getHttpServletRequest() {
		return request;
	}

	public HttpServletResponse getHttpServletResponse() {
		return response;
	}

	public Map getSession() {
		return sessionMap;
	}

	public PageResult getPageResult() {
		return pageResult;
	}

	public void setPageResult(PageResult pageResult) {
		this.pageResult = pageResult;
	}

	public String getSkipActionStr() {
		return skipActionStr;
	}

	public void setSkipActionStr(String skipActionStr) {
		this.skipActionStr = skipActionStr;
	}

	public String getInputParameName() {
		return inputParameName;
	}

	public void setInputParameName(String inputParameName) {
		this.inputParameName = inputParameName;
	}

	public String getInputParameValue() {
		return inputParameValue;
	}

	public void setInputParameValue(String inputParameValue) {
		this.inputParameValue = inputParameValue;
	}

	public String getAskBtnType() {
		return askBtnType;
	}

	public void setAskBtnType(String askBtnType) {
		this.askBtnType = askBtnType;
	}

	public String getModalJavascipt() {
		return modalJavascipt;
	}

	public void setModalJavascipt(String modalJavascipt) {
		this.modalJavascipt = modalJavascipt;
	}

	/**
	 * 初始化pageResult，用数据集给pageResult赋值前，必须调用，一般在find函数开始时调用
	 * */
	public void pageResultInit() {
		if (null == this.pageResult) {
			this.pageResult = new PageResult();
		}
		this.pageResult.setPageNo(this.getPageNum());
		this.pageResult.setPageSize(this.getNumPerPage());
	}
	
	public void listToOutJson(List list) throws IOException{
		PrintWriter out = this.response.getWriter();
		Gson json = new Gson();
		if (list != null && list.size()>0) {
			String str = json.toJson(list);
			out.println(str);
		} else {
			out.println("[]");
		}
	}
	
	/**
	 * 提示错误信息
	 * */
	public void alertMSG(String alertInfo) {
		this.inputParameName = "";
		this.inputParameValue = "";
		this.skipActionStr = "";
		this.message = alertInfo;
		this.askBtnType = "close";
	}
	/**
	 * DWZ json返回
	 * 
	public void dwzReturnJson(int statusCode,String message, String navTabId,
			String forwardUrl, String callType, String title){
		try {
			PrintWriter out = this.response.getWriter();
			Gson json = new Gson();
			String DWZJson = "";
			if (statusCode == 0) {
				DWZJson =json.toJson(DWZUtil.getSuccessJson(message, navTabId,
						forwardUrl, callType, title));
			} else {
				DWZJson =json.toJson(DWZUtil.getFailedJson(message));
			}
			out.print(DWZJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 字符串json 返回
	 * */
	public void strReturnJson(String values){
		try {
			PrintWriter out = this.response.getWriter();
			out.print(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * entity实体 json返回
	 * */
	public void entityReturnJson(Object entity){
		this.response.setContentType("text/json;charset=UTF-8");
		try {
			PrintWriter out=this.response.getWriter();
			Gson json=new Gson();
			String jsonStr=json.toJson(entity);
			out.print(jsonStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * list集合json返回
	 * */
	public void listReturnJson(List list){
		this.response.setContentType("text/json;charset=UTF-8");
		try {
			PrintWriter out=this.response.getWriter();
			Gson json=new Gson();
			String jsonStr=json.toJson(list);
			if(list!=null&&list.size()>0){
				out.print(jsonStr);
			}else{
				out.print("[]");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 提交返回成功
	 * */
	public void submitSuccess(String title,String message,String url){
		this.modalJavascipt="<div id=\"successModal\" data-backdrop=\"static\" class=\"modal hide fade\" tabindex=\"-1\" role=\"dialog\" "+
				"aria-labelledby=\"myModalLabel2\" aria-hidden=\"true\">"+
				"<div class=\"modal-header\">"+
				"<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\"></button>"+
				"<h3 id=\"myModalLabel2\">"+title+"</h3>"+
				"</div>"+
				"<div class=\"modal-body\">"+
				"<p>"+message+"</p>"+
				"</div>"+
				"<div class=\"modal-footer\">"+
				"<button data-dismiss=\"modal\" class=\"btn green\" onclick=\"successModal_locationHref();\">确定</button>"+
				"</div>"+
				"</div>"+
				"<a href=\"#successModal\" id=\"aid_successModal_success\" "+
				"role=\"button\" class=\"btn btn-danger\" data-toggle=\"modal\" style=\"display:none;\"></a>"+
				"<script>$(\"#aid_successModal_success\").click();"+
				"function successModal_locationHref(){"+
				"window.location.href=\""+url+"\""+
				"}"+
				"</script>";
	}
	/**
	 * 提交返回失败
	 * */
	public void submitError(String title,String message){
		this.modalJavascipt="<div id=\"errorModal\" data-backdrop=\"static\" class=\"modal hide fade\" tabindex=\"-1\" role=\"dialog\" "+
				"aria-labelledby=\"myModalLabel2\" aria-hidden=\"true\">"+
				"<div class=\"modal-header\">"+
				"<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\"></button>"+
				"<h3 id=\"myModalLabel2\">"+title+"</h3>"+
				"</div>"+
				"<div class=\"modal-body\">"+
				"<p style=\"color: red\">"+message+"</p>"+
				"</div>"+
				"<div class=\"modal-footer\">"+
				"<button data-dismiss=\"modal\" class=\"btn green\">确定</button>"+
				"</div>"+
				"</div>"+
				"<a href=\"#errorModal\" id=\"aid_errorModal_error\" "+
				"role=\"button\" class=\"btn btn-danger\" data-toggle=\"modal\" style=\"display:none;\"></a>"+
				"<script>$(\"#aid_errorModal_error\").click();</script>";
	}
	/**
	 * 提交返回处理 实体
	 * */
	public void submitReturnJSONEntity(String returnTYPE,String returnMESSAGE,Object entity){
		this.response.setContentType("text/json;charset=UTF-8");
		try {
			PrintWriter out=this.response.getWriter();
			Gson json=new Gson();
			String entityString=json.toJson(entity);
			out.print("{\"returnTYPE\":\""+returnTYPE+"\",\"returnMESSAGE\":\""+returnMESSAGE+"\",\"returnDATA\":"+entityString+"}");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 提交返回处理 集合
	 * */
	public void submitReturnJSONList(String returnTYPE,String returnMESSAGE,List list){
		this.response.setContentType("text/json;charset=UTF-8");
		try {
			PrintWriter out=this.response.getWriter();
			Gson json=new Gson();
			String entityString=json.toJson(list);
			out.print("{\"returnTYPE\":\""+returnTYPE+"\",\"returnMESSAGE\":\""+returnMESSAGE+"\",\"returnDATA\":"+entityString+"}");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 提交返回处理 集合
	 * */
	public void submitReturnJSONString(String returnTYPE,String returnMESSAGE,String str){
		this.response.setContentType("text/json;charset=UTF-8");
		try {
			PrintWriter out=this.response.getWriter();
			out.print("{\"returnTYPE\":\""+returnTYPE+"\",\"returnMESSAGE\":\""+returnMESSAGE+"\",\"returnDATA\":\""+str+"\"}");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}