package elements;

public class Element {

	String content;
	int level;
	int kind;
	
	public Element() {
		// TODO Auto-generated constructor stub
	}
	
	public Element(String _content, int _level, int _kind) {
		content = removeBrackets(_content);
		level = _level;
		kind = _kind;
	}
	
	public String removeBrackets(String content) {
		String rs = content;
		rs = rs.replaceAll("\\(", "");
		rs = rs.replaceAll("\\)", "");
		return rs;
	}
	
	public void print() {
		if (kind == 0) {
			System.out.println(content + "\t" + level + "\t trigger");
		} else if (kind == 1) {
			System.out.println(content + "\t" + level + "\t synchronize");
		} else {
			System.out.println(content + "\t" + level + "\t in block of triggers");
		}
		
	}
}
