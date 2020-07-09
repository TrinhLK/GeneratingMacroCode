package elements1;

public class Element {

	private String content;
	private String instanceName;
	private String action;
	private int level;
	private int kind;
	private String location;
	private String parent;
	
	public Element() {
		// TODO Auto-generated constructor stub
		content = "";
		level = kind = -1;
	}
	
	public Element(String _content, int _level, int _kind, String _location) {
		content = removeBrackets(_content);
		level = _level;
		kind = _kind;
		location = _location;
		setNameAndAction(_content);
	}
	
	public String removeBrackets(String content) {
		String rs = content;
		rs = rs.replaceAll("\\(", "");
		rs = rs.replaceAll("\\)", "");
		return rs;
	}
	
	public void setNameAndAction(String _content) {
		String rs = _content.replaceAll("`|\\*|\\)|\\(", "");
//		System.out.println(rs);
		String[] sp = rs.split("\\.");
//		System.out.println(sp.length);
		instanceName = sp[0];
		action = sp[1];
	}
	
	public void print() {
		if (kind == 0) {
			System.out.println(instanceName + "_" + action + "\t" + level + "\t trigger \tparent:" + parent);
		} else if (kind == 1) {
			System.out.println(instanceName + "_" + action + "\t" + level + "\t synchronize \tparent:" + parent);
		} else {
			System.out.println(instanceName + "_" + action + "\t" + level + "\t in block of triggers \tparent:" + parent);
		}
		
	}

	public String toString() {
		return instanceName + "_" + action;
	}
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
}
