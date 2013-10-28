package se.vgregion.siths.netid;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * @author Patrik Bergström
 */
public class PocHttpListener {

    private static Server server = new Server(8080);

    public static void main(String[] args) throws Exception {
        server.addHandler(new AbstractHandler() {
            @Override
            public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {

                System.out.println(httpServletRequest);

                int k = 1;
                if (k == 1) {
                    httpServletResponse.sendRedirect("vgr://");
                }

                String hsaid = httpServletRequest.getParameter("hsaid");

                System.out.println("hsaId: " + hsaid);

                String request = httpServletRequest.getRequestURI();

                String authSoapEnvelope = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:nias=\"http://netid.secmaker.com/nias/\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <nias:Authenticate>\n" +
                        "         <nias:personalNumber>" + hsaid + "</nias:personalNumber>\n" +
                        "      </nias:Authenticate>\n" +
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

                String collectSoapEnvelope = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:nias=\"http://netid.secmaker.com/nias/\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <nias:Collect>\n" +
                        "         <!--Optional:-->\n" +
                        "         <nias:orderRef>${orderref}</nias:orderRef>\n" +
                        "      </nias:Collect>\n" +
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

                HttpURLConnection http = null;
                String orderref = httpServletRequest.getParameter("orderref");
                if (hsaid != null && orderref == null) {
                    URL url = new URL("https", "access.secmaker.com", 443, "/nias/ServiceServer.asmx");
                    http = (HttpURLConnection) url.openConnection(/*new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888))*/);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8\"");
                    http.setDoOutput(true);
                    http.setDoInput(true);
                    OutputStream outputStream = http.getOutputStream();
                    outputStream.write(authSoapEnvelope.getBytes("UTF-8"));
                    outputStream.close();

                    String soapReply = getResponseAsString(http);
                    System.out.println(soapReply);

                    String orderRef = extractOrderRef(soapReply);
                    System.out.println("orderRef: " + orderRef);

                    httpServletResponse.sendRedirect("vgr://login?hsaId=" + hsaid + "&orderref=" + orderRef + "&message=hejsan");

                } else if (hsaid != null && orderref != null) {
                    URL url = new URL("https", "access.secmaker.com", 443, "/nias/ServiceServer.asmx");
                    http = (HttpURLConnection) url.openConnection(/*new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888))*/);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8");
                    http.setDoOutput(true);
                    http.setDoInput(true);
                    OutputStream outputStream = http.getOutputStream();
                    outputStream.write(collectSoapEnvelope.replace("${orderref}", orderref).getBytes("UTF-8"));
                    outputStream.close();

                    String soapReply = getResponseAsString(http);
                    System.out.println(soapReply);

                    httpServletResponse.setCharacterEncoding("UTF-8");
                    httpServletResponse.setContentType("text/html");
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    ServletOutputStream outputStream1 = httpServletResponse.getOutputStream();
                    outputStream1.write("<!DOCTYPE html>\n<html><body><h1>Välkommen</h1></body></html>".getBytes("UTF-8"));
                    outputStream1.close();
                }
            }
        });
        server.start();
    }

    private static String getResponseAsString(HttpURLConnection http) throws IOException {
        InputStream inputStream = http.getInputStream();
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int n;
        while ((n = inputStream.read(buf)) != -1) {
            sb.append(new String(buf, 0, n, "UTF-8"));
        }

        return sb.toString();
    }

    public static String extractOrderRef(String soapReply) {
        return soapReply.substring(soapReply.indexOf("<AuthenticateResult>") + 20, soapReply.indexOf("</AuthenticateResult>"));
    }


}
