package com.avik.recycleritemswipping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewAdapter viewAdapter;
    private String itemText = "Item";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("onCreate", "called");
        updateList();
    }

    private void updateList(){
        final List<RecyclerItem> items = new ArrayList<>();
        for(int i=0; i<30; i++) {
            RecyclerItem item = new RecyclerItem();
            items.add(item);
            items.get(i).setItemText(itemText + (i+1));
        }
        viewAdapter = new ViewAdapter(items);
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.notifyDataSetChanged();
        SwipeController swipeController = new SwipeController(viewAdapter, new SwipeControllerActions() {
            @Override
            public void viewDetails(int position) {
                Intent intent = new Intent(MainActivity.this, DummyActivity.class);
                intent.putExtra(getResources().getString(R.string.intent_data), items.get(position).getItemText());
                startActivity(intent);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
