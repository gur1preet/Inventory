package com.example.android.inventory;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import static com.example.android.inventory.R.id.edit_name;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mAvailableQuantityEditText;
    private EditText mOrderedQuantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText)findViewById(edit_name);
        mDescriptionEditText = (EditText)findViewById(R.id.edit_description);
        mAvailableQuantityEditText = (EditText)findViewById(R.id.edit_quantity);
        mOrderedQuantityEditText = (EditText)findViewById(R.id.edit_available_quantity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                return true;
            case R.id.action_delete:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

