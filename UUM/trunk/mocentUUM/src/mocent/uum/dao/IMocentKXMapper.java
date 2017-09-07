package mocent.uum.dao;

import java.util.List;

import mocent.uum.entity.MocentImagePath;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface IMocentKXMapper {

	@Select("SELECT id as id,kx_image_url as kxImageUrl,kx_text_url as kxTextUrl,kx_id	as kxId,car_ver_id 	as  carVerId from moc_image_path where kx_id=#{0}")
	public List<MocentImagePath> findImagePath(int kxId);
	
	@Update("<script>update moc_kx set kx_state=#{state} where id in <foreach collection=\"list\" item = \"model\" open=\"(\" separator=\",\" close=\")\">#{model} </foreach></script>")
	public int updateKXState(@Param("list")List<Integer> listId,@Param("state") int state);
}
