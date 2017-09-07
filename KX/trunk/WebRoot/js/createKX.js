var imagePathList;
var kxData;
$(function(){
	
	String.prototype.replaceAll = function (str1,str2){
		var str = this; 
		var result = str.replace(eval("/"+str1+"/gi"),str2);
		return result;
		}
	String.prototype.gblen = function() {  
		  var len = 0;  
		  for (var i=0; i<this.length; i++) {  
		    if (this.charCodeAt(i)>127 || this.charCodeAt(i)==94) {  
		       len += 2;  
		     } else {  
		       len ++;  
		     }  
		   }  
		  return len;  
		}
	
	var kxId=$(window.parent.document).find('iframe').attr('data_id');
	
	initXIUXIU();
	
	initCarVers();
	if(kxId && kxId !=0){
		initKXInfo(kxId)
	}
	//$('[name=carId]:checked').attr("dis-content");
})

function onSubmit(type){
	var carCheckList=$('[name=carId]:checked');
	var  imageUrl=$("#imageUrl").val();
	var title=$("#title").val();
	var content=$("#content").val();
	var url=$("#url").val();
	if(carCheckList && carCheckList.length == 0){
		alert("请勾选车典型号");
		return false;
	}
	if(!kxData && !imageUrl){
		alert("请选择图片");
		return false;
	}
	if(!title){
		alert("请填写标题");
		return false;
	}
	if(!content){
		alert("请填写正文内容");
		return false;
	}
	var contentStr='',carListContent=$(".showCar")
	for(var i=0;i<carListContent.length;i++){
		var content_car=$(carListContent[i]),
		 content_p=content_car.find("p");
		contentStr+="id="+content_car.attr("id").substr(4)+",";//数据为 car_{id}
		for(var j=1;j<content_p.length;j++){
			contentStr+=content_p[j].outerText+"▼";//p的分割
		}
		contentStr+="▽";//区分不同的车典排版
	}
	
	if(!contentStr){
		document.getElementById('content').focus();
		return false;
	}

	if(kxData && kxData.id){
		var httpUrl="/mocentKX/createKX/updateKXInfo";
		var resourceData={};
		var array=new Array();
		if(imageUrl || kxData.kxBody != content || kxData.kxTitle != title){
			 resourceData={kxUrl:url,kxTitle:title,kxBody:content,
					kxCreateBy:parent.mocent.userVO.userId,kxCreateByUserName:parent.mocent.userVO.userName,
					imageUrl:imageUrl}
		}
		for(var i=0;i<carCheckList.length;i++){array.push($(carCheckList[i]).val())}
		resourceData.oldCarList=JSON.stringify(imagePathList);
		resourceData.newCarList=array.join();
		resourceData.contentStr=contentStr;
		resourceData.id=kxData.id;
		resourceData.kxNum=kxData.kxNum;
	}else{
		var httpUrl="/mocentKX/createKX/addKXInfo";
		var resourceData={kxUrl:url,kxTitle:title,kxBody:content,
				kxCreateBy:parent.mocent.userVO.userId,kxCreateByUserName:parent.mocent.userVO.userName,
				contentStr:contentStr,imageUrl:imageUrl}
	}
	if(type == 1){
		resourceData.kxState=2;
	}
	
	$.ajax({
		type: "post",
		url: httpUrl,
		dataType: "json",
		data:resourceData,
		async: true,
		success: function(ret){
			if(ret && ret.stateCode == "S"){
				alert("成功!快讯编号为："+ret.kxNum);
				parent.location.href=parent.location.href;
			}else{
				alert("失败！清先复制出你需要的内容，然后刷新重试。");
			}
		}
	});
}

function initKXInfo(id){
	$.ajax({
		type: "get",
		url: "/mocentKX/createKX/findKXById",
		dataType: "json",
		data:{kxId:id},
		async: true,
		success: function(ret){
			if(ret){
				if(ret.imagePath){
					imagePathList=ret.imagePath;
					$("#flashEditorOut").hide();
					$('[name="carId"]').prop("checked",false).change();
					for(var i=0;i<ret.imagePath.length;i++){
						$('[name="carId"][value="'+imagePathList[i].carVerId+'"]').prop("checked",true).change();
					}
					$("#showImage .col-sm-5").html('<img alt="图片" src="/upload'+imagePathList[0].kxImageUrl.substr(2)+'"><input type="button" onclick="changeImage()" value="切换图片">')
				}
				if(ret.kxObj){
					kxData=ret.kxObj;
					$("#title").val(kxData.kxTitle);
					$("#title").blur();
					$("#content").val(kxData.kxBody);
					$("#content").blur();
					$("#url").val(kxData.kxUrl);
				}
			}
		}
	});
}

function initCarVers(){
	$.ajax({
		type: "get",
		url: "/mocentKX/createKX/showCarVers",
		dataType: "json",
		async: false,
		success: function(ret){
			if(ret && ret.length > 0){
				parent.mocent.carVerList=ret;
				var carList='';
				for(var i=0;i<ret.length;i++){
					carList+='<input name="carId" rownum="'+ret[i].rowNum+'" onchange="checkChange(this)" value="'+ret[i].id+'" dis-content="'+ret[i].carVerName+'" type="checkbox"><div class="car-check-span" title="'+ret[i].carVerName+'">'+ret[i].carVerName+'</div>';
				}
				$(".car-check-div").prepend(carList);
				$('[name="carId"]').prop("checked",true).change();
			}else{
				alert("没有车典信息！");
				 parent.mocent.replaceIframe('welcome.html');
			}
		}
	})
}

function changeImage(){
	$("#flashEditorOut").show();
	$("#showImage").hide();
}

function checkChange(obj){
	var checkObj=$(obj),
		flag=checkObj.is(":checked"),
		carId=checkObj.val()
		rowNum=checkObj.attr("rownum");
	if(flag){
		if($("#car_"+carId).length == 0){
			var str='<div id="car_'+carId+'" class="showCar"><p>'+checkObj.attr("dis-content")+'</p><div class="carVerContent"><div style="margin: 0 auto;width:'+rowNum*14+'px;text-align: left;"></div></div></div>';
			$("#showAllResult").append(str);
		}
	}else{
		$("#car_"+carId).remove();
	}
	
}

function titleBlur(val){
	if(!val){
		return false;
	}
	var carCheckList=$('[name=carId]:checked');
	if(carCheckList && carCheckList.length>0){
		$("#showAllResult").show();
		for(var i=0;i<carCheckList.length;i++){
			var carVerList=parent.mocent.carVerList;
			for(var j=0;j<carVerList.length;j++){
				if(carCheckList[i].value == carVerList[j].id){
					var carId=carVerList[j].id,
					    carDiv=$("#car_"+carId),
						carVerContent=carDiv.children(".carVerContent"),
						carVerTitle=carVerContent.find("#car_title_"+carId);
					if(val.length > carVerList[j].titleNum){
						alert(carVerList[j].carVerName+"标题字数必须小于"+ carVerList[j].titleNum);
					//	document.getElementById('title').focus();
						$("#showAllResult").hide();
						return false;
					}else{	
						if(carVerTitle.length){
							carVerTitle.html(val);
						}else{
							carVerContent.prepend('<p id="car_title_'+carId+'" >'+val+'</p>');
						}
					}
				}
			}
		}
	}
}
function contentBlur(val){
	if(!val){
		return false;
	}
	var carCheckList=$('[name=carId]:checked');
	if(carCheckList && carCheckList.length>0){
		$("#showAllResult").show();
		for(var i=0;i<carCheckList.length;i++){
			var carVerList=parent.mocent.carVerList;
			for(var j=0;j<carVerList.length;j++){
				if(carCheckList[i].value == carVerList[j].id){
					var carId=carVerList[j].id,
						 carDiv=$("#car_"+carId),
						 carVerContent=carDiv.children(".carVerContent"),
						 carVerTitle=carVerContent.find("#car_title_"+carId),
						 carVerContent2=carVerContent.children("div");
						
					if(carVerTitle.length){
						//carVerTitle.siblings("p").remove();
						carVerTitle.siblings("div").children("p").remove();
					}else{
						carVerContent2.html("");
					}
					var flag=appendContent(val,carVerList[j],carVerContent2);
					if(flag == false) return false;
				}
			}
		}
	}
}
function appendContent(val,carVer,carVerContent){
	var rowNum=(carVer.rowNum)*2,
		lineNum=carVer.lineNum,
	    talNum=rowNum*lineNum;
	val="  "+val.trim();
	if(val.gblen() > talNum){
		alert(carVer.carVerName+"内容字节必须小于"+ talNum);
		//document.getElementById('content').focus();
		$("#showAllResult").hide();
		return false;
	}else{
		var str='',
		lastIndex=0;
		var contend_str=val;
		for(var k=0;k<lineNum;k++){
			//var content_p=val.substr(rowNum*k,rowNum);
			var content_p="";
			var _lenCount=0;
			for(var u=0;u<contend_str.length;u++){
				if (contend_str.charAt(u).gblen()>1) { 
	                _lenCount += 2; 
	            } else { 
	                _lenCount += 1; 
	            } 
	 
	            if (_lenCount > rowNum) { 
		            if(k==0){
		            	content_p = contend_str.substring(0, u-1); 
		            	lastIndex=u-1;
	            	}else{
	            		content_p = contend_str.substring(0, u); 
	            		lastIndex=u;
	            	}
	                break; 
	            } else if (_lenCount == rowNum) { 
	            	if(k==0){
	            		content_p = contend_str.substring(0, u); 
	            		lastIndex=u;
	            	}else{
	            		content_p = contend_str.substring(0, u+1); 
	            		lastIndex=u+1;
	            	}
	                break; 
	            } 
			}
			if(content_p){
				contend_str=contend_str.substr(lastIndex);
			}else{
				content_p = contend_str; 
				contend_str="";
			}
			if(content_p && '。？！，、；：”’）……-——+=•》~.?!,\;:"\')~<>-_+='.indexOf(content_p.substr(0,1)) > -1){
				alert("有不合法的字符在句首！");
				$("#showAllResult").hide();
				return false;
			}
			str+='<p>'+content_p+'</p>';
		}
		carVerContent.append(str);
		carVerContent.find("p").eq(0).addClass("text_indent");
	}
	
}
function updateImage(imageUrl,filename){
	$.ajax({
		type: "get",
		url: "/mocentKX/createKX/uploadImage",
		dataType: "text",
		data:{iamgeUrl:imageUrl,filename:filename},
		async: false,
		success: function(ret){
			$("#imageUrl").val(ret);
			var aaa = ret.substr(2);
			console.log();
			$("#showImage .col-sm-5").html('<img alt="图片" src="E:'+ret.substr(2)+'">');
		}
	})
}

function initXIUXIU(){
	xiuxiu.setLaunchVars("cropPresets", ["300x200",{"my iphone":"640x920"},{"my pc":"1960*1020"}]);
	xiuxiu.embedSWF("editorImage", 3, 780, 500, "lite");
	
	xiuxiu.onInit = function (id)
	{
		//xiuxiu.loadPhoto("http://open.web.meitu.com/sources/images/1.jpg", false);
        xiuxiu.setUploadURL("http://web.upload.meitu.com/image_upload.php");
        xiuxiu.setUploadType(2);
        xiuxiu.setUploadDataFieldName("upload_file");
	}
	
	xiuxiu.onBeforeUpload = function(data, id){
		xiuxiu.setUploadArgs({filetype: data.type, type: "image", filename: data.name });
	}
	xiuxiu.onUploadResponse = function (data)
	{
		updateImage(JSON.parse(data).original_pic,xiuxiu.uploadArgs.filename);
		$("#flashEditorOut").hide();
	}
}