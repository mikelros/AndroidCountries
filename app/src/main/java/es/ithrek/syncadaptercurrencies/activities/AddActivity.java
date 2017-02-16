package es.ithrek.syncadaptercurrencies.activities;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import es.ithrek.syncadaptercurrencies.R;

public class AddActivity extends AppCompatActivity {

    private String contentUri = "content://es.ithrek.syncadaptercurrencies.sqlprovider";
    private EditText editTextName, editTextAbb, editTextValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAbb = (EditText) findViewById(R.id.editTextAbb);
        editTextValue = (EditText) findViewById(R.id.editTextValue);
    }

    public void addCurrency(View view) {
        Uri uri = Uri.parse(contentUri);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", String.valueOf(editTextName.getText()));
        contentValues.put("abbreviation", String.valueOf(editTextAbb.getText()));
        contentValues.put("value", Integer.valueOf(String.valueOf(editTextValue.getText())));
        contentValues.put("id_backend", 0);


        // We finally make the request to the content provider
        Uri resultUri = getContentResolver().insert(
                uri,   // The content URI
                contentValues
        );
    }
}
