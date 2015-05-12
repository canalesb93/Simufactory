package itesm.mx.simufactory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/*
Manages the resources that the user can buy with money.
Lists all buyable resources. Click to buy.
*/
public class StoreActivity extends ActionBarActivity {

    ListView lvResources;
    Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        lvResources = (ListView) findViewById(R.id.lvResourcesStore);

        final ArrayList<String> arrResources = new ArrayList<>();
        final ArrayList<Integer> arrIds = new ArrayList<>();
        final TextView budget = (TextView) findViewById(R.id.storeBudgetTV);

        String titleString = null;
        Boolean admin;

        Bundle extras = getIntent().getExtras();

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            admin = extras.getBoolean("admin");
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simufactory.firebaseio.com/");
        final Firebase simulationRef = ref.child("sessions/"+titleString+"/simulation");

        // Updates budget textView when money changes in Firebase.
        simulationRef.child("money").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                budget.setText("$"+dataSnapshot.getValue().toString()+".00");
            }
            public void onCancelled(FirebaseError firebaseError) {}
        });

        for(Operation o : g.getSimulation().getOperations()){
            if(o.getTime() == 0){
                arrResources.add(o.getName() + " - ($" + o.getCost() + ".00)");
                arrIds.add(o.getId());
            }
        }

        ArrayAdapter<String> adapterResources= new ArrayAdapter<String>(this, R.layout.activity_row_store, R.id.resourceNameTV, arrResources);
        lvResources.setAdapter(adapterResources);

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long arg3) {

                // Important money/budget transaction, updates all budget textViews
                simulationRef.runTransaction(new Transaction.Handler() {
                    boolean pass = false;

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        int operationId = arrIds.get(position);
                        Operation actualOperation = g.getSimulation().getOperations().get(operationId);
                        if( ((Long) currentData.child("money").getValue()).intValue() >= actualOperation.getCost() ){
                            g.setMoneySpent(g.getMoneySpent()+actualOperation.getCost());
                            currentData.child("money").setValue(((Long) currentData.child("money").getValue()) - actualOperation.getCost());
                            currentData.child("operations/"+operationId+"/amount").setValue(((Long) currentData.child("operations/"+operationId+"/amount").getValue()) + 1);
                            pass = true;
                        }

                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                        if(pass)
                            Toast.makeText(getApplicationContext(), "Purchased: " + arrResources.get(position), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        };
        lvResources.setOnItemClickListener(itemListener);

    }
}
