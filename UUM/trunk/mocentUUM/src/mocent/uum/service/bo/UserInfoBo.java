package mocent.uum.service.bo;

import java.math.BigInteger;
import java.util.List;

/**
 * 
 * @author jiang
 * @date 2016年9月29日  下午4:14:13
 *
 */
public class UserInfoBo {
	
	private int id; //id
	private String userName; //用户名
	private String webName; //web名
	private BigInteger lastLoginDate; //最后登录时间
	private String role; //角色名
	private String permission; //权限
	private String statusCode; //消息码
	private String message; //消息
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getWebName() {
		return webName;
	}
	public void setWebName(String webName) {
		this.webName = webName;
	}
	public BigInteger getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(BigInteger lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString(){
		return "userName："+getUserName()+" webName："+getWebName()+" lastLoginDate："+getLastLoginDate()+"role："+getRole()+" permission："+getPermission()+"statusCode："+getStatusCode()+"message："+getMessage();
	}
}
