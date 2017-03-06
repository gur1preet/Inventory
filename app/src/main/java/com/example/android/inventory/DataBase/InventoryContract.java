package com.example.android.inventory.DataBase;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DELL on 03-03-2017.
 */

public final class InventoryContract {

    public InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns{
        public final static String TABLE_NAME = "items";
        public  final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_DESCRIPTION = "description";
        public final static String COLUMN_ITEM_AVAILABLE_QUANTITY = "available quantity";
        public final static String COLUMN_ITEM_ORDERED_QUANTITY = "ordered quantity";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_SUPPLIER_NAME = "name";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
    }
}
