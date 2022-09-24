/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.etf.gui;

import java.util.List;
import java.util.Map;
import org.javatuples.Pair;
import rs.ac.bg.etf.DayOfWeek;
import rs.ac.bg.etf.TimeInterval;

/**
 *
 * @author sd213335m
 */
public interface GuiController {

    public void initTeamTables(List<String> teamNames, Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> freeSloots, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution);

    public void refreshGui(Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> freeSloots, Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution);

    public void setListener(GuiListener guiListener);

    public void planningIsFinished();

    public void showMessage(String message);
}
