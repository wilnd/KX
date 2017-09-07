package mocent.Monitor.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * 车主信息
 * @author Administrator
 * 
 */

@Entity
@Table(name="owner")
public class Owner {

	private int id;
	
	private String mobile_phone;
	
	private String pwd;
	
	private int is_female;
	
	private String name;
	
	private String id_number;
	
	private String login_pwd;
	
	private String imei;
	
	private String head_img;
	
	private String nick_name;
	
	private int is_android;
	
	private int ver;

	@Id
	@Column(name="id", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="mobile_phone", unique=false, nullable=false)
	public String getMobile_phone() {
		return mobile_phone;
	}

	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	@Column(name="pwd", unique=false, nullable=false)
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Column(name="is_female", unique=false, nullable=false)
	public int getIs_female() {
		return is_female;
	}

	public void setIs_female(int is_female) {
		this.is_female = is_female;
	}

	@Column(name="name", unique=false, nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="id_number", unique=false, nullable=false)
	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	@Column(name="login_pwd", unique=false, nullable=true)
	public String getLogin_pwd() {
		return login_pwd;
	}

	public void setLogin_pwd(String login_pwd) {
		this.login_pwd = login_pwd;
	}

	@Column(name="imei", unique=false, nullable=false)
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	@Column(name="head_img", unique=false, nullable=true)
	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	@Column(name="nick_name", unique=false, nullable=true)
	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	@Column(name="is_android", unique=false, nullable=false)
	public int getIs_android() {
		return is_android;
	}

	public void setIs_android(int is_android) {
		this.is_android = is_android;
	}

	@Column(name="ver", unique=false, nullable=false)
	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}
}
