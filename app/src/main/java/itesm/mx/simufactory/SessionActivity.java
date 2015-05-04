package itesm.mx.simufactory;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;


public class SessionActivity extends ActionBarActivity {
    final int REQUEST_CODE = 1;
    TextView titleTextView;
    String titleString = "No title";
    String userName = "noname";
    boolean admin = false;
    Simulation simu;
    Simulation mySimulation;


    final ArrayList<String> users = new ArrayList<String>();
    int teamCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        final ListView usersListView = (ListView) findViewById(R.id.teamnamesListView);
        final Button startSession = (Button) findViewById(R.id.startSessionButton);
        final EditText timerConfig = (EditText) findViewById(R.id.timerConfig);
        final EditText budgetConfig = (EditText) findViewById(R.id.budgetET);
        final EditText expensesConfig = (EditText) findViewById(R.id.expensesET);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        titleTextView = (TextView) findViewById(R.id.titleSession);

        Bundle extras = getIntent().getExtras();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");
            userName = extras.getString("name");
            titleTextView.setText(titleString);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase usersRef = ref.child("sessions/"+titleString+"/users");
        final Firebase sessionRef = ref.child("sessions/"+titleString);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_usersrow, R.id.usersrowTV, users);

        usersRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                users.add((String) snapshot.child("name").getValue());

                adapter.notifyDataSetChanged();
                if(admin) {
                    usersRef.child(snapshot.child("name").getValue().toString()).child("teamId").setValue(teamCounter);
                    teamCounter++;
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(FirebaseError firebaseError) {}
        });

//      Log.v("DEBUG test,", "testing"+ sessionRef.child("starter").)

        if(!admin) {
            timerConfig.setEnabled(false);
            expensesConfig.setEnabled(false);
            budgetConfig.setEnabled(false);
            startSession.setEnabled(false);
            spinner.setEnabled(false);

            sessionRef.child("started").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("CHANGED", "Session started: " + dataSnapshot.getValue().toString());
                    if (dataSnapshot.getValue().toString() == "true") {


                        //pending selection
                        Globals g = Globals.getInstance();
                        g.setSimulation(createModel1());

                        Intent intent = new Intent(SessionActivity.this, TeamActivity.class);
                        intent.putExtra("admin", false);
                        intent.putExtra("name", userName);
                        intent.putExtra("sessionTitle", titleString);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.v("CANCELLED", "Session started: " + firebaseError.getMessage());

                }
            });
        }

        usersListView.setAdapter(adapter);
        registerForContextMenu(usersListView);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.models_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        startSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            if(admin){
                mySimulation = createModel1();
                if(timerConfig.getText().length() > 0)
                   mySimulation.setTime((Long.parseLong(timerConfig.getText().toString())) * 1000);
                sessionRef.child("simulation").setValue(mySimulation);

                Globals g = Globals.getInstance();
                g.setSimulation(mySimulation);

                sessionRef.child("started").setValue(true);

                Intent intent = new Intent(SessionActivity.this, TeamActivity.class);
                intent.putExtra("admin", true);
                intent.putExtra("sessionTitle", titleString);



                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "No admin priviledges", Toast.LENGTH_SHORT).show();
            }
            }
        });

    }


    public Simulation createModel1(){
        ArrayList<Operation> operations = new ArrayList<Operation>();
        ArrayList<Machine> machines = new ArrayList<Machine>();

        /// RESOURCE, COST, GAIN, TIME, TEAMID, REQUIRED, AMOUNT ( owned )
        operations.add(new Operation("A", 5, 0, 0, 0, null, 3));
        operations.add(new Operation("B", 10, 0, 0, 0, null, 3));

        operations.add(new Operation("P1", 10, 0, 5000, 1, new ArrayList<Integer>(Arrays.asList(0)), 0));
        operations.add(new Operation("P2", 20, 0, 7500, 2, new ArrayList<Integer>(Arrays.asList(1)), 0));

        operations.add(new Operation("C", 10, 35, 10000, 3, new ArrayList<Integer>(Arrays.asList(2, 3)), 0));

        machines.add(new Machine("Machine 1", 1));
        machines.add(new Machine("Machine 2", 1));
        machines.add(new Machine("Machine 3", 2));
        machines.add(new Machine("Machine 4", 2));
        machines.add(new Machine("Machine 5", 3));

        //CASH, TIME, OPERATIONS, MACHINES, NUM OF TEAMS
        Simulation simulation1 = new Simulation(100, 10000, operations, machines, 3);
        this.simu = simulation1;
        return simulation1;
    }

    public Simulation getSimulation(){
        return simu;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Can't go back while session is running", Toast.LENGTH_SHORT).show();
    }
}
