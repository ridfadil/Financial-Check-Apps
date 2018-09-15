package dua.property.analisis.analisiproperty.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import dua.property.analisis.analisiproperty.R;
import dua.property.analisis.analisiproperty.activity.MainActivity;
import dua.property.analisis.analisiproperty.model.ModelMenu;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ListMenuViewHolder> {

    //deklarasi global variabel
    private Context context;
    private final LinkedList<ModelMenu> listMenu;
    int jumlah ;
    int result = 0;

    //konstruktor untuk menerima data adapter
    public IncomeAdapter(Context context, LinkedList<ModelMenu> listMenu) {
        this.context = context;
        this.listMenu = listMenu;
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
        final ModelMenu mCurrent = listMenu.get(position);
        holder.judul.setText(mCurrent.getJudul());
        holder.harga.setText(mCurrent.getHarga());
        holder.imgMenu.setImageResource(mCurrent.getImageMenu());
        holder.imgPen.setImageResource(mCurrent.getImagePencil());
    }

    //untuk menghitung jumlah data yang ada pada list
    @Override
    public int getItemCount() {
        return listMenu.size();
    }

    public class ListMenuViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        private TextView judul, harga,jumHarga;
        private ImageView imgMenu, imgPen;

        final IncomeAdapter mAdapter;

        //untuk casting view yang digunakan pada list item
        public ListMenuViewHolder(View itemView, IncomeAdapter adapter) {
            super(itemView);
            judul = itemView.findViewById(R.id.tv_judul);
            harga = itemView.findViewById(R.id.tv_harga);
            imgMenu = itemView.findViewById(R.id.iv_menu);
            imgPen = itemView.findViewById(R.id.iv_pen);
            jumHarga =itemView.findViewById(R.id.tv_jumlah_aktf_income);

            this.mAdapter = adapter;
            //itemView.setOnClickListener(this);
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

            final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            harga.setText( editText.getText());
                            jumlah = Integer.valueOf(editText.getText().toString());
                            //jumHarga.setText(editText.getText());
                            //String jumlahTotal = editText.getText().toString();
  /*                          Intent intent = new Intent("toActivity");
                            //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                            intent.putExtra("jumlah",jumlahTotal);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
                        }
                    })
                    .setNegativeButton("Batal",
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
