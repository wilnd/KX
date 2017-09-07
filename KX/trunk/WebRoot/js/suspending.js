/**
 * Created by Mocent_rj_2 on 2016/11/18.
 */
var gridObj,carGrid;
$(function(){

    initGrid ();
    carGridInit();
})

function initGrid (){
    gridObj = $.fn.bsgrid.init('searchTable', {
        url: '/mocentKX/history/findWillSend',
        autoLoad: true,
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
function carGridInit(){
	carGrid = $.fn.bsgrid.init('carTable', {
        url: '/mocentKX/sendKX/findCarByKXId',
        autoLoad: false,
        pageAll: true,
        /*pageSizeSelect: true,
        pageSize: 10,
        pageSizeForGrid:[5, 10, 15],*/
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
function submitKX(){
	var kxid=gridObj.getCheckedValues("id"),
		carId=carGrid.getCheckedValues("id");
	if(kxid.length !=1){
		alert("请选择一条快讯");
		return false;
	}else{
		if(carId.length==0){
			alert("请选择发送车辆");
			return false;
		}else{
			$.ajax({
				type: "post",
				url: "/mocentKX/sendKX/addDownTask",
				dataType: "json",
				data:{carId:carId.join(),kxId:kxid.join()},
				async: true,
				success: function(ret){
					if(ret && ret.stateCode == "S"){
						alert("操作成功");
					}else{
						alert("操作失败");
					}
					parent.mocent.replaceIframe('html/suspending.html');
				}
			});
		}
	}
	
}
function flashCar(){
	var id=gridObj.getCheckedValues("id");
	if(id.length !=1){
		alert("请选择一条快讯");
		$("#showCar").hide();
	}else{
		$("#showCar").show();
		var searchParames ={"kxId" : id};
		if($("#isOnLine").is(":checked")){
			searchParames.isOnLine=0;
		}else{
			searchParames.isOnLine=1;
		}
		carGrid.search(searchParames);
	}
}
function deleteKX(){
	var listId=gridObj.getCheckedValues("id");
	if(listId.length < 1){
		alert("必须选中至少一个!");
		return false;
	}else{
		$.ajax({
			type: "post",
			url: "/mocentKX/history/updateKXState",
			dataType: "json",
			data:{listId:listId.join(),state:2},
			async: true,
			success: function(ret){
				if(ret && ret.stateCode == "S"){
					alert("操作成功");
				}else{
					alert("操作失败");
				}
				parent.mocent.replaceIframe('html/suspending.html');
			}
		});
	}
	
}
function openKX(id){
	if(parent.mocent.userVO.permissionList.indexOf("create,") > -1){
		parent.mocent.replaceIframe('html/createKX.html',id);
	}else{
		parent.mocent.replaceIframe('html/historyKX.html',id);
	}
	
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
	return "待发送";
}