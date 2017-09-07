package mocent.kx.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mocent.kx.dao.ICreateKXDao;
import mocent.kx.dao.IHistoryKXDao;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.entity.TreeNode;
import mocent.kx.service.IHistoryKXService;
import mocent.kx.util.BSPage;
import mocent.kx.util.ConfigUtil;
import mocent.kx.util.ImageUtil;
import mocent.kx.util.PageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryKXServiceImpl implements IHistoryKXService {

	@Autowired
	private IHistoryKXDao historyKXDao;
	@Autowired
	private ICreateKXDao createDao;
	
	@Override
	public List<TreeNode> getNodes(Integer id,Integer year,Integer month,Integer day,Integer userId) {
		//只有id有值  说明点击了年            year有值说明点击了月    month有值说明点击了天
		List<TreeNode> list=new ArrayList<TreeNode>();
		if(null !=month && 0 !=(int)month){
			list=historyKXDao.getNodesByYearAndNonthAndDay(year, month, id,userId);
		}else if(null != year && 0 !=(int)year){
			list=historyKXDao.getNodesByYearAndMonth(year, id,userId);
		}else if(null != id && 0 !=(int)id){
			list=historyKXDao.getNodesByYear(id,userId);
		}else{
			list=historyKXDao.getNodes(userId);
		}
		return list;
	}

	@Override
	public MocentKX findKXById(int kxId) {
		return historyKXDao.findKXById(kxId);
	}

	@Override
	public MocentImagePath findKXImage(int kxId) {
		return historyKXDao.findKXImage(kxId);
	}

	@Override
	public PageUtil<MocentKX> findKXByUserId(int userId, String permissionList,BSPage page) {
		 List<MocentKX> list=new ArrayList<MocentKX>();
		 int result=0;
		//0保存，1提交、2未提交、3采纳、4未采纳
		 if(permissionList.indexOf("create,") > -1){
			 result=historyKXDao.findKXByUserIdCount(userId, 0);
			}else if(permissionList.indexOf("commit,") > -1){
				result=historyKXDao.findKXByUserIdCount(0, 2);
			}else if(permissionList.indexOf("review,") > -1){
				result=historyKXDao.findKXByUserIdCount(0, 1);
			}
		 PageUtil<MocentKX> pageResult=new PageUtil<MocentKX>(page.getCurPage(), result);
		 if(result > 0){
			 if(permissionList.indexOf("create,") > -1){
					list=historyKXDao.findKXByUserId(userId, 0,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize());
				}else if(permissionList.indexOf("commit,") > -1){
					list=historyKXDao.findKXByUserId(0, 2,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize());
				}else if(permissionList.indexOf("review,") > -1){
					list=historyKXDao.findKXByUserId(0, 1,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize());
				}
			 pageResult.setData(list);
		 }
		
		return pageResult;
	}

	@Override
	public int updateKXState(List<Integer> listId, Integer state) {
		return historyKXDao.updateKXState(listId, state);
	}

	@Override
	public PageUtil<MocentKX> findWillSend(BSPage page) {
		int  result=historyKXDao.findKXByUserIdCount(0, 5);
		 PageUtil<MocentKX> pageResult=new PageUtil<MocentKX>(page.getCurPage(), result);
		if(result>0){
			pageResult.setData(historyKXDao.findKXByUserId(0, 5, (page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
		}
		return pageResult;
	}

	@Override
	public Map<String, Object> deleteKX(List<Integer> listId) {
		 Map<String, Object> map=new HashMap<String, Object>();
		 for (Integer kxId : listId) {
			 List<MocentImagePath> findImagePath = createDao.findImagePath(kxId);
			 deleteFile(findImagePath);
			 
		} 
		int result=historyKXDao.delAllInfoKXId(listId);
		if(result>0){
			map.put("stateCode", "S");
		}else{
			map.put("stateCode", "E");
		}
		return map;
	}
	
	public void deleteFile(List<MocentImagePath> findImagePath){
		for (final MocentImagePath mocentImagePath : findImagePath) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String url=mocentImagePath.getKxImageUrl();
					int lastIndexOf = url.lastIndexOf("/");
					String autoName=url.substring(lastIndexOf+1, url.lastIndexOf("."));
					ImageUtil.deleteFile(url);
					ImageUtil.deleteFile(url.substring(0,lastIndexOf)+"/"+autoName+"txt");
					ImageUtil.deleteFile(url.substring(0,lastIndexOf)+"/"+autoName+ConfigUtil.cmdSuff);
				}
				
			}).start();
		}
	}

}
