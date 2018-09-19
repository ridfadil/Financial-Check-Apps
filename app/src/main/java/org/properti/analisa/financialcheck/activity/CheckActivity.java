package org.properti.analisa.financialcheck.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import org.properti.analisa.financialcheck.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckActivity extends AppCompatActivity {

    @BindView(R.id.rv_check)
    RecyclerView rvCheck;
    @BindView(R.id.btn_check)
    Button btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_check)
    public void onViewClicked() {
    }
}
