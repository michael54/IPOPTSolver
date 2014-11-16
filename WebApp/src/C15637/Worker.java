package C15637;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;


public class Worker extends Thread {
	private final BlockingQueue<Task> queue;
	private DB db = null;
	
	public Worker(BlockingQueue<Task> queue, DB db) {
		this.queue = queue;
		this.db = db;
	}

	private void doWork(Task task) throws IOException{
		Runtime rt = Runtime.getRuntime();
		String[] cmd = new String[2];
		cmd[0] = task.getFileName();
		String idString = task.getIdString();
		Process p = rt.exec(cmd[0]);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			stdInput.close();
			stdError.close();
			e1.printStackTrace();
		}
		String stdOutput = IOUtils.toString(stdInput);
		String errOutput = IOUtils.toString(stdError);
				
		DBCollection resultColl = db.getCollection("result");
		BasicDBObject result = new BasicDBObject("std", stdOutput)
				.append("err", errOutput)
				.append("code", 0);
		resultColl.insert(result);
		utils.MongoDBUtils.updateSubmissionResult(idString, (ObjectId) result.get("_id"), stdOutput);
		stdInput.close();
		stdError.close();
		
		System.out.println("Task is done!");
	}

	public void run() {
		try {
			while (true) {
				Task s = queue.take();
				doWork(s);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}