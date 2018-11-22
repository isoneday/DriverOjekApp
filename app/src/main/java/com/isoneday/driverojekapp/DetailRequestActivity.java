package com.isoneday.driverojekapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.isoneday.driverojekapp.helper.DirectionMapsV2;
import com.isoneday.driverojekapp.helper.MyContants;
import com.isoneday.driverojekapp.helper.SessionManager;
import com.isoneday.driverojekapp.model.DataHistory;
import com.isoneday.driverojekapp.model.DataRequestHistory;
import com.isoneday.driverojekapp.model.Distance;
import com.isoneday.driverojekapp.model.Duration;
import com.isoneday.driverojekapp.model.LegsItem;
import com.isoneday.driverojekapp.model.ResponseWaypoint;
import com.isoneday.driverojekapp.model.RoutesItem;
import com.isoneday.driverojekapp.network.InitRetrofit;
import com.isoneday.driverojekapp.network.RestApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.txtiduser)
    TextView txtiduser;
    @BindView(R.id.requestFrom)
    TextView requestFrom;
    @BindView(R.id.requestTo)
    TextView requestTo;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.requestWaktu)
    TextView requestWaktu;
    @BindView(R.id.requestTarif)
    TextView requestTarif;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.requestNama)
    TextView requestNama;
    @BindView(R.id.requestEmail)
    TextView requestEmail;
    @BindView(R.id.requestID)
    TextView requestID;
    @BindView(R.id.requestTakeBooking)
    Button requestTakeBooking;
    private int data;
    private SessionManager session;
    private DataRequestHistory detaildata;
    private GoogleMap mmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);
        data = getIntent().getIntExtra(MyContants.INDEX,0);
        detaildata = HistoryFragment.datahistory.get(data);
        session = new SessionManager(this);
        detailrequest();
    }

    private void detailrequest() {
        requestFrom.setText("dari :"+detaildata.getBookingFrom());
        requestTo.setText("tujuan :"+detaildata.getBookingTujuan());
        requestTarif.setText("tarif :"+detaildata.getBookingBiayaUser());
        requestWaktu.setText("jarak :"+detaildata.getBookingJarak());
        requestNama.setText("nama :"+detaildata.getUserNama());
        requestEmail.setText("email :"+detaildata.getUserEmail());

    }

    @OnClick(R.id.requestTakeBooking)
    public void onViewClicked() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    mmap = googleMap;
    actionrute();
    mmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="
             +detaildata.getBookingTujuanLat()+","+detaildata.getBookingTujuanLng()));
            startActivity(i);
        }
    });
    }

    private void actionrute() {
//get koordinat
        String origin = String.valueOf(detaildata.getBookingFromLat()) + "," + String.valueOf(detaildata.getBookingFromLng());
        String desti = String.valueOf(detaildata.getBookingTujuanLat()) + "," + String.valueOf(detaildata.getBookingTujuanLng());


        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(Double.parseDouble(detaildata.getBookingFromLat()), Double.parseDouble(detaildata.getBookingFromLng())));
        bound.include(new LatLng(Double.parseDouble(detaildata.getBookingTujuanLat()), Double.parseDouble(detaildata.getBookingTujuanLng())));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));
        LatLngBounds bounds = bound.build();
// begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
// end of new code

        mmap.animateCamera(cu);

        RestApi service = InitRetrofit.getInstanceGoogle();
        String api = "AIzaSyAFtiiTldGfM4y7LryTZY6A2BzAN_yxGqo";
        Call<ResponseWaypoint> call = service.getrutelokasi(origin, desti, api);
        call.enqueue(new retrofit2.Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
                List<RoutesItem> routes = response.body().getRoutes();
                List<LegsItem> legsItems = routes.get(0).getLegs();
                Distance distance = legsItems.get(0).getDistance();
                Duration duration = legsItems.get(0).getDuration();
                String jarak = distance.getText();
                double valuejarak = Double.valueOf(distance.getValue());
                String waktu = duration.getText();
                String points = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                DirectionMapsV2 direction = new DirectionMapsV2(DetailRequestActivity.this);
                direction.gambarRoute(mmap, points);
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }
}
