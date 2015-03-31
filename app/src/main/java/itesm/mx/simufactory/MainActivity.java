package itesm.mx.simufactory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase sessionsRef = ref.child("sessions");

        final ListView sessionList = (ListView) findViewById(R.id.sessionListView);
        final ArrayList<String> sessions = new ArrayList<String>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_row, R.id.rowTV, sessions);

        sessionList.setAdapter(adapter);
        registerForContextMenu(sessionList);

        sessionsRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                Toast.makeText(getApplicationContext(), "New session created.", Toast.LENGTH_LONG).show();
//                Session mySession = (Session) snapshot.getValue();

                sessions.add((String) snapshot.child("name").getValue());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getApplicationContext(), "Session changed.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

            //... ChildEventListener also defines onChildChanged, onChildRemoved,
            //    onChildMoved and onCanceled, covered in later sections.
        });



//        String[] sessions = new String[]{};



        final EditText name = (EditText) findViewById(R.id.sessionName);
        final EditText password = (EditText) findViewById(R.id.sessionPassword);
        final Button createSession = (Button) findViewById(R.id.newSessionButton);

        createSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(name.getText().length() > 0){
                    Session mySession = new Session(name.getText().toString(), password.getText().toString());

//                    Map<String, Session> users = new HashMap<String, Session>();
//                    users.put("alanisawesome", alanisawesome);
//                    users.put("gracehop", gracehop);

                    sessionsRef.push().setValue(mySession);

                    Toast.makeText(getApplicationContext(), "Session created.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Name is required.", Toast.LENGTH_LONG).show();
                }
            }


        });

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
