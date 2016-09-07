package eu.learnpad.simulator.mon.storage;

import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.net.ntp.TimeStamp;

import eu.learnpad.sim.rest.event.impl.SessionScoreUpdateEvent;
import eu.learnpad.simulator.mon.coverage.Learner;
import eu.learnpad.simulator.mon.utils.DebugMessages;

public class ScoreTemporaryStorage {

	private static HashMap<String, Long> sessionScoreValues;
	private static String sessionID;
	private static SessionScoreUpdateEvent lastScoreUpdateEventSeen;

	public ScoreTemporaryStorage(Vector<Learner> theLearnersInvolvedInSession, String sessionID) {
		
		ScoreTemporaryStorage.sessionID = sessionID;
				
		sessionScoreValues = new HashMap<String, Long>(theLearnersInvolvedInSession.size());
		
		for (int i = 0; i<theLearnersInvolvedInSession.size(); i++) {
						sessionScoreValues.put(
									theLearnersInvolvedInSession.get(i).getId(), 
									0l);
		}	
	}
	
	public static String getSessionID() {
		return sessionID;
	}

	public static SessionScoreUpdateEvent getLastScoreUpdateEventSeen() {
		return lastScoreUpdateEventSeen;
	}

	public static void setLastScoreUpdateEventSeen(SessionScoreUpdateEvent lastScoreUpdateEventSeen) {
		DebugMessages.println(TimeStamp.getCurrentTime(), ScoreTemporaryStorage.class.getName(), "Storing LastScoreUpdateEventSeen");
		ScoreTemporaryStorage.lastScoreUpdateEventSeen = lastScoreUpdateEventSeen;
		DebugMessages.println(TimeStamp.getCurrentTime(), ScoreTemporaryStorage.class.getName(), "Value Stored");
	}
	
	public static void setSessionID(String sessionID) {
		ScoreTemporaryStorage.sessionID = sessionID;
	}

	
	public static void setTemporaryLearnerSessionScore(String learnerID, Long scoreValue) {
		DebugMessages.println(TimeStamp.getCurrentTime(), ScoreTemporaryStorage.class.getName(), "Storing LearnerSessionScore");
		ScoreTemporaryStorage.sessionScoreValues.put(learnerID, scoreValue);
		DebugMessages.println(TimeStamp.getCurrentTime(), ScoreTemporaryStorage.class.getName(), "Value Stored");

	}
	
	public static Long getTemporaryLearnerSessionScore(String learnerID) {
		return ScoreTemporaryStorage.sessionScoreValues.get(learnerID);
	}
}