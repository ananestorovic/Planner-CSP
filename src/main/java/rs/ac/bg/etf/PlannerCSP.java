/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package rs.ac.bg.etf;

/**
 *
 * @author ana
 */
public class PlannerCSP {

    public static void main(String[] args) {
        FileParser fp = new FileParser();

        fp.parseMeetingsFile("resources/meetings.json");
        fp.parseTeamsFile("resources/teams.json");
        fp.parseRequestsFile("resources/requests.json");
        fp.generateTeamConstraint();

//        for (int i = 0; i < fp.getInfoAboutMeetings().size(); i++) {
//            System.out.println(fp.getInfoAboutMeetings().get(i));
//        }
//
        for (String name : fp.getTeams().keySet()) {
            String key = name.toString();
            String value = fp.getTeams().get(name).toString();
            System.out.println(key + " " + value);

        }
//
//        for (String name : fp.getTeamConstraint().keySet()) {
//            String key = name.toString();
//            String value = fp.getTeamConstraint().get(name).toString();
//            System.out.println(key + " " + value);
//
//        }
//        

        fp.sortUnavailable(fp.getTeams());

        for (String name : fp.getTeams().keySet()) {
            String key = name.toString();
            String value = fp.getTeams().get(name).toString();
            System.out.println(key + " " + value);

        }
//

    }
}
