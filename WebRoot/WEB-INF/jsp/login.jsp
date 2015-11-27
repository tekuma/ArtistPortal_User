<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>login</title>
<link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
<script src="../js/jquery-1.11.2.min.js"></script>
<script src="../js/html5.js"></script>

<link href="../css/login2.css" rel="stylesheet" type="text/css"/>


<style type="">
#errormsg{ position:absolute; top:50%; left:50%;width:200px; margin-left:-76px; margin-top:-75px; font-size:12px; display:none;color:red}
</style>
<script>
/* //facebook Sdk 初始化
window.fbAsyncInit = function() {
  FB.init({
    appId      : '120985198249602',
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
	FB.login(function (response) { 
	    if (response.authResponse) { 
	        FB.api('/me',{fields:'last_name,first_name,about,location,hometown,website'},function (response) { 
	            $.ajax({
	            	url:"http://"+window.location.host+"/TekumaUserServer/system/system_MemberFBLogin.do",
	            	data:{"member.lastname":response.last_name,
						"member.firstname":response.first_name,"member.introduction":response.about,"member.location":response.location,
						"member.nationality":response.hometown,"member.website":response.website,"member.fbid":response.id},
	            	success:function(member){
	            		alert("success");
	            		window.location.href="system_InitWorksIndex.do";
	            	},error:function(){
	            		alert("error");
	            	}
	            });
	        }); 
	    } else { 
	        alert('User cancelled login or did not fully authorize.'); 
	    } 
	},{scope: 'public_profile,email'});
} */

//注册完毕后登陆
$(function(){
	var email=$("#input_login_email").val();
	var password=$("#input_login_password").val();
	if(email!=""&&email!=undefined&&password!=""&&password!=undefined){	
		$("#inputid_login_name").val(email);
		$("#inputid_login_password").val(password);
	}
	
	
	//自动登陆
	/* var name=${name};
	var pass=${pass};
	if(name!=undefined&&name!=""&&pass!=undefined&&pass!=""){	
		$.ajax({
			url:"http://"+window.location.host+"/TekumaUserServer/system/system_memberLogin.do",
			data:{"member.loginname":name,"member.loginpwd":pass},
			type:"post",
			success:function(isSuccess){
				if(isSuccess=="1"){
					window.location.href='http://127.0.0.1:8080/TekumaUserServer/';
				}else if(isSuccess=="2"){
					window.location.href='http://127.0.0.1:8080/TekumaUserServer/';
				}else{
					var memberId=isSuccess.split("###")[1];
					window.location.href="system_InitWorksIndex.do";
				}
			}
		});
	} */
});

	//验证登陆
	function login_loginCommit(){
		var name=$("#inputid_login_name").val();
		var password=$("#inputid_login_password").val();
		if(name!=undefined&&name!=""){
			if(password!=undefined&&password!=""){
				$.ajax({
					url:"system/system_memberLogin.do",
					data:{"member.loginname":name,"member.loginpwd":password},
					type:"post",
					success:function(isSuccess){
						if(isSuccess=="1"){
							public_err_prompt("The account does not exist",$("#inputid_login_name"));
						}else if(isSuccess=="2"){
							public_err_prompt("Password error",$("#inputid_login_password"));
						}else{
							var memberId=isSuccess.split("###")[1];
							window.location.href="system_InitWorksIndex.do";
						}
					}
				});
			}else{
				public_err_prompt("Password can not be empty",$("#inputid_login_password"));
			}
		}else{
			public_err_prompt("Account can not be empty",$("#inputid_login_name"));
			//用户名不能为空
		}
	}
	
	function loginwarning(){
		$(".login_warning").click(function(){
			$(".login_warning").css("border","solid 1px #929597");
		});
	}
	
	function public_err_prompt(nr,obj){
		obj.css("border","solid 1px rgb(242, 76, 76)");
		$("#errormsg").html(nr).fadeIn(300).fadeOut(3000)
	}
	
	
	//点击键盘enter按钮验证登陆
	$(document).keyup(function(event){
		var name=$("#inputid_login_name").val();
		var password=$("#inputid_login_password").val();
		 switch(event.keyCode) {
			 case 13:
				if(name!=undefined&&name!=""&&password!=undefined&&password!=""){
					$.ajax({
						url:"system/system_memberLogin.do",
						data:{"member.loginname":name,"member.loginpwd":password},
						type:"post",
						success:function(isSuccess){
							if(isSuccess=="1"){
								public_err_prompt("The account does not exist",$("#inputid_login_name"));
							}else if(isSuccess=="2"){
								public_err_prompt("Password error",$("#inputid_login_password"));
							}else{
								var memberId=isSuccess.split("###")[1];
								window.location.href="system_InitWorksIndex.do";
							}
						}
					});
				}
		 }
	});	
	
	
	//点击注册-清除cookie
	function signup(name)
	{ 
		$.ajax({
			url:"system/system_DelCookes.do",
			type:"post", async: false,
			success:function(data){
			}
		});
		window.location.href='http://127.0.0.1:8080/ArtistPortal_User';
	}
	

</script>
<script>
$(function(){
	$(".login2_input").fadeToggle(1000)
	})
</script>
</head>
<body>
<input type="hidden" id="input_login_email" value="${email}" />
<input type="hidden" id="input_login_password" value="${password}" />


 
<%--
<a href="#" class="sign_in_header fr" onclick="login_faceBookLogin()">How it works?</a>
<div class="sign_in_content">
<span class="sign_in_a"><img src="../images/Layer-15.jpg" alt=""/></span>
<input type="text" placeholder="LoginName" class="sign_in_Login_name t login_warning" id="inputid_login_name" value="${name}"/>
<input type="password" placeholder="password"class="sign_in_Passwd t login_warning" id="inputid_login_password" onselectstart="return false" oncopy="return false;" oncut="return false;" onpaste="return false"  value="${pass}"/>
<input type="button" value="Submit" class="sign_in_submit" onclick="login_loginCommit()"/>
<a href="#" class="sign_in_forget" onclick="window.location.href='system_goReset.do'">Forget Password</a>
</div>
<div class="clear"></div>
<div class="sign_in_footer"><span class="fr">contact us: <a href="#">hello@tekuma.io</a></span></div> --%>


<div class="login2_container">

<img id="login2_logo" src="../images/wire-framing_20.png" alt="" />
<div class="login2_input">
<div id="errormsg">您的信息输入错误，请重试!</div>
<div class="clear"></div>
<input type="text" placeholder="email" id="inputid_login_name" class="login2_email" onclick="loginwarning()" value="${name}"/>
<input type="password" placeholder="password" class="login2_pwd" id="inputid_login_password" onclick="loginwarning()" onselectstart="return false" oncopy="return false;" oncut="return false;" value=""/>
<div class="clear"></div>
<a href="#" class="login2_forget" onclick="window.location.href='system_goReset.do'">forget your password ?</a>
<a href="#" class="login2_signup" onclick="signup('user')">sign up</a>
<div class="clear"></div>
<div class="login2_or"><span class="fl"></span><font></font><span class="fr"></span></div>
<div class="login2_login2" onclick="login_loginCommit()">log in</div>
</div>
<div class="login2_footer">
<a href="#" class="login2_header fl" onclick="window.location.href='system/system_goCutAvatar.do'">How it works?</a>
<span class="fr">contact us: <a href="#">hello@tekuma.io</a></span>
</div>
</div>

</body>
</html>
