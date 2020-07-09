package elements1;

import java.util.ArrayList;
import java.util.Stack;


public class MainFile {

	public static void main(String[] args) {
		MainFile test = new MainFile();
		test.genMacroCode("[(Tracker.test)`-(Peer.run)]-(Tracker.start)-[(Tracker.stop)`-[(Peer.stop)`-(Peer.fail)]]", "01");
		test.genMacroCode("[(Tracker.test)`-(Peer.run)]-[(Peer.stop)`-(Peer.fail)]", "02");
		test.genMacroCode("[(p.1)`-(p.2)]-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]", "03");
	}
	
	public void genMacroCode(String anno, String id) {
		ArrayList<SimpleConnector> temp = genAllConnector(anno, id);
		for(SimpleConnector sc : temp) {
			sc.print();
		}
	}
	/**
	 * Get List Elements of a Connector from Annotations
	 * Standardize them
	 * */
	public ArrayList<SimpleConnector> genAllConnector(String annotation, String annoId) {
		
		ArrayList<String> listConnectorString = getListConnectorStringFromAnAnnotation(annotation);
		ArrayList<SimpleConnector> listSingleConnector = new ArrayList<SimpleConnector>();
		
		for (int i=0 ; i<listConnectorString.size() ; i++) {
			
			SimpleConnector generalSingleConnector_i = createSingleConnector(listConnectorString.get(i));
			generalSingleConnector_i.setName("Connector" + annoId + (i+1));
			generalSingleConnector_i.standardizeConnector();
			listSingleConnector.add(generalSingleConnector_i);
		}
		
		return listSingleConnector;
	}
	
	public SimpleConnector createSingleConnector(String connectorString) {
		
		SimpleConnector aConnector = new SimpleConnector();
		ArrayList<Element> listElements = new ArrayList<Element>();
		getListElements(listElements, connectorString, 0, "block");
		aConnector.setListElements(listElements);
		return aConnector;
		
	}
	
	/**
	 * Read file contains the list of connectors
	 * Split and store connectors into an arraylist
	 * */
	public ArrayList<String> getListConnectorStringFromAnAnnotation(String annotation) {

        String readLine = annotation.toString();
        ArrayList<String> listConnectorString = new ArrayList<String>();
//        System.out.println("Anno: " + annotation);


    	if(readLine.contains("data:")) {
    		
    	} else if (readLine.contains("prop:")) {
    		
    	} else {
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
           		 listConnectorString.add(standardCon);
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
           	listConnectorString.add(standardCon);
           }
    	}
        	
    	return listConnectorString;
	}
	
	/**
	 * Get List Elements of a Connector in Annotations
	 * */
	public void getListElements(ArrayList<Element> aConnector, String connector, int level, String location){
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
					//System.out.println(e.indexOf("*"));
					temp = temp.substring(0, temp.indexOf("*")) + temp.substring(temp.indexOf("*") + 1);
					kind = 2;
				}else {
					kind = 1;
				}
				aConnector.add(new Element(temp, level, kind, location));
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
		getListElements(aConnector, baseLevelConnector, level, location+"_0-"+pos);
		
		String nextLevelConnector = connector.substring(pos + 1, q - 1);
		int nextLv = level + 1;
		getListElements(aConnector, nextLevelConnector, nextLv, location+"_"+(pos+1)+"-"+(q-1));
		
		if (q + 1 < connector.length()) {
			getListElements(aConnector, connector.substring(q + 1, connector.length() - 1), level, location+"_"+(q+1)+"-"+(connector.length() - 1));
		}
		
		return ;
	}
}
