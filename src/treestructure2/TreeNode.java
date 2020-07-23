package treestructure2;

import java.util.ArrayList;
import java.util.StringJoiner;

public class TreeNode {

	private TreeNode parent;
	private String content; // e.g. Tomcat.start
	private String componentTypeName; // e.g. Tomcat
	private String portTypeName; // e.g. start
	private boolean trigger; // trigger or sync
	private ArrayList<ArrayList<TreeNode>> export; // null means the node is a leaf, i.e. a port
	private ArrayList<TreeNode> children; // null means the node is a leaf, i.e. a port
	
	public TreeNode(String _content, boolean _trigger, TreeNode _parent) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
		parent = _parent;
		children = new ArrayList<TreeNode>();
		export = new ArrayList<ArrayList<TreeNode>>();
	}
	
	public TreeNode(String _content, boolean _trigger) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
		children = new ArrayList<TreeNode>();
	}
	
	/**
	 * Generating codes
	 * ---------------------------------------------------------------------
	 * */
	public String genRequiresCode(String result) {
		
		if (getChildren().size() > 0) {
			ArrayList<TreeNode> listSynchs = getListSynchron(children);
			ArrayList<TreeNode> listTriggers = getListTriggers(children);
			if (listTriggers.size() == 0) { //RENDEZVOUS
				boolean isRedundant = false;
				if (parent != null) {
					if (parent.allChildrenAreSync()) {
//						System.out.println("Shouldn't generate Rendezvous 1: " + this);
						isRedundant = true;
					}else {
						if (!this.isTrigger()) {
//							System.out.println("Shouldn't generate Rendezvous 2: " + this);
							isRedundant = true;
						}else {
							isRedundant = shouldBeDisappear(this, parent);
//							System.out.println("Shouldn't generate Rendezvous 3: " + this + "\t" + isRedundant);
						}
					}
				}
				if (!isRedundant) {
					result += "//ALL Synchron: " + this + "\n";
					result += generateRendezvousRequireCode();
				}
			}else {
				result += "//Synchron & Trigger\n";
				result += generateRequiresBroadcastConnector(listSynchs, listTriggers);
			}
		}
		
		for (TreeNode leaf : children) {
			result = leaf.genRequiresCode(result);
		}
		return result;
	}
	/**
	 * generate requires Macro code for BROADCAST connector 
	 * ------------------------------------------------------------------------------------
	 * */
	public String generateRequiresBroadcastConnector(ArrayList<TreeNode> listSynch, ArrayList<TreeNode> listTriggers) {
		String result = "";
		for (TreeNode synch : listSynch) {
			for (TreeNode trig : listTriggers) {
				result += Sync2Trig(synch.getExport(), trig.getExport());
			}
		}
		return result;
	}
	
	public String Sync2Trig(ArrayList<ArrayList<TreeNode>> synchExport, ArrayList<ArrayList<TreeNode>> trigExport) {
		String result = "";
		for (ArrayList<TreeNode> elemOfSynchExport : synchExport) {
			
			if (elemOfSynchExport.size() == 1) {//[[p1][p2]]
				for (ArrayList<TreeNode> elemOfTrigExport : trigExport) {
					StringJoiner joiner = new StringJoiner(", ");
					result += "\t\tport(" + elemOfSynchExport.get(0).getComponentTypeName() + "Connector.class, \"" 
							+ elemOfSynchExport.get(0).getPortTypeName() + "\")" + ".requires(";
					for (TreeNode elemI : elemOfTrigExport) {
						String tempTrig = elemI.getComponentTypeName() + "Connector.class, \"" + elemI.getPortTypeName() + "\"";
						joiner.add(tempTrig);
					}
					result += joiner.toString() + ");\n";
				}
			}else {//[[p1,p2][p3,p4]]
				for (TreeNode subElemI : elemOfSynchExport) {
		
					for (ArrayList<TreeNode> elemOfTrigExport : trigExport) {
						StringJoiner joiner = new StringJoiner(", ");
						result += "\t\tport(" + subElemI.getComponentTypeName() + "Connector.class, \"" + subElemI.getPortTypeName() + "\")"
								+ ".requires(";
						
						for (TreeNode subElemJ : elemOfSynchExport) {
							if (subElemI != subElemJ) {
								joiner.add(subElemJ.getComponentTypeName() + "Connector.class, \"" + subElemJ.getPortTypeName() + "\"");
							}
						}
						for (TreeNode elemI : elemOfTrigExport) {
							String tempTrig = elemI.getComponentTypeName() + "Connector.class, \"" + elemI.getPortTypeName() + "\"";
							joiner.add(tempTrig);
						}
						result += joiner.toString() + ");\n";
					}
				}
			}
		}
//		System.out.println(result);
		return result;
	}
	
	/**
	 * generate requires Macro code for RENDEZVOUS connector 
	 * ------------------------------------------------------------------------------------
	 * */
	public String generateRendezvousRequireCode() {
		String result = "";
		for (ArrayList<TreeNode> elem : getExport()) {
			result += generateOneRendezvousConnector(elem);
		}
		return result;
	}
	
	public String generateOneRendezvousConnector(ArrayList<TreeNode> input) {
		String result = "";
		for (TreeNode portI : input) {
			String effect = "\t\tport(" + portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\")"
					+ ".requires(";
			StringJoiner joiner = new StringJoiner(", ");
			for (TreeNode portJ : input) {
				if (portI != portJ) {
					String cause = portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\"";
					joiner.add(cause);
				}
			}
			result += effect + joiner.toString() + ");\n";
		}
		return result;
	}
	
	public boolean shouldBeDisappear(TreeNode cur, TreeNode parent) {
		if (!parent.getContent().contains("root")) {
			if (parent.getExport().containsAll(cur.getExport())) {
				if (parent.isTrigger()) {
					return shouldBeDisappear(cur, parent.getParent());
				}else {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Add information of export
	 * ------------------------------------------------------------------------------------
	 * */
	public void addExportedPort() {
				
		for (TreeNode child : children) {
			child.addExportedPort();
		}
		
		if(children.size() == 0) {
			ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
			temp.add(this);
			this.export.add(temp);
		}else {
			//case 1: Compound with no trigger children -> exportedPort = all synch children
			if (allChildrenAreSync()) {
				export = getChildren().get(0).getExport();
				if (getChildren().size() > 1) {
					for (int i=1 ; i<getChildren().size() ; i++) {
						export = merge2ArrayList(export, getChildren().get(i).getExport());
					}
				}
			}else {
				//case 2: Compound with some trigger children -> exportedPort = all trigs children
				for (TreeNode childTriggers : getListTriggers(getChildren())) {
					ArrayList<ArrayList<TreeNode>> childTriggerExport = childTriggers.getExport();
					for (ArrayList<TreeNode> listExportI : childTriggerExport) {
						this.export.add(listExportI);
					}
				}
			}
		}
	}
	
	public ArrayList<ArrayList<TreeNode>> merge2ArrayList(ArrayList<ArrayList<TreeNode>> first, ArrayList<ArrayList<TreeNode>> second) {
		ArrayList<ArrayList<TreeNode>> result = new ArrayList<ArrayList<TreeNode>>();
		for (ArrayList<TreeNode> f_elem : first) {
			for (ArrayList<TreeNode> s_elem : second) {
				ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
				temp.addAll(f_elem);
				temp.addAll(s_elem);
				result.add(temp);
			}
		}
		return result;
	}
	
	/**
	 * Supporting functions
	 * ---------------------------------------------------------------------
	 * */
	public String removeBrackets(String content) {
		return content.replaceAll("`|\\*|\\)|\\(", "");
	}
	
	public void setNameAndAction(String _content) {
		String[] sp = _content.replaceAll("`|\\*|\\)|\\(", "").split("\\.");
		componentTypeName = sp[0];
		portTypeName = sp[1];
	}
	
	public void traversal() {
		String rs = "";
		if (parent != null) {
			rs += content + "\t (trig? " + isTrigger() + ")\t parent:" + parent.getContent();
			rs += "\t export: " + this.getExport();
		}else {
			rs += content + "\t (trig? " + isTrigger();
			rs += ")\t export: " + this.getExport();// + "\t children: " + this.getChildren();
		}
		System.out.println(rs);
		for (TreeNode leaf : children) {
			leaf.traversal();
		}
	}
	
	public void print() {
		if (trigger) {
			System.out.println(componentTypeName + "_" + portTypeName + "\t trigger \tparent:" + parent);
		} else {
			System.out.println(componentTypeName + "_" + portTypeName + "\t synchron \tparent:" + parent);
		}
	}
	
	public boolean allChildrenAreSync() {
    	for (TreeNode child : children) {
    		if (child.isTrigger())
    			return false;
    	}
    	return true;
    }
	
	public ArrayList<TreeNode> getListSynchron(ArrayList<TreeNode> input){
    	ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
    	for (TreeNode child : input) {
    		if (!child.isTrigger()) {
    			rs.add(child);
    		}
    	}
    	return rs;
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
	
	public ArrayList<TreeNode> getSiblings(){
		ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
		if (parent != null) {
			for (TreeNode sib : parent.getChildren()) {
				if (sib != this) {
					rs.add(sib);
				}
			}
		}
		return rs;
	}
	
//	public boolean hasTriggersInList(ArrayList<TreeNode> input) {
//		for (TreeNode t : )
//	}
	/**
	 * Getters and Setters
	 * ---------------------------------------------------------------------
	 * */
	public String toString() {
		return componentTypeName + "." + portTypeName;
	}

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

	public ArrayList<ArrayList<TreeNode>> getExport() {
		return export;
	}

	public void setExport(ArrayList<ArrayList<TreeNode>> export) {
		this.export = export;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}
}
