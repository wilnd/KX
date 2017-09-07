package mocent.uum.dao;

import java.util.List;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.entity.Role;
import mocent.uum.service.bo.Per2RoleBo;
import mocent.uum.service.bo.UserInfoBo;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface RoleMapper {
	
	/**
	 * 通过web名拿到该web下的角色
	 * @param webName
	 * @return
	 */
	@Select("<script>SELECT id,role_name roleName FROM moc_role WHERE 1=1"
			+ "<if test=\"webName !=null and webName !=''  \"> AND web_name = #{webName}</if>"
			+ "<if test=\"roleName !=null and roleName !=''  \"> AND role_Name = #{roleName}</if></script>")
	List<Role> selectRole(@Param("webName")String webName,@Param("roleName")String roleName);
	
	/**
	 * 通过用户名获取角色，用于用户修改的角色
	 * @param userId
	 * @return
	 */
	@Select("SELECT r.id,r.role_name roleName FROM moc_role r,moc_user2role ur WHERE ur.role_id = r.id and ur.user_id = #{userId}")
	List<Role> selectRoleByUserId(int userId);
	/**
	 * 只获取web名
	 * @return
	 */
	@Select("SELECT web_name webName FROM moc_role GROUP BY web_name")
	List<Role> selectWebName();
		
	/**
	 * 权限数据目录展示用
	 */
	@Select("select web_name menuName,GROUP_CONCAT(role_name) childrenName from moc_role group by web_name limit #{0},#{1};")
	List<TreeNodeVo> selectWebNameToTreeNode(int startIndex,int dataSize);
	
	@Select("select count(DISTINCT web_name) from moc_role")
	int selectWebNum();
	/**
	 * 调用存储过程将角色信息插入数据库,返回插入的id
	 * @param roleName
	 * @param webName
	 */
	@Select("CALL addRole(#{roleName},#{webName})")
	int insertRole(@Param("roleName")String roleName,@Param("webName")String webName);
	
	/**
	 * 删除角色信息
	 * @param roleId
	 */
	@Delete("DELETE FROM moc_role WHERE id = #{0}")
	void deleteRole(int roleId);
	
	@Update("UPDATE moc_role SET role_name=#{roleName},web_name=#{webName},last_update_date=UNIX_TIMESTAMP(NOW()) WHERE id = #{roleId}")
	void updateRole(Per2RoleBo prb);
	
	@Select("<script>SELECT COUNT(1) FROM moc_role WHERE 1=1<if test=\"roleName !=null and roleName !=''  \"> AND r.role_name LIKE '%${roleName}%'</if></script>")
	public int findRoleByNameCount(@Param("roleName")String roleName);
	
	@Select("<script>select r.id,r.web_name webName,r.role_name role,GROUP_CONCAT(p.permission_name) permission from moc_role r left join moc_per2role pr on r.id = pr.role_id LEFT JOIN moc_permission p on pr.permission_id = p.id group by r.role_name,r.web_name<if test=\"roleName !=null and roleName !=''  \"> having r.role_name LIKE '%#{roleName}%'</if> LIMIT #{pageStart},#{pageSize}</script>")
	public List<UserInfoBo> findRoleByName(@Param("roleName")String roleName,@Param("pageStart")int pageStart,@Param("pageSize")int pageSize);
	
	@Select("SELECT r.role_name as role,r.web_name as webName,GROUP_CONCAT(p.id) as permission "
			+ " from moc_role r,moc_per2role pr,moc_permission p "
			+ "where r.id=pr.role_id and pr.permission_id=p.id and r.id=#{roleId}")
	UserInfoBo findRoleById(String roleId);
}
