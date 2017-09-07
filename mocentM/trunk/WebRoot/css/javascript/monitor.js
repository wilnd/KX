//引用js文件
mocent.addLoadedUri(mocent.getloadUri("monitor"));

var gCarId = "";

var gCarGrid;

var gServerList = null;
//车辆检索弹出框，在双击选择后关闭弹出框
var gDialog;
//车辆检索
function searchCar()
{
	$("#divCarInfo").css("display", "none");
	$("#txtPlateNumber").val("");
	$("#txtSim").val("");
	$("#txtPhoneNumber").val(""); 
	$("#txtCarId").val()
	gDialog = $("#divSearchCar").dialog({
		height: 'auto',
		width: 700,
		modal:  true,
		title: "车辆检索(双击检索结果选择车辆)",
		buttons: [
		      {
		    	  text: "确认",
		    	  click: function(){
		    		  gDialog.dialog("close");
		    	  }
		      },
		      {
		    	  text: "取消",
		    	  click: function(){
		    		  $( this ).dialog( "close" );
		    	  }
		      }
		]
	});
}
//获取车辆详细信息
function searchCarInfo()
{
	var data = {
		plateNumber: $("#txtPlateNumber").val(),
		sim: $("#txtSim").val(),
		phoneNumber: $("#txtPhoneNumber").val(), 
		id: $("#txtCarId").val()
	}
	var carObj = null;
	$.ajax({
		type: "POST",
		data: data,
		url: "mocentM/searchCarInfo",
		dataType: "json",
		async: false,
		success: function(ret){
			var obj = ret;
			/*if(obj != null && obj.length > 0)
			{*/
				$("#divCarInfo").css("display", "");
				displayCarInfo(obj);
			/*}*/
		}
	});
	return carObj;
}
//获取车辆详细信息
function getCarObj(carId)
{
	var carObj = null
	$.ajax({
		type: "POST",
		data: {carId: carId},
		url: "mocentM/getDetailInfo",
		dataType: "json",
		async: false,
		success: function(ret){
			var obj = ret;
			if(obj != null && obj.length > 0)
			{
				carObj = obj;
			}
		}
	});
	return carObj;
}
//加载客户呼叫队列
var isDataChanged = false;
function loadServerList()
{
	$.ajax({
		type: "post",
		url: "mocentM/loadCallService",
		data: {userId:mocent.application.userInfo.userId},
		dataType: "json",
		async: false,
		success: function(ret){
			var obj = ret;
			if(obj == null)
			{
				$("#divServiceList").html("");
				isDataChanged = true;
			}
			else
			{
				if(gServerList == null)
				{
					gServerList = obj;
					isDataChanged = true;
				}
				else
				{
					//比较当前收到的数据与较早前收到的数据
					//如果两者长度不同，则认为发生改变，并将gServerList替换为当前收到的数据
					if(gServerList.length != obj.length)
					{
						isDataChanged = true;
						gServerList = obj;
						$("#audioPlay").removeAttr("loop");
						$("#audioPlay")[0].pause();
					}
					else
					{
						//如果两者长度相等，比较内容，主要是比较car_id
						for(var m = 0; m < gServerList.length; m++)
						{
							for(var n = 0; n < obj.length; n++)
							{
								if(gServerList[m].car_id != obj[n].car_id)
								{
									isDataChanged = true;
									gServerList = obj;
									break;
								}else if(gServerList[m].car_id == obj[n].car_id && gServerList[m].state != obj[n].state){
									isDataChanged = true;
									gServerList = obj;
									break;
								}
							}
						}
					}
					
				}
				//如果当前收到的数据不为空，且数据发生改变，更新页面显示的数据
				if(gServerList != null && gServerList.length > 0 && isDataChanged)
				{
					var isPlayStr='';
					isDataChanged = false;
					
					var innerHtml = [];
					for(var i = 0; i < gServerList.length; i++)
					{
						var car_id = gServerList[i].car_id;
						var carInfo = getCarObj(car_id);
						var plateNumber = carInfo[0].plate_number;
						//多个呼叫的情况下，默认显示第一辆车所在位置
						if(!gCarId)
						{
							setPosition(gServerList[0].longitude, gServerList[0].latitude);
							document.title = "【新消息:" + plateNumber + "呼叫】客服监控";
							gCarId = car_id;
						}
						
						var lonAndlat = gServerList[i].longitude + "," + gServerList[i].latitude;
						//如果呼叫的时候，经纬度为空值
						//或者呼叫类型为手机呼叫，默认显示车辆最后的位置。
						if(gServerList[i].longitude == "" && gServerList[i].latitude == "")
						{
							var carInfo = getCarObj(car_id);
							if(carInfo != null && carInfo.length > 0)
							{
								lonAndlat = carInfo[0].lst_longitude + "," + carInfo[0].lst_latitude;
							}
						}
						innerHtml.push("<div data-toggle=\"context\" data-target=\"#context-menu\" data_carId=" + car_id + " ondblclick=\"lockCarUser('" + car_id + "')\"  onclick=\"displayDetailInfo('" + car_id + "', '" + lonAndlat + "',this,"+gServerList[i].state+","+gServerList[i].deal_id+")\" style=\"cursor: pointer;height: 36px; line-height: 36px;text-align: center; border-bottom: 1px solid silver\">");
						innerHtml.push("<div class=\"col-xs-6 col-sm-6\" style=\"border-right: 1px solid silver\">"  + plateNumber + "</div>");
						if(gServerList[i].state==1 && gServerList[i].deal_id ==mocent.application.userInfo.userId){
							isPlayStr+=',1';
							var mark='<span style=" background-color: green;width: 5px;">&nbsp;</span>';
						}else if(gServerList[i].state==2){
							var mark='<span style=" background-color: red;width: 5px;">&nbsp;</span>';
							isPlayStr+=',2';
						}else{
							var mark='<span style=" background-color: orange;width: 5px;">&nbsp;</span>';
						}
						if(gServerList[i].from == 0)
						{
							innerHtml.push("<div class=\"col-xs-6 col-sm-6\">车机呼叫&nbsp;"+mark+"</div>");
						}
						else
						{
							innerHtml.push("<div class=\"col-xs-6 col-sm-6\">手机呼叫&nbsp;"+mark+"</div>");
						}
						innerHtml.push("</div>");
						
					
					}
					$("#divServiceList").html(innerHtml.join(""));
					if($("#showCarInfo").val()){
						
						$('[data_carid="'+$("#showCarInfo").val()+'"]').css('background-color','#ccc');
					}
					//在显示新的数据之后，将isDataChanged置为false
				var abc=$("#divServiceList div").contextmenu({
						 target: '#context-menu',
					     onItem: function (context, e) {
					     gCarId=context.parent('div').attr('data_carId');
					     	if($(e.target).text()=='锁定'){
					     		lockCarUser();
					     	}else{
					     		cancelTerWarn();
					     	}
					     }
					});
				//鸣笛处理  不能有在处理状态的  鸣笛一定要有红色
					if(isPlayStr.indexOf(',1') == -1 && isPlayStr.indexOf(',2') > -1){
						$("#audioPlay").attr("loop", "loop");
						$("#audioPlay")[0].play();
					}else{
						$("#audioPlay").removeAttr("loop");
						$("#audioPlay")[0].pause();
					}
				}
				else
				{
					if(obj == null && isDataChanged)
					{
						$("#audioPlay").removeAttr("loop");
						$("#audioPlay")[0].pause();
						//gMapObj.clearMap();
					}
				}
			}
		},
		error: function(){
			console.log("发生错误")
			clearInterval(gInterval);
		}
	})
}
//标定车主所在位置,车主位置是不允许移动的
function setPosition(lon, lat)
{
	convertLonLat(lon / 1000000, lat / 1000000);
}
//显示呼叫位置及范围
function showPosition(obj)
{
	
	if (gOwnerMaker) {
		gOwnerMaker.setMap(null);
		gOwnerMaker = null;
    }
	gOwnerMaker = new AMap.Marker({
		icon: "images/markred.png",
        position: [obj.lon, obj.lat],
        draggable: false,
        cursor: 'move',
        raiseOnDrag: false
    });
	gOwnerMaker.setMap(gMapObj);
	
	var cancelMenu = new AMap.ContextMenu();  //创建右键菜单
	cancelMenu.addItem("锁定", function() {
		lockCarUser();
    }, 0);
	cancelMenu.addItem("结束", function() {
		cancelTerWarn();
    }, 0);
	
	gOwnerMaker.on("rightclick", function(e){
		cancelMenu.open(gMapObj, e.lnglat);
	});
	
	/*注释范围圈圈
	 * gCircle = new AMap.Circle({
        center: [obj.lon, obj.lat],
        radius: 150,
        fillOpacity: 0.5,
        strokeWeight: 1
    })*/
	//gCircle.setMap(gMapObj);
	gMapObj.setFitView()
}
/*gps坐标转换成高德地图经纬度*/
function convertLonLat(lon, lat)
{
	var cLon;
	var cLat;
	AMap.convertFrom([lon, lat], "gps", function(status, result){
		if(status == "complete")
		{
			if(result.info == "ok")
			{
				var pos = result.locations;
				cLon = pos[0].getLng();
				cLat = pos[0].getLat();
				showPosition({lon: cLon, lat: cLat});
			}
		}
	});
}
//显示查询结果
function displayCarInfo(carObj)
{
	$("#divCarList").html("")
	var listInner = [];
	var verList=[];
	var carInfoList=[];
	if(carObj != null && carObj.length > 0)
	{
		for(var i = 0; i < carObj.length; i++)
		{
			listInner.push("<div ondblclick=\"onSelectCar('" + carObj[i].id + "')\" style=\"cursor: hand; height: 36px; line-height: 36px; text-align: center\">");
			listInner.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].plate_number + "</div>");
			listInner.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].cur_owner_name + "</div>");
			var ownerId= carObj[i].cur_owner_id;
			var phone = getOwnerPhone(ownerId);
			listInner.push("<div class=\"col-xs-3 col-sm-3\">" + phone + "</div>");
			listInner.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].sim + "</div>");
			listInner.push("</div>");
			
			verList.push("<div style=\"cursor: hand; height: 36px; line-height: 36px; text-align: center\">");
			verList.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].ver_62 + "</div>");
			verList.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].ver_72 + "</div>");
			verList.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].ver_77 + "</div>");
			verList.push("<div class=\"col-xs-3 col-sm-3\">" + carObj[i].ver_map + "</div>");
			verList.push("</div>");
			
			carInfoList.push("<div style=\"cursor: hand; height: 36px; line-height: 36px; text-align: center\">")
			carInfoList.push("<div class=\"col-xs-4 col-sm-4\">" + carObj[i].unique_code + "</div>");
			if(carObj[i].is_online == 1){
				carInfoList.push("<div class=\"col-xs-4 col-sm-4\">在线</div>");
			}else{
				carInfoList.push("<div class=\"col-xs-4 col-sm-4\">不在线</div>");
			}
			carInfoList.push("<div class=\"col-xs-4 col-sm-4\">" + getLocalTime(carObj[i].lst_pos_time) || '' + "</div>");
			carInfoList.push("</div>");
		}
		$("#divCarList").html(listInner.join(""));
		$("#divCarVer").html(verList.join(""));
		$("#divCarVer1").html(carInfoList.join(""));
	}
	else
	{
		$("#divCarVer").html("没有可用于显示的查询结果!");
	}
}
//详细查询双击选择查询结果
function onSelectCar(carId)
{
	gCarId = carId;
	displayDetailInfo(carId, "");
	gDialog.dialog("close");
}
//获取车主当前手机号码
function getOwnerPhone(id)
{
	var phone = "";
	$.ajax({
		type: "post",
		url: "mocentM/getOwnerInfo",
		data: {id: id},
		dataType: "json",
		async: false,
		success: function(ret){
			var obj = ret;
			if(obj != null && obj.length > 0)
			{
				phone = obj[0].mobile_phone;
			}
		}
	});
	return phone;
}
//加载不在线的车辆列表
function loadOfflineCars()
{
	var carList = getOfflineCars();
	displayCarList(carList);
	$("#divOfflineList").dialog({
		height: 'auto',
		width: 770,
		modal:  true,
		title: "离线车辆",
		buttons: [
		      {
		    	  text: "确定",
		    	  click: function(){
		    		  $( this ).dialog( "close" );
		    	  }
		      }
		]
	});
}
//加载离线车辆列表
function getOfflineCars()
{
	var carList = null;
	 $.ajax({
		type: "POST",
		url: "mocentM/loadOfflineCars",
		dataType: "json",
		async: false,
		success: function(ret){
			var obj = ret;
			if(obj != null && obj.length > 0)
			{
				carList = obj;
			}
		 }
	});
	return carList;
};
//显示车辆列表
function displayCarList(carList)
{
	if($("#tabOfflineCarsList_pt_outTab"))
	{
		$("#tabOfflineCarsList_pt_outTab").remove();
	}
	if(carList != null && carList.length > 0)
	{
		gCarGrid = $.fn.bsgrid.init("tabOfflineCarsList", {
			localData: carList,
			displayBlankRows: false,
			pageSize: 10,
			stripeRows: true,
		});
	}
	else
	{
		$.fn.bsgrid.init("tabOfflineCarsList", {
			url: 'localdata/no-data.json',
            // autoLoad: false,
            pageSizeSelect: true,
            pageSize: 10,
            displayBlankRows: false, // single grid setting
            displayPagingToolbarOnlyMultiPages: true // single grid setting
		});
	}
}
//车主手机号码
function contact_number(obj)
{
	var ownerId = gCarGrid.getRecordIndexValue(obj, "cur_owner_id");
	var phone = getOwnerPhone(ownerId);
	return phone
}
//发送导航点
function sendPoint(type)
{
	if(gPos.lon == "" && gPos.lat == "")
	{
		return;
	}

	if(gCarId == undefined || gCarId == "")
	{
		alert("请选择车辆!");
		return;
	}
	
	var title = "发送";
	if(type == 0)
	{
		title += "导航点";
	}
	else if(type == 1)
	{
		title += "立即导航点";
	}
	var addrName = regeCode(gPos.lon + "," + gPos.lat);
	$("#txtAddrName").val(addrName);
	$("#divSendPoint").dialog({
		height: 'auto',
		width: 400,
		modal:  true,
		title: title,
		buttons: [
		      {
		    	  text: "发送",
		    	  click: function(){
		    		 var dialog = $(this);
		    		 $.ajax({
		    			 type: "POST",
		    			 data: {lat: gPos.lat, lon: gPos.lon, name: $("#txtAddrName").val(), carId: gCarId, nav: type},
		    			 url: "mocentM/sendPoint",
		    			 dataType: "text",
		    			 async: false,
		    			 success: function(ret){
		    				 var obj = ret;
		    				 if(ret == "0")
		    				 {
		    					 alert(title + "成功!");
		    					 dialog.dialog("close");
		    				 }
		    				
		    			 }
		    		 })
		    	  }
		      },
		      {
		    	  text: "取消",
		    	  click: function(){
		    		  $( this ).dialog( "close" );
		    	  }
		      }
		]
	});
}


//监控页面辅助方法
//逆地址解析，或者当前选择的点的名称
function regeCode(lonAndLat)
{
	var addrName = "";
	$.ajax({
		 type: "POST",
		 data: {lonAndLat: lonAndLat},
		 url: "mocenM/regeoAddress",
		 dataType: "text",
		 async: false,
		 success: function(ret){
			addrName = ret;
			if(addrName == "")
			{
				addrName = "后台发送POI";
			}
		 }
	 });
    return addrName;
}
//锁定用车辆为处理
function lockCarUser(car_id){
	if(car_id){
		gCarId=car_id;
	}
	if(gServerList){
		
		for(var i=0;i<gServerList.length;i++){
			//只能锁一张车
			if(car_id && gServerList[i].state ==1 && gServerList[i].car_id == gCarId){
				cancelTerWarn();
				return false;
			}else if(gServerList[i].state ==1 && gServerList[i].car_id != gCarId){
				alert("您已有锁定车辆");
				return false;
			}
			if(gCarId==gServerList[i].car_id){
				//1：表示我在处理 我在处理
				if(1 ==gServerList[i].state){
					alert("已锁定");
					return false;
				}else if(3 ==gServerList[i].state){
					alert("别人已锁定");
					return false;
				}
			}
		}
	}
	$.ajax({
		type: "post",
		url: "mocentM/lockCarUser",
		data: {carId: gCarId,loginId:mocent.application.userInfo.userId},
		dataType: "text",
		async: false,
		success: function(ret){
			if(ret == -1){
				alert("没有服务队列 ");
			}else if(ret == 2){
				alert("锁定成功");
			}else if(ret == 4){
				alert("该服务已不存在");
			}else if(ret == 5){
				alert("该服务已被别人锁定");
			}
		}
	})
}
//取消车辆告警
function cancelTerWarn()
{
	document.title = "客服监控";
	if (gOwnerMaker) 
	{
		gOwnerMaker.setMap(null);
		gOwnerMaker = null;
    }
	if(gCircle)
	{
		gCircle.hide();
		gCircle.setMap(null);
		gCircle = null;
		gMapObj.clearMap();
	}
	if(gServerList){
		for(var i=0;i<gServerList.length;i++){
			if(gCarId==gServerList[i].car_id){
				//1：表示我在处理 我在处理才能结束
				if(1 !=gServerList[i].state){
					alert("请先锁定");
					return false;
				}
			}
		}
	}
	$.ajax({
		type: "post",
		url: "mocentM/cancelTerWarn",
		data: {carId: gCarId},
		dataType: "text",
		async: false,
		success: function(ret){
			if(ret == 0)
			{
				gCarId = "";//将gCarId置为空值
				/*$("#audioPlay").removeAttr("loop");
				$("#audioPlay")[0].pause();*/
				isDataChanged = true;
				clearDetailInfo();
				//alert("取消告警成功");
			}
		}
	});
}
//清除车辆详细信息
function clearDetailInfo()
{
	$("#divOwnerName").text("");
	$("#divPhoneNumber").text("");
	$("#divPlateNumber").text("");
	$("#divCarModel").text("");
	$("#divCarColor").text("");
	$("#divCarSim").text("");
	$("#divRegisterTime").text("");
	$("#divDealer").text("");
	$("#divChannel").text("");
	$("#divVer62").text("");
	$("#divVer72").text("");
	$("#divVer77").text("");
	$("#divVerMap").text("");
	$("#divUniqueCode").text("");
	$("#showCarInfo").val("");
//	document.getElementById("showCarInfo").closest('div').style.textAlign="";
}
//显示车辆详细信息
function displayDetailInfo(car_id, lonAndlat,obj,state,dealId)
{
	if(obj){
	    obj.style.backgroundColor='#ccc';
		var $select=$(obj);
		if($select.siblings('div')){
			$select.siblings('div').css("background-color",'#f5f5f5');
		}
	}
	var carInfo = getCarObj(car_id);
	if(carInfo != null)
	{
		var $dealName=$("#divDealName");
		//橙色车辆处理
		if(state && state==3 ){
			$dealName.text(getUserInfo(dealId).login_name || "");
			$dealName.parent("div").show();
		}else{
			$dealName.parent("div").hide();
		}
		var ownerInfo = getOwnerInfo(carInfo[0].cur_owner_id);
		//对全局的选择车辆进行赋值
		gCarId = carInfo[0].id;
		$("#showCarInfo").val(gCarId);
		//document.getElementById("showCarInfo").closest('div').style.textAlign='center';
		$("#divOwnerName").text(ownerInfo[0].name);
		$("#divPhoneNumber").text(ownerInfo[0].mobile_phone);
		$("#divPlateNumber").text(carInfo[0].plate_number);
		$("#divCarModel").text("");
		$("#divCarColor").text(carInfo[0].color);
		$("#divCarSim").text(carInfo[0].sim);
		var date = new Date(carInfo[0].register_time * 1000);
		$("#divRegisterTime").text(date.Format("yyyy-MM-dd HH:mm"));
		$("#divDealer").text("");
		$("#divChannel").text("");
		$("#divVer62").text(carInfo[0].ver_62);
		$("#divVer72").text(carInfo[0].ver_72);
		$("#divVer77").text(carInfo[0].ver_77);
		$("#divVerMap").text(carInfo[0].ver_map);//暂时还没高德地图版本
		$("#divUniqueCode").text(carInfo[0].unique_code);
		if(lonAndlat != "")
		{
			var lon = lonAndlat.split(",")[0];
			var lat = lonAndlat.split(",")[1];
			setPosition(lon, lat);
		}
		$("#audioPlay").removeAttr("loop");
		$("#audioPlay")[0].pause();
	}
}
//获取车辆详细信息
function getDetailInfo()
{
	var carObj = null;
	$.ajax({
		type: "post",
		data: {carId: gCarId},
		url: "mocentM/getDetailInfo",
		dataType: "json",
		async: false,
		success: function(ret){
			if(ret != null)
			{
				carObj = ret;
			}
		}
	});
	return carObj;
}
//获取车主详细信息
function getOwnerInfo(ownerId)
{
	var ownerObj = null;
	$.ajax({
		type: "post",
		data: {id: ownerId},
		url: "mocentM/getOwnerInfo",
		dataType: "json",
		async: false,
		success: function(ret){
			if(ret != null)
			{
				ownerObj = ret;
			}
		}
	});
	return ownerObj;
}
//获取登陆详细信息
function getUserInfo(userId)
{
	var ownerObj = {};
	$.ajax({
		type: "get",
		data: {userId: userId},
		url: "mocentM/getCustomerInfoById",
		dataType: "json",
		async: false,
		success: function(ret){
			if(ret && ret.length > 0)
			{
				ownerObj = ret[0];
			}
		}
	});
	return ownerObj;
}

//获取登录序列，在服务器注册
function getLoginSeq()
{
	var seq = "";
	$.ajax({
		type: "post",
		data: {id: mocent.application.userInfo.userId},
		url: "mocentM/generateLoginSeq",
		dataType: "text",
		aysnc: false,
		success: function(ret){
			if(ret != null)
			{
				seq = ret;
			}
		}
	});
	return seq;
}

//对Date的扩展，将 Date 转化为指定格式的String 
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function(fmt) 
{ //author: zl
	var o = { 
			"M+" : this.getMonth()+1,                 //月份 
			"d+" : this.getDate(),                    //日 
			"H+" : this.getHours(),                   //小时 
			"m+" : this.getMinutes(),                 //分 
			"s+" : this.getSeconds(),                 //秒 
			"q+" : Math.floor((this.getMonth()+3)/3), //季度 
			"S"  : this.getMilliseconds()             //毫秒 
	}; 
	if(/(y+)/.test(fmt))
	{
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		for(var k in o)
		{
			if(new RegExp("("+ k +")").test(fmt)) 
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
		}
			
		return fmt;
	}
}

$(window).unload(function(){
	unloadHandler();
	if(gInterval)
	{
		clearInterval(gInterval);
	}
});
//页面关闭事件处理
function unloadHandler()
{
	$.ajax({
		type: "post",
		url: "/mocentM/onWindowsClosed",
		data: null,
		datatype: "text",
		async: false,
		success: function(ret){
			
		},
		error: function(){
			
		}
	});
}
//在页面加载之前检查用户是有具有特殊权限
function judgeLoginUser()
{
	$.ajax({
		type: "post",
		data: {uId: mocent.application.userInfo.userId},
		url: "mocentM/judgeLoginUser",
		dataType: "text",
		aysnc: false,
		success: function(ret){
			var loginId = ret;

			if(loginId != null && loginId != ""){
				$("#uploadFile").append("<a href='javascript:void(0)'><img alt='excel导入' title='excel导入' src='images/xls.png'/></a>")
				$("#show").hide();
				$("#hidenA").hide();
				$(".button-group").css("width","140px");
			}else{
				$(".button-group").css("width","110px");
				$("#uploadFile").hide();
				$("#hiddenC").hide();
			}
			}
	});
}
//用于获取上下文
function getContextPath(){   
    var pathName = document.location.pathname;   
    var index = pathName.substr(1).indexOf("/");   
    var result = pathName.substr(0,index+1);   
    return result;   
} 
function uploadOperate(){
	var excleDialog = $("#divExportExcel").dialog({
		title:"excel导入",
		buttons:[
		         	{
		         	text:"确认",
		         	click:function(){
		         		var fileName = $("#exportExcel").val();
		         		$("#filePath").val(fileName);
		         		var formData = new FormData($("#divExportExcel")[0]);//formID
		         			var subFileName = fileName.substring(fileName.lastIndexOf("\\")+1);
		         			var temp = getContextPath();
		         			if(!subFileName.endWith(".xls") && !subFileName.endWith(".xlsx")){
		         				alert("请选择xls或者xlsx文件!");
		         				return ;
		         			}
		         		
		         			$("#divExportExcel").ajaxSubmit({
		         					type:"post",
	         						url: temp+"/servlet/UploadHandleServlet.servlet",
	         						dataType:"json",
	         						async: false,
	         						success: function(ret){
	         							var obj = ret;
	         							if(obj != null){
	         								var errorCode = obj[0].errorCode;
	         								var errorMsg = obj[0].errorMsg;
	         								if(errorCode == "2"){ //成功
	         									alert(errorMsg);
	         								}
	         								if(errorCode == "4"){ //失败
	         									alert(errorMsg);
	         								}
	         								if(errorCode == "5"){ //重复上传
	         									alert(errorMsg);
	         								}
	         							}
	         						},
	         						error: function(){
	         							
	         						}
		         			});
		         			
		         		excleDialog.dialog("close");
		         	}
		         	},
		         	{
		         		text:"取消",
		         		click:function(){
		         			$(this).dialog("close");
		         		}
		         	}
		        ]
		
	});
}
//刷新页面是检查客户端与服务器的连接
function checkSocket()
{
	$.ajax({
		type: "post",
		url: "/mocentM/checkSocketStatus",
		data: null,
		datatype: "text",
		async: false,
		success: function(ret){
			
		},
		error: function(){
			
		}
	});
}

function getLocalTime(nS) {     
    return new Date(parseInt(nS) * 1000).toLocaleString();
 } 