package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
{

    // a numeric code to identify edit activity
    public final static int Edit_REQUEST_CODE = 20;
    //key used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";

    // Declare members variables

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    EditText etNewItem;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //items = new ArrayList<>();

        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);

        //mock data
        //items.add("First item");
       // items.add("Second item");

        // SetupListViewListener function called

        SetupListViewListener();
    }

    // Add fonction

    public void onAddItem(View v)
    {
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();
    }

    // remove function
    private void SetupListViewListener()
    {

        Log.i("Main2Activity", "Setting up listener on list view");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                Log.i("Main2Activity", "Item removed from list:" + i);
                items.remove(i);
                itemsAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Item removed from list", Toast.LENGTH_SHORT).show();
                writeItems();
                return true;
            }
        });

        // set up item listener for edit (Regular click)
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // create the new activity
                Intent intent = new Intent(Main2Activity.this, EditItemActivity.class);
                //pass the data being edited
                intent.putExtra(ITEM_TEXT, items.get(i));
                intent.putExtra(ITEM_POSITION, i);
                // display the activity
                startActivityForResult(intent, Edit_REQUEST_CODE);
            }
        });
    }

    // handle  result from edit activity


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity ok
        if (resultCode == RESULT_OK && requestCode == Edit_REQUEST_CODE)
        {
            // extract updated item text from result intent extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            // extract original position of edited item
            int position = data.getExtras().getInt(ITEM_POSITION);
            // update the model with the new item text at the edited position
            items.set(position, updatedItem);
            // notify the adapter that the model changed
            itemsAdapter.notifyDataSetChanged();
            // persist the changed model
            writeItems();
            // notify the user the operation completed ok
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // GET DATA FIle Function
    private File getDataFile()
    {
        return new File(getFilesDir(), "todo.txt");
    }

    // READ FILES Function
    private void readItems()
    {
        try
        {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e)
        {
            Log.e("Main2Activity", "Error reading file", e);
            items = new ArrayList<>();
        }
    }

    // WRITE FILES Function
    private void writeItems()
    {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e)
        {
            Log.e("Main2Activity", "Error writing file", e);
        }
    }

}
