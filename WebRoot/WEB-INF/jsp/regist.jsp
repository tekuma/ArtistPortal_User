<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>input information-2</title>
<script src="../js/jquery-1.11.2.min.js" type="text/javascript"></script>
<script src="../js/html5.js" type="text/javascript"></script>
<link href="../css/input information-2.css" rel="stylesheet" type="text/css"/>
<style type="">
#errormsg{ position:fixed; top:2%; left:50%;width:200px; margin-left:-156px; font-size:12px; display:none;color:red}
</style>
<script>
$(function(){
	$(".regist_form_warning").click(function(){
		$(".regist_form_warning").css("border","solid 1px #929597");
	});
});
	var regist_registcode;//邮箱验证码
	var regist_registEmail;//验证邮箱
	
	//注册
	function regist_registCommit(){
		var email=$("#inputid_regist_email").val();
		var registcode=$("#inputid_regist_registcode").val();
		var password=$("#inputid_regist_password").val();
		var passwordagin=$("#inputid_regist_passwordagin").val();
		var firstname=$("#inputid_regist_firstname").val();
		var lastname=$("#inputid_regist_lastname").val();
		var location=$("#inputid_regist_location").val();
		var nationality=$("#inputid_regist_nationality").val();
		var invitecode=$("#inputid_regist_invitcode").val();//邀请码
		var desc=$("#input_information-2_area").val();
		if(email==undefined||email==""){
			public_err_prompt("Mailbox cannot be empty",$("#inputid_regist_email"));
		}else if(email!=regist_registEmail){
			public_err_prompt("Mailbox Error",$("#inputid_regist_email"));
		}else if(invitecode==undefined||invitecode==""){
			public_err_prompt("Invitation code can not be empty",$("#inputid_regist_invitcode"));
		}else if(registcode==undefined||registcode==""){
			public_err_prompt("Registration code can not be empty",$("#inputid_regist_registcode"));
		}else if(registcode!=regist_registcode){
			public_err_prompt("Registration code error",$("#inputid_regist_registcode"));
		}else if(password==undefined||password==""){
			public_err_prompt("Password can not be empty",$("#inputid_regist_password"));
		}else if(password.length<6||password.length>15){
			public_err_prompt("Must be 6-15 characters or more",$("#inputid_regist_password"));
		}else if(passwordagin==undefined||passwordagin==""){
			public_err_prompt("Second times the password can not be empty",$("#inputid_regist_passwordagin"));
		}else if(password!=passwordagin){
			public_err_prompt("Two times the password is not consistent",$("#inputid_regist_passwordagin"));
		}else if(firstname==undefined||firstname==""){
			public_err_prompt("Last name can not be empty",$("#inputid_regist_firstname"));
		}else if(firstname.length>64){
			public_err_prompt("Firstname Must be 64 characters in the following",$("#inputid_regist_firstname"));
		}else if(lastname==undefined||lastname==""){
			public_err_prompt("Name can not be empt",$("#inputid_regist_lastname"));
		}else if(lastname.length>64){
			public_err_prompt("Lastname Must be 64 characters in the following",$("#inputid_regist_lastname"));
		}else if(location==undefined||location==""){
			public_err_prompt("Address can not be empty",$("#inputid_regist_location"));
		}else if(location.length>50){
			public_err_prompt("Location Must be 50 characters in the following",$("#inputid_regist_location"));
		}else if(nationality==undefined||nationality==""){
			public_err_prompt("Nationality can not be empty",$("#inputid_regist_nationality"));
		}else if(nationality.length>20){
			public_err_prompt("Nationality Must be 20 characters in the following",$("#inputid_regist_nationality"));
		}else if(desc.length>128){
			public_err_prompt("Description Must be 128 characters in the following",$("#input_information-2_area"));
		}else{
			$.ajax({
				url:"http://"+window.location.host+"/TekumaUserServer/system/system_saveMemberInfo.do",
				type:"post",
				data:{"member.loginname":email,"member.loginpwd":password,"member.lastname":lastname,
						"member.firstname":firstname,"member.introduction":desc,"member.location":location,
						"member.nationality":nationality,"invitecode":invitecode},
				success:function(isSuccess){
					if(isSuccess="success"){
						window.location.href="system_goCutAvatar.do";
					}else{
						public_err_prompt(isSuccess,$("#inputid_regist_email"));
					}
				}
			});
		}
	}
	
	//获取邮箱验证码
	function regist_getRegistCode(){
		var invitecode=$("#inputid_regist_invitcode").val();//邀请码
		var email=$("#inputid_regist_email").val();
		var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/; 
		if(email==undefined||email==""){
			public_err_prompt("Mailbox cannot be empty",$("#inputid_regist_email"));
		}else if(!reg.test(email)){
			public_err_prompt("Mailbox format is not correct",$("#inputid_regist_email"));
		}else if(invitecode==undefined||invitecode==""){
			public_err_prompt("Invitation code can not be empty",$("#inputid_regist_invitcode"));
		}else{
			regist_registEmail=email;
			$.ajax({
				url:"http://"+window.location.host+"/TekumaUserServer/system/system_sendMail.do",
				data:{"email":email,"userName":"qqq","type":"1","registCode":invitecode},
				type:"post",
				success:function(registcode){
					if(registcode=="1"){
						public_err_prompt("Invite code error",$("#inputid_regist_invitcode"));
					}else if(registcode=="2"){
						public_err_prompt("The mailbox has been registered",$("#inputid_regist_email"));
					}else{
						regist_registcode=registcode;
						public_err_prompt("We have sent the verification code to your email address.",$(""));
						//alert(registcode);
					}
				}
			});
		}
	}
	
	function public_err_prompt(nr,obj){
		obj.css("border","solid 1px rgb(242, 76, 76)");
		$("#errormsg").html(nr).fadeIn(300).fadeOut(3000);
	}
</script>

</head>
<body>
<!-- 错误提示 -->
<div id="errormsg"></div>

<a href="javascript:void(0)" class="input_information-2_header fr">How it works?</a>
<div class="input_information-2_content">
<input type="email" placeholder="E-mail" class="input_information-2_e-mail t regist_form_warning" id="inputid_regist_email"/>
<input type="text" placeholder="Invitation code" class="input_information-2_Invitation_code t regist_form_warning" id="inputid_regist_invitcode"/>
<span class="alert_pwd1 fr">*Your format is not correct.</span>
<input type="text" placeholder=" Verification Code" class="input_information-2_yanzhengma fl t regist_form_warning" id="inputid_regist_registcode"/>
<input type="button" value="Get" class="input_information-2_get fl t regist_form_warning" onclick="regist_getRegistCode()"/>

<input type="password" placeholder="Password" class="input_information-2_password t regist_form_warning" id="inputid_regist_password" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')"/>
<span class="alert_pwd2 fr">*Passwords are not less than six.</span>
<input type="password" placeholder="Again input" class="input_information-2_again_input t regist_form_warning" id="inputid_regist_passwordagin" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')"/>
<span class="alert_pwd3 fr">*The passwords you typed do not match.</span>
<input type="text" placeholder="First Name" class="input_information-2_first_name t fl regist_form_warning" id="inputid_regist_firstname"/>
<input type="text" placeholder="Last Name"class="input_information-2_last_name t fl regist_form_warning" id="inputid_regist_lastname"/>
<input type="text" placeholder="Location" class="input_information-2_location t fl regist_form_warning" id="inputid_regist_location"/>
<input type="text" placeholder="Nationality" class="input_information-2_nationality t fl regist_form_warning" id="inputid_regist_nationality"/>
<textarea id="input_information-2_area" class="t regist_form_warning" placeholder="Share something about yourself(maximum 500 characters)" id="inputid_regist_desc"></textarea>
<input type="button" value="Submit" class="input_information-2_submit" id="inputid_regist_submit" onclick="window.location.href='system/system_goSignup.do'"/>
</div>
<div class="clear"></div>
<div class="input_information-2_footer"><span class="fr">contact us:<a href="javascript:void(0)">hello@tekuma.io</a></span></div>
<!-- <div class="tishikuang" id="divid_regist_hint">We have to send a verification code to your mailbox, please check</div> -->
</body>
</html>
