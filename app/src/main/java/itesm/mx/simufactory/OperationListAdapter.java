package itesm.mx.simufactory;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Enrique on 5/5/15.
 */
public class OperationListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList operationName;
    private final ArrayList progress;

    public OperationListAdapter(Activity context, ArrayList<String> operationName, ArrayList<Integer> progress) {
        super(context, R.layout.activity_row_operations, operationName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.operationName=operationName;
        this.progress=progress;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_row_operations, null,true);

        TextView nameTV = (TextView) rowView.findViewById(R.id.operationNameTV);
        ProgressBar pbProgressBar = (ProgressBar) rowView.findViewById(R.id.operationPB);

        nameTV.setText((String)operationName.get(position));
        pbProgressBar.setProgress((Integer)progress.get(position));

        //showprogress

        return rowView;
    };


}
