package com.isoneday.driverojekapp.helper;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PositionHelper extends Service {
    private Context context = null;
    boolean isGPSEnabled = false, isNetworkEnabled = false,
            canGetLocation = false;
    Location location = null;
    double latitude, longitude;
    private static final long MIN_DISTANCE = 0; // 10 meter
    private static final long MIN_TIME = 3000; // detik

    protected LocationManager locationManager;
    public static final String NEW_POSITION = "newPosition";
    private SessionManager sesi;



    @Override
    public void onCreate() {
        super.onCreate();

        sesi = new SessionManager(this);

        context = this;
        getLocation();
    }



    private Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting gps status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //if (!isGPSEnabled && !isNetworkEnabled) {
            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingGps();
            } else {
                canGetLocation = true;
                // get lat/lng by network
                if (isNetworkEnabled) {

                    if (checkPermission(context)) {


                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, MIN_TIME,
                                MIN_DISTANCE, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {

                                    }

                                    @Override
                                    public void onStatusChanged(String s, int i, Bundle bundle) {

                                    }

                                    @Override
                                    public void onProviderEnabled(String s) {

                                    }

                                    @Override
                                    public void onProviderDisabled(String s) {

                                    }
                                });
                        Log.d("network", "network enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    } else {
                        HeroHelper.pesan(context, "Permission for GPS not valid");
                    }
                }

                // get lat/lng by gps
                if (isGPSEnabled) {
                    if (location == null) {
                        if (checkPermission(context)) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, MIN_TIME,
                                    MIN_DISTANCE, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {

                                            Double lat = location.getLatitude();
                                            Double lon = location.getLongitude();

                                            sendPosisi(lat,lon);


                                        }

                                        @Override
                                        public void onStatusChanged(String s, int i, Bundle bundle) {

                                        }

                                        @Override
                                        public void onProviderEnabled(String s) {

                                        }

                                        @Override
                                        public void onProviderDisabled(String s) {

                                        }
                                    });
                            Log.d("GPS", "GPS enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }

                            }
                        } else {
                            HeroHelper.pesan(context, "Permission for GPS not valid");
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;

    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public Location getLocations() {
        if (location != null) {
            return location;
        }

        return null;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingGps() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("GPS Setting");
        alertBuilder.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertBuilder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertBuilder.show();
    }

//	@Override
//	public void onLocationChanged(Location location) {
//		if (location != null) {
//			//NurHelper.pesan(context, "perubahan Alamat");
//
//
//			if (this.location != location) {
//				sendPosisi(location.getLatitude(), location.getLongitude());
//				this.location = location;
//			}
//
//
//		}
//
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//
//	}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    public void sendPosisi(double lat, double lng) {

//        InitLibrary.getInstance().request_posisi(sesi.getIdUser(),String.valueOf(lat),String.valueOf(lng)).enqueue(new Callback<ResponseDaftar>() {
//            @Override
//            public void onResponse(Call<ResponseDaftar> call, Response<ResponseDaftar> response) {
//                Log.d("reesponse service" , response.message());
//
//                String result = response.body().getResult();
//                String msg =  response.body().getMsg();
//                if(response.isSuccessful()){
//                 if (result.equals("true")){
//                    HeroHelper.pesan(context,msg);
//                  }
//}
//            }
//
//            @Override
//            public void onFailure(Call<ResponseDaftar> call, Throwable t) {
//                Log.d("reesponse service" , t.getMessage());
//
//            }
//        });



    }


    public static boolean checkPermission(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }


}
