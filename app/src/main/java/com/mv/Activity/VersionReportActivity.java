package com.mv.Activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mv.Adapter.VersionReportAdapter;
import com.mv.Model.LocationModel;
import com.mv.Model.Task;
import com.mv.Model.Template;
import com.mv.Model.User;
import com.mv.R;
import com.mv.Retrofit.ApiClient;
import com.mv.Retrofit.ServiceRequest;
import com.mv.Utils.Constants;
import com.mv.Utils.PreferenceHelper;
import com.mv.Utils.Utills;
import com.mv.databinding.ActivityVersionReportBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersionReportActivity extends AppCompatActivity implements View.OnClickListener {
    private PreferenceHelper preferenceHelper;
    private List<Template> processAllList = new ArrayList<>();
    private LocationModel locationModel;
    private ArrayList<Template> repplicaCahart = new ArrayList<>();
    private VersionReportAdapter mAdapter;
    private ActivityVersionReportBinding binding;
    private RecyclerView.LayoutManager mLayoutManager;
    private Activity context;
    private RelativeLayout mToolBar;
    private ImageView img_back, img_list, img_logout;
    private TextView toolbar_title;
    public static String approvalType, id, processTitle;
    String url;
    private TextView textNoData;
    private String title;
    private String processId;
    private Task task;
    private String roleList;
    private List<String> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_version_report);
        binding.setActivity(this);
        if(getIntent().getExtras()!=null) {
            locationModel = getIntent().getExtras().getParcelable(Constants.LOCATION);
        }
        initViews();

    }

    private void initViews() {

        task = new Task();
        roleList = "Teacher;HM;CRC;Prerak;TC;MT";
        title = getString(R.string.app_versio_report);
        processId = "version";
        if (locationModel == null) {
            locationModel = new LocationModel();
            locationModel.setState(User.getCurrentUser(getApplicationContext()).getMvUser().getState());
            locationModel.setDistrict(User.getCurrentUser(getApplicationContext()).getMvUser().getDistrict());
            locationModel.setTaluka(User.getCurrentUser(getApplicationContext()).getMvUser().getTaluka());
        }
        textNoData = (TextView) findViewById(R.id.textNoData);
        preferenceHelper = new PreferenceHelper(context);

        setActionbar(getString(R.string.app_versio_report));
        binding.swiperefresh.setOnRefreshListener(
                () -> {
                    if (Utills.isConnected(context))
                        getAllProcess(roleList);
                }
        );
        binding.spinnerRole.setText(roleList);
        binding.spinnerRole.setOnClickListener(v -> showRoleDialog());
        binding.editTextEmail.addTextChangedListener(watch);
        mAdapter = new VersionReportAdapter(processAllList, context);
        mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        if (Utills.isConnected(context))
            getAllProcess(roleList);
        else {
            Utills.showInternetPopUp(context);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;

            case R.id.img_list:
                Intent openClass = new Intent(context, IndicatorLocationSelectionActivity.class);
                openClass.putExtra(Constants.TITLE, title);
                openClass.putExtra(Constants.INDICATOR_TASK, task);
                openClass.putExtra(Constants.INDICATOR_TASK_ROLE, roleList);
                openClass.putExtra(Constants.PROCESS_ID, processId);
                startActivity(openClass);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
                break;
        }
    }

    private void setActionbar(String Title) {
        mToolBar = (RelativeLayout) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(Title);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(this);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logout.setVisibility(View.GONE);


        img_list = (ImageView) findViewById(R.id.img_list);
        img_list.setVisibility(View.VISIBLE);
        img_list.setOnClickListener(this);
        img_list.setImageResource(R.drawable.filter);
        img_logout.setImageResource(R.drawable.share_report);


    }

    private void getAllProcess(String roleName) {
        Utills.showProgressDialog(context, "Loading Process", getString(R.string.progress_please_wait));
        String url = preferenceHelper.getString(PreferenceHelper.InstanceUrl)
                + "/services/apexrest/getuser?state=" + locationModel.getState()
                + "&dist=" + locationModel.getDistrict() + "&tal=" + locationModel.getTaluka() + "&role=" + roleName;

        ServiceRequest apiService =
                ApiClient.getClientWitHeader(context).create(ServiceRequest.class);
        // String url = preferenceHelper.getString(PreferenceHelper.InstanceUrl)
        //       + "/services/apexrest/WS_getProcessAprovalUser?UserId="+ User.getCurrentUser(context).getMvUser().getId()+ "&processId=a1I7F000000VeJQUA0" ;;
        apiService.getSalesForceData(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utills.hideProgressDialog();
                binding.swiperefresh.setRefreshing(false);
                if (response.isSuccess()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() != 0) {


                            processAllList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Template processList = new Template();

                                if (jsonArray.getJSONObject(i).has("Id"))
                                    processList.setId(jsonArray.getJSONObject(i).getString("Id"));
                                if (jsonArray.getJSONObject(i).has("User_Mobile_App_Version__c"))
                                    processList.setUser_Mobile_App_Version__c(jsonArray.getJSONObject(i).getString("User_Mobile_App_Version__c"));
                                if (jsonArray.getJSONObject(i).has("Full_Name__c"))
                                    processList.setName(jsonArray.getJSONObject(i).getString("Full_Name__c"));

                                processAllList.add(processList);
                            }
                            ///     AppDatabase.getAppDatabase(context).userDao().deleteTable();
                            //  AppDatabase.getAppDatabase(context).userDao().insertProcess(processAllList);
                            mAdapter.notifyDataSetChanged();
                            textNoData.setVisibility(View.GONE);
                        } else {
                            textNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utills.hideProgressDialog();

            }
        });
    }

    private TextWatcher watch = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            // TODO Auto-generated method stub
            setFilter(s.toString());

        }
    };

    private void setFilter(String s) {
        List<Template> list = new ArrayList<>();
        repplicaCahart.clear();
        repplicaCahart.addAll(processAllList);
        for (int i = 0; i < repplicaCahart.size(); i++) {
            if (repplicaCahart.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                list.add(repplicaCahart.get(i));
            }
        }
        repplicaCahart.clear();
        repplicaCahart.addAll(list);
        mAdapter = new VersionReportAdapter(repplicaCahart, context);
        binding.recyclerView.setAdapter(mAdapter);
    }

    private void showRoleDialog() {


        if (roleList != null && !roleList.isEmpty()) {
            temp = new ArrayList<>(Arrays.asList(roleList.split(";")));

        }
        //  final List<Community> temp = AppDatabase.getAppDatabase(getApplicationContext()).userDao().getAllCommunities();
        final String[] items = new String[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            items[i] = temp.get(i);
        }
        final boolean[] mSelection = new boolean[items.length];
        Arrays.fill(mSelection, true);
      /* if(temp.contains(User.getCurrentUser(getApplicationContext()).getMvUser().getRoll()))
        mSelection[temp.indexOf(User.getCurrentUser(getApplicationContext()).getMvUser().getRoll())] = true;
*/
// arraylist to keep the selected items
        final ArrayList seletedItems = new ArrayList();
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                .setTitle("Select Role")
                .setMultiChoiceItems(items, mSelection, (dialog13, which, isChecked) -> {
                    if (which < mSelection.length) {
                        mSelection[which] = isChecked;


                    } else {
                        throw new IllegalArgumentException(
                                "Argument 'which' is out of bounds.");
                    }
                })
                .setPositiveButton(getString(R.string.ok), (dialog12, id) -> {
                    StringBuilder sb = new StringBuilder();
                    String prefix = "";
                    for (int i = 0; i < items.length; i++) {
                        if (mSelection[i]) {
                            sb.append(prefix);
                            prefix = ";";
                            sb.append(temp.get(i));
                            //now original string is changed
                        }
                    }

                    if (Utills.isConnected(getApplicationContext()))
                        getAllProcess(sb.toString());
                    binding.spinnerRole.setText(sb.toString());
                }).setNegativeButton(getString(R.string.cancel), (dialog1, id) -> {
                    //  Your code when user clicked on Cancel
                }).create();


        dialog.show();
    }


}
