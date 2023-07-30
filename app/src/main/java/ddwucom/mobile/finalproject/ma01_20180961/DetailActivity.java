package ddwucom.mobile.finalproject.ma01_20180961;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

public class DetailActivity extends AppCompatActivity {
    final static int PERMISSION_REQ_CODE = 100;
    final static String TAG = "DetailActivity";

    EditText etName;
    EditText etPhone;
    EditText etAddress;
    EditText etOpening;
    EditText etRating;
    EditText etUrl;

    PlaceDBHelper placeDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        placeDBHelper = new PlaceDBHelper(this);

        etName = findViewById(R.id.etTitle);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etOpening = findViewById(R.id.etOpen);
        etRating = findViewById(R.id.etRating);
        etUrl = findViewById(R.id.etUrl);

        etPhone.setText("");
        etUrl.setText("");
        etRating.setText("");
        etOpening.setText("");
        etAddress.setText("");

        if(getIntent().getExtras() != null)
            etName.setText(getIntent().getExtras().getString("name"));
            etAddress.setText(getIntent().getExtras().getString("address"));
            etPhone.setText(getIntent().getExtras().getString("phone"));
            etRating.setText(getIntent().getExtras().getString("rating"));
            etUrl.setText(getIntent().getExtras().getString("url"));
        if(getIntent().getExtras().getString("opening") != null)
            etOpening.setText(getIntent().getExtras().getString("opening"));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addBookmark:
                SQLiteDatabase db = placeDBHelper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(PlaceDBHelper.NAME, etName.getText().toString());
                row.put(PlaceDBHelper.ADDRESS, etAddress.getText().toString());
                row.put(PlaceDBHelper.PHONE, etPhone.getText().toString());
                row.put(PlaceDBHelper.OPENING, etOpening.getText().toString());
                row.put(PlaceDBHelper.RATING, etRating.getText().toString());
                row.put(PlaceDBHelper.SITE, etUrl.getText().toString());

                long result = db.insert(PlaceDBHelper.TABLE_NAME, null, row);

                if(result > 0) {
                    Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
                    placeDBHelper.close();
                }
                else {
                    Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                    placeDBHelper.close();
                }
                break;
            case R.id.btn_detailExit:
                finish();
                break;
        }
    }
    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //퍼미션을 획득하였을 경우 맵 로딩 실행
                //locationUpdate();

            } else {
                //퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
            }
        }
    }
}