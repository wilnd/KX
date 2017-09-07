package mocent.kx.service;


import java.util.List;
import java.util.Map;

import mocent.kx.entity.CarInfo;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;

public interface ISendKXService {
	public PageUtil<CarInfo> findCarByKXId(int kxId,int isOnLine);
	
	public Map<String,Object> addDownTask(Integer kxId,List<Integer> carId);
	
	public List<MocentImagePath> findImageByKx(int kxId);
	
	public PageUtil<MocentKX> findKXBySended(int userId,String permissionList,BSPage page);
	
	public int updateKXState(List<Integer> listId,Integer state,Integer userId);
}
