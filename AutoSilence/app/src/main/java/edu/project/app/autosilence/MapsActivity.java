package edu.project.app.autosilence;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    public static final String FROM_MAPS_ACTIVITY = "fromMapsActivity";
    private GoogleMap mMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.mapSelect) {
                if (marker != null) {
                    setResult(RESULT_OK, new Intent().putExtra(ConfirmDialogActivity.GEO_LATITUDE, marker.getPosition().latitude)
                            .putExtra(ConfirmDialogActivity.GEO_LONGITUDE, marker.getPosition().longitude)
                            .putExtra(MainActivity.ACTIVITY_FROM, FROM_MAPS_ACTIVITY));
                }
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        drawAreas();
    }

    private void drawAreas() {
        LocationDBHelper dbHelper = new LocationDBHelper(this);
        ArrayList<AutoSilenceLocation> locations = dbHelper.getAllData();
        for (int i = 0; i < locations.size(); i++) {
            AutoSilenceLocation location = locations.get(i);
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(location.getLat(), location.getLng()))
                    .clickable(false)
                    .radius(location.getRadius())
                    .fillColor(Color.argb(150, 200, 200, 200))
                    .strokeColor(Color.RED)
                    .strokeWidth(2);
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(location.getName())
                    .draggable(false)
                    .position(new LatLng(location.getLat(), location.getLng()))
                    .snippet(location.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
            mMap.addCircle(circleOptions);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this, String.format(Locale.ENGLISH, "Lat=%.4f\nLng=%.4f", marker.getPosition().latitude, marker.getPosition().longitude), Toast.LENGTH_SHORT).show();
    }
}
