import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Arrays;
import java.util.UUID;

public class VoteRecorder {
    private UUID refID;
    private DB db;
    private Vote vote;
    private static Logger LOGGER = LoggerFactory.getLogger(VoteRecorder.class);


    public VoteRecorder(UUID refID) {
        this.refID = refID;
        this.db = setupDB(refID);
    }

    public void closeDB(){
        HTreeMap<String, String> map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
        map.close();
        db.close();
    }

    public  DB  setupDB(UUID refId){
        LOGGER.info("Setting up :" + refId.toString());
        db = DBMaker.fileDB(refId + ".raw").fileMmapEnable().make();
        return db;
    }

    public boolean recordVote(Vote vote){
        HTreeMap<String, String> map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
        // todo vaildate vote 'yes' ,'no' or user-defined option etc.
        if (map.containsKey(vote.getVoterID())){
            String [] parts = map.get(vote.getVoterID()).split(" ");

            String[] storedBallot = parts[4].split(" ");
            Vote storedVote = new Vote(parts[0],parts[1],Long.parseLong(parts[2]),parts[3],storedBallot);
            if(!Arrays.equals(vote.getBallot(), storedVote.getBallot())){
                vote.setValid(false);
                vote.setPinVerification(true);
                map.put(vote.getVoterID(), vote.toString());
                LOGGER.warn(" Recorded bad vote : " + vote.toString()+ " ");

            }

        }
        map.put(vote.getVoterID(), vote.toString());
        LOGGER.info("Added vote:  " + vote.toString());
        db.commit();

        return true;
    }

}