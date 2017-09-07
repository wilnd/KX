package mocent.uum.dao;

import java.util.List;

import mocent.uum.entity.User;
import mocent.uum.service.bo.UserInfoBo;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 
 * @author jiang
 * @date 2016年9月27日  上午11:01:40
 *
 */
public interface UserMapper {
	/**
	 * 查询用户信息
	 * @param username
	 * @return
	 */
	@Select("SELECT id,user_name AS username,user_password AS password,create_date AS createTime,last_login_date AS lastLoginDate "
			+ "FROM moc_user WHERE user_name = #{username}")
	User queryUserByName(String username);
	
	@Insert("UPDATE moc_user SET last_login_date=UNIX_TIMESTAMP(NOW()) WHERE id = #{userId}")
	void insertUserLoginDate(int userId);
	
	/**
	 * 查询用户权限
	 * @param userId
	 * @param webName
	 * @return
	 */
	@Select("<script>SELECT p.permission_name "
			+ "FROM moc_role r,moc_permission p,moc_per2role pr "
			+ "WHERE r.id = pr.role_id AND p.id = pr.permission_id AND r.id "
			+ "IN (SELECT r.id "
			+ "FROM moc_user u,moc_role r,moc_user2role ur "
			+ "WHERE r.id = ur.role_id AND u.id = ur.user_id AND u.id = #{0} <if test=\"webName !=null and webName !=''  \">AND r.web_name = #{webName}</if>)</script>")
	List<String> queryUserPermission(int userId,@Param("webName") String webName);
	
	/**
	 * 调用存储过程增加用户信息，返回增加用户的id
	 */
	
	@Select("CALL addUser(#{username},#{password})")
	int insertUserInfo(@Param("username")String username,@Param("password")String password);
	
	/**
	 * 删除用户信息
	 */
	@Select("CALL deleteUser(#{userId})")
	void deleteUserInfo(int userId);
	
	@Update("UPDATE moc_user SET user_password =#{2} WHERE user_password=#{1} AND id=#{0}")
	int updateUserPwd(int id, String oldPwd, String newPwd);
	@Update("UPDATE moc_user SET user_password =#{1} WHERE id=#{0}")
	public int updateUserPwdByAdmin(int id, String newPwd);
	
	@Select("<script>select count(1) from (select u.id,u.user_name userName,GROUP_CONCAT(DISTINCT r.role_name) role,GROUP_CONCAT(DISTINCT r.web_name) webName,GROUP_CONCAT(DISTINCT p.permission_name) permission,u.last_login_date lastLoginDate from moc_user u "
			+ "LEFT JOIN moc_user2role ur on u.id = ur.user_id "
			+ "LEFT JOIN moc_role r on ur.role_id = r.id  "
			+ "LEFT JOIN moc_per2role pr on r.id = pr.role_id "
			+ "LEFT JOIN moc_permission p on pr.permission_id = p.id group by u.user_name having 1 = 1"
			+ "<if test=\"userName !=null and userName !=''  \"> AND userName LIKE '%${userName}%'</if>"
			+ "<if test=\"webName !=null and webName !='' \"> AND webName LIKE '%${webName}%'</if>) tb</script>")
	int findUserListCount( @Param("userName")String userName,@Param("webName") String webName);
	
	/**
	 * 分页数据被限定死，可通过搜索找出其他数据
	 * @param userName
	 * @param webName
	 * @return
	 */
	@Select("<script>select u.id,u.user_name userName,GROUP_CONCAT(DISTINCT r.role_name) role,GROUP_CONCAT(DISTINCT r.web_name) webName,GROUP_CONCAT(DISTINCT p.permission_name) permission,u.last_login_date lastLoginDate from moc_user u "
			+ "LEFT JOIN moc_user2role ur on u.id = ur.user_id "
			+ "LEFT JOIN moc_role r on ur.role_id = r.id  "
			+ "LEFT JOIN moc_per2role pr on r.id = pr.role_id "
			+ "LEFT JOIN moc_permission p on pr.permission_id = p.id group by u.user_name having 1 = 1"
			+ "<if test=\"userName !=null and userName !=''  \"> and userName like '%${userName}%'</if>"
			+ "<if test=\"webName !=null and webName !='' \"> and webName like '%${webName}%'</if> LIMIT 0,20</script>")
	List<UserInfoBo> findUserList( @Param("userName")String userName,@Param("webName") String webName);

}
