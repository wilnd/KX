var gridObj,webName;
$(function(){
	initData();
	$("#arrowhead").hide();
	$("#parentMenu").text("WEB名");
	$("#childrenMenu").text("角色名");
	//打开添加权限模态框时触发
	$('#addPermissionModal').on('shown.bs.modal', function () {
		getAllParentPermission($("#parentList").closest("div").children("ul"));
	});
	//关闭添加权限模态框时触发
    $('#addPermissionModal').on('hide.bs.modal', function(){ $("#parentList").siblings('ul').children('li').remove();});
    
    //打开删除权限模态框触发
    $("#deletePermissionModal").on('shown.bs.modal',function() {
    	dropPermission($("#allPermisssion"));
    })	
    //设置下拉的展示与隐藏
    $("#addPermissionChooseParent").click(function(){
    	var parentPer=document.getElementById('addPermissionChooseParent');
    	if(parentPer.nextElementSibling.style.display=="none"){
    		parentPer.nextElementSibling.style.display='block';
    	}else{
    		parentPer.nextElementSibling.style.display='none';
    	}
    });
});
function initData(){
	gridObj = $.fn.bsgrid.init('searchTable', {
        url: '/mocentUUM/role/queryWebNameToTreeNode',
        pageSizeSelect: true,
        pageSize: 10,
        pageSizeForGrid:[5, 10, 15],
        displayBlankRows: false,
        displayPagingToolbarOnlyMultiPages: false,
        complete: function(options, XMLHttpRequest, textStatus) {
            if(options && options.totalRows==0){
            	
            }
        }
       ,
        beforeSend: function (options, XMLHttpRequest) {
        	gridObj.getPageCondition(gridObj.getCurPage());
        }
    });
}

function childrenMenu(id,name){
	var url = gridObj.options.settings.url; //旧链接
	if(url == "/mocentUUM/role/queryWebNameToTreeNode"){
		gridObj.options.settings.url = '/mocentUUM/role/queryRoleMenu';
		var	searchParames='webName='+name;
		webName = name;
		$("#arrowhead").show();
		$("#parentMenu").text("角色名");
		$("#childrenMenu").text("权限名");
	}
	if(url =='/mocentUUM/role/queryRoleMenu'){
		gridObj.options.settings.url = '/mocentUUM/permission/queryPermissionMenu';
		var	searchParames='roleId='+id;
		$("#parentMenu").text("权限名");
		$("#childrenMenu").text("");
	}
	gridObj.search(searchParames);
	
}
function operate(record, rowIndex, colIndex, options) {//给web添加链接
    return '<a href="#" onclick="childrenMenu(\''+record.id+'\',\'' + gridObj.getRecordIndexValue(record, 'menuName') + '\');">'+gridObj.getRecordIndexValue(record, 'menuName')+'</a>';
}

//权限页面的数据不同展示请求不同的地址
function thePrevious(){
	var searchParames;
	var thePreviousURL = gridObj.options.settings.url;
	if(thePreviousURL == '/mocentUUM/role/queryRoleMenu'){
		gridObj.options.settings.url = '/mocentUUM/role/queryWebNameToTreeNode'; //将地址置为上一级地址
		$("#arrowhead").hide();
		$("#parentMenu").text("WEB名");
		$("#childrenMenu").text("角色名");
	}else if(thePreviousURL == '/mocentUUM/permission/queryPermissionMenu'){
		gridObj.options.settings.url = '/mocentUUM/role/queryRoleMenu';
		searchParames='webName='+webName;
		$("#parentMenu").text("角色名");
		$("#childrenMenu").text("权限名");
	}
	gridObj.search(searchParames);
}
//添加权限
function addPermission(){
	var permissionName = $("#addPermissionName").val();
	var permissionDesc = $("#addPermissionDesc").val();
	
	if(permissionName.trim() == ""){
		$("#divPermissionNameInfo>span").text("权限名不能为空");
		return;
	}else{
		$("#divPermissionNameInfo>span").text("");
	}
	
	if(permissionDesc.trim() == ""){
		$("#divPermissionDescInfo>span").text("权限描述不能为空");
		return;
	}else{
		$("#divPermissionDescInfo>span").text("");
	}
	if($("#addPermissionChooseParent").text().trim() == '-请选择-'){
		alert("请选择父级权限，选择默认表示本身!");
		return ;
	}
	$("#addPermissionChooseParent").text();
	$.ajax({
		type: "post",
		url: "/mocentUUM/permission/addPermissionInfo",
		dataType: "json",
		data:{permissionName:permissionName,permissionDesc:permissionDesc,parentId:$("#addPermissionChooseParent").val()>0?$("#addPermissionChooseParent").val():-1}, //todo还没给父id
		async: true,
		success: function(ret){
			if(ret.statusCode == 'E'){
				alert(ret.message);
			}else if(ret.statusCode == 'S'){
				alert(ret.message);
				if($("#addPermissionModal").css("display") == 'block'){
					$("#addRoleClose").click();
				}
			}
		}
	})
}

//将父权限查询出来
function getAllParentPermission($obj){
	$.ajax({
		type: "get",
		url: "/mocentUUM/permission/queryPermissionInfo",
		dataType: "json",
		async: true,
		success: function(ret){
			var parentLevel = ret.parentLevel;
			if(ret != null && parentLevel.length>0)
			{
				var str='';
				if($obj.attr('class') && $obj.attr('class')=="dropdown-menu"){
					for(var i=0;i<parentLevel.length;i++){
						if(i == 0){
							str+='<li><a role="menuitem" tabindex="-1" onclick="drop(this)" href="#" value="-1">--默认--</a></li>';
						}
						 str+='<li  class="menu"><a role="menuitem" tabindex="-1" onclick="drop(this)" href="#" value="'+ret.parentLevel[i].id+'">'+parentLevel[i].permissionName+'</a></li>'
					}
				}else{
					for(var i=0;i<ret.length;i++){
						 str+='<li  class="menu"><a role="menuitem" tabindex="-1" onclick="addUserDrop(this)" href="#">'+parentLevel[i].permissionName+'</a></li>'
					}
				}
				
				$obj.append(str);
			}
		}
	})
}
//父权限下拉框
function drop(a){
	  var text=a.text;
	$("#addPermissionChooseParent").text(text);
	$("#addPermissionChooseParent").val($(a).attr("value"));
	$("#addPermissionChooseParent").append('<span class="caret"></span>');
	if($("#divParentId ul").css("display") == 'block'){		
		$("#divParentId ul").css("display","none")
	}
}

function dropPermission($allPermisssion){
	$allPermisssion.html("");
    $.ajax({
        type: "get",
        url: "/mocentUUM/permission/queryPermissionInfo",
        dataType: "json",
        async: false,
        success: function (ret) {
            if("S"==ret.statusCode){
                var len=ret.parentLevel.length;
                var str='';
                for(var i=0;i<len;){
                    var num=i+3;
                    if(num>len){
                        var endStr='<tr>';
                        for(var j=i;j<len;j++){
                            endStr+='<td><input type="checkbox" name="permissionId" value="{0}"></td> <td title="{1}" class="tableContent">{1}</td>'.format(ret.parentLevel[j].id,ret.parentLevel[j].permissionName);
                        }
                        endStr+='</tr>';
                        str+=endStr;
                    }else{
                    	str+=' <tr> <td> <input type="checkbox" name="permissionId" value="{0}"></td> <td title="{1}" class="tableContent">{1}</td> <td> <input type="checkbox" name="permissionId" value="{2}"></td> <td title="{3}" class="tableContent">{3}</td> <td> <input type="checkbox" name="permissionId" value="{4}"></td> <td title="{5}" class="tableContent">{5}</td></tr>'.format(ret.parentLevel[i].id,ret.parentLevel[i].permissionName,ret.parentLevel[i+1].id,ret.parentLevel[i+1].permissionName,ret.parentLevel[i+2].id,ret.parentLevel[i+2].permissionName);
                    }
                    i=i+3;
                }
                $allPermisssion.append(str);
            }
        }
    })
}

//删除权限操作
function deletePermission(){
	if($('[name="permissionId"]:checked').length == 0){
		alert("违法操作，请选择要删除的权限！");
		return ;
	}
	 $('#deletePermissionForm').ajaxSubmit({
	        type: "post",
	        url: "/mocentUUM/permission/delete",
	        dataType: "json",
	        async: true,
	        success: function (ret) {
	            if(ret && ret.statusCode =="S"){
	                alert(ret.message);
	            }else{
	                alert(ret.message);
	            }
	            $("#deletePermissionClose").click();
	        }
	    })
}
//format数据
String.prototype.format=function()  
{  
  if(arguments.length==0) return this;  
  for(var s=this, i=0; i<arguments.length; i++)  
    s=s.replace(new RegExp("\\{"+i+"\\}","g"), arguments[i]);  
  return s;  
};