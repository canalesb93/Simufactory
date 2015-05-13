package itesm.mx.simufactory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class SummaryActivity extends MasterActivity implements View.OnClickListener {

    //View initiaizations
    Button btnFinish;
    TextView tvMoneyEarned;
    TextView tvMoneySpent;
    TextView tvTotalTime;
    ListView lvResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        //View initializations
        btnFinish = (Button) findViewById(R.id.btnFinish);
        tvMoneyEarned = (TextView) findViewById(R.id.tvMoneyEarned);
        tvMoneySpent = (TextView) findViewById(R.id.tvMoneySpent);
        tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
        lvResources = (ListView) findViewById(R.id.lvResources);

        //Resource adapter for resources ListView
        String titleString = null;
        final ResourceListAdapter resourcesAdapter = new ResourceListAdapter(this, allOperations, allOperationsAmount);

        Bundle extras = getIntent().getExtras();

        //Gets session title from Session to display it in the Summary Title
        if(extras != null){
            titleString = extras.getString("sessionTitle");
        }else{
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        tvMoneySpent.setText("$"+Integer.toString(g.getMoneySpent())+".00");

        //Gets Firebase data to get money values that have changed in session
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        // Updates money variable
        simulationRef.child("money").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvMoneyEarned.setText("$"+dataSnapshot.getValue().toString()+".00");
            }
            public void onCancelled(FirebaseError firebaseError) {}
        });

        // Updates time variable
        simulationRef.child("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long time = (Long) dataSnapshot.getValue() / 1000;
                tvTotalTime.setText(time+":00");
            }
            public void onCancelled(FirebaseError firebaseError) {}
        });

        //Populates operations list with their names and amounts
        simulationRef.child("operations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                allOperations.add((String) dataSnapshot.child("name").getValue());
                allOperationsAmount.add(Integer.parseInt(dataSnapshot.child("amount").getValue().toString()));
                resourcesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = Integer.parseInt(dataSnapshot.getKey().toString());
                allOperationsAmount.set(index, Integer.parseInt(dataSnapshot.child("amount").getValue().toString()));
                g.getSimulation().getOperations().get(index).setAmount(Integer.parseInt(dataSnapshot.child("amount").getValue().toString()));

                resourcesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        lvResources.setAdapter(resourcesAdapter);
        btnFinish.setOnClickListener(this);
    }

    //Prevents user from pressing back and going to previous activity
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Click finish to end session", Toast.LENGTH_SHORT).show();
    }

    //When "Finish" button is clicked, user is taken to MainActivity
    @Override
    public void onClick(View v) {
        if(btnFinish.isPressed()){
            Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
