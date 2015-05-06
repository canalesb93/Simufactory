package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OperationActivity extends ActionBarActivity {

    String currentOperation = "No name";
    Integer currentMachine = 0;
    Integer operationPosition = 0;

    final ArrayList<String> requiredResources= new ArrayList<String>();
    final ArrayList<Integer> operationsAmount = new ArrayList<Integer>();

    String titleString = "No title";
    String userName = "Admin";
    boolean admin = false;

    Globals g = Globals.getInstance();
    Operation actualOperation;
    Machine actualMachine;

    Toast myToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        TextView titleTextView = (TextView) findViewById(R.id.operationNameTV);
        TextView timeTextView = (TextView) findViewById(R.id.tvTime);

        final EditText unitsToProduce = (EditText) findViewById(R.id.unitsET);
        final ListView requiredResourcesLV = (ListView) findViewById(R.id.requiredResourcesLV);
        final TextView resCost = (TextView) findViewById(R.id.tvCost);
        final TextView resReward = (TextView) findViewById(R.id.tvReward);
        final TextView resProduces = (TextView) findViewById(R.id.tvProducedRes);

        Bundle extras = getIntent().getExtras();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(extras!= null){
            currentOperation = extras.getString("operationName");
            currentMachine = extras.getInt("selectedMachine");
            operationPosition = extras.getInt("operationPosition");
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");

        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        actualOperation = g.getSimulation().getOperations().get(operationPosition);
        actualMachine = g.getSimulation().getMachines().get(currentMachine);
        titleTextView.setText("Machine " + actualMachine.getName() + " with operation " + currentOperation);


        resCost.setText("$"+actualOperation.getCost());
        resReward.setText("$"+actualOperation.getGain());
        resProduces.setText(actualOperation.getName());

        timeTextView.setText((actualOperation.getTime() / 1000) + " seconds");
        Log.v("TEST", actualOperation.getName());
        final ResourceListAdapter requiredAdapter = new ResourceListAdapter(this, requiredResources, operationsAmount);

        myToast = Toast.makeText(getApplicationContext(), "MESSAGE ERROR", Toast.LENGTH_SHORT);

        int opCounter = 0;
        for( int i : actualOperation.getRequires()){
            requiredResources.add(g.getSimulation().getOperations().get(i).getName());
            operationsAmount.add(g.getSimulation().getOperations().get(i).getAmount());

            final int reqOpPosition = opCounter;

            simulationRef.child("operations/"+i+"/amount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int amount = Integer.parseInt(dataSnapshot.getValue().toString());
                    operationsAmount.set(reqOpPosition, amount);
                    Log.v("CHANGED", reqOpPosition+" amount is " + amount);
                    requiredAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.v("CANCELLED", "Session: " + firebaseError.getMessage());
                }
            });
            opCounter++;
        }


        requiredResourcesLV.setAdapter(requiredAdapter);


        Button startMachineButton = (Button) findViewById(R.id.startMachineButton);
        startMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pending verifications
                if(actualMachine.getCurrentResource() < 0){
                    simulationRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {

                            if (((Long) currentData.child("money").getValue()).intValue() >= actualOperation.getCost()) {
                                boolean pass = true;
                                int amountToProduce = 0;
                                amountToProduce = Integer.parseInt(unitsToProduce.getText().toString());
                                Log.v("REQUIRED", "STARTING TRANSACTION");
                                for (int i : actualOperation.getRequires()) {
                                    if ((Long) currentData.child("operations/" + i + "/amount").getValue() < amountToProduce) {
                                        pass = false;
                                        Log.v("REQUIRED", "Need more resources of ID: " + i);

                                    }
                                }
                                if (pass) {
                                    for (int i : actualOperation.getRequires()) {
                                        currentData.child("operations/" + i + "/amount").setValue((Long) currentData.child("operations/" + i + "/amount").getValue() - 1);
                                    }
                                    currentData.child("money").setValue((Long) currentData.child("money").getValue() - actualOperation.getCost());
                                    Log.v("REQUIRED", "Setting amounts");

                                    long millis = System.currentTimeMillis() - g.getStartTime();

                                    actualMachine.setCurrentResource(operationPosition);
                                    for (int i = 0; i < amountToProduce; i++) {
                                        millis = millis + actualOperation.getTime();
                                        Log.v("MILLIS", millis + " ");
                                        actualMachine.addTime(millis);
                                    }
                                    myToast.setText("Machine activated");

                                } else {
                                    Log.v("TOAST", "You need more resources");
                                    myToast.setText("You need more resources");
                                }
                            } else {
                                Log.v("TOAST", "You need more money");
                                myToast.setText("You need more money");
                            }
                            return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                        }


                        @Override
                        public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                            myToast.show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Machine is in use ", Toast.LENGTH_SHORT).show();
                    Log.v("TOAST", "Machine is in use");
                }

            }
        });




    }

    public void toastNoResource(){

    }

    public void toastNoMoney(){
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }

}
