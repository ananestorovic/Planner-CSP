package rs.ac.bg.etf;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class CSPAlgorithm {

    private FileParser fp;
    private Semaphore semaphore;

    public CSPAlgorithm(FileParser fp, Semaphore semaphore) {
        this.fp = fp;
        this.semaphore = semaphore;
    }

    public boolean complete(Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        return solution.entrySet().stream().allMatch(mapEntry -> !mapEntry.getValue().getValue1().isEmpty());
    }

    //
    public Pair<String, String> getMostConstrainedVariable(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> available, Map<String, Set<String>> teamConstraint) {

        return available.entrySet().stream().sorted(Comparator.comparing(
                (Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> pairMapEntry) -> getTeamConstraintSize(teamConstraint, pairMapEntry)
        ).reversed().thenComparing(CSPAlgorithm::getIntervalsSum)
        ).map(entry -> entry.getKey()).collect(Collectors.toList()).remove(0);
    }

    private static int getTeamConstraintSize(Map<String, Set<String>> teamConstraint, Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> pairMapEntry) {
        return teamConstraint.get(pairMapEntry.getKey().getValue0()).size();
    }

    private static int getIntervalsSum(Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> mapEntry) {
        return mapEntry.getValue().entrySet().stream().mapToInt(
                mapEntry1 -> mapEntry1.getValue().size()).sum();
    }

    public Quartet<Integer, Integer, Integer, Integer> nextElement(Quartet<Integer, Integer, Integer, Integer> curElement) {

        int v0 = curElement.getValue1() == 0 ? curElement.getValue0() : curElement.getValue0() + 1;
        int v1 = curElement.getValue1() == 0 ? 30 : 0;
        int v3 = curElement.getValue3() == 0 ? curElement.getValue2() : curElement.getValue2() + 1;
        int v4 = curElement.getValue3() == 0 ? 30 : 0;

        return new Quartet<>(v0, v1, v3, v4);
    }

    public int getDurationMeeting(String meeting, List<Pair<String, Integer>> infoAboutMeetings) {
        for (Pair<String, Integer> p : infoAboutMeetings) {
            if (p.getValue0().equals(meeting)) {
                return p.getValue1();
            }
        }
        return 0;
    }

//    public Pair<String, List<Quartet<Integer, Integer, Integer, Integer>>> assigneMeeting(String team, String meeting, HashMap<Pair<String, String>, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> domain,
//                                                                                          List<Pair<String, Integer>> infoAboutMeetings) {
//        int num = getDurationMeeting(meeting, infoAboutMeetings);
//        int sH, sM, eH, eM; //vidi trebaju li ti ove info
//        for (Map.Entry<Pair<String, String>, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> entry : domain.entrySet()) {
//            if (entry.getKey().getValue0().equals(team) && entry.getKey().getValue1().equals(meeting)) {
//                for (Map.Entry<String, List<Quartet<Integer, Integer, Integer, Integer>>> lists : entry.getValue().entrySet()) {
//                    for (String day : daysWeek) {
//                        int n = num;
//                        List<Quartet<Integer, Integer, Integer, Integer>> toRemove = new ArrayList<>();
//                        Quartet<Integer, Integer, Integer, Integer> prev = null;
//                        if (lists.getKey().equals(day)) {
//                            for (Quartet<Integer, Integer, Integer, Integer> q : lists.getValue()) {
//
//
//                                if (prev == null) {
//                                    prev = new Quartet<>(q.getValue0(), q.getValue1(), q.getValue2(), q.getValue3());
//                                    sH = q.getValue0();
//                                    sM = q.getValue1();
//                                    toRemove.add(q);
//                                    --n;
//
//                                } else {
//                                    Quartet<Integer, Integer, Integer, Integer> next = nextElement(prev);
//                                    if (!next.equals(q)) {
//                                        prev = null;
//                                        sH = q.getValue0();
//                                        sM = q.getValue1();
//                                        toRemove.clear();
//                                        n = num;
//                                    } else {
//                                        prev = new Quartet<>(q.getValue0(), q.getValue1(), q.getValue2(), q.getValue3());
//                                        toRemove.add(q);
//                                        --n;
//                                    }
//
//                                    if (n == 0) {
//                                        eH = q.getValue2();
//                                        eM = q.getValue3();
//                                        Pair<String, String> p = new Pair<>(team, meeting);
//                                        domain.get(p).get(day).removeAll(toRemove);
//
//                                        Pair<String, List<Quartet<Integer, Integer, Integer, Integer>>> l = new Pair<>(day, toRemove);
//
//                                        return l;
//                                    }
//
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
//
//        }
//        return null;
//    }
    public String getMeeting(String team, HashMap<Pair<String, String>, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> solution) {

        for (Map.Entry<Pair<String, String>, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> entry : solution.entrySet()) {
            if (entry.getKey().getValue0().equals(team)) {
                if (!isMeetingAssigned(solution, team, entry.getKey().getValue1())) {
                    return entry.getKey().getValue1();
                }
            }
        }

        return null;
    }

    public Boolean isMeetingAssigned(HashMap<Pair<String, String>, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> solution,
            String team, String meeting) {

        HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>> hm = solution.get(new Pair<>(team, meeting));

        for (Map.Entry<String, List<Quartet<Integer, Integer, Integer, Integer>>> entry : hm.entrySet()) {
            if (entry.getValue().size() != 0) {
                return true;
            }
        }

        return false;
    }

//    public Boolean isConsistentAssignment(Pair<String, List<Quartet<Integer, Integer, Integer, Integer>>> assignedMeeting,
//                                          HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> available,
//                                          HashMap<String, List<String>> teamConstraint, String team, String meeting) {
//        boolean first = true;
//        boolean match = false;
//
//        List<Quartet<Integer, Integer, Integer, Integer>> listAssigned = assignedMeeting.getValue1();
//        HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>> hm = available.get(team);
//        List<Quartet<Integer, Integer, Integer, Integer>> listAvailable = hm.get(assignedMeeting.getValue0());
//
//        for (Quartet<Integer, Integer, Integer, Integer> q : listAssigned) {
//            if (!first) {
//                if (!match) return false;
//                else match = false;
//            }
//            for (Quartet<Integer, Integer, Integer, Integer> q1 : listAvailable) {
//                first = false;
//                if (q.equals(q1)) match = true;
//            }
//        }
//        return null; //Dodaces nesto, ali moras proveriti constatints vezane za timove
//    }
    public TimeInterval getNextElement(TimeInterval curElement) {

        int v0 = curElement.getStartMinute() == 0 ? curElement.getStartHour() : curElement.getStartHour() + 1;
        int v1 = curElement.getStartMinute() == 0 ? 30 : 0;
        int v3 = curElement.getEndMinute() == 0 ? curElement.getEndHour() : curElement.getEndHour() + 1;
        int v4 = curElement.getEndMinute() == 0 ? 30 : 0;

        return new TimeInterval(v0, v1, v3, v4);
    }

    public Pair<DayOfWeek, List<TimeInterval>> getContinuousIntervals(Map<DayOfWeek, List<TimeInterval>> domain, int numberOfIntervals) {

        for (Map.Entry<DayOfWeek, List<TimeInterval>> oneDayEntry : domain.entrySet()) {
            List<TimeInterval> intervals = new ArrayList<>();
            int n = numberOfIntervals;
            TimeInterval prev = null;
            for (TimeInterval timeInterval : oneDayEntry.getValue()) {
                if (prev == null) {
                    prev = new TimeInterval(timeInterval.getStartHour(), timeInterval.getStartMinute(),
                            timeInterval.getEndHour(), timeInterval.getEndMinute());
                    intervals.add(timeInterval);
                    --n;
                } else {
                    TimeInterval expectedTimeInterval = getNextElement(prev);
                    if (!expectedTimeInterval.equals(timeInterval)) {
                        intervals.clear();
                        n = numberOfIntervals;
                    }
                    --n;
                    intervals.add(timeInterval);
                    prev = timeInterval;
                }
                if (n == 0) {
                    return new Pair<>(oneDayEntry.getKey(), intervals);
                }
            }
        }
        return null;
    }

    private Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> deepCopy(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domain) {
        Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomain = new HashMap<>();
        for (Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> entry : domain.entrySet()) {
            Map<DayOfWeek, List<TimeInterval>> newMap = new HashMap<>();
            for (Map.Entry<DayOfWeek, List<TimeInterval>> entry1 : entry.getValue().entrySet()) {
                List<TimeInterval> newList = new ArrayList<>();
                for (TimeInterval timeInterval : entry1.getValue()) {
                    newList.add(new TimeInterval(timeInterval.getStartHour(), timeInterval.getStartMinute(),
                            timeInterval.getEndHour(), timeInterval.getEndMinute()));
                }
                newMap.put(entry1.getKey(), newList);
            }
            newDomain.put(entry.getKey(), newMap);
        }
        return newDomain;
    }

    public Pair<DayOfWeek, TimeInterval> isConsistentAssignment(Pair<String, String> teamAndMeeting, Pair<DayOfWeek, List<TimeInterval>> timeAndDay,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution,
            Map<String, Set<String>> teamConstraint) {

        Set<String> teamConstraintList = teamConstraint.get(teamAndMeeting.getValue0());
//        teamConstraintList.add(teamAndMeeting.getValue0());
        //if (teamConstraintList == null) return null;

        for (Pair<String, String> teamAndMeetingForCompare : generateAllVariables(teamConstraintList)) {
            Pair<DayOfWeek, List<TimeInterval>> timeAndDayConstraint = solution.get(teamAndMeetingForCompare);

            if (timeAndDay.getValue0().equals(timeAndDayConstraint.getValue0())) {
                for (TimeInterval timeInterval : timeAndDay.getValue1()) {
                    for (TimeInterval timeIntervalConstraint : timeAndDayConstraint.getValue1()) {
                        if (timeInterval.equals(timeIntervalConstraint)) {
                            return new Pair<>(timeAndDay.getValue0(), timeInterval);
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<Pair<String, String>> generateAllVariables(Set<String> teamConstraintList) {
        List<Pair<String, String>> variables = new ArrayList<>();
        for (String teamName : teamConstraintList) {
            for (String meetingName : fp.getMeetings()) {
                variables.add(new Pair<>(teamName, meetingName));
            }
        }
        return variables;
    }

    private Map<DayOfWeek, List<TimeInterval>> deepCopyForMap(Map<DayOfWeek, List<TimeInterval>> dayOfWeekListMap) {
        Map<DayOfWeek, List<TimeInterval>> newMap = new HashMap<>();
        for (Map.Entry<DayOfWeek, List<TimeInterval>> entry : dayOfWeekListMap.entrySet()) {
            List<TimeInterval> newList = new ArrayList<>();
            for (TimeInterval timeInterval : entry.getValue()) {
                newList.add(new TimeInterval(timeInterval.getStartHour(), timeInterval.getStartMinute(),
                        timeInterval.getEndHour(), timeInterval.getEndMinute()));
            }
            newMap.put(entry.getKey(), newList);
        }
        return newMap;
    }

    public void removeEveryDayAndTimeInterval(Set<String> listOfConstrainedTeams, Pair<DayOfWeek, List<TimeInterval>> timeAndDay,
            Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domain) {
        for (String constrainedTeam : listOfConstrainedTeams) {
            for (Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> entry : domain.entrySet()) {
                if (entry.getKey().getValue0().equals(constrainedTeam)) {
                    for (Map.Entry<DayOfWeek, List<TimeInterval>> entry1 : entry.getValue().entrySet()) {
                        if (entry1.getKey().equals(timeAndDay.getValue0())) {
                            for (TimeInterval timeInterval : timeAndDay.getValue1()) {
                                entry1.getValue().remove(timeInterval);
                            }
                        }
                    }
                }
            }
        }

    }

    public Boolean backtracking(Map<String, Set<String>> teamConstraint,
            List<Pair<String, Integer>> infoAboutMeetings,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution,
            Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains) throws InterruptedException {
        if (complete(solution)) {
            return true;
        }

        Pair<String, String> teamAndMeeting = getMostConstrainedVariable(domains, teamConstraint);
        String team = teamAndMeeting.getValue0();
        String meeting = teamAndMeeting.getValue1();
        //TODO preskoceno sort_domain_least_constraining --treba li?
        Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(teamAndMeeting));
        domains.remove(teamAndMeeting);
        Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        while (timeAndDay != null) {
            Pair<DayOfWeek, TimeInterval> overlappedTeamTimeInterval = isConsistentAssignment(teamAndMeeting, timeAndDay, solution, teamConstraint);
            if (overlappedTeamTimeInterval == null) {

                solution.put(teamAndMeeting, timeAndDay);
                Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains = deepCopy(domains);
                semaphore.acquire();
//                newDomains.get(teamAndMeeting).put(timeAndDay.getValue0(), timeAndDay.getValue1());

                if (backtracking(teamConstraint, infoAboutMeetings, solution, newDomains)) {
                    return true;
                }

                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
                semaphore.acquire();
            } else {
                domain.get(overlappedTeamTimeInterval.getValue0()).remove(overlappedTeamTimeInterval.getValue1());
            }
            timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        }

        return false;

    }

    public Boolean fcBacktracking(Map<String, Set<String>> teamConstraint,
            List<Pair<String, Integer>> infoAboutMeetings,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution,
            Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains) {
        if (complete(solution)) {
            return true;
        }

        Pair<String, String> teamAndMeeting = getMostConstrainedVariable(domains, teamConstraint);
        String team = teamAndMeeting.getValue0();
        String meeting = teamAndMeeting.getValue1();
        //TODO preskoceno sort_domain_least_constraining --treba li?
        Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(teamAndMeeting));
        domains.remove(teamAndMeeting);
        Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        while (timeAndDay != null) {
            Pair<DayOfWeek, TimeInterval> overlappedTeamTimeInterval = isConsistentAssignment(teamAndMeeting, timeAndDay, solution, teamConstraint);
            if (overlappedTeamTimeInterval == null) {

                solution.put(teamAndMeeting, timeAndDay);
                Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains = deepCopy(domains);
                removeEveryDayAndTimeInterval(teamConstraint.get(teamAndMeeting.getValue0()), timeAndDay, newDomains);

                if (fcBacktracking(teamConstraint, infoAboutMeetings, solution, newDomains)) {
                    return true;
                }

                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
            } else {
                domain.get(overlappedTeamTimeInterval.getValue0()).remove(overlappedTeamTimeInterval.getValue1());
            }
            timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        }

        return false;

    }

    public Boolean arcBacktracking(Map<String, Set<String>> teamConstraint,
            List<Pair<String, Integer>> infoAboutMeetings,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution,
            Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains) {
        if (complete(solution)) {
            return true;
        }

        Pair<String, String> teamAndMeeting = getMostConstrainedVariable(domains, teamConstraint);
        String team = teamAndMeeting.getValue0();
        String meeting = teamAndMeeting.getValue1();
        //TODO preskoceno sort_domain_least_constraining --treba li?
        Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(teamAndMeeting));
        domains.remove(teamAndMeeting);
        Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        while (timeAndDay != null) {
            Pair<DayOfWeek, TimeInterval> overlappedTeamTimeInterval = isConsistentAssignment(teamAndMeeting, timeAndDay, solution, teamConstraint);
            if (overlappedTeamTimeInterval == null) {

                solution.put(teamAndMeeting, timeAndDay);
                Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains = deepCopy(domains);
                removeEveryDayAndTimeInterval(teamConstraint.get(teamAndMeeting.getValue0()), timeAndDay, newDomains);

                if (!arcConsistency(newDomains, teamConstraint, teamAndMeeting, infoAboutMeetings)) {
                    solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                    domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
                    timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
                    continue;
                }

                if (arcBacktracking(teamConstraint, infoAboutMeetings, solution, newDomains)) {
                    return true;
                }

                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
            } else {
                domain.get(overlappedTeamTimeInterval.getValue0()).remove(overlappedTeamTimeInterval.getValue1());
            }
            timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        }

        return false;

    }

    private boolean arcConsistency(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains, Map<String, Set<String>> teamConstraints, Pair<String, String> teamAndMeeting, List<Pair<String, Integer>> infoAboutMeetings) {

        Set<String> listOfTeams = teamConstraints.get(teamAndMeeting.getValue0());

        for (String team : listOfTeams) {
            for (String meeting : fp.getMeetings()) {
                if (team.equals(teamAndMeeting.getValue0()) && meeting.equals(teamAndMeeting.getValue1())) {
                    continue;
                }
                Pair<String, String> newTeamAndMeeting = new Pair<>(team, meeting);
                if (!domains.containsKey(newTeamAndMeeting)) {
                    continue;
                }
                Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(newTeamAndMeeting));
                Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(meeting, infoAboutMeetings) / FileParser.INTERVAL);

                if (timeAndDay == null) {
                    return false;
                } else {
                    Set<String> listOfConstrainedTeams = teamConstraints.get(team);
                    for (String constrainedTeam : listOfConstrainedTeams) {
                        for (String constrainedMeeting : fp.getMeetings()) {
                            if ((constrainedTeam.equals(team) && constrainedMeeting.equals(meeting)) || (constrainedTeam.equals(teamAndMeeting.getValue0()) && constrainedMeeting.equals(teamAndMeeting.getValue1()))) {
                                continue;
                            }
                            Pair<String, String> newConstrainedTeamAndMeeting = new Pair<>(constrainedTeam, constrainedMeeting);
                            //treba li i ovde kopija domena??
                            if (!domains.containsKey(newConstrainedTeamAndMeeting)) {
                                continue;
                            }
                            Map<DayOfWeek, List<TimeInterval>> newDomain = deepCopyForMap(domains.get(newConstrainedTeamAndMeeting));
                            for (TimeInterval interval : timeAndDay.getValue1()) {
                                if (newDomain.get(timeAndDay.getValue0()).contains(interval)) {
                                    newDomain.get(timeAndDay.getValue0()).remove(interval);
                                }
                            }
                            if (getContinuousIntervals(newDomain, getDurationMeeting(constrainedMeeting, infoAboutMeetings) / FileParser.INTERVAL) == null) {
                                return false;
                            }

                        }
                    }
                }

            }
        }

        //return true;
        return true;
    }

}
