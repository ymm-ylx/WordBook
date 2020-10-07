package com.example.woldbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.TableLayout;

import androidx.annotation.Nullable;

public class WordsProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.woldbook";//URI授权者

    private SQLiteDatabase db;
    private WordsDBHelper wordsDBHelper;
    private static final int Table_WORD = 1; //UriMathcher匹配结果码
    private static final int SINGLE_WORD = 2;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Words.Word.TABLE_NAME);
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, Words.Word.TABLE_NAME, Table_WORD);
        //uriMatcher.addURI(Words.AUTHORITY, Words.Word.PATH_MULTIPLE, MULTIPLE_WORDS);
    }
    public WordsProvider(){

    }

    @Override
    public boolean onCreate() {
        wordsDBHelper=new WordsDBHelper(getContext());
        //db=d.getWritableDatabase();
        //SQLiteDatabase db =sDbHelper.getReadableDatabase();
        //WordsDBHelper sDbHelper;
        return true;
    }

    @Override
    public int delete(@Nullable Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case Table_WORD:
                count = db.delete(Words.Word.TABLE_NAME, selection, selectionArgs);
                return count;

        }
        //通知ContentResolver,数据已经发生改变
        //getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Nullable
    @Override
    public String getType(@Nullable Uri uri) {
      return null;
    }

    @Nullable
    @Override
    public Uri insert(@Nullable Uri uri,@Nullable ContentValues values) {
        db=wordsDBHelper.getWritableDatabase();
        wordsDBHelper=new WordsDBHelper(getContext());
        switch (uriMatcher.match(uri)){
            case Table_WORD:
                long rowID=db.insert(Words.Word.TABLE_NAME, null, values);
                Uri reUri=ContentUris.withAppendedId(CONTENT_URI, rowID);
                return reUri;
        }
        return null;
        //long id = db.insert(Words.Word.TABLE_NAME, null, values);
        //if ( id > 0 ){
            //在已有的Uri后面添加id
            //Uri newUri = ContentUris.withAppendedId(Words.Word.CONTENT_URI, id);
            //getContext().getContentResolver().notifyChange(newUri, null);
            //return newUri;
        //}
        //throw new SQLException("Failed to insert row into " + uri);
    }



    @Override
    public Cursor query(@Nullable Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        db=wordsDBHelper.getReadableDatabase();
        Cursor cursor;

        //SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //qb.setTables(Words.Word.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case Table_WORD:
                cursor= db.query(Words.Word.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + uriMatcher.match(uri));
        }
        return cursor;
    }

    @Override
    public int update( @Nullable Uri uri,  @Nullable ContentValues values, @Nullable String selection,
                       @Nullable String[] selectionArgs) {
        db=wordsDBHelper.getWritableDatabase();
        int count ;
        switch (uriMatcher.match(uri)) {
            case Table_WORD:
                count = db.update(Words.Word.TABLE_NAME, values, selection, selectionArgs);
                return count;
        }
        //通知ContentResolver,数据已经发生改变
        //getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
}
