<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>passwd</title>

<script src="../js/jquery-1.11.2.min.js"></script>
<link href="../css/pwreset.css" rel="stylesheet" type="text/css"/>


<style>
#errormsg{ position:fixed; top:48%; left:55%; margin-left:-126px; font-size:12px; display:none;color:red}
</style>

<script >
var regist_registcode;//邮箱验证码
var regist_registEmail;//验证邮箱

//注册
function reset_resetCommit(){
	var email=$("#inputid_reset_loginname").val();
	var registcode=$("#passwd_identifying_code").val();
	var password=$("#inputid_reset_pwd").val();
	var passwordagin=$("#inputid_reset_repwd").val();
	
	if(email==undefined||email==""){
		public_err_prompt("Mailbox cannot be empty",$("#inputid_reset_loginname"));
	}else if(email!=regist_registEmail){
		public_err_prompt("Mail and registered mail is not consistent",$("#inputid_reset_loginname"));
	}else if(registcode==undefined||registcode==""){
		public_err_prompt("Verification code can not be empty",$("#passwd_identifying_code"));
	}else if(registcode!=regist_registcode){
		public_err_prompt("Registration code error",$("#passwd_identifying_code"));
	}else if(password==undefined||password==""){
		public_err_prompt("Password can not be empty",$("#inputid_reset_pwd"));
	}else if(password.length<6||password.length>15){
		public_err_prompt("Must be 6-15 characters or more",$("#inputid_reset_pwd"));
	}else if(passwordagin==undefined||passwordagin==""){
		public_err_prompt("Second times the password can not be empty",$("#inputid_reset_repwd"));
	}else if(password!=passwordagin){
		public_err_prompt("Two times the password is not consistent",$("#inputid_reset_repwd"));
	}else{
		$.ajax({
			url:"system/system_saveResetPassword.do",
			type:"post",
			data:{"loginName":email,"pwd":password,"type":"2"},
			success:function(){
				window.location.href="system_goLogin.do";
			}
		});
	}
}

//获取邮箱验证码
function reset_getResetCode(){
	var email=$("#inputid_reset_loginname").val();
	regist_registEmail=email;
	var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/; 
	if(email==undefined||email==""){
		public_err_prompt("Mailbox cannot be empty",$("#inputid_reset_loginname"));
	}else if(!reg.test(email)){
		public_err_prompt("Mailbox format is not correct",$("#inputid_reset_loginname"));
	}else{
		inter = setInterval("countDown()",1000);
		alert("The verification code is sent to your mailbox, please check.");
		$.ajax({
			url:"system/system_sendMail.do",
			data:{"email":email,"userName":"qqq","type":"2"},
			type:"post",
			success:function(registcode){
				if(registcode=="2"){
					public_err_prompt("The mailbox has not been registered",$("#inputid_reset_loginname"));
				}else{
					regist_registcode=registcode;
				}
			}
		});
	}
}

function resetformwarning(){
	$(".reset_form_warning").css("border","solid 1px #929597");
}

function public_err_prompt(nr,obj){
	obj.css("border","solid 1px rgb(242, 76, 76)");
	$("#errormsg").html(nr).fadeIn(300).fadeOut(3000);
}


	var t = 60;
	var inter;
	function countDown(){
		$("#d").val("Wait"+" "+"("+t+")");
		t--;
		$("#d").attr("disabled",true); 
		if (t==0) {
			alert("Please enter the verification code, if not received, please re - obtain");
			$("#d").attr("disabled",false); 
			$("#d").val("Get");
			clearInterval(inter);
		}
	}

	




</script>
</head>
<body>

<!-- 错误提示 -->
<div id="errormsg">error!</div>
 
<div class="passwd_content">
<input type="email" placeholder="Login Name" class="passwd_Login_name passwd_t" id="inputid_reset_loginname" onclick="resetformwarning()"/>
<span class="passwd_t1 fr">* Your format is not correct</span>
<input type="text" placeholder="identifying code"class="passwd_t fl" onclick="resetformwarning()" id="passwd_identifying_code"/>
<input type="button" value="Get" class="passwd_t fl" id="d" onclick="reset_getResetCode()"/>
<span class="passwd_t2 fr">* Verification code error</span>
<input type="password" placeholder="Enter a new password" class="passwd_new passwd_t" onclick="resetformwarning()" id="inputid_reset_pwd" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')"/>
<input type="password" placeholder="Again input" class="passwd_again  passwd_t" onclick="resetformwarning()" id="inputid_reset_repwd" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')"/>
<%-- <span class="passwd_t3 fr" id="spanid_resert_error">* Two input is not consistent</span> --%>
<input type="button" value="Submit" class="passwd_submit" onclick="reset_resetCommit()"/>
</div>
<div class="clear"></div>
<div class="passwd_footer"><a href="javascript:void(0)" class="passwd_header fl">How it works?</a><span class="fr">contact us: <a href="javascript:void(0)">hello@tekuma.io</a></span></div>
</body>
</html>
