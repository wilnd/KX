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
	
	var webName=getWebName(location.search);
	$.ajax({
		type: "get",
		url: "/mocentUUM/user/login",
		data: {username: loginName, password: password,webName:webName},
		dataType: "json",
		async: true,
		success: function(ret){
			if(ret != null)
			{
				 if("S" ==ret.statusCode)
				{
					 loginsuccess(ret);
				}else{
					$("#divPasswordInfo>span").text(ret.message);
				}
				
					
				
			}
		}
	})
}
//登录成功
function loginsuccess(userInfo)
{
	cookie("userId",userInfo.userId,{ expires: 1});
	cookie("permissionList",userInfo.permissionList+",",{ expires: 1});
	cookie("username",userInfo.username,{ expires: 1});
	
	var gDefaultPage="http://www.mocent.com/";
	var index=location.search.indexOf("&redirect");
	if(index > -1){
		//'&redirect='.length 长度为10
		gDefaultPage=location.search.substr(index+10);
	}
	if(userInfo.username=='admin'){
		gDefaultPage=location.origin+'/mocentUUM/manager/userList.html';
	}
	window.location.href = gDefaultPage;
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
	    			 /*else
	    			 {
	    				 //改动了获取旧密码的获取方式，起初是进行MD5加密，现在改成明文
	    				 var old_pwd = $("#oldpwd").val();
		    			 if(old_pwd != memInfo.password)
		    			 {
		    				 $("#divErrorMsg").html("").html("<span style='color:red'>旧密码错误，请重新输入。</span>")
			    			 return;
		    			 }
	    			 }*/
		    		 
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
		    			id:memInfo.id,
		    			oldPwd: $("#oldpwd").val(),
		    			newPwd: newPwd
		    		 }
		    		 var dialog = $(this);
		    		 $.ajax({
		    			 type: "post",
		    			 data: obj,
		    			 url: "/mocentUUM/user/updateUserPwd",
		    			 dataType: "json",
		    			 async: false,
		    			 success: function(ret){
		    				 if(ret && "S"==ret.statusCode ){
		    					 dialog.dialog("close");
		    					 alert(ret.message);
		    				 }else{
		    					 $("#divErrorMsg").html("").html("<span style='color:red'>"+ret.message+"</span>")
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
		url: "/mocentUUM/user/getCustInfo",
		data: {username: loginname},
		dataType: "json",
		async: false,
		success: function(ret){
			result = ret.user;
		}
	});
	return result;
}

/**
* @example mocent.util.cookie('the_cookie', 'the_value', { expires: 7, path: '/', domain: 'mocent.com', secure: true });
* @example mocent.util.cookie('the_cookie', 'the_value');
* @example mocent.util.cookie('the_cookie', null); 
* @example mocent.util.cookie('the_cookie');
*/
 function cookie(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
       options = extend({ path: "/" }, options);
    	
        if (value === null || value == "") {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toGMTString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toGMTString(); // use expires attribute, max-age is not supported by IE            
        }

        var path = options.path ? '; path=' + (options.path) : '';
        var domain = options.domain ? '; domain=' + (options.domain) : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } 
    else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = cookies[i].trim();
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue ? cookieValue : "";
    }
};
  function extend(destination, source, props) {

    if (destination == null || source == null)
        return destination;

    if (props) {
        for (var i = 0; i < props.length; i++) {
            destination[props[i]] = source[props[i]];
        }
    }
    else {
        for (var property in source) {
            //safari event not to setter
            //if (!(IS_SAFARI && property == "event"))
            destination[property] = source[property];
        }
    }
    return destination;
};
function getWebName(url){
	var index=url.lastIndexOf("webName");
	var webName;
	if(index>0){
		//9 是'&redirect'.length
		webName=url.substr(index+"webName".length+1,url.indexOf('&redirect')-9);
	}
	return webName;
}