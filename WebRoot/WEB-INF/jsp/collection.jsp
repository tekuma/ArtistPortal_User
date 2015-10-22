<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>your collection</title>
<link href="../css/collection.css" rel="stylesheet" type="text/css"/>
<script src="../js/jquery-1.11.2.min.js"></script>
<script src="../js/html5.js"></script>
<script src="../js/image-file-visible.js"></script>
<script src="../js/ajaxfileupload.js"></script>
<script src="../js/pool.js"></script>
<script src="../js/user.js"></script>
<script src="../js/setup.js"></script>

<script src="../js/dropzone.js"></script>

<!-- 固定导航栏 -->
<script type="text/javascript" src="../plugins/bootstrap/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="../plugins/bootstrap/css/bootstrap.min.css"/>
<link rel="stylesheet" href="../plugins/bootstrap-theme.css"/>
<link rel="stylesheet" href="../css/loaders.css"/>



<style type="">
.errormsg{position:fixed;width:200px;top:50%;left:50%; margin-top:-195px; font-size:12px;margin-left:260px;color:red}
.errormsg_setup{position:fixed; top:310px;; left:50%;width:200px; margin-left:335px; margin-top:20px; font-size:12px; display:none;}
.errormsg_user{position:fixed;width:200px;top:360px;left:58.5%; margin-top:-195px;margin-left:260px;font-size:12px;}
</style>
<script>
var collection_uploadWork_timestamp=[];//上传作品时的时间戳集合
$(function(){
	$.ajax({
		url:"system/system_getServerFileUrl.do",
		type:"post",
		success:function(fileUrl){
			fileServerUrl=fileUrl;
			$.ajax({
				url:"system/system_getServerusertxlj.do",
				type:"post",
				success:function(Listset){
					for(var i in Listset){
						$("#divid_collection_username").html(Listset[i].firstname+" "+Listset[i].lastname);
						$("#index_user_First_Name").val(Listset[i].firstname);
						$("#index_user_last_Name").val(Listset[i].lastname);
						if(Listset[i].gender!=""&&Listset[i].gender!=undefined){
							if(Listset[i].gender=="1"){
								$("#gender div").removeClass("bbb");
								$("#male_div").addClass("bbb");
							}else if(Listset[i].gender=="2"){
								$("#gender div").removeClass("bbb");
								$("#female_div").addClass("bbb");
							}else{
								$("#gender div").removeClass("bbb");
								$("#custom_div").addClass("bbb");
							}
						}
						collection_initUserHeadPic(fileServerUrl+Listset[i].avatarpath);
					}
				}
			});
		}
	 }); 
	
	$("#nine_picture_close").click(function(){
		  $("#nine_picture_t_window_zuopin").fadeOut(300);
		  $("#nine_picture_t_bj_zuopin").fadeOut(300)
	})
	
	$("#close1").click(function(){
		  $("#divid_collection_bj1").fadeOut(300);
		  $("#divid_collection_window1").fadeOut(300);
	});
	
	$("#ul1 a").click(function(){
		var i=$(this).index("#ul1 a");
		$(".xf:eq("+i+")").fadeIn().siblings().hide();
	});
	
	
	collection_iniCollection();//加载收藏夹
	//加载用户修改信息弹框
	$("#imgid_collection_head").click(function(){
		Imageinitialization();
		$("#index_setup").fadeOut(300)
		$("#index_user_m").fadeIn(300)
		$("#index_user").fadeIn(300);
	});
	
	$("#index_user_close").click(function(){
		$("#index_user").fadeOut(300);
		$("#index_user_m").fadeOut(300)
	});
	
	//设置
	$("#imgid_collection_setup").click(function(){
		$("#index_user").fadeOut(300);
		$("#index_setup_m").fadeIn(300)
		$("#index_setup").fadeIn(300)
	})
		
	$("#index_setup_close").click(function(){
		$("#index_setup").fadeOut(300)
		$("#index_setup_m").fadeOut(300);
	});
	
	$(".collection_update_warning").click(function(){
		$(".collection_update_warning").css("border","solid 1px #929597");
	});
	$(".pool_add_warning").click(function(){
		$(".pool_add_warning").css("border","solid 1px #929597");
	});
	
	
	//页面加载的时候判断是客户端还是pc端，同时设置pool和collection区域的宽度
	var system = { 
            win: false, 
            mac: false, 
            xll: false, 
            ipad:false 
        }; 
        //检测平台 
        var p = navigator.platform; 
        system.win = p.indexOf("Win") == 0; 
        system.mac = p.indexOf("Mac") == 0; 
        system.x11 = (p == "X11") || (p.indexOf("Linux") == 0); 
        system.ipad = (navigator.userAgent.match(/iPad/i) != null)?true:false; 
        //跳转语句，如果是手机访问就自动跳转到wap.baidu.com页面 
        if (system.win || system.mac || system.xll||system.ipad) { 
 			//alert("computer");
 			var k;
        	if (window.innerWidth){
    			k = window.innerWidth;
    		}else if((document.body) && (document.body.clientWidth)){
    			k = document.body.clientWidth; 
    		}
 			//alert(k);
 			if(k<=1130){
 				//alert(k);
 				$("#divid_collection_k").width(k);
 				$("#divid_collection_items").width(k);
 				$(".collection_header").width(k);
 			}
        } else { 
        	//alert("Mobile");
			var winWidth=window.screen.width;
			var wdth=230;
			var w;
			for(var i=1;i<20;i++){
			w=wdth*i;
			if(w!="0"&&w>winWidth){
				//alert(winWidth);
				//alert(w);
				//pool
				$("#divid_collection_k").width(w);
				//coll
				$("#divid_collection_items").width(w);
				//header
				$(".collection_header").width(w);
				return false;
				}
			}
        } 
        
        
        //获取页面所有input并添加样式
        $("input").each(function(){
            //var value = $(this).val(); 
            //失去焦点
            $(this).blur(function(event) {
            	$(this).css("border-color","#e9e9e9");
 			});
            //得到焦点
            $(this).click(function(event) {
            	$(this).css("border-color","#ec008b");
 			});
        });
      //获取页面所有textarea并添加样式
        $("textarea").each(function(){
            //var value = $(this).val(); 
            //失去焦点
            $(this).blur(function(event) {
            	$(this).css("border-color","#e9e9e9");
 			});
            //得到焦点
            $(this).click(function(event) {
            	$(this).css("border-color","#ec008b");
 			});
        });
        
});

//下拉到底部加载更多
$(document).ready(function(){  
    var range = 10;             //距下边界长度/单位px  
    var elemt = 500;           //插入元素高度/单位px  
    var maxnum = 200;            //设置加载最多次数  
    var totalheight = 0;   
    var main = $("#divid_collection_initwait");//主体元素  
    $(window).scroll(function(){  
        var srollPos = $(window).scrollTop();    //滚动条距顶部距离(页面超出窗口的高度)  
        totalheight = parseFloat($(window).height()) + parseFloat(srollPos);  
        if(($(document).height()-range) <= totalheight  && collection_checkPool_checkCollection=="pool") { 
        	$("#img_butterfly").attr("src","../images/jz.gif");
        	setTimeout("collection_initPoolNextPage()",3000);
        	setTimeout("poolajimg()",3000);
        }   
    });  
});
//图片加载出来关闭滚动图
function poolajimg(){
	var arr = document.getElementsByTagName("img");
	for( var num=0;num<arr.length;num++){
		if(arr[num].id=="poolajimgload"){
			var arr = document.getElementsByTagName("div");
			for( var num=0;num<arr.length;num++){
				if(arr[num].id=="pool_ajload"){
					arr[num].style.display = "none"; 
				}
			}
		}
	}
}

//初始化用户头像
function collection_initUserHeadPic(headUrl){
	if(headUrl.indexOf("head") > 0){
		//$("#imgid_collection_headPic").attr("background-image",headUrl);
		$("#imgid_collection_headPic").attr("style","background:url('"+headUrl+"') center center no-repeat;"); 
		$("#imgid_user_pic").attr("src",headUrl);
	}else{
		$("#imgid_collection_headPic").attr("style","background:url('../images/profile-icon.png') center center no-repeat;background-size:100% 100%;"); 
		$("#imgid_user_pic").attr("src","../images/profile-icon.png");
	}
}

//加载pool图片
function collection_initPoolNextPage(){
	$.ajax({
		url:"system/system_ajaxInitPool.do",
		data:{'page':$("#inputid_collection_page").val()},
		type:"post",
		success:function(pools){
			if(pools!=null&&pools!=""&&pools!=undefined)
			{
				//$("#divid_collection_waitpic").show();
				var html="";
				/* for(var i in pools){
					html+="<div class=\"z1 fl\" id=\"divid_collection_pool_"+pools[i].id+"\">"+
					"<div class=\"zz1\"><div id=\"zq1\"><img id=\"poolimg_"+pools[i].id+"\" class=\"imgclass_collection_works\""+
					"src=\""+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"\" /><div id=\"pool_m\" onclick=\"collection_checkworks('"+pools[i].id+"','"+fileServerUrl+pools[i].storeaddress.replace(".","ac.")+"','"+pools[i].title+"','"+pools[i].entrylabel+"','"+pools[i].description+"','"+fileServerUrl+pools[i].storeaddress+"')\"></div></div><span id=\"idspan_collection_poolTitle_"+pools[i].id+"\"> </span><span>"+pools[i].title+ 
					"<a href=\"javascript:void(0)\"><img src=\"../images/lajitong.png\" class=\"imgclass_collection_lajitong\"><div class=\"divclass_collection_cjn\"><button class=\"buttonclass_collection_delButton\" onclick=\"collection_delPoolById("+pools[i].id+")\">Delete</button></div></a></span></div></div>";
				}
				*/
				for(var i in pools){
					html+="<div class=\"z1 fl\" id=\"divid_collection_pool_"+pools[i].id+"\">"+
					"<div class=\"zz1\"><div id=\"zq1\"><img id=\"poolajimgload\" class=\"imgclass_collection_works\""+
					"src=\""+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"\" /><div id=\"pool_m\" onclick=\"collection_checkworks('"+pools[i].id+"','"+fileServerUrl+pools[i].storeaddress.replace(".","ac.")+"','"+pools[i].title+"','"+pools[i].entrylabel+"','"+pools[i].description+"','"+fileServerUrl+pools[i].storeaddress+"')\"></div>"+
					"<div class=\"loader-inner line-spin-fade-loader\" id=\"pool_ajload\"><div style=\"background:#e9e9e9;\"></div><div style=\"background:#e9e9e9;\"></div>"+
					"<div style=\"background:#e9e9e9;\"></div><div style=\"background:#e9e9e9;\"></div><div style=\"background:#e9e9e9;\"></div>"+
					"<div style=\"background:#e9e9e9;\"></div><div style=\"background:#e9e9e9;\"></div><div style=\"background:#e9e9e9;\"></div></div> "+
					"</div><span id=\"idspan_collection_poolTitle_"+pools[i].id+"\"> </span><span><span class=\"coll_ajspan_title\">"+pools[i].title+ 
					"</span><a href=\"javascript:void(0)\"><img src=\"../images/lajitong.png\" class=\"imgclass_collection_lajitong\"><div class=\"divclass_collection_cjn\"><button class=\"buttonclass_collection_delButton\" onclick=\"collection_delPoolById("+pools[i].id+")\">Delete</button></div></a></span></div></div>";
				}
				//$("#divid_collection_waitpic").hide();
				$("#img_butterfly").attr("src","../images/sw_03.jpg");
				$("#add_photo").before(html);
				$("#inputid_collection_page").val(parseInt($("#inputid_collection_page").val())+1);
				//加载鼠标移入事件
				$(".imgclass_collection_lajitong").click(function(){
				   	 $(this).next().css("left","107px");
				});
			    $(".divclass_collection_cjn").mouseout(function(){
			   	 $(this).css("left","210px");
			    });
			  /*   var arr = document.getElementsByTagName("div");
				for( var num=0;num<arr.length;num++){
					if(arr[num].id=="pool_ajload"){
						arr[num].style.display = "none"; 
					}
				} */
			}else{
				$("#img_butterfly").attr("src","../images/sw_03.jpg");
				$("#img_clear1").html("There is no picture you can browse.").fadeIn(800).fadeOut(4000);
			}
		},
		/* beforeSend:function(){
			$("#divid_collection_waitpic").show();
		} */
	});
}
//参数说明：str表示原字符串变量，flg表示要插入的字符串，sn表示要插入的位置
function insert_flg(str,flg,sn){
    var newstr="";
    for(var i=0;i<str.length;i+=sn){
        var tmp=str.substring(i, i+sn);
        newstr+=tmp+flg;
    }
    return newstr;
}

//切换pool/collection
function collection_checkPoolAndCollection(checked){
	var classnamesa=document.getElementById("sa").className;
	var classnamesb=document.getElementById("sb").className;
	if(checked=="pool" && classnamesa=="scool"){
		$("#sa").attr('class','');
		$("#sb").attr('class','');
		$("#sa").attr("class","spool");
		$("#sb").attr("class","scool");
		$("#spool").attr("class","spool2");
		$("#scool").attr("class","scool2");
	}
	if(checked=="collection" && classnamesb=="scool"){
		$("#sa").attr('class','');
		$("#sb").attr('class','');
		$("#sa").attr("class","scool");
		$("#sb").attr("class","spool");
		$("#spool").attr("class","scool2");
		$("#scool").attr("class","spool2");
	}
	collection_checkPool_checkCollection=checked;
}


//点击弹框以外部分关闭弹框
	//关闭查看pool作品弹框
	function window2_hide(){
		$("#divid_collection_window2").fadeOut(300);
		$("#divid_collection_bj2").fadeOut(300);
	}
	//关闭查看Collection收藏夹弹框
	function zuopin_hide(){
		$("#nine_picture_t_window_zuopin").fadeOut(300);
		$("#nine_picture_t_bj_zuopin").fadeOut(300);
		$(".collection2_window2").fadeOut(300);
	}
	//关闭个人信息
	function box_hide_user(){
		$("#index_user_m").fadeOut(300);
		$("#index_user").fadeOut(300);
	}
	//关闭设置弹框
	function box_hide_setup(){
		$("#index_setup_m").fadeOut(300);
		$("#index_setup").fadeOut(300);
	}
	
	//点击键盘Esc按钮关闭弹框
	$(document).keyup(function(event){
		 switch(event.keyCode) {
			 case 27:
				$("#divid_collection_window2").fadeOut(300);
				$("#divid_collection_bj2").fadeOut(300);
				$("#nine_picture_t_window_zuopin").fadeOut(300);
				$("#nine_picture_t_bj_zuopin").fadeOut(300);
				$(".collection2_window2").fadeOut(300);
				$("#index_user_m").fadeOut(300);
				$("#index_user").fadeOut(300);
				$("#index_setup_m").fadeOut(300);
				$("#index_setup").fadeOut(300);
			 case 96:
				$("#divid_collection_window2").fadeOut(300);
				$("#divid_collection_bj2").fadeOut(300);
				$("#nine_picture_t_window_zuopin").fadeOut(300);
				$("#nine_picture_t_bj_zuopin").fadeOut(300);
				$(".collection2_window2").fadeOut(300);
				$("#index_user_m").fadeOut(300);
				$("#index_user").fadeOut(300);
				$("#index_setup_m").fadeOut(300);
				$("#index_setup").fadeOut(300);
		 }
	});	
	
	
	
	//显示固定导航栏
	$(window).scroll(function (){
		var st = $(this).scrollTop();
		if(st>($("#test").height())+10){
			var temp=$("#divid_collection_window2").is(":hidden");//是否隐藏
			var temp2=$("#nine_picture_t_window_zuopin").is(":hidden");//是否隐藏
			var temp3=$("#index_user_m").is(":hidden");//是否隐藏
			var temp4=$("#index_setup_m").is(":hidden");//是否隐藏
			if(temp==true&&temp2==true&&temp3==true&&temp4==true){
				$("#cool_navigation").fadeIn();
			}else{
				$("#cool_navigation").hide();
			}
		}else{
			$("#cool_navigation").hide();
		}
		//打印日志
		//console.log(st);
	});
	
	//切换固定导航栏
	function collection_checknavigation(checked){
		if(checked=="pool"){
			$("#spool").attr("class","spool2");
			$("#scool").attr("class","scool2");
			sa.click();
		}else{
			$("#spool").attr("class","scool2");
			$("#scool").attr("class","spool2");
			sb.click();
		}
	}
	
	
	//作品和collection动态居中
	function adaptivepoll(){
		var winWidth = 0; //浏览器的宽度
		var wdth=230; //单个图片的宽度
		var w;
		if (window.innerWidth){
			winWidth = window.innerWidth;
		}else if((document.body) && (document.body.clientWidth)){
			winWidth = document.body.clientWidth; 
		}
		for(var i=1;i<20;i++){
			w=wdth*i;
			if(w!="0"&&w<=winWidth){
				//alert(winWidth);
				//alert(winWidth+wdth);
				//alert(winWidth+"浏览器的宽度");
				//alert(w+"单个图片的宽度");
				//console.log(w);
				//pool
				$("#divid_collection_k").width(w);
				//coll
				$("#divid_collection_items").width(w);
				//header
				$(".collection_header").width(w);
				//return false;
			}
		}
	}
	adaptivepoll(); 
	//调用函数，获取数值 
	window.onresize=adaptivepoll;
 

</script>

</head>
<body id="body" style="overflow-x:hidden;">

	<div id="cool_navigation" style="display: none;">
		<nav class="navbar navbar-fixed-top" role="navigation">
		<div class="container" id="container">
			<%-- <span id="name">${member.firstname}&nbsp;${member.lastname}</span> --%>
			<ul id="ul2">
				<li><a href="javascript:void(0)" onclick="collection_checknavigation('pool')" id="spool"
					class="spool2">All Your Work</a></li>
				<li><a href="javascript:void(0)" onclick="collection_checknavigation('collection')"
					id="scool" class="scool2">Your Collection</a></li>
			</ul>
		</div>
		</nav>
	</div>


<div id="test">
<div class="collection_header">
<a href="http://tekuma.io/" class="collection_logo_c"><img id="collection_logo"  src="../images/jgugj_03.jpg" alt="" /></a> 
<a class="collection_startup"><img src="../images/04_add-artworks-into-your-pool_05.jpg" alt="" id="imgid_collection_setup"/></a>
<a class="collection_user_p"><img src="../images/04_add-artworks-into-your-pool_07.jpg" alt="" id="imgid_collection_head"/></a> 
</div>
<div class="collection_user" id="imgid_collection_headPic">
<!-- <img src="" id="imgid_collection_headPic"/> -->
</div>
<div class="collection_user_name" id="divid_collection_username"><%-- ${member.firstname}&nbsp;${member.lastname} --%></div>
<!--特效开始-->
<ul id="ul1">
<li><a href="javascript:void(0)" onclick="collection_checkPoolAndCollection('pool')" id="sa"  class="spool">All Your Work</a></li>
<li id="fg"></li>
<li><a href="javascript:void(0)" onclick="collection_checkPoolAndCollection('collection')" id="sb" class="scool">Your Collection</a></li>
</ul>
</div>


<div class="clear qa"></div>
<div class="collection_allyourwork">
<div class="xf  dropzone" id="dis">
<script>
$(".dropzone").dropzone({
        url: "system/system_savePoolWorks.do",
        paramName:"upload",
        params:{"pool.title":""},
        acceptedFiles: "image/*",
        previewsContainer:"",
        dictInvalidFileType: "You can't upload the type file, the file type can only be image",
        init: function() {
            this.on("success", function(file,respone) {
                var pool=eval(eval('(' + respone + ')'));
                var timestamp=collection_uploadWork_timestamp[0];
                $("#z1_"+timestamp).attr("id","divid_collection_pool_"+pool.id);
                $("#img_"+timestamp).attr("src",fileServerUrl+pool.storeaddress.replace(".","tb."))
                .attr("onclick","collection_checkworks('"+pool.id+"',"+
            			"'"+fileServerUrl+pool.storeaddress.replace(".","ac.")+"','"+pool.title+"',"+
            			"'"+pool.entrylabel+"','"+pool.desc+"','"+fileServerUrl+pool.storeaddress+"')");
                $("#span_"+timestamp).append(pool.title);
                var html="<div class=\"divclass_collection_cjn\"><button class=\"buttonclass_collection_delButton\" onclick=\"collection_delPoolById('"+pool.id+"')\">Delete</button></div>";
                $("#ljt_"+timestamp).attr("class","imgclass_collection_lajitong").after(html);
                $("#collection_uploadwork_progress_"+collection_uploadWork_timestamp[0]).remove();
                collection_uploadWork_timestamp.splice(0,1);
              	//加载鼠标移入事件
    			$(".imgclass_collection_lajitong").click(function(){
    			   	 $(this).next().css("left","107px");
    			});
    		    $(".divclass_collection_cjn").mouseout(function(){
    		   	 	 $(this).css("left","210px");
    		    });
    		    window.location.reload();
            });
            this.on("removedfile", function(file) {
            });
            this.on("thumbnail",function(file, dataUrl) {
            	var timestamp=new Date().getTime();
            	collection_uploadWork_timestamp.push(timestamp);
            	var html="<div class=\"z1 fl\" id=\"z1_"+timestamp+"\">"+
    			"<div class=\"zz1\"><div id=\"zq1\"><img class=\"imgclass_collection_works\""+
    			"src=\""+dataUrl+"\" id=\"img_"+timestamp+"\"/><div id=\"pool_m\" onclick=\"document.getElementById('img_"+timestamp+"').click();\"></div></div>"+
    			"<span id=\"span_"+timestamp+"\"><a href=\"javascript:void(0)\">"+
    			"<progress id=\"collection_uploadwork_progress_"+timestamp+"\" style=\"border-radius:5px;\" max=\"100\" value=\"0\"><ie style=\"width:20%;\"></ie></progress><img src=\"../images/lajitong.png\" alt=\"\" id=\"ljt_"+timestamp+"\"/></a></span></div></div>";
    			$("#pool_add").after(html);
            });
            this.on("addedfile", function (file) {  
            	
            }); 
            this.on("uploadprogress", function (q,w,e) {  
            	$("#collection_uploadwork_progress_"+collection_uploadWork_timestamp[0]).width(w);
            });
            this.on("canceled",function(){
            	collection_uploadWork_timestamp.splice(0,1);
            });
            this.on("error", function(file) {
            });
            //用户有东西掉到DropZone
            this.on("drop", function(file) {
            	$("#collection_tuozhuai_mengban").show();
            	$("#collection_tuozhuai_mengban").delay(300).hide(0);
            }); 
    
        }
    }); 
</script>
<!--初始化界面开始-->
<div class="k" id="divid_collection_k" >
<!--放置添加按钮处-->
<div id="pool_add"  onclick="collection_addPool()"><input type="file" id="pool_add_position" name="upload" accept="image/*"/><img src="../images/defr_06.jpg"/>Upload Your Work</div>
<!--放置添加按钮处-->
<s:iterator id="works" value="pools" status="colla">
<div class="z1 fl" id="divid_collection_pool_<s:property value="#works.id"/>" >
<div class="zz1">
<div id="zq1">
<%-- <s:property value="#colla.index+1"/> --%>
<img class="imgclass_collection_works" id="poolimg_${works.id}"
src="<s:property value="#works.thumbnailurl"/>"/>
<div id="pool_m" onclick="collection_checkworks('<s:property value="#works.id"/>','<s:property value="#works.thumbnailurlac"/>','<s:property value="#works.title"/>','<s:property value="#works.entrylabel"/>','<s:property value="#works.description"/>','<s:property value="#works.storeaddress"/>')"></div>
<!-- 图片未加载出来的等待 -->
<div class="loader-inner line-spin-fade-loader" id="pool_load_${works.id}">
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
	<div style="background:#e9e9e9;"></div>
</div>          
<script type="text/javascript">
//图片加载出来关闭滚动图
poolimg_${works.id}.onload = function() {
	document.getElementById("pool_load_"+${works.id}).style.display="none";
}
setTimeout(function(){document.getElementById("pool_load_"+${works.id}).style.display="none";},2000);
</script>                                                                 
</div>
<span id="idspan_collection_poolTitle_<s:property value="#works.id" />" class="coll_span_title"><s:property value="#works.title" /></span>
<span id="idspan_collection_poolTitl_<s:property value="#works.id"/>">
<a href="javascript:void(0)">
<!-- 删除特效 -->
<img src="../images/lajitong.png" class="imgclass_collection_lajitong"/>
<div class="divclass_collection_cjn">
    <button class="buttonclass_collection_delButton" onclick="collection_delPoolById(<s:property value="#works.id"/>)">Delete</button>
</div>
</a>
<!-- 删除特效 -->
<a href="javascript:void(0)"></a>
</span>
</div>
</div>
</s:iterator>
<!--下拉加载图片处-->
<div id="add_photo"/></div>
<!--放置添加按钮处-->
</div>
</div>
<!-- 收藏夹 -->
<div id="sp" class="xf">
<div class="content1" id="divid_collection_items">
		
</div>
<div class="clear1"></div>
</div>

</div>
<!--特效结束-->
<!--小蝴蝶-->
<div class="clear1"></div>
<div id="collection_butterfly"><img src="../images/sw_03.jpg" id="img_butterfly" /></div>
<div class="clear1" id="img_clear1" style="text-align: center;"></div>
<!--小蝴蝶-->
<div class="collection_contact">
<span>contact us: <a href="javascript:void(0)">hello@tekuma.io</a></span>
</div>
<!--收藏夹修改-->
<div class="bj2" id="divid_collection_bj1"></div>
<div class="window2" id="divid_collection_window1">
<div class="left_box fl">
<ul class="creat_information_ul" id="ulid_collection_addcollection">
<!-- creat_information_add -->
<li><a href="#"><input type="file"></a></li>
<!-- creat_information_add -->
</ul>
</div>
<div class="creat_information_right_box fr">
<!-- 错误提示 -->
<div class="errormsg"></div>
<a href="javascript:void(0)" id="close1"><img src="../images/wire-framing_03.png" alt="" /></a>
<h1 class="creat_information_h1">Collection Description</h1>
<input type="text" placeholder="Collection Title" id="inputid_collection_cname" class="collection_update_warning" value=" "/> 
<!-- <input type="text" placeholder="Number of Prints" id="inputid_collection_cLimitNum" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')" class="collection_update_warning" value=" "/> -->
<%-- <select id="inputid_Upload_ptime">==Upload time==
<option value="2015">2015</option>
<option value="2014">2014</option>
<option value="2013">2013</option>
<option value="2012">2012</option>
</select> --%>
<!-- 标签四 -->
<!--<select id="inputid_collection_colors" class="collection_update_warning">
<option value="">Tags</option>
<option value="red">red</option>
<option value="yellow">yellow</option>
<option value="blue">blue</option>
<option value="green">green</option>
<option value="other">other</option>
</select>-->
<input type="text" id="inputid_collection_tags">
<select id="inputid_collection_categories" class="collection_update_warning">
<option value="">All Media</option>
<option value="Collage">Collage</option>
<option value="Drawing">Drawing</option>
<option value="Installation">Installation</option>
<option value="New Media">New Media</option>
<option value="Painting">Painting</option>
<option value="Photography">Photography</option>
<option value="Printmaking">Printmaking</option>
<option value="Sculpture">Sculpture</option>
<option value="Video">Video</option>
<option value="other">other</option>
</select>
<!--标签2 -->
<select id="inputid_collection_styles" class="collection_update_warning">
<option value="">All Styles</option>
<option value="Abstract">Abstract</option>
<option value="Abstract Expressionism">Abstract Expressionism</option>
<option value="Art Deco">Art Deco</option>
<option value="Conceptual">Conceptual</option>
<option value="Cubism">Cubism</option>
<option value="Dada">Dada</option>
<option value="Documentary">Documentary</option>
<option value="Expressionism">Expressionism</option>
<option value="Figurative">Figurative</option>
<option value="Fine Art">Fine Art</option>
<option value="Folk">Folk</option>
<option value="Illustration">Illustration</option>
<option value="Impressionism">Impressionism</option>
<option value="Minimalism">Minimalism</option>
<option value="Modern">Modern</option>
<option value="Photorealism">Photorealism</option>
<option value="Pop Art">Pop Art</option>
<option value="Portraiture">Portraiture</option>
<option value="Realism">Realism</option>
<option value="Street Art">Street Art</option>
<option value="Surrealism">Surrealism</option>
<option value="other">other</option>
</select>
<!--标签3 -->
<!--<select id="inputid_collection_prices" class="collection_update_warning">
<option value="">All Subjects</option>
<option value="Abstract">Abstract</option>
<option value="Aerial">Aerial</option>
<option value="Aeroplane">Aeroplane</option>
<option value="Airplane">Airplane</option>
<option value="Animal">Animal</option>
<option value="Architecture">Architecture</option>
<option value="Automobile">Automobile</option>
<option value="Beach">Beach</option>
<option value="Bicycle">Bicycle</option>
<option value="Bike">Bike</option>
<option value="Boat">Boat</option>
<option value="Body">Body</option>
<option value="Botanic">Botanic</option>
<option value="Business">Business</option>
<option value="Calligraphy">Calligraphy</option>
<option value="Car">Car</option>
<option value="Cartoon">Cartoon</option>
<option value="Cats">Cats</option>
<option value="Celebrity">Celebrity</option>
<option value="Children">Children</option>
<option value="Cinema">Cinema</option>
<option value="Cities">Cities</option>
<option value="Classical mythology">Classical mythology</option>
<option value="Comics">Comics</option>
<option value="Cows">Cows</option>
<option value="Cuisine">Cuisine</option>
<option value="Culture">Culture</option>
<option value="Dogs">Dogs</option>
<option value="Education">Education</option>
<option value="Erotic">Erotic</option>
<option value="Family">Family</option>
<option value="Fantasy">Fantasy</option>
<option value="Fashion">Fashion</option>
<option value="Fish">Fish</option>
<option value="Floral">Floral</option>
<option value="Food">Food</option>
<option value="Food &amp; Drink">Food &amp; Drink</option>
<option value="Garden">Garden</option>
<option value="Geometric">Geometric</option>
<option value="Graffiti">Graffiti</option>
<option value="Health &amp; Beauty">Health &amp; Beauty</option>
<option value="Home">Home</option>
<option value="Horse">Horse</option>
<option value="Humor">Humor</option>
<option value="Interiors">Interiors</option>
<option value="Kids">Kids</option>
<option value="Kitchen">Kitchen</option>
<option value="Landscape">Landscape</option>
<option value="Language">Language</option>
<option value="Light">Light</option>
<option value="Love">Love</option>
<option value="Men">Men</option>
<option value="Mortality">Mortality</option>
<option value="Motor">Motor</option>
<option value="Motorbike">Motorbike</option>
<option value="Motorcycle">Motorcycle</option>
<option value="Music">Music</option>
<option value="Nature">Nature</option>
<option value="Nude">Nude</option>
<option value="Outer Space">Outer Space</option>
<option value="Patterns">Patterns</option>
<option value="People">People</option>
<option value="Performing Arts">Performing Arts</option>
<option value="Places">Places</option>
<option value="Political">Political</option>
<option value="Politics">Politics</option>
<option value="Pop Culture/Celebrity">Pop Culture/Celebrity</option>
<option value="Popular culture">Popular culture</option>
<option value="Portrait">Portrait</option>
<option value="Religion">Religion</option>
<option value="Religious">Religious</option>
<option value="Rural life">Rural life</option>
<option value="Sailboat">Sailboat</option>
<option value="Science">Science</option>
<option value="Science/Technology">Science/Technology</option>
<option value="Seascape">Seascape</option>
<option value="Seasons">Seasons</option>
<option value="Ship">Ship</option>
<option value="Sport">Sport</option>
<option value="Sports">Sports</option>
<option value="Still Life">Still Life</option>
<option value="Technology">Technology</option>
<option value="Time">Time</option>
<option value="Train">Train</option>
<option value="Transportation">Transportation</option>
<option value="Travel">Travel</option>
<option value="Tree">Tree</option>
<option value="Typography">Typography</option>
<option value="Wall">Wall</option>
<option value="Water">Water</option>
<option value="Women">Women</option>
<option value="World Culture">World Culture</option>
<option value="Yacht">Yacht</option>
<option value="other">other</option>
</select>-->
<textarea placeholder="Write a few words on the intent of your work and what inspire you." id="creat_collection_textarea" class="collection_update_Warning"></textarea>
<a href="javascript:void(0)" id="acla_collection_back">Back</a>
<a href="javascript:void(0)" id="happy" onclick="collection_addCollectionSubmit()">Save</a>
</div>
</div>
<!-- pool添加 -->
<div class="bj2" id="divid_collection_bj2" onclick="window2_hide()"></div>
<div class="window2" id="divid_collection_window2">
<a href="#" class="pool2_l" id="pool_left" onclick="pool_flip('left')"><img src="../images/jt.png" alt=""/></a>
<div class="left_box fl" id="left_box">

<!-- 加载等待图 -->
<div class="containloader" align="center" id="bgasdasdasd"> 
	<div class="zgroup"> 
	  <div class="bigSqr">
	    <div class="square first"></div>
	    <div class="square second"></div>
	    <div class="square third"></div>
	    <div class="square fourth"></div>
	  </div>
	  <p style="font-size: 25px;margin-top: 20px;">Loading  Please wait......</p>
	</div>
</div>
<!-- 结束等待图 -->

<div class="creat_information_img" id="image-wrap">
<img  src="" alt="" id="inputid_collection_pimg" />
<%-- <script type="text/javascript">
inputid_collection_pimg.onload = function() {
	alert("");
	document.getElementById("bgasdasdasd").style.display="none";
	document.getElementById("inputid_collection_pimg").style.display="";
}
</script> --%>
</div>
</div>
<div class="creat_information_right_box fr">
<!-- 错误提示 -->
<div class="errormsg"></div>
<a href="javascript:void(0)" id="pool_close" onclick="collection_closeWorksBox()"><img src="../images/wire-framing_03.png" alt="" /></a>
<h1 class="creat_information_h1">Work Information</h1>
<input type="text" placeholder="Pool Name" id="inputid_collection_pname" class="pool_add_warning" value=" "/>
<!-- <input type="text" placeholder="label" id="inputid_collection_ptime"  class="pool_add_warning" value=" "/> -->
<select id="inputid_Upload_ptime"  class="pool_add_warning">==Create time==
<option value="2015">2015</option>
<option value="2014">2014</option>
<option value="2013">2013</option>
<option value="2012">2012</option>
<option value="other">other</option>
</select>
<!-- 作品id -->
<input type="text"  id="inputid_collection_worksid" style="display:none"/>	

<textarea placeholder="Project Description(maximum 500 characters)" id="inputid_collection_pdesc"  class="pool_add_warning"></textarea>
<a href="javascript:void(0)" id="collection_saveyourpool" onclick="collection_comitWorks()">Save</a>
<!-- 下载链接 -->
<a id="cool_download">Download</a>
</div>
<input type="hidden" id="pool_flipid" value=""/>
<a href="#" id="pool_right" class="pool2_r" onclick="pool_flip('right')"><img src="../images/jt2.png" alt=""/></a>
</div>

<!-- 浏览收藏夹 -->
<div id="nine_picture_t_bj_zuopin" onclick="zuopin_hide()"></div>
<div id="nine_picture_t_window_zuopin">
<div class="nine_picture_left_box fl">
<ul class="nine_picture_ul3" id="ulid_collection_cScanBox">
</ul>
</div>
<div class="nine_picture_right_box fr">
<a href="javascript:void(0)" id="nine_picture_close"><img src="../images/wire-framing_03.png" alt="" /></a>
<h1 class="nine_picture_h1">Collection Information</h1>
<input type="text" placeholder="Work title" id="nine_picture_Work_title">
<!-- <input type="text" placeholder="Year of creation" id="nine_picture_Year_of_creation" readOnly="true"/> -->
<input type="text" id="inputid_collection_tags">
<select id="inputid_collection_categories" class="collection_update_warning">
<option value="">All Media</option>
<option value="Collage">Collage</option>
<option value="Drawing">Drawing</option>
<option value="Installation">Installation</option>
<option value="New Media">New Media</option>
<option value="Painting">Painting</option>
<option value="Photography">Photography</option>
<option value="Printmaking">Printmaking</option>
<option value="Sculpture">Sculpture</option>
<option value="Video">Video</option>
<option value="other">other</option>
</select>
<!--标签2 -->
<select id="inputid_collection_styles" class="collection_update_warning">
<option value="">All Styles</option>
<option value="Abstract">Abstract</option>
<option value="Abstract Expressionism">Abstract Expressionism</option>
<option value="Art Deco">Art Deco</option>
<option value="Conceptual">Conceptual</option>
<option value="Cubism">Cubism</option>
<option value="Dada">Dada</option>
<option value="Documentary">Documentary</option>
<option value="Expressionism">Expressionism</option>
<option value="Figurative">Figurative</option>
<option value="Fine Art">Fine Art</option>
<option value="Folk">Folk</option>
<option value="Illustration">Illustration</option>
<option value="Impressionism">Impressionism</option>
<option value="Minimalism">Minimalism</option>
<option value="Modern">Modern</option>
<option value="Photorealism">Photorealism</option>
<option value="Pop Art">Pop Art</option>
<option value="Portraiture">Portraiture</option>
<option value="Realism">Realism</option>
<option value="Street Art">Street Art</option>
<option value="Surrealism">Surrealism</option>
<option value="other">other</option>
</select>

<textarea placeholder="Work description" id="nine_picture_textarea"></textarea>
<a href="javascript:void(0)" id="acla_collection_select">Select</a>
<a href="javascript:void(0)" id="happy" onclick="collection_addCollectionSubmit()">Save</a>
<!-- <a href="javascript:void(0)">Save Your nine_picture</a> -->
</div>


<!--collection的二次弹框 -->
<div class="collection2_window2" id="divid_collection_window2">
<a href="#" class="collection2_l" onclick="collectionleft()"><img src="../images/jt.png" alt=""/></a>
<input type="hidden" id="input_index" placeholder="最大页"/>
<input type="hidden" id="input_collection2_l" placeholder="当前页"/>
<input type="hidden" id="input_page" placeholder="总共页数"/>
<div class="collection2_left_box fl" id="left_box">
 <div class="collection2_img" id="image-wrap">
<img class="inputid_collectionpool_pimg" />
</div>
<!-- <ul id="ulid_collectionpool_cScanBox">
</ul> -->
</div>
<div class="collection2_right_box fr">
<div class="errormsg"></div>
<a href="javascript:void(0)" id="collection2_close"><img src="../images/wire-framing_03.png" alt="" /></a>
<h1 class="collection2_h1">Collection Information</h1>
<input type="text" placeholder="Pool Name" id="inputid_collectionpool_pname" class="pool_add_warning"/>
<!-- <input type="text" placeholder="label" id="inputid_collectionpool_plabel" class="pool_add_warning"/> -->
<!-- <input type="text" placeholder="Create time" id="collectionpool_inputid_Upload_ptime" class="collection_add_warning"/> -->
<select id="collectionpool_inputid_Upload_ptime"  class="collection_add_warning">==Create time==
<option value="2015">2015</option>
<option value="2014">2014</option>
<option value="2013">2013</option>
<option value="2012">2012</option>
<option value="other">other</option>
</select>
<input type="text"  id="inputid_collection_worksidec" style="display: none;" value=""/>	
<input type="text"  id="inputid_collection_coolid" style="display: none;" value=""/>	
<textarea placeholder="Project Description" id="inputid_collection_pdesc" class="pool2_add_warning"></textarea>
<a href="#" id="collsecondary_save" onclick="collection_poolcomitWorks()">Save</a>
<!-- 下载链接 -->
<a id="coolsecondary_download">Download</a>
<!--<a href="javascript:void(0)" class="last_button" onclick="collection_comitWorks()">Save Your Pool</a>-->
</div>
<a href="#" class="collection2_r" onclick="collectionright()"><img src="../images/jt2.png" alt=""/></a>
</div>
<!--collection的二次弹框结束 -->
</div>
<!-- 用户信息 -->
<div id="index_user_m" onclick="box_hide_user()"></div>
<div id="index_user" style="display:none">
<div class="index_user_right_box">
<!-- 错误提示 -->
<div class="errormsg errormsg_user"></div>
<a href="javascript:void(0)" id="index_user_close"><img src="../images/wire-framing_03.png" alt=""/></a>
<h1 class="index_user_h1">Profile Information</h1>
<input type="text" placeholder="First Name" id="index_user_First_Name" value="" class="userinfo_form_warning"/>
<input type="text" placeholder="last Name" id="index_user_last_Name" value="" class="userinfo_form_warning"/>
<div id="gender">
<div class="male" id="male_div" onclick="user_selectGender(this,'1')"></div>
<span id="male">male</span>
<div class="female" id="female_div" onclick="user_selectGender(this,'2')"></div>
<span id="female">female</span>
<div class="custom" id="custom_div" onclick="user_selectGender(this,'3')"></div>
<span id="custom">custom</span>
<div class="clear1"></div>
</div>
<div class="clear1"></div>
<div class="index_user_picture" id="index_user_picture"><img src="../images/v8_09.jpg" alt="" id="imgid_user_pic" onclick="index_user_picchange.click()"/>
</div>
<input type="file" value="Change your picture" id="index_user_picchange" />
<a href="#" id="index_user_picchange_none" onclick="index_user_picchange.click()">change your picture</a>
<div class="clear1"></div>
<input type="text" placeholder="Where are you live ?" id="index_user_Year_Location" value="${member.location }"/>
<%-- <input type="text" placeholder="Storage address" id="index_user_Website" value="${member.website }"/> --%>
<textarea placeholder="Work description" id="index_user_textarea" value="${member.bio }"></textarea>
<a href="#" id="index_user_save" onclick="user_updateInfo()">Save</a>
</div>
</div>

<!-- 设置 -->
<div id="index_setup_m" onclick="box_hide_setup()"></div>
<div id="index_setup" style="display:none">
<div class="index_setup_right_box">
<!-- 错误提示 -->
<div class="errormsg errormsg_setup"></div>
<a href="javascript:void(0)" id="index_setup_close"><img src="../images/wire-framing_03.png" alt=""/></a>
<h1 class="index_setup_h1">Account Information</h1>
<input type="text" placeholder="email_address" id="index_setup_email_address" value="${member.loginname }" readOnly="true"/>
<div id="index_setup_password">
<input type="password" placeholder="Old Password" id="index_setup_Old_passeord" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')" class="setup_form_warning"/>
<input type="password" placeholder="New Password" id="index_setup_New_passeord" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')" class="setup_form_warning"/>
<input type="password" placeholder="New Password" id="index_setup_New_passeord2" onKeyUp="value=value.replace(/[^\a-\z\A-\Z0-9]/g,'')" class="setup_form_warning"/>
<input type="button" value="Change it" id="index_setup_change_it" onclick="set_updateUserPwd()"/>
</div>
<div class="clear1"></div>
<div class="index_setup_pay">
<span class="index_setup_annotation">
<p>annotation:</p>To receice earings form your artworks,enter yourpayment information here.We current do transactions through Alipay and PayPal.
</span>
<div class="clear"></div>
<a href="#" id="index_setup_zhifubao">Alipay</a>
<a href="#" id="index_setup_paypal">PayPal</a>
</div>

<a href="#" id="index_setup_save">Save</a>
<a href="#" id="index_setup_logout" onclick="window.location.href='system_goLogin.do'">Log out</a>
<div class="clear"></div>
</div>
</div>

<!-- 加载等待 -->
<div id="divid_collection_initwait" >  
<div class="collection_footer_img" id="divid_collection_waitpic" style="display:none">
<img src="../images/jz.gif" alt="" />
</div>
</div> 

<!-- 页码 -->
<input style="display:none" value="1" id="inputid_collection_page"/>

<div class="collection_tuozhuai" id="collection_tuozhuai_mengban">Drop files to upload</div>

</body>
</html>
