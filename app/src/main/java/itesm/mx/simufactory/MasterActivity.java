package itesm.mx.simufactory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.ArrayList;


public abstract class MasterActivity extends ActionBarActivity {

    TextView timerTextView;
    long endTime = 15000;

    int budget = 0;

    String titleString = "No title";
    String userName = "Admin";
    boolean admin = false;
    boolean first = false;
    Simulation mySimulation;

    Firebase sessionRef = null;

    long teamId = 0;

    final ArrayList<String> machines = new ArrayList<String>();
    final ArrayList<Integer> myMachinesIds = new ArrayList<Integer>();
    final ArrayList<String> allMachines = new ArrayList<String>();

    final ArrayList<String> allOperations = new ArrayList<String>();
    final ArrayList<Integer> myOperationsIds = new ArrayList<Integer>();
    final ArrayList<Integer> listViewOpIds = new ArrayList<Integer>();

    final ArrayList<Integer> allOperationsAmount = new ArrayList<Integer>();

    final ArrayList<String> resources = new ArrayList<String>();
    final ArrayList<Integer> resourcesCost = new ArrayList<Integer>();

    final ArrayList<String> opsNames = new ArrayList<>();
    final ArrayList<Integer> opsProgress = new ArrayList<>();
    OperationListAdapter opsListAdapter;

    Firebase simulationRef;


    Globals g = Globals.getInstance();

    Toast myToast;


    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if(timerTextView != null) {
                long millis = System.currentTimeMillis() - g.getStartTime();
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;


                // Resource generation time check
                for( final Machine m : g.getSimulation().getMachines()){
                    if(m.getTimeCounter() < m.getTimes().size()) {
                        long v = m.getTimes().get(m.getTimeCounter());
                        Log.v("MILLIS", v + " ");
                        final Operation actualOperation = g.getSimulation().getOperations().get(m.getCurrentResource());

                        int listId = listViewOpIds.get(actualOperation.getId());

                        double startTime =((Long) ((v - actualOperation.getTime())/1000)).doubleValue();
                        double progressTime = ((Long)((millis - (v - actualOperation.getTime()))/1000)).doubleValue();
                        double endTime = ((Long)(v/1000)).doubleValue();

                        int progressVal = ((Double) ((progressTime/(endTime - startTime))*100)).intValue();

                        Log.v("STARTTIME", listId+"");

                        opsProgress.set(listId, progressVal);
//                        Log.v("STARTTIME", startTime+"");
//                        Log.v("PROGRESSTIME", progressTime+"");
//                        Log.v("ENDTIME", endTime+"");
//                        Log.v("PROGRESSVAL", progressVal+"");
                        opsListAdapter.notifyDataSetChanged();

                        // v end time
                        if (millis >= v) {
                            Log.v("MILLIS", "FINISHED ONE GOING TO NEXT");
                            m.addTimeCounter();



                            //add resource START
                            simulationRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData currentData) {
                                    Operation actualOperation = g.getSimulation().getOperations().get(m.getCurrentResource());

                                    boolean pass = true;
                                    Log.v("REQUIRED", "STARTING TRANSACTION");
                                    currentData.child("operations/" + m.getCurrentResource() + "/amount").setValue((Long) currentData.child("operations/" + m.getCurrentResource() + "/amount").getValue() + 1);
                                    currentData.child("money").setValue((Long) currentData.child("money").getValue() + actualOperation.getGain());
                                    myToast.setText("Resource " + actualOperation.getName() +" was produced");
                                    if (m.getTimeCounter() < m.getTimes().size()) {
                                        if (((Long) currentData.child("money").getValue()).intValue() >= actualOperation.getCost()) {
                                            currentData.child("money").setValue((Long) currentData.child("money").getValue() - actualOperation.getCost());
                                            for (int i : actualOperation.getRequires()) {
                                                if ((Long) currentData.child("operations/" + i + "/amount").getValue() <= 0) {
                                                    pass = false;
                                                    Log.v("REQUIRED", "Need more resources of ID: " + i);
                                                }
                                            }
                                            if (pass) {
                                                for (int i : actualOperation.getRequires()) {
                                                    currentData.child("operations/" + i + "/amount").setValue((Long) currentData.child("operations/" + i + "/amount").getValue() - 1);
                                                }


                                            } else {
                                                myToast.setText("Need more resources");
                                                m.setCurrentResource(-1);
                                                m.clearTimes();
                                                Log.v("MACHINE", "Machine " + m.getName() + " free!");
                                            }
                                        } else {
                                            Log.v("REQUIRED", "Need more money");
                                            myToast.setText("Need more money");
                                            m.setCurrentResource(-1);
                                            m.clearTimes();
                                            Log.v("MACHINE", "Machine " + m.getName() + " free!");
                                        }
                                    } else {
                                        m.setCurrentResource(-1);
                                        m.clearTimes();
                                        Log.v("MACHINE", "Machine " + m.getName() + " free!");

                                    }
                                    return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                                }

                                @Override
                                public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                                    myToast.show();
                                }
                            });
                        }
                    }
                }

                if(millis > endTime && !first){
                    first = true;
                    timerHandler.removeCallbacks(timerRunnable);
                    Intent intent = new Intent(MasterActivity.this, SummaryActivity.class);
                    intent.putExtra("admin", admin);
                    intent.putExtra("name", userName);
                    intent.putExtra("sessionTitle", titleString);
                    if(admin){
                        sessionRef.child("active").setValue(false);
                    }
                    startActivity(intent);
                    return;
                }
                seconds = seconds % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

            }

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    public void onFinish(){

    }

    @Override
    public void onPause() {
        super.onPause();
//        timerHandler.removeCallbacks(timerRunnable);
    }
}
