package itesm.mx.simufactory;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TeamActivity extends MasterActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);


        timerTextView = (TextView) findViewById(R.id.mainTimer);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
//        Button b = (Button) findViewById(R.id.button);
//        b.setText("start");
//        b.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Button b = (Button) v;
//                if (b.getText().equals("stop")) {
//                    timerHandler.removeCallbacks(timerRunnable);
//                    b.setText("start");
//                } else {
//                    startTime = System.currentTimeMillis();
//                    timerHandler.postDelayed(timerRunnable, 0);
//                    b.setText("stop");
//                }
//            }
//        });
    }

}
