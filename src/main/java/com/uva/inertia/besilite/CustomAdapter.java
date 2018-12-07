package com.uva.inertia.besilite;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<CheckboxListViewItem>
{
    ArrayList<CheckboxListViewItem> modelItems = null;
    Context context;

    public CustomAdapter(Context context, ArrayList<CheckboxListViewItem> resource) {
        super(context,R.layout.listview_row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
        }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.listview_row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        name.setText(modelItems.get(position).getName());

        final int pos = position;
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelItems.get(pos).getValue() == 1)
                    modelItems.get(pos).setValue(0);
                else
                    modelItems.get(pos).setValue(1);
            }
        });

        if(modelItems.get(position).getValue() == 1)
            cb.setChecked(true);
        else
            cb.setChecked(false);

        return convertView;
        }
}