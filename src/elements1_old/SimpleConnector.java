package elements1_old;

import java.util.ArrayList;

public class SimpleConnector {

	private String name;
//	private SimpleConnector absName;
	private ArrayList<Element> listElements;
	private ArrayList<Element> listStoredElements;
	//Allias
	private ArrayList<Compound> listCompound;
	
	public SimpleConnector() {
		// TODO Auto-generated constructor stub
		listElements = new ArrayList<Element>();
		listCompound = new ArrayList<Compound>();
	}
	
	public SimpleConnector(ArrayList<Element> _listElements) {
		// TODO Auto-generated constructor stub
		listElements = _listElements;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Compound> getListCompound() {
		return listCompound;
	}

	public void setListCompound(ArrayList<Compound> listCompound) {
		this.listCompound = listCompound;
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
			if (e.getKind() == 0)
				listTrigs.add(e);
		}
		return listTrigs;
	}
	
	public ArrayList<Element> getSynchs(){
		ArrayList<Element> listSynchs = new ArrayList<Element>();
		for (Element e : listElements) {
			if (e.getKind() == 1)
				listSynchs.add(e);
		}
		return listSynchs;
	}
	
	//Check the connector is flat or not
	public boolean isFlatConnector() {
		for (int i=0 ; i<listElements.size() ; i++) {
			for (int j=i+1 ; j<listElements.size() ; j++) {
				if (listElements.get(i).getLevel() != listElements.get(j).getLevel()) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Check the connector are Atomic
	public boolean allElementsInLevelAreSynch(int maxLv) {
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getLevel() == maxLv) {
				if (listElements.get(i).getKind() == 0)
					return false;
			}
		}
		return true;
	}
	
	//Get all element in block triggers at level
	public ArrayList<Element> getElememtInBlockOfTriggers(int maxLv) {
		ArrayList<Element> listElemOfBlockTriggs = new ArrayList<Element>();
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getLevel() == maxLv) {
				if (listElements.get(i).getKind() == 2)
					listElemOfBlockTriggs.add(listElements.get(i));
			}
		}
		return listElemOfBlockTriggs;
	}
	
	//Get all Triggers in block triggers at level
	public ArrayList<Element> getTriggerInBlockOfTriggers(int maxLv) {
		ArrayList<Element> listTrigOfBlockTriggs = new ArrayList<Element>();
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getLevel() == maxLv) {
				if (listElements.get(i).getKind() == 2 && listElements.get(i).getContent().contains("`"))
					listTrigOfBlockTriggs.add(listElements.get(i));
			}
		}
		return listTrigOfBlockTriggs;
	}
		
	//Get all Synchs in block triggers at level
	public ArrayList<Element> getSynchInBlockOfTriggers(int maxLv) {
		ArrayList<Element> listSynchOfBlockTriggs = new ArrayList<Element>();
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getLevel() == maxLv) {
				if (listElements.get(i).getKind() == 2 && !listElements.get(i).getContent().contains("`"))
					listSynchOfBlockTriggs.add(listElements.get(i));
			}
		}
		return listSynchOfBlockTriggs;
	}
	
	//Get all Synchs in block triggers at level
	public ArrayList<Compound> getCompoundAtLv(int lv) {
		ArrayList<Compound> rs = new ArrayList<Compound>();
		
		for (int i=0 ; i<listCompound.size() ; i++) {
			if (listCompound.get(i).getLevel() == lv) {
				rs.add(listCompound.get(i));
			}
		}
		return rs;
	}
	
	//Get all triggers at level
	public ArrayList<Element> TriggersAtLv(int maxLv) {
		ArrayList<Element> listTriggs = new ArrayList<Element>();
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getLevel() == maxLv) {
				if (listElements.get(i).getKind() == 0)
					listTriggs.add(listElements.get(i));
			}
		}
		return listTriggs;
	}
	
	//Get all synchronize at level
	public ArrayList<Element> SynchsAtLv(int maxLv) {
		ArrayList<Element> listSynchs = new ArrayList<Element>();
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getLevel() == maxLv) {
				if (listElements.get(i).getKind() == 1)
					listSynchs.add(listElements.get(i));
			}
		}
		return listSynchs;
	}
		
	public ArrayList<Element> getListElementAtLevel(int lv){
		ArrayList<Element> rs = new ArrayList<Element>();
		for (Element e : listElements) {
			if (e.getLevel() == lv) rs.add(e);
		}
		return rs;
	}
	public int getMaxLevel() {
		int maxlv = 0;
		for (Element e : listElements) {
			if (maxlv < e.getLevel()) {
				maxlv = e.getLevel();
			}
		}
		
		for (Compound c : listCompound) {
			if (maxlv < c.getLevel()) {
				maxlv = c.getLevel();
			}
		}
		
		return maxlv;
	}
	
	public void setChildrenOfCompounds() {
		for (int i=0 ; i<listCompound.size() ; i++) {
			for (int j=i ; j<listCompound.size() ; j++) {
				
				if (listCompound.get(i).getName().equals(listCompound.get(j).getParent())) {
					//System.out.println(listCompound.get(i).name + "\t" + listCompound.get(j).parent + "\t" + listCompound.get(j).name);
					listCompound.get(i).getChildren().add(listCompound.get(j));
				}
			}
		}
	}
	
	public void setParentsOfCompoundAndElemts() {
		for (int i=0 ; i<listElements.size() ; i++) {
			listElements.get(i).setParent("root");
		}
		
		for (int i=0 ; i<listCompound.size() ; i++) {
			if (listCompound.get(i).getLevel() <= 1) {
				listCompound.get(i).setParent("root");
			}else {
				Compound thisComp = listCompound.get(i);

				ArrayList<Compound> listUplvlCompounds = getCompoundAtLv(thisComp.getLevel()-1);
	
				for (Compound c : listUplvlCompounds) {
					String tempUpCompoundName = c.getName();
					tempUpCompoundName = tempUpCompoundName.substring(0, tempUpCompoundName.lastIndexOf("_"));
					if (thisComp.getName().contains(tempUpCompoundName) && tempUpCompoundName.contains("_")) {
//						System.out.println(thisComp.name + "\t" + tempUpCompoundName);
						listCompound.get(i).setParent(c.getName());
						break;
					}
				}
				
			}
		}
	}
	
	public void modifyName() {
		
//		System.out.println("Number of compounds: " + listCompound.size());
		
		for (int i=0 ; i<listCompound.size() ; i++) {
//			System.out.println(listCompound.get(i));
			String cName = listCompound.get(i).getName();
			cName = cName.substring(0, cName.indexOf("-")) + cName.substring(cName.lastIndexOf("-")) + "-" + listCompound.get(i).getLevel();
			listCompound.get(i).setName((this.name + "_" + cName).replaceAll("-", "_"));
			String parentName = listCompound.get(i).getParent();
			if (!parentName.equals("root")) {
				parentName = parentName.substring(0, parentName.indexOf("-")) + parentName.substring(parentName.lastIndexOf("-")) + "-"  + (listCompound.get(i).getLevel() - 1);
				listCompound.get(i).setParent((this.name + "_" + parentName).replaceAll("-", "_"));
			}
		}
	}
	
	/**
	 * Collect single elements into blocks 
	 * Set Parents for each block and element
	 * Cut redundant characters in names
	 * */
	public void standardizeConnector() {
		listStoredElements = (ArrayList<Element>) listElements.clone();
		if (getMaxLevel() > 0) {
			for (int i=0 ; i<listElements.size() ; i++) {
				ArrayList<Element> temp = new ArrayList<Element>();
				temp.add(listElements.get(i));
				
				for (int j=i+1 ; j<listElements.size() ; j++) {
					if (listElements.get(i).getLevel() > 0) {
						if (listElements.get(i).getLocation().equals(listElements.get(j).getLocation())) {
							temp.add(listElements.get(j));
							listElements.remove(listElements.get(j));
						}
					}
				}
				
				Compound tempCompund;
				
				if (listElements.get(i).getKind() == 2) {
					tempCompund = new Compound(listElements.get(i).getLocation(), listElements.get(i).getLevel(), 0, temp);
				}else {
					tempCompund = new Compound(listElements.get(i).getLocation(), listElements.get(i).getLevel(), 1, temp);
				}
				
				listCompound.add(tempCompund);
			}
			listElements.clear();
			
			for (int i=0 ; i<listCompound.size() ; i++) {
				if (listCompound.get(i).getLevel() == 0) {
					for (int j=0 ; j<listCompound.get(i).getListElems().size() ; j++) {
						listElements.add(listCompound.get(i).getListElems().get(j));
					}
				}
			}
			
			for (int i=0 ; i<listCompound.size() ; i++) {
				if (listCompound.get(i).getLevel() == 0) {
					listCompound.remove(listCompound.get(i));
				}
			}
		}
		
		
//		System.out.println("Compound: " + listCompound.size() + "\t Elements: "+ listElements.size());
//		for (int i=0 ; i<listElements.size() ; i++) {
//			listElements.get(i).print();
//		}
		
		setParentsOfCompoundAndElemts();
		modifyName();
		setChildrenOfCompounds();
	}
	
	/*
	public String genBIPConnector() {
		String rs = "";
//		System.out.println("---Compounds---Details----");
		for (int lv = getMaxLevel() ; lv>=1 ; lv--) {
			for (int i=0 ; i<listCompound.size() ; i++) {
				if (listCompound.get(i).getLevel() == lv)
					rs += listCompound.get(i).genBIPConnector();
			}
		}
		
//		System.out.println("---Connector---Details----");
		rs += "\t\tconnector " + this.getName() + " " + name + "_detail(";
		
		for(int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getKind() == 0)
				rs += listElements.get(i).getInstanceName() + "." + listElements.get(i).getAction() + ", ";
		}
		
		for(int i=0 ; i<listCompound.size() ; i++) {
			if(listCompound.get(i).getKind() == 0)
				rs += listCompound.get(i).getName() + "_detail.ep, ";
		}
		
		for(int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getKind() != 0)
				rs += listElements.get(i).getInstanceName() + "." + listElements.get(i).getAction() + ", ";
		}
		
		for(int i=0 ; i<listCompound.size() ; i++) {
			if(listCompound.get(i).getKind() != 0 && listCompound.get(i).getLevel() == 1)
				rs += listCompound.get(i).getName() + "_detail.ep, ";
		}
		
		rs += ")";
		rs = rs.replaceAll(", \\)", "\\)");
//		rs += "\nend";
//		System.out.println(rs);
		return rs + "\n";
	}
	
	public String genBIPConnectorDefination() {
		
		String rs = "";
//		System.out.println("---Compounds---" + getMaxLevel());
		for (int lv = getMaxLevel() ; lv>=1 ; lv--) {
			for (int i=0 ; i<listCompound.size() ; i++) {
				if (listCompound.get(i).getLevel() == lv)
					rs += listCompound.get(i).genBIPConnectorDefination();
			}
		}
		
		
//		System.out.println("---Connector---");
		rs += "\tconnector type " + name + "(";
		
		for (int i=0 ; i<listElements.size() ; i++) {
			rs += "Port p" + (i+1) + ", ";
		}
		for (int i=0 ; i<listCompound.size() ; i++) {
			if (listCompound.get(i).getParent().equals("root"))
				rs += "Port p" + (i+listElements.size()+1) + ", ";
		}
		
		rs += ")";
		rs = rs.replaceAll(", \\)", "\\)");
		rs += "\n\t\tdefine";
		int count = 1;
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getKind() == 0) {
				rs += " p" + count + "\'";
				count++;
			}
		}
		
		for (int i=0 ; i<listCompound.size() ; i++) {
			if (listCompound.get(i).getKind() == 0 && listCompound.get(i).getParent().equals("root")) {
				rs += " p" + count + "\'";
				count++;
			}
		}
		
		for (int i=0 ; i<listElements.size() ; i++) {
			if (listElements.get(i).getKind() != 0) {
				rs += " p" + count;
				count++;
			}
		}
		
		for (int i=0 ; i<listCompound.size() ; i++) {
			if (listCompound.get(i).getKind() != 0 && listCompound.get(i).getParent().equals("root")) {
				rs += " p" + count;
				count++;
			}
		}
		
		rs += "\n\tend";
		return rs + "\n";
	}
	*/
//	
//	public SimpleConnector getAbsName() {
//		return absName;
//	}
//
//	public void setAbsName(SimpleConnector absName) {
//		this.absName = absName;
//	}

	
	public ArrayList<Element> getListStoredElements() {
		return listStoredElements;
	}

	public void setListStoredElements(ArrayList<Element> listStoredElements) {
		this.listStoredElements = listStoredElements;
	}

	public void print() {
		System.out.println("\n----Printing Connector: " + name);
		System.out.println("Elements:");
		for (Element e : listElements) {
			e.print();
		}
		System.out.println("Compound:");
		for (Compound c : listCompound) {
			c.print();
		}
	}
}