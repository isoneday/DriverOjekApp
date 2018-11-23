package com.isoneday.driverojekapp.network;


import com.isoneday.driverojekapp.model.ResponseCheckBooking;
import com.isoneday.driverojekapp.model.ResponseDetailDriver;
import com.isoneday.driverojekapp.model.ResponseHistory;
import com.isoneday.driverojekapp.model.ResponseHistoryRequest;
import com.isoneday.driverojekapp.model.ResponseInsertBooking;
import com.isoneday.driverojekapp.model.ResponseLoginRegis;
import com.isoneday.driverojekapp.model.ResponseWaypoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {
//    //todo 2 set endpoint di api.php

    //    //endpoint untuk register
    @FormUrlEncoded
    @POST("daftar/22")
    Call<ResponseLoginRegis> registeruser(
            @Field("nama") String nama,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("email") String email
    );

    //
    //endpoint untuk login
    @FormUrlEncoded
    @POST("login_driver")
    Call<ResponseLoginRegis> loginuser(
            @Field("device") String device,
            @Field("f_password") String pass,
            @Field("f_email") String email);
//
    //endpoint untuk login
    @FormUrlEncoded
    @POST("insert_booking")
    Call<ResponseInsertBooking> insertbooking(
            @Field("f_idUser") int iduser,
            @Field("f_latAwal") String latawal,
            @Field("f_lngAwal") String longawal,
            @Field("f_awal") String awal,
            @Field("f_latAkhir") String latakhir,
            @Field("f_lngAkhir") String longakhir,
            @Field("f_akhir") String lokasitujuan,
            @Field("f_catatan") String catatan,
            @Field("f_jarak") float jarak,
            @Field("f_token") String token,
            @Field("f_device") String device
    );
//
//
    //endpoint untuk checkbooking
    @FormUrlEncoded
    @POST("checkBooking")
    Call<ResponseCheckBooking> checkbooking(
            @Field("idbooking") int idbooking);
//
    //endpoint untuk getdata history
    @FormUrlEncoded
    @POST("get_booking")
        Call<ResponseHistory> getdatahistory(
            @Field("f_idUser") int iduser,
            @Field("f_token") String token,
            @Field("f_device") String device,
            @Field("status") int status);
//
    //endpoint untuk chancelbooking
    @FormUrlEncoded
    @POST("cancel_booking")
    Call<ResponseLoginRegis> cancelbooking(
            @Field("idbooking") int idbooking,
            @Field("f_token") String token,
            @Field("f_device") String device);
//
    //endpoint ke api google map waypoint
    @GET("json")
    Call<ResponseWaypoint> getrutelokasi(
            @Query("origin") String alamatasal,
            @Query("destination") String alamattujuan,
            @Query("key") String key
    );
//
    //endpoint untuk get_driver
    @FormUrlEncoded
    @POST("get_driver")
    Call<ResponseDetailDriver> getdetaildriver(
            @Field("f_iddriver") int iddriver);
 //endpoint untuk insert_token
    @FormUrlEncoded
    @POST("registerGcm")
    Call<ResponseDetailDriver> insertToken(
            @Field("f_idUser") int iduser,
            @Field("f_gcm") String fcm);

    @FormUrlEncoded
    @POST("get_handle_booking")
    Call<ResponseHistoryRequest> getHandleHistory(
            @Field("f_device") String device,
            @Field("f_token") String token,
            @Field("f_idUser") String iddriver);

    @FormUrlEncoded
    @POST("get_complete_booking")
    Call<ResponseHistoryRequest> getCompleteHistory(
            @Field("f_device") String device,
            @Field("f_token") String token,
            @Field("f_idUser") String iddriver);

    @FormUrlEncoded
    @POST("take_booking")
    Call<ResponseHistoryRequest> take_booking(
            @Field("id") String idbooking,
            @Field("f_iddriver") String iddriver,
            @Field("f_device") String device,
            @Field("f_token") String token);

    @FormUrlEncoded
    @POST("insert_posisi")
    Call<ResponseHistoryRequest> sendposition(
            @Field("f_lat") String lat,
            @Field("f_lng") String longitude,
            @Field("f_idUser") String iddriver,
            @Field("f_device") String device,
            @Field("f_token") String token);

    @GET("get_request_booking")
    Call<ResponseHistoryRequest> getRequestHistory();


}