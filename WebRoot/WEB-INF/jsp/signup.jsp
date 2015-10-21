<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="../js/jquery-1.11.2.min.js"></script>
<link href="../css/signup2.css" rel="stylesheet" type="text/css"/>

<style>
#error{position:absolute;top:50%; left:50%; margin-left:-80px;margin-top:-20px;}
#errormsg{font-size:12px; display:none;color:red}
</style>

<script>
	function reset_resetCommit(){
		var signupemail=$("#input_signup_email").val();
		var signuppassword=$("#input_signup_password").val();
		
		var singupInvitationcode=$("#input_singup_Invitationcode").val();
		var singupcomfirmpwd=$("#input_singup_comfirmpwd").val();
		var singupfirstname=$("#input_singup_firstname").val();
		var singuplastname=$("#input_singup_lastname").val();
		
		//验证邀请码
		if(singupInvitationcode==""||singupInvitationcode==undefined){
			public_err_prompt("Invitation code cannot be empty.",$("#input_singup_Invitationcode"));
			return false;
		}else{
			$.ajax({
				url:"system/system_registCode.do",
				type:"post",
				data:{"singupInvitationcode":singupInvitationcode},
				success:function(codedate){
					if(codedate==null||codedate==""||codedate==undefined){
						public_err_prompt("The invitation code you entered is incorrect.",$("#input_singup_Invitationcode"));
						return false;
					}
				}
			});
		}
		//判断二次密码是否输入正确
		if(singupcomfirmpwd==""||singupcomfirmpwd==undefined){
			public_err_prompt("password cannot be empty.",$("#input_singup_comfirmpwd"));
			return false;
		}else{
			if(singupcomfirmpwd!=signuppassword){
				public_err_prompt("Two password input is not the same.",$("#input_singup_comfirmpwd"));
				return false;
			}
		}
		//名不能为空
		if(singupfirstname==""||singupfirstname==undefined){
			public_err_prompt("firstname cannot be empty.",$("#input_singup_firstname"));
			return false;
		}
		//字不能为空
		if(singuplastname==""||singuplastname==undefined){
			public_err_prompt("lastname cannot be empty.",$("#input_singup_lastname"));
			return false;
		}
		//用户注册
		$.ajax({
			url:"system/system_saveMemberInfo.do",
			type:"post",
			data:{"member.loginname":signupemail,"member.loginpwd":singupcomfirmpwd,"member.lastname":singuplastname,
					"member.firstname":singupfirstname},
			success:function(isSuccess){
				if(isSuccess=="success"){
					window.location.href="system_goLogin.do";
				}else{
					public_err_prompt(isSuccess,$("#inputid_regist_email"));
					window.setTimeout("location.href='http://127.0.0.1:8080/TekumaUserServer'",3000);
				}
			}
		});
	}


	
	function public_err_prompt(nr,obj){
		obj.css("border","solid 1px rgb(242, 76, 76)");
		$("#errormsg").html(nr).fadeIn(300).fadeOut(7000);
	}

</script>
<script>
$(function(){
	$(".signup2_input").fadeToggle(1400)
		
	})
</script>

</head>
<body>
<input type="hidden" id="input_signup_email" value="${email}">
<input type="hidden" id="input_signup_password" value="${password}">

<!-- 错误提示 -->
<div class="signup2_container">
<img id="signup2_logo" src="../images/wire-framing_20.png" alt="">
<div class="signup2_input">
<div id="error"><div id="errormsg">error!</div></div>
<div class="clear"></div>
<input type="text" placeholder="Invitation code" class="signup2_Invitation" id="input_singup_Invitationcode">
<input type="password" placeholder="confirm your password" class="signup2_pwd" id="input_singup_comfirmpwd">
<input type="text" placeholder="first name" class="sign2_firstname" id="input_singup_firstname">
<input type="text" placeholder="last name" class="sign2_lastname" id="input_singup_lastname">
<div class="clear"></div>
<div class="signup2_or"><span class="fl"></span><font></font><span class="fr"></span></div>
<div class="signup2_signup2" onclick="reset_resetCommit()">Submit</div>
<span class="sign2_t">signing up means you agree with TEKUMA's<a href="#">Term Of Use</a></span>

</div>
<div class="signup2_footer">
<a href="#" class="signup2_header fl" onclick="window.location.href='system/system_goCutAvatar.do'">How it works?</a>
<span class="fr">contact us: <a href="#">hello@tekuma.io</a></span>
</div>
</div>
</body>
</html>