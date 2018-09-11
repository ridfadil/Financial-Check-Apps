package dua.property.analisis.analisiproperty.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import dua.property.analisis.analisiproperty.R;
import dua.property.analisis.analisiproperty.model.ModelMenu;

public class SpendingAdapter extends RecyclerView.Adapter<SpendingAdapter.ListMenuViewHolder> {

    //deklarasi global variabel
    private Context context;
    private final LinkedList<ModelMenu> listMenu;

    //konstruktor untuk menerima data adapter
    public SpendingAdapter(Context context, LinkedList<ModelMenu> listMenu) {
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

    public class ListMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView judul,harga;
        private ImageView imgMenu,imgPen;

        final SpendingAdapter mAdapter;

        //untuk casting view yang digunakan pada list item
        public ListMenuViewHolder(View itemView, SpendingAdapter adapter) {
            super(itemView);
            judul = itemView.findViewById(R.id.tv_judul);
            harga = itemView.findViewById(R.id.tv_harga);
            imgMenu = itemView.findViewById(R.id.iv_menu);
            imgPen = itemView.findViewById(R.id.iv_pen);

            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        //untuk menambah action click pada list item
        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            ModelMenu element = listMenu.get(mPosition);

            //intent ke main activity dengan passing data
          /*  Intent i = new Intent(context, CounterActivity.class);
            i.putExtra("namTimSatu", element.getTimSatu());
            i.putExtra("namaTimDua", element.getTimDua());
            i.putExtra("logoTimSatu", element.getLogoTimSatu());
            i.putExtra("logoTimDua", element.getLogoTimDua());
            context.startActivity(i);*/
            mAdapter.notifyDataSetChanged();
        }
    }
}


