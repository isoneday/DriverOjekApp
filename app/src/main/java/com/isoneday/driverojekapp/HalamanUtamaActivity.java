package com.isoneday.driverojekapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.isoneday.driverojekapp.fcm.MyFirebaseInstanceIDService;
import com.isoneday.driverojekapp.fcm.MyFirebaseMessagingService;
import com.isoneday.driverojekapp.helper.SessionManager;
import com.isoneday.driverojekapp.model.ResponseDetailDriver;
import com.isoneday.driverojekapp.network.InitRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalamanUtamaActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
        session = new SessionManager(this);
        if (session.getGcm().isEmpty()){
            String token =FirebaseInstanceId.getInstance().getToken();
            session.setGcm(token);
            inserttoken(token);
        }
        Intent a = new Intent(this,MyFirebaseMessagingService.class);
        Intent c = new Intent(this,MyFirebaseInstanceIDService.class);
        startService(a);
        startService(c);
    }

    private void inserttoken(String token) {
        int iduser = Integer.parseInt(session.getIdUser());
        InitRetrofit.getInstance().insertToken(iduser,token).enqueue(
                new Callback<ResponseDetailDriver>() {
                    @Override
                    public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                        if (response.isSuccessful()){
                            String msg =response.body().getMsg();
                            String result =response.body().getResult();
                            if (result.equals("true")){
                                Toast.makeText(HalamanUtamaActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }else{
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
    startActivity(new Intent(this,HistoryBookingActivity.class));
    }


    public void onGoride(View view) {
        startActivity(new Intent(this,GorideActivity.class));

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
}
