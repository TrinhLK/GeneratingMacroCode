package elements_old;

import java.util.ArrayList;
import java.util.Stack;


public class GenCode {
	
	ArrayList<String> listConnectors;
	ArrayList<Element> aConnector;
	ArrayList<SimpleConnector> listSeparatedConnectors;

	
	public GenCode() {
		// TODO Auto-generated constructor stub
		listConnectors = new ArrayList<String>();
		aConnector = new ArrayList<Element>();
		listSeparatedConnectors = new ArrayList<SimpleConnector>();
	}
	
	public void getListElements(ArrayList<Element> aConnector, String connector, int level){
		if (aConnector == null)
			aConnector = new ArrayList<Element>();
		
		if (connector.length() == 0)
			return ;

		int pos = connector.indexOf('[');
		if (pos == -1) {
			String[] elems = connector.split("-");
			for(String e : elems) {
				String temp = e.trim();
				int kind = -1;
				if (temp.contains("`")) {
					kind = 0;
				}else if (temp.contains("*")) {
					temp = temp.substring(0, temp.indexOf("*")) + temp.substring(temp.indexOf("*") + 1);
					kind = 2;
				}else {
					kind = 1;
				}
				aConnector.add(new Element(temp, level, kind));
			}
			return ;
		}
	
		Stack<Character> stack = new Stack<>();
		stack.push(connector.charAt(pos));
		int q = pos + 1;
		while (q < connector.length()) {
			if (connector.charAt(q) == ']') {
				if (!stack.empty())
					stack.pop();
			} else if (connector.charAt(q) == '[') {
				stack.push(connector.charAt(q));
			}
			q++;
			if (stack.empty())
				break;
		}
		
		String baseLevelConnector = connector.substring(0, pos);
		getListElements(aConnector, baseLevelConnector, level);
		
		String nextLevelConnector = connector.substring(pos + 1, q - 1);
		int nextLv = level + 1;
		getListElements(aConnector, nextLevelConnector, nextLv);
		
		if (q + 1 < connector.length()) {
			getListElements(aConnector, connector.substring(q + 1, connector.length() - 1), level);
		}
		
//		TreeNode root = new TreeNode("");
//		root.val = connector.substring(0, pos);
//		root.left = str2tree(connector.substring(pos + 1, q - 1));
//		if (q + 1 < s.length()) {
//			root.right = str2tree(connector.substring(q + 1, connector.length() - 1));
//		}
		return ;
	}
	
	/**
	 * Read file contains the list of connectors
	 * Split and store connectors into an arraylist
	 * */
	public String readAnnotations(String annotation) {

        String readLine = annotation.toString();

        System.out.println("Anno: " + annotation);


    	if(readLine.contains("data:")) {
    		
    	}else if(readLine.contains("prop:")) {
    		
    	}
    	else {
    		if (readLine.contains("+")) {
           	 String[] subConnectors = readLine.split("\\+");
           	 for (String con : subConnectors) {
           		 String standardCon = con.trim();
           		 if (standardCon.contains("]`")) {
           			 String standCon1 = standardCon.substring(standardCon.indexOf("["), standardCon.indexOf("]`"));
           			 String standCon2 = standardCon.substring(standardCon.indexOf("]`"));
           			 standCon1 = standCon1.replaceAll("\\)", "\\)*");
           			 standCon2 = standCon2.replaceAll("\\]`", "\\]");
           			 standardCon = standCon1 + standCon2;
           		 }
           		 listConnectors.add(standardCon);
           	 }
                	
           } else {
           	String standardCon = readLine.trim();
           	if (standardCon.contains("]`")) {
       			 String standCon1 = standardCon.substring(standardCon.indexOf("["), standardCon.indexOf("]`"));
       			 String standCon2 = standardCon.substring(standardCon.indexOf("]`"));
       			 standCon1 = standCon1.replaceAll("\\)", "\\)*");
       			 standCon2 = standCon2.replaceAll("\\]`", "\\]");
       			 standardCon = standCon1 + standCon2;
       		 }
           	listConnectors.add(standardCon);
           }
    	}
        	
    	return "done\t" + readLine;
	}
	
	public void getAllConnector(String annotation, String annoId) {
		
		readAnnotations(annotation);
		for (String con : listConnectors) {
			SimpleConnector aConnector = new SimpleConnector();
			//ArrayList<Element> aConnector = new ArrayList<Element>();
			getListElements(aConnector.listElements, con, 0);
//			aConnector.print();
			listSeparatedConnectors.add(aConnector);
		}
		for (int i=0 ; i<listSeparatedConnectors.size() ; i++) {
			listSeparatedConnectors.get(i).setName("Connector" + annoId + (i+1));
		}
	}
	
	public String generatingMacroCode(String annotation, String annoId) {
		listConnectors = new ArrayList<String>();
		aConnector = new ArrayList<Element>();
		listSeparatedConnectors = new ArrayList<SimpleConnector>();
		
		getAllConnector(annotation, annoId);
		String rs = "";
		for (SimpleConnector sc : listSeparatedConnectors) {
			rs += sc.generateMacroCode() + "\n";
		}
		return rs;
	}

	/*
	public String generatingDataTransfer(Annotation annotation) {
		//data: int x: Tracker-Peer
		String input = annotation.getValue();
		String rs = "";
		String[] parts = input.split(":");
		String[] varInfor = parts[1].trim().split(" ");
		String[] dataClasses = parts[2].trim().split("-");
		rs += "\t\tdata(" + dataClasses[0].trim() + "Connector.class, \"" + varInfor[1] + "\").to(" + dataClasses[1].trim() + "Connector.class, \"" + varInfor[1] + "\");\n";
		System.out.println(rs);
		return rs;
	}
	
	
	public String generatingMacroCode(Annotation annotation) {
		
		listConnectors = new ArrayList<String>();
		aConnector = new ArrayList<Element>();
		listSeparatedConnectors = new ArrayList<SimpleConnector>();
		
		getAllConnector(annotation.getValue(), annotation.getKey());
		String rs = "";
		for (SimpleConnector sc : listSeparatedConnectors) {
			rs += sc.generateMacroCode() + "\n";
		}
		return rs;
	}
	*/
	

	public static void main(String[] args) {
		GenCode test = new GenCode();
//		test.getAllConnector("listConnectors.txt");
//		String anno[] = {"(LightA.switchon)-(LightB.switchoff) + (Light.switchon)-(LightA.switchoff)", "(LightB.switchon)`-(LightA.switchoff)-(Light.switchon)"};
//		for (int i=0 ; i<anno.length ; i++) {
//			System.out.println(test.generatingMacroCode(anno[i]));
//		}
//		System.out.println(test.generatingMacroCode("(LightA.switchon)-(LightB.switchoff) + (Light.switchon)-(LightA.switchoff)"));
		System.out.println(test.generatingMacroCode("[(Tracker.test)`-(Peer.run)]-(Tracker.start)-[(Tracker.stop)`-(Peer.stop)]", "01"));
		//System.out.println(test.listSeparatedConnectors.size());
		
	}
	
}
