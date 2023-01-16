package com.example.contactapp_e020320110;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private RecyclerView contactRv;

    private DbHelper dbHelper;

    private AdapterContact adapterContact;

    private String sortByNewest = Constants.C_ADDED_TIME + " DESC";
    private String sortByOldest = Constants.C_ADDED_TIME + " ASC";
    private String sortByNameAsc = Constants.C_NAME + " ASC";
    private String sortByNameDesc = Constants.C_NAME + " DESC";

    private String currentSort = sortByNewest;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar =getSupportActionBar();

        dbHelper = new DbHelper(this);

        //initialization
        fab = findViewById(R.id.fab);
        contactRv = findViewById(R.id.contactRv);

        contactRv.setHasFixedSize(true);

        //add listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move to new activity to add contact
                Intent intent = new Intent(MainActivity.this, AddEditContact.class);
                intent.putExtra("isEditMode", false);
                startActivity(intent);
            }
        });

         loadData(currentSort);
    }

    private void loadData(String currentSort) {
        adapterContact = new AdapterContact(this, dbHelper.getAllData(currentSort));
        contactRv.setAdapter(adapterContact);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(currentSort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_top_menu,menu);

        MenuItem item = menu.findItem(R.id.searchContact);

        SearchView searchView = (SearchView)  item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContact(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContact(newText);
                return true;
            }
        });

        return true;
    }

    private void searchContact(String query) {
        adapterContact = new AdapterContact(this,dbHelper.getSearchContact(query));
        contactRv.setAdapter(adapterContact);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteAllContact:
                dbHelper.deleteAllContact();
                onResume();
                break;
            case R.id.sortContact:
                sortDialog();
                break;
        }

        return true;
    }

    private void sortDialog() {
        String[] option = {"Newest","Oldest","Name Asc","Name Desc"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0){
                    loadData(sortByNewest);
                }else if (which == 1){
                    loadData(sortByOldest);
                }else if (which == 2){
                    loadData(sortByNameAsc);
                }else if (which == 3){
                    loadData(sortByNameDesc);
                }
            }
        });
        builder.create().show();
    }
}