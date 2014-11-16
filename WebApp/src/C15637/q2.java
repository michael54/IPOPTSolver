package C15637;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * Servlet implementation class q1
 */
@WebServlet("/q2")
public final class q2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DB db = null;
	private static GridFS gridfs = null;
	static String ipoptPath = "/home/ubuntu/Ipopt-3.11.9/build/bin/ipopt";
	static String s = null;
	private static final int POOL_SIZE = 1;
	private static final BlockingQueue<Task> queue = new SynchronousQueue<Task>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public q2() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		try {
			db = utils.MongoDBUtils.connect();
			gridfs = new GridFS(db);
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < POOL_SIZE; i++) {
			Worker worker = new Worker(queue,db);
			worker.start();
		}
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		utils.MongoDBUtils.destory();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter responseOut = response.getWriter();
		String idString = request.getParameter("id");
		DBObject found = utils.MongoDBUtils.getModelFromSubmission(idString);
		String fileId = found.get("f").toString();
		GridFSDBFile file = gridfs.findOne((ObjectId) found.get("f"));
		String fileName = "/tmp/" + fileId + ".nl";
		file.writeTo(fileName);

		try {
			queue.put(new Task(ipoptPath+" "+fileName,idString));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseOut.write("Submit successful");
		
	}


}