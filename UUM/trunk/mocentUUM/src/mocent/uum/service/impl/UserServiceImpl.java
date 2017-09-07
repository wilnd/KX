package mocent.uum.service.impl;

import java.util.List;

import mocent.uum.dao.RelevancyMapper;
import mocent.uum.dao.UserMapper;
import mocent.uum.entity.User;
import mocent.uum.service.RoleService;
import mocent.uum.service.UserService;
import mocent.uum.service.bo.Per2RoleBo;
import mocent.uum.service.bo.UserInfoBo;
import mocent.uum.util.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author jiang
 * @date 2016年9月27日  上午9:51:20
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RelevancyMapper relevancyMapper;
	@Autowired
	private RoleService roleService;
	
	@Override
	public User getUserByName(String username,String password) {
		User user = userMapper.queryUserByName(username);
		if(user != null && password != null && password.equals(user.getPassword())){
			userMapper.insertUserLoginDate(user.getId());
		}
		return user;
	}
	
	@Override
	public void addUser(String username,String password,List<Integer> roleId){
		int userId = userMapper.insertUserInfo(username, password);
		relevancyMapper.insertUser2Role(userId, roleId);
	}
	
	@Override
	public void deleteUser(int userId) {
		userMapper.deleteUserInfo(userId);
	}
	
	@Override
	public List<String> getUserPermissionById(int userId,String webName) {
		return userMapper.queryUserPermission(userId, webName);
	}

	@Override
	public int updateUserPwd(int id, String oldPwd, String newPwd) {
		return userMapper.updateUserPwd(id, oldPwd, newPwd);
	}

	@Override
	public Page<UserInfoBo> findUserList(String userName, String webName) {
		int findUserListCount = userMapper.findUserListCount(userName, webName);
		Page<UserInfoBo> pageReuslt=new Page<UserInfoBo>(5, 1, findUserListCount);
		if(findUserListCount>0){
			pageReuslt.setResult(userMapper.findUserList(userName, webName));
		}
		return pageReuslt;
	}

	@Override
	public int updateUserPwdByAdmin(int id, String newPwd,List<Integer> roleId) {
		int sign = userMapper.updateUserPwdByAdmin(id, newPwd);
		roleService.updateRole(id, roleId);
		return sign;
	}
}
