package com.worthybitbuilders.squadsense.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.GsonBuilder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.MapSearchResultAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMapBinding;
import com.worthybitbuilders.squadsense.databinding.BoardMapItemPopupBinding;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private final static int LOCATION_REQUEST_CODE = 44;
    private final ProjectService projectService = RetrofitServices.getProjectService();
    private ActivityMapBinding binding;
    private GoogleMap map;
    private SupportMapFragment mapActivity;
    private List<Marker> addressMarkers = new ArrayList<>();
    private MapSearchResultAdapter mapSearchAdapter;
    private BoardMapItemModel boardMapItemModel;
    private final String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
    private String projectId;
    private String boardId;
    private String cellId;
    // temporary marker, which is not saved and for user interact
    // same as temporary address model
    private Marker temporaryMarker = null;
    private BoardMapItemModel.AddressModel temporaryAddressModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getIntent = getIntent();
        String itemModelString = getIntent.getStringExtra("itemModel");
        boardMapItemModel = new GsonBuilder().create().fromJson(itemModelString, BoardMapItemModel.class);
        projectId = getIntent.getStringExtra("projectId");
        boardId = getIntent.getStringExtra("boardId");
        cellId = getIntent.getStringExtra("cellId");

        mapSearchAdapter = new MapSearchResultAdapter(address -> {
            LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions markerEnd = new MarkerOptions().position(point).title(address.getAddressLine(0));
            if (temporaryMarker != null) temporaryMarker.remove();
            temporaryMarker = map.addMarker(markerEnd);
            temporaryAddressModel = new BoardMapItemModel.AddressModel(address.getAddressLine(0), "", address.getLatitude(), address.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
        });

        binding.rvSearchResult.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvSearchResult.setAdapter(mapSearchAdapter);

        mapActivity = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapActivity != null) {
            mapActivity.getMapAsync(this);
        } else {
            ToastUtils.showToastError(this, "Something went wrong, please try again", Toast.LENGTH_SHORT);
            finish();
        }

        binding.btnSearch.setOnClickListener((view) -> {
            binding.btnSearch.setVisibility(View.GONE);
            binding.btnClearSearch.setVisibility(View.VISIBLE);
            binding.etSearch.setVisibility(View.VISIBLE);
            binding.etSearch.requestFocus();
            binding.rvSearchResult.setVisibility(View.VISIBLE);
        });

        binding.btnClearSearch.setOnClickListener((view) -> {
            binding.btnSearch.setVisibility(View.VISIBLE);
            binding.btnClearSearch.setVisibility(View.GONE);
            binding.etSearch.setVisibility(View.GONE);
            binding.etSearch.setText("");
            mapSearchAdapter.setData(null);
            binding.rvSearchResult.setVisibility(View.GONE);
        });

        binding.btnBack.setOnClickListener((view) -> finish());

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                handleSearch(editable.toString());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMarkerClickListener(this);

        if (boardMapItemModel.getAddresses() != null) {
            List<BoardMapItemModel.AddressModel> addresses = boardMapItemModel.getAddresses();
            for (int i = 0; i < addresses.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(addresses.get(i).latitude, addresses.get(i).longitude))
                        .title(addresses.get(i).title);

                Marker marker = map.addMarker(markerOptions);
                addressMarkers.add(marker);

                if (i == 0) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(addresses.get(i).latitude, addresses.get(i).longitude)));
                }
            }
        }

        binding.btnMyLocation.setOnClickListener((view) -> getCurrentLocation());
        map.setOnMapClickListener(latLng -> {
            Geocoder geocoder = new Geocoder(MapActivity.this);
            try {
                Address address = ((ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)).get(0);
                MarkerOptions markerEnd = new MarkerOptions().position(latLng).title(address.getAddressLine(0));
                if (temporaryMarker != null) temporaryMarker.remove();
                temporaryAddressModel = new BoardMapItemModel.AddressModel(address.getAddressLine(0), "", latLng.latitude, latLng.longitude);
                temporaryMarker = map.addMarker(markerEnd);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(MapActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        Task<Location> task = LocationServices.getFusedLocationProviderClient(MapActivity.this).getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title(location.toString());
                if (temporaryMarker != null) temporaryMarker.remove();
                temporaryAddressModel = new BoardMapItemModel.AddressModel(location.toString(), "", location.getLatitude(), location.getLongitude());
                temporaryMarker = map.addMarker(markerOptions);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));
            }
        });
    }

    public void handleSearch(String query) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(MapActivity.this);
        try {
            // TODO: use Place Autocomplete Api for ambiguous query
            addresses = geocoder.getFromLocationName(query, 4);
            if (addresses != null) mapSearchAdapter.setData(addresses);
        } catch (Exception ignored) {}
    }

    public void locationPopup(BoardMapItemModel.AddressModel addressModel, boolean isAddingNewAddress, Integer updatingPosition) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardMapItemPopupBinding popupBinding = BoardMapItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(popupBinding.getRoot());

        if (isAddingNewAddress) popupBinding.btnSave.setText("Add");
        popupBinding.tvAddressLocation.setText("Location: " + addressModel.title);
        popupBinding.etAddressDescription.setText(addressModel.description);
        popupBinding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());

        popupBinding.btnSave.setOnClickListener((view -> {
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(MapActivity.this);
            loadingDialog.show();

            addressModel.description = popupBinding.etAddressDescription.getText().toString();

            if (isAddingNewAddress) {
                boardMapItemModel.getAddresses().add(addressModel);
            } else boardMapItemModel.getAddresses().set(updatingPosition, addressModel);

            // TODO: if it fails to update, there should be a way to reverse the changes that took effect
            projectService.updateCellToRemote(userId, projectId, boardId, cellId, boardMapItemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        // the reason why it is null
                        // is when after updating, if user click another part it wont be remove by ".remove()"
                        temporaryMarker = null;
                    } else {
                        ToastUtils.showToastError(MapActivity.this, "Unable to update, you should consider reset this page", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(MapActivity.this, "Something went wrong, you should consider reset this page", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });
        }));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        LatLng point = marker.getPosition();

        // check if it is adding a new address
        if (temporaryMarker != null && temporaryAddressModel != null)
            if (Double.compare(point.latitude, temporaryAddressModel.latitude) == 0 &&
                Double.compare(point.longitude, temporaryAddressModel.longitude) == 0) {
                locationPopup(temporaryAddressModel, true, null);
                return false;
            }

        // if it's not adding a new address, get the updating position
        List<BoardMapItemModel.AddressModel> addresses = boardMapItemModel.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int firstCompare = Double.compare(addresses.get(i).latitude, marker.getPosition().latitude);
            int secondCompare = Double.compare(addresses.get(i).longitude, marker.getPosition().longitude);
            if (firstCompare == 0 && secondCompare == 0) {
                locationPopup(addresses.get(i), false, i);
                return false;
            }
        }

        // if it run into here then it means error
        throw new RuntimeException("Error");
    }
}
