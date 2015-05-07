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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    String validPassword;
    boolean pass = false;

    static final int REQUEST_CODE_SESSION = 1;
    String titleString;
    String pressedSession;
    String pressedPassword;
    final ArrayList<String> sessions = new ArrayList<String>();
    final ArrayList<String> actives = new ArrayList<String>();
    final ArrayList<String> passwords = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG = "SDF";
        final ListView sessionList = (ListView) findViewById(R.id.sessionListView);

        ////// ADAPTER
        final SessionListAdapter activeAdapter = new SessionListAdapter(this, sessions, actives);

        final EditText name = (EditText) findViewById(R.id.sessionName);
        final EditText password = (EditText) findViewById(R.id.sessionPassword);
        final Button createSession = (Button) findViewById(R.id.newSessionButton);

        final LoginSessionDialog adLoginSession = new LoginSessionDialog();

        //Crear sesion en Firebase
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase sessionsRef = ref.child("sessions");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Log.d("TAG", "AddListener!");

        sessionsRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                //Toast.makeText(getApplicationContext(), "Session added to Firebase.", Toast.LENGTH_SHORT).show();
                sessions.add((String) snapshot.child("name").getValue());
                passwords.add((String) snapshot.child("password").getValue());
                if((boolean) snapshot.child("active").getValue()){
                    actives.add("active");
                } else {
                   actives.add("inactive");
                }
                activeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Toast.makeText(getApplicationContext(), "Session changed.", Toast.LENGTH_SHORT).show();
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

        });

        sessionList.setAdapter(activeAdapter);

        registerForContextMenu(sessionList);

        createSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(name.getText().length() > 0){
                    Session mySession = new Session(name.getText().toString(), password.getText().toString());
//                    Map<String, Session> mysessions = new HashMap<String, Session>();
//                    mysessions.put(name.getText().toString(), mySession);
                    sessionsRef.child(name.getText().toString()).setValue(mySession);

                    Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                    intent.putExtra("sessionTitle", name.getText().toString());
                    intent.putExtra("admin", true);
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
                pressedSession = sessions.get(position);
                pressedPassword = passwords.get(position);
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //Get layout inflater
//            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_login, null);
            builder.setView(view);
                    //.setMessage("Ingresa contraseña para ingresar")
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // OK was pressed
                            final EditText userPassword = (EditText) view.findViewById(R.id.password);
                            final EditText userName = (EditText) view.findViewById(R.id.username);

                            validPassword = pressedPassword;

                            if (validPassword.equals("") || userPassword.getText().toString().equals(validPassword)) {

                                final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
                                final Firebase usersRef = ref.child("sessions/"+pressedSession+"/users");

                                User myuser = new User(userName.getText().toString());
//                                Map<String, User> myusers = new HashMap<String, User>();
//                                myusers.put(userName.getText().toString(), myuser);
                                usersRef.child(userName.getText().toString()).setValue(myuser);

                                Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                                intent.putExtra("admin", false);
                                intent.putExtra("sessionTitle", pressedSession);
                                intent.putExtra("name", userName.getText().toString());

                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
