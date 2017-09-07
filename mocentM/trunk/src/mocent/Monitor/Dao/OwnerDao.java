package mocent.Monitor.Dao;

import mocent.Monitor.Entity.Owner;

public interface OwnerDao extends BaseDao<Owner, Integer>{
	
	/**
	 * 根据车主手机号码查询车主信息
	 * @param phone
	 * @return
	 */
	public Owner getOwnerByPhone(String phone);
}
