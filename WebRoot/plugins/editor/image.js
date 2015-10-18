function image(obj,id){
	var editor = KindEditor.editor({
		fileManagerJson : ctxPath+'/static/plugins/editor/jsp/file_manager_json.jsp',
		uploadJson : ctxPath+'/static/plugins/editor/jsp/upload_json.jsp',
		allowFileManager : true
	});
	editor.loadPlugin('image', function() {
		editor.plugin.imageDialog({
			imageUrl : KindEditor(obj).attr('src'),
			clickFn : function(url, title, width, height, border, align) {
				KindEditor(obj).attr('src',url);
				KindEditor(id).val(url);
				editor.hideDialog();
			}
		});
	});
}