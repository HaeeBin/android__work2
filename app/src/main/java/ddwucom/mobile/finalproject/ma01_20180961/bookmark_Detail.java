package ddwucom.mobile.finalproject.ma01_20180961;

import static ddwucom.mobile.finalproject.ma01_20180961.PlaceDBHelper.ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class bookmark_Detail extends AppCompatActivity {

    EditText etName;
    EditText etAddress;
    EditText etPhone;
    EditText etOpening;
    EditText etRating;
    EditText etMemo;
    EditText etSite;

    long id;
    PlaceDBHelper placeDBHelper;
    bookmarkDto dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_detail);

        etName = findViewById(R.id.etbookName);
        etAddress = findViewById(R.id.etbookAddress);
        etPhone = findViewById(R.id.etbookPhone);
        etOpening = findViewById(R.id.etbookOpen);
        etRating = findViewById(R.id.etbookRating);
        etSite = findViewById(R.id.etbookSite);
        etMemo = findViewById(R.id.etMemo);

        placeDBHelper = new PlaceDBHelper(this);
        id = getIntent().getLongExtra("id", 0);

        SQLiteDatabase sqLiteDatabase = placeDBHelper.getReadableDatabase();

        String where = ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        Cursor cursor = sqLiteDatabase.query(PlaceDBHelper.TABLE_NAME, null, where, whereArgs, null, null, null, null);

        while(cursor.moveToNext()) {
            dto = new bookmarkDto();
            dto.set_id(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
            dto.setName(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.NAME)));
            dto.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.ADDRESS)));
            dto.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.PHONE)));
            dto.setOpening(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.OPENING)));
            dto.setRating(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.RATING)));
            dto.setSite(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.SITE)));
            dto.setMemo(cursor.getString(cursor.getColumnIndexOrThrow(PlaceDBHelper.MEMO)));
        }

        etName.setText(dto.getName());
        etAddress.setText(dto.getAddress());
        etPhone.setText(dto.getPhone());
        etOpening.setText(dto.getOpening());
        etRating.setText(dto.getRating());
        etSite.setText(dto.getSite());
        etMemo.setText(dto.getMemo());

        cursor.close();
        placeDBHelper.close();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                String name = etName.getText().toString();
                String address = etAddress.getText().toString();
                String phone = etPhone.getText().toString();
                String opening = etOpening.getText().toString();
                String rating = etRating.getText().toString();
                String site = etSite.getText().toString();
                String memo = etMemo.getText().toString();

                ContentValues row = new ContentValues();
                row.put(PlaceDBHelper.NAME, name);
                row.put(PlaceDBHelper.ADDRESS, address);
                row.put(PlaceDBHelper.PHONE, phone);
                row.put(PlaceDBHelper.OPENING, opening);
                row.put(PlaceDBHelper.RATING, rating);
                row.put(PlaceDBHelper.SITE, site);
                row.put(PlaceDBHelper.MEMO, memo);

                SQLiteDatabase db = placeDBHelper.getWritableDatabase();

                String whereClause = PlaceDBHelper.ID + "=?";
                String[] whereArg = new String[]{String.valueOf(id)};
                db.update(PlaceDBHelper.TABLE_NAME, row, whereClause, whereArg);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", id);
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}