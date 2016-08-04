/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.learnpad.ontology.simulation;

import ch.fhnw.cbr.persistence.OntAO;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import eu.learnpad.ontology.config.APP;
import eu.learnpad.ontology.persistence.FileOntAO;
import eu.learnpad.ontology.persistence.util.OntUtil;
import eu.learnpad.ontology.recommender.RecommenderException;
import eu.learnpad.ontology.util.ArgumentCheck;
import eu.learnpad.or.rest.data.SimulationScoreType;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Provides methods to log (store/update) user simulation scores in the ontology
 * for further analysis (KPI performance). Three main types of simulation scores
 * are logged:
 *
 * - Process simulation score - Simulation session score (where more than one
 * process might be simulated) - Global simuluation score (over all simulations
 * of a user)
 *
 * @author sandro.emmenegger
 */
public class SimulationScoreLog {

    private static final SimulationScoreLog SINGLETON = new SimulationScoreLog();

    private SimulationScoreLog() {
    }

    public static SimulationScoreLog getInstance() {
        return SINGLETON;
    }

    /**
     * Stores latest simulation score of given type in the ontology. This scores
     * might be used for KPI calculations.
     *
     * @param timestamp
     * @param simulationSessionId
     * @param modelSetId
     * @param processArtifactId
     * @param userId
     * @param scoreType
     * @param scoreUpdateValue
     */
    public void logSimulationScore(Long timestamp, String simulationSessionId, String modelSetId,
            String processArtifactId, String userId, SimulationScoreType scoreType, Float scoreUpdateValue) throws RecommenderException {

        ArgumentCheck.notNullThrowException(timestamp, "Timestamp for simulation score update missing.");
        ArgumentCheck.notNullThrowException(userId, "UerId for simulation score update missing.");
        ArgumentCheck.notNullThrowException(simulationSessionId, "ScoreType for simulation score update missing.");
        ArgumentCheck.notNullThrowException(scoreUpdateValue, "Score value for simulation score update missing.");

        if (scoreType.equals(SimulationScoreType.BP_SCORE)) {
            ArgumentCheck.notNullThrowException(processArtifactId, "Process artifact id cannot be null in case of a business process score.");
        }
        if (scoreType.equals(SimulationScoreType.SESSION_SCORE)) {
            ArgumentCheck.notNullThrowException(simulationSessionId, "Simulation session id cannot be null in case of a simulation session score.");
        }

        OntModel model = FileOntAO.getInstance().getModelWithExecutionData(modelSetId);

        Individual scoreInstance = null;
        switch (scoreType) {
            case BP_SCORE: {
                scoreInstance = createInstance(model, APP.NS.LPD + "BPSimulationScore", "BPScore_value_");
                Individual process = model.getIndividual(APP.NS.TRANSFER + processArtifactId);
                addProperty(model, scoreInstance, APP.NS.LPD + "simulationScoreBelongsToBusinessProcess", process);
                break;
            }
            case SESSION_SCORE: {
                scoreInstance = createInstance(model, APP.NS.LPD + "SimulationSessionScore", "BPScore_value_");
                Individual simulationSessionCase = model.getIndividual(simulationCaseInstanceUri(simulationSessionId));
                if (simulationSessionCase == null) {
                    OntClass simSessionCaseClass = model.getOntClass(simulationCaseUri());
                    simulationSessionCase = simSessionCaseClass.createIndividual(simulationCaseInstanceUri(simulationSessionId));
                }
                addProperty(model, scoreInstance, APP.NS.LPD + "simulationScoreOfSession", simulationSessionCase);
                break;
            }
            case GLOBAL_SCORE: {
                scoreInstance = createInstance(model, APP.NS.LPD + "GlobalSimulationScore", "BPScore_value_");
                break;
            }
        }

        if (scoreInstance != null) {
            Literal scoreValue = model.createTypedLiteral(scoreUpdateValue);
            addProperty(model, scoreInstance, APP.NS.LPD + "hasSimulationScore", scoreValue);
            
            Individual user = getUser(model, userId);
            addProperty(model, scoreInstance, APP.NS.LPD + "simulationScoreOfPerformer", user);
            
            Calendar timestampCalendar = Calendar.getInstance();
            timestampCalendar.setTimeInMillis(timestamp);
            Literal timestampValue = model.createTypedLiteral(timestampCalendar);
            addProperty(model, scoreInstance, APP.NS.LPD + "simulationScoreCreatedAt", timestampValue);            
        }

    }

    private Individual createInstance(OntModel model, String className, String prefix) {
        OntClass ontClass = model.getOntClass(className);
        Individual scoreInstance = ontClass.createIndividual(APP.NS.EXEC + prefix + UUID.randomUUID());
        return scoreInstance;
    }

    private void addProperty(OntModel model, Individual instance, String propertyName, RDFNode value) {
        OntProperty property = model.getOntProperty(propertyName);
        instance.addProperty(property, value);
    }

    private String simulationCaseUri() {
        return APP.CONF.getString("ontology.simulation.case.class.uri");
    }

    private String simulationCaseInstanceUri(String simulationSessionId) {
        return simulationCaseUri() + simulationSessionId;
    }
    
    private Individual getUser(OntModel model, String userId){
        OntClass ontClass = model.getOntClass(APP.NS.OMM + "Performer");
        OntProperty ontProperty = model.getOntProperty(APP.NS.EMO + "performerHasEmailAddress");
        Literal userIdValue = model.createTypedLiteral(userId);
        List<Individual> persons = OntUtil.getInstancesWithProperty(model, ontClass, ontProperty, userIdValue);
        
        if(!persons.isEmpty()){
            return persons.get(0);
        }
        return null;
        
    }
}
