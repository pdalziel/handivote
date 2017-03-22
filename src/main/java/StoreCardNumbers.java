import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

class StoreCardNumbers {

    private ArrayList numsList;
    private UUID refID;
    private DB db;
    private String numFile;
    private static Logger LOGGER = LoggerFactory.getLogger(StoreCardNumbers.class);

    public StoreCardNumbers(UUID refID, String numbersFile) throws IOException {
        this.refID = refID;
        this.db = setupDB(refID);
        String numFile = numbersFile;
        readFromFile(numFile);
    }

    StoreCardNumbers(UUID refID, ArrayList numsList) {
        this.refID = refID;
        this.db = setupDB(refID);
        this.numsList = numsList;


    }
    private DB setupDB(UUID refId){
        db = DBMaker.fileDB(refId + ".register").fileMmapEnable().make();
        return db;
    }
    void readFromFile(String numFile) throws IOException {
        numsList = new ArrayList();
        File file = new File("./" + numFile);
        FileReader fr = null;
        fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        try {
            while (br.readLine() != null) {
                numsList.add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(numsList.size());
    }

    void storeCardNumbers(){
        HTreeMap<String, String> map = db.hashMap("numsMap", Serializer.STRING, Serializer.STRING).createOrOpen();
        for (int i=0; i<numsList.size(); i++){
            String str = (String) numsList.get(i);
            String [] parts = str.split(" ");
            map.put(parts[0], parts[1]);
            //System.out.println("added " +numsList.get(i));
        }
        db.commit();
        db.close();

    }
}