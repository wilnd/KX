/**
 * Created by Mocent_rj_2 on 2016/11/18.
 */
var gridObj
$(function(){

    initGrid ();

    doSearch();

})

function initGrid (){
    gridObj = $.fn.bsgrid.init('searchTable', {
        url: '/mocentKX/sendKX/findKXBySended',
        autoLoad: false,
        pageSizeSelect: true,
        pageSize: 10,
        pageSizeForGrid:[5, 10, 15],
        displayBlankRows: false,
        displayPagingToolbarOnlyMultiPages: false,
        complete: function(options, XMLHttpRequest, textStatus) {
            if(options && options.totalRows==0){

            }
            // alert(options.totalRows);
        },
        beforeSend: function (options, XMLHttpRequest) {
            //  alert( gridObj.getPageCondition(gridObj.getCurPage()));
        }
    });
}
function doSearch() {
    var searchParames ={"userId" : parent.mocent.userVO.userId};
    //权限  create,commit,review
    searchParames.permissionList=parent.mocent.userVO.permissionList;
    if(!searchParames.permissionList || searchParames.permissionList == ','){
        parent.mocent.login();
        return false;
    }
    gridObj.search(searchParames);
}

function submitKX(){
	debugger;
	var listId=gridObj.getCheckedValues("id")
	if(listId.length < 1){
		alert("必须选中至少一个!");
		return false;
	}else{
		var state=5;//状态变成待发送  可再次发送
		$.ajax({
			type: "post",
			url: "/mocentKX/sendKX/updateKXState",
			dataType: "json",
			data:{listId:listId.join(),state:state,userId:parent.mocent.userVO.userId},
			async: true,
			success: function(ret){
				if(ret && ret.stateCode == "S"){
					alert("操作成功");
				}else{
					alert("操作失败");
				}
				parent.mocent.replaceIframe('html/sendKX.html');
			}
		});
	}
	
}
function openKX(id){
	parent.mocent.replaceIframe('html/historyKX.html',id);
	
}
function operate(record, rowIndex, colIndex, options) {
    return '<a href="#" onclick="openKX(' + gridObj.getRecordIndexValue(record, 'id') + ')">'+gridObj.getRecordIndexValue(record, 'kxNum')+'</a>';
}
function showTime(record, rowIndex, colIndex, options){
	if(gridObj.getRecordIndexValue(record, 'createDate')){
		return new Date(gridObj.getRecordIndexValue(record, 'createDate')*1000).toLocaleString();
	}
}
function showState(record, rowIndex, colIndex, options){
	if(gridObj.getRecordIndexValue(record, 'kxState') == 0){
		return '已保存';
	}else if(gridObj.getRecordIndexValue(record, 'kxState') == 1){
		return '提交';
	}else if(gridObj.getRecordIndexValue(record, 'kxState') == 2){
		return '未提交';
	}else if(gridObj.getRecordIndexValue(record, 'kxState') == 3){
		return '采纳';
	}
}