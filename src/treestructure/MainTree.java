package treestructure;

import java.util.ArrayList;
import java.util.Stack;

public class MainTree {

//	private TreeNode root = new TreeNode("root.null", false, null);
//	ArrayList<TreeNode> listNodes = new ArrayList<TreeNode>();
	
	public void test() {
//		String connectorString = "[(p.1)`-(p.2)]`-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]";
//		String connectorString1 = "[(p.1)`-(p.2)]-(p.3)-[(p.4)-[(p.5)-(p.6)]]";
//		String connectorString2 = "[(p.1)-(p.2)]-(p.3)-[(p.4)-(p.5)]";
		String connectorString = "[(p.1)-(p.2)]-(p.3)`-[(p.4)-(p.5)]";
		String connectorString1 = "[(p.1)-(p.2)]-[(p.3)-(p.4)]";
		String connectorString2 = "[(p.1)-(p.2)]`-(p.3)`-[(p.4)-(p.5)]";
		String connectorString3 = "[(p.1)-(p.2)]`-(p.3)-(p.4)";
		String connectorString4 = "(p.1)`-(p.2)-[(p.3)-(p.4)]";
		String connectorString5 = "[(p.1)`-(p.2)]-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]";
		String connectorString6 = "[(p.1)`-(p.2)]-[(p.3)`-(p.4)`-(p.5)]-(p.6)";
		String connectorString7 = "[(p.1)`-(p.2)]`-(p.3)-[(p.4)-(p.5)]";
		String connectorString8 = "(p.1)`-(p.2)-[(p.3)`-(p.4)`]";
		String connectorString9 = "(p.1)-[(p.2)`-(p.3)`]";
		String connectorString10 = "(p.1)`-[[(p.2)`-(p.21)`-[(p.2a)-(p.2b]]-[(p.3)`-(p.31)`-(p.3a)]]";
		String connectorString11 = "(p.1)`-(p.2)`-(p.3)";
		String connectorString12 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)-(p.3c)]`-[(p.3d)-(p.3e)]]"
				+ "-[(p.3a1)`-[(p.3b1)-(p.3c1)]`-[(p.3d1)-(p.3e1)]]";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString13 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)`-[(p.3c1)-(p.3c2)]`-(p.3e)]`-(p.3d)]";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]

		
		System.out.println("--- String 1: " + connectorString);
		System.out.println(genMacroCode(connectorString));
		
		System.out.println("--- String 2: " + connectorString1);
		System.out.println(genMacroCode(connectorString1));
		
		System.out.println("--- String 3: " + connectorString2);
		System.out.println(genMacroCode(connectorString2));
		
		System.out.println("--- String 4: " + connectorString3);
		System.out.println(genMacroCode(connectorString3));
//		
		System.out.println("--- String 5: " + connectorString4);
		System.out.println(genMacroCode(connectorString4));
//		
		System.out.println("--- String 6: " + connectorString5);
		System.out.println(genMacroCode(connectorString5));
//		
		System.out.println("--- String 7: " + connectorString6);
		System.out.println(genMacroCode(connectorString6));
//		
		System.out.println("--- String 8: " + connectorString7);
		System.out.println(genMacroCode(connectorString7));
//		
		System.out.println("--- String 9: " + connectorString8);
		System.out.println(genMacroCode(connectorString8));
//		
		System.out.println("--- String 10: " + connectorString9);
		System.out.println(genMacroCode(connectorString9));
//		
		System.out.println("--- String 11: " + connectorString10);
		System.out.println(genMacroCode(connectorString10));
//		
		System.out.println("--- String 12: " + connectorString11);
		System.out.println(genMacroCode(connectorString11));
//		
		System.out.println("--- String 13: " + connectorString12);
		System.out.println(genMacroCode(connectorString12));
//		
		System.out.println("--- String 14: " + connectorString13);
		System.out.println(genMacroCode(connectorString13));
	}
	
//	public String generatingDataTransfer(Annotation annotation) {
//		//data: int x: Tracker-Peer
//		String input = annotation.getValue();
//		String rs = "";
//		String[] parts = input.split(":");
//		String[] varInfor = parts[1].trim().split(" ");
//		String[] dataClasses = parts[2].trim().split("-");
//		rs += "\t\tdata(" + dataClasses[0].trim() + "Connector.class, \"" + varInfor[1] + "\").to(" + dataClasses[1].trim() + "Connector.class, \"" + varInfor[1] + "\");\n";
//		System.out.println(rs);
//		return rs;
//	}
	public String generatingMacroCode(String annotation) {
		ArrayList<String> listConnectors = readAnnotations(annotation);
		String rs = "";
		for (String con : listConnectors) {
			rs += genMacroCode(con);
		}
		return rs;
	}
	
	public ArrayList<String> readAnnotations(String annotation) {

        String readLine = annotation.toString();
        ArrayList<String> result = new ArrayList<String>();
        System.out.println("Anno: " + annotation);

    	if(readLine.contains("data:")) {
    		
    	}else if(readLine.contains("prop:")) {
    		
    	}
    	else {
    		if (readLine.contains("+")) {
    			String[] subConnectors = readLine.split("\\+");
              	 for (String con : subConnectors) {
              		 String standardCon = con.trim();
              		 result.add(standardCon);
              	 }
    		}else {
    			String standardCon = readLine.trim();
    			result.add(standardCon);
    		}
    	}
    	return result;
	}
	
	/**
	 * Generate requires + accepts functions for Glue Builder
	 * */
	public String genMacroCode(String connectorString) {
		TreeNode root = new TreeNode("root.null", false, null);
		createTree(root, connectorString, 0);

		TreeNode reducedTree = renewTree(root);
		reducedTree.traversal();
//		return "";
		return reducedTree.printRequireMacro("") + "\n" + genAcceptsCode(connectorString);
	}
	
	/**
	 * Gen ACCEPTS Code
	 * */
	public String genAcceptsCode(String connectorString) {
		String rs = "";
		TreeNode root = new TreeNode("root.null", false, null);
		createTree(root, connectorString, 0);
		TreeNode clone = root;
		clone.markReduceTree();
		ArrayList<TreeNode> lNodes = tree2List(clone);
		for (int i=0 ; i<lNodes.size() ; i++) {
			if (!lNodes.get(i).getContent().contains("null")) {
				rs += "\t\tport(" + lNodes.get(i).getComponentTypeName() + "Connector.class, \"" + lNodes.get(i).getPortTypeName() + "\")"
						+ ".accepts(";
				for (int j=0 ; j<lNodes.size() ; j++) {
					if (i != j) {
						TreeNode portJ = lNodes.get(j);
						if (!portJ.getContent().contains("null")) {
							String s = portJ.getComponentTypeName() + "Connector.class, \"" + portJ.getPortTypeName() + "\"";
							if (i != lNodes.size() - 1) {
								if (j != lNodes.size() - 1) {
									rs += s + ", ";
								} else {
									rs += s + ");\n";
								}
							} else {
								if (j != lNodes.size() - 2) {
									rs += s + ", ";
								} else {
									rs += s + ");\n";
								}
							}
						}
					}
				}
			}
		}
		return rs;
	}
	
	public static void main(String[] args) {
		MainTree testMT = new MainTree();
		testMT.test();
	}
	
	/**
	 * Remove redundant nodes
	 * Add the information of exported port
	 * */
	public TreeNode renewTree(TreeNode root) {
		TreeNode clone = root;
		clone.markReduceTree();
//		System.out.println("----Original----");
//		root.traversal();
//		System.out.println("--------");
		ArrayList<TreeNode> lNodes = tree2List(clone);
		lNodes = List2Tree(lNodes);
		lNodes.get(0).addExportedPort();
		System.out.println("----Original----");
		lNodes.get(0).traversal();
		System.out.println("--------");
		updateExportList(lNodes.get(0));
		return lNodes.get(0);
	}
	
	/**
	 * Update the export list 
	 * */
	public void updateExportList(TreeNode root) {
		
		if (root.getChildren().size() > 0) {
			for (TreeNode child : root.getChildren()) {
				ArrayList<TreeNode> newList = new ArrayList<TreeNode>();
				if (child.getExport().size() > 0) {
					child.reOrganizeExportedList(newList, child);
					child.setExport(newList);
				}
//				System.out.println(child.getContent() + "\tnewExport: " + newList);
			}
		}

		
	}
	
	public ArrayList<TreeNode> tree2List(TreeNode root) {
		ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
		removeRedundantNodes(root, rs);
		return rs;
	}
	
	public ArrayList<TreeNode> List2Tree(ArrayList<TreeNode> listNodes) {
		for (int i=0 ; i<listNodes.size() ; i++) {
			ArrayList<TreeNode> listChildren = new ArrayList<TreeNode>();
			for (int j=i+1 ; j<listNodes.size() ; j++) {
				if (listNodes.get(i).getContent().equals(listNodes.get(j).getParent().getContent())) {
					listChildren.add(listNodes.get(j));
				}
			}
			listNodes.get(i).setChildren(listChildren);
		}
		return listNodes;
	}
//	public TreeNode findReducable() {
//        TreeNode root = null ? null : root.find(data);
//        return root;
//    }
	public void removeRedundantNodes(TreeNode root, ArrayList<TreeNode> listNodes) {
		//ArrayList<TreeNode> listChildren = new ArrayList<TreeNode>();
		//System.out.println("Check list before: " + root.getChildren().size());
		if (root.getContent().equalsIgnoreCase("root.null"))
			listNodes.add(new TreeNode("root.null", false, null));
		for (TreeNode child : root.getChildren()) {
			if (child.isCanRemove() == false)
				listNodes.add(child);
		}

		for (TreeNode child : root.getChildren()) {
			removeRedundantNodes(child, listNodes);
		}
	}
	
	public void createTree(TreeNode root, String connectorString, int index){
		
		if (connectorString.length() == 0)
			return ;

		int pos = connectorString.indexOf('[');
		//if it's flat
		if (pos == -1) {
			String[] elems = connectorString.split("-");
			//for all elements
			for(String e : elems) {
				String content = e.trim();
				boolean isTrigger = false;
				
				if (content.contains("`")) {
					isTrigger = true;
				}
				
				TreeNode elem = new TreeNode(content, isTrigger, root);
				root.getChildren().add(elem);
			}
			return ;
		} else {
			Stack<Character> stack = new Stack<>();
			stack.push(connectorString.charAt(pos));
			int q = pos + 1;
			while (q < connectorString.length()) {
				if (connectorString.charAt(q) == ']') {
					if (!stack.empty())
						stack.pop();
				} else if (connectorString.charAt(q) == '[') {
					stack.push(connectorString.charAt(q));
				}
				q++;
				if (stack.empty())
					break;
			}
			
			//before compound
			String baseLevelConnector = connectorString.substring(0, pos);
			createTree(root, baseLevelConnector, index);
			
			//the compound
			String nextLevelConnector = connectorString.substring(pos + 1, q-1);
//			System.out.println(connectorString);
//			System.out.println(connectorString.indexOf("]"));
			
			
			boolean isTrigger = false;

			if (q + 1 < connectorString.length()) {
				String temp = connectorString.substring(pos, q+1);
				if (temp.charAt(temp.length()-1) == '`')
					isTrigger = true;
			}
			
			TreeNode compound = new TreeNode("c" + pos + index + ".null", isTrigger, root);
			root.getChildren().add(compound);
			createTree(compound, nextLevelConnector, index+1);
			
			//after compound
			if (q + 1 < connectorString.length()) {
				index = index+10;
				String remainStr = connectorString.substring(q + 1, connectorString.length());
				if (remainStr.indexOf("-") == 0) {
					remainStr = remainStr.substring(1);
				}
//				System.out.println("After: " + remainStr);
				createTree(root, remainStr, index+1);
			}
		}
	}
}
