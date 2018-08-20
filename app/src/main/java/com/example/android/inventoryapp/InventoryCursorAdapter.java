package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        Button saleButton = view.findViewById(R.id.sale_button);

        TextView nameTextView = view.findViewById(R.id.item_name_text_view);
        TextView priceTextView = view.findViewById(R.id.item_price_text_view);
        final TextView quantityTextView = view.findViewById(R.id.item_quantity_text_view);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(name);

        if (price == 0) {
            String noPrice = context.getString(R.string.no_price);
            priceTextView.setText(noPrice);
        } else {
            BigDecimal priceDecimal = new BigDecimal(price).movePointLeft(2);
            priceTextView.setText(NumberFormat.getCurrencyInstance().format(priceDecimal));
        }

        quantityTextView.setText(String.valueOf(quantity));

        final int productId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productId);

        if (quantity > 0) {
            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    int mQuantity = quantity;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, --mQuantity);

                    context.getContentResolver().update(currentProductUri, values, null, null);
                }
            });
        } else {
            quantityTextView.setText(context.getString(R.string.out_of_stock));
        }
    }
}