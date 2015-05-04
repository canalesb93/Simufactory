package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class OperationActivity extends MasterActivity {

    String currentOperation = "No name";
    String currentMachine = "No machine";
    int operationPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        TextView titleTextView = (TextView) findViewById(R.id.operationNameTV);
        TextView timeTextView = (TextView) findViewById(R.id.tvTime);

        Bundle extras = getIntent().getExtras();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(extras!= null){
            currentOperation = extras.getString("operationName");
            currentMachine = extras.getString("operationName");
            operationPosition = extras.getInt("operationPosition");
            titleTextView.setText("Operation " + currentOperation);
            Log.v("TEXT: ", allOperationsAmount.get(0).toString());
            //timeTextView.setText(allOperationsTime.get(operationPosition));
        } else {
            Toast.makeText(getApplicationContext(), "ERROR.", Toast.LENGTH_SHORT).show();
        }




    }

}
