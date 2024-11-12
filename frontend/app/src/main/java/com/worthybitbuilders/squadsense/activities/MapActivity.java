package com.worthybitbuilders.squadsense.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityMapBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private ActivityMapBinding binding;
    private GoogleMap map;
    private SupportMapFragment mapActivity;

    private ArrayList<Address> locationArrayList;
    private final static int LOCATION_REQUEST_CODE = 44;

    private boolean removeMarkerMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapActivity = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapActivity != null) {
            mapActivity.getMapAsync(this);
        }

        locationArrayList = new ArrayList<>();
        // add address in tokyo
        locationArrayList.add(new Address(null));
        locationArrayList.get(0).setLatitude(35.6803997);
        locationArrayList.get(0).setLongitude(139.7690174);
        locationArrayList.get(0).setAddressLine(0, "Tokyo");
        // add address in kyoto
        locationArrayList.add(new Address(null));
        locationArrayList.get(1).setLatitude(35.0116363);
        locationArrayList.get(1).setLongitude(135.7680294);
        locationArrayList.get(1).setAddressLine(0, "Kyoto");
        // add address in osaka
        locationArrayList.add(new Address(null));
        locationArrayList.get(2).setLatitude(34.6937378);
        locationArrayList.get(2).setLongitude(135.5021651);
        locationArrayList.get(2).setAddressLine(0, "Osaka");

        binding.btnSelect.setOnClickListener(view -> {
            Log.d("Location", "-------------");
            for (int i = 0; i < locationArrayList.size(); i++) {
                Log.d("Location", locationArrayList.get(i).getAddressLine(0));
            }
        });

        binding.btnRmMarker.setOnClickListener(view -> {
            removeMarkerMode = !removeMarkerMode;

            if (removeMarkerMode) {
                Toast.makeText(MapActivity.this, "Click on a marker to remove", Toast.LENGTH_SHORT).show();
                binding.btnRmMarker.setText("Cancel");
                binding.btnRmMarker.setTextColor(getResources().getColor(R.color.red));
            } else {
                Toast.makeText(MapActivity.this, "Remove marker mode canceled", Toast.LENGTH_SHORT).show();
                binding.btnRmMarker.setText("Remove Marker");
                binding.btnRmMarker.setTextColor(getResources().getColor(R.color.white));
            }
        });

        onHandleSearch();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // set map type
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // set zoom control
        map.getUiSettings().setZoomControlsEnabled(true);
        // set marker click listener
        map.setOnMarkerClickListener(this);
        for (int i = 0; i < locationArrayList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude())).title(locationArrayList.get(i).getAddressLine(0));

            map.addMarker(markerOptions);
            // move camera to first location
            map.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude())));
        }

        map.setOnMapClickListener(latLng -> {
            Geocoder geocoder = new Geocoder(MapActivity.this);
            try {
                Address address = ((ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)).get(0);
                MarkerOptions markerEnd = new MarkerOptions().position(latLng).title(address.getAddressLine(0));
                map.addMarker(markerEnd);
                // add to address list
                locationArrayList.add(address);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(MapActivity.this, "Không xác định được vị trí", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void onHandleSearch() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = binding.searchView.getQuery().toString();
                List<Address> addresses = null;
                Geocoder geocoder = new Geocoder(MapActivity.this);
                try {
                    addresses = geocoder.getFromLocationName(location, 1);
                    if (addresses != null) {
                        Address address = addresses.get(0);
                        LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
                        MarkerOptions markerEnd = new MarkerOptions().position(point).title(location);
                        map.addMarker(markerEnd);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
                    }
                } catch (Exception e) {
                    Toast.makeText(MapActivity.this, "Không tìm ra địa điểm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        if (removeMarkerMode) {
            // remove in address list
            for (int i = 0; i < locationArrayList.size(); i++) {

                if (locationArrayList.get(i).getAddressLine(0).equals(marker.getTitle())) {
                    locationArrayList.remove(i);
                    break;
                }
            }
            marker.remove();
            Toast.makeText(MapActivity.this, "Marker removed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
