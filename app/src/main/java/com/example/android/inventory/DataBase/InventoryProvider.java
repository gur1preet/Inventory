package com.example.android.inventory.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.inventory.DataBase.InventoryContract.InventoryEntry.TABLE_NAME;

/**
 * Created by DELL on 06-03-2017.
 */

public class InventoryProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private InventoryDBHelper mDbHelper;

    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.TABLE_NAME,ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.TABLE_NAME + "/#",ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+ uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {

        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Enter valid name of Item.");
        }

        Integer available_quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY);
        if (available_quantity == null && available_quantity < 0) {
            throw new IllegalArgumentException("Item quantity should be greater than zero");
        }

        Integer ordered_quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY);
        if (ordered_quantity != null && ordered_quantity < 0) {
            throw new IllegalArgumentException("Ordered Items should be greater than zero.");
        }

        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Enter a valid price");
        }

        String supplierName = values.getAsString(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Enter valid supplier name.");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item needs a name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY)) {
            Integer avaialableQuantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_AVAILABLE_QUANTITY);
            if (avaialableQuantity != null && avaialableQuantity < 0) {
                throw new IllegalArgumentException("Enter valid quantity to order");
            }
            return 0;
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY)) {
            Integer orderedQuantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_ORDERED_QUANTITY);
            if (orderedQuantity != null && orderedQuantity < 0) {
                throw new IllegalArgumentException("Enter valid quantity to order");
            }
            return 0;
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE)) {
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Enter valid amount for price");
            }
            return 0;
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
