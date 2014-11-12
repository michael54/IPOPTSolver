package C15637;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;


public class Worker extends Thread {
	private final BlockingQueue<Task> queue;
	private String s;
	
	public Worker(BlockingQueue<Task> queue) {
		this.queue = queue;
	}

	private void doWork(Task task) throws IOException{
		Runtime rt = Runtime.getRuntime();
		String[] cmd = new String[2];
		cmd[0] = task.getFileName();
		PrintWriter responseOut = task.getResponseOut();
		Process p = rt.exec(cmd[0]);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		while ((s = stdInput.readLine()) != null) {
			responseOut.println(s);
		}
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));
		while ((s = stdError.readLine()) != null) {
			responseOut.println(s);
		}
		try {
			p.waitFor();
			stdInput.close();
			stdError.close();

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
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