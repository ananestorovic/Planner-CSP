/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package rs.ac.bg.etf;

import enums.CspAlgorithmType;
import org.javatuples.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.ac.bg.etf.gui.GuiController;
import rs.ac.bg.etf.gui.GuiListener;

/**
 *
 * @author ana
 */
public class PlannerCSP implements GuiListener, Runnable {

    private Semaphore semaphore;

    private GuiController guiController;

    public PlannerCSP(GuiController guiController) {
        this.guiController = guiController;
        this.semaphore = new Semaphore(0);
    }

    private void runCSPAlgoritum(CspAlgorithmType cspAlgorithmType) throws InterruptedException {

        FileParser fp = new FileParser();
        CSPAlgorithm csp = new CSPAlgorithm(fp, semaphore);

        fp.parseMeetingsFile("resources/meetings.json");
        fp.parseTeamsFile("resources/teams.json");
        fp.parseRequestsFile("resources/requests.json");
        fp.generateTeamConstraint(); // just once i need to generate this

        fp.generateAvailable(); //initial table, later we will update this
        Map<Pair<String, String>, Map<DayOfWeek, List<TimeInterval>>> a = fp.getAvailable();

        Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution = fp.generateEmptySolution();

        guiController.initTeamTables(fp.getTeams(), fp.getallVarsTermin(), solution);
        semaphore.acquire();

        guiController.refreshGui(fp.getAvailable(), solution);
        semaphore.acquire();

        switch (cspAlgorithmType) {
            case BASIC_CSP:
                csp.fcBacktracking(fp.getTeamConstraint(), fp.getInfoAboutMeetings(), solution,
                        fp.getAvailable());
                break;
            case FC:
                csp.fcBacktracking(fp.getTeamConstraint(), fp.getInfoAboutMeetings(), solution,
                        fp.getAvailable());
                break;
            case ARC_WITH_FC:
                csp.arcBacktracking(fp.getTeamConstraint(), fp.getInfoAboutMeetings(), solution,
                        fp.getAvailable());
                break;
            default:
                throw new AssertionError();
        }

    }

    private CspAlgorithmType cspAlgorithmType;
    private Thread thread;

    @Override
    public void run() {
        try {
            runCSPAlgoritum(cspAlgorithmType);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlannerCSP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void startSimulation(CspAlgorithmType cspAlgorithmType) {
        this.cspAlgorithmType = cspAlgorithmType;
        this.thread = new Thread(this);
        this.thread.start();

    }

    @Override
    public void stopSimulation() {
        this.thread.interrupt();

    }

    @Override
    public void nextStep() {
        semaphore.release();

    }
}
