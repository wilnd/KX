
$(function(){
	//format数据
	String.prototype.format=function()  
	{  
	  if(arguments.length==0) return this;  
	  for(var s=this, i=0; i<arguments.length; i++)  
	    s=s.replace(new RegExp("\\{"+i+"\\}","g"), arguments[i]);  
	  return s;  
	};
	initChooseWeb($('.dropdown-menu'));
    //左边菜单点击链接
    $('.gallery-list a').on('click', function(e) {
        var el = $(this);
        var link = el.attr('href');
        window.location = link;
    });
    initSelect();
    $("#chooseRole").hide();
    $("#addUserChooseWeb").click(function(){
    	var web=document.getElementById('addUserChooseWeb');
    	if(web.nextElementSibling.style.display=="none"){
    		web.nextElementSibling.style.display='block';
    	}else{
    		web.nextElementSibling.style.display='none';
    	}
    });
    $("#updateUserChooseWeb").click(function(){
    	var web=document.getElementById('updateUserChooseWeb');
    	if(web.nextElementSibling.style.display=="none"){
    		web.nextElementSibling.style.display='block';
    	}else{
    		web.nextElementSibling.style.display='none';
    	}
    });
    $("#addUserChooseRole").click(function(){
    	var web=$("#addUserChooseWeb").text().trim();
    	if(web && '选择web项目名'!=web){
    		var web=document.getElementById('addUserChooseRole');
        	if(web.nextElementSibling.style.display=="none"){
        		web.nextElementSibling.style.display='block';
        	}else{
        		web.nextElementSibling.style.display='none';
        	}
    	}else{
    		alert("请选择web项目");
    	}
    })
    $("#updateUserChooseRole").click(function(){
    	var web=$("#updateUserChooseWeb").text().trim();
    	if(web && '选择web项目名'!=web){
    		var web=document.getElementById('updateUserChooseRole');
        	if(web.nextElementSibling.style.display=="none"){
        		web.nextElementSibling.style.display='block';
        	}else{
        		web.nextElementSibling.style.display='none';
        	}
    	}else{
    		alert("请选择web项目");
    	}
    })
    //添加用户
    $('#addUserModal').modal('hide');
   //打开添加用户
    $('#addUserModal').on('show.bs.modal', function(){
    	    initChooseWeb($("#addUserChooseWeb").closest('div').children('ul'));
    	});
    //关闭添加用户
    $('#addUserModal').on('hide.bs.modal', function(){ $("#addUserChooseWeb").siblings('ul').children('li').remove();});
    
    $('#updateUserModal').modal('hide');
    $('#updateUserModal').on('show.bs.modal', function(){
    	var userId=$('.checked:checked');
    	if(userId.length !=1){
    		alert("请选择一个用户");
    		return false;
    	}else{
    		$("#divUserNameInfo").val(userId.val());
			$("#updateUserName").val(userId.parents("tr").find('td').eq(1).text());
			userId.parents("tr").find('td').eq(4).text(); //web名
			roleSelect2(userId.parents("tr").find('td').eq(4).text().trim(),userId.val());
    	}
    	
    });
    initSelect2();
})

function initSelect2(){
	$('#usertype2').selectpicker({
        'selectedText': 'cat'
   });
}

function resetSelect2(ret){
	$("#usertype2").siblings("div").remove();
	$("#usertype2").remove();
	$("#chooseRole2").append('<select id="usertype2" name="usertype" class="selectpicker show-tick form-control" multiple data-live-search="false"></div');
	var str;
	for(var i=0;i<ret.include.length;i++){
		 str+='<option selected="selected" value="'+ret.include[i].id+'">'+ret.include[i].roleName+'</option>';
	}
	for(var k=0; k<ret.exclude.length; k++){
		str+='<option value="'+ret.exclude[k].id+'">'+ret.exclude[k].roleName+'</option>';
	}
	$("#usertype2").html(str);
	initSelect2();
}
//todo
function roleSelect2(webName,userId){
	$.ajax({
		type:"get",
		url: "/mocentUUM/role/queryRole",
		data:{webName:webName,userId:userId},
		dataType: "json",
		async: true,
		success: function(ret){
			if(ret && ret != null){
				resetSelect2(ret);
			}
		}
	})
}
function initSelect(){
	 $('#usertype').selectpicker({
	        'selectedText': 'cat'
	   });
}
function restSelect(ret){
	$("#usertype").siblings("div").remove();
	$("#usertype").remove();
	$("#chooseRole").append('<select id="usertype" name="usertype" class="selectpicker show-tick form-control" multiple data-live-search="false"></div');
	var str='';
	for(var i=0;i<ret.length;i++){
		 str+='<option value="'+ret[i].id+'">'+ret[i].roleName+'</option>';
		 
	}
	$("#usertype").html(str);
	initSelect();
}

function getLocalTime(nS) {     
	       return new Date(parseInt(nS) * 1000).toLocaleString();
	 };
 //下拉框
function drop(a){
	  var text=a.text;
	$("#chooseWeb").text(text);
	$("#chooseWeb").val(text);
	$("#chooseWeb").append('<span class="caret"></span>');
};

function addUserDrop(a){
	  var text=a.text;
	  var id ='#'+a.parentElement.parentElement.previousElementSibling.id;
	$(id).text(text);
	$(id).val(text);
	$(id).append('<span class="caret"></span>');
	$(id).siblings('ul').hide();
	
	var roleId=id.substring(0,id.length-3)+"Role";
	$(roleId).text('选择角色').append('<span class="caret"></span>');
	initRole($(id),$("#usertype").siblings("div").find("ul"));
	$("#chooseRole").show();
}; 

function addUserDropRole(a){
	  var text=a.text;
	  var id ='#'+a.parentElement.parentElement.previousElementSibling.id;
		$(id).text(text);
		$(id).val(a.getAttribute('value'));
		$(id).append('<span class="caret"></span>');
		$(id).siblings('ul').hide();
}
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
				if($obj.attr('class') && $obj.attr('class')=="dropdown-menu"){
					for(var i=0;i<ret.length;i++){
						 str+='<li  class="menu"><a role="menuitem" tabindex="-1" onclick="drop(this)" href="#">'+ret[i].webName+'</a></li>'
					}
				}else{
					for(var i=0;i<ret.length;i++){
						 str+='<li  class="menu"><a role="menuitem" tabindex="-1" onclick="addUserDrop(this)" href="#">'+ret[i].webName+'</a></li>'
					}
				}
				
				$obj.append(str);
			}
		}
	})
}

function initRole($id,$roleId){
	var data={webName:$id.text().trim()}
	
	$.ajax({
		type: "get",
		url: "/mocentUUM/role/query",
		dataType: "json",
		data:data,
		async: true,
		success: function(ret){
			if(ret != null && ret.length>0)
			{
				restSelect(ret);
			}
		}
	})
}

function keydown(){
	var e = window.event || arguments.callee.caller.arguments[0];
	if(e && e.keyCode == 13){
		findUser();
	}
}
function findUser(){
	var obj={},url="/mocentUUM/user/findUserList";
	obj.userName=$('[name=userName]').val().trim();
	obj.webName=$("#chooseWeb").val()=="----不选----"?null:$("#chooseWeb").val();
	searchUser(obj,url);
}
function searchUser(obj,url){
	$.ajax({
		type: "get",
		url: url,
		data: obj,
		dataType: "json",
		async: true,
		success: function(ret){
		     var tip=$('#tip');
		     tip.siblings('tr').remove();
			if(ret && ret.list.totalData>0){
				tip.hide();
				userTable(ret.list.result);
			}else{
				tip.show();
			}
		}
	})
}
function userTable(data){
	var str='';
	for(var i=0;i<data.length;i++){
		str+='<tr><td><input class="checked" type="checkbox" value="{0}"></td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td><td>{5}</td></tr>'.format(data[i].id,data[i].userName||'',data[i].role||'',data[i].permission||'',data[i].webName||'',getLocalTime(data[i].lastLoginDate)||'');
	}
	$('#userList').append(str);
}
function addUser(){
	var addUserName = $("#addUserName").val();
	var addUserPassword = $("#addUserPassword").val();
	var addUserPassword2 = $("#addUserPassword2").val();
	if(addUserName.trim() == "")
	{
		$("#divUserNameInfo>span").text("账号不能为空");
		return;
	}
	else
	{
		$("#divUserNameInfo>span").text("");
	}
	if(addUserPassword.trim() == "")
	{
		$("#divPasswordInfo>span").text("密码不能为空");
		return;
	}
	else
	{
		$("#divPasswordInfo>span").text("");
	}
	if(addUserPassword.trim() != addUserPassword2.trim())
	{
		$("#divPasswordInfo2>span").text("确认密码不相等");
		return;
	}
	else
	{
		$("#divPasswordInfo2>span").text("");
	}
	if($("#addUserChooseWeb").val() && $("#chooseRole").find("button").attr("title").trim() != "==请选择=="){
		$("#divPasswordInfo2>span").text("");
	}else{
		$("#divPasswordInfo2>span").text("请选择web或角色！");
		return ;
	}
	
	$.ajax({
		type: "post",
		url: "/mocentUUM/user/add",
		dataType: "json",
		data:{username:addUserName,password:addUserPassword,roleId:$("#usertype").val().join()},
		async: true,
		success: function(ret){
			alert(ret.message);
			if(ret.message=='添加用户成功！'){
				$('#addUserClose').click();
			}
		}
	})
}

function updateUser(){
	var addUserPassword = $("#updateUserPassword").val();
	var addUserPassword2 = $("#updateUserPassword2").val();
	if(addUserPassword.trim() == "")
	{
		$("#divPasswordInfo>span").text("密码不能为空");
		return;
	}
	else
	{
			$("#divPasswordInfo>span").text("");
	}
	if(addUserPassword.trim() != addUserPassword2.trim())
	{
		$("#divPasswordInfo2>span").text("确认密码不相等");
		return;
	}
	else
	{
		$("#divPasswordInfo2>span").text("");
	}
	var roleid = $("#usertype2").val();
	$.ajax({
		type: "post",
		url: "/mocentUUM/user/updateUserPwdByAdmin",
		dataType: "json",
		data:{id:$("#divUserNameInfo").val(),newPwd:addUserPassword,roleId:roleid.join()},
		async: true,
		success: function(ret){
		      alert(ret.message)
			if(ret &&ret.statusCode == 'S'){
				$("#updateUserClose").click();
			}
			
		}
	})
}
function deleteUser(){
	var userId=$('.checked:checked');
	if(userId.length !=1){
		alert("请选择一个用户");
		return false;
	}else{
		var isOk=confirm("您确定要删除"+userId.parents("tr").find('td').eq(1).text()+"这个用户");
		if(isOk){
			$.ajax({
				type: "get",
				url: "/mocentUUM/user/del",
				dataType: "json",
				data:{userId:userId.val()},
				async: true,
				success: function(ret){
				      alert(ret.message);
				      findUser();
				}
			})
		}
		
	}
}