package com.example.woldbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_search);
        WordsDBHelper sDbHelper = new WordsDBHelper(this);
        //在列表显示全部单词
        ArrayList<Map<String, String>> items = (ArrayList<Map<String, String>>) intent.getSerializableExtra("result");
        setWordsListView(items);

    }

    private void setWordsListView(ArrayList<Map<String, String>> items){
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{"_ID","COLUMN_NAME_WORD", "COLUMN_NAME_MEANING", "COLUMN_NAME_SAMPLE"},
                new int[]{R.id.textId,R.id.textViewWord, R.id.textViewMeaning, R.id.textViewSample});
        ListView list = (ListView) findViewById(R.id.lstWords);
        list.setAdapter(adapter);

    }

}