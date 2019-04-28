package com.example.findyourtoilet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

public class ToiletList extends AppCompatActivity implements ToiletAdapter.OnListItemClickListener{

    RecyclerView mToiletList;
    RecyclerView.Adapter mToiletAdapter;
    ArrayList<Toilet> toilets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_list);

        mToiletList=findViewById(R.id.rv);
        mToiletList.hasFixedSize();
        mToiletList.setLayoutManager(new LinearLayoutManager(this));

        Bundle budndle=getIntent().getExtras();
        toilets=(ArrayList<Toilet>)budndle.getSerializable("toiletsInAarhus");

        mToiletAdapter=new ToiletAdapter(toilets,this);
        mToiletList.setAdapter(mToiletAdapter);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(this, "Status: "+toilets.get(clickedItemIndex).getStatus() , Toast.LENGTH_SHORT).show();
    }
}
