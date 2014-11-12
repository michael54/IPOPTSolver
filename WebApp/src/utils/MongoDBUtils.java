package utils;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoDBUtils {
	private static MongoClient mongoClient = null;
	private static DB db = null;
	private static DBCollection submissionColl = null;
	private static DBCollection modelColl = null;
	public MongoDBUtils() {
		// TODO Auto-generated constructor stub
	}

	public static DB connect() {
		try {
			mongoClient = new MongoClient("192.168.10.10", 27017);
			db = mongoClient.getDB("IPOPT");
			submissionColl = db.getCollection("submission");
			modelColl = db.getCollection("n_l_p_model");
//			System.out.println("Connected");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}

	public static void destory() {
		if (mongoClient != null) {
			try {
				mongoClient.close();
			} catch (MongoException e) {
				e.printStackTrace();
			}
		}
	}

	public static DBObject getModelFromSubmission(String id) {
		DBObject found = null;
		try {
			DBObject searchById = new BasicDBObject("_id", new ObjectId(id));
			DBObject submission = submissionColl.findOne(searchById);
			found = modelColl.findOne(submission.get("model"));
		} catch (MongoException e) {
			e.printStackTrace();
		} 
		return found;
	}
	
	public static void updateSubmissionResult(String id, ObjectId resultId, String result) throws UnsupportedEncodingException{
		try{
			DBObject searchById = new BasicDBObject("_id", new ObjectId(id));
			DBObject submission = submissionColl.findOne(searchById);
			submission.put("result", resultId);
			submissionColl.save(submission);
			if ((boolean)submission.get("sendemail")==true){
				utils.EmailUtils.sendResult((String) submission.get("email"), result);
			}
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

}
