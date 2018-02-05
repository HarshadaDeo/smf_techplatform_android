package com.mv.Activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mv.R;
import com.mv.Utils.LocaleManager;

public class AboutUsActivity extends AppCompatActivity  implements View.OnClickListener{
    private ImageView img_back, img_list,img_logout;
    private TextView toolbar_title;
    private RelativeLayout mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setActionbar(getString(R.string.about_us));
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
    private void setActionbar(String Title) {

            mToolBar = (RelativeLayout) findViewById(R.id.toolbar);
            toolbar_title = (TextView) findViewById(R.id.toolbar_title);
            toolbar_title.setText(Title);
            img_back = (ImageView) findViewById(R.id.img_back);
            img_back.setVisibility(View.VISIBLE);
            img_back.setOnClickListener(this);
            img_list = (ImageView) findViewById(R.id.img_list);
            img_list.setVisibility(View.INVISIBLE);
            img_list.setOnClickListener(this);
            img_logout = (ImageView) findViewById(R.id.img_logout);
            img_logout.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.img_back :
                finish();
        }

    }
}