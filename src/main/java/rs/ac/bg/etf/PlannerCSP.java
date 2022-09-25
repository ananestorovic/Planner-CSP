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

    private final Logger logger = Logger.getLogger(PlannerCSP.class.getName());
    private final Semaphore semaphore;
    private final GuiController guiController;
    private final Object mutex = new Object();
    private CSPAlgorithm csp;
    private FileParser fp;
    private boolean runToEnd;
    private String teamsFile;
    private String meetingsFile;

    public PlannerCSP(GuiController guiController) {
        this.guiController = guiController;
        this.semaphore = new Semaphore(0);
    }

    private void runCSPAlgoritum() throws InterruptedException {

        fp.parseMeetingsFile("resources/meetings.json");
        fp.parseTeamsFile("resources/teams.json");
        fp.parseRequestsFile("resources/requests.json");
        fp.generateTeamConstraint(); // just once i need to generate this

        fp.generateAvailable(); //initial table, later we will update this

        Map<Pair<String, String>, Pair<DayOfWeek, List<TimeInterval>>> solution = fp.generateEmptySolution();

        guiController.initTeamTables(fp.getTeams(), fp.getallVarsTermin(), solution, fp.getTeamConstraint());
        semaphore.acquire();
        if (!runToEnd) {
            guiController.refreshGui(fp.getAvailable(), solution);
            semaphore.acquire();
        }
        switch (cspAlgorithmType) {
            case BASIC_CSP:
                csp.backtracking(fp.getTeamConstraint(), fp.getInfoAboutMeetings(), solution,
                        fp.getAvailable());
                fp.printSolution(solution);
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
        if (!csp.complete(solution)){
            guiController.refreshGui(fp.getAvailable(), solution);
            guiController.planningIsFinished();
            guiController.showMessage("There is no solution!");
        } else {
            guiController.planningIsFinished();
        }
    }

    private CspAlgorithmType cspAlgorithmType;
    private Thread thread;

    @Override
    public void run() {
        try {
            runCSPAlgoritum();
        } catch (InterruptedException ex) {
            logger.log(Level.INFO, "Thread for running csp algorithm is interrupted");
        }
    }

    @Override
    public void runAlgorithmToEnd() {
        semaphore.release();
        this.runToEnd = true;
        csp.setRunToEnd(true);
    }

    @Override
    public void startSimulation(CspAlgorithmType cspAlgorithmType, String teamsFile, String meetngsFile, String timeOffFile) {
        this.cspAlgorithmType = cspAlgorithmType;
        setTeamsFile(teamsFile);
        setMeetingsFile(meetngsFile);
        setTimeOffFile(timeOffFile);

        this.fp = new FileParser();
        this.csp = new CSPAlgorithm(fp, semaphore, guiController);
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

    private void setTeamsFile(String teamsFile) {
        if (teamsFile != null) {
            this.teamsFile = teamsFile;
        } else {
            this.teamsFile = "resources/meetings.json";
        }
    }

    private void setMeetingsFile(String meetngsFile) {
        if (meetngsFile != null) {
            this.meetingsFile = teamsFile;
        } else {
            this.meetingsFile = "resources/teams.json";
        }
    }

    private void setTimeOffFile(String timeOffFile) {
        if (timeOffFile != null) {
            this.meetingsFile = teamsFile;
        } else {
            this.meetingsFile = "resources/requests.json";
        }
    }
}
