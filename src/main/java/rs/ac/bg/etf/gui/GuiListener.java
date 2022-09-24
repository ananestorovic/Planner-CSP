/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.etf.gui;

import enums.CspAlgorithmType;

/**
 *
 * @author sd213335m
 */
public interface GuiListener {

    public void startSimulation(CspAlgorithmType cspAlgorithmType, String teams, String meetings, String timeOff);
    
    public void stopSimulation();

    public void nextStep();

    public void runAlgorithmToEnd();
    
}
