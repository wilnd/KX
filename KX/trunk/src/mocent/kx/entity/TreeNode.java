package mocent.kx.entity;

public class TreeNode {

	private int id;
	private String name;
	private Boolean isParent =true;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(int num) {
		if(num > 0){
			isParent=true;
		}else{
			isParent=false;
		}
	}
}
