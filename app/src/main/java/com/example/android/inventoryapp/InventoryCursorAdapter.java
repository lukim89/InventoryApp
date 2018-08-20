package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class InventoryCursorAdapter extends CursorAdapter {

    InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.item_name_text_view);
        TextView priceTextView = view.findViewById(R.id.item_price_text_view);
        TextView quantityTextView = view.findViewById(R.id.item_quantity_text_view);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        String priceString = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        int price = Integer.parseInt(priceString);
        if (price == 0) {
            priceString = context.getString(R.string.no_price);
            priceTextView.setText(priceString);
        } else {
            BigDecimal priceDecimal = new BigDecimal(price).movePointLeft(2);
            priceTextView.setText(NumberFormat.getCurrencyInstance().format(priceDecimal));
        }

        if (Integer.parseInt(quantity) == 0) {
            quantity = context.getString(R.string.out_of_stock);
        }

        nameTextView.setText(name);
        quantityTextView.setText(quantity);
    }
}