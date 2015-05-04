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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        TextView titleTextView = (TextView) findViewById(R.id.operationNameTV);
        TextView timeTextView = (TextView) findViewById(R.id.tvTime);
        final EditText unitsToProduce = (EditText) findViewById(R.id.unitsET);
        final ListView requiredResourcesLV = (ListView) findViewById(R.id.requiredResourcesLV);

        Bundle extras = getIntent().getExtras();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(extras!= null){
            currentOperation = extras.getString("operationName");
            currentMachine = extras.getInt("selectedMachine");
            operationPosition = extras.getInt("operationPosition");
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");
            titleTextView.setText("Operation " + currentOperation);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        actualOperation = g.getSimulation().getOperations().get(operationPosition);
        Log.v("TEST", actualOperation.getName());
        final ResourceListAdapter requiredAdapter = new ResourceListAdapter(this, requiredResources, operationsAmount);

        for( int i : actualOperation.getRequires()){
            requiredResources.add(g.getSimulation().getOperations().get(i).getName());
            operationsAmount.add(g.getSimulation().getOperations().get(i).getAmount());

            final int reqOpPosition = i;

            simulationRef.child("operations/"+g.getSimulation().getOperations().get(i).getId()+"/amount").addValueEventListener(new ValueEventListener() {
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
        }


        requiredResourcesLV.setAdapter(requiredAdapter);


        Button startMachineButton = (Button) findViewById(R.id.startMachineButton);
        startMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pending verifications
                int amountToProduce = 0;
                amountToProduce = Integer.parseInt(unitsToProduce.getText().toString());
                long millis = System.currentTimeMillis() - g.getStartTime();
                Machine temp = g.getSimulation().getMachines().get(currentMachine);
                temp.setCurrentResource(operationPosition);
                for (int i = 0; i < amountToProduce; i++) {
                    millis = millis + actualOperation.getTime();
                    Log.v("MILLIS", millis+" ");
                    temp.addTime(millis);
                }

                simulationRef.child("operations/"+operationPosition+"/amount").runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if(currentData.getValue() == null) {
                            currentData.setValue(0);
                        } else {
                            currentData.setValue((Long) currentData.getValue() - 1);
                        }
                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                    }
                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                    }
                });
            }
        });




    }

    @Override
    public void onBackPressed() {
        requiredResources.clear();
        operationsAmount.clear();
        super.onBackPressed();
    }

}
