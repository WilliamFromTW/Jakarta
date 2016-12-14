package inmethod.jakarta.web;


import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

	/**
	 * Update DDNS using google DDNS.
	 * This class must run in java web container (servlet 3.1).
	 * 
	 * @BackEnd
	 * <pre>
	 *   URL: ddclientProxyServlet
	 * 
	 *   FEATURE: update DDNS  
	 *   PARAMETER: 
	 *        (R) username
	 *        (R) password
	 *        (R) hostname
	 *        (O) myip 
	 *   回傳:
	 *        {"STATUS":"TRUE|FALSE","MSG":"msg"}
	 * </pre>       
	 */
	@WebServlet(name = "ddclientProxyServlet", urlPatterns = { "/ddclientProxyServlet" })
	public class ddclientProxyServlet extends HttpServlet {
		private static String FUNCTION_NAME = null;
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, java.io.IOException {
			doPost(req, resp);
		}

		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, java.io.IOException {
			
			response.setContentType("text/html; charset=UTF-8");
			FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
			PrintWriter out = response.getWriter();
			String arg[] = new String [5];
			arg[0] = "googledomains";
			arg[1] = request.getParameter("username");
			arg[2] = request.getParameter("password");
			arg[3] = request.getParameter("hostname");
			if( request.getParameter("myip")!=null )
			  arg[4] = request.getParameter("myip");
			else
			  arg[4] = "";
			ddclient.main(arg);
			out.println("{\"STATUS\":\"TRUE\",\"MSG\":\"pass to ddclient\"}");
			out.flush();
			out.close();
	  }
	}
	