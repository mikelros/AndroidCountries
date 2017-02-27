package es.ithrek.syncadaptercurrencies.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import es.ithrek.syncadaptercurrencies.Contract;
import es.ithrek.syncadaptercurrencies.R;
import es.ithrek.syncadaptercurrencies.Util;
import es.ithrek.syncadaptercurrencies.models.Currency;

public class AddActivity extends AppCompatActivity {

    private EditText editTextName, editTextAbb, editTextValue;
    private Button button;
    private boolean isUpdating = false;
    private Currency currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Util u = new Util();
        u.useStaticContext(getApplicationContext());

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAbb = (EditText) findViewById(R.id.editTextAbb);
        editTextValue = (EditText) findViewById(R.id.editTextValue);
        button = (Button) findViewById(R.id.button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long id = extras.getLong(Contract.CURRENCY_ID);
            if (id != null) {
                Log.d("DEBUG", "Id not null in add: " + id);
                Cursor cursor = getContentResolver().query(
                        Uri.parse(Contract.CONTENT_URI + "/" + Contract.PATH_ONE_CURRENCY),   // The content URI
                        new String[]{Contract.CURRENCY_ID, Contract.CURRENCY_NAME, Contract.CURRENCY_VALUE, Contract.CURRENCY_ABBREVIATION, Contract.CURRENCY_ID_BACKEND},
                        "",                        // The columns to return for each row
                        new String[]{String.valueOf(id)},                     // Selection criteria
                        ""
                );

                if (cursor.getCount() > 0) {
                    isUpdating = true;
                    currency = new Currency();
                    cursor.moveToFirst();
                    while (cursor.isAfterLast() == false) {
                        currency.setId(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID)));
                        currency.setName(cursor.getString(cursor.getColumnIndex(Contract.CURRENCY_NAME)));
                        currency.setValue(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_VALUE)));
                        currency.setAbbreviation(cursor.getString(cursor.getColumnIndex(Contract.CURRENCY_ABBREVIATION)));
                        currency.setId_backend(cursor.getInt(cursor.getColumnIndex(Contract.CURRENCY_ID_BACKEND)));

                        editTextName.setText(currency.getName());
                        editTextAbb.setText(currency.getAbbreviation());
                        editTextValue.setText(String.valueOf(currency.getValue()));
                        button.setText("Update currency");

                        cursor.moveToNext();
                    }
                }

            }
        }
    }

    public void addCurrency(View view) {
        ContentValues contentValues = new ContentValues();
        Uri uri = Uri.parse(Contract.CONTENT_URI);

        contentValues.put(Contract.CURRENCY_NAME, String.valueOf(editTextName.getText()));
        contentValues.put(Contract.CURRENCY_ABBREVIATION, String.valueOf(editTextAbb.getText()));
        contentValues.put(Contract.CURRENCY_VALUE, Integer.valueOf(String.valueOf(editTextValue.getText())));


        if (isUpdating) {
            contentValues.put(Contract.CURRENCY_ID, currency.getId());
            contentValues.put(Contract.CURRENCY_ID_BACKEND, currency.getId_backend());

            int rows = getContentResolver().update(
                    uri,
                    contentValues,
                    null,
                    null
            );
            Util.notify(view, "Updated", "Currency " + currency.getId() + " is updated");
            return;
        }

        contentValues.put(Contract.CURRENCY_ID_BACKEND, 0);

        Uri resultUri = getContentResolver().insert(
                uri,   // The content URI
                contentValues
        );

        Util.notify(view, "Inserted", "Currency " + String.valueOf(editTextName.getText()) + " is inserted");
    }
}
