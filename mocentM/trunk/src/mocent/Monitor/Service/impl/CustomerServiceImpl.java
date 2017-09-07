package mocent.Monitor.Service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import mocent.Monitor.Dao.CustomerDao;
import mocent.Monitor.Entity.Customer;
import mocent.Monitor.Service.CustomerService;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer> implements CustomerService{

	@Resource
	private CustomerDao customerDao;
	
	@Resource
	private void setCustomerDao(CustomerDao customerDao)
	{
		super.setBaseDao(customerDao);
	}
}
