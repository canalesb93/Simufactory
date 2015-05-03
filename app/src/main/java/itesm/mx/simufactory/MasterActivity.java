package itesm.mx.simufactory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.Firebase;


public abstract class MasterActivity extends ActionBarActivity {

    TextView timerTextView;
    long startTime = 0;
    long endTime = 15000;

    String titleString = "No title";
    String userName = "Admin";
    boolean admin = false;
    boolean first = false;
    Simulation mySimulation;

    Firebase sessionRef = null;

    int teamId = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if(timerTextView != null) {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                if(millis > endTime && !first){
                    first = true;
                    timerHandler.removeCallbacks(timerRunnable);
                    Intent intent = new Intent(MasterActivity.this, SummaryActivity.class);
                    intent.putExtra("admin", admin);
                    intent.putExtra("name", userName);
                    intent.putExtra("sessionTitle", titleString);
                    if(admin){
                        sessionRef.child("active").setValue(false);
                    }
                    startActivity(intent);
                    return;
                }
                seconds = seconds % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

            }

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    public void onFinish(){

    }

    @Override
    public void onPause() {
        super.onPause();
//        timerHandler.removeCallbacks(timerRunnable);
    }
}
