package edu.nitt.iot.bustracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionDetector {
    private Context _context;
    public static final String TAG = "ConnectionDetector";

    public ConnectionDetector(Context context){
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectedInternet() {
        Log.i(TAG,"checking if connected to the internet");
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        //Now we might be connected to a wifi or even the local tower
                        // which in turn might NOT be connected to the Global Internet
                        // We can ping google to check if we are in a network which has access
                        // to most of the webPages.
                        // THUS we shall add httpParameters for ALL network operations otherwise
                        // we will have the app crash after waiting for a long time
                        Log.i(TAG,"connected");
                        return true;
                        //return isConnectedServer();
                    }
                }
            }
        }
        Log.i(TAG,"NOT connected");
        return false;
    }

    private boolean isConnectedServer() {

        try {
            Process p1 = Runtime.getRuntime().exec("ping -c 1 " +Home.SERVER);
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG,"Not connected to a network through which we can access server");
        return false;
    }
}
