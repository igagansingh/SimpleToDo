package com.visionary.developers.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME="data.txt";

    private List<String> items;
    private ItemsAdapter itemsAdapter;

    private EditText eText;
    private RecyclerView rView;
    private ImageButton addBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eText = findViewById(R.id.eItem);
        addBtn = findViewById(R.id.add);
        rView = findViewById(R.id.rView);
        rView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        items = loadItems();

        itemsAdapter = new ItemsAdapter(items, new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
        rView.setAdapter(itemsAdapter);
        rView.setLayoutManager(new LinearLayoutManager(this));


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = eText.getText().toString();
                if(s.equals("") || s.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "No information provided!", Toast.LENGTH_SHORT).show();
                }
                else {
                    items.add(s);
                    itemsAdapter.notifyItemInserted(items.size()-1);
                    eText.setText("");
                    Toast.makeText(getApplicationContext(), "Item was added!", Toast.LENGTH_SHORT).show();
                    saveItems();
                }
            }
        });
    }


    private File getDataFile() {
        return new File(getFilesDir(), FILE_NAME);
    }

    private List<String> loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
        return items;
    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
            e.printStackTrace();
        }
    }
}
