var collection_curCollectionId="";//当前收藏夹Id
var collection_poolArr=[];//收藏夹作品集合
var collection_pools;//被收藏的作品集合
var collection_checkPool_checkCollection="pool";
var fileServerUrl="";

function public_err_prompt(nr,obj){
	obj.css("border","solid 1px rgb(242, 76, 76)");
	$(".errormsg").html(nr).fadeIn(300).fadeOut(3000);
}

//加载收藏列表
function collection_iniCollection(){
	$.ajax({
		url:"system/system_ajaxInitCollections.do",
		type:"post",
		success:function(pools){
			collection_pools=pools;//初始化作品集合全局变量
			collection_collectionIteration(pools);
		}
	});
}

//迭代作品集合
function collection_collectionIteration(pools){
	var html="";
	if(pools!=undefined&&pools.length>0){
		var lastCollectionId=pools[0].collectionid;
		var index=0;
		html+="<div class=\"z2\" onclick=\"collection_openAddCollection()\"><a href=\"javascript:void(0)\"><img src=\"../images/defr_06.jpg\" alt=\"\"/></a>create a collection</div>";
		html+="<div class=\"z1\" id=\"ulid_collection_collectionid_"+lastCollectionId+"\"><div class=\"zz1\"><ul  class=\"zp\" onclick=\"collection_openCollectionScan("+pools[0].collectionid+")\">";
		for(var i in pools){
			if(pools[i].collectionid==lastCollectionId&&index<4){
				index++;
				html+="<li><a href=\"javascript:void(0)\" class=\"za"+index+"\"><img src='"+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"' class=\"imgclass_collection_works\"/></a></li>";
			}else if(pools[i].collectionid!=lastCollectionId){
				index=1;
				lastCollectionId=pools[i].collectionid;
				html+="</ul><span><em>"+pools[i-1].collectiontitle+"</em><img src=\"../images/lajitong.png\"class='collection_del1 imgclass_collection_lajitong'><div class=\"divclass_collection_cjn\"><button class=\"buttonclass_collection_delButton\" onclick=\"collection_delCollectionById("+pools[i-1].collectionid+")\">Delete</button></div><a href=\"javascript:void(0)\"><img onclick=\"collection_openUpdateCollection("+pools[i-1].collectionid+")\" src=\"../images/bi.png\"/></a></span></div></div>";
				html+="<div class=\"z1 fl\" id=\"ulid_collection_collectionid_"+pools[i].collectionid+"\"><div class=\"zz1\"><ul  class=\"zp\" onclick=\"collection_openCollectionScan("+pools[i].collectionid+")\">";
				html+="<li ><a href=\"javascript:void(0)\" class=\"za1\"><img src='"+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"' class=\"imgclass_collection_works\"/></a></li>";
			}
			if(i==pools.length-1){
				html+="</ul><span><em>"+pools[i].collectiontitle+"</em><img src=\"../images/lajitong.png\" class='collection_del1 imgclass_collection_lajitong'><div class=\"divclass_collection_cjn\"><button class=\"buttonclass_collection_delButton\" onclick=\"collection_delCollectionById("+pools[i].collectionid+")\">Delete</button></div><a href=\"javascript:void(0)\"><img onclick=\"collection_openUpdateCollection("+pools[i].collectionid+")\" src=\"../images/bi.png\"/></a></span></div></div>";
			}
		}
	}else{
		html+="<div class=\"z2\" onclick=\"collection_openAddCollection()\"><a href=\"javascript:void(0)\"><img src=\"../images/defr_06.jpg\" alt=\"\"/></a>create a collection</div>";
	}
	$("#divid_collection_items").html(html);
	//加载鼠标移入事件
	$(".imgclass_collection_lajitong").click(function(){
	   	 $(this).next().css("left","107px");
	});
    $(".divclass_collection_cjn").mouseout(function(){
   	 $(this).css("left","210px");
    });
}

//添加收藏/点击收藏夹
function collection_openUpdateCollection(collectionId){
	$.ajax({
		url:"system/system_findUpdateCollectionList.do",
		type:"post",
		data:{"collectionid":collectionId},
		success:function(pools){
			collection_curCollectionId=collectionId;//初始化被选中收藏夹ID
			$("#divid_collection_bj1").css("display","block");
			$("#divid_collection_window1").css("display","block");
			var html="";
			collection_poolArr=[];//初始化数组
			var poolIds=collection_CollectionPoolIdStr(collectionId);
			for(var i in pools){
				if(poolIds.indexOf(pools[i].id)!=-1){
					collection_poolArr.push(pools[i].id);//初始化选中收藏集合
					html+="<li><a href=\"javascript:void(0)\"  onclick=\"collection_selectWorks(this,'"+pools[i].id+"')\" id=syq"+pools[i].id+"><img class=\"imgclass_collection_works\" src=\""+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"\"/></a><span class=\"on\" onclick=\"ck('syq"+pools[i].id+"')\"></span></li>";
				}else{
					html+="<li><a href=\"javascript:void(0)\"  onclick=\"collection_selectWorks(this,'"+pools[i].id+"')\" id=cyq"+pools[i].id+"><img class=\"imgclass_collection_works\" src=\""+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"\"/></a><span class=\"not\" onclick=\"ck('cyq"+pools[i].id+"')\"></span></li>";
				}
			}
			$("#ulid_collection_addcollection").html(html);
			collection_initCollection(collectionId);
		}
	});
}

//初始化修改收藏夹弹框
function collection_initCollection(collectionId){
	var collection;
	for(var i in collection_pools){
		if(collection_pools[i].collectionid==collectionId){
			$("#inputid_collection_cname").val(collection_pools[i].collectiontitle);
			$("#inputid_collection_cLimitNum").val(collection_pools[i].printquantity);
			$("#creat_collection_textarea").val(collection_pools[i].descriptionof);
			$("#inputid_collection_categories").val(collection_pools[i].categories);
			$("#inputid_collection_styles").val(collection_pools[i].styles);
			$("#inputid_collection_prices").val(collection_pools[i].subject);
			$("#inputid_collection_colors").val(collection_pools[i].color);
			return;
		}
	}
}

//初始化修改收藏夹弹框
function collection_clearCollection(){
	$("#inputid_collection_cname").val("");
	$("#inputid_collection_cLimitNum").val("");
	$("#creat_collection_textarea").val("");
	$("#inputid_collection_categories").val("");
	$("#inputid_collection_styles").val("");
	$("#inputid_collection_prices").val("");
	$("#inputid_collection_colors").val("");
}

//添加收藏/点击收藏夹(无选中收藏夹)
function collection_openAddCollection(){
	collection_curCollectionId="";//初始化被选中收藏夹ID/用于判断是save或update
	$.ajax({
		url:"system/system_findAddCollectionList.do",
		type:"post",
		data:{"page":"0"},
		success:function(pools){
			$("#divid_collection_bj1").css("display","block");
			$("#divid_collection_window1").css("display","block");
			var html="";
			collection_poolArr=[];//初始化数组
			html+="<li id=\"pool_add\" Style=\"width:137px;height:135px;font-size:.8em;box-shadow:none;border-radius:1px;margin-top:21px;\" onclick=\"coll_addPool()\"><input type=\"file\" id=\"cool_add_position\" name=\"upload\" accept=\"image/*\"/><img style='width:45px;height:45px;margin-top:20%;' src=\"../images/defr_06.jpg\" />Upload your work</li>";
			for(var i in pools){
				html+="<li><a href=\"javascript:void(0)\" onclick=\"collection_selectWorks(this,'"+pools[i].id+"')\" id=yq"+pools[i].id+"><img class=\"imgclass_collection_works\" src=\""+fileServerUrl+pools[i].storeaddress.replace(".","tb.")+"\"/></a><span class=\"not\" onclick=\"ck('yq"+pools[i].id+"')\" ></span></li>";
			}
			$("#ulid_collection_addcollection").html(html);
			collection_clearCollection()
		}
	});
}

//点击小圆球选中
function ck(id){
	var djid="yq"+id;
	$("#"+djid+" a").click();
}

//收藏夹//打开添加作品框
function coll_addPool(){
	/*$("#divid_collection_bj2").show(100);
	$("#divid_collection_window2").show(300);*/
	document.getElementById("bgasdasdasd").style.display="none";
	document.getElementById("cool_download").style.display="none";
	document.getElementById("pool_left").style.display="none";
	document.getElementById("pool_right").style.display="none";
	$.imageFileVisible({wrapSelector: "#image-wrap",   
		fileSelector: "#cool_add_position",
		id:"inputid_collection_pimg",
	},function(src){
		collection_poolSize(src);
		$("#inputid_collection_worksid").val("cool");
		$("#divid_collection_bj2").show(100);
		$("#divid_collection_window2").show(300);
	});
}



//返回收藏夹作品（pool）id数组 
function collection_CollectionPoolIdStr(collectionId){
	var poolIds=[];
	for(var i in collection_pools){
		if(collection_pools[i].collectionid==collectionId){
			poolIds.push(collection_pools[i].id);
		}
	}
	return poolIds;
}

//关闭添加收藏窗口
function collection_closeBox(){
	$(".window2").slideUp(300);
	$(".bj2").fadeOut(300)
}

//提交选中的作品到收藏夹
function collection_addCollectionSubmit(){
	var poolsStr="";
	var collectionTitle=$("#inputid_collection_cname").val();
	var cNum=$("#inputid_collection_cLimitNum").val();
	var cDesc=$("#creat_collection_textarea").val();
	
	var categories=$("#inputid_collection_categories").val();
	var styles=$("#inputid_collection_styles").val();
	var subject=$("#inputid_collection_prices").val();
	var color=$("#inputid_collection_colors").val();
	
	if(collection_poolArr==undefined||collection_poolArr.length<1){
		public_err_prompt("Choose work",$(""));
	}else if(collectionTitle==undefined||collectionTitle==""){
		public_err_prompt("Title can not be empty",$("#inputid_collection_cname"));
	}else{
		for(var i in collection_poolArr){
			poolsStr+=collection_poolArr[i]+",";
		}
		poolsStr=poolsStr.substring(0,poolsStr.length-1);
		var isAdd=collection_curCollectionId==""?"yes":"no";//判断是否是添加/修改
		$.ajax({
			url:"system/system_saveCollection.do",
			type:"post",
			data:{"isAdd":isAdd,"poolIds":poolsStr ,"collection.id":collection_curCollectionId,"collection.collectiontitle":collectionTitle,
				"collection.printquantity":cNum,"collection.descriptionof":cDesc,
				"collection.categories":categories,"collection.styles":styles,"collection.subject":subject,"collection.color":color
			},
			success:function(){
				collection_closeBox();
				collection_iniCollection();
			}
		});
	}
}

//选则收藏夹内作品
function collection_selectWorks(obj,poolId){
	for(var i in collection_poolArr){
		if(collection_poolArr[i]==poolId){
			collection_poolArr.splice(i,1);
			$(obj).next().attr("class","not");
			return;
			//移除选中样式
		}else if(i==collection_poolArr.length-1){
			if(collection_poolArr.length<12){
				collection_poolArr.push(poolId);
				//添加选中样式
				$(obj).next().attr("class","on");
			}else{
				public_err_prompt("Can only add 12 pieces of works",$(""));
			}
		}
	}
	if(collection_poolArr.length==0){
		collection_poolArr.push(poolId);
		$(obj).next().attr("class","on");
	}
}

//查看作品初始化
function collection_checkworks(id,url,title,label,desc,loadpath){
	$("#divid_collection_bj2").show(100);
	$("#divid_collection_window2").show(300);
	//关闭页头
	$("#cool_navigation").hide();
	document.getElementById("pool_left").style.display="";
	document.getElementById("pool_right").style.display="";
	document.getElementById("cool_download").style.display="";
	//$("#image-wrap").html("<img  src=\""+url+"\" alt=\"\" id=\"inputid_collection_pimg\" />");
	//$("#inputid_collection_pimg").attr("src",url);
	document.getElementById("bgasdasdasd").style.display="";
	document.getElementById("cool_download").style.display="";
	document.getElementById("inputid_collection_pimg").style.display="none";
	collection_poolSize(url);
	if(title!=undefined&&title!=null&&title!=""){
		$("#inputid_collection_pname").val(title);
	}
	if(desc!=undefined&&desc!=null&&desc!=""){
		$("#inputid_collection_pdesc").val(desc);
	}
	if(label!=undefined&&label!=null&&label!=""){
		$("#inputid_collection_ptime").val(label);
	}
	$("#inputid_collection_worksid").val(id);
	$("#pool_flipid").val(id);
	//显示下载链接
	$("#cool_download").attr('href','system_download.do?urlString='+loadpath+'&filename='+loadpath+'');	
}

//查看作品翻页
function pool_flip(type){
	document.getElementById("bgasdasdasd").style.display="";
	document.getElementById("inputid_collection_pimg").style.display="none";
	var flipid=$("#pool_flipid").val();
	//alert(flipid);
	//alert(type);
	$.ajax({
        url:"system/system_poolflip.do", //上传文件的服务端
        data:{"flipid":flipid,"type":type},
        type:"post",
        success:function(pools){
        	//alert(pools);
        	for(var i in pools){
        		//alert(pools[i].id+"下一个");
        		//alert(pools[i].storeaddress);
        		collection_poolSize(pools[i].thumbnailurlac);
        		if(pools[i].title!=undefined&&pools[i].title!=null&&pools[i].title!=""){
        			$("#inputid_collection_pname").val(pools[i].title);
        		}
        		if(pools[i].desc!=undefined&&pools[i].desc!=null&&pools[i].desc!=""){
        			$("#inputid_collection_pdesc").val(pools[i].desc);
        		}
        		if(pools[i].label!=undefined&&pools[i].label!=null&&pools[i].label!=""){
        			$("#inputid_collection_ptime").val(pools[i].label);
        		}
        		$("#inputid_collection_worksid").val(pools[i].id);
        		$("#pool_flipid").val(pools[i].id);
        		//显示下载链接
        		$("#cool_download").attr('href','system_download.do?urlString='+pools[i].storeaddress+'&filename='+pools[i].storeaddress+'');	
        	}
        }
    });
}


//图片显示
function collection_poolSize(url){
	var img = new Image(); 
	img.src =url; 
	img.onload=function(){
		document.getElementById("bgasdasdasd").style.display="none";
		document.getElementById("inputid_collection_pimg").style.display="";
		var imgWidth = img.width; //原图宽度 
		var imgHeight = img.height;//原图高度
		if(imgWidth>imgHeight){
			var width=$(".left_box").width();
			var rate=imgWidth/width;
			$("#image-wrap").width(width-20);
			$("#image-wrap").height(imgHeight/rate);
			$("#image-wrap").css("margin-top",($("#left_box").height()-(imgHeight/rate))/2+"px").css("margin-left","10px");
		}else if(imgWidth<imgHeight){
			var height=$(".left_box").height();
			var rate=imgHeight/height;
			$("#image-wrap").height(height-20);
			$("#image-wrap").width(imgWidth/rate);
			$("#image-wrap").css("margin-top","10px").css("margin-left",($("#left_box").width()-(imgWidth/rate))/2+"px");
		}else{
			var height=$(".left_box").height();
			var width=$(".left_box").width();
			$("#image-wrap").height(height-20);
			$("#image-wrap").width(width-20);
			$("#image-wrap").css("margin-top","10px").css("margin-left","10px");
		}
		//显示图片
		$("#inputid_collection_pimg").attr("src",url);
	};  
}


//提交作品
function collection_comitWorks(){
	var title=$("#inputid_collection_pname").val();
	var desc=$("#inputid_collection_pdesc").val();
	var label=$("#inputid_collection_ptime").val();
	var date=$("#inputid_Upload_ptime").val();
	var pImgSrc=$("#inputid_collection_pimg").attr("src");
	if(pImgSrc==undefined||pImgSrc==""){
		public_err_prompt("Choose the work you want to upload.",$(""));
	}else if(title==undefined||title==""){
		public_err_prompt("Title can not be empty",$("#inputid_collection_pname"));
	}else if(title.length>64){
		public_err_prompt("Title Can't be better than 30 characters",$("#inputid_collection_pname"));
	}/*else if(label==undefined||label==""){
		public_err_prompt("Please enter the work label",$("#inputid_collection_ptime"));
	}else if(label.length>64){
		public_err_prompt("Label Can't be better than 100 characters",$("#inputid_collection_ptime"));
	}*/else if(date==undefined||date==""){
		public_err_prompt("Please choose the year of creation",$("#inputid_Upload_ptime"));
	}else if(desc.length>128){
		public_err_prompt("Description Can't be better than 100 characters",$("#inputid_collection_pdesc"));
	}else{
		var id=$("#inputid_collection_worksid").val();
		if(id==undefined||id==""){//保存
			collection_uploadWorksAjax(title,desc,label,date,pImgSrc);
		}else if(id=="cool"){
			pollection_uploadWorksAjax(title,desc,id,label,date);
		}else{
			collection_updateWorksAjax(title,desc,id,label,date);
		}
	}
}

//修改作品
function collection_updateWorksAjax(title,desc,id,label,date){
	$.ajax({
        url:"system/system_savePoolWorks.do", //上传文件的服务端
        data:{"pool.title":title,"pool.description":desc,"pool.id":id,"pool.entrylabel":label,"pool.createtime":date},
        type:"post",
        success:function(pool){
        	collection_closeWorksBox();
        	collection_updatePoolBefor(pool);
        	collection_iniCollection();
        }
    });
}

//添加作品 
function collection_uploadWorksAjax(title,desc,label,date){
	$.ajaxFileUpload({
        url:"system/system_savePoolWorks.do", //上传文件的服务端
        data:{"pool.title":title,"pool.description":desc,
        	"pool.entrylabel":label,"pool.createtime":date},
        secureuri:false,  //是否启用安全提交
        dataType: 'json',   //数据类型  
        fileElementId:"pool_add_position", //表示文件域ID
        //提交成功后处理函数      html为返回值，status为执行的状态
        success:function(pool){
        	collection_closeWorksBox();
        	collection_addPoolBeforUpload(pool);
        	window.location.reload();
        },
        //提交失败处理函数
        error: function (html,status,e){
        	alert("Upload failed");
        }
	    });
}

//cool添加pool作品 
function pollection_uploadWorksAjax(title,desc,label,date){
	$.ajaxFileUpload({
        url:"system/system_savePoolWorks.do", //上传文件的服务端
        data:{"pool.title":title,"pool.description":desc,
        	"pool.entrylabel":label,"pool.createtime":date},
        secureuri:false,  //是否启用安全提交
        dataType: 'json',   //数据类型  
        fileElementId:"cool_add_position", //表示文件域ID
        //提交成功后处理函数      html为返回值，status为执行的状态
        success:function(pool){
        	collection_closeWorksBox();
        	collection_addPoolUpload(pool);
        },
        //提交失败处理函数
        error: function (html,status,e){
        	alert("Upload failed");
        }
	    });
}

//collection里面添加作品请求结束后在页面添加作品
function collection_addPoolUpload(pool){
	collection_addPoolBeforUpload(pool);
	collection_openAddCollection();
}

//添加作品请求结束后在页面添加作品
function collection_addPoolBeforUpload(pool){
	var html="<div class=\"z1 fl\" id=\"divid_collection_pool_"+pool.id+"\">"+
			"<div class=\"zz1\"><div id=\"zq1\"><img id=\"imgclass_"+pool.id+"\" class=\"imgclass_collection_works\""+
			"src=\""+fileServerUrl+pool.storeaddress.replace(".","tb.")+"\" onclick=\"collection_checkworks('"+pool.id+"',"+
			"'"+fileServerUrl+pool.storeaddress.replace(".","ac.")+"','"+pool.title+"',"+
			"'"+pool.entrylabel+"','"+pool.desc+"','"+fileServerUrl+pool.storeaddress+"')\"/><div id=\"pool_m\" onclick=\"document.getElementById('imgclass_"+pool.id+"').click();\"></div></div>"+
			"<span>"+pool.title+"<a href=\"javascript:void(0)\">"+
			"<img src=\"../images/lajitong.png\" alt=\"\" onclick=\"collection_delPoolById("+pool.id+")\" /></a></span></div></div>";
	$("#pool_add").after(html);
}

//修改作品请求结束后在页面添加作品
function collection_updatePoolBefor(pool){
	$("#idspan_collection_poolTitle_"+pool.id).html(pool.title);
	$("#divid_collection_pool_"+pool.id).click(function(){
		collection_checkworks(pool.id,'',pool.title,pool.entrylabel,pool.description,fileServerUrl+pool.storeaddress);
	});
}


//关闭作品详细页
function collection_closeWorksBox(){
	$("#divid_collection_bj2").hide();
	$("#divid_collection_window2").hide();
	//$("#image-wrap").html("");
	$("#inputid_collection_pimg").attr("src","");
	$("#inputid_collection_pname").val("");
	$("#inputid_collection_pdesc").val("");
	$("#inputid_collection_worksid").val("");
}

//打开收藏夹浏览框
function collection_openCollectionScan(collectionId){
	//关闭页头
	$("#cool_navigation").hide();
	var html="";
	var index;
	var num=0;
	for(var i in collection_pools){
		if(collection_pools[i].collectionid==collectionId){
			index=i;
			/*html+=""+i+"";*/
			html+="<li><a href=\"javascript:void(0)\"><img onclick=\"collection_cWorksDetailspool('"+i+"')\" src=\""+fileServerUrl+collection_pools[i].storeaddress.replace(".","tb.")+"\" alt=\"\"/></a></li>";
			num++;
		}
	}
/*	collection_cWorksDetails(index);*/
	$("#nine_picture_Work_title").val(collection_pools[index].collectiontitle);
	$("#nine_picture_Year_of_creation").val(collection_pools[index].printquantity);
	$("#nine_picture_textarea").val(collection_pools[index].descriptionof);
	$("#ulid_collection_cScanBox").html(html);
	$("#nine_picture_t_bj_zuopin").show(100);
	$("#nine_picture_t_window_zuopin").show(300);
	$("#input_index").val(index);
	$("#input_page").val(num);
}

/*//收藏夹浏览页/点击作品查看收藏夹详情
function collection_cWorksDetails(index){
	$("#nine_picture_Work_title").val(collection_pools[index].collectiontitle);
	$("#nine_picture_Year_of_creation").val(collection_pools[index].printquantity);
	$("#nine_picture_textarea").val(collection_pools[index].descriptionof);
}*/

function collection_poolSize22(url){
	var img = new Image(); 
	img.src =url; 
	img.onload=function(){
		var imgWidth = img.width; //图片实际宽度 
		var imgHeight = img.height;
		if(imgWidth>imgHeight){
			var width=$(".left_box").width();
			var rate=imgWidth/width;
			$(".inputid_collectionpool_pimg").width(width-20);
			$(".inputid_collectionpool_pimg").height(imgHeight/rate);
			$(".inputid_collectionpool_pimg").css("margin-top",($("#left_box").height()-(imgHeight/rate))/2+"px").css("margin-left","10px");
		}else if(imgWidth<imgHeight){
			var height=$(".left_box").height();
			var rate=imgHeight/height;
			$(".inputid_collectionpool_pimg").height(height-20);
			$(".inputid_collectionpool_pimg").width(imgWidth/rate);
			$(".inputid_collectionpool_pimg").css("margin-top","10px").css("margin-left",($("#left_box").width()-(imgWidth/rate))/2+"px");
		}else{
			var height=$(".left_box").height();
			var width=$(".left_box").width();
			$(".inputid_collectionpool_pimg").height(height-20);
			$(".inputid_collectionpool_pimg").width(width-20);
			$(".inputid_collectionpool_pimg").css("margin-top","10px").css("margin-left","10px");
		}
		//显示图片
		$(".inputid_collectionpool_pimg").attr("src",url);
	};  
}


/////////////hg//////////////
//收藏夹浏览页/点击作品查看收藏夹详情/点击单个作品显示作品详情
function collection_cWorksDetailspool(index){
	//显示作品信息弹框
	$(".collection2_window2").fadeToggle(300);
	//关闭页头
	$("#cool_navigation").hide();
	//关闭作品信息弹框
	$("#collection2_close").click(function() {
		$(".collection2_window2").fadeOut(300);
	});
	collection_poolSize22(fileServerUrl+collection_pools[index].storeaddress.replace(".","ac."));
	$("#input_collection2_l").val(index);
	$("#inputid_collectionpool_pname").val(collection_pools[index].title);
	$("#inputid_collectionpool_plabel").val(collection_pools[index].entrylabel);
	$("#collectionpool_inputid_Upload_ptime").val(collection_pools[index].createtime);
	$(".pool2_add_warning").val(collection_pools[index].description);
	$("#inputid_collection_worksidec").val(collection_pools[index].id);
	$("#inputid_collection_coolid").val(collection_pools[index].collectionid);
	//添加下载链接
	$("#coolsecondary_download").attr('href','system_download.do?urlString='+fileServerUrl+collection_pools[index].storeaddress+'&filename='+fileServerUrl+collection_pools[index].storeaddress+'');	
	
}

//收藏夹浏览页/查看作品/左翻页查看
function collectionleft(){
	var inputindex=$("#input_index").val();//最后页
	var inputpage=$("#input_page").val();//页数
	var indexval=$("#input_collection2_l").val();//当前页
	var subtractor=indexval-1;//前一页
	var firstpage=(inputindex-inputpage)+1;
	if(firstpage<=subtractor){
		collection_poolSize22(fileServerUrl+collection_pools[subtractor].storeaddress.replace(".","ac."));
		$("#input_collection2_l").val(subtractor);
		$("#inputid_collectionpool_pname").val(collection_pools[subtractor].title);
		$("#inputid_collectionpool_plabel").val(collection_pools[subtractor].entrylabel);
		$("#collectionpool_inputid_Upload_ptime").val(collection_pools[subtractor].createtime);
		$(".pool2_add_warning").val(collection_pools[subtractor].description);
		$("#inputid_collection_worksidec").val(collection_pools[subtractor].id);
		//添加下载链接
		$("#coolsecondary_download").attr('href','system_download.do?urlString='+fileServerUrl+collection_pools[subtractor].storeaddress+'&filename='+fileServerUrl+collection_pools[subtractor].storeaddress+'');	
		
	}else{
		collection_poolSize22(fileServerUrl+collection_pools[inputindex].storeaddress.replace(".","ac."));
		$("#input_collection2_l").val(inputindex);
		$("#inputid_collectionpool_pname").val(collection_pools[inputindex].title);
		$("#inputid_collectionpool_plabel").val(collection_pools[inputindex].entrylabel);
		$("#collectionpool_inputid_Upload_ptime").val(collection_pools[inputindex].createtime);
		$(".pool2_add_warning").val(collection_pools[inputindex].description);
		$("#inputid_collection_worksidec").val(collection_pools[inputindex].id);
		//添加下载链接
		$("#coolsecondary_download").attr('href','system_download.do?urlString='+fileServerUrl+collection_pools[inputindex].storeaddress+'&filename='+fileServerUrl+collection_pools[inputindex].storeaddress+'');	
		
	}
}


//收藏夹浏览页/查看作品/右翻页查看
function collectionright(){
	var inputindex=$("#input_index").val();//最后页
	var inputpage=$("#input_page").val();//页数
	var indexval=$("#input_collection2_l").val();//当前页
	var subtractor=parseInt(indexval)+1;//后一页
	var firstpage=(inputindex-inputpage)+1;
	if(subtractor<=inputindex){
		collection_poolSize22(fileServerUrl+collection_pools[subtractor].storeaddress.replace(".","ac."));
		$("#input_collection2_l").val(subtractor);
		$("#inputid_collectionpool_pname").val(collection_pools[subtractor].title);
		$("#inputid_collectionpool_plabel").val(collection_pools[subtractor].entrylabel);
		$("#collectionpool_inputid_Upload_ptime").val(collection_pools[subtractor].createtime);
		$(".pool2_add_warning").val(collection_pools[subtractor].description);
		$("#inputid_collection_worksidec").val(collection_pools[subtractor].id);
		//添加下载链接
		$("#coolsecondary_download").attr('href','system_download.do?urlString='+fileServerUrl+collection_pools[subtractor].storeaddress+'&filename='+fileServerUrl+collection_pools[subtractor].storeaddress+'');	
		
	}else{
		collection_poolSize22(fileServerUrl+collection_pools[firstpage].storeaddress.replace(".","ac."));
		$("#input_collection2_l").val(firstpage);
		$("#inputid_collectionpool_pname").val(collection_pools[firstpage].title);
		$("#inputid_collectionpool_plabel").val(collection_pools[firstpage].entrylabel);
		$("#collectionpool_inputid_Upload_ptime").val(collection_pools[firstpage].createtime);
		$(".pool2_add_warning").val(collection_pools[firstpage].description);
		$("#inputid_collection_worksidec").val(collection_pools[firstpage].id);
		//添加下载链接
		$("#coolsecondary_download").attr('href','system_download.do?urlString='+fileServerUrl+collection_pools[firstpage].storeaddress+'&filename='+fileServerUrl+collection_pools[firstpage].storeaddress+'');	
		
	}
}

//收藏夹浏览页/查看作品/修改作品
function collection_poolcomitWorks(){
	var title=$("#inputid_collectionpool_pname").val();
	var desc=$(".pool2_add_warning").val();
	var label=$("#inputid_collectionpool_plabel").val();
	var date=$("#collectionpool_inputid_Upload_ptime").val();
	var id=$("#inputid_collection_worksidec").val();
	var coolid=$("#input_collection2_l").val();
	//alert(title+desc+label+date+id);
	$.ajax({
        url:"system/system_savePoolWorks.do", 
        data:{"pool.title":title,"pool.description":desc,"pool.id":id,"pool.entrylabel":label,"pool.createtime":date},
        type:"post",
        success:function(pool){        	
	    	$(".collection2_window2").fadeOut(300);
	    	collection_updateWorksAjax(title,desc,id,label,date);
	    	collection_iniCollection();
	    	//collection_openUpdateCollection(coolid);
	    	//$(".collection2_window2").fadeOut(300);
        }
    });
}


//打开添加作品框
function collection_addPool(){
	/*$("#divid_collection_bj2").show(100);
	$("#divid_collection_window2").show(300);*/
	document.getElementById("bgasdasdasd").style.display="none";
	document.getElementById("cool_download").style.display="none";
	document.getElementById("pool_left").style.display="none";
	document.getElementById("pool_right").style.display="none";
	$.imageFileVisible({wrapSelector: "#image-wrap",   
		fileSelector: "#pool_add_position",
		id:"inputid_collection_pimg",
	},function(src){
		collection_poolSize(src);
		$("#divid_collection_bj2").show(100);
		$("#divid_collection_window2").show(300);
	});
}

//删除作品
function collection_delPoolById(poolId){
	$.ajax({
		url:"system/system_delPoolById.do", //上传文件的服务端
        data:{"pool.id":poolId},
        type:"post",
        success:function(){
        	$("#divid_collection_pool_"+poolId).remove();
        	collection_iniCollection();
        }
	});
}

//删除收藏夹根据ID
function collection_delCollectionById(collectionId){
	$.ajax({
		url:"system/system_delCollectionById.do", //上传文件的服务端
        data:{"collection.id":collectionId},
        type:"post",
        success:function(){
        	$("#ulid_collection_collectionid_"+collectionId).remove();
        }
	});
}

