package treestructure1;

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
			
			//Flat connector
			result += generateRequiresBroadcastConnector(listSynchs, listTriggers);
			
			if (listTriggers.size() == 0) {
				if ((parent != null && parent.allChildrenAreSync()) || parent == null) {
					//!(This is compound and export list doesn't contain trigger)
					//Condition to dont generate redundant codes
					if (!(this.isCompound() && !this.getExport().get(0).get(0).isTrigger())) {
						result += "//REN2: " + listSynchs + "\n";
						result += generateRequiresRendezvous(listSynchs);
					}
				}
			}
			else {
				for (TreeNode trig : listTriggers) {
					if (trig.isCompound() && trig.getExport().size()==1) {
						for (ArrayList<TreeNode> listExp : trig.getExport()) {
//							System.out.println("List export: " + trig.getExport());
							if (!listExp.get(0).isTrigger()) {
								result += "//REN3: " + listExp + "\n";
								result += generateRequiresRendezvous(listExp);
							}
						}
					}
				}
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
			for (TreeNode triggers : listTriggers) {
				/**
				 * 1. triggers & synch are not compound
				 * -> flat broadcast
				 * */
				if (!triggers.isCompound() && !synch.isCompound()) {
					result += "//1st case: no compound: //" + triggers.getContent() + "`-" + synch.getContent() + "\n";
					result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
							+ ".requires(" + triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
				}
				
				/**
				 * 2. synch compound, trigger not compound
				 * -> exportSynch_i requires trig
				 * */
				if (!triggers.isCompound() && synch.isCompound()) {
					result += "//2nd case: synch compound, trig is not//"  + triggers.getContent() + "`-" + synch.getExport() + "\n";
					ArrayList<ArrayList<TreeNode>> exportSynchron = synch.getExport();
					for (ArrayList<TreeNode> exportSyncI : exportSynchron) {
						//exportI: {p1}
						if (exportSyncI.size() == 1) {
							result += "\t\tport(" + exportSyncI.get(0).getComponentTypeName() + "Connector.class, \"" 
									+ exportSyncI.get(0).getPortTypeName() + "\")"+ ".requires(" 
									+ triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
						} //exportI: {p2, p3} or {p2`, p3`}
						else {
							//{p2`, p3`, p4`}
							if (exportSyncI.get(0).isTrigger()) {
								for (TreeNode exportSyncIelem : exportSyncI) {
									result += "\t\tport(" + exportSyncIelem.getComponentTypeName() + "Connector.class, \"" 
											+ exportSyncIelem.getPortTypeName() + "\")"+ ".requires(" 
											+ triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
								}
							} //{p2, p3, p4} --> ATOMIC BROADCAST
							else {
								for (int i=0 ; i<exportSyncI.size() ; i++) {
									result += "\t\tport(" + exportSyncI.get(i).getComponentTypeName() + "Connector.class, \"" 
											+ exportSyncI.get(i).getPortTypeName() + "\")"+ ".requires(";
									String temp = "";
									for (int j=0 ; j<exportSyncI.size() ; j++) {
										if (i != j) {
											temp += exportSyncI.get(j).getComponentTypeName() + "Connector.class, \"" 
													+ exportSyncI.get(j).getPortTypeName() + "\", ";
										}
									}
									result += temp + triggers.getComponentTypeName() + "Connector.class, \"" 
											+ triggers.getPortTypeName() + "\");\n";
								}
							}
						}
					}
//					for (TreeNode epSynch : synch.getExport()) {
//						result += "\t\tport(" + epSynch.getComponentTypeName() + "Connector.class, \"" + epSynch.getPortTypeName() + "\")"
//								+ ".requires(" + triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
//					}
				}
				
				/**
				 * 3. synch not compound but trigger
				 * -> synch requires trig_i
				 * */
				if (triggers.isCompound() && !synch.isCompound()) {
					result += "//3rd case: trig compound, synch is not";
					result += "//" + triggers.getExport() + "`-" + synch + "\n";
//					String tempTrig = "";
//					StringJoiner joiner = new StringJoiner(", ");
					for (int i=0 ; i<triggers.getExport().size() ; i++) {
						ArrayList<TreeNode> exportI = triggers.getExport().get(i);
						StringJoiner joiner = new StringJoiner(", ");
						for (TreeNode exportIelem : exportI) {
							String temp = exportIelem.getComponentTypeName() + "Connector.class, \"" + exportIelem.getPortTypeName() + "\"";
							joiner.add(temp);
						}
						result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
								+ ".requires(" + joiner.toString() + ");\n";
					}
					
				}
				
				/**
				 * 4. synch & trigger are compound
				 * -> synch_i requires trig_i
				 * */
				if (triggers.isCompound() && synch.isCompound()) {
					result += "//4th case: both are compound //" + triggers.getExport() + "`-" + synch.getExport() + "\n";
					for (ArrayList<TreeNode> epSynch : synch.getExport()) {
						for (TreeNode epSynchI : epSynch) {
							
							
							for (ArrayList<TreeNode> epTrig : triggers.getExport()) {
								StringJoiner joiner = new StringJoiner(", ");
								for (TreeNode epSynchJ : epSynch) {
									if (epSynchI != epSynchJ) {
										String tempSynchJ = epSynchJ.getComponentTypeName() + "Connector.class, \"" + epSynchJ.getPortTypeName();
										joiner.add(tempSynchJ);
									}
								}
								for (TreeNode epTrigI : epTrig) {
									String temTrig = epTrigI.getComponentTypeName() + "Connector.class, \"" + epTrigI.getPortTypeName();
									joiner.add(temTrig);
								}
								
								result += "\t\tport(" + epSynchI.getComponentTypeName() + "Connector.class, \"" + epSynchI.getPortTypeName() + "\")"
										+ ".requires(" + joiner.toString() + ");\n";
//								result += "\t\tport(" + epSynch.getComponentTypeName() + "Connector.class, \"" + epSynch.getPortTypeName() + "\")"
//										+ ".requires(" + epTrig.getComponentTypeName() + "Connector.class, \"" + epTrig.getPortTypeName() + "\");\n";
							}
						}
						
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * generate requires Macro code for RENDEZVOUS connector 
	 * ------------------------------------------------------------------------------------
	 * */
	public String generateRequiresRendezvous(ArrayList<TreeNode> listSynch) {
		String result = "";
		ArrayList<String> listConnectors = genArrayListOfRendez(listSynch);
		
		for (String connector : listConnectors) {
			ArrayList<TreeNode> aTreeNodeList = convertRendezStringToTreeNode1(connector);
			result += generateOneRendezvousConnector1(aTreeNodeList);
		}
		return result;
	}
	
	public String generateOneRendezvousConnector1(ArrayList<TreeNode> input) {
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
	
	//convert rendezvous string to Tree
	public ArrayList<TreeNode> convertRendezStringToTreeNode1(String input) {
		ArrayList<TreeNode> result1 = new ArrayList<TreeNode>();
		String[] elems = input.split("-");
		for (String elem : elems) {
			if (elem.contains("~")) {
				String[] sub_elem = elem.split("~");
				for (String se : sub_elem) {
					TreeNode temp = findTreeNodeFromContent(se);
					if (temp != null) {
						result1.add(temp);
					}
				}
			}else {
				TreeNode temp = findTreeNodeFromContent(elem);
				if (temp != null) {
					result1.add(temp);
				}
					
			}
		}
		System.out.println("RENDEZVOUS connector: " + result1);
		return result1;
	}
	
	public TreeNode findTreeNodeFromContent(String content){
    	TreeNode result = null;
    	
        //check for value first
    	if (this.getContent().equals(content)) {
    		return this;
    	}

        //check if you can go any further from the current node
    	if (this.getChildren().size() == 0) return null;

    	for (int i=0 ; i<this.getChildren().size()-1 ; i++) {
    		//now go right
    		result = this.getChildren().get(i).findTreeNodeFromContent(content);
    		//check the node
    		if (result != null && result.getContent().equals(content)) return result;
    	}

        //if not found return the last
        return this.getChildren().get(this.getChildren().size()-1).findTreeNodeFromContent(content);
    }
	
	//gen the list of all Rendezvous connector
	public ArrayList<String> genArrayListOfRendez(ArrayList<TreeNode> listSynch) {
		ArrayList<ArrayList<String>> inputStr = new ArrayList<ArrayList<String>>();
		ArrayList<String> resultStr = new ArrayList<String>();
		for (TreeNode child : listSynch) {
			ArrayList<String> tempStr = new ArrayList<String>();
			if (!child.isCompound()) {
				tempStr.add(child.getContent());
			}else {
				for (ArrayList<TreeNode> export : child.getExport()) {
					StringJoiner joiner = new StringJoiner("~");
					for (TreeNode exportI : export) {
						
						String temp = exportI.getContent();
						joiner.add(temp);
						
					}
					tempStr.add(joiner.toString());
				}
			}
			inputStr.add(tempStr);
		}
		generatePermutations(inputStr, resultStr, 0, "");
//		System.out.println(resultStr);
		return resultStr;
	}
	
	void generatePermutations(ArrayList<ArrayList<String>> input, ArrayList<String> result, int depth, String current) {
	    if (depth == input.size()) {
	        result.add(current);
	        return;
	    }

	    for (int i = 0; i < input.get(depth).size(); i++) {
	        generatePermutations(input, result, depth + 1, current + input.get(depth).get(i) + "-");
	    }
	}
	
	/**
	 * Add information of export
	 * ------------------------------------------------------------------------------------
	 * */
	public void addExportedPort() {
				
		for (TreeNode child : children) {
			child.addExportedPort();
		}
		
		//compound with one or more trigger children
		if(this.isCompound()) {
			//get list to check children's kind
			ArrayList<TreeNode> considering;//
			
			//case 1: Compound with no trigger children -> exportedPort = all synch children
			if (allChildrenAreSync()) {
				considering = getChildren();
			}else {
				//case 2: Compound with some trigger children -> exportedPort = all trigs children
				considering = getListTriggers(getChildren());
			}
			
			if (noCompoundInList(considering)) {
				if (considering.get(0).isTrigger()) {	//{{p1`}, {p2`}}
					for (TreeNode child : considering) {
						ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
						temp.add(child);
						export.add(temp);
					}
				}else { //{{p1,p2}}
//					ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
//					for (TreeNode child : considering) {
//						temp.add(child);
//					}
					export.add(considering);
				}
			}else {
				for (TreeNode child : considering) {
					if (!child.isCompound()) {
						ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
						temp.add(child);
						export.add(temp);
					}else {
						for (ArrayList<TreeNode> childExp : child.getExport()) {
							export.add(childExp);
						}
					}
				}
			}
		}
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
			rs += content + "\t (trig? " + isTrigger() + ")\t parent:" + parent.getContent();// + "\t siblings: " + getSiblings();
			if (this.isCompound()) {
				rs += "\t export: " + this.getExport();// + "\t children: " + this.getChildren();
			}
			
		}else {
			rs += content + "\t (trig? " + isTrigger();
			if (this.isCompound() || this.getContent().contains("root")) {
				rs += ")\t export: " + this.getExport();// + "\t children: " + this.getChildren();
			}
			
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
	
	public boolean isCompound() {
    	if (content.matches("c[0-9]+.null")) {
    		return true;
    	}
    	return false;
    }
	
	public boolean hasCompoundInChildren() {
    	for (TreeNode child : children) {
    		if (child.isCompound()) {
    			return true;
    		}
    	}
    	return false;
    }
	
	public boolean noCompoundInList(ArrayList<TreeNode> input) {
    	for (TreeNode child : input) {
    		if (child.isCompound()) {
    			return false;
    		}
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
