package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class SessionActivity extends ActionBarActivity {
    final int REQUEST_CODE = 1;
    TextView titleTextView;
    String titleString;

    final ArrayList<String> users = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        final ListView usersListView = (ListView) findViewById(R.id.teamnamesListView);

        titleTextView = (TextView) findViewById(R.id.titleSession);
        Bundle extras = getIntent().getExtras();

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            titleTextView.setText(titleString);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase usersRef = ref.child("sessions/"+titleString+"/users");

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

        usersListView.setAdapter(adapter);
        registerForContextMenu(usersListView);


        ArrayList<Operation> operations = new ArrayList<Operation>();
//        operations.add(new Operation("A", "P1", ))

    }


}
