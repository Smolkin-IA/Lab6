package com.example.lab6;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView userList;
    Spinner spinner;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor productCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        userList = findViewById(R.id.list);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String catName = spinner.getSelectedItem().toString();
                if(!catName.equalsIgnoreCase("Все категории")){
                    getInfo(catName);
                } else {
                    getInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        databaseHelper.create_db();
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        productCursor.close();
    }

    private void updateInfo(Cursor newCursor) {
        if (userAdapter == null) {
            userAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.listview_item,
                    newCursor,
                    new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_DESCRIPTION, DatabaseHelper.COLUMN_PRICE},
                    new int[]{R.id.product_name, R.id.product_description, R.id.product_price},
                    0
            );
            userList.setAdapter(userAdapter);
        } else {
            Cursor oldCursor = userAdapter.swapCursor(newCursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    private void getInfo() {
        db = databaseHelper.open();
        productCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);
        updateInfo(productCursor);
    }

    private void getInfo(String catName) {
        db = databaseHelper.open();
        String sqlQuery = "SELECT * FROM products AS p JOIN category AS c ON p.category_id = c.id WHERE c.category_name = ?";
        productCursor = db.rawQuery(sqlQuery, new String[]{catName});
        updateInfo(productCursor);
    }
}


