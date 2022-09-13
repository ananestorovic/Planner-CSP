/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.etf;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ana
 */
public class FileParser {

    static final int startHours = 9;
    static final int startMin = 0;
    static final int endHours = 17;
    static final int endMin = 0;

    private final HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> unavailable = new HashMap<>(Map.of());
    private final HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> available = new HashMap<>(Map.of());

    private final List<Triplet<String, Integer, Integer>> infoAboutMeetings = new ArrayList<>();

    private final String[] daysWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private final HashMap<String, List<Integer>> employee = new HashMap<>(Map.of());

    private final HashMap<String, List<String>> teamConstraint = new HashMap<>(Map.of());

    public HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> getTeams() {
        return unavailable;
    }

    public List<Triplet<String, Integer, Integer>> getInfoAboutMeetings() {
        return infoAboutMeetings;
    }

    public HashMap<String, List<String>> getTeamConstraint() {
        return teamConstraint;
    }

    
    @SuppressWarnings("unchecked")
    public void parseTeamsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try ( FileReader reader = new FileReader(fileName)) {
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
            daysWithHoursOn.put(day, new ArrayList<>());
        }

        unavailable.put(name, daysWithHoursOff);
        available.put(name, daysWithHoursOn);

        employee.put(name, new ArrayList<>());

        teamConstraint.put(name, new ArrayList<>());

    }

    @SuppressWarnings("unchecked")
    public void parseRequestsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try ( FileReader reader = new FileReader(fileName)) {
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

        try ( FileReader reader = new FileReader(fileName)) {
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
        String timesInWeek = (String) meetingObject.get("timesInWeek");

        infoAboutMeetings.add(new Triplet<String, Integer, Integer>(name, Integer.parseInt(duration), Integer.parseInt(timesInWeek)));

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
    
    
    public void generateAvailable(HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> table){ //bilo bi lijepo da ti ovo vraca mapu
        
        List<Quartet<Integer, Integer, Integer, Integer>> listOfAvailable = new ArrayList<>();
        
        Quartet<Integer, Integer, Integer, Integer> element = new Quartet<>(startHours, startMin, endHours, endMin);
        
        listOfAvailable.add(element);
        
        table.forEach((k, v) ->{
            
            for (String day: daysWeek){
                
                List<Quartet<Integer, Integer, Integer, Integer>> listOfUnavailable = table.get(k).get(day);
                
                
                
                
                
                
                
                
                
            }
            
        });
        
    }

}
