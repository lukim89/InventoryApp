package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static Activity detailsActivity;

    public String mPhoneNumber;

    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView mNameEditText;
    private TextView mPriceEditText;
    private TextView mQuantityEditText;
    private TextView mDescriptionEditText;
    private TextView mSupplierNameEditText;
    private TextView mSupplierPhoneEditText;

    private int mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsActivity = this;

        setContentView(R.layout.activity_details);
        setTitle(getString(R.string.details_activity_title));

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        mNameEditText = findViewById(R.id.product_name);
        mPriceEditText = findViewById(R.id.product_price);
        mQuantityEditText = findViewById(R.id.product_quantity);
        mDescriptionEditText = findViewById(R.id.product_description);
        mSupplierNameEditText = findViewById(R.id.product_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.product_supplier_phone);

        View phoneCallLayout = findViewById(R.id.phone_call_layout);
        phoneCallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(DetailsActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    String uri = "tel:" + mPhoneNumber.trim();
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse(uri));
                    startActivity(phoneIntent);
                }
            }
        });


        Button decreaseButton = findViewById(R.id.decrease_button);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDecrement();
            }
        });

        Button increaseButton = findViewById(R.id.increase_button);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onIncrement();
            }
        });
    }

    public void onIncrement() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, ++mQuantity);
        getContentResolver().update(mCurrentProductUri, values, null, null);
    }

    public void onDecrement() {
        if (mQuantity > 0) {
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, --mQuantity);
            getContentResolver().update(mCurrentProductUri, values, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_DESCRIPTION,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                mCurrentProductUri,
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
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            BigDecimal priceDecimal = new BigDecimal(price).movePointLeft(2);
            mPriceEditText.setText(NumberFormat.getCurrencyInstance().format(priceDecimal));

            mNameEditText.setText(name);
            mQuantityEditText.setText(quantity);
            mDescriptionEditText.setText(description);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);

            mPhoneNumber = supplierPhone;
            mQuantity = cursor.getInt(quantityColumnIndex);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}