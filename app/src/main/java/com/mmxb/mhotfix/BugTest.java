package com.mmxb.mhotfix;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wxy on 2018/12/24
 */

public class BugTest {
    public void getBug(Context context) {
        //模拟一个bug
        int i = 10;
        int a = 0;
        Toast.makeText(context, "Hello,Minuit:" + i / a, Toast.LENGTH_SHORT).show();
    }
}