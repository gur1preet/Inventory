package com.example.android.inventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory.DataBase.InventoryContract;

import java.io.ByteArrayOutputStream;

import static com.example.android.inventory.R.id.activity_chooser_view_content;
import static com.example.android.inventory.R.id.edit_name;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FILE_SELECT_CODE = 2;

    Boolean infoItemHasChanged = false;

    private static final int EXISTING_PET_LOADER = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;


    private Uri mCurrentItemUri;
    Uri actualUri;

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mAvailableQuantityEditText;
    private EditText mOrderedQuantityEditText;
    private EditText mSupplierName;
    private String image;
    private EditText mPrice;
    private ImageView mimageView;
    private ImageView mimageBtn;

    private boolean mItemHasChanged = false;

    private static final int PICK_IMAGE_REQUEST = 1;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mimageBtn=(ImageView)findViewById(R.id.add_image_button);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        mimageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();
                infoItemHasChanged = true;
            }
        });

        if (mCurrentItemUri == null) {
            setTitle("Add Item");
            invalidateOptionsMenu();
        } else {
            setTitle("Update Item");
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        mNameEditText = (EditText)findViewById(edit_name);
        mDescriptionEditText = (EditText)findViewById(R.id.edit_description);
        mAvailableQuantityEditText = (EditText)findViewById(R.id.edit_quantity);
        mOrderedQuantityEditText = (EditText)findViewById(R.id.edit_available_quantity);
        mPrice = (EditText)findViewById(R.id.price_edit_view);
        mSupplierName = (EditText)findViewById(R.id.supplier_name_edit_text);
        mimageView= (ImageView)findViewById(R.id.image_view);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mAvailableQuantityEditText.setOnTouchListener(mTouchListener);
        mOrderedQuantityEditText.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mimageView.setOnTouchListener(mTouchListener);
    }


    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }
    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_order_more:
                orderMore();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(descriptionString) ||
                TextUtils.isEmpty(availableQuantity) || TextUtils.isEmpty(orderedQuantity) ||
                TextUtils.isEmpty(price) || TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, "Please fill all the entries.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(actualUri == null){
            Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show();
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
        values.put(InventoryContract.InventoryEntry.COLUMN_IMAGE,actualUri.toString());

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
        finish();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("All items will be lost");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_DESCRIPTION,
                InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_IMAGE};

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_DESCRIPTION);
            int orderedQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY);
            int availableQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int orderedQuantity = cursor.getInt(orderedQuantityColumnIndex);
            int availableQuantity = cursor.getInt(availableQuantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            actualUri = Uri.parse(cursor.getString(imageColumnIndex));

            mNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            mOrderedQuantityEditText.setText(Integer.toString(orderedQuantity));
            mAvailableQuantityEditText.setText(Integer.toString(availableQuantity));
            mPrice.setText(Integer.toString(price));
            mSupplierName.setText(supplierName);
            mimageView.setImageURI(Uri.parse(cursor.getString(imageColumnIndex)));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mOrderedQuantityEditText.setText("0");
        mAvailableQuantityEditText.setSelection(0);
        mPrice.setText("0");
        mSupplierName.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.insertion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertion_succesful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                actualUri = resultData.getData();
                mimageView.setImageURI(actualUri);
                mimageView.invalidate();
            }
        }
    }

    public void orderMore(){
        String name = mNameEditText.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ordering more of " + name);
        intent.putExtra(Intent.EXTRA_TEXT, "Hello");

        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivity(intent);
        }
    }
}

