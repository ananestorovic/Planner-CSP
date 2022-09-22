/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.etf.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import org.javatuples.Pair;
import org.javatuples.Quartet;

/**
 *
 * @author sd213335m
 */
public class TeamData {

    private String teamName;
    private List<Pair<LocalTime, LocalTime>> freeList;
    private HashMap<String, Pair<Quartet<Integer, Integer, Integer, Integer>, Quartet<Integer, Integer, Integer, Integer>>> meetings;

    public void setFreeList(List<Pair<LocalTime, LocalTime>> freeList) {
        this.freeList = freeList;
    }

    public void setMeetings(HashMap<String, Pair<Quartet<Integer, Integer, Integer, Integer>, Quartet<Integer, Integer, Integer, Integer>>> meetings) {
        this.meetings = meetings;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Pair<LocalTime, LocalTime>> getFreeList() {
        return freeList;
    }

    public HashMap<String, Pair<Quartet<Integer, Integer, Integer, Integer>, Quartet<Integer, Integer, Integer, Integer>>> getMeetings() {
        return meetings;
    }

    public String getTeamName() {
        return teamName;
    }

}
