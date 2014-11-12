package C15637;

import java.io.PrintWriter;

public class Task {
	public Task(String fileName, PrintWriter responseOut) {
		this.fileName = fileName;
		this.responseOut = responseOut;
	}

	private String fileName;
	private PrintWriter responseOut;

	public String getFileName() {
		return fileName;
	}

	public PrintWriter getResponseOut() {
		return responseOut;
	}

}