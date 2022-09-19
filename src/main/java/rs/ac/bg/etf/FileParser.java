/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.etf;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

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
    static final int START_HOURS = 9;
    static final int START_MINS = 0;
    static final int END_HOURS = 17;
    static final int END_MINS = 0;

    //limitation: the duration of meetings is equal 30 min * n
    static final int INTERVAL = 30;


    private Map<String, Map<DayOfWeek, List<TimeInterval>>> unavailable;
    private Map<Pair<String,String>, Map<DayOfWeek, List<TimeInterval>>> available;
    private List<Pair<String, Integer>> infoAboutMeetings;

    private Map<String, List<Integer>> employee;

    private Map<String, List<String>> teamConstraint;

    private List<TimeInterval> allTimeIntervals;

    private List<String> teams;

    private List<String> meetings;


    public FileParser() {

        teamConstraint = new HashMap<>();
        employee = new HashMap<>();
        unavailable = new HashMap<>();
        available = new HashMap<>();
        infoAboutMeetings = new ArrayList<>();

        teams = new ArrayList<>();
        meetings = new ArrayList<>();
        allTimeIntervals = new ArrayList<>();

        makeAllPartsOfDay();
    }

    private void makeAllPartsOfDay() {

        int sHours = 0;
        int sMins = 0;
        int eHours = 0;
        int eMins = 0;

        sHours = eHours = START_HOURS;
        sMins = START_MINS;
        eMins += INTERVAL;

        while (eHours < END_HOURS) {
            TimeInterval element = new TimeInterval(sHours, sMins, eHours, eMins);
            sMins += INTERVAL;
            if (sMins % (2 * INTERVAL) == 0) {
                eHours = ++sHours;
                sMins = 0;
                eMins = INTERVAL;
            } else {
                eHours++;
                eMins = 0;
            }
            allTimeIntervals.add(element);
        }
    }


    public Map<String, Map<DayOfWeek, List<TimeInterval>>> getUnavailable() {
        return unavailable;
    }

    public List<Pair<String, Integer>> getInfoAboutMeetings() {
        return infoAboutMeetings;
    }

    public Map<String, List<String>> getTeamConstraint() {
        return teamConstraint;
    }

    public Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> getAvailable() {
        return available;
    }

    public void parseTeamsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray teamsList = (JSONArray) obj;

            //Iterate over requests array
            teamsList.forEach(team -> processTeamObject((JSONObject) team));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }


    private void processTeamObject(JSONObject team) {
        //Get team object within list
        JSONObject teamObject = (JSONObject) team.get("team");

        //Get information about team
        String name = (String) teamObject.get("name");

        HashMap<DayOfWeek, List<TimeInterval>> daysWithHoursOff = getDaysWithHoursOff();

        teams.add(name);

        unavailable.put(name, daysWithHoursOff);

        employee.put(name, new ArrayList<>());

        teamConstraint.put(name, new ArrayList<>());

    }

    private HashMap<DayOfWeek, List<TimeInterval>> getDaysWithHoursOn() {
        HashMap<DayOfWeek, List<TimeInterval>> daysWithHoursOn = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            daysWithHoursOn.put(day, new ArrayList<>(allTimeIntervals));
        }
        return daysWithHoursOn;
    }

    private HashMap<DayOfWeek, List<TimeInterval>> getDaysWithHoursOff() {
        HashMap<DayOfWeek, List<TimeInterval>> daysWithHoursOff = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            daysWithHoursOff.put(day, new ArrayList<>());
        }
        return daysWithHoursOff;
    }


    public void parseRequestsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray requestsList = (JSONArray) obj;

            //Iterate over requests array
            requestsList.forEach(req -> processRequestObject((JSONObject) req));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void processRequestObject(JSONObject request) {
        //Get request object within list
        JSONObject requestObject = (JSONObject) request.get("request");

        //Get information about request
        String id = (String) requestObject.get("id");
        String team = (String) requestObject.get("team");
        String dayOff = (String) requestObject.get("dayOff");
        String startTimeOff = (String) requestObject.get("startTimeOff");
        String endTimeOff = (String) requestObject.get("endTimeOff");
        String startHours = StringUtils.substringBefore(startTimeOff, ":");
        String startMinutes = StringUtils.substringAfter(startTimeOff, ":");
        String endHours = StringUtils.substringBefore(endTimeOff, ":");
        String endMinutes = StringUtils.substringAfter(endTimeOff, ":");

        TimeInterval element = new TimeInterval(Integer.parseInt(startHours), Integer.parseInt(startMinutes), Integer.parseInt(endHours), Integer.parseInt(endMinutes));

        unavailable.get(team).get(DayOfWeek.fromString(dayOff)).add(element);

        employee.get(team).add(Integer.parseInt(id));

    }

    public void parseMeetingsFile(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray meetingsList = (JSONArray) obj;

            //Iterate over requests array
            meetingsList.forEach(meeting -> processMeetingObject((JSONObject) meeting));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void processMeetingObject(JSONObject meeting) {
        //Get meeting object within list
        JSONObject meetingObject = (JSONObject) meeting.get("meeting");

        //Get information about meeting
        String name = (String) meetingObject.get("name");
        String duration = (String) meetingObject.get("duration");

        meetings.add(name);

        infoAboutMeetings.add(new Pair<>(name, Integer.parseInt(duration)));

    }

    public void generateTeamConstraint() {

        employee.forEach((k, v) ->

            employee.forEach((key, val) -> {

                if (!k.equals(key) && v.stream().anyMatch(val::contains)) {
                        teamConstraint.get(k).add(key);
                    }
            })

        );
    }

    public void sortUnavailable(Map<String, Map<DayOfWeek, List<TimeInterval>>> teamUnavailableMap) {
        teamUnavailableMap.forEach((k, v) -> {
            for (DayOfWeek day : DayOfWeek.values()) {
                List<TimeInterval> oldList = teamUnavailableMap.get(k).get(day);
                oldList.sort(this::compareTwoIntervals);
                teamUnavailableMap.get(k).replace(day, oldList);
            }
        });
    }

    private int compareTwoIntervals(TimeInterval o1, TimeInterval o2) {
        if (o1.getStartHour() < o2.getStartHour()) {
            return -1;
        } else if (o1.getStartHour() > o2.getStartHour()) {
            return 1;
        } else {
            if (o1.getStartMinute() < o2.getStartMinute()) {
                return -1;
            } else if (o1.getStartMinute() > o2.getStartMinute()) {
                return 1;
            } else {
                if (o1.getEndHour() < o2.getEndHour()) {
                    return -1;
                } else if (o1.getEndHour() > o2.getEndHour()) {
                    return 1;
                } else {
                    if (o1.getEndMinute() < o2.getEndMinute()) {
                        return -1;
                    } else if (o1.getEndMinute() > o2.getEndMinute()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    public void generateAvailable() {

        sortUnavailable(unavailable);
        sortInfoAboutMeetings(infoAboutMeetings);
        for (String teamName: teams) {
            for (Pair<String, Integer> infoAboutMeeting: infoAboutMeetings) {
                available.put(new Pair<>(teamName, infoAboutMeeting.getValue0()), new HashMap<>());
                generateAvailableIntervals(teamName, infoAboutMeeting.getValue0(), unavailable);
            }
        }
    }

    private void generateAvailableIntervals(String teamName, String teamMeeting, Map<String, Map<DayOfWeek, List<TimeInterval>>> unavailable) {
        for (DayOfWeek day : DayOfWeek.values()) {
            List<TimeInterval> teamAvailableIntervalsOneDay = getTimeIntervals(teamName, unavailable, day);
            available.get(new Pair<>(teamName, teamMeeting)).put(day, teamAvailableIntervalsOneDay);
        }
    }

    private List<TimeInterval> getTimeIntervals(String teamName, Map<String, Map<DayOfWeek, List<TimeInterval>>> unavailable, DayOfWeek day) {
        List<TimeInterval> teamAvailableIntervals = new ArrayList<>(allTimeIntervals);
        List<TimeInterval> teamUnavailableIntervals = unavailable.get(teamName).get(day);
        List<TimeInterval> intervalsToRemove = new ArrayList<>();

        for (TimeInterval unavailableInterval : teamUnavailableIntervals) {

            for (TimeInterval availableInterval : teamAvailableIntervals) {

                // case when someone requested whole day off
                if (unavailableInterval.getStartHour() == START_HOURS && unavailableInterval.getStartMinute() == START_MINS &&
                        unavailableInterval.getEndHour() == END_HOURS && unavailableInterval.getEndMinute() == END_MINS) {
                   return new ArrayList<>();
                } else {
                    if (isIntervalInsideAnother(unavailableInterval, availableInterval)
                            || isIntervalStartTimeSame(availableInterval, unavailableInterval)
                            || isIntervalEndTimeSame(availableInterval, unavailableInterval)) {
                        intervalsToRemove.add(availableInterval);
                    }
                }
            }

        }
        if (!intervalsToRemove.isEmpty()) {
            teamAvailableIntervals.removeAll(intervalsToRemove);
        }
        return teamAvailableIntervals;
    }

    private static boolean isIntervalEndTimeSame(TimeInterval unavailableInterval, TimeInterval availableInterval) {
        return unavailableInterval.getEndHour() == availableInterval.getEndMinute() &&
                unavailableInterval.getEndMinute() == availableInterval.getEndMinute();
    }

    private static boolean isIntervalStartTimeSame(TimeInterval unavailableInterval, TimeInterval availableInterval) {
        return availableInterval.getStartHour() == unavailableInterval.getStartHour() &&
                availableInterval.getStartMinute() == unavailableInterval.getStartMinute();
    }

    private static boolean isIntervalInsideAnother(TimeInterval unavailableInterval, TimeInterval availableInterval) {
        return availableInterval.getStartHour() > unavailableInterval.getStartHour() &&
                availableInterval.getEndHour() < unavailableInterval.getEndHour();
    }

    public Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> generateDomain() {
        Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domain = new HashMap<>();
        for (String team : teams) {
            for (String meeting : meetings) {
                Map<DayOfWeek, List<TimeInterval>> daysWithHoursOnDomain = new HashMap<>();
                for (DayOfWeek day : DayOfWeek.values()) {
                    daysWithHoursOnDomain.put(day, new ArrayList<>(allTimeIntervals));
                }
                Pair<String, String> teamAndMeeting = new Pair<>(team, meeting);
                domain.put(teamAndMeeting, daysWithHoursOnDomain);
            }
        }
        return domain;
    }

    public Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> generateEmptySolution() {
        Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution = new HashMap<>();
        for (String team : teams) {
            for (String meeting : meetings) {
                Pair<String, String> teamAndMeeting = new Pair<>(team, meeting);
                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
            }
        }
        return solution;
    }

    public void sortInfoAboutMeetings( List<Pair<String, Integer>>infoAboutMeetings){
        infoAboutMeetings.sort(Comparator.comparing(Pair::getValue1, Comparator.reverseOrder()));
    }

    public void printSolution(Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        for (Pair<String, String> teamAndMeeting : solution.keySet()) {
            System.out.println(teamAndMeeting.getValue0() + " " + teamAndMeeting.getValue1() + " " + solution.get(teamAndMeeting).getValue0() + " " + solution.get(teamAndMeeting).getValue1());
        }
    }
}
