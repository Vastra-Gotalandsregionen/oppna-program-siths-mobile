package se.vgregion.siths.servlet;

import com.secmaker.netid.nias.ResultCollect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.vgregion.siths.netid.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Patrik Bergström
 */
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    private final AuthenticationService authenticationService;

    public LoginServlet() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml");

        authenticationService = ctx.getBean(AuthenticationService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String hsaid = req.getParameter("hsaid");
        String orderref = req.getParameter("orderref");

        LOGGER.info("HSA-ID: " + hsaid);
        LOGGER.info("orderref: " + orderref);

        if (hsaid != null && orderref == null) {
            orderref = authenticationService.authenticate(hsaid);
            resp.sendRedirect("vgr://login?hsaId=" + hsaid + "&orderref=" + orderref + "&message=hejsan");
        } else if (hsaid != null && orderref != null) {
            ResultCollect collect = authenticationService.collect(orderref);

            // todo verify login is completed

            req.getSession().setAttribute("authInfo", collect);
            resp.sendRedirect("/siths-poc/index.jsf");
        }

    }
}
