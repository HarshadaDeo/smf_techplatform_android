package com.mv.Activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mv.Model.Salary;
import com.mv.Model.User;
import com.mv.R;
import com.mv.Retrofit.ApiClient;
import com.mv.Retrofit.ServiceRequest;
import com.mv.Utils.Constants;
import com.mv.Utils.LocaleManager;
import com.mv.Utils.PreferenceHelper;
import com.mv.Utils.Utills;
import com.mv.databinding.ActivitySalaryDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalaryDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Salary salary;
    private PreferenceHelper preferenceHelper;
    private ActivitySalaryDetailBinding binding;
    public static final String MESSAGE_PROGRESS = "message_progress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_salary_detail);
        binding.setActivity(this);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        salary = (Salary) getIntent().getSerializableExtra(Constants.SALARY);
        setActionbar("Month :- " + salary.getMonth());
        preferenceHelper = new PreferenceHelper(this);

        initView();
    }

    private void initView() {
        binding.txtPayslipTitle.setText(String.format("Payslip for the Month of %s", salary.getMonth()));
        binding.txtTelephone.setText(salary.getTelephone_Expense__c());
        binding.txtNetSalary.setText(salary.getNet_Salary__c());

        binding.txtEmployeeNo.setText(User.getCurrentUser(this).getExtendedUser().getEmployee_Id__c());
        binding.txtBankAcc.setText(User.getCurrentUser(this).getExtendedUser().getBank_Account_Number__c());
        binding.txtPfNo.setText(User.getCurrentUser(this).getExtendedUser().getPF_Number__c());
        binding.txtUAN.setText(User.getCurrentUser(this).getExtendedUser().getUAN_Number__c());

        binding.txtAbsentDays.setText(salary.getAbsent_Days__c());
        binding.txtArrears.setText(salary.getArrears__c());

        binding.txtSecurity.setText(salary.getSecurity_Fund__c());
        binding.txtSpecial.setText(salary.getSpecial_Allowance__c());
        binding.txtTDS.setText(salary.getTDS__c());
        binding.txtTotalPresenntDays.setText(salary.getPresent_Days__c());

        binding.txtUnpaidLeave.setText(salary.getUnpaid_Leaves__c());

        binding.txtDeductions.setText(salary.getTotal_Deductions__c());
        binding.txtGross.setText(salary.getGross_Earning_for_the_Month__c());
        binding.txtMedical.setText(salary.getMedical_Allowance__c());
        binding.txtName.setText(salary.getName());
        binding.txtHRA.setText(salary.getHRA__c());

        binding.txtOther.setText(salary.getOther_Deductions__c());
        binding.txtPaidLeave.setText(salary.getPaid_Leaves__c());
        binding.txtPerks.setText(salary.getPerks__c());
        binding.txtPF.setText(salary.getProvident_Fund__c());
        binding.txtProfession.setText(salary.getProfession_Tax__c());
        binding.txtSalaryAdvance.setText(salary.getSalary_Advance__c());

        binding.txtConsolidated.setText(salary.getConsolidated_Basic__c());
        binding.txtConveyance.setText(salary.getConveyance_Allowance__c());

        binding.txtHouserent.setText(salary.getHouse_Rent__c());
        binding.txtTravelExp.setText(salary.getTravelling_Exp__c());
        binding.txtTeleExp.setText(salary.getTelephone_Expense__Allowance());
        binding.txtsecurityfund.setText(salary.getSecurity_Fund__Allowance());

        if (salary.getSecurity_Fund__Allowance() == null) {
            binding.txtsecurityfund.setText("0.0");
        } else {
            binding.txtsecurityfund.setText(salary.getSecurity_Fund__Allowance());
        }

        binding.txtAppointAllow.setText(salary.getAppontment_Allowance__c());
        if (salary.getAny_other__c() == null) {
            binding.txtAnyOthers.setText("0.0");
        } else {
            binding.txtAnyOthers.setText(salary.getAny_other__c());
        }

        binding.txtTotalreimbursement.setText(salary.getTotal_Reimbursement__c());
        binding.txtNetBankAmount.setText(salary.getTotal_Amount_to_Bank_Net_Salary_Reimbur__c());
        binding.txtLeaveDeductionAmt.setText(salary.getTotal_Leave_Deduction__c());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;

            case R.id.img_logout:
                sendSalaryPDF_Email();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    private void setActionbar(String title) {
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        String str = title;
        if (title != null && title.contains("\n")) {
            str = title.replace("\n", " ");
        }
        toolbar_title.setText(str);

        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(this);

        ImageView img_list = (ImageView) findViewById(R.id.img_list);
        img_list.setVisibility(View.GONE);

        ImageView img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setImageResource(R.drawable.downloadfile);
        img_logout.setVisibility(View.VISIBLE);
        img_logout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendSalaryPDF_Email() {
        if (Utills.isConnected(this)) {
            try {
                Utills.showProgressDialog(this);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("salaryId", salary.getId());
                ServiceRequest apiService = ApiClient.getClientWitHeader(this).create(ServiceRequest.class);
                JsonParser jsonParser = new JsonParser();
                JsonObject gsonObject = (JsonObject) jsonParser.parse(jsonObject.toString());

                apiService.sendDataToSalesforce(preferenceHelper.getString(PreferenceHelper.InstanceUrl)
                        + "/services/apexrest/SendSalarySlipPDFOverEmail", gsonObject).enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Utills.hideProgressDialog();
                        try {
                            if (response.body() != null) {
                                if (response.isSuccess()) {
                                    String data = response.body().string();
                                    if (data.length() > 0) {
                                        JSONObject object = new JSONObject(data);
                                        Utills.showToast(object.getString("Status"), SalaryDetailActivity.this);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Utills.hideProgressDialog();
                            Utills.showToast(getResources().getString(R.string.error_something_went_wrong), SalaryDetailActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utills.hideProgressDialog();
                        Utills.showToast(getResources().getString(R.string.error_something_went_wrong), SalaryDetailActivity.this);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Utills.hideProgressDialog();
                Utills.showToast(getResources().getString(R.string.error_something_went_wrong), SalaryDetailActivity.this);
            }
        } else {
            Utills.showToast(getResources().getString(R.string.error_no_internet), SalaryDetailActivity.this);
        }
    }
}
