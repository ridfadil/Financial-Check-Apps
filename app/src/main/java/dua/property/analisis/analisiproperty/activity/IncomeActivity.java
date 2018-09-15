package dua.property.analisis.analisiproperty.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import dua.property.analisis.analisiproperty.R;
import dua.property.analisis.analisiproperty.adapter.IncomeAdapter;
import dua.property.analisis.analisiproperty.model.ModelMenu;

public class IncomeActivity extends AppCompatActivity {

    private final LinkedList<ModelMenu> listMenu = new LinkedList<>();

    private RecyclerView mRecyclerView;
    private IncomeAdapter mAdapter;
    TextView jumlah;
    String sJum,sHasil;
    int hasil=0 ,jum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        toolbar();
        init();
        //Intent dari Adapter
       // LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));

        listMenu.addLast(new ModelMenu(getString(R.string.Profesi),getString(R.string.harga), R.drawable.profesi, R.drawable.bluepen));
        listMenu.addLast(new ModelMenu(getString(R.string.Trading),getString(R.string.harga), R.drawable.trading, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.lainlain),getString(R.string.lainlain), R.drawable.lainlain, R.drawable.bluepen));

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_income);

        mAdapter = new IncomeAdapter(IncomeActivity.this, listMenu);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

   /* public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String ItemName = intent.getStringExtra("item");
            jum = Integer.valueOf(ItemName);
            hasil= hasil+jum;
            sHasil = Integer.toString(hasil);
            jumlah.setText(sHasil);
        }
    };*/

    public void init(){
        jumlah = findViewById(R.id.tv_jumlah_aktf_income);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Income");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
