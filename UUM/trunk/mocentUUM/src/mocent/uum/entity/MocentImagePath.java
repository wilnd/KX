package mocent.uum.entity;

public class MocentImagePath {

	private int id;
	private String kxImageUrl;
	private String kxTextUrl;
	private int kxId;
	private int carVerId;
	private String linuxUrl;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKxImageUrl() {
		return kxImageUrl;
	}
	public void setKxImageUrl(String kxImageUrl) {
		this.kxImageUrl = kxImageUrl;
	}
	public String getKxTextUrl() {
		return kxTextUrl;
	}
	public void setKxTextUrl(String kxTextUrl) {
		this.kxTextUrl = kxTextUrl;
	}
	public int getKxId() {
		return kxId;
	}
	public void setKxId(int kxId) {
		this.kxId = kxId;
	}
	public int getCarVerId() {
		return carVerId;
	}
	public void setCarVerId(int carVerId) {
		this.carVerId = carVerId;
	}
	public String getLinuxUrl() {
		return linuxUrl;
	}
	public void setLinuxUrl(String linuxUrl) {
		this.linuxUrl = linuxUrl;
	}
	@Override
	public String toString() {
		return "MocentImagePath [id=" + id + ", kxImageUrl=" + kxImageUrl
				+ ", kxTextUrl=" + kxTextUrl + ", kxId=" + kxId + ", carVerId="
				+ carVerId + ", linuxUrl=" + linuxUrl + "]";
	}
	
	
}
