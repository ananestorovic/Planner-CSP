/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package rs.ac.bg.etf;

import org.javatuples.Pair;

import java.util.List;
import java.util.Map;

/**
 *
 * @author ana
 */
public class PlannerCSP {

    public static void main(String[] args) {
        FileParser fp = new FileParser();
        CSPAlgorithm csp = new CSPAlgorithm(fp);

        fp.parseMeetingsFile("resources/meetings.json");
        fp.parseTeamsFile("resources/teams.json");
        fp.parseRequestsFile("resources/requests.json");
        fp.generateTeamConstraint(); // just once i need to generate this

        fp.generateAvailable(); //initial table, later we will update this
        Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> a = fp.getAvailable();

        Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution = fp.generateEmptySolution();
        csp.backtracking(fp.getTeamConstraint(), fp.getInfoAboutMeetings(), solution,
                fp.getAvailable());

        fp.printSolution(solution);




//        for (int i = 0; i < fp.getInfoAboutMeetings().size(); i++) {
//            System.out.println(fp.getInfoAboutMeetings().get(i));
//        }
//
//        for (String name : fp.getUnavailable().keySet()) {
//            String key = name.toString();
//            String value = fp.getUnavailable().get(name).toString();
//            System.out.println(key + " " + value);
//
//        }
//
//        for (String name : fp.getTeamConstraint().keySet()) {
//            String key = name.toString();
//            String value = fp.getTeamConstraint().get(name).toString();
//            System.out.println(key + " " + value);
//
//        }
//        



//        for (String name : fp.getUnavailable().keySet()) {
//            String key = name.toString();
//            String value = fp.getUnavailable().get(name).toString();
//            System.out.println(key + " " + value);
//
//        }



//        for (String name : fp.getAvailable().keySet()) {
//            String key = name.toString();
//            String value = fp.getAvailable().get(name).toString();
//            System.out.println(key + " " + value);
//
//        }

//

    }
}
