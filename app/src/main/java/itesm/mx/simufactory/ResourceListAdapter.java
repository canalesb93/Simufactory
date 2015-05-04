package itesm.mx.simufactory;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by roelcm, canalesb on 5/3/15.
 */

public class ResourceListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList resourceName;
    private final ArrayList amounts;

    public ResourceListAdapter(Activity context, ArrayList<String> resourceName, ArrayList<Integer> amounts) {
        super(context, R.layout.activity_row, resourceName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.resourceName=resourceName;
        this.amounts=amounts;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_row_resources, null,true);

        TextView nameTV = (TextView) rowView.findViewById(R.id.resourcesRowTV);
        TextView amountTV = (TextView) rowView.findViewById(R.id.resourcesAmountRowTV);

        if(amounts.size()>0) {
            nameTV.setText((String) resourceName.get(position));
            amountTV.setText(amounts.get(position).toString());
        }
//        if (amounts.get(position).equals("active")){
//            amountTV.setTextColor(Color.rgb(36, 152, 40));
//        } else {
//            amountTV.setTextColor(Color.RED);
//        }

        return rowView;
    };
}