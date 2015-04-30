package itesm.mx.simufactory;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class TeamActivity extends MasterActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        timerTextView = (TextView) findViewById(R.id.mainTimer);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        Bundle extras = getIntent().getExtras();

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase usersRef = ref.child("sessions/"+titleString+"/users");
        sessionRef = ref.child("sessions/"+titleString);
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        simulationRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot snapshot) {
                 endTime = (long) snapshot.child("time").getValue();
             }

            public void onCancelled(FirebaseError firebaseError) {}
         });

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



}
