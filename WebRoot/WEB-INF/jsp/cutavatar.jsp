<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>input information</title>
<script src="../js/jquery-1.8.3.min.js"></script>
<script src="../js/html5.js"></script>
<!--裁剪框-->
<script type="text/javascript" src="../js/jquery.imgareaselect.pack.js"></script>
<script src="../js/image-file-visible.js"></script>
<script type="text/javascript">
var cut_width;
var cut_height;
var cut_x;
var cut_y;

function preview(img, selection) {

	if (!selection.width || !selection.height)
			return;
		var scaleX = 100 / selection.width;
		var scaleY = 100 / selection.height;
		$('#preview img').css({
			width : Math.round(scaleX * 300),
			height : Math.round(scaleY * 300),
			marginLeft : -Math.round(scaleX * selection.x1),
			marginTop : -Math.round(scaleY * selection.y1)
		});

		$('#x1').val(selection.x1);
		$('#y1').val(selection.y1);
		$('#x2').val(selection.x2);
		$('#y2').val(selection.y2);
		$('#w').val(selection.width);
		$('#h').val(selection.height);
		cut_width=selection.width;
		cut_height=selection.height;
		cut_x=selection.x1;
		cut_y=selection.y1;
	}

	$(function() {
		$('#photo').imgAreaSelect({
			aspectRatio : '1:1',
			handles : true,
			fadeSpeed : 200,
			onSelectChange : preview
		});
	});

	$(document).ready(function() {
		$.imageFileVisible({
			wrapSelector : "#frame1",
			fileSelector : "#idvid_cut_file",
			id:"photo"
		},function(){
			$("#imgid_cut_imgPreview").attr("src",$("#photo").attr("src"));
			$('#photo').imgAreaSelect({
				aspectRatio : '1:1',
				handles : true,
				fadeSpeed : 200,
				onSelectChange : preview,
				imageHeight:300,
				imageWidth:300,x1: 0, y1: 0, x2: 100, y2: 100
			});
		});
	})
	
	//上传头像
	function cut_upload_head(){
		if($("#photo").attr("src")!=undefined&&$("#photo").attr("src")!=""){
			var img = new Image(); 
			img.src =$('#photo').attr("src") ; 
			var imgWidth = img.width; //图片实际宽度 
			var imgHeight = img.height;
			var wRate=imgWidth/300;
			var hRate=imgHeight/300;
			
			$.ajax({
				url:"system/system_saveUserHead.do", //上传文件的服务端
				data:{"imgW":Math.round(cut_width*wRate),"imgH":Math.round(cut_height*hRate),"imgX1":Math.round(cut_x*wRate),"imgY1":Math.round(cut_y*hRate),"imgUrl":$("#photo").attr("src")},
				type:"post",
				success:function(data){
					window.location.href="system_goLogin.do";
				}
			});
		}else{
			alert("Please choose a picture");
		}
	}
</script>
<link href="../css/cutavatar.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<a href="#" class="upload_profile_picture_header fr">How it works?</a>
<div class="upload_profile_picture_content">
<a href="#" class="cropit-image-input_none" >Select file</a>
<input type="file" id="idvid_cut_file" placeholder="Select file"  class="cropit-image-input"/>
<div class="frame"><div id="preview"><img src="" id="imgid_cut_imgPreview"/></div></div>
<div class="clear"></div>
<div class="frame" id="frame1"><img id="photo" src="" /></div>
<!--裁剪框-->
<p>Click and drag the mouse to select a region in the image.</p>
<input type="button" value="Upload" id="caijian_upload" onclick="cut_upload_head()"/>
</div>
<div class="clear"></div>
<div class="upload_profile_picture_footer"><span class="fr">contact us:<a href="#">hello@tekuma.io</a></span></div>
</body>
</html>
