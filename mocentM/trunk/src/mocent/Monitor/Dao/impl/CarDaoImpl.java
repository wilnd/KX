package mocent.Monitor.Dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import mocent.Monitor.Dao.CarDao;
import mocent.Monitor.Entity.Car;
import mocent.Monitor.Util.StringUtil;

@Repository
public class CarDaoImpl extends BaseDaoImpl<Car, Integer> implements CarDao{
	/**
	 * 查询车辆详细信息
	 */
	public Car searchCarInfo(Map<String, String> conMap)
	{
		Car car = new Car(); 
		String hql = "FROM Car car WHERE 1=1"; //加上1=1没有实际意义
		try
		{
			if(conMap.size() > 0)
			{
				if(!StringUtil.isNullOrEmpty(conMap.get("plateNumber")))
				{
					hql += " AND car.plate_number=:plate";
				}
				if(!StringUtil.isNullOrEmpty(conMap.get("sim")))
				{
					hql += " AND car.sim like :simNumber";
				}
				if(!StringUtil.isNullOrEmpty(conMap.get("cur_owner_id")))
				{
					hql += " AND car.cur_owner_id=:ownerId";
				}
				if(!StringUtil.isNullOrEmpty(conMap.get("id")))
				{
					hql += " AND car.id=:carId";
				}
				Query query = getSession().createQuery(hql);
				if(!StringUtil.isNullOrEmpty(conMap.get("plateNumber")))
				{
					query.setParameter("plate", conMap.get("plateNumber"));
				}
				if(!StringUtil.isNullOrEmpty(conMap.get("sim")))
				{
					query.setParameter("simNumber", '%' + conMap.get("sim") + '%');
				}
				if(!StringUtil.isNullOrEmpty(conMap.get("cur_owner_id")))
				{
					query.setParameter("ownerId", Integer.valueOf(conMap.get("cur_owner_id")));
				}
				if(!StringUtil.isNullOrEmpty(conMap.get("id")))
				{
					query.setParameter("carId", Integer.valueOf(conMap.get("id")));
				}
				car =(Car)query.list().get(0);
			}
			else
			{
				car = null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return car;
	}
	
	@SuppressWarnings("unchecked")
	public List<Car> loadOfflineCars() 
	{
		String hql = "FROM Car car where car.is_online=0 order by register_time desc";
		List<Car> carList = new ArrayList<Car>();
		Query query = getSession().createQuery(hql);
		carList = query.list();
		return carList;
	}
	
	public List findCarByNum(Map<String,String> map) throws Exception{
		Query query = getSession().createSQLQuery(findCarByNumSql(map,false));
		return query.list();
	}
	
	public Integer findCarByNumCount(Map<String,String> map) throws Exception{
		Query query = getSession().createSQLQuery(findCarByNumSql(map,true));
		return Integer.parseInt(query.list().get(0).toString());
	}
	public String findCarByNumSql(Map<String,String> map,Boolean isCount) throws Exception{
		   String sql="";
			if(isCount){
				sql="SELECT count(1)";
			}else{
				sql="SELECT c.plate_number,c.sim,c.cur_owner_name,o.mobile_phone,c.is_online,c.lst_pos_time,c.ver_62,c.ver_72,c.ver_77,ver_map";
			}
		sql += " FROM car c LEFT JOIN owner o ON c.cur_owner_id = o.id WHERE 1=1  "; 
		if(map.size()>0){
			if(!StringUtil.isNullOrEmpty(map.get("simNum"))){
				sql+=" and c.sim like '%"+map.get("simNum")+"%'";
			}
			if(!StringUtil.isNullOrEmpty(map.get("carNum"))){
				sql+=" and c.plate_number like '%"+map.get("carNum")+"%'";
			}
			if(!StringUtil.isNullOrEmpty(map.get("phoneNum"))){
				sql+=" and o.mobile_phone like '%"+map.get("phoneNum")+"%'";
			}
			if(!StringUtil.isNullOrEmpty(map.get("pageIndex"))){
				int pageIndex=Integer.valueOf(map.get("pageIndex"));
				int pageNum=Integer.valueOf(map.get("pageNum"));
				sql+=" LIMIT "+(pageIndex-1)*pageNum+","+pageNum+"";
			}
		}
		return sql;
	}
}
