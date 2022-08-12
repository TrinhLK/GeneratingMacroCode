package treestructure2;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class MainTestFile {

	public void test() {
//		String connectorString = "[(p.1)`-(p.2)]`-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]";
//		String connectorString1 = "[(p.1)`-(p.2)]-(p.3)-[(p.4)-[(p.5)-(p.6)]]";
//		String connectorString2 = "[(p.1)-(p.2)]-(p.3)-[(p.4)-(p.5)]";
//		String connectorString = "[(p.1)-(p.2)]-(p.3)`-[(p.4)-(p.5)]";
		String connectorString = "[(p.1)-(p.1)]`-(p.3)";
		String connectorString1 = "[(p.1)-(p.2)]-[(p.3)-(p.4)]";
		String connectorString21 = "(p.1)-(p.2)-(p.3)-(p.4)";
		String connectorString22 = "[(p.1)-(p.2)]-(p.3)-(p.4)";
		String connectorString2 = "[(p.1)-(p.2)]`-(p.3)`-[(p.4)-(p.5)]";
		String connectorString3 = "[(p.1)-(p.2)]`-(p.3)-(p.4)";
		String connectorString4 = "(p.1)`-(p.2)-[(p.3)-(p.4)]";
		String connectorString5 = "[(p.1)`-(p.2)]-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]";
		String connectorString6 = "[(p.1)`-(p.2)]-[(p.3)`-(p.4)`-(p.5)]-(p.6)";
		String connectorString7 = "[(p.1)`-(p.2)]`-(p.3)-[(p.4)-(p.5)]";
		String connectorString8 = "(p.1)`-(p.2)-[(p.3)`-(p.4)`]";
		String connectorString9 = "(p.1)-[(p.2)`-(p.3)`]";
		String connectorString10 = "(p.1)`-[[(p.2)`-(p.21)`-[(p.2a)-(p.2b]]-[(p.3)`-(p.31)`-(p.3a)]]";
		String connectorString11 = "(p.1)`-(p.2)`-(p.3)";
		String connectorString12 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)-(p.3c)]`-[(p.3d)-(p.3e)]]"
				+ "-[(p.3a1)`-[(p.3b1)-(p.3c1)]`-[(p.3d1)-(p.3e1)]]";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString13 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)`-[(p.3c1)-(p.3c2)]`-(p.3e)]`-(p.3d)]";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString14 = "[(p.1a)`-[(p.1b)-(p.1c)]`]`-[(p.3a)`-[(p.3b)-(p.3c)]`-(p.3d)]";
		String connectorString15 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)-(p.3c)]`-(p.3d)]";
		String connectorString16 = "(MySQL.start)`-[(Tomcat.start)`-(Apache.start)]";
		String connectorString17 = "[(MySQL.running)-(MySQL.stop)-(Tomcat.running)-(MySQL.fail)]`-(Tomcat.start)";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString18 = "(Route.on)-(Monitor.add)";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString19 = "[(HerokuClearDBMySQL.on)-(HerokuPostgres.on)-(HerokuScoutAPM.on)-(HerokuNewRelicAPM.on)-(Deployer.setAddonsForUS)]`-(HerokuRegion.setAddonsForUS)";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString20 = "[(HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on) - (Deployer.setAddonsForEU)]` - (HerokuRegion.setAddonsForEU)";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
		String connectorString211 = "(Tracker.broadcast)`-[(Peer.listen)`-[(Peer.listen)-(Peer.listen)]`]" ;
		String connectorString212 = "(Tracker.broadcast)`-[(Peer.listen)-(Peer.listen)]-(Peer.listen)-(Peer.speak)" ;
		
		
		System.out.println("--- String 1: " + connectorString);
		System.out.println(genMacroCode(connectorString));
		
		System.out.println("--- String 2: " + connectorString1);
		System.out.println(genMacroCode(connectorString1));
		
		System.out.println("--- String 21: " + connectorString21);
		System.out.println(genMacroCode(connectorString21));
		
		System.out.println("--- String 22: " + connectorString22);
		System.out.println(genMacroCode(connectorString22));
		
		System.out.println("--- String 3: " + connectorString2);
		System.out.println(genMacroCode(connectorString2));
		
		System.out.println("--- String 4: " + connectorString3);
		System.out.println(genMacroCode(connectorString3));
//		
		System.out.println("--- String 5: " + connectorString4);
		System.out.println(genMacroCode(connectorString4));
//		
		System.out.println("--- String 6: " + connectorString5);
		System.out.println(genMacroCode(connectorString5));
//		
		System.out.println("--- String 7: " + connectorString6);
		System.out.println(genMacroCode(connectorString6));
//		
		System.out.println("--- String 8: " + connectorString7);
		System.out.println(genMacroCode(connectorString7));
//		
		System.out.println("--- String 9: " + connectorString8);
		System.out.println(genMacroCode(connectorString8));
//		
		System.out.println("--- String 10: " + connectorString9);
		System.out.println(genMacroCode(connectorString9));
//		
		System.out.println("--- String 11: " + connectorString10);
		System.out.println(genMacroCode(connectorString10));
//		
		System.out.println("--- String 12: " + connectorString11);
		System.out.println(genMacroCode(connectorString11));
//		
		System.out.println("--- String 13: " + connectorString12);
		System.out.println(genMacroCode(connectorString12));
//		
		System.out.println("--- String 14: " + connectorString13);
		System.out.println(genMacroCode(connectorString13));
//		
		System.out.println("--- String 15: " + connectorString14);
		System.out.println(genMacroCode(connectorString14));
//		
		System.out.println("--- String 16: " + connectorString15);
		System.out.println(genMacroCode(connectorString15));
//		
		System.out.println("--- String 17: " + connectorString16);
		System.out.println(genMacroCode(connectorString16));
		
		System.out.println("--- String 18: " + connectorString17);
		System.out.println(genMacroCode(connectorString17));
		
		System.out.println("--- String 18r: " + connectorString18);
		System.out.println(genMacroCode(connectorString18));
		
		System.out.println("--- String 19: " + connectorString19);
		System.out.println(genMacroCode(connectorString19));
		
		System.out.println("--- String 20: " + connectorString20);
		System.out.println(genMacroCode(connectorString20));
		
		System.out.println("--- String 211: " + connectorString211);
		System.out.println(genMacroCode(connectorString211));
		
		System.out.println("--- String 212: " + connectorString212);
		System.out.println(genMacroCode(connectorString212));
	}
	
	public void herokuListCoordination() {
		String herokuList[] = {
			"(HerokuDynoType.sub1) - (Deployer.setFreeDyno)",
			"(HerokuDynoType.sendDynoResponse) - (Deployer.receiveDynoResponse)",
			"(HerokuRegion.toUS) - (Deployer.setUSRegion)",
			"(HerokuRegion.toEU) - (Deployer.setEURegion)",
			"HerokuRegion.setAddonsForEU)` - (HerokuRegion.setAddonsForUS)` - (HerokuPostgres.on) - (HerokuClearDBMySQL.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on)",
			"[(HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on) - (Deployer.setAddonsForUS)]` - (HerokuRegion.setAddonsForUS)",
			"[(HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on) - (Deployer.setAddonsForEU)]` - (HerokuRegion.setAddonsForEU)",
			"(HerokuRegion.setAddonsForUS)` - (Deployer.setAddonsForUS)",
			"(HerokuRegion.setAddonsForEU)` - (Deployer.setAddonsForEU)",
			
			"(Deployer.setJava) - (HerokuBuildpack.setJava)",
			"(Deployer.setScala) - (HerokuBuildpack.setScala)",
			"(Deployer.setPython) - (HerokuBuildpack.setPython)",
			"(Deployer.setRuby) - (HerokuBuildpack.setRuby)",
			"(Deployer.setNodejs) - (HerokuBuildpack.setNodejs)",
			"(Deployer.setClojure) - (HerokuBuildpack.setClojure)",
			"(Deployer.setGradle) - (HerokuBuildpack.setGradle)",
			"(Deployer.setJvm) - (HerokuBuildpack.setJvm)",
			"(Deployer.setPhp) - (HerokuBuildpack.setPhp)",
			"(Deployer.setGo) - (HerokuBuildpack.setGo)",
			//210 -- Postgres doesn't support Gradle
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuPostgres.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuPostgres.on)",
			
			//221 -- ClearDB doesn't support Gradle
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuClearDBMySQL.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuClearDBMySQL.on)",
			
			//232 -- ScoutAPM doesn't support Java, Scala, NodeJS, Clojure, Gradle, Jvm, Go
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuScoutAPM.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuScoutAPM.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuScoutAPM.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuScoutAPM.off)",
			
			//243 -- NewRelicAPM doesn't support Scala, Clojure, Gradle, Go
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuNewRelicAPM.off)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuNewRelicAPM.off)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuNewRelicAPM.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuNewRelicAPM.off)",
			
			//403 -- 
			"[(Deployer.setAddonsForJava) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForJava)",
			"[(Deployer.setAddonsForScala) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForScala)",
			"[(Deployer.setAddonsForPython) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForPython)",
			"[(Deployer.setAddonsForRuby) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForRuby)",
			"[(Deployer.setAddonsForNodejs) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForNodejs)",
			"[(Deployer.setAddonsForClojure) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForClojure)",
			"[(Deployer.setAddonsForGradle) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForGradle)",
			"[(Deployer.setAddonsForJvm) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForJvm)",
			"[(Deployer.setAddonsForPhp) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForPhp)",
			"[(Deployer.setAddonsForGo) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForGo)",

			//543 --  set supported addons for each Language
			"(HerokuBuildpack.setAddonsForJava)` - (Deployer.setAddonsForJava)",
			"(HerokuBuildpack.setAddonsForScala)` - (Deployer.setAddonsForScala)",
			"(HerokuBuildpack.setAddonsForPython)` - (Deployer.setAddonsForPython)",
			"(HerokuBuildpack.setAddonsForRuby)` - (Deployer.setAddonsForRuby)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (Deployer.setAddonsForNodejs)",
			"(HerokuBuildpack.setAddonsForClojure)` - (Deployer.setAddonsForClojure)",
			"(HerokuBuildpack.setAddonsForGradle)` - (Deployer.setAddonsForGradle)",
			"(HerokuBuildpack.setAddonsForJvm)` - (Deployer.setAddonsForJvm)",
			"(HerokuBuildpack.setAddonsForPhp)` - (Deployer.setAddonsForPhp)",
			"(HerokuBuildpack.setAddonsForGo)` - (Deployer.setAddonsForGo)",

			// 665 Deployer add addons
			"(HerokuPostgres.sendAddonResponse)` - (Deployer.receiveAddonResponse)",
			"(Deployer.addHerokuPostgres1)` - (HerokuPostgres.sub1)",
			"(Deployer.addHerokuPostgres2)` - (HerokuPostgres.sub2)",
			"(Deployer.addClearDBMySQL1)` - (HerokuClearDBMySQL.sub1)",
			"(Deployer.addScoutAPM1)` - (HerokuScoutAPM.sub1)",
			"(Deployer.addNewRelicAPM1)` - (HerokuNewRelicAPM.sub1)",
			
			//705 Reset setup
			"(Deployer.resetAll)` - (HerokuDynoType.reset1)",
			"(Deployer.resetAll)` - (HerokuRegion.USreset)",
			"(Deployer.resetAll)` - (HerokuRegion.EUreset)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeJava)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeScala)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeJvm)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removePython)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeRuby)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeNodejs)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeClojure)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeGradle)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removePhp)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeGo)",
			
			//721 
			"(Deployer.resetAll)` - (HerokuPostgres.off)",
			"(Deployer.resetAll)` - (HerokuPostgres.reset1)",
			"(Deployer.resetAll)` - (HerokuPostgres.reset2)",
			
			"(Deployer.resetAll)` - (HerokuClearDBMySQL.off)",
			"(Deployer.resetAll)` - (HerokuClearDBMySQL.reset1)",
			
			"(Deployer.resetAll)` - (HerokuScoutAPM.off)",
			"(Deployer.resetAll)` - (HerokuScoutAPM.reset1)",
			
			"(Deployer.resetAll)` - (HerokuNewRelicAPM.off)",
			"(Deployer.resetAll)` - (HerokuNewRelicAPM.reset1)"
		};
		System.out.println("Heroku list's length: " + herokuList.length);
		for (int i=0 ; i<herokuList.length ; i++) {
			System.out.println("// connector_" + (i+1) + ":" + herokuList[i]);
			System.out.println(genMacroCode(herokuList[i]));
		}
	}
	
	public void herokuListCoordination1() {
		String herokuList[] = {
			"(HerokuDynoType.sub1) - (Deployer.setFreeDyno)",
			"(HerokuDynoType.sendDynoResponse) - (Deployer.receiveDynoResponse)",
			"(HerokuRegion.toUS) - (Deployer.setUSRegion)",
			"(HerokuRegion.toEU) - (Deployer.setEURegion)",
			"HerokuRegion.setAddonsForEU)` - (HerokuRegion.setAddonsForUS)` - (HerokuPostgres.on) - (HerokuClearDBMySQL.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on)",
			"[(HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on) - (Deployer.setAddonsForUS)]` - (HerokuRegion.setAddonsForUS)",
			"[(HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.on) - (HerokuNewRelicAPM.on) - (Deployer.setAddonsForEU)]` - (HerokuRegion.setAddonsForEU)",
			"(HerokuRegion.setAddonsForUS)` - (Deployer.setAddonsForUS)",
			"(HerokuRegion.setAddonsForEU)` - (Deployer.setAddonsForEU)",
			
			"(Deployer.setJava) - (HerokuBuildpack.setJava)",
			"(Deployer.setScala) - (HerokuBuildpack.setScala)",
			"(Deployer.setPython) - (HerokuBuildpack.setPython)",
			"(Deployer.setRuby) - (HerokuBuildpack.setRuby)",
			"(Deployer.setNodejs) - (HerokuBuildpack.setNodejs)",
			"(Deployer.setClojure) - (HerokuBuildpack.setClojure)",
			"(Deployer.setGradle) - (HerokuBuildpack.setGradle)",
			"(Deployer.setJvm) - (HerokuBuildpack.setJvm)",
			"(Deployer.setPhp) - (HerokuBuildpack.setPhp)",
			"(Deployer.setGo) - (HerokuBuildpack.setGo)",
			//210 -- Postgres doesn't support Gradle
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuPostgres.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuPostgres.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuPostgres.on)",
			
			//221 -- ClearDB doesn't support Gradle
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuClearDBMySQL.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuClearDBMySQL.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuClearDBMySQL.on)",
			
			//232 -- ScoutAPM doesn't support Java, Scala, NodeJS, Clojure, Gradle, Jvm, Go
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuScoutAPM.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuScoutAPM.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuScoutAPM.off)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuScoutAPM.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuScoutAPM.off)",
			
			//243 -- NewRelicAPM doesn't support Scala, Clojure, Gradle, Go
			"(HerokuBuildpack.setAddonsForJava)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForScala)` - (HerokuNewRelicAPM.off)",
			"(HerokuBuildpack.setAddonsForPython)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForRuby)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForClojure)` - (HerokuNewRelicAPM.off)",
			"(HerokuBuildpack.setAddonsForGradle)` - (HerokuNewRelicAPM.off)",
			"(HerokuBuildpack.setAddonsForJvm)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForPhp)` - (HerokuNewRelicAPM.on)",
			"(HerokuBuildpack.setAddonsForGo)` - (HerokuNewRelicAPM.off)",
			
			//403 -- 
			"[(Deployer.setAddonsForJava) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForJava)",
			"[(Deployer.setAddonsForScala) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForScala)",
			"[(Deployer.setAddonsForPython) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForPython)",
			"[(Deployer.setAddonsForRuby) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForRuby)",
			"[(Deployer.setAddonsForNodejs) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForNodejs)",
			"[(Deployer.setAddonsForClojure) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForClojure)",
			"[(Deployer.setAddonsForGradle) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForGradle)",
			"[(Deployer.setAddonsForJvm) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForJvm)",
			"[(Deployer.setAddonsForPhp) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForPhp)",
			"[(Deployer.setAddonsForGo) - (HerokuClearDBMySQL.on) - (HerokuPostgres.on) - (HerokuScoutAPM.off) - (HerokuNewRelicAPM.on)]` - (HerokuBuildpack.setAddonsForGo)",

			//543 --  set supported addons for each Language
			"(HerokuBuildpack.setAddonsForJava)` - (Deployer.setAddonsForJava)",
			"(HerokuBuildpack.setAddonsForScala)` - (Deployer.setAddonsForScala)",
			"(HerokuBuildpack.setAddonsForPython)` - (Deployer.setAddonsForPython)",
			"(HerokuBuildpack.setAddonsForRuby)` - (Deployer.setAddonsForRuby)",
			"(HerokuBuildpack.setAddonsForNodejs)` - (Deployer.setAddonsForNodejs)",
			"(HerokuBuildpack.setAddonsForClojure)` - (Deployer.setAddonsForClojure)",
			"(HerokuBuildpack.setAddonsForGradle)` - (Deployer.setAddonsForGradle)",
			"(HerokuBuildpack.setAddonsForJvm)` - (Deployer.setAddonsForJvm)",
			"(HerokuBuildpack.setAddonsForPhp)` - (Deployer.setAddonsForPhp)",
			"(HerokuBuildpack.setAddonsForGo)` - (Deployer.setAddonsForGo)",

			// 665 Deployer add addons
			"(HerokuPostgres.sendAddonResponse)` - (Deployer.receiveAddonResponse)",
			"(Deployer.addHerokuPostgres)` - (HerokuPostgres.sub1)",
			"(Deployer.addClearDBMySQL)` - (HerokuClearDBMySQL.sub1)",
			"(Deployer.addScoutAPM)` - (HerokuScoutAPM.sub1)",
			"(Deployer.addNewRelicAPM)` - (HerokuNewRelicAPM.sub1)",
			
			//705 Reset setup
			"(Deployer.resetAll)` - (HerokuDynoType.reset)",
			"(Deployer.resetAll)` - (HerokuRegion.USreset)",
			"(Deployer.resetAll)` - (HerokuRegion.EUreset)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeJava)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeScala)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeJvm)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removePython)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeRuby)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeNodejs)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeClojure)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeGradle)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removePhp)",
			"(Deployer.resetAll)` - (HerokuBuildpack.removeGo)",
			
			//721 
			"(Deployer.resetAll)` - (HerokuPostgres.off)",
			"(Deployer.resetAll)` - (HerokuPostgres.reset)",
			
			"(Deployer.resetAll)` - (HerokuClearDBMySQL.off)",
			"(Deployer.resetAll)` - (HerokuClearDBMySQL.reset)",
			
			"(Deployer.resetAll)` - (HerokuScoutAPM.off)",
			"(Deployer.resetAll)` - (HerokuScoutAPM.reset)",
			
			"(Deployer.resetAll)` - (HerokuNewRelicAPM.off)",
			"(Deployer.resetAll)` - (HerokuNewRelicAPM.reset)"
		};
		System.out.println("Heroku list's length: " + herokuList.length);
		for (int i=0 ; i<herokuList.length ; i++) {
			System.out.println("// connector_" + (i+1) + ":" + herokuList[i]);
			System.out.println(genMacroCode(herokuList[i]));
		}
	}
	public static void main(String[] args) {
		MainTestFile testMT = new MainTestFile();
		testMT.test();
//		testMT.herokuListCoordination1();
	}
	
	/**
	 * Generate requires + accepts functions for Glue Builder
	 * */
	public String genMacroCode(String connectorString) {
		TreeNode root = new TreeNode("root.null", false, null);
		
		createTree(root, connectorString, 0);
		root.addExportedPort();
//		System.out.println("----Added Export list----");
		root.traversal();
//		System.out.println("--------");
		//--- Gen Accepts code
		ArrayList<TreeNode> listLeaves = new ArrayList<TreeNode>();
		tree2List(root, listLeaves);
//		System.out.println("list Leave: " + listLeaves.size() + "\t" + listLeaves);
//		TreeNode reducedTree = renewTree(root);
//		reducedTree.traversal();
//		return genAcceptsCode(listLeaves);
		return root.genRequiresCode("") + "\n" + genAcceptsCode(listLeaves);
	}
	
	/**
	 * Gen ACCEPTS Code
	 * */
	public String genAcceptsCode(ArrayList<TreeNode> input) {
		String rs = "";
		ArrayList<String> acceptsList = new ArrayList<String>();
		
		for (int i=0 ; i<input.size() ; i++) {
			String tmp_accept = "\t\tport(" + input.get(i).getComponentTypeName() + "Connector.class, \"" + input.get(i).getPortTypeName() + "\")"
					+ ".accepts(";
			StringJoiner joiner = new StringJoiner(", ");
			ArrayList<String> listAcceptElements = new ArrayList<String>();
			for (int j=0 ; j<input.size() ; j++) {
				if (i != j) {
					String s = input.get(j).getComponentTypeName() + "Connector.class, \"" + input.get(j).getPortTypeName() + "\"";
					listAcceptElements.add(s);
				}
			}
			ArrayList<String> newList = (ArrayList<String>) listAcceptElements.stream().distinct().collect(Collectors.toList());
			for (String element : newList) {
				joiner.add(element);
			}
			tmp_accept += joiner.toString() + ");\n";
			acceptsList.add(tmp_accept);
		}
		ArrayList<String> newAcceptsList = (ArrayList<String>) acceptsList.stream().distinct().collect(Collectors.toList());
		for (String acceptSentence : newAcceptsList) {
			rs += acceptSentence;
		}
//		for (int i=0 ; i<input.size() ; i++) {
//			rs += "\t\tport(" + input.get(i).getComponentTypeName() + "Connector.class, \"" + input.get(i).getPortTypeName() + "\")"
//					+ ".accepts(";
//			StringJoiner joiner = new StringJoiner(", ");
//			ArrayList<String> listAcceptElements = new ArrayList<String>();
//			for (int j=0 ; j<input.size() ; j++) {
//				if (i != j) {
//					String s = input.get(j).getComponentTypeName() + "Connector.class, \"" + input.get(j).getPortTypeName() + "\"";
//					listAcceptElements.add(s);
//				}
//			}
//			ArrayList<String> newList = (ArrayList<String>) listAcceptElements.stream().distinct().collect(Collectors.toList());
//			for (String element : newList) {
//				joiner.add(element);
//			}
//			rs += joiner.toString() + ");\n";
//		}
		
		return rs;
	}
	
	public void createTree(TreeNode root, String connectorString, int index){
		
		if (connectorString.length() == 0)
			return ;

		int pos = connectorString.indexOf('[');
		//if it's flat
		if (pos == -1) {
			String[] elems = connectorString.split("-");
			//for all elements
			for(String e : elems) {
				String content = e.trim();
				boolean isTrigger = false;
				
				if (content.contains("`")) {
					isTrigger = true;
				}
				
				if (!content.equals("")) {
					TreeNode elem = new TreeNode(content, isTrigger, root);
					root.getChildren().add(elem);
				}
				
			}
			return ;
		} else {
			Stack<Character> stack = new Stack<>();
			stack.push(connectorString.charAt(pos));
			int q = pos + 1;
			while (q < connectorString.length()) {
				if (connectorString.charAt(q) == ']') {
					if (!stack.empty())
						stack.pop();
				} else if (connectorString.charAt(q) == '[') {
					stack.push(connectorString.charAt(q));
				}
				q++;
				if (stack.empty())
					break;
			}
			
			//before compound
			String baseLevelConnector = connectorString.substring(0, pos);
			createTree(root, baseLevelConnector, index);
			
			//the compound
			String nextLevelConnector = connectorString.substring(pos + 1, q-1);
//			System.out.println("nextLevelConnector: " + connectorString);
//			System.out.println(connectorString.indexOf("]"));
			
			
			boolean isTrigger = false;
//			System.out.println("test substr: " + connectorString);
			if (q + 1 <= connectorString.length()) {
				String temp = connectorString.substring(pos, q+1);
				if (temp.charAt(temp.length()-1) == '`')
					isTrigger = true;
			}
			
			TreeNode compound = new TreeNode("c" + pos + index + ".null", isTrigger, root);
			root.getChildren().add(compound);
//			System.out.println("nextLevelConnector: " + nextLevelConnector);
			createTree(compound, nextLevelConnector, index+1);
			
			//after compound
			if (q + 1 < connectorString.length()) {
				index = index+10;
				String remainStr = connectorString.substring(q + 1, connectorString.length());
				if (remainStr.indexOf("-") == 0) {
					remainStr = remainStr.substring(1);
				}
//				System.out.println("After: " + remainStr);
				createTree(root, remainStr, index+1);
			}
		}
	}
	
	public void tree2List(TreeNode root, ArrayList<TreeNode> listNodes) {
		
		if (root.getChildren().size() == 0) {
			listNodes.add(root);
			return ;
		}
		for (TreeNode child : root.getChildren()) {
			tree2List(child, listNodes);
		}
		
	}
}
