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


// Import  {{{

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

// }}}




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

			writer.println(user + "> " + message);
			writer.flush();
			writer.close();

			connect.flushBuffer();
		}
	}
}




// __END__  {{{1
// vim: foldmethod=marker
