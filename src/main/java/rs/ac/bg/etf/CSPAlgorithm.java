package rs.ac.bg.etf;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import rs.ac.bg.etf.gui.GuiController;

public class CSPAlgorithm {

    private final FileParser fp;
    private final Semaphore semaphore;
    private final GuiController guiController;
    private boolean runToEnd;

    public CSPAlgorithm(FileParser fp, Semaphore semaphore, GuiController guiController) {
        this.fp = fp;
        this.semaphore = semaphore;
        this.guiController = guiController;
    }

    private static int getTeamConstraintSize(Map<String, Set<String>> teamConstraint, Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> pairMapEntry) {
        return teamConstraint.get(pairMapEntry.getKey().getValue0()).size();
    }

    private static int getIntervalsSum(Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> mapEntry) {
        return mapEntry.getValue().entrySet().stream().mapToInt(mapEntry1 -> mapEntry1.getValue().size()).sum();
    }

    private static void setVarDomain(Pair<String, String> teamAndMeeting, Pair<DayOfWeek, List<TimeInterval>> timeAndDay, Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains) {
        newDomains.get(teamAndMeeting).put(timeAndDay.getValue0(), timeAndDay.getValue1());
        newDomains.get(teamAndMeeting).forEach((key, value) -> {
            if (!key.equals(timeAndDay.getValue0())) {
                value.clear();
            }
        });
    }

    public boolean complete(Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        return solution.entrySet().stream().noneMatch(mapEntry -> mapEntry.getValue().getValue1().isEmpty());
    }


    public Pair<String, String> getMostConstrainedVariable(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> available, Map<String, Set<String>> teamConstraint, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        return available.entrySet().stream().filter(pairMapEntry -> solution.get(pairMapEntry.getKey()).getValue1().isEmpty())
                .sorted(Comparator.comparing
                                (CSPAlgorithm::getIntervalsSum)
                        .thenComparing((Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> pairMapEntry)
                                -> getTeamConstraintSize(teamConstraint, pairMapEntry), Comparator.reverseOrder())
                        .thenComparing((Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> pairMapEntry)
                                -> pairMapEntry.getKey().getValue0()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .remove(0);

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
                    prev = new TimeInterval(timeInterval.getStartHour(), timeInterval.getStartMinute(), timeInterval.getEndHour(), timeInterval.getEndMinute());
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
                    newList.add(new TimeInterval(timeInterval.getStartHour(), timeInterval.getStartMinute(), timeInterval.getEndHour(), timeInterval.getEndMinute()));
                }
                newMap.put(entry1.getKey(), newList);
            }
            newDomain.put(entry.getKey(), newMap);
        }
        return newDomain;
    }

    public Pair<DayOfWeek, TimeInterval> isConsistentAssignment(Pair<String, String> teamAndMeeting, Pair<DayOfWeek, List<TimeInterval>> timeAndDay, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution, Map<String, Set<String>> teamConstraint) {

        Set<String> teamConstraintList = teamConstraint.get(teamAndMeeting.getValue0());
        //teamConstraintList.add(teamAndMeeting.getValue0());
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
                newList.add(new TimeInterval(timeInterval.getStartHour(), timeInterval.getStartMinute(), timeInterval.getEndHour(), timeInterval.getEndMinute()));
            }
            newMap.put(entry.getKey(), newList);
        }
        return newMap;
    }

    public void removeEveryDayAndTimeInterval(Set<String> listOfConstrainedTeams, Pair<DayOfWeek, List<TimeInterval>> timeAndDay, Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domain, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        for (String constrainedTeam : listOfConstrainedTeams) {
            for (Map.Entry<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> entry : domain.entrySet()) {
                if (!solution.get(entry.getKey()).getValue1().isEmpty()) {
                    continue;
                }
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

    public Boolean backtracking(Map<String, Set<String>> teamConstraint, List<Pair<String, Integer>> infoAboutMeetings, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution, Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains) throws InterruptedException {
        if (complete(solution)) {
            guiController.refreshGui(domains, solution);
            return true;
        }

        if (!runToEnd && someSolutionExists(solution)) {
            guiController.refreshGui(domains, solution);
            semaphore.acquire();
        }


        Pair<String, String> teamAndMeeting = getMostConstrainedVariable(domains, teamConstraint, solution);
        String team = teamAndMeeting.getValue0();
        String meeting = teamAndMeeting.getValue1();
        //TODO preskoceno sort_domain_least_constraining --treba li?
        Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(teamAndMeeting));
//        domain.forEach((key, value)->value.sort(null));
        Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        while (timeAndDay != null) {
            Pair<DayOfWeek, TimeInterval> overlappedTeamTimeInterval = isConsistentAssignment(teamAndMeeting, timeAndDay, solution, teamConstraint);
            if (overlappedTeamTimeInterval == null) {

                solution.put(teamAndMeeting, timeAndDay);
                Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains = deepCopy(domains);
                setVarDomain(teamAndMeeting, timeAndDay, newDomains);

                if (!runToEnd) {
                    guiController.refreshGui(domains, solution);
                    semaphore.acquire();
                }

                if (backtracking(teamConstraint, infoAboutMeetings, solution, newDomains)) {
                    return true;
                }

                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
                if (!runToEnd) {
                    guiController.refreshGui(domains, solution);
                    semaphore.acquire();
                }
            } else {
                domain.get(overlappedTeamTimeInterval.getValue0()).remove(overlappedTeamTimeInterval.getValue1());
            }
            timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        }
        return false;
    }

    private boolean someSolutionExists(Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        return solution.entrySet().stream().anyMatch(mapEntry -> !mapEntry.getValue().getValue1().isEmpty());
    }

    public boolean fcBacktracking(Map<String, Set<String>> teamConstraint, List<Pair<String, Integer>> infoAboutMeetings, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution, Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains) throws InterruptedException {
        if (complete(solution)) {
            guiController.refreshGui(domains, solution);
            return true;
        }

        if (!runToEnd && someSolutionExists(solution)) {
            guiController.refreshGui(domains, solution);
            semaphore.acquire();
        }


        Pair<String, String> teamAndMeeting = getMostConstrainedVariable(domains, teamConstraint, solution);
        String team = teamAndMeeting.getValue0();
        String meeting = teamAndMeeting.getValue1();
        //TODO preskoceno sort_domain_least_constraining --treba li?
        Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(teamAndMeeting));
        Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        while (timeAndDay != null) {
            Pair<DayOfWeek, TimeInterval> overlappedTeamTimeInterval = isConsistentAssignment(teamAndMeeting, timeAndDay, solution, teamConstraint);
            if (overlappedTeamTimeInterval == null) {

                solution.put(teamAndMeeting, timeAndDay);
                Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains = deepCopy(domains);
                setVarDomain(teamAndMeeting, timeAndDay, newDomains);
                removeEveryDayAndTimeInterval(teamConstraint.get(teamAndMeeting.getValue0()), timeAndDay, newDomains, solution);

                if (!runToEnd) {
                    guiController.refreshGui(domains, solution);
                    semaphore.acquire();
                }

                if (fcBacktracking(teamConstraint, infoAboutMeetings, solution, newDomains)) {
                    return true;
                }

                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
                if (!runToEnd) {
                    guiController.refreshGui(domains, solution);
                    semaphore.acquire();
                }
            } else {
                domain.get(overlappedTeamTimeInterval.getValue0()).remove(overlappedTeamTimeInterval.getValue1());
            }
            timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        }

        return false;

    }

    public boolean arcBacktracking(Map<String, Set<String>> teamConstraint, List<Pair<String, Integer>> infoAboutMeetings, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution, Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains) throws InterruptedException {
        if (complete(solution)) {
            guiController.refreshGui(domains, solution);
            return true;
        }

        if (!runToEnd && someSolutionExists(solution)) {
            guiController.refreshGui(domains, solution);
            semaphore.acquire();
        }


        Pair<String, String> teamAndMeeting = getMostConstrainedVariable(domains, teamConstraint, solution);
        String team = teamAndMeeting.getValue0();
        String meeting = teamAndMeeting.getValue1();
        //TODO preskoceno sort_domain_least_constraining --treba li?
        Map<DayOfWeek, List<TimeInterval>> domain = deepCopyForMap(domains.get(teamAndMeeting));
        Pair<DayOfWeek, List<TimeInterval>> timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        while (timeAndDay != null) {
            Pair<DayOfWeek, TimeInterval> overlappedTeamTimeInterval = isConsistentAssignment(teamAndMeeting, timeAndDay, solution, teamConstraint);
            if (overlappedTeamTimeInterval == null) {

                solution.put(teamAndMeeting, timeAndDay);
                Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> newDomains = deepCopy(domains);
                setVarDomain(teamAndMeeting, timeAndDay, newDomains);
                removeEveryDayAndTimeInterval(teamConstraint.get(teamAndMeeting.getValue0()), timeAndDay, newDomains, solution);

                if (!runToEnd) {
                    guiController.refreshGui(domains, solution);
                    semaphore.acquire();
                }

                if (!check_arc(teamConstraint, teamAndMeeting, solution, newDomains, infoAboutMeetings)) {
                    solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                    domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
                    if (!runToEnd) {
                        guiController.refreshGui(domains, solution);
                        semaphore.acquire();
                    }
                    timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
                    continue;
                }

                if (arcBacktracking(teamConstraint, infoAboutMeetings, solution, newDomains)) {
                    return true;
                }

                solution.put(teamAndMeeting, new Pair<>(null, new ArrayList<>()));
                domain.get(timeAndDay.getValue0()).remove(timeAndDay.getValue1().get(0));
                if (!runToEnd) {
                    guiController.refreshGui(domains, solution);
                    semaphore.acquire();
                }
            } else {
                domain.get(overlappedTeamTimeInterval.getValue0()).remove(overlappedTeamTimeInterval.getValue1());
            }
            timeAndDay = getContinuousIntervals(domain, getDurationMeeting(teamAndMeeting.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
        }

        return false;

    }

    private boolean arcConsistency(
            Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains,
            Map<String, Set<String>> teamConstraints, Pair<String, String> teamAndMeeting,
            List<Pair<String, Integer>> infoAboutMeetings,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {

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

                if (!solution.get(newTeamAndMeeting).getValue1().isEmpty()) {
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

                            if (!solution.get(newConstrainedTeamAndMeeting).getValue1().isEmpty()) {
                                continue;
                            }
                            Map<DayOfWeek, List<TimeInterval>> newDomain = deepCopyForMap(domains.get(newConstrainedTeamAndMeeting));
                            for (TimeInterval interval : timeAndDay.getValue1()) {
                                newDomain.get(timeAndDay.getValue0()).remove(interval);
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

    void setRunToEnd(boolean runToEndNewVal) {
        runToEnd = runToEndNewVal;
    }


    private List<Pair<Pair<String, String>, Pair<String, String>>> getAllArcs(
            Map<String, Set<String>> teamConstraints, Pair<String, String> teamAndMeeting,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution
    ) {

        List<Pair<Pair<String, String>, Pair<String, String>>> arcs = new ArrayList<>();

        for (String team : fp.getTeams()) {
            Set<String> listOfTeams = teamConstraints.get(team);
            for (String constrainedTeam : listOfTeams) {
                for (String meeting : fp.getMeetings()) {
                    Pair<String, String> currentTeamAndMeeting = new Pair<>(team, meeting);
                    Pair<String, String> constrainedTeamAndMeeting = new Pair<>(constrainedTeam, meeting);

                    if (!solution.get(constrainedTeamAndMeeting).getValue1().isEmpty() || teamAndMeeting.equals(constrainedTeamAndMeeting)) {
                        continue;
                    }

                    if (!solution.get(currentTeamAndMeeting).getValue1().isEmpty() || teamAndMeeting.equals(currentTeamAndMeeting)) {
                        continue;
                    }

                    arcs.add(
                            new Pair<>(currentTeamAndMeeting, constrainedTeamAndMeeting)
                    );
                }
            }
        }
        return arcs;
    }

    private boolean check_arc(
            Map<String, Set<String>> teamConstraints, Pair<String, String> teamAndMeeting,
            Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution,
            Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains,
            List<Pair<String, Integer>> infoAboutMeetings
    ) {
        List<Pair<Pair<String, String>, Pair<String, String>>> arcs = getAllArcs(teamConstraints, teamAndMeeting, solution);

        while (!arcs.isEmpty()) {
            Pair<Pair<String, String>, Pair<String, String>> varAB = arcs.remove(0);
            Pair<String, String> varA = varAB.getValue0();
            Pair<String, String> varB = varAB.getValue1();

            List<Pair<DayOfWeek, List<TimeInterval>>> varAValueToDelete = new ArrayList<>();

            List<Pair<DayOfWeek, List<TimeInterval>>> varADomain = getAllPossibleTermins(domains.get(varA), getDurationMeeting(varA.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
            for (Pair<DayOfWeek, List<TimeInterval>> valA : varADomain) {
                boolean varBNoValue = true;
                List<Pair<DayOfWeek, List<TimeInterval>>> varBDomain = getAllPossibleTermins(domains.get(varB), getDurationMeeting(varB.getValue1(), infoAboutMeetings) / FileParser.INTERVAL);
                for (Pair<DayOfWeek, List<TimeInterval>> valB : varBDomain) {
                    if (satisfyConstraint(valA, valB)) {
                        varBNoValue = false;
                        break;
                    }
                }
                if (varBNoValue) {
                    varAValueToDelete.add(valA);
                }
            }
            if (!varAValueToDelete.isEmpty()) {
                varADomain.removeAll(varAValueToDelete);
                removeFromDomain(domains, varA, varADomain);
                if (isDomainEmpty(domains, varA)) {
                    return false;
                }
                arcs.addAll(getArcsToVar(varA, teamConstraints, solution));
            }
        }
        return true;
    }

    private List<Pair<Pair<String, String>, Pair<String, String>>> getArcsToVar(Pair<String, String> varA, Map<String, Set<String>> teamConstraints, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution) {
        List<String> constrainedTeams = teamConstraints.get(varA.getValue0()).stream().filter(elem -> !elem.equals(varA.getValue0())).collect(Collectors.toList());
        List<Pair<Pair<String, String>, Pair<String, String>>> arcs = new ArrayList<>();
        for (String team : constrainedTeams) {
            for (String meeting: fp.getMeetings()){
                Pair<String, String> constrainedVar = new Pair<>(team, meeting);

                if (!solution.get(constrainedVar).getValue1().isEmpty() || varA.equals(constrainedVar)) {
                    continue;
                }

                if (!solution.get(varA).getValue1().isEmpty()) {
                    continue;
                }

                Pair<String, String> newPair = new Pair<>(team, meeting);
                arcs.add(new Pair<>(newPair, varA));
            }
        }
        return arcs;
    }

    private boolean isDomainEmpty(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains, Pair<String, String> varA) {
        Map<DayOfWeek, List<TimeInterval>> domain = domains.get(varA);
        return domain.entrySet().stream().allMatch(dayToTimes -> dayToTimes.getValue().isEmpty());
    }

    private void removeFromDomain(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> domains, Pair<String, String> varA, List<Pair<DayOfWeek, List<TimeInterval>>> leftInDomain) {
        Map<DayOfWeek, List<TimeInterval>> domain = domains.get(varA);
        for (Pair<DayOfWeek, List<TimeInterval>> valToKeep : leftInDomain) {
            DayOfWeek dayOfWeek = valToKeep.getValue0();
            List<TimeInterval> leftValues = valToKeep.getValue1();
            domain.get(dayOfWeek).removeIf(elem -> !leftValues.contains(elem));
        }
    }

    private boolean satisfyConstraint(
            Pair<DayOfWeek, List<TimeInterval>> valA,
            Pair<DayOfWeek, List<TimeInterval>> valB
    ) {

        boolean isSatisfy = true;
        DayOfWeek valADay = valA.getValue0();
        DayOfWeek valBDay = valB.getValue0();
        if (valADay.equals(valBDay)) {
            Set<TimeInterval> valATimes = new HashSet<>(valA.getValue1());
            Set<TimeInterval> valBTimes = new HashSet<>(valB.getValue1());
            valATimes.retainAll(valBTimes);
            isSatisfy = valATimes.isEmpty();
        }

        return isSatisfy;

    }

    private List<Pair<DayOfWeek, List<TimeInterval>>> getAllPossibleTermins(
            Map<DayOfWeek, List<TimeInterval>> domain,
            int numberOfIntervals
    ) {
        List<Pair<DayOfWeek, List<TimeInterval>>> values = new ArrayList<>();
        Map<DayOfWeek, List<TimeInterval>> copyDomain = deepCopyForMap(domain);
        while (!copyDomain.isEmpty()) {
            Pair<DayOfWeek, List<TimeInterval>> interval = getContinuousIntervals(copyDomain, numberOfIntervals);
            if (interval == null) {
                break;
            }
            copyDomain.get(interval.getValue0()).remove(interval.getValue1().get(0));
            values.add(interval);
        }
        return values;
    }


}
