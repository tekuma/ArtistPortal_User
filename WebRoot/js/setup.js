//修改密码
function set_updateUserPwd(){
	var login_pwd=$("#index_setup_Old_passeord").val();
	var login_newPwd=$("#index_setup_New_passeord").val();
	var login_newAginPwd=$("#index_setup_New_passeord2").val();
	if(login_pwd==undefined||login_pwd==""){
		public_err_prompt("Password can not be empty",$("#index_setup_Old_passeord"));
	}else if(login_newPwd==undefined||login_newPwd==""){
		public_err_prompt("The new password can not be empty",$("#index_setup_New_passeord"));
	}else if(login_newPwd.length<6){
		public_err_prompt("Must be 6 characters or more",$("#index_setup_New_passeord"));
	}else if(login_newAginPwd==undefined||login_newAginPwd==""){
		public_err_prompt("Repeat password can not be empty",$("#index_setup_New_passeord2"));
	}else if(login_newAginPwd!=login_newPwd){
		public_err_prompt("Two times the password is not consistent",$("#index_setup_New_passeord2"));
	}else{
		$.ajax({
			url:"system/system_saveSetup.do",
			data:{"oldpwd":login_pwd,"newpwd":login_newPwd},
			type:"post",
			success:function(data){
				if(data=="1"){
					public_err_prompt("Old password error",$("#index_setup_Old_passeord"));
				}else{
					$("#index_setup").slideUp(300);
				}
			}
		});
	}
}

//密码框点击改变样式
$(function(){
	$(".setup_form_warning").click(function(){
		$(".setup_form_warning").css("border","solid 1px #929597");
	});
})


//保存支付宝信息
function addAccount(){
	var account=$("#index_setup_zhifubao").val();
	if(account==undefined||account==""){
		public_err_prompt("account can not be empty.",$("#index_setup_save"));
	}else{
		$.ajax({
			url:"system/system_saveAccount.do",
			data:{"account":account},
			type:"post",
			success:function(data){
				if(data==false){
					public_err_prompt("Account already exists.",$("#index_setup_save"));
				}else{
					$("#index_setup").slideUp(300);
					$("#index_setup_zhifubao").val(account);
				}
			}
		});
	}
}

