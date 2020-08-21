package com.visionary.developers.todo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.visionary.developers.todo.adapters.ItemsAdapter;
import com.visionary.developers.todo.R;

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

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), R.string.removed, Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                EditText editText = new EditText(MainActivity.this);
                editText.setText(items.get(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.edit_item)
                        .setView(editText)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String s = editText.getText().toString();
                                if(s.equals("") || s.trim().equals("")) {
                                    Toast.makeText(getApplicationContext(), R.string.no_information, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    items.set(position, s);
                                    itemsAdapter.notifyItemChanged(position);
                                    editText.setText("");
                                    Toast.makeText(getApplicationContext(), R.string.update, Toast.LENGTH_SHORT).show();
                                    saveItems();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rView.setAdapter(itemsAdapter);
        rView.setLayoutManager(new LinearLayoutManager(this));


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = eText.getText().toString();
                if (s.equals("") || s.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.no_information, Toast.LENGTH_SHORT).show();
                } else {
                    items.add(s);
                    itemsAdapter.notifyItemInserted(items.size() - 1);
                    eText.setText("");
                    Toast.makeText(getApplicationContext(), R.string.added, Toast.LENGTH_SHORT).show();
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
