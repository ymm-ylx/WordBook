package com.example.woldbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WordsDBHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_land);
            ListView list1 = (ListView) findViewById(R.id.lstWords1);
            registerForContextMenu(list1);
            //创建SQLiteOpenHelper对象，注意第一次运行时，此时数据库并没有被创建
            mDbHelper = new WordsDBHelper(this);
            //在列表显示全部单词
            ArrayList<Map<String, String>> items1=getAll();
            setWordsListViewland(items1);

        }

        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
            ListView list = (ListView) findViewById(R.id.lstWords);
            registerForContextMenu(list);
            //创建SQLiteOpenHelper对象，注意第一次运行时，此时数据库并没有被创建
            mDbHelper = new WordsDBHelper(this);
            //在列表显示全部单词
            ArrayList<Map<String, String>> items=getAll();
            setWordsListView(items);
        }

    }

    private void setWordsListViewland(ArrayList<Map<String, String>> items) {
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{"_ID","COLUMN_NAME_WORD"},
                new int[]{R.id.textId,R.id.textViewWord});

        ListView list1 = (ListView) findViewById(R.id.lstWords1);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list1,View view, int i, long l) {
                Map<String,String> temp= (Map<String, String>) list1.getItemAtPosition(i);
                String w = temp.get("COLUMN_NAME_WORD");
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Cursor c=db.query("words",null,"word='"+w+"'",null,null,null,"word");
                TextView t1=findViewById(R.id.txtMeaning1);
                TextView t2=findViewById(R.id.txtSample1);
                while (c.moveToNext()) {
                    t1.setText(c.getString(c.getColumnIndex("meaning")));
                    t2.setText(c.getString(c.getColumnIndex("sample")));
                }

            }
        });


    }


    private ArrayList<Map<String, String>> getAll() {//如何从数据库中拿出数据添加到map中？
        //call DBOpenHelper

        ArrayList<Map<String, String>> listitem=new ArrayList<Map<String, String>>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor c = db.query("words",null,null,null,null,null,null);
        c.moveToFirst();
        int iColCount = c.getColumnCount();
        int iNumber = 0;
        String strType = "";
        while (iNumber < c.getCount()){
            Map<String,String> map = new HashMap<String, String>();
            map.put("_ID", c.getString(c.getColumnIndex("_id")));
            map.put("COLUMN_NAME_WORD", c.getString(c.getColumnIndex("word")));
            map.put("COLUMN_NAME_MEANING",  c.getString(c.getColumnIndex("meaning")));
            map.put("COLUMN_NAME_SAMPLE",  c.getString(c.getColumnIndex("sample")));
            c.moveToNext();
            listitem.add(map);
            iNumber++;
        }
        c.close();
        db.close();
        return listitem;
    }

    private void setWordsListView(ArrayList<Map<String, String>> items){
        /*
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{"_ID","COLUMN_NAME_WORD", "COLUMN_NAME_MEANING", "COLUMN_NAME_SAMPLE"},
                new int[]{R.id.textId,R.id.textViewWord, R.id.textViewMeaning, R.id.textViewSample});

        ListView list = (ListView) findViewById(R.id.lstWords);
        list.setAdapter(adapter);
        */
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{"_ID","COLUMN_NAME_WORD"},
                new int[]{R.id.textId,R.id.textViewWord});

        ListView list= (ListView) findViewById(R.id.lstWords);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list,View view, int i, long l) {
                Map<String,String> temp= (Map<String, String>) list.getItemAtPosition(i);
                String w = temp.get("COLUMN_NAME_WORD");
                ArrayList<Map<String, String>> items=SearchUseSql(w);
                // items=Search(txtSearchWord);
                if(items.size()>0) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("result",items);
                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }



            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
              //查找
                SearchDialog();
                return true;
            case R.id.action_insert:
                    //新增单词
                InsertDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextmenu_wordslistview, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView textId=null;
        TextView textWord=null;
        TextView textMeaning=null;
        TextView textSample=null;
        AdapterView.AdapterContextMenuInfo info=null;
        View itemView=null;
        switch (item.getItemId()){
            case R.id.action_delete:
                //删除单词
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId =(TextView)itemView.findViewById(R.id.textId);
                if(textId!=null){
                    String strId=textId.getText().toString();
                    DeleteDialog(strId);
                }
                break;
            case R.id.action_update:
                //修改单词  o_o ....
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId =(TextView)itemView.findViewById(R.id.textId);
                textWord =(TextView)itemView.findViewById(R.id.textViewWord);
                textMeaning =(TextView)itemView.findViewById(R.id.textViewMeaning);
                textSample =(TextView)itemView.findViewById(R.id.textViewSample);
                if(textId!=null && textWord!=null && textMeaning!=null && textSample!=null){
                    String strId=textId.getText().toString();
                    String strWord=textWord.getText().toString();
                    String strMeaning=textMeaning.getText().toString();
                    String strSample=textSample.getText().toString();
                    UpdateDialog(strId, strWord, strMeaning, strSample);
                }
                break;
        }
        return true;
    }


    private void InsertUserSql(String strWord, String strMeaning, String strSample){
        String sql="insert into  words(word,meaning,sample) values(?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql,new String[]{strWord,strMeaning,strSample});
    }
    //新增对话框
    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this).setTitle("新增单词").setView(tableLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String strWord=((EditText)tableLayout.findViewById(R.id.txtWord)).getText().toString();
                String strMeaning=((EditText)tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                String strSample=((EditText)tableLayout.findViewById(R.id.txtSample)).getText().toString();
                //既可以使用Sql语句插入，也可以使用使用insert方法插入
                InsertUserSql(strWord, strMeaning, strSample);
                ArrayList<Map<String, String>> items=getAll();
                setWordsListView(items);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create() .show();
    }

    private void DeleteUseSql(String strId) {
        String sql="delete from words where _id='"+strId+"'";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(sql);
    }


    private void DeleteDialog(final String strId){
        new AlertDialog.Builder(this).setTitle("删除单词").setMessage("是否真的删除单词?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //既可以使用Sql语句删除，也可以使用使用delete方法删除
                DeleteUseSql(strId);
                //Delete(strId);
                setWordsListView(getAll());
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    private void UpdateUseSql(String strId,String strWord, String strMeaning, String strSample) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="update words set word=?,meaning=?,sample=? where _id=?";
        db.execSQL(sql, new String[]{strWord, strMeaning, strSample,strId});
    }

    private void UpdateDialog(final String strId, final String strWord, final String strMeaning, final String strSample) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText)tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText)tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText)tableLayout.findViewById(R.id.txtSample)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词").setView(tableLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        //既可以使用Sql语句更新，也可以使用使用update方法更新
                        UpdateUseSql(strId, strNewWord, strNewMeaning, strNewSample);
                        //  Update(strId, strNewWord, strNewMeaning, strNewSample);
                        setWordsListView(getAll());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
        }).create().show();
    }


    private ArrayList<Map<String, String>> SearchUseSql(String strWordSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="select * from words where word like ? order by word desc";
        Cursor c=db.rawQuery(sql,new String[]{"%"+strWordSearch+"%"});
        return ConvertCursor2List(c);
    }

    private ArrayList<Map<String, String>> ConvertCursor2List(Cursor c) {
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
        while (c.moveToNext())
        {
            Map<String, String> map = new HashMap<String, String>();
            map.put("_ID", c.getString(c.getColumnIndex("_id")));
            map.put("COLUMN_NAME_WORD", c.getString(c.getColumnIndex("word")));
            map.put("COLUMN_NAME_MEANING",  c.getString(c.getColumnIndex("meaning")));
            map.put("COLUMN_NAME_SAMPLE",  c.getString(c.getColumnIndex("sample")));
            result.add(map);
        }
        return result;

    }


    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.searchterm, null);
        new AlertDialog.Builder(this).setTitle("新增单词").setView(tableLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String txtSearchWord=((EditText)tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();
                ArrayList<Map<String, String>> items=null;
                //既可以使用Sql语句查询，也可以使用方法查询
                items=SearchUseSql(txtSearchWord);
                // items=Search(txtSearchWord);
                if(items.size()>0) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("result",items);
                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else
                    Toast.makeText(MainActivity.this,"没有找到",Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }            }).create().show();}


}



