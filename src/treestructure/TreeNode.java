package treestructure;

import java.util.ArrayList;

public class TreeNode {

	private TreeNode parent;
	private String content; // e.g. Tomcat.start
	private String componentTypeName; // e.g. Tomcat
	private String portTypeName; // e.g. start
	private boolean trigger; // trigger or sync
	private boolean canRemove;	//can be removed
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
		return componentTypeName + "." + portTypeName;
	}
	
	/**
	 * ---------------------------------------------------------------------
	 * Generation part
	 * */
	public String printRequireMacro(String result) {
		//String result = "";
		for (TreeNode leaf : children) {
			result = leaf.printRequireMacro(result);
		}
//		System.out.println("Test cur: " + this.getContent() + "\t children size: " + this.getChildren().size());
		if (children.size() > 0) {
			ArrayList<TreeNode> listTriggers = getListTriggers(children);
			ArrayList<TreeNode> listSynch = getListSynch(children);
			
			if (listTriggers.size() == 0) {
//				System.out.println(this.getContent() + ":\tno trigger");
				//ATOMIC BROADCAST
				// parent != null and siblings have triggers
				if (parent!=null){
					if (!parent.allChildrenAreSync()) {
						result += "//ATOMIC BROADCAST\n";
						result += generateRequireAtomicBroadcastConnector(listSynch);
					}else { //RENDEZ
						result += "//REN1\n";
//						ArrayList<String> temp = genArrayListOfRendez(listSynch);
//						System.out.println(temp);
//						parent.convertRendezStringToTreeNode(temp);
						result += generateRendezConnector(listSynch);
					}
				}else {
					result += "//REN2\n";
//					ArrayList<String> temp = genArrayListOfRendez(this.getChildren());
//					System.out.println(temp);
//					convertRendezStringToTreeNode(temp);
//					if (!hasCompoundInChildren())
					result += generateRendezConnector(listSynch);
				}
			}	//BROADCAST
			else {
				//(p.1)-[(p.2)`-(p.3)`]
				result += "//BROADCAST\n";
				result += generateRequiresBroadcastConnector(listSynch, listTriggers);
				for (TreeNode trig : listTriggers) {
					if (trig.isCompound() && !trig.getExport().get(0).isTrigger()) {
						result += "//REN3\n";
						result += generateRendezConnector(trig.getExport());
//						ArrayList<String> temp = genArrayListOfRendez(trig.getExport());
//						System.out.println(temp);
//						convertRendezStringToTreeNode(temp);
					}
				}
			}
		}
		return result;		
	}
	
	/**
	 * generate Macro code for RENDEZVOUS connector 
	 * ------------------------------------------------------------------------------------
	 * */
	public String generateRendezConnector(ArrayList<TreeNode> listSynch) {
		String result = "";
		ArrayList<String> temp = genArrayListOfRendez(listSynch);
		ArrayList<ArrayList<TreeNode>> listRendezWithCompound = convertRendezStringToTreeNode(temp);
		for (ArrayList<TreeNode> rendezvousConnector : listRendezWithCompound) {
//			System.out.println("Rendez: " + rendezvousConnector);
			result += "//" + rendezvousConnector + "\n";
			for (int i=0 ; i<rendezvousConnector.size() ; i++) {
				TreeNode portI = rendezvousConnector.get(i);
				
				if (!portI.isCompound()) {
					result += "\t\tport(" + portI.getComponentTypeName() + "Connector.class, \"" + portI.getPortTypeName() + "\")"
							+ ".requires(";
					for (int j=0 ; j<rendezvousConnector.size() ; j++) {
						if (i!=j) {
							TreeNode portJ = rendezvousConnector.get(j);
							String s = "";
							if (!portJ.isCompound()) {
								s += portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\"";
							}else {
								for (int k=0 ; k<portJ.getExport().size()-1 ; k++) {
									s += portJ.getExport().get(k).getComponentTypeName() + "Connector.class, \"" 
											+ portJ.getExport().get(k).getPortTypeName() + "\", ";
								}
								s += portJ.getExport().get(portJ.getExport().size()-1).getComponentTypeName() + "Connector.class, \"" 
										+ portJ.getExport().get(portJ.getExport().size()-1).getPortTypeName() + "\"";
								}
 
								if (i != rendezvousConnector.size() - 1) {
									if (j != rendezvousConnector.size() - 1) {
										result += s + ", ";
								} else {
									result += s + ");\n";
								}
							} else {
								if (j != rendezvousConnector.size() - 2) {
									result += s + ", ";
								} else {
									result += s + ");\n";
								}
							}
						}
					}
				}else {
					for (TreeNode portIExport : portI.getExport()) {
						result += "\t\tport(" + portIExport.getComponentTypeName() + "Connector.class, \"" + portIExport.getPortTypeName() + "\")"
								+ ".requires(";
						for (int j=0 ; j<rendezvousConnector.size() ; j++) {
							if (i!=j) {
								TreeNode portJ = rendezvousConnector.get(j);
								String s = "";
								if (!portJ.isCompound()) {
									s += portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\"";
								}else {
									for (int k=0 ; k<portJ.getExport().size()-1 ; k++) {
										s += portJ.getExport().get(k).getComponentTypeName() + "Connector.class, \"" 
												+ portJ.getExport().get(k).getPortTypeName() + "\", ";
									}
									s += portJ.getExport().get(portJ.getExport().size()-1).getComponentTypeName() + "Connector.class, \"" 
											+ portJ.getExport().get(portJ.getExport().size()-1).getPortTypeName() + "\"";
								}
 
								if (i != rendezvousConnector.size() - 1) {
									if (j != rendezvousConnector.size() - 1) {
										result += s + ", ";
									} else {
										result += s + ");\n";
									}
								} else {
									if (j != rendezvousConnector.size() - 2) {
										result += s + ", ";
									} else {
										result += s + ");\n";
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public ArrayList<ArrayList<TreeNode>> convertRendezStringToTreeNode(ArrayList<String> input) {
		ArrayList<ArrayList<TreeNode>> result = new ArrayList<ArrayList<TreeNode>>();
		for (String aRendez : input) {
			String[] elems = aRendez.split("-");
			ArrayList<TreeNode> tempResult = new ArrayList<TreeNode>();
			for (String elem : elems) {
				TreeNode temp = findTreeNodeFromContent1(elem);
				if (temp != null)
					tempResult.add(temp);
			}
//			System.out.println(tempResult);
			result.add(tempResult);
		}
//		System.out.println(result);
		return result;
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
				for (TreeNode export : child.getExport()) {
					tempStr.add(export.getContent());
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
//					result += "//1st case: no compound: //" + triggers.getContent() + "`-" + synch.getContent() + "\n";
					result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
							+ ".requires(" + triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
				}
				
				/**
				 * 2. synch compound, trigger not compound
				 * -> exportSynch_i requires trig
				 * */
				if (!triggers.isCompound() && synch.isCompound()) {
//					result += "//2nd case: synch compound, trig is not//"  + triggers.getContent() + "`-" + synch.getExport() + "\n";
					for (TreeNode epSynch : synch.getExport()) {
						result += "\t\tport(" + epSynch.getComponentTypeName() + "Connector.class, \"" + epSynch.getPortTypeName() + "\")"
								+ ".requires(" + triggers.getComponentTypeName() + "Connector.class, \"" + triggers.getPortTypeName() + "\");\n";
					}
				}
				
				/**
				 * 3. synch not compound but trigger
				 * -> synch requires trig_i
				 * */
				if (triggers.isCompound() && !synch.isCompound()) {
//					result += "//3rd case: trig compound, synch is not";
//					result += "//" + triggers.getExport() + "`-" + synch + "\n";
					String tempTrig = "";
					for (int i=0 ; i<triggers.getExport().size()-1 ; i++) {
						TreeNode exportI = triggers.getExport().get(i);
						if (!exportI.isCompound()) {
							tempTrig += exportI.getComponentTypeName() + "Connector.class, \"" + exportI.getPortTypeName() + "\", ";
						}else {
							for (int j = 0 ; j <exportI.getExport().size() ; j++) {
								TreeNode exportJ = exportI.getExport().get(j);
								tempTrig += exportJ.getComponentTypeName() + "Connector.class, \"" + exportJ.getPortTypeName() + "\", ";
							}
						}
						
					}
					TreeNode exportI = triggers.getExport().get(triggers.getExport().size()-1);
					if (!exportI.isCompound()) {
						tempTrig += exportI.getComponentTypeName() + "Connector.class, \"" + exportI.getPortTypeName();
					}else {
						for (int j = 0 ; j <exportI.getExport().size()-1 ; j++) {
							TreeNode exportJ = exportI.getExport().get(j);
							tempTrig += exportJ.getComponentTypeName() + "Connector.class, \"" + exportJ.getPortTypeName() + "\", ";
						}
						tempTrig += exportI.getExport().get(exportI.getExport().size()-1).getComponentTypeName() + "Connector.class, \"" 
								+ exportI.getExport().get(exportI.getExport().size()-1).getPortTypeName();
					}
//					TreeNode lastExportI = exportI.getExport().get(exportI.getExport().size()-1);
//					if(exportI.isCompound()) {
//						tempTrig += exportI.getExport().get(exportI.getExport().size()-1).getComponentTypeName() + "Connector.class, \"" 
//								+ exportI.getExport().get(exportI.getExport().size()-1).getPortTypeName();
//					}
					result += "\t\tport(" + synch.getComponentTypeName() + "Connector.class, \"" + synch.getPortTypeName() + "\")"
							+ ".requires(" + tempTrig + "\");\n";
				}
				
				/**
				 * 4. synch & trigger are compound
				 * -> synch_i requires trig_i
				 * */
				if (triggers.isCompound() && synch.isCompound()) {
//					result += "//4th case: both are compound //" + triggers.getExport() + "`-" + synch.getExport();
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
	 * generate require Macro code for ATOMIC BROADCAST connector 
	 * ------------------------------------------------------------------------------------
	 * */
	public String generateRequireAtomicBroadcastConnector(ArrayList<TreeNode> listSynch) {
		String result = "";
		ArrayList<ArrayList<TreeNode>> listAtomicBroadcastConnector = genArrayListOfAtomicBroadcast(listSynch);
		for (ArrayList<TreeNode> anABC : listAtomicBroadcastConnector) {
			String temp = "\t\tport(" + anABC.get(0).getComponentTypeName() + "Connector.class, \"" + anABC.get(0).getPortTypeName() + "\")"
					+ ".requires(";
			for (int i=1 ; i<anABC.size()-1 ; i++) {
				temp += anABC.get(i).getComponentTypeName() + "Connector.class, \"" + anABC.get(i).getPortTypeName() + "\", ";
			}
			temp += anABC.get(anABC.size()-1).getComponentTypeName() + "Connector.class, \"" + anABC.get(anABC.size()-1).getPortTypeName() + "\");\n";
			result += temp;
		}
		return result;
	}
	
	//Get the list of all Atomic Broadcast connector
	public ArrayList<ArrayList<TreeNode>> genArrayListOfAtomicBroadcast(ArrayList<TreeNode> listSynch) {
		ArrayList<ArrayList<TreeNode>> result = new ArrayList<ArrayList<TreeNode>>();
		ArrayList<TreeNode> listAboveTrig = getListTriggers(parent.getChildren());
		
		for (TreeNode trig : listAboveTrig) {
			//To skip the case of p1`-[p2-p3]`
			if (!this.isTrigger()) {
				for (int i=0 ; i<listSynch.size() ; i++) {
					TreeNode portI = listSynch.get(i);
					if (!portI.isCompound()) {
						for (int j=0 ; j<listSynch.size() ; j++) {
							if (i != j) {
								TreeNode portJ = listSynch.get(j);
								if (!portJ.isCompound()) {
									if (!trig.isCompound()) {
										ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
										temp.add(portI);
										temp.add(portJ);
										temp.add(trig);
										result.add(temp);
									}else {
										ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
										temp.add(portI);
										temp.add(portJ);
										for (TreeNode epPorts : trig.getExport()) {
											temp.add(epPorts);
										}
										result.add(temp);
									}
								}else {
									for (TreeNode portJexport : portJ.getExport()) {
										if (!trig.isCompound()) {
											ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
											temp.add(portI);
											temp.add(portJexport);
											temp.add(trig);
											result.add(temp);
										}else {
											ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
											temp.add(portI);
											temp.add(portJexport);
											for (TreeNode epPorts : trig.getExport()) {
												temp.add(epPorts);
											}
											result.add(temp);
										}
									}
								}
							}
						}
					}else {
						for (int j=0 ; j<listSynch.size() ; j++) {
							if (i != j) {
								for (TreeNode portIexport : portI.getExport()) {
									TreeNode portJ = listSynch.get(j);
									if (!portJ.isCompound()) {
										if (!trig.isCompound()) {
											ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
											temp.add(portIexport);
											temp.add(portJ);
											temp.add(trig);
											result.add(temp);
										}else {
											ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
											temp.add(portIexport);
											temp.add(portJ);
											for (TreeNode epPorts : trig.getExport()) {
												temp.add(epPorts);
											}
											result.add(temp);
										}
									}else {
										for (TreeNode portJexport : portJ.getExport()) {
											if (!trig.isCompound()) {
												ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
												temp.add(portIexport);
												temp.add(portJexport);
												temp.add(trig);
												result.add(temp);
											}else {
												ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
												temp.add(portIexport);
												temp.add(portJexport);
												for (TreeNode epPorts : trig.getExport()) {
													temp.add(epPorts);
												}
												result.add(temp);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	/**
	 * ---------------------------------------------------------------------
	 * Handling tree information
	 * */
	/**
	 * Mark elements should be reduced
	 * 
	 * */
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
		String rs = "";
		if (parent != null) {
			rs += content + "\t (trig? " + isTrigger() + ")\t parent:" + parent.getContent();// + "\t export:";
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
	
	/**
	 * To re-orgarnize the list of export in each compound node
	 * */
	public void reOrganizeExportedList(ArrayList<TreeNode> newList, TreeNode cur) {
		
		ArrayList<TreeNode> curList = cur.getExport();
		if (curList.size() > 0) {
			//If no compound in its export
			//add all of them to a new export list
			if (noCompoundInList(curList)) {
			
				if (cur.getParent().getContent().contains("root")) {
					for (TreeNode elem : curList) {
						newList.add(elem);
					}
				}else {
					newList.add(cur);
				}
				return ;
			}else {
				//If has some compounds in its export
				//Add all non-compounds
				//Recursive call for compounds
				for (TreeNode elem : curList) {
					if (!elem.isCompound()) {
						newList.add(elem);
					}else {
						reOrganizeExportedList(newList, elem);
					}
				}
			}
		}
		
	}
	
	/**
	 * Add information of export
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
		//case4: Sync compound with no trigger children -> reduced or atomic broadcast -> no need to care
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
//    			System.out.println("check content of child " + child.getContent());
    			return true;
    		}
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
//    			System.out.println("check content of compound " + child.getContent());
    			return false;
    		}
    	}
    	return true;
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
    public boolean hasCompoundInList(ArrayList<TreeNode> input) {
    	for (TreeNode child : input) {
    		if (child.isCompound()) {
    			return true;
    		}
    	}
    	return false;
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
    			rs.add(child);
    		}
    	}
    	return rs;
    }
    public ArrayList<TreeNode> getListSynchWithExport(ArrayList<TreeNode> input){
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
    
    public TreeNode findTreeNodeFromContent1 (String content){
    	TreeNode result = null;

        //check for value first
    	if (this.getContent().equals(content)) {
    		return this;
    	}

        //check if you can go any further from the current node
    	if (this.getChildren().size() == 0) return null;

    	for (int i=0 ; i<this.getChildren().size()-1 ; i++) {
    		result = this.getChildren().get(i).findTreeNodeFromContent1(content);
    		if (result != null && result.getContent().equals(content))
    			return result;
    	}
        //now go right
//        result = this.right.find(v);

        //check the node
//        if(result != null && result.value == v) return result;

        //if not found return whatever is returned by searching left
//        return this.left.find(v);
        return this.getChildren().get(this.getChildren().size()-1).findTreeNodeFromContent1(content);
    }
    
    public void findTreeNodeFromContent(String content, TreeNode result) {
		if (this.getContent().equals(content)) {
			System.out.println("check equals to find:" + this.getContent());
			result = new TreeNode(this.getContent(), this.isTrigger(), this.getParent());
			result.setExport(this.getExport());
			return ;
		}
		
		for (TreeNode child : getChildren()) {
			child.findTreeNodeFromContent(content, result);
			return;
		}
    }
}
