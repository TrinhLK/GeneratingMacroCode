package elements_old;

import java.util.ArrayList;

public class SimpleConnector {

	String name;
	ArrayList<Element> listElements; 
	ConnectorKind kind;
	ArrayList<Element> listTriggers; 
	ArrayList<Element> listSynchronizes; 
	
	public SimpleConnector() {
		// TODO Auto-generated constructor stub
		listElements = new ArrayList<Element>();
	}
	
	public SimpleConnector(ArrayList<Element> _listElements) {
		// TODO Auto-generated constructor stub
		listElements = _listElements;
		listTriggers = getTriggers();
		listSynchronizes = getSynchs();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<Element> getListElements() {
		return listElements;
	}

	public void setListElements(ArrayList<Element> listElements) {
		this.listElements = listElements;
	}
	
	public ArrayList<Element> getTriggers(){
		ArrayList<Element> listTrigs = new ArrayList<Element>();
		for (Element e : listElements) {
			if (e.kind == 0)
				listTrigs.add(e);
		}
		return listTrigs;
	}
	
	public ArrayList<Element> getSynchs(){
		ArrayList<Element> listSynchs = new ArrayList<Element>();
		for (Element e : listElements) {
			if (e.kind == 1)
				listSynchs.add(e);
		}
		return listSynchs;
	}
	
	public ConnectorKind getKind() {
		if (isFlatConnector()) {
			for (int i=0 ; i<listElements.size() ; i++) {
				if (listElements.get(i).kind == 0) {
					return ConnectorKind.BROADCAST;
				}
			}
			return ConnectorKind.RENDEZVOUS;
		} else {
			int count = 0;
			for (Element e : listElements) {
				if (e.kind == 1) {
					count++;
				}
			}
			if (count == listElements.size()) return ConnectorKind.RENDEZVOUS;

			if (allElementsInLevelAreSynch(getMaxLevel())) {
				return ConnectorKind.ATOMIC_BROADCAST;
			}else {
				return ConnectorKind.CAUSALITY_CHAIN;
			}
		}
	}
	
	public String generateMacroCode() {
		kind = getKind();
		switch (kind) {
		case RENDEZVOUS:
//			System.out.println("RENDEZVOUS");
//			print();
			return genRendezvous() + "\n" + genAcceptsMacro();
//			System.out.println(genRendezvous());
			//genRendezvous();
//			break;
		case BROADCAST:
//			System.out.println("BROADCAST");
//			print();
//			System.out.println(genBroadcast());
			return genBroadcast() + "\n" + genAcceptsMacro();
//			genBroadcast();
//			break;
		default:
//			System.out.println("Hirar");
//			print();
//			System.out.println(genHirachicalConnector(getMaxLevel()));
			return genHirachicalConnector(getMaxLevel()) + "\n" + genAcceptsMacro();
//			genHirachicalConnector(getMaxLevel());
//			break;
		}
//		System.out.println(genAcceptsMacro());
	}
	
	public String genAcceptsMacro() {
		String result = "";
		for (int i=0 ; i<listElements.size() ; i++) {
			String portI = listElements.get(i).content.replaceAll("`", "");
			result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
					+ ".accepts(";
			for (int j=0 ; j<listElements.size() ; j++) {
				if (i != j) {
					String portJ = listElements.get(j).content.replaceAll("`", "");
					String s = portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\"";
					if (i != listElements.size() - 1) {
						if (j != listElements.size() - 1) {
							result += s + ", ";
						} else {
							result += s + ");\n";
						}
					} else {
						if (j != listElements.size() - 2) {
							result += s + ", ";
						} else {
							result += s + ");\n";
						}
					}
				}
			}
		}
		//System.out.println(result);
		return result;
	}
	
	/**
	 * Generate ATOMIC_BROADCAST
	 * */
	public String genHirachicalConnector(int maxLv) {

		ArrayList<Element> listElemsAtMax = getListElementAtLevel(maxLv);
		ArrayList<Element> listTrigsAboveMax = TriggersAtLv(maxLv-1);
//		ArrayList<Element> listSynchsAboveMax = SynchsAtLv(maxLv-1);
		String result = "";
		
		//All the elements in the max level are synchronize
		if (allElementsInLevelAreSynch(maxLv)) {
			//If there is any trigger above, it is ATOMIC_BROADCAST
			if (listTrigsAboveMax.size() > 0) {
				for (Element trig : listTrigsAboveMax) {
					String trigger = trig.content.replaceAll("`", "");
					for (int i=0 ; i<listElemsAtMax.size() ; i++) {
						String portI = listElemsAtMax.get(i).content;
						result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
								+ ".requires(";
						for (int j=0 ; j<listElemsAtMax.size() ; j++) {
							if (i != j) {
								String portJ = listElemsAtMax.get(j).content;
								result += portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\", ";
								result += trigger.substring(0, trigger.indexOf(".")) + "Connector.class, \"" + trigger.substring(trigger.indexOf(".") + 1) + "\");\n";
							}
						}
					}
				}
			}
	
		} else {
			//CAUSALITY_CHAIN
			//If there are both trigger and synch at max level
			/**
			 * Check all compounds at this level
			 * */
			ArrayList<Element> listTrigsAtMax = TriggersAtLv(maxLv);
			ArrayList<Element> listSyncsAtMax = SynchsAtLv(maxLv);
			
			for (Element synch : listSyncsAtMax) {
				String portI = synch.content;
				
				for (Element triggers : listTrigsAtMax) {
					String portJ = triggers.content.replaceAll("`", "");
					result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
							+ ".requires(" + portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\");\n";
				}
			}
			
			//If there is any trigger above
			if (listTrigsAboveMax.size() > 0) {
				for (Element effects : listTrigsAtMax) {
					String portI = effects.content.replaceAll("`", "");
					
					for (Element cause : listTrigsAboveMax) {
						String portJ = cause.content.replaceAll("`", "");
						result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
								+ ".requires(" + portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\");\n";
					}
				}
			}
		}
		
		//If there is any block is a trigger
		if (getElememtInBlockOfTriggers(maxLv).size() > 0) {
			ArrayList<Element> listTrigsAtMax = getTriggerInBlockOfTriggers(maxLv);
			ArrayList<Element> listSyncsAtMax = getSynchInBlockOfTriggers(maxLv);
			
			//Triggers and Synchs
			if (listTrigsAtMax.size() > 0) {
				for (Element synch : listSyncsAtMax) {
					String portI = synch.content.replaceAll("*", "");
					
					for (Element triggers : listTrigsAtMax) {
						String portJ = triggers.content.replaceAll("`", "");
						portJ = triggers.content.replaceAll("*", "");
						result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
								+ ".requires(" + portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\");\n";
					}
				}
				
				ArrayList<Element> listSynchsAboveMax = SynchsAtLv(maxLv-1);
				for (Element synch : listSynchsAboveMax) {
					String portI = synch.content.replaceAll("*", "");
					
					for (Element triggers : listTrigsAtMax) {
						String portJ = triggers.content.replaceAll("`", "");
						portJ = triggers.content.replaceAll("*", "");
						result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
								+ ".requires(" + portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\");\n";
					}
				}
			} else {
				//All Synch
				for (int i=0 ; i<listSyncsAtMax.size() ; i++) {
					String portI = listSyncsAtMax.get(i).content;
					result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
							+ ".requires(";
					for (int j=0 ; j<listSyncsAtMax.size() ; j++) {
						if (i != j) {
							String portJ = listSyncsAtMax.get(j).content;
							String s = portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\"";
							if (i != listSyncsAtMax.size() - 1) {
								if (j != listSyncsAtMax.size() - 1) {
									result += s + ", ";
								} else {
									result += s + ");\n";
								}
							} else {
								if (j != listSyncsAtMax.size() - 2) {
									result += s + ", ";
								} else {
									result += s + ");\n";
								}
							}
						}
					}
				}
				
				ArrayList<Element> listSynchsAboveMax = SynchsAtLv(maxLv-1);
				for (Element synch : listSynchsAboveMax) {
					String portI = synch.content;
					result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
							+ ".requires(";
					for (int j=0 ; j<listSyncsAtMax.size() ; j++) {
						String portJ = listSyncsAtMax.get(j).content;
						String s = portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\"";
						if (j < listSyncsAtMax.size()-1) {
							result += s + ", ";
						}else {
							result += s + ");\n";
						}
					}
				}
			}
			
			
		}
		//System.out.println(result);
		
		if (maxLv > 1) {
			return result += genHirachicalConnector(maxLv-1);
		}else {
			return result;
		}
	}
	
	/**
	 * Generate RENDEZVOUS
	 * */
	public String genRendezvous() {
		String result = "";
		for (int i=0 ; i<listElements.size() ; i++) {
			String portI = listElements.get(i).content;
			result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
					+ ".requires(";
			for (int j=0 ; j<listElements.size() ; j++) {
				if (i != j) {
					String portJ = listElements.get(j).content;
					String s = portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\"";
					if (i != listElements.size() - 1) {
						if (j != listElements.size() - 1) {
							result += s + ", ";
						} else {
							result += s + ");\n";
						}
					} else {
						if (j != listElements.size() - 2) {
							result += s + ", ";
						} else {
							result += s + ");\n";
						}
					}
				}
			}
		}
		//System.out.println(result);
		return result;
	}
	
	/**
	 * Generate BROADCAST
	 * */
	public String genBroadcast() {
		String result = "";
		
		for (Element synch : getSynchs()) {
			String portI = synch.content;
			
			for (Element triggers : getTriggers()) {
				String portJ = triggers.content.replaceAll("`", "");
				result += "\t\tport(" + portI.substring(0, portI.indexOf(".")) + "Connector.class, \"" + portI.substring(portI.indexOf(".") + 1) + "\")"
						+ ".requires(" + portJ.substring(0, portJ.indexOf(".")) + "Connector.class, \"" + portJ.substring(portJ.indexOf(".") + 1) + "\");\n";
			}
		}
		
		//System.out.println(result);
		return result;
	}
	
	//Check the connector is flat or not
	public boolean isFlatConnector() {
		for (int i=0 ; i<listElements.size() ; i++) {
			for (int j=i+1 ; j<listElements.size() ; j++) {
				if (listElements.get(i).level != listElements.get(j).level) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Check the connector are Atomic
	public boolean allElementsInLevelAreSynch(int maxLv) {
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).level == maxLv) {
				if (listElements.get(i).kind == 0)
					return false;
			}
		}
		return true;
	}
	
	//Get all element in block triggers at level
	public ArrayList<Element> getElememtInBlockOfTriggers(int maxLv) {
		ArrayList<Element> listElemOfBlockTriggs = new ArrayList<Element>();
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).level == maxLv) {
				if (listElements.get(i).kind == 2)
					listElemOfBlockTriggs.add(listElements.get(i));
			}
		}
		return listElemOfBlockTriggs;
	}
	
	//Get all Triggers in block triggers at level
	public ArrayList<Element> getTriggerInBlockOfTriggers(int maxLv) {
		ArrayList<Element> listTrigOfBlockTriggs = new ArrayList<Element>();
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).level == maxLv) {
				if (listElements.get(i).kind == 2 && listElements.get(i).content.contains("`"))
					listTrigOfBlockTriggs.add(listElements.get(i));
			}
		}
		return listTrigOfBlockTriggs;
	}
		
	//Get all Synchs in block triggers at level
	public ArrayList<Element> getSynchInBlockOfTriggers(int maxLv) {
		ArrayList<Element> listSynchOfBlockTriggs = new ArrayList<Element>();
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).level == maxLv) {
				if (listElements.get(i).kind == 2 && !listElements.get(i).content.contains("`"))
					listSynchOfBlockTriggs.add(listElements.get(i));
			}
		}
		return listSynchOfBlockTriggs;
	}
	
	//Get all triggers at level
	public ArrayList<Element> TriggersAtLv(int maxLv) {
		ArrayList<Element> listTriggs = new ArrayList<Element>();
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).level == maxLv) {
				if (listElements.get(i).kind == 0)
					listTriggs.add(listElements.get(i));
			}
		}
		return listTriggs;
	}
	
	//Get all synchronize at level
	public ArrayList<Element> SynchsAtLv(int maxLv) {
		ArrayList<Element> listSynchs = new ArrayList<Element>();
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).level == maxLv) {
				if (listElements.get(i).kind == 1)
					listSynchs.add(listElements.get(i));
			}
		}
		return listSynchs;
	}
		
	public ArrayList<Element> getListElementAtLevel(int lv){
		ArrayList<Element> rs = new ArrayList<Element>();
		for (Element e : listElements) {
			if (e.level == lv) rs.add(e);
		}
		return rs;
	}
	public int getMaxLevel() {
		int maxlv = 0;
		for (Element e : listElements) {
			if (maxlv < e.level) {
				maxlv = e.level;
			}
		}
		return maxlv;
	}
	public void print() {
		System.out.println("Printing");
		for (Element e : listElements) {
			e.print();
		}
	}
}
