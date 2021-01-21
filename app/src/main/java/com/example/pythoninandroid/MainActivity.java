package com.example.pythoninandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainActivity extends AppCompatActivity {
    EditText var1, var2;
    Button btnCalculate;
    TextView outText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        var1 = findViewById(R.id.editVar1);
        var2 = findViewById(R.id.editVar2);
        outText = findViewById(R.id.out);
        btnCalculate = findViewById(R.id.btnCalculate);

        // add python
        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        Python py = Python.getInstance();
        // add reference to python script
        final PyObject pyObject = py.getModule("script");

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add reference to python function 'main' (in 'script.py') with its arguments
                PyObject pobj = pyObject.callAttr("main", var1.getText().toString(), var2.getText().toString());
                // print result
                outText.setText(pobj.toString());
            }
        });
    }
}