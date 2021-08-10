package agri.route.agriroute;

import android.app.Application;

public class MyApplication extends Application {

    private static agri.route.agriroute.MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized agri.route.agriroute.MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(agri.route.agriroute.ConnectivityReceiver.ConnectivityReceiverListener listener) {
        agri.route.agriroute.ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
