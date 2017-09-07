package mocent.Monitor.Service;

import mocent.Monitor.Entity.Owner;

public interface OwnerService extends BaseService<Owner, Integer>{

	/**
	 * 查询车主信息
	 * @param phone
	 * @return
	 */
	public Owner getOwnerByPhone(String phone);
}
