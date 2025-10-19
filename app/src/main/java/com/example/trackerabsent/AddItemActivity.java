package com.example.trackerabsent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    EditText editTextItem;
    Button btnCreate, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_work); // make sure this matches your XML filename

        editTextItem = findViewById(R.id.etWorkName);  // your input field ID
        btnCreate = findViewById(R.id.btnCreate);        // your “Create” button ID
        btnCancel = findViewById(R.id.btnCancel);        // your “Cancel” button ID

        // Create button logic
        btnCreate.setOnClickListener(v -> {
            String itemName = editTextItem.getText().toString().trim();

            if (!itemName.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("NEW_ITEM", itemName);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Cancel button logic
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
