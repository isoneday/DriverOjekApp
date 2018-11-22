package com.isoneday.driverojekapp.helper;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isoneday.driverojekapp.DetailRequestActivity;
import com.isoneday.driverojekapp.R;
import com.isoneday.driverojekapp.model.DataHistory;
import com.isoneday.driverojekapp.model.DataRequestHistory;

import java.util.List;


/**
 * Created by nandoseptianhusni on 8/30/17.
 */

//ini class untuk memindahkan data ke recylerview dan juga custom recylerview
public class CustomRecycler extends RecyclerView.Adapter<CustomRecycler.MyHolder> {

    List<DataRequestHistory> data;
    FragmentActivity c;
    int status;


    public CustomRecycler(List<DataRequestHistory> data, FragmentActivity c, int idstatus) {
        this.data = data;
        this.c = c;
        status = idstatus;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(c).inflate(R.layout.custom_recyclerview, parent, false);

        return new MyHolder(inflater);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.texttgl.setText(data.get(position).getBookingTanggal());
        holder.txtawal.setText(data.get(position).getBookingFrom());
        holder.txtakhir.setText(data.get(position).getBookingTujuan());
        holder.txtharga.setText(data.get(position).getBookingBiayaUser());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==1){
                    Intent i = new Intent(c,DetailRequestActivity.class);
                    i.putExtra(MyContants.INDEX,position);
                        c.startActivity(i);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        TextView texttgl;

        TextView txtawal;

        TextView txtakhir;

        TextView txtharga;

        public MyHolder(View itemView) {
            super(itemView);

            texttgl = (TextView) itemView.findViewById(R.id.texttgl);
            txtawal = (TextView) itemView.findViewById(R.id.txtawal);
            txtakhir = (TextView) itemView.findViewById(R.id.txtakhir);
            txtharga = (TextView) itemView.findViewById(R.id.txtharga);


        }
    }


}
