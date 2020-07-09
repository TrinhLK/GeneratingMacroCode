package elements1;
import java.util.ArrayList;

public class Compound {

	private String name;
	private int level;
	private int kind;
	//private String type;
	private ArrayList<Element> listElems;
//	ArrayList<Compound> listCompound;
	private ArrayList<Compound> children;
	private String parent;
	
	public Compound(String _name, int _level, int _kind, ArrayList<Element> _listElems) {
		// TODO Auto-generated constructor stub
		name = _name;
//		type = _name;
		level = _level;
		kind = _kind;
		listElems = _listElems;
		children = new ArrayList<Compound>();
	}
	
	/**
	 * Generate connector code
	 * 
	public String genBIPConnector() {
		String rs = "\t\tconnector " + type + " " + name + "_detail(";
		
		for(int i=0 ; i<listElems.size() ; i++) {
			if (listElems.get(i).getKind() == 0)
				rs += listElems.get(i).getInstanceName() + "." + listElems.get(i).getAction() + ", ";
		}
		
		for(int i=0 ; i<children.size() ; i++) {
			if (children.get(i).getKind() == 0)
				rs += children.get(i).name + "_detail.ep, ";
		}
		
		for(int i=0 ; i<listElems.size() ; i++) {
			if (listElems.get(i).getKind() != 0)
				rs += listElems.get(i).getInstanceName() + "." + listElems.get(i).getAction() + ", ";
		}
		
		for(int i=0 ; i<children.size() ; i++) {
			if (children.get(i).getKind() != 0)
				rs += children.get(i).name + "_detail.ep, ";
		}
		
		rs += ")";
		rs = rs.replaceAll(", \\)", "\\)");
//		rs += "\nend";
//		System.out.println(rs);
		return rs + "\n";
	}
	*/
	/**
	 * Generate connector defination
	 * */
	public String genBIPConnectorDefination() {
		String rs = "\tconnector type " + name + "(";
		
//		for (int i=0 ; i<listElems.size() ; i++) {
//			if (listElems.get(i).getKind() == 0) {
//				rs += "Port p" + count + ", ";
//				count++;
//			}
//		}
//		
//		for (int i=0 ; i<children.size() ; i++) {
//			if (children.get(i).getKind() == 0) {
//				rs += "Port p" + count + ", ";
//				count++;
//			}
//		}
//		
//		for (int i=0 ; i<listElems.size() ; i++) {
//			if (listElems.get(i).getKind() == 1) {
//				rs += "Port p" + count + ", ";
//				count++;
//			}
//		}
//		
//		for (int i=0 ; i<children.size() ; i++) {
//			if (children.get(i).getKind() == 1) {
//				rs += "Port p" + count + ", ";
//				count++;
//			}
//		}
		
		for (int i=0 ; i<(listElems.size() + children.size()) ; i++) {
			rs += "Port p" + (i+1) + ", ";
		}
		
		rs += ")";
		rs = rs.replaceAll(", \\)", "\\)");
		rs += "\n\t\texport port Port ep()\n\t\tdefine";
		int count = 1;
		
		for (int i=0 ; i<listElems.size() ; i++) {
			if (listElems.get(i).getKind() == 0) {
				rs += " p" + count + "\'";
				count++;
			}
		}
		
		for (int i=0 ; i<children.size() ; i++) {
			if (children.get(i).getKind() == 0) {
				rs += " p" + count + "\'";
				count++;
			}
		}
		
		for (int i=0 ; i<listElems.size() ; i++) {
			if (listElems.get(i).getKind() != 0) {
				rs += " p" + count;
				count++;
			}
		}
		
		for (int i=0 ; i<children.size() ; i++) {
			if (children.get(i).getKind() != 0) {
				rs += " p" + count;
				count++;
			}
		}

		rs += "\n\tend\n";
//		System.out.println(rs);
		return rs;
	}
	
	public void print() {
		String rs = "\t";
		
		if (kind == 0) {
			System.out.println(" " + name + "\t" + level + "\t trigger \tparent:" + parent);
		} else if (kind == 1) {
			System.out.println(" " + name + "\t" + level + "\t synchronize \tparent:" + parent);
		} else {
			System.out.println(" " + name + "\t" + level + "\t in block of triggers \tparent:" + parent);
		}
		
		for (int i=0 ; i<listElems.size() ; i++) {
			//rs += listElems.get(i).getContent() + "\t ";
			listElems.get(i).setParent(name);
			listElems.get(i).print();
		}
//		System.out.println(rs);
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public ArrayList<Compound> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Compound> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public ArrayList<Element> getListElems() {
		return listElems;
	}

	public void setListElems(ArrayList<Element> listElems) {
		this.listElems = listElems;
	}

//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//	
//	public String toString() {
//		return type + "\t" + name;
//	}
}
