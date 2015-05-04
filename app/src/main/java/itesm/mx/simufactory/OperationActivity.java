package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class OperationActivity extends ActionBarActivity {

    String currentOperation = "No name";
    Integer currentMachine = 0;
    Integer operationPosition = 0;

    final ArrayList<String> requiredResources= new ArrayList<String>();


    Globals g = Globals.getInstance();
    Operation actualOperation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        TextView titleTextView = (TextView) findViewById(R.id.operationNameTV);
        TextView timeTextView = (TextView) findViewById(R.id.tvTime);
        final EditText unitsToProduce = (EditText) findViewById(R.id.unitsET);
        final ListView requiredResourcesLV = (ListView) findViewById(R.id.requiredResourcesLV);

        Bundle extras = getIntent().getExtras();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(extras!= null){
            currentOperation = extras.getString("operationName");
            currentMachine = extras.getInt("selectedMachine");
            operationPosition = extras.getInt("operationPosition");
            titleTextView.setText("Operation " + currentOperation);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }

        actualOperation = g.getSimulation().getOperations().get(operationPosition);
        Log.v("TEST", actualOperation.getName());
        final ArrayAdapter<String> requiredResourcesAdapter = new ArrayAdapter<String>(this, R.layout.activity_row_machines, R.id.machineNameTV, requiredResources);
        for( int i : actualOperation.getRequires()){
            requiredResourcesAdapter.add(g.getSimulation().getOperations().get(i).getName());
        }
        requiredResourcesLV.setAdapter(requiredResourcesAdapter);


        Button startMachineButton = (Button) findViewById(R.id.startMachineButton);
        startMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pending verifications
                int amountToProduce = 0;
                amountToProduce = Integer.parseInt(unitsToProduce.getText().toString());
                long millis = System.currentTimeMillis() - g.getStartTime();
                Machine temp = g.getSimulation().getMachines().get(currentMachine);

                for (int i = 0; i < amountToProduce; i++) {
                    millis = millis + actualOperation.getTime();
                    Log.v("MILLIS", millis+" ");
                    temp.addTime(millis);
                }
            }
        });



    }

}
