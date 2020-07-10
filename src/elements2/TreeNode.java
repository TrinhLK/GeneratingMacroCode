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
		String accept = result.replaceAll("\\.requires\\(", "\\.accepts\\(");
		return result + "\n" + accept;
	}
	
	/**
	 * generate requires Macro code for BROADCAST connector 
	 * */
	public String generateRequiresBroadcastConnector(ArrayList<TreeNode> listSynch, ArrayList<TreeNode> listTriggers) {
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
				 * -> synch_i accepts trig_i
				 * */
				if (triggers.isCompound() && synch.isCompound()) {
					for (TreeNode epSynch : synch.getExport()) {
						for (TreeNode epTrig : triggers.getExport()) {
							result += "\t\tport(" + epSynch.getComponentTypeName() + "Connector.class, \"" + epSynch.getPortTypeName() + "\")"
									+ ".requires(" + epTrig.getComponentTypeName() + "Connector.class, \"" + epTrig.getPortTypeName() + "\");\n";
						}
					}
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
			}
		}
		
		return result;
	}
	
	/**
	 * generate accepts Macro code for BROADCAST connector 
	 * */
	public String generateAcceptsBroadcastConnector(ArrayList<TreeNode> listSynch, ArrayList<TreeNode> listTriggers) {
		String result = "";
		//Sync accepts Trigs
		for (TreeNode synch : listSynch) {
			if (!synch.isCompound()) {
				
				ArrayList<String> trigList = new ArrayList<String>();
				for (TreeNode triggers : listTriggers) {
					if (!triggers.isCompound()) {
						String temp = triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\"";
						trigList.add(temp);
					}else {
						for (TreeNode trigEx : triggers.getExport()) {
							String temp = trigEx.getComponentTypeName() + "Connector.class, \"" + trigEx.getPortTypeName() + "\"";
							trigList.add(temp);
						}
					}
				}
				if (trigList.size() > 0) {
					result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
							+ ".accepts(";
					for (int i=0 ; i<trigList.size()-1 ; i++) {
						result += trigList.get(i) + ", ";
					}
					result += trigList.get(trigList.size()-1) + ");\n";
				}
//				System.out.println(trigList.size());
				
			}else {
				ArrayList<TreeNode> listSynchExport = synch.getExport();
				for (TreeNode synchEx : listSynchExport) {
					
					ArrayList<String> trigList = new ArrayList<String>();
					for (TreeNode triggers : listTriggers) {
						if (!triggers.isCompound()) {
							String temp = triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\"";
							trigList.add(temp);
						}else {
							for (TreeNode trigEx : triggers.getExport()) {
								String temp = trigEx.getComponentTypeName() + "Connector.class, \"" + trigEx.getPortTypeName() + "\"";
								trigList.add(temp);
							}
						}
					}
					
					if (trigList.size() > 0) {
						result += "\t\tport(" + synchEx.getComponentTypeName() + "Connector.class, \"" + synchEx.getPortTypeName() + "\")"
								+ ".accepts(";
						for (int i=0 ; i<trigList.size()-1 ; i++) {
							result += trigList.get(i) + ", ";
						}
						result += trigList.get(trigList.size()-1) + ");\n";
					}
					
				}
			}
		}
		
		//Sync accepts Trigs
		for (TreeNode trigs : listTriggers) {
			if (!trigs.isCompound()) {
				
				ArrayList<String> syncList = new ArrayList<String>();
				for (TreeNode sync : listSynch) {
					if (!sync.isCompound()) {
						String temp = sync.getComponentTypeName() + "Connector.class, \"" + sync.getPortTypeName() + "\"";
						syncList.add(temp);
					}else {
						for (TreeNode synchEx : sync.getExport()) {
							String temp = synchEx.getComponentTypeName() + "Connector.class, \"" + synchEx.getPortTypeName() + "\"";
							syncList.add(temp);
						}
					}
				}
				
				if (syncList.size() > 0) {
					result += "\t\tport(" + trigs.getComponentTypeName() + "Connector.class, \"" + trigs.getPortTypeName() + "\")"
							+ ".accepts(";
					for (int i=0 ; i<syncList.size()-1 ; i++) {
						result += syncList.get(i) + ", ";
					}
					result += syncList.get(syncList.size()-1) + ");\n";
				}
			}else {
				ArrayList<TreeNode> listTrigsExport = trigs.getExport();
				for (TreeNode trigsEx : listTrigsExport) {
					
					ArrayList<String> syncList = new ArrayList<String>();
					for (TreeNode sync : listSynch) {
						if (!sync.isCompound()) {
							String temp = sync.getComponentTypeName() + "Connector.class, \"" + sync.getPortTypeName() + "\"";
							syncList.add(temp);
						}else {
							for (TreeNode syncEx : sync.getExport()) {
								String temp = syncEx.getComponentTypeName() + "Connector.class, \"" + syncEx.getPortTypeName() + "\"";
								syncList.add(temp);
							}
						}
					}
					if (syncList.size() > 0) {
						result += "\t\tport(" + trigsEx.getComponentTypeName() + "Connector.class, \"" + trigsEx.getPortTypeName() + "\")"
								+ ".accepts(";
						for (int i=0 ; i<syncList.size()-1 ; i++) {
							result += syncList.get(i) + ", ";
						}
						result += syncList.get(syncList.size()-1) + ");\n";
					}
					
				}
			}
		}
		
		return result;
	}
	
	/**
	 * generate require Macro code for ATOMIC BROADCAST connector 
	 * */
	public String generateRequireAtomicBroadcastConnector(ArrayList<TreeNode> listSynch) {
		String result = "";
		ArrayList<TreeNode> listAboveTrig = getListTriggers(parent.getChildren());

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
	
	/**
	 * generate accept Macro code for ATOMIC BROADCAST connector 
	 * */
	public String generateAcceptAtomicBroadcastConnector(ArrayList<TreeNode> listSynch) {
		String result = "";
		if (parent != null) {
			ArrayList<TreeNode> listAboveTrig = getListTriggers(parent.getChildren());

			for (TreeNode aboveTrig : listAboveTrig) {
				if (!aboveTrig.isCompound()) {
					result += "\t\tport(" + aboveTrig.getComponentTypeName() + "Connector.class, \"" + aboveTrig.getPortTypeName() + "\")"
							+ ".accepts(";
					for (int i=0 ; i<listSynch.size()-1 ; i++) {
						TreeNode portI = listSynch.get(i);
						result += portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\", ";
					}
					result += listSynch.get(listSynch.size()-1).getComponentTypeName() 
							+ "Connector.class, \"" + listSynch.get(listSynch.size()-1).getPortTypeName() + "\");\n";
				}else {
					if (!this.isTrigger()) {
						ArrayList<TreeNode> listExportedTrig = aboveTrig.getExport();
						for (TreeNode aboveExportTrig : listExportedTrig) {
							result += "\t\tport(" + aboveExportTrig.getComponentTypeName() + "Connector.class, \"" + aboveExportTrig.getPortTypeName() + "\")"
									+ ".accepts(";
							for (int i=0 ; i<listSynch.size()-1 ; i++) {
								TreeNode portI = listSynch.get(i);
								result += portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\", ";
							}
							result += listSynch.get(listSynch.size()-1).getComponentTypeName() 
									+ "Connector.class, \"" + listSynch.get(listSynch.size()-1).getPortTypeName() + "\");\n";
						}
					}
					
				}
				
				if (!this.isTrigger()) {
					for (int i=0 ; i<listSynch.size() ; i++) {
						TreeNode portI = listSynch.get(i);
						result += "\t\tport(" + portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\")"
								+ ".accepts(";
						for (int j=0 ; j<listSynch.size() ; j++) {
							if (i != j) {
								TreeNode portJ = listSynch.get(j);
								result += portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\", ";
								if (!aboveTrig.isCompound()) {
									result += aboveTrig.getComponentTypeName() + "Connector.class, \"" + aboveTrig.getPortTypeName() + "\");\n";
								}else {
									ArrayList<TreeNode> epPorts = aboveTrig.getExport();
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
		}
		
		
		return result;
	}
	
	public String printAcceptMacro(String result) {
		for (TreeNode leaf : children) {
			result = leaf.printAcceptMacro(result);
		}
		if (children.size() > 0) {
			ArrayList<TreeNode> listTriggers = getListTriggers(children);
			ArrayList<TreeNode> listSynch = getListSynch(children);
			if (listTriggers.size() == 0) {
				
				//ATOMIC BROADCAST
				if (parent!=null && !parent.allChildrenAreSync()){
					result += generateAcceptAtomicBroadcastConnector(listSynch);
				}//RENDEZVOUS
			} //BROADCAST
			else {
				result += generateAcceptsBroadcastConnector(listSynch, listTriggers);
			}
//			result += generateAcceptsBroadcastConnector(listSynch, listTriggers);
//			result += generateAcceptAtomicBroadcastConnector(listSynch);
		}
		return result;
	}
	
	public String printRequireMacro(String result) {
		//String result = "";
		for (TreeNode leaf : children) {
			result = leaf.printRequireMacro(result);
		}
		
		if (children.size() > 0) {
			ArrayList<TreeNode> listTriggers = getListTriggers(children);
			ArrayList<TreeNode> listSynch = getListSynch(children);
			
			if (listTriggers.size() == 0) {
				
				//ATOMIC BROADCAST
				if (parent!=null && !parent.allChildrenAreSync()){
					result += generateRequireAtomicBroadcastConnector(listSynch);
				}//RENDEZVOUS
				else {
					result += generateRendezConnector(listSynch);
				}				
			}	//BROADCAST
			else {
				result += generateRequiresBroadcastConnector(listSynch, listTriggers);
				for (TreeNode trig : listTriggers) {
					if (trig.isCompound() && !trig.getExport().get(0).isTrigger()) {
						result += generateRendezConnector(trig.getExport());
					}
				}
			}
//			result += generateAcceptsBroadcastConnector(listSynch, listTriggers);
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
