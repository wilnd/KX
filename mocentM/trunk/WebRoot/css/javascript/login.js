/***
 * 登录相关脚本
 */
var gDefaultPage = "";

document.onkeydown = function(event)
{
	var e = event || window.event || arguments.callee.caller.arguments[0];
	if(e && e.keyCode == 13)
	{
		login();
	}
}

//登录
function login()
{
	var loginName = $("#loginname").val();
	var password = $("#password").val();
	if(loginName.trim() == "")
	{
		$("#divUserNameInfo>span").text("登录名不能为空");
		return;
	}
	else
	{
		$("#divUserNameInfo>span").text("");
	}
	if(password.trim() == "")
	{
		$("#divPasswordInfo>span").text("密码不能为空");
		return;
	}
	else
	{
		$("#divPasswordInfo>span").text("");
	}
	
	$.ajax({
		type: "post",
		url: "/mocentM/login",
		data: {name: loginName, pwd: password},
		dataType: "json",
		async: false,
		success: function(ret){
			var obj = ret;
			if(obj != null)
			{
				var errorCode = obj[0].errorCode;
				var errorMsg = obj[0].errorMsg;
				
				if(errorCode == "4")
				{
					$("#divUserNameInfo>span").text(errorMsg);
				}
				else if(errorCode == "3" || errorCode == "2")
				{
					$("#divPasswordInfo>span").text(errorMsg);
				}
				else if(errorCode == "5")
				{
					loginsuccess();
				}
			}
		}
	})
}
//登录成功
function loginsuccess()
{
	var customerInfo = getCustomerInfo($("#loginname").val());
	if(customerInfo != null)
	{
		var userInfo = new mocent.UserProfile();
		userInfo.userId = customerInfo[0].id;
		userInfo.loginId = customerInfo[0].login_name;
		userInfo.userName = customerInfo[0].true_name;
		mocent.setUserInfo(userInfo);
	}
	//车机软件版本发布和查询
	if(customerInfo[0].userType==1){
		gDefaultPage = "../toolBar/manager.html";
	}else{
		gDefaultPage = "monitor.html";
	}
	window.location = gDefaultPage;
}
//修改密码
function modifyPwd()
{
	$("#divModifyPwd").dialog({
		height: 'auto',
		width: 400,
		modal:  true,
		title: "修改密码",
		buttons: [
		      {
		    	  text: "确认",
		    	  click: function(){
		    		  if($("#account").val().trim() == "")
		    		  {
		    			  $("#divErrorMsg").html("").html("<span style='color:red'>账号不可为空。</span>")
			    			 return;
		    		  }
		    		  else
		    		 {
		    			  var memInfo = getCustomerInfo($("#account").val());
		    			  if(memInfo == null)
		    			  {
		    				  $("#divErrorMsg").html("").html("<span style='color:red'>账号不存在，请重新输入。</span>")
		    				  return;
		    			  }
		    		 }
	    		
	    			 if($("#oldpwd").val().trim() == "")
	    			 {
	    				 $("#divErrorMsg").html("").html("<span style='color:red'>旧密码不可为空。</span>")
		    			 return;
	    			 }
	    			 else
	    			 {
	    				 //改动了获取旧密码的获取方式，起初是进行MD5加密，现在改成明文
	    				 var old_pwd = $("#oldpwd").val();
		    			 if(old_pwd != memInfo[0].login_pwd)
		    			 {
		    				 $("#divErrorMsg").html("").html("<span style='color:red'>旧密码错误，请重新输入。</span>")
			    			 return;
		    			 }
	    			 }
		    		 
		    		 if($("#newpwd").val().trim() == "" || $("#newpwd1").val().trim() == "")
		    		 {
		    			 $("#divErrorMsg").html("").html("<span style='color:red'>新密码或确认密码不可为空。</span>")
		    			 return;
		    		 }
		    		 else
		    		 {
		    			 if($("#newpwd").val() != $("#newpwd1").val())
			    		 {
			    			 $("#divErrorMsg").html("").html("<span style='color:red'>两次输入的新密码不一致，请重新输入。</span>")
			    			 return;
			    		 }
		    		 }
		    		 
		    		 var newPwd = $("#newpwd").val();
		    		 var obj = {
		    			id:memInfo[0].id,
		    			loginName: $("#account").val(),
		    			newPwd: newPwd
		    		 }
		    		 var dialog = $(this);
		    		 $.ajax({
		    			 type: "POST",
		    			 data: obj,
		    			 url: "mocentM/modifyUserPwd",
		    			 dataType: "text",
		    			 async: false,
		    			 success: function(ret){
		    				 var obj = ret;
		    				 if(obj != null && obj == "success")
		    				{
		    					 dialog.dialog("close");
		    					 alert("密码修改成功！");
		    				}
		    			 }
		    		 })
		    	  }
		      },
		      {
		    	  text: "取消",
		    	  click: function(){
		    		  $( this ).dialog("close");
		    	  }
		      }
		]
	});
}
//获取用户的信息
function getCustomerInfo(loginname)
{
	var result = null;
	$.ajax({
		type: "post",
		url: "/mocentM/getCustomerInfo",
		data: {name: loginname},
		dataType: "json",
		async: false,
		success: function(ret){
			result = ret;
		}
	});
	return result;
}