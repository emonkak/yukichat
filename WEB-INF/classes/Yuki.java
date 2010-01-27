// yukichat, the chat servlet that used comet.
// Version: 0.0.0
// Copyright (C) 2010 emon <http://github.com/emonkak>
// License: MIT license  {{{
//     Permission is hereby granted, free of charge, to any person
//     obtaining a copy of this software and associated documentation
//     files (the "Software"), to deal in the Software without
//     restriction, including without limitation the rights to use,
//     copy, modify, merge, publish, distribute, sublicense, and/or
//     sell copies of the Software, and to permit persons to whom the
//     Software is furnished to do so, subject to the following
//     conditions:
//
//     The above copyright notice and this permission notice shall be
//     included in all copies or substantial portions of the Software.
//
//     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//     EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//     NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//     HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//     WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//     FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//     OTHER DEALINGS IN THE SOFTWARE.
// }}}


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import net.arnx.jsonic.JSON;
import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;


public class Yuki extends HttpServlet implements CometProcessor
{
	private ArrayList<HttpServletResponse> connections =
		new ArrayList<HttpServletResponse>();

	public void event(CometEvent event)
		throws IOException, ServletException
	{
		HttpServletRequest request = event.getHttpServletRequest();
		HttpServletResponse response = event.getHttpServletResponse();

		// When it is post request, notice all users and close connections.
		if (request.getMethod().equals("POST")) {
			String user = request.getParameter("user");
			String message = request.getParameter("message");

			pushClients(user, message);
			event.close();
			return;
		}

		switch (event.getEventType()) {
		case BEGIN:
			event.setTimeout(10 * 60 * 1000);  // Timeout in 10 minutes.
			response.setContentType("text/plain");

			synchronized (connections) {
				connections.add(response);
			}
			break;
		case END:
			synchronized (connections) {
				connections.remove(response);
			}
			event.close();
			break;
		case ERROR:
			synchronized (connections) {
				connections.remove(response);
			}
			event.close();
			break;
		case READ:
			break;
		}
	}

	private void pushClients(String user, String message)
		throws IOException
	{
		for (HttpServletResponse connect : connections) {
			PrintWriter writer = connect.getWriter();

			writer.println(user + ": " + message);
			writer.flush();
			writer.close();

			connect.flushBuffer();
		}
	}
}




/*
public class ChatCore extends HttpServlet  implements CometProcessor
{
	protected ArrayList<HttpServletResponse> connections =
		new ArrayList<HttpServletResponse>();
	protected MessageSender sender = null;

	// The event handler that is called when there was access.
	public void event(CometEvent event) throws IOException, ServletException
	{
		HttpServletRequest request = event.getHttpServletRequest();
		HttpServletResponse response = event.getHttpServletResponse();
		PrintWriter writer;

		response.setContentType("application/xhtml+xml; charset=utf-8");

		// Receive post message.
		if (request.getMethod().equals("POST")) {
			String message = request.getParameter("message");
			sender.send(message);

			event.close();
			return;
		}

		switch (event.getEventType()) {
		case BEGIN:
			event.setTimeout(60 * 1000 * 30);

			request.setAttribute("org.apache.tomcat.comet", Boolean.TRUE);
			writer = response.getWriter();
			writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"");
			writer.println("  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
			writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"ja\">");
			writer.println("<head>");
			writer.println("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			writer.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />");
			writer.println("  <script type=\"text/javascript\" src=\"chat.js\"></script>");
			writer.println("  <title>YUKI chat</title>");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("<div id=\"header\">");
			writer.println("  <form action=\"#\">");
			writer.println("    <div>");
			writer.println("      <input type=\"text\" id=\"message\" size=\"64\" onkeypress=\"return post(event)\" />");
			writer.println("    </div>");
			writer.println("  </form>");
			writer.println("</div>");
			writer.println("<div id=\"contents\">");
			writer.flush();
			synchronized (connections) {
				connections.add(response);
			}
			break;
		case ERROR:
			synchronized (connections) {
				connections.remove(response);
			}
			event.close();
			break;
		case END:
			synchronized (connections) {
				connections.remove(response);
			}
			writer = response.getWriter();
			writer.println("</div>");
			writer.println("</body>");
			writer.println("</html>");
			event.close();
			break;
		case READ:
			break;
		}
	}

	// Initialize
	public void init() throws ServletException
	{
		sender = new MessageSender();
		Thread sender_thread = new Thread(sender);
		sender_thread.setDaemon(true);
		sender_thread.start();
	}

	// Quit
	public void destroy()
	{
		connections.clear();
		sender.stop();
		sender = null;
	}

	// The thread for date sending.
	public class MessageSender implements Runnable
	{
		protected boolean running = true;
		protected ArrayList<String> messages = new ArrayList<String>();

		public void send(String message)
		{
			synchronized (messages) {
				messages.add(message);
				messages.notify();
			}
		}

		public void run()
		{
			while (running) {
				if (messages.size() == 0) {
					try {
						synchronized (messages) {
							messages.wait();
						}
					} catch (InterruptedException e) {
					}
				}

				synchronized (connections) {
					String[] queue = null;
					synchronized (messages) {
						queue = messages.toArray(new String[0]);
						messages.clear();
					}

					// Send any queue message on all the open connections.
					for (int i = 0; i < connections.size(); i++) {
						try {
							PrintWriter writer = connections.get(i).getWriter();
							for (int j = 0; j < queue.length; j++)
								writer.println("<p>YUKI.N> " + queue[j] + "</p>");
							writer.flush();
						} catch (IOException e) {
						}
					}
				}
			}
		}

		public void stop()
		{
			running = false;
		}
	}
}
*/
// __END__  {{{1
// vim: foldmethod=marker
