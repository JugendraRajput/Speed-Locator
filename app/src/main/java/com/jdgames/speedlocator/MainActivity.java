package com.jdgames.speedlocator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.skyfishjy.library.RippleBackground;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView bottomTextView, speedTextView, kmTextView;
    MaterialButton button;

    String latitude = "";
    String longitude = "";
    String address = "";

    int REQUEST_LOCATION_PERMISSION = 101;
    int REQUEST_LOCATION_PERMISSION_REQUIRED = 102;
    int OPEN_APP_SETTINGS = 103;
    int OPEN_APP_SETTINGS_REQUIRED = 104;
    int OPEN_GPS_SETTINGS = 105;
    int OPEN_GPS_SETTINGS_REQUIRED = 106;

    Activity activity = MainActivity.this;
    RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomTextView = findViewById(R.id.textView);
        speedTextView = findViewById(R.id.speedTextView);
        kmTextView = findViewById(R.id.kmTextView);
        button = findViewById(R.id.button);

        rippleBackground = findViewById(R.id.ripple_animation);

        button.setOnClickListener(view -> {
            button.setEnabled(false);
            CheckPermission(1);
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                UpdateLocationInfo(location);
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

        CheckPermission(0);
    }

    public void CheckPermission(int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(activity, "Please enable location permission", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                }
            }
            if (requestCode == 1) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(activity, "Please enable location permission", Toast.LENGTH_SHORT).show();
                    Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                    myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(myAppSettings, OPEN_APP_SETTINGS_REQUIRED);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_REQUIRED);
                }
            }
        } else {
            if (requestCode == 0) {
                EnableGPS(OPEN_GPS_SETTINGS);
            }
            if (requestCode == 1) {
                EnableGPS(OPEN_GPS_SETTINGS_REQUIRED);
            }
        }
    }

    private void EnableGPS(int requestCode) {
        LocationRequest locationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setWaitForAccurateLocation(false)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        com.google.android.gms.tasks.Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, locationSettingsResponse -> getLocation());

        task.addOnFailureListener(activity, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(activity, requestCode);
                } catch (IntentSender.SendIntentException ignored) {
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        rippleBackground.startRippleAnimation();
        button.setEnabled(false);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @SuppressLint("SetTextI18n")
    public void UpdateLocationInfo(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        Log.d("TAG", "latitude: " + latitude);
        Log.d("TAG", "longitude: " + longitude);
        speedTextView.setText(String.format("%s", location.getSpeed()));
        kmTextView.setVisibility(View.VISIBLE);
        if (location.getSpeed() <= 1) {
            speedTextView.setTextColor(getResources().getColor(R.color.c1));
        } else {
            if (location.getSpeed() <= 3) {
                speedTextView.setTextColor(getResources().getColor(R.color.c2));
            } else {
                if (location.getSpeed() <= 5) {
                    speedTextView.setTextColor(getResources().getColor(R.color.c3));
                } else {
                    if (location.getSpeed() <= 8) {
                        speedTextView.setTextColor(getResources().getColor(R.color.c4));
                    } else {
                        if (location.getSpeed() <= 10) {
                            speedTextView.setTextColor(getResources().getColor(R.color.c5));
                        } else {
                            if (location.getSpeed() <= 15) {
                                speedTextView.setTextColor(getResources().getColor(R.color.c6));
                            } else {
                                if (location.getSpeed() <= 20) {
                                    speedTextView.setTextColor(getResources().getColor(R.color.c7));
                                } else {
                                    if (location.getSpeed() <= 25) {
                                        speedTextView.setTextColor(getResources().getColor(R.color.c8));
                                    } else {
                                        if (location.getSpeed() <= 30) {
                                            speedTextView.setTextColor(getResources().getColor(R.color.c9));
                                        } else {
                                            if (location.getSpeed() <= 35) {
                                                speedTextView.setTextColor(getResources().getColor(R.color.c10));
                                            } else {
                                                if (location.getSpeed() <= 45) {
                                                    speedTextView.setTextColor(getResources().getColor(R.color.c11));
                                                } else {
                                                    if (location.getSpeed() <= 50) {
                                                        speedTextView.setTextColor(getResources().getColor(R.color.c12));
                                                    } else {
                                                        speedTextView.setTextColor(getResources().getColor(R.color.c13));
                                                        speedTextView.setText("GO Slow");
                                                        kmTextView.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            address = "";

            if (listAddresses != null && listAddresses.size() > 0) {

                if (listAddresses.get(0).getThoroughfare() != null) {
                    address += listAddresses.get(0).getThoroughfare() + ", ";
                }

                if (listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality() + ", ";
                }

                if (listAddresses.get(0).getAdminArea() != null) {
                    address += listAddresses.get(0).getAdminArea() + " - ";
                }

                if (listAddresses.get(0).getPostalCode() != null) {
                    address += listAddresses.get(0).getPostalCode();
                }
            }
            Log.d("TAG", "Address: " + address);
            bottomTextView.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EnableGPS(OPEN_GPS_SETTINGS);
            }
        }
        if (requestCode == REQUEST_LOCATION_PERMISSION_REQUIRED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EnableGPS(OPEN_GPS_SETTINGS_REQUIRED);
            } else {
                CheckPermission(1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_APP_SETTINGS) {
            Toast.makeText(activity, "Location Permission denied!", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == OPEN_APP_SETTINGS_REQUIRED) {
            CheckPermission(1);
        }
        if (requestCode == OPEN_GPS_SETTINGS) {
            if (resultCode == RESULT_OK) {
                getLocation();
            }
        }
        if (requestCode == OPEN_GPS_SETTINGS_REQUIRED) {
            if (resultCode == RESULT_OK) {
                getLocation();
            } else {
                EnableGPS(OPEN_GPS_SETTINGS_REQUIRED);
            }
        }
    }

}