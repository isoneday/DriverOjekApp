package com.isoneday.driverojekapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.isoneday.driverojekapp.helper.HeroHelper;
import com.isoneday.driverojekapp.helper.LocationMonitoringService;
import com.isoneday.driverojekapp.helper.SessionManager;
import com.isoneday.driverojekapp.model.ResponseDetailDriver;
import com.isoneday.driverojekapp.model.ResponseHistoryRequest;
import com.isoneday.driverojekapp.network.InitRetrofit;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalamanUtamaActivity extends AppCompatActivity {

    private static final String TAG = "testlocation";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 323;
    @BindView(R.id.testlocation)
    TextView testlocation;
    private Timer time;
    private SessionManager session;
    boolean mAlreadyStartedService =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
        ButterKnife.bind(this);
        session = new SessionManager(this);
        time = new Timer();
        if (session.getGcm().isEmpty()) {
            String token = FirebaseInstanceId.getInstance().getToken();
            session.setGcm(token);
            inserttoken(token);
        }
        setLocationDriver();

    }

    private void setLocationDriver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                        sendLocation(latitude, longitude);
                        if (latitude!=null&&longitude!=null){
                            testlocation.setText("latitude : "+latitude+"\n longitude:"+longitude);
                        }
                    }
                },new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );
    }

    private void sendLocation(String latitude, String longitude) {
        String token = session.getToken();
        String device = HeroHelper.getDeviceUUID(this);
        String iddriver = session.getIdUser();
        InitRetrofit.getInstance().sendposition(latitude,longitude,iddriver,device,token)
                .enqueue(new Callback<ResponseHistoryRequest>() {
                    @Override
                    public void onResponse(Call<ResponseHistoryRequest> call, Response<ResponseHistoryRequest> response) {
                        if (response.isSuccessful()){
                            String result = response.body().getResult();
                            String msg = response.body().getMsg();
                            if (result.equals("true")) {
                                Toast.makeText(HalamanUtamaActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(HalamanUtamaActivity.this, msg, Toast.LENGTH_SHORT).show();

                            }
                            }

                    }

                    @Override
                    public void onFailure(Call<ResponseHistoryRequest> call, Throwable t) {
                        Toast.makeText(HalamanUtamaActivity.this, "cek koneksi"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void inserttoken(String token) {
        int iduser = Integer.parseInt(session.getIdUser());
        InitRetrofit.getInstance().insertToken(iduser, token).enqueue(
                new Callback<ResponseDetailDriver>() {
                    @Override
                    public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                        if (response.isSuccessful()) {
                            String msg = response.body().getMsg();
                            String result = response.body().getResult();
                            if (result.equals("true")) {
                                Toast.makeText(HalamanUtamaActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HalamanUtamaActivity.this, msg, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {

                    }
                }
        );

    }

    public void onHistory(View view) {
        startActivity(new Intent(this, HistoryBookingActivity.class));
    }


    public void onGoride(View view) {
        startActivity(new Intent(this, GorideActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //untuk memilih item yang ada di menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) {
            startActivity(new Intent(this, HistoryBookingActivity.class));
        } else if (id == R.id.profil) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Keluar ?");
            builder.setMessage("apakah anda yakin logout aplikasi ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    session.logout();
                    startActivity(new Intent(HalamanUtamaActivity.this, LoginRegisterActivity.class));
                    finish();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //cek status playservice tersedia atau tidak

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                cekplayservice();
            }
        },0,3000);
    }


    @Override
    protected void onPause() {
        super.onPause();
        time.cancel();
    }

    private void cekplayservice() {
        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {
        DialogInterface dialogInterface = null;
            //Passing null to indicate that it is executing for the first time.
            cekkoneksidevice(dialogInterface);

        } else {
            Toast.makeText(getApplicationContext(), "playserc", Toast.LENGTH_LONG).show();
        }
    }
        //check koneksi internet
    private boolean cekkoneksidevice(DialogInterface dialogInterface) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialogInterface != null) {
            dialogInterface.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            aktifkanservice();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    private void aktifkanservice() {
        if (!mAlreadyStartedService && testlocation != null) {

            testlocation.setText("service start");

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    //ketika tidak ada koneksi internet
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HalamanUtamaActivity.this);
        builder.setTitle("no internet");
        builder.setMessage("no internet");

        String positiveText = "refresh";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (cekkoneksidevice(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                aktifkanservice();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.resionable,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HalamanUtamaActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HalamanUtamaActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(int resionable, int ok, View.OnClickListener onClickListener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(resionable),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(ok), onClickListener).show();
    }

    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;


    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this,LocationMonitoringService.class));
        mAlreadyStartedService =false;
    }
}
