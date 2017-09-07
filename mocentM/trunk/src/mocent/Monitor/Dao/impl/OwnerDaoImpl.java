package mocent.Monitor.Dao.impl;

import org.springframework.stereotype.Repository;

import mocent.Monitor.Dao.OwnerDao;
import mocent.Monitor.Entity.Owner;

@Repository
public class OwnerDaoImpl extends BaseDaoImpl<Owner, Integer> implements OwnerDao{
	/**
	 * 查询车主信息
	 */
	public Owner getOwnerByPhone(String phone)
	{
		String hql = "FROM Owner owner WHERE owner.mobile_phone like ?";
		Owner owner = (Owner)getSession().createQuery(hql).setParameter(0, '%' + phone + '%').uniqueResult();
		return owner;
	}
}
