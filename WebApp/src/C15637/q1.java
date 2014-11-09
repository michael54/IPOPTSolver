package C15637;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * Servlet implementation class q1
 */
@WebServlet("/q1")
public final class q1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static MongoClient mongoClient = null;
	private static DB db = null;
	private static GridFS gridfs = null;
	static String ipoptPath = "/home/michael/Downloads/Ipopt-3.11.9/build/bin/ipopt";
	static String s = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public q1() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		try {
			mongoClient = new MongoClient("192.168.10.10", 27017);
			db = mongoClient.getDB("IPOPT");
			gridfs = new GridFS(db);
			System.out.println("Connected");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		if (mongoClient != null) {
			try {
				mongoClient.close();
			} catch (MongoException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter responseOut = response.getWriter();
		String idString = request.getParameter("id");
		DBCollection coll = db.getCollection("n_l_p_model");
		System.out.println(coll.toString());

		DBObject searchById = new BasicDBObject("_id", new ObjectId(idString));
		DBObject found = coll.findOne(searchById);
		String fileId = found.get("f").toString();
		GridFSDBFile file = gridfs.findOne((ObjectId) found.get("f"));
		String fileName = "/tmp/" + fileId + ".nl";
		file.writeTo(fileName);

		Runtime rt = Runtime.getRuntime();
		String[] cmd = new String[2];
		cmd[0] = ipoptPath + " " + fileName;
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}