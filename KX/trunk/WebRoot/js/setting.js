var gridObj;
$(function(){
	initNum();
	
	
	  gridObj = $.fn.bsgrid.init('searchTable', {
	        url: '/mocentKX/setting/findCarVers',
	        pageSizeSelect: true,
	        pageSize: 10,
	        pageSizeForGrid:[5, 10, 15],
	        displayBlankRows: false,
	        displayPagingToolbarOnlyMultiPages: false,
	        complete: function(options, XMLHttpRequest, textStatus) {
	           // debugger;
	            if(options && options.totalRows==0){
	            	
	            }
	           // alert(options.totalRows);
	        }
	       ,
	        beforeSend: function (options, XMLHttpRequest) {
	         //  alert( gridObj.getPageCondition(gridObj.getCurPage()));
	        }
	    });
})
function operate(record, rowIndex, colIndex, options) {
        return '<a href="#" onclick="parent.mocent.replaceIframe(\'html/addCarVer.html\','+gridObj.getRecordIndexValue(record, 'id')+')">'+gridObj.getRecordIndexValue(record, 'carVerName')+'</a>';
    }
function showState(record, rowIndex, colIndex, options){
	if(gridObj.getRecordIndexValue(record, 'carState') > 0){
		return '使用中';
	}else{
		return '失效';
	}
}
function showImageSize(record, rowIndex, colIndex, options){
	return gridObj.getRecordIndexValue(record, 'imageSizeWidth')+'*'+gridObj.getRecordIndexValue(record, 'imageSizeHigh');
}
function showTime(record, rowIndex, colIndex, options){
	if(gridObj.getRecordIndexValue(record, 'carCreateDate')){
		return new Date(gridObj.getRecordIndexValue(record, 'carCreateDate')*1000).toLocaleString();
	}
}

function initNum(){
	$.ajax({
		type: "get",
		url: "/mocentKX/setting/findLookUp",
		dataType: "json",
		async: true,
		success: function(ret){
			if(ret){
				$("#makeNum").val(ret.makeNum);
				$("#sendNum").val(ret.sendNum);
				$("#lookUpId").val(ret.id);
			}
		}
	})
}

function numFormSub(){
	var makeNum=$("#makeNum").val();
	var sendNum=$("#sendNum").val();
	var id=$("#lookUpId").val();
	if(! makeNum || !sendNum){
		alert("请填写相关配置")
		return false;
	}
	$.ajax({
		type: "post",
		url: "/mocentKX/setting/updateLookUp",
		dataType: "json",
		data:{makeNum:makeNum,sendNum:sendNum,id:id},
		async: true,
		success: function(ret){
			if(ret && ret.state == "S"){
				alert("修改成功");
			}else{
				alert("修改失败");
			}
		}
	})
	
}