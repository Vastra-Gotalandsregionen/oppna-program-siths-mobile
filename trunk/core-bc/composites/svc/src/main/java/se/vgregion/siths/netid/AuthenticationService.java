package se.vgregion.siths.netid;

import com.secmaker.netid.nias.NetiDAccessServer;
import com.secmaker.netid.nias.NetiDAccessServerSoap;
import com.secmaker.netid.nias.ResultCollect;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Patrik Bergström
 */
public class AuthenticationService {

    private final String wsdlUrl = "https://access.secmaker.com:443/nias/ServiceServer.asmx?WSDL";
    private final NetiDAccessServerSoap client;

    public AuthenticationService() throws MalformedURLException {
        NetiDAccessServer accessServer = new NetiDAccessServer(new URL(wsdlUrl));
        client = accessServer.getNetiDAccessServerSoap12();
    }

    public String authenticate(String hsaId) {
        String orderRef = client.authenticate(hsaId, null, null);
        return orderRef;
    }

    public ResultCollect collect(String orderRef) {
        ResultCollect collect = client.collect(orderRef);

        return collect;
    }


}
