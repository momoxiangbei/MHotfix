package com.mmxb.mhotfix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 启动 bug activity
        findViewById(R.id.btn_bug_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        // load dex 含有修复了bug的BugTest.class
        findViewById(R.id.btn_fix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dexFile = new File(getFilesDir().getAbsolutePath() +
                        File.separator + "class.dex");
                HotFixUtil.loadFixDex(MainActivity.this, dexFile);
            }
        });
    }
}
