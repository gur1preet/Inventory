package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.DataBase.InventoryContract;

/**
 * Created by DELL on 07-03-2017.
 */

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context,Cursor cursor){
        super(context,cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView)view.findViewById(R.id.quantity_text_view);
        ImageView imgView=(ImageView)view.findViewById(R.id.list_image);

        int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int availableQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY);
        int orderedQuantityCloumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int imgColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE);

        final long id = cursor.getLong(idColumnIndex);
        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final int availableQuantity = cursor.getInt(availableQuantityColumnIndex);
        final int orderedQuantity = cursor.getInt(orderedQuantityCloumnIndex);
        imgView.setImageURI(Uri.parse(cursor.getString(imgColumnIndex)));

        ImageView sellButton = (ImageView) view.findViewById(R.id.sale_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (availableQuantity > 0) {

                    ContentValues values = new ContentValues();

                    int newAvailableQuantity = availableQuantity - 1;
                    int newOrderedQuantity = orderedQuantity + 1;

                    Log.v("new quantity", "after click" + newAvailableQuantity);

                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY, newAvailableQuantity);
                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY, newOrderedQuantity);

                    Uri uri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                    view.getContext().getContentResolver().update(uri, values, null, null);
                } else {
                    Toast.makeText(view.getContext(),"No more available Items",Toast.LENGTH_SHORT).show();
                }
            }
        });

        String name = "Name: " + itemName;
        String price = "Price: " + itemPrice;
        String quantity = "Quantity: " + availableQuantity;

        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);
    }
}
