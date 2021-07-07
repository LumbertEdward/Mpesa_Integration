package com.example.mpesaintegration;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mpesaintegration.Clients.DarajaApiClient;
import com.example.mpesaintegration.Clients.Utils;
import com.example.mpesaintegration.Constants.ConstantsClass;
import com.example.mpesaintegration.ModelClasses.AccessTokens;
import com.example.mpesaintegration.ModelClasses.STKPush;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private DarajaApiClient darajaApiClient;

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.GONE);

        darajaApiClient = new DarajaApiClient();
        darajaApiClient.setIsDebug(true);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = phone.getText().toString();
                String amountTot = amount.getText().toString();
                performSTKPush(phoneNo, amountTot);
            }
        });

        getAccessToken();
    }

    private void performSTKPush(String phoneNo, String amountTot) {
        progressBar.setVisibility(View.VISIBLE);
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                ConstantsClass.BUSINESS_SHORT_CODE,
                Utils.getPassword(ConstantsClass.BUSINESS_SHORT_CODE, ConstantsClass.PASSKEY, timestamp),
                timestamp,
                ConstantsClass.TRANSACTION_TYPE,
                String.valueOf(amountTot),
                Utils.sanitizePhoneNumber(phoneNo),
                ConstantsClass.PARTYB,
                Utils.sanitizePhoneNumber(phoneNo),
                ConstantsClass.CALLBACKURL,
                "Bakes Test",
                "Testing"
        );
        darajaApiClient.setGetAccessToken(false);
        darajaApiClient.stkPushService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(Call<STKPush> call, Response<STKPush> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Post submitted to API", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<STKPush> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Timber.e(t);

            }
        });
    }

    private void getAccessToken() {
        darajaApiClient.setGetAccessToken(true);
        darajaApiClient.stkPushService().getAccessTokens().enqueue(new Callback<AccessTokens>() {
            @Override
            public void onResponse(Call<AccessTokens> call, Response<AccessTokens> response) {
                if (response.isSuccessful()){
                    darajaApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(Call<AccessTokens> call, Throwable t) {

            }
        });
    }
}