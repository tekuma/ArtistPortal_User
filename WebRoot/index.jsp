<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>tekuma</title>
<link href="css/login.css" rel="stylesheet" type="text/css"/>
<script src="js/jquery-1.11.2.min.js"></script>

<link rel="icon" href="../images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="../images/favicon.ico" type="image/x-icon" />

<style>
#error{position:absolute;top:50%; left:50%; margin-left:-100px;}
#errormsg{font-size:12px; display:none;color:red}
</style>

<script>

//facebook Sdk 初始化
/* window.fbAsyncInit = function() {
  FB.init({
    appId      : '472101712951308',
    xfbml      : true,
    version    : 'v2.4'
  });
};

(function(d, s, id){
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) {return;}
	js = d.createElement(s); js.id = id;
	js.src = "//connect.facebook.net/en_US/sdk.js";
	fjs.parentNode.insertBefore(js, fjs);
 }(document, 'script', 'facebook-jssdk'));

//facebook登陆
function login_faceBookLogin(){
	 FB.login(function(response) {
	   Log.info('FB.login response', response);
	   alert(response.name);
	 });
	FB.login(function (response) { 
	    if (response.authResponse) { 
	        alert('Welcome!  Fetching your information.... '); 
	        FB.api('/me', function (response) { 
	            alert('Good to see you, ' + response.name + '.'); 
	        }); 
	    } else { 
	        alert('User cancelled login or did not fully authorize.'); 
	    } 
	});
} */

 $(function(){
	//自动登陆
	var email;
	var passward;
	$.ajax({
		url:"http://"+window.location.host+"/TekumaUserServer/system/system_getCookes.do",
		type:"post", async: false,
		success:function(data){
			email=data.split(":")[0]; 
			passward=data.split(":")[1]; 
		}
	});
	if(email!=undefined&&email!=""&&passward!=undefined&&passward!=""){
		$.ajax({
			url:"http://"+window.location.host+"/TekumaUserServer/system/system_memberLogin.do",
			data:{"member.loginname":email,"member.loginpwd":passward},
			type:"post",
			success:function(isSuccess){
				if(isSuccess!="1"&&isSuccess!="2"){
					var memberId=isSuccess.split("###")[1];
					window.location.href="system/system_InitWorksIndex.do";
				}
			}
		});
	}
}); 

function indexformwarning(){
	$(".reset_form_warning").css("border","solid 1px #929597");
}

function indexsignup(){
	var indexusername=$(".index_username").val();
	var indexuserpwd=$(".index_userpwd").val();
	if(indexusername!=""&&indexuserpwd!=""){
		var reg = /^([a-zA-Z0-9]+[_|\-|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\-|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/gi;
		if (!reg.test(indexusername)) {
			public_err_prompt("Mailbox format is not correct",$("#inputid_reset_loginname"));
		}else{
			window.location.href='system/system_goSignup.do?email='+indexusername+'&password='+indexuserpwd;
		}
	}else{
		public_err_prompt("Email or password can not be empty",$("#inputid_reset_loginname"));
		public_err_prompt("Email or password can not be empty",$("#inputid_reset_password"));
	}
}

function public_err_prompt(nr,obj){
	obj.css("border","solid 1px rgb(242, 76, 76)");
	$("#errormsg").html(nr).fadeIn(300).fadeOut(5000);
}


</script>
</head>
<body>
<div class="login_container">
<img id="login_logo" src="images/wire-framing_20.png" alt=""/>
<div class="index_input">
<div id="error"><div id="errormsg">error!</div></div>
<div class="clear"></div>
<input type="email" placeholder="e-mail" class="index_username reset_form_warning" id="inputid_reset_loginname" onclick="indexformwarning()" name="email"/>
<input type="password" placeholder="password" class="index_userpwd reset_form_warning" id="inputid_reset_password" name="password"/>
<div class="clear"></div>
<a href="#" class="index_signup" onclick="indexsignup()">sign up</a>
<div class="index_or"><span class="fl"></span><font></font><span class="fr"></span></div>
<div class="index_login" onclick="window.location.href='system/system_goLogin.do'">log in</div>

</div>
<div class="login_footer">
<a href="#" class="login_header fl" onclick="window.location.href='system/system_goCutAvatar.do'">How it works?</a>
<span class="fr">contact us:<a href="#">hello@tekuma.io</a></span>
</div>
</div>
</body>
</html>
