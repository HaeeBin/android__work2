package ddwucom.mobile.finalproject.ma01_20180961;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class search_restaurant extends AppCompatActivity implements OnMapReadyCallback{

    final static String TAG = "SEARCH_ACTIVITY";
    final static int PERMISSION_REQ_CODE = 100;

    EditText etAddress;
    private PlacesClient placesClient;
    private GoogleMap mGoogleMap;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private Marker allMarker;
    private MarkerOptions markerOptions;

    RadioGroup radioGroup;
    int what;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mapLoad();

        etAddress = (EditText) findViewById(R.id.et_address);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupClickListener);

        geocoder = new Geocoder(this, Locale.getDefault());

        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this);
    }

    public void onClick(View v) {
        mGoogleMap.clear();
        switch (v.getId()) {
            case R.id.btnMyLocation:
                Location lastLocation = null;
                if(checkPermission())
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng currentLat = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                String currentAddress = getAddress(lastLocation.getLatitude(), lastLocation.getLongitude()).get(0);
                etAddress.setText(currentAddress);
                if (what == 1) {
                    searchStart(currentLat); //카페
                    searchStartRes(currentLat); //식당
                } else if (what == 2) {
                    searchStartRes(currentLat);
                } else if (what == 3) {
                    searchStart(currentLat);
                }
                break;
            case R.id.btn_search:
                String text = etAddress.getText().toString();
                List<LatLng> searchLatLng = getLatLng(text);
                if (searchLatLng == null) {
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (what == 1) {
                    searchStart(searchLatLng.get(0)); //카페
                    searchStartRes(searchLatLng.get(0)); //식당
                } else if (what == 2) {
                    searchStartRes(searchLatLng.get(0));
               } else if (what == 3) {
                    searchStart(searchLatLng.get(0));
                }
                break;
            case R.id.btn_bookmark:
                Intent intent = new Intent(search_restaurant.this, bookmarkList.class);
                startActivity(intent);
                break;
            case R.id.btn_exit:
                locationManager.removeUpdates(locationListener);
                finish();
                break;
        }
    }

    /*입력된 유형의 주변 정보를 검색*/
    private void searchStart(LatLng searchResult) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult, 15));
        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.api_key))
                .latlng(Double.valueOf(searchResult.latitude),
                        Double.valueOf(searchResult.longitude))
                .radius(200)
                .type(PlaceType.CAFE)
                .build()
                .execute();

    }

    private void searchStartRes(LatLng searchResult) {
        new NRPlaces.Builder().listener(placesListener2)
                .key(getResources().getString(R.string.api_key))
                .latlng(Double.valueOf(searchResult.latitude),
                        Double.valueOf(searchResult.longitude))
                .radius(200)
                .type(PlaceType.RESTAURANT)
                .build()
                .execute();

    }
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // 매배변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        markerOptions = new MarkerOptions();
        Log.d(TAG, "Map ready");

        if(checkPermission()) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        /*info클릭하면 실주소 변환*/
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                String placeId = marker.getTag().toString();
                getPlaceDetail(placeId);
            }
        });
    };

    private void locationUpdate() {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 0, locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    RadioGroup.OnCheckedChangeListener radioGroupClickListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.radioAll)
                what = 1;
            else if(checkedId == R.id.radioRes)
                what = 2;
            else if(checkedId == R.id.radioCafe)
                what = 3;
        }
    };

    //위도,경도->주소
    private List<String> getAddress(double latitude, double longitude) {
        List<Address> addresses = null;
        ArrayList<String> addressFragments = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {
            return null;
        } else {
            Address addressList = addresses.get(0);
            addressFragments = new ArrayList<String>();

            for(int i = 0; i <= addressList.getMaxAddressLineIndex(); i++) {
                addressFragments.add(addressList.getAddressLine(i));
            }
        }

        return addressFragments;
    }
    //주소->위도,경도
    private List<LatLng> getLatLng(String targetLocation) {

        List<Address> addresses = null;
        ArrayList<LatLng> addressFragments = null;

//        주소에 해당하는 위도/경도 정보를 Geocoder 에게 요청
        try {
            addresses = geocoder.getFromLocationName(targetLocation, 1);
        } catch (IOException e) { // Catch network or other I/O problems.
            e.printStackTrace();
        } catch (IllegalArgumentException e) { // Catch invalid address values.
            e.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {
            return null;
        } else {
            Address addressList = addresses.get(0);
            addressFragments = new ArrayList<LatLng>();

            for(int i = 0; i <= addressList.getMaxAddressLineIndex(); i++) {
                LatLng latLng = new LatLng(addressList.getLatitude(), addressList.getLongitude());
                addressFragments.add(latLng);
            }
        }

        return addressFragments;
    }

    PlacesListener placesListener = new PlacesListener() {

        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            Log.d(TAG, "Add Markers");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //마커 추가
                    for(noman.googleplaces.Place place : places) {
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        allMarker = mGoogleMap.addMarker(markerOptions);
                        allMarker.setTag(place.getPlaceId());
                        Log.d(TAG, place.getName() + " : " + place.getPlaceId());
                        allMarker.showInfoWindow(); } }}); }
        @Override
        public void onPlacesFailure(PlacesException e) { }

        @Override
        public void onPlacesStart() { }

        @Override
        public void onPlacesFinished() { }
    };

    PlacesListener placesListener2 = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            Log.d(TAG, "Add Markers");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //마커 추가
                    for(noman.googleplaces.Place place : places) {
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        allMarker = mGoogleMap.addMarker(markerOptions);
                        allMarker.setTag(place.getPlaceId());
                        Log.d(TAG, place.getName() + " : " + place.getPlaceId());
                        allMarker.showInfoWindow();

                    }
                }
            });

        }
        @Override
        public void onPlacesFailure(PlacesException e) { }

        @Override
        public void onPlacesStart() { }

        @Override
        public void onPlacesFinished() { }
    };

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
                locationUpdate();

            } else {
                //퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.PHONE_NUMBER, Place.Field.ADDRESS, Place.Field.OPENING_HOURS,
                Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.PHOTO_METADATAS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener(
                new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        Place place = response.getPlace();
                        Log.d(TAG, "Place found: " + place.getName());
                        Log.d(TAG, "Phone: " + place.getPhoneNumber());
                        Log.d(TAG, "Address: " + place.getRating());
                        Log.d(TAG, "Address: " + place.getWebsiteUri());
                        Log.d(TAG, "Address: " + place.getOpeningHours().getWeekdayText());
                        Log.d(TAG, "Address: " + place.getPhotoMetadatas());

                        callDetailActivity(place);

                    }
                }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d(TAG, "Place not found: " + statusCode + " " + e.getMessage());
                        }

                    }
                });
    }
    private void callDetailActivity(Place place) {
        Intent intent = new Intent(search_restaurant.this, DetailActivity.class);
        intent.putExtra("name",place.getName());
        intent.putExtra("phone",place.getPhoneNumber());
        intent.putExtra("address",place.getAddress());
        intent.putExtra("url", place.getWebsiteUri());
        intent.putExtra("rating", place.getRating());
        if(place.getOpeningHours().getWeekdayText().get(0) != null)
            intent.putExtra("opening", place.getOpeningHours().getWeekdayText().get(0));
        else
            intent.putExtra("opening", "");

        Toast.makeText(search_restaurant.this, "address : " + place.getAddress(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}