package com.example.sakurajiang.testcplugin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static int x = 7;
    int y = 6;
    int q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().getCurrentState();
        ((TextView)findViewById(R.id.my_tv)).setText(com.example.sakurajiang.testcplugin.MyPluginTestClass.str);

        testPrintln(5555);
        move(x,y);
        Log.d("hahah","x="+x);
        Log.d("hahah","y="+y);

    }

    public void initTextView(TextView textView){
        if(textView!=null){
            textView.setText(com.example.sakurajiang.testcplugin.MyPluginTestClass.str);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    getIntent()
                }
            });
        }
    }

    public void testPrintln(int s){
        Log.d("hahah",s+"");
    }

    public void move(int dx, int dy) {
        Log.d("hahah1","dx="+dx);
//        int i = 9;
        x += dx;
        Log.d("hahah2","dx="+dx);
        y += dy; }

}
