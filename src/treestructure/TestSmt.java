package treestructure;

import java.util.ArrayList;
import java.util.StringJoiner;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

public class TestSmt {

	public static void main(String[] args) {
		TestSmt tst = new TestSmt();
		ArrayList<ArrayList<String>> input1 = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> input2 = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> input3 = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> f_str1 = new ArrayList<String>();
		ArrayList<String> f_str2 = new ArrayList<String>();
		f_str1.add("p1a");
		f_str2.add("p1b");
		f_str2.add("p1c");
		input1.add(f_str1);
		input1.add(f_str2);
		ArrayList<String> s_str = new ArrayList<String>(); 
		s_str.add("p2");
//		s_str.add("B");
//		s_str.add("C");
		input2.add(s_str);
		ArrayList<String> t_str1 = new ArrayList<String>();
		ArrayList<String> t_str2 = new ArrayList<String>();
		t_str1.add("p3a");
		t_str2.add("p3b");
		t_str2.add("p3c");
//		t_str.add("z");
//		t_str.add("t");
		input3.add(t_str1);
		input3.add(t_str2);
//		input.add(f_str);
//		input.add(s_str);
//		input.add(t_str);
		result = tst.merge2ArrayList(input1, input2);
		result = tst.merge2ArrayList(result, input3);
		tst.Sync2Trig(input3, input1);
//		StringJoiner joiner = new StringJoiner(",");
//		for (String item : s_str) {
//		    joiner.add(item.toString());
//		}
//		System.out.println(joiner.toString());
////		return joiner.toString();
//		
////		tst.generatePermutations(input, result, 0, "");
//		System.out.println(result);
	}
	
	public ArrayList<ArrayList<String>> merge2ArrayList(ArrayList<ArrayList<String>> first, ArrayList<ArrayList<String>> second) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> f_elem : first) {
			for (ArrayList<String> s_elem : second) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.addAll(f_elem);
				temp.addAll(s_elem);
				result.add(temp);
			}
		}
		System.out.println(result);
		return result;
	}
	
	public String Sync2Trig(ArrayList<ArrayList<String>> synchExport, ArrayList<ArrayList<String>> trigExport) {
		String result = "";
		for (ArrayList<String> elemOfSynchExport : synchExport) {
			
			if (elemOfSynchExport.size() == 1) {//[[p1][p2]]
				for (ArrayList<String> elemOfTrigExport : trigExport) {
					StringJoiner joiner = new StringJoiner(", ");
					result += "\t\tport(" + elemOfSynchExport.get(0) + "Connector.class, \"" + elemOfSynchExport.get(0) + "\")"
							+ ".requires(";
					for (String elemI : elemOfTrigExport) {
						String tempTrig = elemI + "Connector.class, \"" + elemI + "\"";
						joiner.add(tempTrig);
					}
					result += joiner.toString() + ");\n";
				}
			}else {//[[p1,p2][p3,p4]]
				for (String subElemI : elemOfSynchExport) {
//					result += "\t\tport(" + subElemI + "Connector.class, \"" + subElemI + "\")"
//							+ ".requires(";
//					StringJoiner joiner = new StringJoiner(", ");
					
					for (ArrayList<String> elemOfTrigExport : trigExport) {
						StringJoiner joiner = new StringJoiner(", ");
						result += "\t\tport(" + subElemI + "Connector.class, \"" + subElemI + "\")"
								+ ".requires(";
						
						for (String subElemJ : elemOfSynchExport) {
							if (subElemI != subElemJ) {
								joiner.add(subElemJ + "Connector.class, \"" + subElemJ + "\"");
							}
						}
						for (String elemI : elemOfTrigExport) {
							String tempTrig = elemI + "Connector.class, \"" + elemI + "\"";
							joiner.add(tempTrig);
						}
						result += joiner.toString() + ");\n";
					}
				}
			}
		}
		
		
		System.out.println(result);
		return result;
	}
}
