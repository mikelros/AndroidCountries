package es.ithrek.syncadaptercurrencies.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.ithrek.syncadaptercurrencies.R;
import es.ithrek.syncadaptercurrencies.models.Currency;

public class AddActivity extends AppCompatActivity {

    private String contentUri = "content://es.ithrek.syncadaptercurrencies.sqlprovider";
    private EditText editTextName, editTextAbb, editTextValue;
    private Button button;
    private boolean isUpdating = false;
    private Currency currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAbb = (EditText) findViewById(R.id.editTextAbb);
        editTextValue = (EditText) findViewById(R.id.editTextValue);
        button = (Button) findViewById(R.id.button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long id = extras.getLong("_id");
            if (id != null) {
                Cursor cursor = getContentResolver().query(
                        Uri.parse(contentUri + "/currency/id"),   // The content URI
                        new String[]{"_id", "name", "value", "abbreviation", "id_backend"},
                        "",                        // The columns to return for each row
                        new String[]{String.valueOf(id)},                     // Selection criteria
                        ""
                );

                if (cursor.getCount() > 0) {
                    isUpdating = true;
                    currency = new Currency();
                    cursor.moveToFirst();
                    while (cursor.isAfterLast() == false) {
                        currency.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                        currency.setName(cursor.getString(cursor.getColumnIndex("name")));
                        currency.setValue(cursor.getInt(cursor.getColumnIndex("value")));
                        currency.setAbbreviation(cursor.getString(cursor.getColumnIndex("abbreviation")));
                        currency.setId_backend(cursor.getInt(cursor.getColumnIndex("id_backend")));

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
        Uri uri = Uri.parse(contentUri);

        contentValues.put("name", String.valueOf(editTextName.getText()));
        contentValues.put("abbreviation", String.valueOf(editTextAbb.getText()));
        contentValues.put("value", Integer.valueOf(String.valueOf(editTextValue.getText())));


        if (isUpdating) {
            contentValues.put("_id", currency.getId());
            contentValues.put("id_backend", currency.getId_backend());

            int rows = getContentResolver().update(
                    uri,
                    contentValues,
                    null,
                    null
            );
            Toast.makeText(AddActivity.this, "Updated", Toast.LENGTH_LONG).show();
            return;
        }

        contentValues.put("id_backend", 0);

        Uri resultUri = getContentResolver().insert(
                uri,   // The content URI
                contentValues
        );

        Toast.makeText(AddActivity.this, "Inserted", Toast.LENGTH_LONG).show();

    }
}
