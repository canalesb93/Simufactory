package itesm.mx.simufactory;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
                    if (dataSnapshot.getValue().toString() != "false") {

                        //pending selection
                        Globals g = Globals.getInstance();
                        switch (dataSnapshot.getValue().toString()){
                            case "1":
                                g.setSimulation(createModel2());
                                break;
                            default:
                                g.setSimulation(createModel1());
                                break;
                        }

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
                int modelType = spinner.getSelectedItemPosition();
                switch (modelType){
                    case 1:
                        mySimulation = createModel2();
                        break;
                    default:
                        mySimulation = createModel1();
                        break;
                }


                if(timerConfig.getText().length() > 0) {
                    mySimulation.setTime((Long.parseLong(timerConfig.getText().toString())) * 1000);
                }
                if (budgetConfig.getText().length() > 0) {
                    String cleanString = budgetConfig.getText().toString().replaceAll("[$,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    mySimulation.setMoney((int) parsed);

                }
                sessionRef.child("simulation").setValue(mySimulation);

                Globals g = Globals.getInstance();
                g.setSimulation(mySimulation);

                sessionRef.child("started").setValue(modelType+"");

                Intent intent = new Intent(SessionActivity.this, TeamActivity.class);
                intent.putExtra("admin", true);
                intent.putExtra("sessionTitle", titleString);


                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "No admin priviledges", Toast.LENGTH_SHORT).show();
            }
            }
        });

        budgetConfig.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    budgetConfig.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");
                    int parsed;
                    if (cleanString.equals("")){
                        parsed = 0;
                    } else {
                        parsed = Integer.parseInt(cleanString);
                    }
                    String formatted = String.format("%,d", parsed);
                    formatted = '$' + formatted;
                    current = formatted;
                    budgetConfig.setText(formatted);
                    budgetConfig.setSelection(formatted.length());

                    budgetConfig.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        expensesConfig.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    expensesConfig.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");


                    int parsed;
                    if (cleanString.equals("")){
                        parsed = 0;
                    } else {
                        parsed = Integer.parseInt(cleanString);
                    }
                    String formatted = String.format("%,d", parsed);
                    formatted = '$' + formatted;


                    current = formatted;
                    expensesConfig.setText(formatted);
                    expensesConfig.setSelection(formatted.length());

                    expensesConfig.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public Simulation createModel1(){
        ArrayList<Operation> operations = new ArrayList<Operation>();
        ArrayList<Machine> machines = new ArrayList<Machine>();

        /// RESOURCE, ID, COST, GAIN, TIME, TEAMID, REQUIRED, AMOUNT ( owned )
        operations.add(new Operation("Sand", 0, 5, 0, 0, 0, null, 3));
        operations.add(new Operation("Steel", 1, 10, 0, 0, 0, null, 3));

        operations.add(new Operation("Glass", 2, 10, 0, 5000, 1, new ArrayList<Integer>(Arrays.asList(0)), 0));
        operations.add(new Operation("Frame", 3, 20, 0, 7000, 2, new ArrayList<Integer>(Arrays.asList(1)), 0));

        operations.add(new Operation("Window", 4, 10, 100, 10000, 3, new ArrayList<Integer>(Arrays.asList(2, 3)), 0));

        machines.add(new Machine("Red1", 1));
        machines.add(new Machine("Red2", 1));
        machines.add(new Machine("Green1", 2));
        machines.add(new Machine("Green2", 2));
        machines.add(new Machine("Blue1", 3));

        //CASH, TIME, OPERATIONS, MACHINES, NUM OF TEAMS
        Simulation simulation1 = new Simulation(100, 120000, operations, machines, 3);
        this.simu = simulation1;
        return simulation1;
    }

    public Simulation createModel2(){
        ArrayList<Operation> operations = new ArrayList<Operation>();
        ArrayList<Machine> machines = new ArrayList<Machine>();

        /// RESOURCE, ID, COST, GAIN, TIME, TEAMID, REQUIRED, AMOUNT ( owned )
        operations.add(new Operation("A", 0, 30, 0, 0, 0, null, 3));
        operations.add(new Operation("C", 1, 35, 0, 0, 0, null, 3));
        operations.add(new Operation("E", 2, 30, 0, 0, 0, null, 3));
        operations.add(new Operation("F", 3, 60, 0, 0, 0, null, 3));

        /// RESOURCE, ID, COST, GAIN, TIME, TEAMID, REQUIRED, AMOUNT ( owned )
        operations.add(new Operation("A1", 4, 0, 0, 4000, 2, new ArrayList<Integer>(Arrays.asList(0)), 0));
        operations.add(new Operation("C1", 5, 0, 0, 5000, 2, new ArrayList<Integer>(Arrays.asList(1)), 0));
        operations.add(new Operation("E1", 6, 0, 0, 9000, 3, new ArrayList<Integer>(Arrays.asList(2)), 0));
        operations.add(new Operation("F1", 7, 0, 0, 15000, 2, new ArrayList<Integer>(Arrays.asList(3)), 0));

        operations.add(new Operation("E2", 8, 0, 0, 18000, 4, new ArrayList<Integer>(Arrays.asList(6)), 0));
        operations.add(new Operation("F1", 9, 0, 0, 12000, 3, new ArrayList<Integer>(Arrays.asList(7)), 0));

        operations.add(new Operation("B3", 10, 0, 0, 8000, 5, new ArrayList<Integer>(Arrays.asList(4, 5)), 0));
        operations.add(new Operation("F3", 11, 0, 0, 20000, 4, new ArrayList<Integer>(Arrays.asList(9)), 0));

        operations.add(new Operation("A5", 12, 0, 0, 15000, 2, new ArrayList<Integer>(Arrays.asList(10)), 0));
        operations.add(new Operation("C5", 13, 0, 0, 6000, 1, new ArrayList<Integer>(Arrays.asList(10)), 0));
        operations.add(new Operation("E5", 14, 0, 0, 28000, 1, new ArrayList<Integer>(Arrays.asList(2)), 0));
        operations.add(new Operation("F5", 15, 0, 0, 14000, 1, new ArrayList<Integer>(Arrays.asList(3)), 0));

        operations.add(new Operation("A6", 16, 0, 0, 15000, 3, new ArrayList<Integer>(Arrays.asList(12)), 0));

        operations.add(new Operation("A7", 17, 0, 0, 20000, 4, new ArrayList<Integer>(Arrays.asList(16)), 0));
        operations.add(new Operation("D7", 18, 0, 0, 9000, 5, new ArrayList<Integer>(Arrays.asList(13, 14)), 0));
        operations.add(new Operation("F7", 19, 0, 0, 7000, 4, new ArrayList<Integer>(Arrays.asList(15)), 0));

        operations.add(new Operation("A9 Product", 20, 0, 180, 18000, 3, new ArrayList<Integer>(Arrays.asList(17)), 0));
        operations.add(new Operation("D9 Product", 21, 0, 240, 6000, 3, new ArrayList<Integer>(Arrays.asList(18)), 0));
        operations.add(new Operation("F9 Product", 22, 0, 180, 10000, 3, new ArrayList<Integer>(Arrays.asList(19)), 0));

        machines.add(new Machine("Blue1", 1));
        machines.add(new Machine("Green1", 2));
        machines.add(new Machine("Green2", 2));
        machines.add(new Machine("Aqua1", 3));
        machines.add(new Machine("Aqua2", 3));
        machines.add(new Machine("Purple1", 4));
        machines.add(new Machine("Brown1", 5));

        //CASH, TIME, OPERATIONS, MACHINES, NUM OF TEAMS
        Simulation simulation1 = new Simulation(2500, 600000, operations, machines, 3);
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
