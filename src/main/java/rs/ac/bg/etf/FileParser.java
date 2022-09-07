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

  
    HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> teams = new HashMap<>(Map.of());
    List<Triplet<String, Integer, Integer>> infoAboutMeetings = new ArrayList<>();

    String daysWeek[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> getTeams() {
        return teams;
    }

    public List<Triplet<String, Integer, Integer>> getInfoAboutMeetings() {
        return infoAboutMeetings;
    }

    public String[] getDaysWeek() {
        return daysWeek;
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

        if (!teams.containsKey(team)) {

            HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>> daysWithHoursOff = new HashMap<>(Map.of());

            for (String day : getDaysWeek()) {
                daysWithHoursOff.put(day, new ArrayList<>());
            }

            teams.put(name, daysWithHoursOff);

        }

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
        String team = (String) requestObject.get("team");
        String dayOff = (String) requestObject.get("dayOff");
        String startTimeOff = (String) requestObject.get("startTimeOff");
        String endTimeOff = (String) requestObject.get("endTimeOff");
        String startHours = StringUtils.substringBefore(startTimeOff, ":");
        String startMinutes = StringUtils.substringAfter(startTimeOff, ":");
        String endHours = StringUtils.substringBefore(endTimeOff, ":");
        String endMinutes = StringUtils.substringAfter(endTimeOff, ":");

        Quartet<Integer, Integer, Integer, Integer> element = new Quartet<>(Integer.parseInt(startHours), Integer.parseInt(startMinutes), Integer.parseInt(endHours), Integer.parseInt(endMinutes));

        teams.get(team).get(dayOff).add(element);

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

}
