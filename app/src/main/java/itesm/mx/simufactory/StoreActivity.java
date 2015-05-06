package itesm.mx.simufactory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Enrique on 5/5/15.
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
        ArrayList<Integer> arrIds = new ArrayList<>();

        for(Operation o : g.getSimulation().getOperations()){
            if(o.getTime() == 0){
                arrResources.add(o.getName());
                arrIds.add(o.getId());
            }
        }

        ArrayAdapter<String> adapterResources= new ArrayAdapter<String>(this, R.layout.activity_row_store, R.id.resourceNameTV, arrResources);
        lvResources.setAdapter(adapterResources);

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

                Toast.makeText(getApplicationContext(), "Purchased: " + arrResources.get(position), Toast.LENGTH_SHORT).show();

            }
        };
        lvResources.setOnItemClickListener(itemListener);

    }
}
