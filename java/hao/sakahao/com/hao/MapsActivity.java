package hao.sakahao.com.hao;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback{

    private LocationRequest mLocationRequest;
    protected static final String TAG = "MainActivity";
    private SupportMapFragment  mMapFragment; // Might be null if Google Play services APK is not available.
    GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    public Toolbar myToolbar;
    private CoordinatorLayout mCoordinatorLayout;
    NavigationView navigation;
    DrawerLayout drawerLayout;
    TextView userName;
    String mHaoLocation = "Test 2";
    String bsHaoName, bsHaoDescription, bsHaoLocation;
    double mHaoLat;
    double mHaoLong;
    TextView txtHaoLocal, txtHaoTitle;
    BottomSheetBehavior bottomSheetBehavior;
    HashMap <String, String> mMarkers = new HashMap<String, String>();
    Iterator<String> iterator = mMarkers.keySet().iterator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        txtHaoLocal = (TextView) findViewById(R.id.txt_hao_local);
        txtHaoTitle = (TextView) findViewById(R.id.txt_hao_title);
        LinearLayout bottomSheetViewgroup
                = (LinearLayout) findViewById(R.id.design_bottom_sheet_hao);

       bottomSheetBehavior =
                BottomSheetBehavior.from(bottomSheetViewgroup);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        navigation = (NavigationView) findViewById(R.id.navView);

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                            if(item.isChecked())
                                item.setChecked(false);
                            else
                                item.setChecked(true);

                switch (item.getItemId()){
                    case R.id.navigation_maps:
                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(i);
                    case R.id.navigation_watch_list:
                    case R.id.navigation_about:
                    case R.id.navigation_promotions:
                    case R.id.navigation_refer:
                }

                return true;
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                UserDetails userDetails = new UserDetails();
                userDetails.setUpDetails();
                userName = (TextView) findViewById(R.id.user_name);
                if(userName != null){
                    userName.setText(userDetails.getuName());}
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewHao();
            }
        });


        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        buildGoogleApiClient();
        coordinatorLayoutParams();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpPins(); }

    public void setUpPins() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final HaoRetrieve haoRetriever = dataSnapshot.getValue(HaoRetrieve.class);



                for ( DataSnapshot child : dataSnapshot.getChildren()){

                    Double dLat = haoRetriever.getLat();
                    Double dLong = haoRetriever.getLongitude();
                    bsHaoName = haoRetriever.getName();

                   Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(dLat, dLong))
                            .title(bsHaoName).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));

                    mMarkers.put("MakerId", marker.getId());
                    mMarkers.put("MarkerTitle", haoRetriever.getName());
                    mMarkers.put("MarkerDescription", haoRetriever.getDescription());
                    mMarkers.put("MarkerLocation", haoRetriever.getLocation());
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker m) {
                        if(mMarkers.get("MakerId").equals(m.getId()))
                            txtHaoTitle.setText(mMarkers.get("MarkerTitle"));
                        txtHaoLocal.setText(mMarkers.get("MarkerLocation"));
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    public void coordinatorLayoutParams() {
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.mapAppbar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMapFragment == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment = (SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.map);
            mMapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMapFragment != null) {
            }
        }
    }

    private void setUpMap() {
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        setUpPins();
    }

    private void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
         mHaoLat = location.getLatitude();
         mHaoLong = location.getLongitude();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            if (addressList != null && addressList.size() > 0) {
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);

                MarkerOptions a = new MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));;

                Marker m = mMap.addMarker(a);
                m.setPosition(new LatLng(currentLatitude,currentLongitude));

               // mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
              //  MarkerOptions options = new MarkerOptions()
               //         .position(latLng)
               //         .title("You are here")
               //         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
              //  mMap.addMarker(options);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                //TextView locationName = (TextView) findViewById(R.id.haoTitle);
                //locationName.setText(addressList.get(0).getFeatureName());

                myToolbar.setTitle(addressList.get(0).getFeatureName());
                myToolbar.setSubtitle(addressList.get(0).getLocality());

                mHaoLocation = addressList.get(0).getFeatureName();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onConnected(Bundle bundle) {

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
           /* double longitude = mLastLocation.getLongitude();
            double latitude = mLastLocation.getLatitude();
            //mLatitudeText.setText(String.format("%s: %f", "Latitude",
                    latitude));
           // mLongitudeText.setText(String.format("%s: %f", "Longitude",
                    longitude));*/
            handleNewLocation(mLastLocation);
        } else {
            Snackbar.make(mCoordinatorLayout, "Location services have been turned off. Please turn on GPS or allow us to access your current location.", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(this, "Location services have been turned off. Please turn on GPS or allow us to access your current location.", Toast.LENGTH_LONG).show();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }


    public void addNewHao(){
        AddHaoDialog newHao = new AddHaoDialog();
        //AddHaoDialog addHaoDialog = new AddHaoDialog();
        Bundle args = new Bundle();
       // args.putString("key1", mHaoName);
        args.putString("key2", mHaoLocation);
       // args.putString("key3", mHaoDescription);
        args.putDouble("key4", mHaoLat);
        args.putDouble("key5", mHaoLong);
        newHao.setArguments(args);
        newHao.show(getSupportFragmentManager(), "tag");



        }



}

