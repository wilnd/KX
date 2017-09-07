//城市切换检所依赖
window.onload = function() {
	gMapObj.plugin(["AMap.ToolBar"], function() {
		gMapObj.addControl(new AMap.ToolBar());
	});
	if(location.href.indexOf('&guide=1')!==-1){
		gMapObj.setStatus({scrollWheel:false})
	}
}

function enptyMap(){ //选中复选框清空相关项
	var check=$(this);
	if(!check.prop("checked")){
		$("#txtKeyWord").val("");
		//证明是第二次搜索过
		if($("#tip2").html()){
			$("#tip2").html("");
	   		gMapObj.clearLimitBounds();
	   		//placeSearch.P.map.clearMap();
	     	$($(".amap-sug-result")[0]).remove();
		}
	}
}
$(function(){
	$("#screenSearchId").change(function(){
		if($("#tip2").html()){
			$("#tip2").html("");
	   		gMapObj.clearLimitBounds();
	   		if(placeSearch.M){
	   			placeSearch.M.clearOverlays();
	   			placeSearch.M.clearBound();
	   		}
	   		$("#txtKeyWord").val("");
	   		$($(".amap-sug-result")[1]).remove();
		}
	});
	
	$("#city").click(function(){
		if($("#province").val() == '--请选择--'){
			alert("请优先选择省份！");
		}
	});
	
	$("#resetId").click(function(){ //地图恢复默认
		$("#screenSearchId").prop("checked",'');
		enptyMap();
   		setSearchLocation(defaultProvince, defaultCity);
	});
})
var inputObj,placeSearch1;
//按下回车后操作
function keyDownSearch(obj){
	var e =window.event || arguments.callee.caller.arguments[0];
	if(e && e.keyCode == 13){
		if($("#province").val() != '--请选择--' && $("#city").val() == '--请选择--'){ //判断市是否被选择
			alert("请选择市区后再进行搜索！");
			return false;
		}
		if(obj.target.id =='txtKeyWord2'){
			var resultArr=$($('.amap-sug-result')[1]).find('.auto-item');
		}else{
			var txtKeyWord2=$("#txtKeyWord2");
			if(txtKeyWord2 && txtKeyWord2.val() && txtKeyWord2.val().trim()){
				placeSearch.M.clearOverlays();
				placeSearch.M.clearBound();
				txtKeyWord2.val("");
			}
			
			 gMapObj.clearLimitBounds();
				 var resultArr=$($('.amap-sug-result')[0]).find('.auto-item');
				 $("#txtKeyWord2").val("");
		}
		
		inputObj=obj;
	 	if(resultArr && resultArr.length>0 && obj.currentTarget.value){
	 		var flag = true,noPlaceNum=0,$lineObj,minIndex;
 			for(var i=0; i<resultArr.length; i++){ //输入移动上下键回车选择某一个地址,因为选择之后会有背景
 				var index = $(resultArr[i]).css('background').indexOf('202');
 				var address=$(resultArr[i]).children().text(),
 					content=$(resultArr[i]).html().split("<")[0];
 					if(index>-1){//通过上下键选择的
 						if(!address){ //没有地址
 							$lineObj=$(resultArr[i]);
 							addressSearch($lineObj,"","",$(obj.currentTarget));
 							return false;
 						}else{
 							resultArr[i].click();
 							return false;
 						}
 					}else{//直接回车的
 						if(obj.currentTarget.value == content){//是否全匹配 
 							if(!address){//没有地址
 								$lineObj=$(resultArr[i]);
 								addressSearch($lineObj,"","",$(obj.currentTarget));
 								return false;
 							}else{
 								resultArr[i].click();
 								return false;
 							}
 							return false;
 						}else{ //非全匹配且地址为空
 							if(!address){
 								if(!minIndex && minIndex!=0){
 									minIndex=i;
 								}
 								noPlaceNum++;
 							}
 						}
 					}
 			}
 			if(noPlaceNum>0 && minIndex==0){
 				addressSearch($(resultArr[0]),"","",$(obj.currentTarget));
 			}else{ 				
 				resultArr[0].click();
 			}
 			return ;
	 	}else{
	 		alert("请输入正确的地址！");
	 	}
	}
 	
}

//没有地址的，以当前城市搜索
function addressSearch($lineObj,content,address,$input){
	if(!$("#tip2").html()){//初始化第二个输入框
		$($(".amap-sug-result")[1]).remove();
		initCity("txtKeyWord2");
	}
	if($lineObj){
		content=$lineObj.html().split("<")[0];
		address=$lineObj.children().text();
	}
	if(!address){
		if($input){
			$input.val(content);
			$(".amap-sug-result").hide();
			if($input.attr("id") == "txtKeyWord2"){
				placeSearch.setCity(selectCity);
				if($("#screenSearchId").is(":checked")) {
					placeSearch.M &&placeSearch.M.clearBound();
					var bounds = gMapObj.getBounds(); //获取可视范围
					placeSearch.searchInBounds(content ,bounds, function(status,data){
			    		if(status=="no_data"){
				    		alert("当前区域没有该位置!");
				    		return;
				    	}
			    	});
				}else{
					placeSearch.search(content);				
				}
			}else{
				placeSearch1.setCity(selectCity);
				if($("#screenSearchId").is(":checked")) {
					placeSearch1.M &&placeSearch1.M.clearBound();
					var bounds = gMapObj.getBounds(); //获取可视范围
					placeSearch1.searchInBounds(content ,bounds, function(status,data){
			    		if(status=="no_data"){
				    		alert("当前区域没有该位置!");
				    		return;
				    	}
			    	});
				}else{
					$("#screenSearchId").prop("checked",'checked');
					placeSearch1.search(content);				
				}
			}
		}
			
		}
}

