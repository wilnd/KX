/*********************************************************************
 *系统用户部分
 */
//=============================================================================
mocent.addLoadedUri(mocent.getloadUri("user"));

mocent.UserCookieItems = ["userId", "loginId", "userType", "areaId", "userName"];
mocent.UserProfile = function(){
	this._typeName = "mocent.UserProfile";
	
	this.userId = "";
	
	this.loginId = "";
	
	this.userType = "";
	
	this.areaId = "";
	
	this.userName = "";
}
/**跳到登录页**/
mocent.gotoLogin = function (style) {
	var defaultLoginUrl = "";
	//综合平台
	if(mocent.application.userInfo != null && (mocent.application.userInfo.userType == "1"))
	{
		defaultLoginUrl = "mocentYJ/bss/login.html";
	}//ujreg
	else if(mocent.application.userInfo != null && mocent.application.userInfo.userType == "10")
	{
		defaultLoginUrl = "mocentYJ/bms/login.html";
	}//淘宝验证
	else if(mocent.application.userInfo != null && mocent.application.userInfo.userType == "20")
	{
		defaultLoginUrl = "mocentYJ/cms/login.html";
	}
    if(mocent["AUTH_TYPE"] == "local")
    {
        window.top.location = mocent.getWebRoot() + defaultLoginUrl;
    }
};
//是否已经登录
mocent.isLogin = function(){
	return false;
}
//登录状态验证
mocent.valideLogin = function(){
	mocent.application.userInfo = mocent.getUserInfo();
    var userInfo = mocent.application.userInfo;
    mocent.setUserInfo(userInfo);
    if(userInfo != null && userInfo.userId && userInfo.loginId)
    {
    	return true;
    }
    else
    {
    	mocent.gotoLogin();
    }
}
//设置用户信息
mocent.setUserInfo = function(/*mocent.UserProfile*/userInfo)
{
	for (var i = 0; i < mocent.UserCookieItems.length; i++) {
        var name = mocent.UserCookieItems[i];
        //name的值依次是：userId，loginId，userType，areaId，userName
        
        mocent.util.cookie(name, null);
        if (userInfo) {
        	mocent.util.cookie(name, userInfo[name],{ expires:0.5}); //,{ expires:0.5});
        }
    }
	
	 if (userInfo) 
	 {
		 mocent.application.userInfo = userInfo;
	 }
}
//获取用户信息
mocent.getUserInfo = function()
{
	var userInfo = new mocent.UserProfile();
	for (var i = 0; i < mocent.UserCookieItems.length; i++) {
        var _name = mocent.UserCookieItems[i];
        userInfo[_name] = mocent.util.cookie(_name);
    }
    return userInfo;
}

mocent.application.userInfo = mocent.getUserInfo();