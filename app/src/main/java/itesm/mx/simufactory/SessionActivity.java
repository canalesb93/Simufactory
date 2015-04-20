package itesm.mx.simufactory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


public class SessionActivity extends ActionBarActivity {
    final int REQUEST_CODE = 1;
    TextView titleTextView;
    String titleString = "No title";
    boolean admin = false;

    final ArrayList<String> users = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        final ListView usersListView = (ListView) findViewById(R.id.teamnamesListView);
        final Button startSession = (Button) findViewById(R.id.startSessionButton);


        titleTextView = (TextView) findViewById(R.id.titleSession);
        Bundle extras = getIntent().getExtras();

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");
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
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(FirebaseError firebaseError) {}
        });

//        Log.v("DEBUG test,", "testing"+ sessionRef.child("starter").)

        if(!admin) {
            sessionRef.child("started").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("CHANGED", "Session started: " + dataSnapshot.getValue().toString());
                    Intent intent = new Intent(SessionActivity.this, TeamActivity.class);
                    intent.putExtra("admin", false);
                    intent.putExtra("sessionTitle", titleString);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.v("CANCELLED", "Session started: " + firebaseError.getMessage());

                }
            });
        }

        usersListView.setAdapter(adapter);
        registerForContextMenu(usersListView);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
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


    public void createModel1(){
        ArrayList<Operation> operations = new ArrayList<Operation>();
        operations.add(new Operation("A", 5, 0, 0, null));
        operations.add(new Operation("B", 10, 0, 0, null));

        operations.add(new Operation("P1", 10, 0, 5000, new String[]{"A"}));
        operations.add(new Operation("P2", 20, 0, 7500, new String[]{"B"}));

        operations.add(new Operation("C", 10, 35, 10000, new String[]{"P1", "P2"}));

        Simulation simulation1 = new Simulation(100, 180000, operations, 3);
    }
}
