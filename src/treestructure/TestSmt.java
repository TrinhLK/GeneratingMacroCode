package treestructure;

import java.util.ArrayList;
import java.util.StringJoiner;

public class TestSmt {

	public static void main(String[] args) {
		TestSmt tst = new TestSmt();
		ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> f_str = new ArrayList<String>();
		f_str.add("1");
		f_str.add("2");
		f_str.add("3");
		ArrayList<String> s_str = new ArrayList<String>(); 
		s_str.add("A");
//		s_str.add("B");
//		s_str.add("C");
		ArrayList<String> t_str = new ArrayList<String>(); 
		t_str.add("x");
		t_str.add("y");
		t_str.add("z");
		t_str.add("t");
		input.add(f_str);
		input.add(s_str);
		input.add(t_str);
		
		StringJoiner joiner = new StringJoiner(",");
		for (String item : s_str) {
		    joiner.add(item.toString());
		}
		System.out.println(joiner.toString());
//		return joiner.toString();
		
		tst.generatePermutations(input, result, 0, "");
		System.out.println(result);
	}
	
	void generatePermutations(ArrayList<ArrayList<String>> input, ArrayList<String> result, int depth, String current) {
	    if (depth == input.size()) {
	        result.add(current);
	        return;
	    }

	    for (int i = 0; i < input.get(depth).size(); i++) {
	        generatePermutations(input, result, depth + 1, current + input.get(depth).get(i) + "-");
	    }
	}
	
	void generatePermutations1(ArrayList<ArrayList<ArrayList<String>>> input, ArrayList<String> result, int depth, String current) {
	    if (depth == input.size()) {
	        result.add(current);
	        return;
	    }

	    for (int i = 0; i < input.get(depth).size(); i++) {
	        generatePermutations1(input, result, depth + 1, current + input.get(depth).get(i) + "-");
	    }
	}
}
