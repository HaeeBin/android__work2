package ddwucom.mobile.finalproject.ma01_20180961;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class bookmarkList extends AppCompatActivity {
    final int UPDATE_CODE = 200;

    ListView listView = null;
    PlaceDBHelper placeDBHelper;
    Cursor cursor;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        listView = findViewById(R.id.listView);
        placeDBHelper = new PlaceDBHelper(this);

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[] {PlaceDBHelper.NAME, PlaceDBHelper.ADDRESS},
                new int[] {android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(bookmarkList.this, bookmark_Detail.class);
                intent.putExtra("id", l);
                startActivityForResult(intent, UPDATE_CODE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(bookmarkList.this);
                builder.setTitle("삭제 확인")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRes(l);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_listexit:
                finish();
                break;
        }
    }
    private void deleteRes(long id) {
        PlaceDBHelper helper = new PlaceDBHelper(this);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        String whereClause = PlaceDBHelper.ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(id)};
        int result = sqLiteDatabase.delete(PlaceDBHelper.TABLE_NAME, whereClause, whereArgs);
        if(result > 0) {
            Toast.makeText(this, "삭제 완료", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "삭제 실패", Toast.LENGTH_SHORT).show();
        }
        Cursor cursor2 = sqLiteDatabase.rawQuery("select * from " + PlaceDBHelper.TABLE_NAME, null);
        adapter.changeCursor(cursor2);

        helper.close();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = placeDBHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + PlaceDBHelper.TABLE_NAME, null);

        adapter.changeCursor(cursor);
        placeDBHelper.close();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "수정 취소", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}