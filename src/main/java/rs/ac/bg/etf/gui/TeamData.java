/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.etf.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import rs.ac.bg.etf.DayOfWeek;

/**
 *
 * @author sd213335m
 */
public class TeamData {

    private String teamName;
    private Map<DayOfWeek, Set<Pair<LocalTime, LocalTime>>> freeList;
    private Map<DayOfWeek, Map<String, Set<Pair<LocalTime, LocalTime>>>> meetings;

    public void setFreeList(Map<DayOfWeek, Set<Pair<LocalTime, LocalTime>>> freeList) {
        this.freeList = freeList;
    }

    public void setMeetings(Map<DayOfWeek, Map<String, Set<Pair<LocalTime, LocalTime>>>> meetings) {
        this.meetings = meetings;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Map<DayOfWeek, Set<Pair<LocalTime, LocalTime>>> getFreeList() {
        return freeList;
    }

    public Map<DayOfWeek, Map<String, Set<Pair<LocalTime, LocalTime>>>> getMeetings() {
        return meetings;
    }

    public String getTeamName() {
        return teamName;
    }

}
