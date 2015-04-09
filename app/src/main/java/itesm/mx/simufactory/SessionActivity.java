package itesm.mx.simufactory;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SessionActivity extends ActionBarActivity {
    final int REQUEST_CODE = 1;
    TextView titleTextView;
    String titleString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        titleTextView = (TextView) findViewById(R.id.titleSession);
        Bundle extras = getIntent().getExtras();

        if(extras!= null){
            titleString = extras.getString("sessionTitle");
            titleTextView.setText(titleString);
        }
    }
}
