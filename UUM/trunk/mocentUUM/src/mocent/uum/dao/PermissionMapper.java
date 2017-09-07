package mocent.uum.dao;

import java.util.List;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.entity.Permission;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PermissionMapper {
	
	@Select("<script>"
			+ "SELECT id,permission_name permissionName,permission_desc permissionDesc,parent_id parentId,create_date createDate,last_update_date lastUpdateDate FROM moc_permission WHERE 1=1"
			+ "<if test=\"permissionName !=null and permissionName !=''  \"> AND permission_name = #{permissionName}</if></script>")
	List<Permission> selectPermission(@Param("permissionName")String permissionName);
	
	@Insert("INSERT INTO moc_permission (permission_name,permission_desc,parent_id,create_date)VALUES(#{permissionName},#{permissionDesc},0,UNIX_TIMESTAMP(NOW()))")
	void insertPermission(Permission per);
	
	@Select("SELECT LAST_INSERT_ID()")
	int getParentId();
	
	/**
	 * 传过来没有父级id时，父级是自己
	 * @param parentId
	 */
	@Update("UPDATE moc_permission SET parent_id = #{parentId} WHERE id = #{parentId}")
	void insertPermissionParentId(int parentId);
	
	@Delete("<script><foreach collection='list' item='item' index='index' separator=';'>DELETE FROM moc_permission WHERE id = #{item}</foreach></script>")
	void deletePermission(List<Integer> perId);
	
	/**
	 * 传过来有父级id时
	 * @param parentId 权限父级id
	 * @param conditionId 当前插入数据id
	 */
	@Update("UPDATE moc_permission SET parent_id = #{0} WHERE id = #{1}")
	void insetPermissionParentIdByConditionId(int parentId,int conditionId); 
	
	@Select("select r.id,r.role_name menuName,GROUP_CONCAT(p.permission_name) childrenName from moc_role r,moc_per2role pr,moc_permission p where pr.permission_id = p.id and r.id = pr.role_id and r.web_name=#{webName} group by r.role_name limit #{1},#{2}")
	List<TreeNodeVo> selectPermissionByWebName(@Param("webName")String webName,int startIndex,int dataSize);
	
	@Select("select count(1) from (select r.id,r.role_name menuName,GROUP_CONCAT(p.permission_name) childrenName from moc_role r,moc_per2role pr,moc_permission p where pr.permission_id = p.id and r.id = pr.role_id and r.web_name=#{webName} group by r.role_name) a")
	int  selectRoleNum(@Param("webName")String webName);
	
	@Select("SELECT pr.permission_id id,p.permission_name menuName FROM moc_per2role pr INNER JOIN moc_permission p ON pr.permission_id = p.id AND role_id = #{0} LIMIT #{1},#{2}")
	List<TreeNodeVo> selectPermissionByRoleId(int roleId,int startIndex,int dataSize);
	
	@Select("select count(1) from (SELECT pr.permission_id id,p.permission_name menuName FROM moc_per2role pr INNER JOIN moc_permission p ON pr.permission_id = p.id AND role_id = #{0}) a")
	int selectRolePermissionNum(int roleId);
}
