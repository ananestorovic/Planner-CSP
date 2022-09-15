/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.etf;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author ana
 */
public class FileParser {

    // work hour: 09:00 - 17:00
    static final int startHours = 9;
    static final int startMins = 0;
    static final int endHours = 17;

    //limitation: the duration of meetings is equal 30 min * n
    static final int interval = 30;

    static final int endMins = 0;

    private final String[] daysWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private int sHours, sMins, eHours, eMins;
    private HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> unavailable;
    private HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> available;

    private List<Pair<String, Integer>> infoAboutMeetings;

    private HashMap<String, List<Integer>> employee;

    private HashMap<String, List<String>> teamConstraint;

    private List<Quartet<Integer, Integer, Integer, Integer>> wholeDay;

    private HashMap<String, List<Pair<String, Boolean>>> teamsAndMeetings;


    public FileParser() {

        teamConstraint = new HashMap<>(Map.of());
        employee = new HashMap<>(Map.of());
        unavailable = new HashMap<>(Map.of());
        available = new HashMap<>(Map.of());
        teamsAndMeetings = new HashMap<>(Map.of());
        infoAboutMeetings = new ArrayList<Pair<String, Integer>>();

        wholeDay = new ArrayList<>();

        sHours = eHours = startHours;
        sMins = startMins;
        eMins += interval;

        while (eHours < endHours) {
            Quartet<Integer, Integer, Integer, Integer> element = new Quartet<>(sHours, sMins, eHours, eMins);
            sMins += interval;
            if (sMins % (2 * interval) == 0) {
                eHours = ++sHours;
                sMins = 0;
                eMins = interval;
            } else {
                eHours++;
                eMins = 0;
            }

            wholeDay.add(element);

        }


    }


    public HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> getUnavailable() {
        return unavailable;
    }

    public List<Pair<String, Integer>> getInfoAboutMeetings() {
        return infoAboutMeetings;
    }

    public HashMap<String, List<String>> getTeamConstraint() {
        return teamConstraint;
    }

    public HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> getAvailable() {
        return available;
    }

    public HashMap<String, List<Pair<String, Boolean>>> getTeamsAndMeetings() {
        return teamsAndMeetings;
    }

    @SuppressWarnings("unchecked")
    public void parseTeamsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray teamsList = (JSONArray) obj;

            //Iterate over requests array
            teamsList.forEach(team -> parseTeamObject((JSONObject) team));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void parseTeamObject(JSONObject team) {
        //Get team object within list
        JSONObject teamObject = (JSONObject) team.get("team");

        //Get informations about team
        String name = (String) teamObject.get("name");

        HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>> daysWithHoursOff = new HashMap<>(Map.of());

        HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>> daysWithHoursOn = new HashMap<>(Map.of());

        for (String day : daysWeek) {
            daysWithHoursOff.put(day, new ArrayList<>());
            daysWithHoursOn.put(day, new ArrayList<>(wholeDay));
        }

        unavailable.put(name, daysWithHoursOff);
        available.put(name, daysWithHoursOn);

        employee.put(name, new ArrayList<>());

        teamsAndMeetings.put(name, new ArrayList<>());

        teamConstraint.put(name, new ArrayList<>());

    }

    @SuppressWarnings("unchecked")
    public void parseRequestsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray requestsList = (JSONArray) obj;

            //Iterate over requests array
            requestsList.forEach(req -> parseRequestObject((JSONObject) req));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseRequestObject(JSONObject request) {
        //Get request object within list
        JSONObject requestObject = (JSONObject) request.get("request");

        //Get informations about request
        String id = (String) requestObject.get("id");
        String team = (String) requestObject.get("team");
        String dayOff = (String) requestObject.get("dayOff");
        String startTimeOff = (String) requestObject.get("startTimeOff");
        String endTimeOff = (String) requestObject.get("endTimeOff");
        String startHours = StringUtils.substringBefore(startTimeOff, ":");
        String startMinutes = StringUtils.substringAfter(startTimeOff, ":");
        String endHours = StringUtils.substringBefore(endTimeOff, ":");
        String endMinutes = StringUtils.substringAfter(endTimeOff, ":");

        Quartet<Integer, Integer, Integer, Integer> element = new Quartet<>(Integer.parseInt(startHours), Integer.parseInt(startMinutes), Integer.parseInt(endHours), Integer.parseInt(endMinutes));

        unavailable.get(team).get(dayOff).add(element);

        employee.get(team).add(Integer.parseInt(id));

    }

    @SuppressWarnings("unchecked")
    public void parseMeetingsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray meetingsList = (JSONArray) obj;

            //Iterate over requests array
            meetingsList.forEach(meeting -> parseMeetingObject((JSONObject) meeting));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseMeetingObject(JSONObject meeting) {
        //Get meeting object within list
        JSONObject meetingObject = (JSONObject) meeting.get("meeting");

        //Get informations about meeting
        String name = (String) meetingObject.get("name");
        String duration = (String) meetingObject.get("duration");

        infoAboutMeetings.add(new Pair<String, Integer>(name, Integer.parseInt(duration)));

    }

    public void generateTeamConstraint() {

        employee.forEach((k, v) -> {

            employee.forEach((key, val) -> {

                if (!k.equals(key)) {
                    if (v.stream().anyMatch(element -> val.contains(element))) {
                        teamConstraint.get(k).add(key);
                    }
                }

            });

        });

    }

    public void sortUnavailable(HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> table) {

        table.forEach((k, v) -> {

            for (String day : daysWeek) {

                List<Quartet<Integer, Integer, Integer, Integer>> oldList = table.get(k).get(day);

                oldList.sort(new Comparator<Quartet<Integer, Integer, Integer, Integer>>() {
                    @Override
                    public int compare(Quartet<Integer, Integer, Integer, Integer> o1, Quartet<Integer, Integer, Integer, Integer> o2) {
                        if (o1.getValue0() < o2.getValue0()) {
                            return -1;
                        } else if (o1.getValue0() > o2.getValue0()) {
                            return 1;
                        } else {
                            if (o1.getValue1() < o2.getValue1()) {
                                return -1;
                            } else if (o1.getValue1() > o2.getValue1()) {
                                return 1;
                            } else {
                                if (o1.getValue2() < o2.getValue2()) {
                                    return -1;
                                } else if (o1.getValue2() > o2.getValue2()) {
                                    return 1;
                                } else {
                                    if (o1.getValue3() < o2.getValue3()) {
                                        return -1;
                                    } else if (o1.getValue3() > o2.getValue3()) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }

                                }
                            }
                        }

                    }

                });

                table.get(k).replace(day, oldList);

            }

        });

    }


    public void generateAvailable() {

        getUnavailable().forEach((k, v) -> {

            boolean flagToPass = false;

            for (String day : daysWeek) {

                flagToPass = false;

                List<Quartet<Integer, Integer, Integer, Integer>> listOfUnavailable = getUnavailable().get(k).get(day);
                List<Quartet<Integer, Integer, Integer, Integer>> listOfAvailable = getAvailable().get(k).get(day);
                List<Quartet<Integer, Integer, Integer, Integer>> helper = new ArrayList<>();


                for (Quartet<Integer, Integer, Integer, Integer> e1 : listOfUnavailable) {

                    if (flagToPass) break;

                    for (Quartet<Integer, Integer, Integer, Integer> e2 : listOfAvailable) {

                        if (flagToPass) break;

                        //case when someone requested whole day off
                        if (e1.getValue0().equals(startHours) && e1.getValue1().equals(startMins) &&
                                e1.getValue2().equals(endHours) && e1.getValue3().equals(endMins)) {

                            flagToPass = true;
                            helper = wholeDay;

                        } else {

                            if (e2.getValue0() > e1.getValue0() && e2.getValue2() < e1.getValue2()
                                    && e2.getValue0() > e1.getValue0() && e2.getValue2() < e1.getValue2())
                                helper.add(e2); //between
                            else if (e2.getValue0().equals(e1.getValue0()) && e2.getValue1().equals(e1.getValue1())
                                    && e2.getValue0().equals(e1.getValue0()) && e2.getValue1().equals(e1.getValue1()))
                                helper.add(e2); //start
                            else if (e1.getValue2().equals(e2.getValue2()) && e1.getValue3().equals(e2.getValue3())
                                    && e1.getValue2().equals(e2.getValue2()) && e1.getValue3().equals(e2.getValue3()))
                                helper.add(e2); //end


                        }


                    }

                }

                if (!helper.isEmpty()) {
                    listOfAvailable.removeAll(helper);
                    getAvailable().get(k).replace(day, listOfAvailable);
                }

            }


        });

    }

    public void generateTeamsAndMeetings(){
        teamsAndMeetings.forEach((k,v)->{
            for(Pair<String, Integer> meeting : infoAboutMeetings) {
                Pair<String, Boolean> m = new Pair<>(meeting.getValue0(), false);
                teamsAndMeetings.get(k).add(m);
            }

        });
    }

}
