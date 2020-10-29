package com.Velocate.getaccesstolocationservices_mapversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String NUMBER = "##########"; //Dummy "phone number"
    private static final int REQUEST_PHONE_CALL = 1; //Request constant to make phone call
    private static final int REQUEST_ENABLE_GPS = 2; //Request constant to enable GPS
    private static final int REQUEST_ENABLE_LOCATION = 3; //Request constant to grant location services permission

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Dialog dialog;
    Button button3, button5, butonete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        button3 = findViewById(R.id.button3);
        button5 = findViewById(R.id.button5); //Custom refresh button
        dialog = new Dialog(this); //Dialog for popup when button3 is pressed

        getLocationPermission(); //Request permission for accessing Location services
        isMapsAndNetworkEnabled(); //User accesses Wifi and Location setting in order

        //Toolbar implementation
        Toolbar toolbar = findViewById(R.id.toolbarus);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backresized);
        //"Back" button implementation using intent
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Retrieve current location of device when refresh button is pressed
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 10; i++) {
                    getLastLocation();
                }
            }
        });
    }

    //Method for implementing popup window
    public void onButtonShowPopupWindowClick(View view) {
        TextView idb;
        //Inflate popup window layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View popupView = inflater.inflate(R.layout.custom_popup_phone, null);
        //Popup window is created
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false); //If you press outside of the popup window nothing happens
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        idb = popupView.findViewById(R.id.b);
        butonete = popupView.findViewById(R.id.butonete);

        idb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        butonete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }

    public void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            String dial = "tel:" + NUMBER;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    //Method used for retrieving current location of device (last recorded location)
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("ShowToast")
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map); //GoogleMap id is the one from corresponding activity_maps.xml file
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                } else {
                    Toast.makeText(MapsActivity.this, "No Location recorded", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    //This method is called when the map exists
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.clear();
        }
        assert googleMap != null;
        googleMap.setInfoWindowAdapter(new MapMarkerInfoWindow(MapsActivity.this)); //Info window (which appears above map marker) is set using MapMarkerInfoWindow class
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()); //Object with device current location created using getLatitude and getLongitude functions
        Geocoder geocoder = new Geocoder(getApplicationContext()); //geocoder is an object of Geocoder class used for retrieving an address from the current device location using latitude and longitude functions
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true); //Adds a blue dot indicating the current device location. This helps the marker to move to the correct location
            List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            String title = "Your Location:"; //Title of info window
            String snippet = "\n";
            snippet += addressList.get(0).getAddressLine(0) + "\n"; //Snippet of info window
            snippet += "\n" + "Remember this location for the phone call."; //Snippet of info window
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).snippet(snippet); //Marker position, title and snippet are set
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_mini)); //Setting the marker icon
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.1f)); //Zooming in on marker
            googleMap.addMarker(markerOptions); //Adding the previously created marker to the map
            Marker marker = googleMap.addMarker(markerOptions.position(latLng)); //Creating object marker from Marker class using the characteristics previously defined
            marker.showInfoWindow(); //Adding the info window to the marker
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method used for enabling Location setting if GPS is not enabled
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS is turned off. Please enable it to continue.") //Alert message
                .setTitle("GSP Disabled") //Alert title
                .setCancelable(false)
                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, REQUEST_ENABLE_GPS); //User is sent to Location settings on device via intent if positive button is pressed
                        for(int i=0; i<10; i++) { //Adding a for loop seems to improve the functionality of the app
                            getLastLocation(); //getLastLocation method is called to retrieve current device location
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent back = new Intent(MapsActivity.this, MainActivity.class);
                        startActivity(back); //Back to MainActivity because location wasn't enabled
                    }
                });
        final AlertDialog alert = builder.create();
        //Code below is used for changing color of buttons
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF020101")); //Black
                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4")); //Blue
            }
        });
        alert.show();
    }

    //Method used for enabling Wifi connection if no network connection exists
    private void buildAlertMessageNoNetwork() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        if(!mobileNetwork()) {
            builder.setMessage("Your network is disabled. Please enable it to continue.") //Alert message
                    .setTitle("\n" + "Network is disabled") //Alert title
                    .setCancelable(false)
                    .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            Intent enableInternet = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(enableInternet); //User is sent to WiFi settings on device via intent
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent back = new Intent(MapsActivity.this, MainActivity.class);
                            startActivity(back); //Back to MainActivity because network connection wasn't enabled
                        }
                    });
            final android.app.AlertDialog alert = builder.create();
            //code below is used for changing color of buttons
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF020101")); //Black
                    alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4")); //Blue
                }
            });
            alert.show();
        }
    }

    //Method for determining whether mobile network connection is enabled
    private boolean mobileNetwork() {
        boolean mobileConnected = false;
        final ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("mobile"))
                if(info.isConnected())
                    mobileConnected = true;
        }
        return mobileConnected;
    }

    //Method for setting the order in which the user accesses Wifi and Location settings
    public void isMapsAndNetworkEnabled() {
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { //If GPS is not enabled,
            buildAlertMessageNoGps(); ///////////////////////////////////////////user is sent to Location settings using this buildAlertMessageNoGPS.
            if (!wifiManager.isWifiEnabled()) { /////////////////////////////////If WiFi is not enabled,
                buildAlertMessageNoNetwork(); ///////////////////////////////////then the user must go to settings to enable WiFi using buildAlertMessageNoNetwork.
                getLastLocation(); ////////////////////////////////////////////When a network connection exists and GPS is on, device current location is retrieved.
            }
        }
        else if (!wifiManager.isWifiEnabled()) { ///////////////////////////////If GPS is enabled but WiFi is not enabled,
                buildAlertMessageNoNetwork(); //////////////////////////////////then the user must go to settings to enable WiFi using buildAlertMessageNoNetwork.
                getLastLocation(); ///////////////////////////////////////////When a network connection exists and GPS is on, device current location is retrieved.
            }
        else{
                getLastLocation(); ///////////////////////////////////////////If a network connection exists and GPS is on, device current location is retrieved.
            }
        }

    //Method used for requesting permission to use Location Services (first runtime)
    private void getLocationPermission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
        }
    }

    //If requestCode coincides with constant REQUEST_ENABLE_GPS and if
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastLocation();
    }
}
