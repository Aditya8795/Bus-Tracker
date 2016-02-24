package edu.nitt.iot.bustracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;



public class Home extends AppCompatActivity implements RetrieveJSON.MyCallbackInterface{
    Boolean destinationFlag = Boolean.FALSE;
    Boolean sourceFlag = Boolean.FALSE;

    String TAG = "edu.nitt.iot.bustracker.DEBUG";
    String selectedDestination;
    String selectedSource;

    public static String SERVER ="http://058b460d.ngrok.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Spinner spinnerSource = (Spinner) findViewById(R.id.spinnerSource);
        ArrayAdapter<CharSequence> adapter_source = ArrayAdapter.createFromResource(this,
                R.array.source_bus_stops, android.R.layout.simple_spinner_item);
        adapter_source.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(adapter_source);

        final Spinner spinnerDestination = (Spinner) findViewById(R.id.spinnerDestination);
        ArrayAdapter<CharSequence> adapter_destination = ArrayAdapter.createFromResource(this,
                R.array.destination_bus_stops, android.R.layout.simple_spinner_item);
        adapter_destination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestination.setAdapter(adapter_destination);

        spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedDestination = parent.getItemAtPosition(pos).toString();

                if(pos==0){
                    destinationFlag = Boolean.FALSE;
                }
                else{
                    destinationFlag = Boolean.TRUE;
                }

                Log.i(TAG,selectedDestination+" "+destinationFlag.toString()+" "+sourceFlag.toString()+" "+Integer.toString(pos));

                if(destinationFlag && sourceFlag){
                    setList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedSource = parent.getItemAtPosition(pos).toString();

                if(pos==0){
                    sourceFlag = Boolean.FALSE;
                }
                else{
                    sourceFlag = Boolean.TRUE;
                }

                Log.i(TAG,selectedSource+"  "+destinationFlag.toString()+" "+sourceFlag.toString()+" "+Integer.toString(pos));

                if(destinationFlag && sourceFlag){
                    setList();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }
    public String getPhoneNumber(String busStop){
        if(Objects.equals(busStop, "Station Number 1")){
            return "919629976478";
        }
        else if(Objects.equals(busStop, "Station Number 2")){
            return "919629976914";
        }
        else if(Objects.equals(busStop, "Station Number 3")){
            return "919445341302";
        }
        return null;
    }

    private void setList() {
        Log.i(TAG, selectedDestination + " and the other station is " + selectedSource);
        String getRequest = SERVER+"/stations/getAvailableBuses?station1="+getPhoneNumber(selectedSource)+"&station2="+getPhoneNumber(selectedDestination);
        Log.i(TAG, getRequest);
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (connectionDetector.isConnectedInternet()){
            new RetrieveJSON(this).execute(getRequest);
            Toast.makeText(this, "Fetching latest bus information from server", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
            destinationFlag = Boolean.FALSE;
            sourceFlag = Boolean.FALSE;
        }

    }

    @Override
    public void onRequestCompleted(JSONArray result){
        Log.i(TAG,result.toString());
        ListView listView = (ListView) findViewById(R.id.listViewBusList);

        // Defined Array values to show in ListView
        String[] values = new String[result.length()+1]; // 20 is max number of buses that could be returned
        Log.i(TAG,"The number of buses are "+Integer.toString(result.length()));

        values[0] = "Bus Number - Time Taken - Occupancy";

        for(int i=1;i<result.length()+1;i++){
            try {
                Log.i(TAG,result.getJSONObject(i-1).getString("bus_no"));
                values[i] = result.getJSONObject(i-1).getString("bus_no")+"      "+result.getJSONObject(i-1).getString("timeTaken")+"      "+result.getJSONObject(i-1).getString("occupancy");
            } catch (JSONException e) {
                values[i] = "Server has experienced some error";
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);
    }
}
