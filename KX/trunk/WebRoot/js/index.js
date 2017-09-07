/**
 * Created by Mocent_rj_2 on 2016/11/4.
 */


if (!window) {
    this.window = this;
}

window.mocent = Function;

var mocent = {};
$(function(){
	initUser();
	
	if(parent.mocent.userVO.permissionList.indexOf("commit,") == -1){$("#suspending,#setting,#sendKX").hide();}
	
	if(parent.mocent.userVO.permissionList.indexOf("create,") == -1){$("#createKX").hide();}
	
    $('.J_menuItem').on('click', menuItem);
    config();
    $.fn.zTree.init($("#treeDemo"), setting);
})
var setting;
function config(){
	 setting = {
			async: {
			enable: true,
			//type:"get",
			url:"/mocentKX/history/getNodes",
			autoParam:["id", "name=name", "level=lv"],
			/*otherParam:{"otherParam":$("#selectYearId").val(),"month":$("#selectMonthId").val(),"day":$("#selectDayId").val()},*/
			/*otherParam:{"otherParam":"zTreeAsyncTest","year":$("#selectYearId").val()},*/
			dataFilter: filter
			},
			view: {expandSpeed:"",
				selectedMulti: false
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeAsync:function(){
					if(mocent.userVO.permissionList.indexOf("create,")>-1){
						setting.async.otherParam={"year":$("#selectYearId").val(),"month":$("#selectMonthId").val(),"day":$("#selectDayId").val(),"userId":mocent.userVO.userId}
					}else{
						setting.async.otherParam={"year":$("#selectYearId").val(),"month":$("#selectMonthId").val(),"day":$("#selectDayId").val()}
					}
				},
				onClick:function(a,b,c){
					if(c && c.level == 3){
						mocent.replaceIframe('html/historyKX.html',c.id);
					}
				}
			}
		};

			function filter(treeId, parentNode, childNodes) {
				if(parentNode){
					if(parentNode.level == 0){
						$("#selectYearId").val(parentNode.id);
					}else if(parentNode.level == 1){
						$("#selectMonthId").val(parentNode.id)
					}else{
						$("#selectDayId").val(parentNode.id)
					}
				}
				if (!childNodes) return null;
				for (var i=0, l=childNodes.length; i<l; i++) {
					childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
				}
				return childNodes;
			}
			
}


function initUser(){
	var user={};
	user.userId=cookie("userId");
    user.userName=cookie("username");
    user.permissionList=cookie("permissionList");
    mocent.userVO=user;
    if(!user.userId){
        mocent.login();
    }else{
    	$("#userName").html(user.userName);
    }
}
mocent.login=function(){
    location.href=location.origin+'/mocentUUM/login.html?webName=KX&redirect='+location.href;
}
function loginOut(){
    cookie("userId","");
    cookie("permissionList","");
    cookie("username","");
    mocent.login();
}

mocent.replaceIframe =function(dataUrl,id){
	if(!id){
		id=0;
	}
	var str='<iframe frameborder="0" data_id="'+id+'" id="MocentMain" name="MocentMain" scrolling="auto" src="'+dataUrl+'"'+
    ' style="z-index: 0; width:100%; height: calc(100% - 140px); border-right: 0px; border-left: 0px;'+
	       ' position: absolute; border-bottom: 0px;">'+
	   '</iframe>';
	 $('.J_mainContent').find('iframe').remove();
     $('.J_mainContent').append(str)
     return false;
}
function menuItem() {
        // 获取标识数据
        var dataUrl = $(this).attr('href'),
            dataIndex = $(this).data('index'),
            menuName = $.trim($(this).text());
          
        if (dataUrl == undefined || $.trim(dataUrl).length == 0)return false;

        // 添加选项卡对应的iframe
        var str='<iframe frameborder="0" data_id="0" id="MocentMain" name="MocentMain" scrolling="auto" src="'+dataUrl+'"'+
	       ' style="z-index: 0; width:100%; height: calc(100% - 140px); border-right: 0px; border-left: 0px;'+
		       ' position: absolute; border-bottom: 0px;">'+
		   '</iframe>';
      //  var str1 = '<iframe class="J_iframe" name="iframe' + dataIndex + '"  width="100%" height="calc(100% - 140px)" src="' + dataUrl + '" frameborder="0" data-id="' + dataUrl + '"></iframe>';
        $('.J_mainContent').find('iframe').remove();
        $('.J_mainContent').append(str);
        $(this).parent("li").siblings("li").removeClass("selectLi");
        $(this).parent("li").addClass("selectLi");
        return false;
    }
function getRequestValue(id){
    var param=location.search.substr(1);
    var arrayParam=param.split("&");
    var requst=new Object();
    for(var i=0;i<arrayParam.length;i++){
        var split=arrayParam[i].split('=');
        requst[split[0]]=unescape(split[1]);
    }
    return requst[id];
}
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
            destination[property] = source[property];
        }
    }
    return destination;
};

