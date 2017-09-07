package mocent.uum.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mocent.uum.entity.User;
import mocent.uum.service.UserService;
import mocent.uum.util.ObjectFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * 
 * @author jiang
 * @date 2016年9月26日  下午5:59:27
 *
 */

@Controller
@SessionAttributes("currUser")
@RequestMapping("/user")
public class UserController {

	private static final Logger log=Logger.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	
	/**
	 *  添加用户信息
	 */
	@ResponseBody
	@RequestMapping("/add")
	public Map<String,Object> addUserInfo(String username,String password,@RequestParam(value="roleId",required=false)List<Integer> roleId){
		
		Map<String, Object> registerInfo= ObjectFactory.getMap();
		User user = userService.getUserByName(username,"");
		if(null !=user){
			registerInfo.put("message", "用户名已存在！");
		}else{
			userService.addUser(username, password,roleId);
			registerInfo.put("message", "添加用户成功！");
		}
		return registerInfo;
	}
	
	@ResponseBody
	@RequestMapping("/del")
	public Map<String,Object> delUser(String userId){
		Map<String, Object> delInfo = ObjectFactory.getMap();
		try{
			userService.deleteUser(Integer.parseInt(userId));
		}catch(Exception e){
			delInfo.put("message", "用户删除失败！");
		}
		delInfo.put("message", "用户删除成功！");
		return delInfo;
	}
	/**
	 * 处理登录请求
	 */
	@ResponseBody
	@RequestMapping("/login")
	public Map<String, Object> userLoginHandle(String username ,String password,String webName,HttpSession session){
		
		Map<String, Object> userPer = ObjectFactory.getMap();
		
		User user = userService.getUserByName(username,password);
		if(null !=user && password.equals(user.getPassword()) ){
			session.setAttribute("currUser",user);
			//获取用户权限
			List<String> list = userService.getUserPermissionById(user.getId(),webName);
			if(list == null || list.size() <= 0){
				userPer.put("statusCode","W");
				userPer.put("message", "你登录的用户不具备任何权限，请联系管理员添加！");
				return userPer;
			}
			String jsonStr = list.toString();
			String permission = jsonStr.substring(1, jsonStr.length()-1);
			userPer.put("userId", String.valueOf(user.getId()));
			userPer.put("username",user.getUsername());
			userPer.put("permissionList", permission);
			userPer.put("statusCode", "S");
		}else{
			userPer.put("statusCode", "E");
			userPer.put("message", "用户名或密码不对！");
		}
		return userPer;
	}
	
	@ResponseBody
	@RequestMapping("/getCustInfo")
	public Map<String,Object> getCustomerInfo(String username){
		Map<String, Object> map = ObjectFactory.getMap();
		User user = userService.getUserByName(username,"");
		user.setPassword("");//密码无需在前台显示
		map.put("user", user);
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/updateUserPwdByAdmin")
	public Map<String,Object> updateUserPwdByAdmin(int id,String newPwd,@RequestParam(value="roleId",required=false)List<Integer> roleId){
		Map<String, Object> statusInfo = ObjectFactory.getMap();
		int result;
		try {
			result = userService.updateUserPwdByAdmin(id, newPwd,roleId);
			if(result > 0){
				statusInfo.put("statusCode", "S");
				statusInfo.put("message", "用户信息成功修改！");
			}else{
				statusInfo.put("statusCode", "E");
				statusInfo.put("message", "用户信息修改失败!");
			}
		} catch (Exception e) {
			statusInfo.put("statusCode", "E");
			statusInfo.put("message", "用户信息修改失败!");
			log.error(e.toString());
		}
		
		return statusInfo;
	}
	
	@ResponseBody
	@RequestMapping("/updateUserPwd")
	public Map<String,Object> updateUserPwd(int id,String oldPwd,String newPwd){
		Map<String, Object> statusInfo = ObjectFactory.getMap();
		int result;
		try {
			result = userService.updateUserPwd(id, oldPwd, newPwd);
			if(result > 0){
				statusInfo.put("statusCode", "S");
				statusInfo.put("message", "密码已成功修改！");
			}else{
				statusInfo.put("statusCode", "E");
				statusInfo.put("message", "修改密码失败!");
			}
		} catch (Exception e) {
			statusInfo.put("statusCode", "E");
			statusInfo.put("message", "修改密码失败!");
			log.error(e.toString());
		}
		
		return statusInfo;
	}
	
	@ResponseBody
	@RequestMapping("/findUserList")
	public Map<String,Object> findUserList(String userName,String webName){
		Map<String, Object> map = ObjectFactory.getMap();
		map.put("list", userService.findUserList(userName, webName));
		return map;
	}
	@ResponseBody
	@RequestMapping("/userInfo")
	public Boolean getSessionUserInfo(Integer userId,HttpServletRequest request){
		User user = (User)request.getSession().getAttribute("currUser");
		if( null!=user && null!= userId && user.getId()==userId){
			return true;
		}
		return false;
	}
}
