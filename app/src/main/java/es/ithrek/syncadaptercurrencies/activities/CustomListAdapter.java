package es.ithrek.syncadaptercurrencies.activities;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import es.ithrek.syncadaptercurrencies.R;

/**
 * Created by Mikel on 13/02/17.
 */

public class CustomListAdapter extends CursorAdapter {

    private Activity activity;

    public CustomListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View v = view;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_row, null);

        }

        TextView textViewId = (TextView) v.findViewById(R.id.idIdentifier);
        textViewId.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));

        TextView textViewName = (TextView) v.findViewById(R.id.idName);
        textViewName.setText(cursor.getString(cursor.getColumnIndex("name")));

        TextView textViewAbb = (TextView) v.findViewById(R.id.idAbb);
        textViewAbb.setText(cursor.getString(cursor.getColumnIndex("abbreviation")));

        TextView textViewValue = (TextView) v.findViewById(R.id.idValue);
        textViewValue.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("value"))) + "€");
    }
}
