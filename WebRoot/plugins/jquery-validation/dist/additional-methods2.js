jQuery.validator.addMethod("code", function(value, element) {     
	return this.optional(element) || (/^([a-zA-Z0-9|]+)$/.test(value));     
}, "请输入字母、数字和|");

jQuery.validator.addMethod("letternum", function(value, element) {     
    return this.optional(element) || (/^([a-zA-Z0-9_|]+)$/.test(value));     
}, "请输入字母、数字、下划线"); 

jQuery.validator.addMethod("cnletternum", function(value, element) {    
	return this.optional(element) || /^[\u0391-\uFFE5\w]+$/.test(value);    
}, "只能包括中文字、英文字母、数字和下划线"); 


