package elements2;

import java.util.ArrayList;

public class TreeNode {

	private TreeNode parent;
	private String content;
	private String componentTypeName; // e.g. Tomcat
	private String portTypeName; // e.g. start
	private boolean trigger; // true means the node is a trigger, false means the node is a synchron
	private boolean canRemove;
	private ArrayList<TreeNode> export; // null means the node is a leaf, i.e. a port
	private ArrayList<TreeNode> children; // null means the node is a leaf, i.e. a port
	
	public TreeNode(String _content, boolean _trigger, TreeNode _parent) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
		parent = _parent;
		canRemove = false;
		children = new ArrayList<TreeNode>();
		export = new ArrayList<TreeNode>();
	}
	
	public TreeNode(String _content, boolean _trigger) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
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
	
	/**
	 * generate Macro code for RENDEZVOUS connector 
	 * */
	public String generateRendezConnector(ArrayList<TreeNode> listSynch) {
		String result = "";
		for (int i=0 ; i<listSynch.size() ; i++) {
			TreeNode portI = listSynch.get(i);
			result += "\t\tport(" + portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\")"
					+ ".requires(";
			for (int j=0 ; j<listSynch.size() ; j++) {
				if (i!=j) {
					TreeNode portJ = listSynch.get(j);
					String s = portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\"";
					if (i != listSynch.size() - 1) {
						if (j != listSynch.size() - 1) {
							result += s + ", ";
						} else {
							result += s + ");\n";
						}
					} else {
						if (j != listSynch.size() - 2) {
							result += s + ", ";
						} else {
							result += s + ");\n";
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * generate Macro code for BROADCAST connector 
	 * */
	public String generateBroadcastConnector(ArrayList<TreeNode> listSynch, ArrayList<TreeNode> listTriggers) {
		String result = "";
		for (TreeNode synch : listSynch) {				
			for (TreeNode triggers : listTriggers) {
				/**
				 * 1. triggers & synch are not compound
				 * -> flat broadcast
				 * */
				if (!triggers.isCompound() && !synch.isCompound()) {
					result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
							+ ".requires(" + triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
				}
				
				/**
				 * 2. synch compound, !trigger
				 * -> exportSynch_i requires trig
				 * */
				if (!triggers.isCompound() && synch.isCompound()) {
					for (TreeNode epSynch : synch.getExport()) {
						result += "\t\tport(" + epSynch.getComponentTypeName() + "Connector.class, \"" + epSynch.getPortTypeName() + "\")"
								+ ".requires(" + triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
					}
				}
				
				/**
				 * 3. !synch but trigger
				 * -> synch requires trig_i
				 * */
				if (triggers.isCompound() && !synch.isCompound()) {
					String tempTrig = "";
					for (int i=0 ; i<triggers.getExport().size()-1 ; i++) {
						tempTrig += triggers.getExport().get(i).getComponentTypeName() + "Connector.class, \"" 
								+ triggers.getExport().get(i).getPortTypeName() + "\", ";
					}
					tempTrig += triggers.getExport().get(triggers.getExport().size()-1).getComponentTypeName() + "Connector.class, \"" 
							+ triggers.getExport().get(triggers.getExport().size()-1).getPortTypeName();
					result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
							+ ".requires(" + tempTrig + "\");\n";
				}
				
				/**
				 * 4. synch & trigger are compound
				 * -> synch_i requires trig_i
				 * */
				if (triggers.isCompound() && synch.isCompound()) {
					for (TreeNode epSynch : synch.getExport()) {
						for (TreeNode epTrig : triggers.getExport()) {
							result += "\t\tport(" + epSynch.getComponentTypeName() + "Connector.class, \"" + epSynch.getPortTypeName() + "\")"
									+ ".requires(" + epTrig.getComponentTypeName() + "Connector.class, \"" + epTrig.getPortTypeName() + "\");\n";
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * generate Macro code for ATOMIC BROADCAST connector 
	 * */
	public String generateAtomicBroadcastConnector(ArrayList<TreeNode> listSynch) {
		String result = "";
		ArrayList<TreeNode> listAboveTrig = getListTriggers(parent.getChildren());
//		System.out.println("trig size: " + listAboveTrig.size());
		for (TreeNode trig : listAboveTrig) {
			
			if (!this.isTrigger()) {
				for (int i=0 ; i<listSynch.size() ; i++) {
					TreeNode portI = listSynch.get(i);
					result += "\t\tport(" + portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\")"
							+ ".requires(";
					for (int j=0 ; j<listSynch.size() ; j++) {
						if (i != j) {
							TreeNode portJ = listSynch.get(j);
							result += portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\", ";
							if (!trig.isCompound()) {
								result += trig.getComponentTypeName() + "Connector.class, \"" + trig.getPortTypeName() + "\");\n";
							}else {
								ArrayList<TreeNode> epPorts = trig.getExport();
								for (int k=0 ; k<epPorts.size()-1 ; k++) {
									result += epPorts.get(k).componentTypeName + "Connector.class, \"" + epPorts.get(k).getPortTypeName() + "\", ";
								}
								result += epPorts.get(epPorts.size()-1).getComponentTypeName() 
										+ "Connector.class, \"" + epPorts.get(epPorts.size()-1).getPortTypeName() + "\");\n";
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public String printMacro(String result) {
		//String result = "";
		for (TreeNode leaf : children) {
			result = leaf.printMacro(result);
		}
		
		if (children.size() > 0) {
			ArrayList<TreeNode> listTriggers = getListTriggers(children);
			ArrayList<TreeNode> listSynch = getListSynch(children);
			
			if (listTriggers.size() == 0) {
				
				//ATOMIC BROADCAST
				if (parent!=null && !parent.allChildrenAreSync()){
					result += generateAtomicBroadcastConnector(listSynch);
					System.out.println("atomic: " + this.getContent());
				}//RENDEZVOUS
				else {
					result += generateRendezConnector(listSynch);
					System.out.println("rendez");
				}				
			}	//BROADCAST
			else {
				result += generateBroadcastConnector(listSynch, listTriggers);
				System.out.println("brc");
				for (TreeNode trig : listTriggers) {
					if (trig.isCompound() && !trig.getExport().get(0).isTrigger()) {
						System.out.println("brc1");
						result += generateRendezConnector(trig.getExport());
					}
				}
			}
		}
		return result;		
	}
	/**
	 * mark elements should be reduced
	 * */
	public void markReduceTree() {
		if (parent != null) {
			if (allChildrenAreTrigger() && !hasCompoundInChildren()) {
				if (getContent().matches("c[0-9]+.null")) {
					this.canRemove = true;
				}
				for (TreeNode child : children) {
					child.setParent(parent);
				}
			}
			
			if (allChildrenAreSync() && !hasCompoundInChildrenAndCanRemove() && parent.allChildrenAreSync()) {
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
		if (parent != null) {
			System.out.println(content + "\t" + isTrigger() + "\t" + parent.getContent() + "\trm: " + canRemove);
		}else {
			System.out.println(content + "\t" + isTrigger() + "\trm: " + canRemove);
		}
		
		for (TreeNode leaf : children) {
			leaf.traversal();
		}
	}
	
	/**
	 * Add all triggers children into the list export 
	 * */
	public void addExportedPort() {
		//case1: Synchron compound with some trigger children -> exportedPort = all trigger children
		//case2: Trigger compound with some trigger children -> exportedPort = all trigger children
		//compound with one or more trigger children
		if(this.isCompound()) {
			for (TreeNode child : getChildren()) {
				if (child.isTrigger()) {
					export.add(child);
				}
			}
		}
		//case3: Trigger compound with no trigger children -> exportedPort = all synch children
		//case4: Sync compound with no trigger children -> reduced, no need to care
		if(this.isCompound() && this.isTrigger() && this.allChildrenAreSync()) {
			for (TreeNode child : getChildren()) {
				export.add(child);
			}
		}
		for (TreeNode child : children) {
			child.addExportedPort();
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
	
	public ArrayList<TreeNode> getExport() {
		return export;
	}

	public void setExport(ArrayList<TreeNode> export) {
		this.export = export;
	}

	/**
	 * Supporting functions
	 * ----------------------------------------
	 * */
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
    public boolean hasCompoundInChildrenAndCanRemove() {
    	for (TreeNode child : children) {
    		if (child.isCompound() && child.isCanRemove()) {
    			System.out.println("check content of child " + child.getContent());
    			return true;
    		}
    	}
    	return false;
    }
    public boolean hasCompoundInChildren() {
    	for (TreeNode child : children) {
    		if (child.isCompound()) {
    			System.out.println("check content of compound " + child.getContent());
    			return true;
    		}
    	}
    	return false;
    }
    public boolean isCompound() {
    	if (content.matches("c[0-9]+.null")) {
    		return true;
    	}
    	return false;
    }
    
    public ArrayList<TreeNode> getListTriggers(ArrayList<TreeNode> input){
    	ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
    	for (TreeNode child : input) {
    		if (child.isTrigger()) {
    			rs.add(child);
    		}
    	}
    	return rs;
    }
    
    public ArrayList<TreeNode> getListNotCompoundTriggers(ArrayList<TreeNode> input){
    	ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
    	for (TreeNode child : input) {
    		if (child.isTrigger() && !child.isCompound()) {
    			rs.add(child);
    		}
    	}
    	return rs;
    }
    
    public ArrayList<TreeNode> getListSynch(ArrayList<TreeNode> input){
    	ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
    	for (TreeNode child : input) {
    		//if (!child.isTrigger() && !child.getContent().matches("c[0-9]+.null")) {
    		if (!child.isTrigger()) {
    			if (child.isCompound()) {
    				for (TreeNode ep : child.getExport()) {
    						rs.add(ep);
    				}
    			}else {
    				rs.add(child);
    			}
    		}
    	}
    	return rs;
    }
}
