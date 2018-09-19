package org.properti.analisa.financialcheck.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.properti.analisa.financialcheck.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.tv_result_username)
    TextView tvResultUsername;
    @BindView(R.id.txt_financial_total)
    TextView txtFinancialTotal;
    @BindView(R.id.tv_financial_condition)
    TextView tvFinancialCondition;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.cv_share)
    LinearLayout cvShare;
    @BindView(R.id.rv_result)
    RecyclerView rvResult;
    @BindView(R.id.btn_home_result)
    Button btnHomeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_home_result)
    public void onViewClicked() {
    }
}
