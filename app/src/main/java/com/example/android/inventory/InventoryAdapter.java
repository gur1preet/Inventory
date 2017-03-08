package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        ImageView imgView=(ImageView)view.findViewById(R.id.list_image);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int imgColumnIndex=cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE);

        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        imgView.setImageURI(Uri.parse(cursor.getString(imgColumnIndex)));

        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
    }
}
