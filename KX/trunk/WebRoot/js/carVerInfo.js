$(function(){
	var carVerId=$(parent.document).find("iframe").attr("data_id");
	$("#carVerId").val(carVerId);
	$("#carCreateBy").val(parent.mocent.userVO.userId);
	$("#carCreateUserName").val(parent.mocent.userVO.userName);
	initCarVerInfo(carVerId);
	initValidate();
	
	/*$("#signupForm").on("submit",function(){
		debugger;
		var error=$(".has-error");
			if(error && error.length>0){
				return false;
			}else{
				if($("#carVerId").val()>0){
					var url="/mocentKX/setting/updateCarVer";
				}else{
					var url="/mocentKX/setting/addCarVer";
				}
				$('#signupForm').ajaxSubmit({
			        type: "post",
			        url: url,
			        dataType: "json",
			        async: true,
			        success: function (ret) {debugger;
			            if(ret && ret.state =="S"){
			                alert('成功');
			            }else{
			            	 alert('失败');
			            }
			        }
			    })
			}
	});*/
}) 
function initCarVerInfo(id){
	if(id == "0"){
		return false;
	}else{
		$.ajax({
			type: "get",
			url: "/mocentKX/setting/findCarVerById",
			data:{id:id},
			dataType: "json",
			async: true,
			success: function(ret){
				if(ret){
					$("#carVerName").val(ret.carVer.carVerName);
					if(ret.carVer.carState>0){
						$("#carState").prop("checked",true);
					}
					$("#imageSizeWidth").val(ret.carVer.imageSizeWidth);
					$("#imageSizeHigh").val(ret.carVer.imageSizeHigh);
					$("#titleNum").val(ret.carVer.titleNum);
					$("#lineNum").val(ret.carVer.lineNum);
					
					$("#rowNum").val(ret.carVer.rowNum);
				}
			}
		})
	}
}

function initValidate(){
	 var icon = "<i class='fa fa-times-circle'></i> ";
     $("#signupForm").validate({
         rules: {
        	 carVerName: "required",
        	 imageSizeWidth:{
                 required: true,
                 minlength: 2
             },
        	 imageSizeHigh:{
                 required: true,
                 minlength: 2
             },
        	 titleNum: {
                 required: true,
                 minlength: 1,
                 min: 1
             },
        	 lineNum: {
                 required: true,
                 minlength: 1,
                 min: 1
             },
        	 rowNum:  {
                 required: true,
                 minlength: 1,
                 min: 1
             }
          
         },
         messages: {
        	 carVerName: icon + "请输入车典名称",
        	 imageSizeWidth: {
                 required: icon + "请输入图片宽度",
                 minlength: icon + "用户名必须两个字符以上"
             },
             imageSizeHigh: {
                 required: icon + "请输入图片高度",
                 minlength: icon + "密码必须两个字符以上"
             },
             titleNum: {
                 required: icon + "请输入标题字数",
                 minlength: icon + "密码必须1个字符以上",
                 min:  icon + "密码必须大于1"
             },
             lineNum: {
                 required: icon + "请输入正文行数",
                 minlength: icon + "密码必须1个字符以上",
                 min:  icon + "密码必须大于1"
             },
             rowNum: {
                 required: icon + "请输入每行字数",
                 minlength: icon + "密码必须1个字符以上",
                 min:  icon + "密码必须大于1"
             }
             
         },submitHandler: function(form){
             $(form).ajaxSubmit(function() {
                     var error = $(".has-error");
                     if (error && error.length > 0) {
                         return false;
                     } else {
                         if ($("#carVerId").val() > 0) {
                             var url = "/mocentKX/setting/updateCarVer";
                         } else {
                             var url = "/mocentKX/setting/addCarVer";
                         }
                         $('#signupForm').ajaxSubmit({
                             type: "post",
                             url: url,
                             dataType: "json",
                             async: true,
                             success: function (ret) {
                                 if (ret && ret.state == "S") {
                                     alert('成功');
                                     parent.mocent.replaceIframe('html/setting.html');
                                 } else {
                                     alert('失败');
                                 }
                             }
                         })
                     }
                 });
         }
     });
}

$.validator.setDefaults({
            highlight: function (element) {
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
            },
            success: function (element) {
                element.closest('.form-group').removeClass('has-error').addClass('has-success');
            },
            errorElement: "span",
            errorPlacement: function (error, element) {
                if (element.is(":radio") || element.is(":checkbox")) {
                    error.appendTo(element.parent().parent().parent());
                } else {
                    error.appendTo(element.parent());
                }
            },
            errorClass: "help-block m-b-none",
            validClass: "help-block m-b-none"
});