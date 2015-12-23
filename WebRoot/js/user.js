function user_updateInfo(){
	var first_name=$("#index_user_First_Name").val();
	var last_name=$("#index_user_last_Name").val();
	var location=$("#index_user_Year_Location").val();
	//var website=$("#index_user_Website").val();
	var bio=$("#index_user_textarea").val();
	if(first_name==undefined||first_name==""){
		public_err_prompt("Last name can not be empty",$("#index_user_First_Name"));
	}else if(last_name==undefined||last_name==""){
		public_err_prompt("Name can not be empty",$("#index_user_last_Name"));
	}else if(first_name.length>64){
		public_err_prompt("First name Can't be better than 64 characters",$("#index_user_First_Name"));
	}else if(last_name.length>64){
		public_err_prompt("Last name Can't be better than 64 characters",$("#index_user_last_Name"));
	}else if(location.length>50){
		public_err_prompt("Location Can't be better than 50 characters",$("#index_user_Year_Location"));
	}/*else if(website.length>100){
		public_err_prompt("Website Can't be better than 100 characters",$("#index_user_Website"));
	}*/else if(bio.length>1000){
		public_err_prompt("Bio Can't be better than 100 characters",$("#index_user_textarea"));
	}else{
		$.ajax({
			url:"system/system_updateUserInfo.do",
			data:{"member.lastname":last_name,"member.firstname":first_name,
				"member.location":location,"member.bio":bio,"member.gender":user_checkedGender,
				"headimg":$("#imgid_user_pic").attr("src")},
			type:"post",
			dataType:"json",
			success:function(member){
				if(member.isSuccess=="fail"){
					public_err_prompt("Save avatar failure",$("index_user_picture"));
				}else{
					collection_initUserHeadPic(fileServerUrl+member.avatarpath);
					$("#divid_collection_username").html(member.firstname+" "+member.lastname);
					$("#index_user").slideUp(300);//关闭弹框
					window.location.reload();
				}
			}
		});
	}
}


function Imageinitialization(){
	$.imageFileVisible({wrapSelector: "#index_user_picture",   
		fileSelector: "#index_user_picchange",
		id:"imgid_user_pic"
	},function(){});
	
	$(".userinfo_form_warning").click(function(){
		$(".userinfo_form_warning").css("border","solid 1px #929597");
	});
}

var user_checkedGender="";
function user_selectGender(obj,selected){
	user_checkedGender=selected;
	//alert($(obj).attr('class'));
	$("#gender div").removeClass("bbb");
	$(obj).addClass("bbb");
}