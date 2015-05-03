package itesm.mx.simufactory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;


public class TeamActivity extends MasterActivity {

    final ArrayList<String> operations = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        timerTextView = (TextView) findViewById(R.id.mainTimer);
        TextView teamName = (TextView) findViewById(R.id.teamNameTV);
        final ListView operationLV = (ListView) findViewById(R.id.operationsLV);

        startTime = System.currentTimeMillis();
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

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase userRef = ref.child("sessions/"+titleString+"/users/"+userName);
        sessionRef = ref.child("sessions/"+titleString);
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        simulationRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot snapshot) {
                 endTime = (long) snapshot.child("time").getValue();
             }

             public void onCancelled(FirebaseError firebaseError) {}
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child("teamId").getValue() != null)
                    teamId = (int) snapshot.child("teamId").getValue();
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });



        final ArrayAdapter<String> operationsAdapter = new ArrayAdapter<String>(this, R.layout.activity_row, R.id.rowTV, operations);

        simulationRef.child("operations").addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                if((long) snapshot.child("team").getValue() == teamId || teamId == 0) {
                    operations.add((String) snapshot.child("name").getValue());
                    operationsAdapter.notifyDataSetChanged();
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(FirebaseError firebaseError) {}
        });

        operationLV.setAdapter(operationsAdapter);
        registerForContextMenu(operationLV);

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pressedOperation = operations.get(position);

                Intent intent = new Intent(TeamActivity.this, OperationActivity.class);
                intent.putExtra("operationName", pressedOperation);

                startActivity(intent);
            }
        };
        operationLV.setOnItemClickListener(itemListener);




//        Button b = (Button) findViewById(R.id.button);
//        b.setText("start");
//        b.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Button b = (Button) v;
//                if (b.getText().equals("stop")) {
//                    timerHandler.removeCallbacks(timerRunnable);
//                    b.setText("start");
//                } else {
//                    startTime = System.currentTimeMillis();
//                    timerHandler.postDelayed(timerRunnable, 0);
//                    b.setText("stop");
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Can't go back while session is running", Toast.LENGTH_SHORT).show();
    }



}
