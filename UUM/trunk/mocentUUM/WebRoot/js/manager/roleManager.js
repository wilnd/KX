var gridObj;
$(function(){
    //format数据
    String.prototype.format=function()
    {
        if(arguments.length==0) return this;
        for(var s=this, i=0; i<arguments.length; i++)
            s=s.replace(new RegExp("\\{"+i+"\\}","g"), arguments[i]);
        return s;
    };
	initChooseWeb($("#chooseWeb").closest('div').children('ul'));
    gridObj = $.fn.bsgrid.init('searchTable', {
        url: '/mocentUUM/role/findRole',
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
	//添加用户
    $('#addRoleModal').modal('hide');
   //打开添加用户
    $('#addRoleModal').on('show.bs.modal', function(){
    		$("#newWebTextbox").show();
    		$("#myRoleForm").find("div").first().css("visibility","visible");//显示web选择框输入框
    		
    		$("#addRoleChooseWeb").closest('div').children('ul').children('li').remove();
    	    initChooseWeb($("#addRoleChooseWeb").closest('div').children('ul'));
           allPermission($("#allPermisssion"));
    	});
    
  //添加用户
    $('#updateRoleModal').modal('hide');
   //打开添加用户
    $('#updateRoleModal').on('show.bs.modal', function(){
    		var roleId=$(".bsgrid_editgrid_check:checked");
    		var flag=true;
    		if(roleId  && roleId.length !=1){
    			flag=false;
    			if(roleId.length ==2 && roleId[0].value =="on"){
    				flag=true;
    				roleId=$(roleId[1]);
        		}
    		}
    		
    		if(!flag){
    			alert("请选择一个角色");
        		return false;
    		}else{
    			$("#updateRoleChooseWeb").closest('div').children('ul').children('li').remove();
    			initChooseWeb($("#updateRoleChooseWeb").closest('div').children('ul'));
    			allPermission($("#updatePermisssion"));
    			initRoleInfo(roleId.val());
    		}
    	});
    $("#addRoleChooseWeb").change(function(){//隐藏web下拉
    	var webName = $("#addRoleChooseWeb").text().trim();
    	if(webName != '选择web项目名' && webName != '' ){
    		$("#newWebTextbox").hide();
    	}
	});

})
function initRoleInfo(roleId){
    $.ajax({
        type: "get",
	    url: "/mocentUUM/role/findRoleById",
        dataType: "json",
        data:{roleId:roleId},
        success: function (ret) {
            $('[name="roleId"]').val(roleId);
            $('[name="uroleName"]').val(ret.role);
        	$('[name="updateRoleName"]').val(ret.role);
        	$("#updateRoleChooseWeb").text(ret.webName);
        	$("#updateRoleChooseWeb").val(ret.webName);
        	$("#updateRoleChooseWeb").append('<span class="caret"></span>');

           var perIds=document.getElementsByName('permissionId');
            for(var i=0;i<perIds.length;i++){
                    if(ret.permission.indexOf(","+perIds[i].value+",")>-1){
                        perIds[i].checked=true;
                    }
            }
        }
    })
}
function keydown(){
	var e = window.event || arguments.callee.caller.arguments[0];
	if(e && e.keyCode == 13){
		doSearch();
	}
}

function doSearch(){
    var searchParames = $('#searchParams').serializeArray();
    gridObj.search(searchParames);
}

function updateRole(){
    var num= $('[name="permissionId"]:checked').length;
    var $roleName=$('[name="updateRoleName"]'),
        $tip=$roleName.closest('div').siblings('div');

    if(!$roleName.val().trim() ||  $tip.text().indexOf('角色')>0){
        $tip.text('请输入正确的角色名');
    }else if(!$("#updateRoleChooseWeb").val()){
        $tip.text('请选择web项目');
    }else if(num==0){
        $tip.text('请勾选权限');
    }else{
        $tip.text('');
    }
    if($tip.text()){
        return false;
    }
    $('[name="webName"]').val($("#updateRoleChooseWeb").val());
    $('#myUpdateRoleForm').ajaxSubmit({
        type: "post",
        url: "/mocentUUM/role/update",
        dataType: "json",
        async: true,
        success: function (ret) {
            if(ret && ret.statusCode =="S"){
                alert('修改成功');
                $("#updateRoleClose").click();
                doSearch();
            }else{
                alert(ret.message);
            }
        }
    })
}

function deleteRole(){
    var roleId=$(".bsgrid_editgrid_check:checked");
    var flag=true;
	if(roleId  && roleId.length !=1){
		flag=false;
		if(roleId.length ==2 && roleId[0].value =="on"){
			flag=true;
			roleId=$(roleId[1]);
		}
	}
    if(!flag){
        alert("请选择一个角色");
        return false;
    }else{
        $.ajax({
            type: "get",
            url: "/mocentUUM/role/delete",
            dataType: "json",
            data:{roleId:roleId.val()},
            async: true,
            success: function (ret) {
                if(ret.statusCode != "S"){
                    alert(ret.message);
                }
                doSearch();
            }
        })
    }
}
function addRole(){
   var num= $('[name="permissionId"]:checked').length;
    var $roleName=$('[name="addRoleName"]'),
         $tip=$roleName.closest('div').siblings('div');
    var addWebName=$('[name="addWebName"]').val().trim();
     if(!$roleName.val().trim() ||  $tip.text().indexOf('角色')>0){
        $tip.text('请输入正确的角色名');
    }else if(!$("#addRoleChooseWeb").val() && !addWebName){
        $tip.text('请选择或添加新的web项目！');
    }else if(num==0){
        $tip.text('请勾选权限');
    }else{
        $tip.text('');
    }
    if($tip.text()){
       return false;
    }
   //用输入的代替选择的，如果输入没有的web则新增web
    if(addWebName){
    	$('[name="webName"]').val(addWebName);
    }else{
    	$('[name="webName"]').val($("#addRoleChooseWeb").val());
    }
    
    $('#myRoleForm').ajaxSubmit({
        type: "post",
        url: "/mocentUUM/role/add",
        dataType: "json",
        async: true,
        success: function (ret) {
            if(ret && ret.statusCode =="S"){
                alert('新增成功');
                $("#addRoleClose").click();
            }else{
                alert(ret.message);
            }
        }
    })


}
function checkRoleName(obj){
	
    if(obj.name == "updateRoleName"){
        var webName=$("#updateRoleChooseWeb").val();
    }else{
        var webName=$("#addRoleChooseWeb").val(); //取下拉框web名
    }
    
    if(!webName){
    	var addWebName=$('[name="addWebName"]').val().trim(); //取的新增web名
    	//用输入的代替选择的，如果输入没有的web则新增web
    	if(addWebName){
        	webName=addWebName;
        }else{
        	obj.value='';
        	obj.closest("div").nextElementSibling.innerText='请选择web项目';
        	return false;        	
        }
    }
    var roleName=obj.value.trim();
   
    if(!roleName){
        obj.closest("div").nextElementSibling.innerText='请输入角色';
        return false;
    }
    if(roleName && roleName == $('[name="uroleName"]').val()){
    	obj.closest("div").nextElementSibling.innerText='';
       return false;
    }
    $.ajax({
        type: "get",
        url: "/mocentUUM/role/query",
        dataType: "json",
        data:{webName:webName,roleName:roleName},
        async: true,
        success: function (ret) {
            if(ret && ret.length > 0){
                obj.closest("div").nextElementSibling.innerText='该web下角色已存在';
            }else{
                obj.closest("div").nextElementSibling.innerText='';
            }
        }
    })
}
function allPermission($allPermisssion){
	$allPermisssion.html("");
    $.ajax({
        type: "get",
        url: "/mocentUUM/permission/query",
        dataType: "json",
        async: false,
        success: function (ret) {
            if("S"==ret.statusCode){
                var len=ret.permission.length;
                var str='';
                for(var i=0;i<len;){
                    var num=i+3;
                    if(num>len){
                        var endStr='<tr>';
                        for(var j=i;j<len;j++){
                            endStr+='<td><input type="checkbox" name="permissionId" value="{0}"></td> <td title="{1}" class="tableContent">{1}</td>'.format(ret.permission[j].id,ret.permission[j].permissionName);
                        }
                        endStr+='</tr>';
                        str+=endStr;
                    }else{
                        str+=' <tr> <td> <input type="checkbox" name="permissionId" value="{0}"></td> <td title="{1}" class="tableContent">{1}</td> <td> <input type="checkbox" name="permissionId" value="{2}"></td> <td title="{3}" class="tableContent">{3}</td> <td> <input type="checkbox" name="permissionId" value="{4}"></td> <td title="{5}" class="tableContent">{5}</td></tr>'.format(ret.permission[i].id,ret.permission[i].permissionName,ret.permission[i+1].id,ret.permission[i+1].permissionName,ret.permission[i+2].id,ret.permission[i+2].permissionName);
                    }
                    i=i+3;
                }
                $allPermisssion.append(str);
            }
        }
    })
}
function changWeb(obj){
    //区别是否是弹出框里的web选择
    if(!obj.nextElementSibling.style.display){
        return false;
    }
    if(obj.nextElementSibling.style.display=="none"){
        obj.nextElementSibling.style.display='block';
    }else{
        obj.nextElementSibling.style.display='none';
    }
}
//下拉框
function drop(a){
    var text=a.text;
    var id ='#'+a.parentElement.parentElement.previousElementSibling.id;
    $(id).text(text);
    $(id).val(text);
    $(id).append('<span class="caret"></span>');
   changWeb(document.getElementById(id.substr(1)));
   $("#addRoleChooseWeb").change();
};

function initChooseWeb($obj){
	$.ajax({
		type: "get",
		url: "/mocentUUM/role/queryWebName",
		dataType: "json",
		async: true,
		success: function(ret){
			if(ret != null && ret.length>0)
			{
				var str='';
					for(var i=0;i<ret.length;i++){
						 str+='<li  class="menu"><a role="menuitem" tabindex="-1" onclick="drop(this)" href="#">'+ret[i].webName+'</a></li>'
					}
				$obj.append(str);
			}
		}
	})
}

function hideWebMenu(obj){
	var newWebName = $(obj).val(); //取得文本框输入的web名的值
	if(newWebName && newWebName != ''){
		$("#myRoleForm").find("div").first().css("visibility","hidden"); 
	}else{
		$("#myRoleForm").find("div").first().css("visibility","visible");
	}
	if(obj != null){
		
	}
}