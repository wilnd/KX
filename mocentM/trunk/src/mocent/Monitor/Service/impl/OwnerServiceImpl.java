package mocent.Monitor.Service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import mocent.Monitor.Dao.OwnerDao;
import mocent.Monitor.Entity.Owner;
import mocent.Monitor.Service.OwnerService;

@Service
public class OwnerServiceImpl extends BaseServiceImpl<Owner, Integer> implements OwnerService{

	@Resource
	private OwnerDao ownerDao;
	
	@Resource
	private void setOwnerDao(OwnerDao ownerDao)
	{
		super.setBaseDao(ownerDao);
	}
	/**
	 * 查询车主信息
	 */
	public Owner getOwnerByPhone(String phone)
	{
		return ownerDao.getOwnerByPhone(phone);
	}
}
