/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.etf.gui;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import org.javatuples.Pair;
import org.javatuples.Quartet;

/**
 *
 * @author sd213335m
 */
public interface GuiController {

    public void initTeamTables(List<String> teamNames, List<Quartet<Integer, Integer, Integer, Integer>> freeSloots);

    public void refreshGui(HashMap<String, HashMap<String, List<Quartet<Integer, Integer, Integer, Integer>>>> teamData,
            HashMap<String, List<Pair<String, Boolean>>> meetings);
    
}
