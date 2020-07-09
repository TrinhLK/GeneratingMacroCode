package elements2;

import java.util.ArrayList;

public class TreeNode {

	private TreeNode parent;
	private String content;
	private String componentTypeName; // e.g. Tomcat
	private String portTypeName; // e.g. start
	private boolean trigger; // true means the node is a trigger, false means the node is a synchron
	private boolean canRemove;
	private ArrayList<TreeNode> children; // null means the node is a leaf, i.e. a port
	
	public TreeNode(String _content, boolean _trigger, TreeNode _parent) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
		parent = _parent;
		canRemove = false;
		children = new ArrayList<TreeNode>();
	}
	
	public String removeBrackets(String content) {
		return content.replaceAll("`|\\*|\\)|\\(", "");
	}
	
	public void setNameAndAction(String _content) {
		String[] sp = _content.replaceAll("`|\\*|\\)|\\(", "").split("\\.");
		componentTypeName = sp[0];
		portTypeName = sp[1];
	}
	
	public void print() {
		if (trigger) {
			System.out.println(componentTypeName + "_" + portTypeName + "\t trigger \tparent:" + parent);
		} else {
			System.out.println(componentTypeName + "_" + portTypeName + "\t synchron \tparent:" + parent);
		}
	}
	
	public String toString() {
		return componentTypeName + "_" + portTypeName;
	}
		
	public void markReduceTree() {
		if (parent != null) {
			if (allChildrenAreSync() && !hasCompoundInChildren() && parent.allChildrenAreSync()) {
				
				if (getContent().matches("c[0-9]+.null")) {
					this.canRemove = true;
				}
				for (TreeNode child : children) {
					child.setParent(parent);
				}
			}
		}	
		for (TreeNode leaf : children) {
			leaf.markReduceTree();
		}
	}
	
	public void traversal() {
		if (canRemove == false) {
			if (parent != null) {
				System.out.println(content + "\t" + isTrigger() + "\t" + parent.getContent() + "\trm: " + canRemove);
			}else {
				System.out.println(content + "\t" + isTrigger() + "\trm: " + canRemove);
			}
		}
		
		for (TreeNode leaf : children) {
			leaf.traversal();
//			System.out.println(leaf.getContent());
		}
	}
	
	/**
	 * Getters & Setters
	 * */
	public TreeNode getParent() {
		return parent;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getComponentTypeName() {
		return componentTypeName;
	}
	public void setComponentTypeName(String componentTypeName) {
		this.componentTypeName = componentTypeName;
	}
	public String getPortTypeName() {
		return portTypeName;
	}
	public void setPortTypeName(String portTypeName) {
		this.portTypeName = portTypeName;
	}
	public boolean isTrigger() {
		return trigger;
	}
	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}
	public ArrayList<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}
	
	public boolean isCanRemove() {
		return canRemove;
	}

	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}

	public void addListChildren(ArrayList<TreeNode> children) {
		for (TreeNode child : children) {
			this.children.add(child);
		}
	}
    public boolean allChildrenAreSync() {
    	for (TreeNode child : children) {
    		if (child.isTrigger())
    			return false;
    	}
    	return true;
    }
    public boolean allChildrenAreTrigger() {
    	for (TreeNode child : children) {
    		if (!child.isTrigger())
    			return false;
    	}
    	return true;
    }
    public boolean hasCompoundInChildren() {
    	for (TreeNode child : children) {
    		if (child.getContent().contains("c.null"))
    			return true;
    	}
    	return false;
    }
}
