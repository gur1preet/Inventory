package com.example.android.inventory;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.DataBase.InventoryContract;

import static com.example.android.inventory.R.id.edit_name;

public class EditorActivity extends AppCompatActivity {

    private Uri mCurrentItemUri;

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mAvailableQuantityEditText;
    private EditText mOrderedQuantityEditText;
    private EditText mSupplierName;
    private EditText mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText)findViewById(edit_name);
        mDescriptionEditText = (EditText)findViewById(R.id.edit_description);
        mAvailableQuantityEditText = (EditText)findViewById(R.id.edit_quantity);
        mOrderedQuantityEditText = (EditText)findViewById(R.id.edit_available_quantity);
        mPrice = (EditText)findViewById(R.id.price_edit_view);
        mSupplierName = (EditText)findViewById(R.id.supplier_name_edit_text);


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
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String availableQuantity = mAvailableQuantityEditText.getText().toString().trim();
        String orderedQuantity  = mOrderedQuantityEditText.getText().toString().trim();
        String price = mPrice.getText().toString().trim();
        String supplierName = mSupplierName.getText().toString().trim();

        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(descriptionString) &&
                TextUtils.isEmpty(availableQuantity) && TextUtils.isEmpty(orderedQuantity) &&
                TextUtils.isEmpty(price) && TextUtils.isEmpty(supplierName)) {

            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, nameString);

        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_DESCRIPTION, descriptionString);

        int intAvailableQuantity = 0;
        if (!TextUtils.isEmpty(availableQuantity)) {
            intAvailableQuantity = Integer.parseInt(availableQuantity);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY, intAvailableQuantity);

        int intOrderedQuantity = 0;
        if(!TextUtils.isEmpty(orderedQuantity)){
            intOrderedQuantity = Integer.parseInt(orderedQuantity);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY, intOrderedQuantity);

        int intPrice = 0;
        if (!TextUtils.isEmpty(price)){
            intPrice = Integer.parseInt(price);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, intPrice);

        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);

        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insertion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertion_succesful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.insertion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertion_succesful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

