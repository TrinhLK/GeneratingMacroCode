package elements2;

import java.util.ArrayList;

public class ListNode {
	ArrayList<TreeNode> listNode = new ArrayList<TreeNode>();
	
	public ListNode(TreeNode root) {
		// TODO Auto-generated constructor stub
		getListNode(root, listNode);
	}
	
	public void getListNode(TreeNode root, ArrayList<TreeNode> listNodes){
		listNodes.add(root);
		for(TreeNode child : root.getChildren()) {
			getListNode(child, listNodes);
		}
	}
}
