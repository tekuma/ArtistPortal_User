var editor;
Ext.define('Ext.ux.form.KindEditor',{
	extend:"Ext.form.TextArea",
	alias:'widget.kindeditor',
	onRender :function(ct, position){ 
	//	alert(document.getElementById('contents').innerHTML);
		if (this.id === undefined) this.id = this.name;
	    this.callParent(arguments);
	   // this.html = "<span>"+this.fieldLabel+"</span><textarea id='" + this.getId() + "' name='" + this.name + "'>aaaa</textarea>";
		setTimeout(this.initKindEditor(this.value),300);
	},
	initKindEditor:function(value){
		 editor=KindEditor.create('textarea[name="htmlbodys"]', {
			allowFileManager : true,
			fileManagerJson : ctxPath+'/static/plugins/editor/jsp/file_manager_json.jsp',
		 	uploadJson : ctxPath+'/static/plugins/editor/jsp/upload_json.jsp'
		});
		editor.html(value);
//		var v=KindEditor('textarea[name="htmlbodys"]').attr('style');
//		alert(v);
	},
	
	
	reset : function() {
		if (editor) {
			editor.html('');
		}
	},
	getValue : function() {
		if (editor) {
			return editor.html();
		} else {
			return ''
		}
	},
	saveValue:function(){
		KindEditor('textarea[name="htmlbodys"]').val(this.getValue());
		alert(KindEditor('textarea[name="htmlbodys"]').val());
	},
	getRawValue : function() {
		if (editor) {
			return editor.text();
		} else {
			return ''
		}
	}
	
});