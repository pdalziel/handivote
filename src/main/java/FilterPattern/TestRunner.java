package FilterPattern;


import java.util.UUID;

public class TestRunner {
    private static UUID refID;



    public static void main(String[] args){
        int numCards = 100;
        refID = UUID.randomUUID();
        GenerateCardNumbers gcn = new GenerateCardNumbers(refID,"password1", "wordpass2", numCards, 4);
        StoreCardNumbers scn = new StoreCardNumbers(refID, gcn.getNumsList());
        scn.storeCardNumbers();
        EmailCollector emails = new EmailCollector();
        emails.collectVotes(refID);
        //emails.stopServer();
        System.out.println("done");
        //DB db = DBMaker.fileDB(refID + ".raw").make();

        PINValidator validator = new PINValidator(refID);
        validator.validateVoterPIN();

    }
}