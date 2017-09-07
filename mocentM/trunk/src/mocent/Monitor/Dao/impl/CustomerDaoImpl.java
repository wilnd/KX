package mocent.Monitor.Dao.impl;

import org.springframework.stereotype.Repository;

import mocent.Monitor.Dao.CustomerDao;
import mocent.Monitor.Entity.Customer;

@Repository
public class CustomerDaoImpl extends BaseDaoImpl<Customer, Integer> implements CustomerDao{

}
