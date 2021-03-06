package itesm.mx.simufactory;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

/*
This activity manages the main simulation activity, where the users are interacting mostly with the
simulation.
It defines variables from MasterActivity which it extends.

It uses 3 list views to manage: resources, machines and operations.

 */
public class TeamActivity extends MasterActivity implements View.OnClickListener {

    final ArrayList<String> operations = new ArrayList<String>();

    Integer selectedMachine = -1;
    Button btnStore;


    // Defines most variables
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        timerTextView = (TextView) findViewById(R.id.mainTimer);
        TextView teamName = (TextView) findViewById(R.id.teamNameTV);
        final TextView currentBudget = (TextView) findViewById(R.id.currentBudgetTV);
        final ListView operationLV = (ListView) findViewById(R.id.operationsLV);
        final ListView machinesLV = (ListView) findViewById(R.id.machinesLV);
        final ListView resourcesLV = (ListView) findViewById(R.id.resourcesLV);

        opsListAdapter = new OperationListAdapter(this, opsNames, opsProgress);
        resID=getResources().getIdentifier("finished", "raw", getPackageName());
        mediaPlayer= MediaPlayer.create(this, resID);
        btnStore = (Button) findViewById(R.id.buyResourcesButton);

        g.setStartTime(System.currentTimeMillis());
        timerHandler.postDelayed(timerRunnable, 0);

        Bundle extras = getIntent().getExtras();

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");
            if(extras.get("name") != null)
                userName = extras.getString("name");
            teamName.setText("Team " + userName);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        myToast = Toast.makeText(getApplicationContext(), "MESSAGE ERROR", Toast.LENGTH_SHORT);

        // Initializes Firebase library
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase userRef = ref.child("sessions/"+titleString+"/users/"+userName);
        sessionRef = ref.child("sessions/"+titleString);
        simulationRef = ref.child("sessions/"+titleString+"/simulation");
        simulationRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot snapshot) {
                 endTime = (long) snapshot.child("time").getValue();
             }

             public void onCancelled(FirebaseError firebaseError) {}
        });

        final ResourceListAdapter resourcesAdapter = new ResourceListAdapter(this, allOperations, allOperationsAmount);
        //final ArrayAdapter<String> operationsAdapter = new ArrayAdapter<String>(this, R.layout.activity_row_operations, R.id.operationNameTV, operations);

        final ArrayAdapter<String> machinesAdapter = new ArrayAdapter<String>(this, R.layout.activity_row_machines, R.id.machineNameTV, machines);

        // Retrieves information from the user
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.child("teamId").getValue() != null)
                    teamId = (long) snapshot.child("teamId").getValue();

                int oCounter = 0;
                Log.v("TEAM", teamId + " is the team id");
                for( Operation o : g.getSimulation().getOperations()){
                    if(( o.getTeam() == teamId || teamId == 0)  && o.getTime() != 0) {
                        myOperationsIds.add(oCounter);
                        Log.v("DEBUG", " ASSIGNED id"+ oCounter + " from " + o.getName() + " on teamID" + o.getTeam());
                    }
                    oCounter++;
                }

                int mCounter = 0;
                for( Machine m : g.getSimulation().getMachines()){
                    if( m.getTeam() == teamId || teamId == 0) {
                        machines.add(m.getName());
                        myMachinesIds.add(mCounter);
                        Log.v("MACHINES", "Added machine " + m.getName() + " for team " + teamId);

                    }
                    allMachines.add(m.getName());
                    mCounter++;
                }
                machinesAdapter.notifyDataSetChanged();
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // Handles the monay TextView, keeps it looking for changes
        simulationRef.child("money").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                budget = Integer.parseInt(dataSnapshot.getValue().toString());
                String formatted = String.format("%,d", budget);
                formatted = '$' + formatted;
                currentBudget.setText(formatted);


                Log.v("CHANGED", "Budget is " + budget);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CANCELLED", "Session: " + firebaseError.getMessage());
            }
        });

        // Retrieves all operations from firebase, filters the ones which do not belong to the user
        // team.
        simulationRef.child("operations").addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            int opsCounter = 0;

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                if(((long) snapshot.child("team").getValue() == teamId || teamId == 0) && Integer.parseInt(snapshot.child("time").getValue().toString()) != 0) {

                    double fractionPercentage = 0.0;
                    int percentageProgress = 0;

                    opsNames.add((String) snapshot.child("name").getValue());
                    opsProgress.add(0);
                    listViewOpIds.add(opsCounter);
                    opsListAdapter.notifyDataSetChanged();
                    opsCounter++;
                } else {
                    listViewOpIds.add(-1);
                }
                if(Integer.parseInt(snapshot.child("time").getValue().toString()) == 0){
                    resources.add(snapshot.child("name").getValue().toString());
                    resourcesCost.add(Integer.parseInt(snapshot.child("cost").getValue().toString()));
                }
                allOperations.add((String) snapshot.child("name").getValue());

                allOperationsAmount.add(Integer.parseInt(snapshot.child("amount").getValue().toString()));

                resourcesAdapter.notifyDataSetChanged();

            }

            public void onChildChanged(DataSnapshot snapshot, String s) {
                int index = Integer.parseInt(snapshot.getKey().toString());

//                if(((Long) snapshot.child("amount").getValue()).intValue() >  allOperationsAmount.get(index)){
//                    Toast.makeText(getApplicationContext(), "Resource "+ snapshot.child("name").getValue().toString() + " produced.", Toast.LENGTH_SHORT).show();
//                }

                allOperationsAmount.set(index, Integer.parseInt(snapshot.child("amount").getValue().toString()));
                g.getSimulation().getOperations().get(index).setAmount(Integer.parseInt(snapshot.child("amount").getValue().toString()));

                resourcesAdapter.notifyDataSetChanged();

            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(FirebaseError firebaseError) {}
        });

        //operationLV.setAdapter(operationsAdapter);
        operationLV.setAdapter(opsListAdapter);
        machinesLV.setAdapter(machinesAdapter);
        resourcesLV.setAdapter(resourcesAdapter);
        registerForContextMenu(operationLV);

        AdapterView.OnItemClickListener operationListViewListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pressedOperation = opsNames.get(position);
                Log.v("DEBUG", myOperationsIds.get(position)+" is the id");
                if(selectedMachine != -1) {
                    Intent intent = new Intent(TeamActivity.this, OperationActivity.class);
                    intent.putExtra("admin", admin);
                    intent.putExtra("sessionTitle", titleString);
                    intent.putExtra("selectedMachine", myMachinesIds.get(selectedMachine));
                    intent.putExtra("operationName", pressedOperation);
                    intent.putExtra("operationPosition", myOperationsIds.get(position));

                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "You must select a machine", Toast.LENGTH_SHORT).show();
                }


            }
        };
        AdapterView.OnItemClickListener machineListViewListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMachine = position;
            }
        };
        machinesLV.setOnItemClickListener(machineListViewListener);
        operationLV.setOnItemClickListener(operationListViewListener);

        btnStore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(btnStore.isPressed()){
            Intent intent = new Intent(TeamActivity.this, StoreActivity.class);
            intent.putExtra("admin", admin);
            intent.putExtra("sessionTitle", titleString);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Can't go back while session is running", Toast.LENGTH_SHORT).show();
    }



}
