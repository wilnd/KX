package mocent.Monitor.Entity;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 车辆信息，很多用不到的字段，只是为了跟数据库保持一致
 * @author Administrator
 *
 */
@Entity
@Table(name="car")
public class Car {
	private int id;
	
	private String sim;
	
	private String sn;
	
	private int cur_owner_id;
	
	private int cur_owner_is_female;
	
	private String cur_owner_name;
	
	private String real_owner_id_number;
	
	private String real_owner_name;
	
	private int real_owner_is_female;
	
	private String plate_number;
	
	private int car_type_id;
	
	private String city;
	
	private String color;
	
	private String ver_77;
	
	private String ver_72;
	
	private String ver_62;
	
	private String ver_map;
	
	private String unique_code;
	
	private String resolution;
	
	private int register_time;
	
	private String salewaysnum;
	
	private int lst_longitude;
	
	private int lst_latitude;
	
	private int lst_pos_time;
	
	private int alert_type;
	
	private int alert_is_formal;
	
	private int alert_time;
	
	private int lst_login_time;
	
	private int lst_logout_time;
	
	private int is_online;
	
	private int engin_started;
	
	private Blob conf;
	
	private String front_img_name;
	
	private String back_img_name;
	
	private String frame_img_name;
	
	private String szm_img_name;
	
	private String chair_img_name;
	
	private String plate_img_name;

	@Id
	@Column(name="id", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="sim", unique=false, nullable=false)
	public String getSim() {
		return sim;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}

	@Column(name="sn", unique=false, nullable=false)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(name="cur_owner_id", unique=false, nullable=false)
	public int getCur_owner_id() {
		return cur_owner_id;
	}

	public void setCur_owner_id(int cur_owner_id) {
		this.cur_owner_id = cur_owner_id;
	}

	@Column(name="cur_owner_is_female", unique=false, nullable=false)
	public int getCur_owner_is_female() {
		return cur_owner_is_female;
	}

	public void setCur_owner_is_female(int cur_owner_is_female) {
		this.cur_owner_is_female = cur_owner_is_female;
	}

	@Column(name="cur_owner_name", unique=false, nullable=false)
	public String getCur_owner_name() {
		return cur_owner_name;
	}

	public void setCur_owner_name(String cur_owner_name) {
		this.cur_owner_name = cur_owner_name;
	}

	@Column(name="real_owner_id_number", unique=false, nullable=false)
	public String getReal_owner_id_number() {
		return real_owner_id_number;
	}

	public void setReal_owner_id_number(String real_owner_id_number) {
		this.real_owner_id_number = real_owner_id_number;
	}

	@Column(name="real_owner_name", unique=false, nullable=false)
	public String getReal_owner_name() {
		return real_owner_name;
	}

	public void setReal_owner_name(String real_owner_name) {
		this.real_owner_name = real_owner_name;
	}

	@Column(name="real_owner_is_female", unique=false, nullable=false)
	public int getReal_owner_is_female() {
		return real_owner_is_female;
	}

	public void setReal_owner_is_female(int real_owner_is_female) {
		this.real_owner_is_female = real_owner_is_female;
	}

	@Column(name="plate_number", unique=false, nullable=false)
	public String getPlate_number() {
		return plate_number;
	}

	public void setPlate_number(String plate_number) {
		this.plate_number = plate_number;
	}

	@Column(name="car_type_id", unique=false, nullable=false)
	public int getCar_type_id() {
		return car_type_id;
	}

	public void setCar_type_id(int car_type_id) {
		this.car_type_id = car_type_id;
	}

	@Column(name="city", unique=false, nullable=false)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name="color", unique=false, nullable=false)
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Column(name="ver_77", unique=false, nullable=false)
	public String getVer_77() {
		return ver_77;
	}

	public void setVer_77(String ver_77) {
		this.ver_77 = ver_77;
	}
	@Column(name="ver_72", unique=false, nullable=false)
	public String getVer_72() {
		return ver_72;
	}

	public void setVer_72(String ver_72) {
		this.ver_72 = ver_72;
	}
	
	@Column(name="ver_map", unique=false, nullable=false)
	public String getVer_map() {
		return ver_map;
	}

	public void setVer_map(String ver_map) {
		this.ver_map = ver_map;
	}
	
	@Column(name="unique_code", unique=false, nullable=false)
	public String getUnique_code() {
		return unique_code;
	}

	public void setUnique_code(String unique_code) {
		this.unique_code = unique_code;
	}
	@Column(name="ver_62", unique=false, nullable=false)
	public String getVer_62() {
		return ver_62;
	}

	public void setVer_62(String ver_62) {
		this.ver_62 = ver_62;
	}

	@Column(name="resolution", unique=false, nullable=false)
	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	@Column(name="register_time", unique=false, nullable=false)
	public int getRegister_time() {
		return register_time;
	}

	public void setRegister_time(int register_time) {
		this.register_time = register_time;
	}

	@Column(name="salewaysnum", unique=false, nullable=false)
	public String getSalewaysnum() {
		return salewaysnum;
	}

	public void setSalewaysnum(String salewaysnum) {
		this.salewaysnum = salewaysnum;
	}

	@Column(name="lst_longitude", unique=false, nullable=false)
	public int getLst_longitude() {
		return lst_longitude;
	}

	public void setLst_longitude(int lst_longitude) {
		this.lst_longitude = lst_longitude;
	}

	@Column(name="lst_latitude", unique=false, nullable=false)
	public int getLst_latitude() {
		return lst_latitude;
	}

	public void setLst_latitude(int lst_latitude) {
		this.lst_latitude = lst_latitude;
	}

	@Column(name="lst_pos_time", unique=false, nullable=false)
	public int getLst_pos_time() {
		return lst_pos_time;
	}

	public void setLst_pos_time(int lst_pos_time) {
		this.lst_pos_time = lst_pos_time;
	}

	@Column(name="alert_type", unique=false, nullable=false)
	public int getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(int alert_type) {
		this.alert_type = alert_type;
	}

	@Column(name="alert_is_formal", unique=false, nullable=false)
	public int getAlert_is_formal() {
		return alert_is_formal;
	}

	public void setAlert_is_formal(int alert_is_formal) {
		this.alert_is_formal = alert_is_formal;
	}

	@Column(name="alert_time", unique=false, nullable=false)
	public int getAlert_time() {
		return alert_time;
	}

	public void setAlert_time(int alert_time) {
		this.alert_time = alert_time;
	}

	@Column(name="lst_login_time", unique=false, nullable=false)
	public int getLst_login_time() {
		return lst_login_time;
	}

	public void setLst_login_time(int lst_login_time) {
		this.lst_login_time = lst_login_time;
	}

	@Column(name="lst_logout_time", unique=false, nullable=false)
	public int getLst_logout_time() {
		return lst_logout_time;
	}

	public void setLst_logout_time(int lst_logout_time) {
		this.lst_logout_time = lst_logout_time;
	}

	@Column(name="is_online", unique=false, nullable=false)
	public int getIs_online() {
		return is_online;
	}

	public void setIs_online(int is_online) {
		this.is_online = is_online;
	}

	@Column(name="engin_started", unique=false, nullable=false)
	public int getEngin_started() {
		return engin_started;
	}

	public void setEngin_started(int engin_started) {
		this.engin_started = engin_started;
	}

	@Column(name="conf", unique=false, nullable=false)
	public Blob getConf() {
		return conf;
	}

	public void setConf(Blob conf) {
		this.conf = conf;
	}
	
	@Column(name="front_img_name", unique=false, nullable=true)
	public String getFront_img_name() {
		return front_img_name;
	}

	public void setFront_img_name(String front_img_name) {
		this.front_img_name = front_img_name;
	}

	@Column(name="back_img_name", unique=false, nullable=true)
	public String getBack_img_name() {
		return back_img_name;
	}

	public void setBack_img_name(String back_img_name) {
		this.back_img_name = back_img_name;
	}

	@Column(name="frame_img_name", unique=false, nullable=true)
	public String getFrame_img_name() {
		return frame_img_name;
	}

	public void setFrame_img_name(String frame_img_name) {
		this.frame_img_name = frame_img_name;
	}

	@Column(name="szm_img_name", unique=false, nullable=true)
	public String getSzm_img_name() {
		return szm_img_name;
	}

	public void setSzm_img_name(String szm_img_name) {
		this.szm_img_name = szm_img_name;
	}

	@Column(name="chair_img_name", unique=false, nullable=true)
	public String getChair_img_name() {
		return chair_img_name;
	}

	public void setChair_img_name(String chair_img_name) {
		this.chair_img_name = chair_img_name;
	}
	
	@Column(name="plate_img_name", unique=false, nullable=true)
	public String getPlate_img_name() {
		return plate_img_name;
	}

	public void setPlate_img_name(String plate_img_name) {
		this.plate_img_name = plate_img_name;
	}
}
