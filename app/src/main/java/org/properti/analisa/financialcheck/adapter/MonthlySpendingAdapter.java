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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.utils.CurrencyEditText;

import java.util.LinkedList;

public class MonthlySpendingAdapter extends RecyclerView.Adapter<MonthlySpendingAdapter.ListMenuViewHolder> {

    //deklarasi global variabel
    private Context context;
    private final LinkedList<Common> listMenu;

    DatabaseReference dbSpendingMonth;

    int pos;
    String idUser;

    //konstruktor untuk menerima data adapter
    public MonthlySpendingAdapter(Context context, LinkedList<Common> listMenu, String idUser) {
        this.context = context;
        this.listMenu = listMenu;
        this.idUser = idUser;
    }

    //view holder berfungsi untuk setting list item yang digunakan
    @Override
    public ListMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_menu, null, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mItemView.setLayoutParams(layoutParams);

        return new ListMenuViewHolder(mItemView, this);
    }

    //bind view holder berfungsi untuk set data ke view yang ditampilkan pada list item
    @Override
    public void onBindViewHolder(ListMenuViewHolder holder, int position) {
        final Common mCurrent = listMenu.get(position);
        holder.judul.setText(mCurrent.getJudul());
        holder.harga.setText(CurrencyEditText.currencyFormatterLong(Long.parseLong(mCurrent.getHarga())));
        Glide.with(context).
                load(mCurrent.getImage()).
                placeholder(R.drawable.pengeluaranbulanan).
                into(holder.imgMenu);
        holder.imgPen.setImageResource(R.drawable.bluepen);
    }

    //untuk menghitung jumlah data yang ada pada list
    @Override
    public int getItemCount() {
        return listMenu.size();
    }

    public class ListMenuViewHolder extends RecyclerView.ViewHolder {
        private TextView judul, harga;
        private ImageView imgMenu, imgPen;

        final MonthlySpendingAdapter mAdapter;

        //untuk casting view yang digunakan pada list item
        public ListMenuViewHolder(View itemView, MonthlySpendingAdapter adapter) {
            super(itemView);
            judul = itemView.findViewById(R.id.tv_judul);
            harga = itemView.findViewById(R.id.tv_harga);
            imgMenu = itemView.findViewById(R.id.iv_menu);
            imgPen = itemView.findViewById(R.id.iv_pen);

            this.mAdapter = adapter;

            imgPen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowInputialog(v);
                }
            });

        }

        public void ShowInputialog(View view) {

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

            dbSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(idUser).child(listMenu.get(pos).getId());

            alertDialogBuilder.setCancelable(true)
                    .setPositiveButton(context.getString(R.string.simpan), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Common spendingMonth = new Common(etKeterangan.getText().toString(), etNominal.getText().toString().replace(".", ""), listMenu.get(pos).getImage());
                            spendingMonth.setId(listMenu.get(pos).getId());
                            dbSpendingMonth.setValue(spendingMonth);

                            judul.setText(etKeterangan.getText());
                            harga.setText(String.valueOf(CurrencyEditText.currencyFormatterLong(Long.parseLong(etNominal.getText().toString().replace(".", "")))));
                        }
                    })
                    .setNeutralButton(context.getString(R.string.hapus), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dbSpendingMonth.removeValue();

                            listMenu.remove(pos);
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(context.getString(R.string.batal), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }
}

