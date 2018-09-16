package org.properti.analisa.financialcheck.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.LinkedList;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.adapter.IncomeAdapter;
import org.properti.analisa.financialcheck.adapter.MonthlySpendingAdapter;
import org.properti.analisa.financialcheck.adapter.PassiveIncomeAdapter;
import org.properti.analisa.financialcheck.model.ModelMenu;

public class SpendingMothActivity extends AppCompatActivity {

    private final LinkedList<ModelMenu> listMenu = new LinkedList<>();

    private RecyclerView mRecyclerView;
    private MonthlySpendingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending_moth);
        toolbar();

        listMenu.addLast(new ModelMenu(getString(R.string.makan_dalam_rumah),getString(R.string.harga), R.drawable.profesi, R.drawable.bluepen));
        listMenu.addLast(new ModelMenu(getString(R.string.listrik_gas_air),getString(R.string.harga), R.drawable.trading, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.telepon_rumah),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.telepon_hp),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.sekolah_les_anak),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.cicilan_hutang_rumah),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.cicilan_kendaraan),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.cicilan_kartut_kredit),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.asuransi),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.pembantu),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.mobil_bensin),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.pakaian),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.lainlain),getString(R.string.harga), R.drawable.belihandphone, R.drawable.redpen));


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_month_spending);

        mAdapter = new MonthlySpendingAdapter(SpendingMothActivity.this, listMenu);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        getSupportActionBar().setTitle("Monthly Spending");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
