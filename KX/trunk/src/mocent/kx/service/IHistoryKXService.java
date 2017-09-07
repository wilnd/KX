package mocent.kx.service;

import java.util.List;
import java.util.Map;

import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.entity.TreeNode;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;

public interface IHistoryKXService {

	public List<TreeNode> getNodes(Integer id,Integer year,Integer month,Integer day,Integer userId);
	
	public MocentKX findKXById(int kxId);
	
	public MocentImagePath findKXImage(int kxId);
	
	public PageUtil<MocentKX> findKXByUserId(int userId,String permissionList,BSPage page);
	
	public int updateKXState(List<Integer> listId,Integer state);
	
	public PageUtil<MocentKX> findWillSend(BSPage page);
	
	public Map<String,Object> deleteKX(List<Integer> listId);
	
}
