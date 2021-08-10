package agri.route.agriroute;

public class CheckConnection {

    public boolean checkInternetConnection() {
        boolean isConnected = agri.route.agriroute.ConnectivityReceiver.isConnected();
        return(isConnected);
    }
}