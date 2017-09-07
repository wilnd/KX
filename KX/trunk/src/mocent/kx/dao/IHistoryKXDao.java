package mocent.kx.dao;

import java.util.List;

import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.entity.MocentRecord;
import mocent.kx.entity.TreeNode;

import org.apache.ibatis.annotations.Param;

public interface IHistoryKXDao {

	 public List<TreeNode> getNodes(@Param("userId")Integer userId);
	 
	 public List<TreeNode> getNodesByYear(@Param("year")int year,@Param("userId")Integer userId);
	 
	 public  List<TreeNode> getNodesByYearAndMonth(@Param("year")int year,@Param("month")int month,@Param("userId")Integer userId);
	 
	 public List<TreeNode> getNodesByYearAndNonthAndDay(@Param("year")int year,@Param("month")int month,@Param("day")int day,@Param("userId")Integer userId);
	 
	 public MocentKX findKXById(int kxId);
	 
	 public MocentImagePath findKXImage(int kxId);
	 
	 public List<MocentKX> findKXByUserId(@Param("userId")int userId,@Param("state")int state,@Param("startLimt")int startLimt,@Param("pageSize")int pageSize);
	 
	 public int findKXByUserIdCount(@Param("userId")int userId,@Param("state")int state);
	 
	 public int updateKXState(@Param("list")List<Integer> listId,@Param("state") int state);
	 
	 public int delAllInfoKXId(@Param("list")List<Integer> listId);
	 
	 public int addRecord(List<MocentRecord> record);
	 
}
