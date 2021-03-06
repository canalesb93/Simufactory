package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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


//Operation class Activity
public class OperationActivity extends ActionBarActivity {

    //Variable initialization
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

    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        //Views initializations
        gestureDetector = new GestureDetector(getApplicationContext(), new SwipeGestureDetector());

        TextView titleTextView = (TextView) findViewById(R.id.operationNameTV);
        TextView timeTextView = (TextView) findViewById(R.id.tvTime);

        final EditText unitsToProduce = (EditText) findViewById(R.id.unitsET);
        final ListView requiredResourcesLV = (ListView) findViewById(R.id.requiredResourcesLV);
        final TextView resCost = (TextView) findViewById(R.id.tvCost);
        final TextView resReward = (TextView) findViewById(R.id.tvReward);
        final TextView resProduces = (TextView) findViewById(R.id.tvProduces);
        final TextView processing = (TextView) findViewById(R.id.tvProcessing);
        final TextView done = (TextView) findViewById(R.id.tvDone);

        Bundle extras = getIntent().getExtras();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Recieves values from TeamActivity
        if(extras!= null){
            currentOperation = extras.getString("operationName");
            currentMachine = extras.getInt("selectedMachine");
            operationPosition = extras.getInt("operationPosition");
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");

        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        //Firebase initialization
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        //Get actual Operation attributes
        actualOperation = g.getSimulation().getOperations().get(operationPosition);
        actualMachine = g.getSimulation().getMachines().get(currentMachine);
        titleTextView.setText("Machine " + actualMachine.getName() + " with operation " + currentOperation);


        //Set view values from Operation attributes
        resCost.setText("$"+actualOperation.getCost());
        resReward.setText("$"+actualOperation.getGain());
        resProduces.setText(actualOperation.getName());
        processing.setText("0");
        done.setText("0");

        //Set time in time view
        timeTextView.setText((actualOperation.getTime() / 1000) + " seconds");

        //ResourceAdapter to show the resource numbers in the ListView
        final ResourceListAdapter requiredAdapter = new ResourceListAdapter(this, requiredResources, operationsAmount);

        myToast = Toast.makeText(getApplicationContext(), "MESSAGE ERROR", Toast.LENGTH_SHORT);

        //Iterate through the required Operations of the actual Operation
        int opCounter = 0;
        for( int i : actualOperation.getRequires()){
            requiredResources.add(g.getSimulation().getOperations().get(i).getName());
            operationsAmount.add(g.getSimulation().getOperations().get(i).getAmount());

            final int reqOpPosition = opCounter;

            simulationRef.child("operations/"+i+"/amount").addValueEventListener(new ValueEventListener() {
                //Get Operations thata from Firebase and show them in views
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int amount = Integer.parseInt(dataSnapshot.getValue().toString());
                    operationsAmount.set(reqOpPosition, amount);
                    processing.setText(actualMachine.getTimes().size() + "");
                    done.setText(actualMachine.getTimeCounter()+"");
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
        simulationRef.child("operations").addValueEventListener(new ValueEventListener() {
            //Set view values
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                processing.setText(actualMachine.getTimes().size() + "");
                done.setText(actualMachine.getTimeCounter()+"");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });


        requiredResourcesLV.setAdapter(requiredAdapter);

        //Start Machine Button
        Button startMachineButton = (Button) findViewById(R.id.startMachineButton);
        startMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pending verifications
                if(actualMachine.getCurrentResource() < 0){
                    //Verifies that user has enough resources to carry on with the operation
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
                                    finish();

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeftSwipe() {}

    private void onRightSwipe() {
        finish();
    }

    // Private class for gestures
    private class SwipeGestureDetector
            extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    OperationActivity.this.onLeftSwipe();

                    // Right swipe
                } else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    OperationActivity.this.onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("YourActivity", "Error on gestures");
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }

}
