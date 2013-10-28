package se.vgregion.siths.jsf;

import com.secmaker.netid.nias.ResultCollect;
import org.springframework.stereotype.Component;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Patrik Bergström
 */
@Component
public class ManagedBean {

    private static final String LINE_FEED = "<br/>";

    public String getMessage() {
        return "Hejsan";
    }

    public void login() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect("vgr://");
    }

    public String getAuthInfo() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        StringBuilder sb = new StringBuilder();

        if (session.getAttribute("authInfo") != null) {
            ResultCollect authInfo = (ResultCollect) session.getAttribute("authInfo");

            sb.append("Enhet: " + authInfo.getDeviceInfo().getName() + LINE_FEED);
            sb.append("OCS-svar: " + authInfo.getOcspResponse() + LINE_FEED);
            sb.append("Autentiseringsstatus: " + authInfo.getProgressStatus() + LINE_FEED);
            sb.append("Signatur: " + authInfo.getSignature() + LINE_FEED);
            return sb.toString();
        } else {
            return "Ej inloggad";
        }
    }

    public boolean getLoggedIn() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        return session.getAttribute("authInfo") != null;
    }
}
