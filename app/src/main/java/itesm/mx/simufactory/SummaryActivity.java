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
import android.widget.Toast;


public class SummaryActivity extends ActionBarActivity implements View.OnClickListener {

    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        btnFinish = (Button) findViewById(R.id.btnFinish);

        final ListView lvTeamSummary = (ListView) findViewById(R.id.lvTeamSummary);
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_row_summary, R.id.tv, sessions);
//        final ArrayAdapter<String> activeAdapter = new ArrayAdapter<String>(this, R.layout.activity_row, R.id.activeTV, actives);

        btnFinish.setOnClickListener(this);
    }




    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Can't go back while session is running", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(btnFinish.isPressed()){
            Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
