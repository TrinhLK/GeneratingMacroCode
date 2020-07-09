package elements2;

import java.util.ArrayList;
import java.util.Stack;

public class MainTree {

	private TreeNode root = new TreeNode("root.null", false, null);
	
	public void test() {
		String connectorString = "[(p.1)`-(p.2)]`-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]";
		String connectorString1 = "[(p.1)-(p.2)]-(p.3)-[(p.4)`-[(p.5)-(p.6)]]";
		String connectorString2 = "[(p.1)-(p.2)]-(p.3)-[(p.4)-(p.5)]";
		createTree(root, connectorString1,0);
		root.traversal();
		System.out.println("-- reduce --");
		root.markReduceTree();
//		root.reduceTree();
		root.traversal();
	}
	public static void main(String[] args) {
		MainTree testMT = new MainTree();
		testMT.test();
	}
	
	public void getListNode(TreeNode root, ArrayList<TreeNode> listNodes){
		listNodes.add(root);
		for(TreeNode child : root.getChildren()) {
			getListNode(child, listNodes);
		}
	}
	
//	public TreeNode findReducable() {
//        TreeNode root = null ? null : root.find(data);
//        return root;
//    }
	
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
			System.out.println(connectorString);
			System.out.println(connectorString.indexOf("]"));
			boolean isTrigger = false;
			if (connectorString.indexOf("]`") > 0) {
				isTrigger = true;
			}
			TreeNode compound = new TreeNode("c" + index + ".null", isTrigger, root);
			root.getChildren().add(compound);
			createTree(compound, nextLevelConnector, index+1);
			
			//after compound
			if (q + 1 < connectorString.length()) {
				String remainStr = connectorString.substring(q + 1, connectorString.length());
				if (remainStr.indexOf("-") == 0) {
					remainStr = remainStr.substring(1);
				}
				System.out.println("After: " + remainStr);
				createTree(root, remainStr, index+1);
			}
		}
	}
}
