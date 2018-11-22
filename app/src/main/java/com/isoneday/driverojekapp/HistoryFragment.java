package com.isoneday.driverojekapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.isoneday.driverojekapp.helper.CustomRecycler;
import com.isoneday.driverojekapp.helper.HeroHelper;
import com.isoneday.driverojekapp.helper.SessionManager;
import com.isoneday.driverojekapp.model.DataHistory;
import com.isoneday.driverojekapp.model.DataRequestHistory;
import com.isoneday.driverojekapp.model.ResponseHistory;
import com.isoneday.driverojekapp.model.ResponseHistoryRequest;
import com.isoneday.driverojekapp.network.InitRetrofit;
import com.isoneday.driverojekapp.network.RestApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HistoryFragment extends Fragment {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Unbinder unbinder;
    private SessionManager session;
    private String iduser;
    private String token;
    private String device;
    public static List<DataRequestHistory> datahistory;
    int idstatus ;

    @SuppressLint("ValidFragment")
    public HistoryFragment(int i) {
        idstatus =i;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tampilan = inflater.inflate(R.layout.fragment_proses, null);
        unbinder = ButterKnife.bind(this, tampilan);
        session = new SessionManager(getActivity());

        getdatahistory(idstatus);
        return tampilan;
    }

    private void getdatahistory(final int status) {
        iduser =session.getIdUser();
        token =session.getToken();
        device = HeroHelper.getDeviceUUID(getContext());

        RestApi api = InitRetrofit.getInstance();
        if (status==1){

            Call<ResponseHistoryRequest> historyCall  =api.getRequestHistory();
            historyCall.enqueue(new Callback<ResponseHistoryRequest>() {
                @Override
                public void onResponse(Call<ResponseHistoryRequest> call, Response<ResponseHistoryRequest> response) {
                    if (response.isSuccessful()){
                        String result = response.body().getResult();
                        String msg = response.body().getMsg();
                        if (result.equals("true")){
                            datahistory = response.body().getData();
                            CustomRecycler adapter = new CustomRecycler(datahistory,getActivity(),status);
                            recyclerview.setAdapter(adapter);
                            recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }else{
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryRequest> call, Throwable t) {

                }
            });
        }else if (status==2){

            Call<ResponseHistoryRequest> historyCall  =api.getHandleHistory(
                    device,token,iduser
            );
            historyCall.enqueue(new Callback<ResponseHistoryRequest>() {
                @Override
                public void onResponse(Call<ResponseHistoryRequest> call, Response<ResponseHistoryRequest> response) {
                    if (response.isSuccessful()){
                        String result = response.body().getResult();
                        String msg = response.body().getMsg();
                        if (result.equals("true")){
                            datahistory = response.body().getData();
                            CustomRecycler adapter = new CustomRecycler(datahistory,getActivity(),status);
                            recyclerview.setAdapter(adapter);
                            recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }else{
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryRequest> call, Throwable t) {

                }
            });
        }else{

            Call<ResponseHistoryRequest> historyCall  =api.getCompleteHistory(
                    device,token,iduser
            );
            historyCall.enqueue(new Callback<ResponseHistoryRequest>() {
                @Override
                public void onResponse(Call<ResponseHistoryRequest> call, Response<ResponseHistoryRequest> response) {
                    if (response.isSuccessful()){
                        String result = response.body().getResult();
                        String msg = response.body().getMsg();
                        if (result.equals("true")){
                            datahistory = response.body().getData();
                            CustomRecycler adapter = new CustomRecycler(datahistory,getActivity(),status);
                            recyclerview.setAdapter(adapter);
                            recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }else{
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryRequest> call, Throwable t) {

                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
