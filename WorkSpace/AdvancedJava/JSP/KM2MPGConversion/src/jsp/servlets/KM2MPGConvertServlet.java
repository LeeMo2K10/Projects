package jsp.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class KM2MPGConvertServlet
 */
public class KM2MPGConvertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat FMT = new DecimalFormat("#0.00");
	private static final String PAGE_TOP = "" + "<HTML>" + "<HEAD>" + "<TITLE>Fuel Efficiency Conversion Chart</TITLE>"
			+ "</HEAD>" + "<BODY>" + "<H3>Fuel Efficiency Conversion Chart</H3>"
			+ "<TABLE BORDER=1 CELLPADDING=3 CELLSPACING=0>" + "<TR>" + "<TH>Kilometers per Liter</TH>"
			+ "<TH>Miles per Gallon</TH>" + "</TR>";
	private static final String PAGE_BOTTOM = "" + "</TABLE>" + "</BODY>" + "</HTML>";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(PAGE_TOP);
		for (double kpl = 5; kpl <= 20; kpl += 1.0) {
			double mpg = kpl * 2.352146;
			out.println("<TR>");
			out.println("<TD>" + FMT.format(kpl) + "</TD>");
			out.println("<TD>" + FMT.format(mpg) + "</TD>");
			out.println("</TR>");
		}
		out.println(PAGE_BOTTOM);
	}
}
