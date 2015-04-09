package itesm.mx.simufactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    static final int REQUEST_CODE_SESSION = 1;
    String titleString;
    final ArrayList<String> sessions = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG = "SDF";
        final ListView sessionList = (ListView) findViewById(R.id.sessionListView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_row, R.id.rowTV, sessions);

        final EditText name = (EditText) findViewById(R.id.sessionName);
        final EditText password = (EditText) findViewById(R.id.sessionPassword);
        final Button createSession = (Button) findViewById(R.id.newSessionButton);

        final LoginSessionDialog adLoginSession = new LoginSessionDialog();

        //Crear sesion en Firebase
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase sessionsRef = ref.child("sessions");

        sessionsRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                Toast.makeText(getApplicationContext(), "Session added to Firebase.", Toast.LENGTH_SHORT).show();
//                Session mySession = (Session) snapshot.getValue();

                sessions.add((String) snapshot.child("name").getValue());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getApplicationContext(), "Session changed.", Toast.LENGTH_SHORT).show();
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

        sessionList.setAdapter(adapter);
        registerForContextMenu(sessionList);

        createSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(name.getText().length() > 0){
                    Session mySession = new Session(name.getText().toString(), password.getText().toString());
                    sessionsRef.push().setValue(mySession);

                    Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                    intent.putExtra("sessionTitle", name.getText().toString());
                    Toast.makeText(getApplicationContext(), "Session created.", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, REQUEST_CODE_SESSION);
                } else {
                    Toast.makeText(getApplicationContext(), "Name is required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adLoginSession.show(getFragmentManager(), TAG );
            }
        };
        sessionList.setOnItemClickListener(itemListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SESSION && resultCode == RESULT_OK){
            titleString = (String) data.getExtras().get("sessionTitle");
        }
    }

    public class LoginSessionDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //Get layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_login,null))

                    //.setMessage("Ingresa contraseña para ingresar")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // OK was pressed
                            //Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                            //intent.putExtra("sessionTitle", sessions.get(position));
                            //startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
