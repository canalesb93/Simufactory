package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SummaryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        final ListView lvTeamSummary = (ListView) findViewById(R.id.lvTeamSummary);
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_row_summary, R.id.tv, sessions);
//        final ArrayAdapter<String> activeAdapter = new ArrayAdapter<String>(this, R.layout.activity_row, R.id.activeTV, actives);
    }




    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Can't go back while session is running", Toast.LENGTH_SHORT).show();
    }
}
