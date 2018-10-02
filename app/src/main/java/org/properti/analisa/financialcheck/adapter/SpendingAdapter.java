package org.properti.analisa.financialcheck.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.utils.CurrencyEditText;

import java.util.LinkedList;

public class SpendingAdapter extends RecyclerView.Adapter<SpendingAdapter.ListMenuViewHolder> {

    private Context context;
    private final LinkedList<Common> listMenu;

    DatabaseReference dbSpending;

    int pos;
    String idUser;

    public SpendingAdapter(Context context, LinkedList<Common> listMenu, String idUser) {
        this.context = context;
        this.listMenu = listMenu;
        this.idUser = idUser;
    }

    @Override
    public ListMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_menu, null, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mItemView.setLayoutParams(layoutParams);

        return new ListMenuViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(ListMenuViewHolder holder, int position) {
        final Common mCurrent = listMenu.get(position);
        holder.judul.setText(mCurrent.getJudul());
        holder.harga.setText(CurrencyEditText.currencyFormat(Long.parseLong(mCurrent.getHarga())));
        Glide.with(context).
                load(mCurrent.getImage()).
                placeholder(R.drawable.pengeluaranlainnya).
                into(holder.imgMenu);
        holder.imgPen.setImageResource(R.drawable.redpen);
    }

    @Override
    public int getItemCount() {
        return listMenu.size();
    }

    public class ListMenuViewHolder extends RecyclerView.ViewHolder {
        private TextView judul,harga;
        private ImageView imgMenu,imgPen;

        final SpendingAdapter mAdapter;

        private InterstitialAd interstitialAd;

        public ListMenuViewHolder(View itemView, SpendingAdapter adapter) {
            super(itemView);
            judul = itemView.findViewById(R.id.tv_judul);
            harga = itemView.findViewById(R.id.tv_harga);
            imgMenu = itemView.findViewById(R.id.iv_menu);
            imgPen = itemView.findViewById(R.id.iv_pen);
            this.mAdapter = adapter;

            MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getString(R.string.ad_id_interstitial));
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    showInputDialog();
                }
            });

            imgPen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition()==2){
                        if (interstitialAd != null && interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        }
                        else {
                            showInputDialog();
                        }
                    }
                    else {
                        showInputDialog();
                    }
                }
            });

        }

        public void showInputDialog() {

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptView);

            final EditText etKeterangan = (EditText) promptView.findViewById(R.id.et_keterangan);
            final EditText etNominal = (EditText) promptView.findViewById(R.id.et_nominal);

            new CurrencyEditText(etNominal);

            pos = getAdapterPosition();
            Common mCurrent = listMenu.get(pos);
            etKeterangan.setText(mCurrent.getJudul());
            etNominal.setText(mCurrent.getHarga());

            dbSpending = FirebaseDatabase.getInstance().getReference("spending").child(idUser).child(listMenu.get(pos).getId());

            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton(context.getString(R.string.simpan), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Common spending = new Common(etKeterangan.getText().toString(), ""+Long.parseLong(etNominal.getText().toString().replace(".", "")), listMenu.get(pos).getImage());
                            spending.setId(listMenu.get(pos).getId());
                            dbSpending.setValue(spending);

                            judul.setText(etKeterangan.getText());
                            harga.setText(String.valueOf(CurrencyEditText.currencyFormat(Long.parseLong(etNominal.getText().toString().replace(".", "")))));
                        }
                    })
                    .setNeutralButton(context.getString(R.string.hapus), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dbSpending.removeValue();

                            listMenu.remove(pos);
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(context.getString(R.string.batal),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

    }
}