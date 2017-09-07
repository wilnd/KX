package mocent.uum.service;

import java.util.List;

import mocent.uum.entity.User;
import mocent.uum.service.bo.UserInfoBo;
import mocent.uum.util.Page;

/**
 * 
 * @author jiang
 * @date 2016年9月27日  上午9:51:26
 *
 */
public interface UserService {
	
	/**
	 * @param username 用户名
	 * 通过用户名获取对象
	 */
	User getUserByName(String username,String password);
	
	/**
	 * 增加用户信息
	 */
	void addUser(String username,String password,List<Integer> roleId);
	/**
	 * 通过用户id删除用户信息
	 */
	void deleteUser(int userId);
	
	List<String> getUserPermissionById(int userId,String webName);
	
	int updateUserPwd(int id,String oldPwd,String newPwd);
	/**
	 * 管理员修改用户密码
	 * @param id
	 * @param newPwd
	 * @return
	 */
	int updateUserPwdByAdmin(int id,String newPwd,List<Integer> roleId);
	
	Page<UserInfoBo> findUserList(String userName,String webName);
	
}
