package itesm.mx.simufactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by roelcm on 5/3/15.
 */

public class SessionListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList sessionName;
    private final ArrayList active;

    public SessionListAdapter(Activity context, ArrayList<String> sessionName, ArrayList<String> active) {
        super(context, R.layout.activity_row, sessionName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.sessionName=sessionName;
        this.active=active;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_row, null,true);

        TextView nameTV = (TextView) rowView.findViewById(R.id.rowTV);
        TextView activeTV = (TextView) rowView.findViewById(R.id.activeTV);

        nameTV.setText((String)sessionName.get(position));
        activeTV.setText((String) active.get(position));
        if (active.get(position).equals("active")){
            activeTV.setTextColor(Color.rgb(36, 152, 40));
        } else {
            activeTV.setTextColor(Color.RED);
        }
        return rowView;
    };
}