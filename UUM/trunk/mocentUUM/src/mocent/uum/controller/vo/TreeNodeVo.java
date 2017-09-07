package mocent.uum.controller.vo;

public class TreeNodeVo {
	private int id;
	private String menuName;
	private String childrenName;

	private Boolean isParent = true;
	
	public String getChildrenName() {
		return childrenName;
	}
	public void setChildrenName(String childrenName) {
		this.childrenName = childrenName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Integer num) {
		if(null !=num && num>0){
			this.isParent=true;
		}else{
			this.isParent=false;
		}
	}
}
