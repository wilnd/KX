package mocent.uum.dao;

import java.util.List;

import mocent.uum.service.bo.Per2RoleBo;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author jiang
 * @date 2016年9月30日  下午6:11:32
 *
 * @desc 用于中间表的关联操作
 */
public interface RelevancyMapper {
	
	//**************************用户角色关联操作*****************************
	/**
	 * 增加用户与角色的关联表数据
	 * @param userId
	 * @param roleId
	 */
	@Insert("<script>INSERT INTO moc_user2role(user_id,role_id,create_date)VALUES<foreach collection='list' item='item' index='index' separator=','>(#{userId},#{item},UNIX_TIMESTAMP(NOW()))</foreach></script>")
	void insertUser2Role(@Param("userId")int userId,@Param("list")List roleId);
	
	/**
	 * 
	 * @param userId
	 * @param roleId
	 */
	@Delete("<script><foreach collection='list' item='item' index='index' separator=';'>Delete from moc_user2role WHERE user_id = #{0} and role_id = #{item}</foreach></script>")
	void deleteUser2Role(int userId,@Param("list")List roleId);
	
	
	//****************************角色权限关联操作******************************
	/**
	 *  增加角色与权限的关联表数据
	 * @param roleId
	 * @param list
	 */
	@Insert("<script>INSERT INTO moc_per2role(role_id,permission_id,create_date)VALUES<foreach collection='list' item='item' index='index' separator=','>(#{roleId},#{item},UNIX_TIMESTAMP(NOW()))</foreach></script>")
	void inserPer2Role(@Param("roleId")int roleId,@Param("list")List<Integer> list);
	
	/**
	 * 删除角色关联表数据
	 * @param roleId
	 */
	@Delete("<script><foreach collection='list' item='item' index='index' separator=';'>DELETE FROM moc_per2role WHERE 1=1<if test=\"item.roleId !=null and item.roleId !=''  \"> AND role_id = #{item.roleId}</if><if test=\"item.perId !=null and item.perId !=''  \"> AND permission_id = #{item.perId}</if></foreach></script>")
	void deletePer2Role(@Param("list")List<Per2RoleBo> list);
	
	@Delete("<script><foreach collection='list' item='item' index='index' separator=';'>DELETE FROM moc_per2role WHERE permission_id = #{item}</foreach></script>")
	void deletePermissionByPerId(List<Integer> perId);
	/**
	 * 通过角色id查询出某一个角色所对应的权限
	 * @param roleId
	 * @return
	 */
	@Select("SELECT r.id roleId,r.role_name roleName,r.web_name webName,pr.id per2RoleId,pr.permission_id perId FROM moc_role r,moc_per2role pr WHERE r.id = pr.role_id AND pr.role_id = #{roleId}")
	List<Per2RoleBo> selectPer2Role(int roleId);
	
	//这里暂时用不到，因为修改角色权限只有删除和增加
	@Update("<script><foreach collection='list' item='item' index='index' separator=';'>UPDATE moc_per2role SET permission_id = #{item.perId},last_update_date = UNIX_TIMESTAMP(NOW()) WHERE id = #{item.per2RoleId} AND role_id = #{item.roleId}</foreach></script>")
	void updatePer2Role(@Param("list")List<Per2RoleBo> list);
}
