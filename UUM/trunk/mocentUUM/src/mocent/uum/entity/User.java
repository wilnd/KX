package mocent.uum.entity;

import java.math.BigInteger;

/**
 * 
 * @author jiang
 * @date 2016年9月27日  上午10:39:31
 * @Desc 用户实体类
 *  
 */
public class User {
	
	private int id;  //id
	
	private String username;  //用户名
	
	private String password;  //密码
	
	private BigInteger createTime;  //创建时间
	
	private BigInteger lastLoginDate; //上一次登录时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public BigInteger getCreateTime() {
		return createTime;
	}
	public void setCreateTime(BigInteger createTime) {
		this.createTime = createTime;
	}
	public BigInteger getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(BigInteger lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
	
	
	@Override
	public String toString() {
		return "id："+getId()
				+" username："+getUsername()
				+" password："+getPassword()
				+" createDate："+getCreateTime()
				+" lastLoginDate："+getLastLoginDate();
	}
	
	
}
