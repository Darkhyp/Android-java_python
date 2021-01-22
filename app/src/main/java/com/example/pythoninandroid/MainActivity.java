package com.example.pythoninandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private static final int READ_REQUEST_CODE = 42;
    private long execution_time;
    private EditText var1, var2;
    private Button btnCalculate, btnOpenFile;
    private TextView outText;
    private Intent fileIntent;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this,"Permission not granted",Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private String readFile(String filename){
        File file = new File(filename);
        StringBuilder text = new StringBuilder();
        try {

            BufferedReader buffer = new BufferedReader(new FileReader(file));
            String line;
            while ((line=buffer.readLine())!=null){
                text.append(line);
                text.append("\n");
            }
            // close file
            buffer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(this, "onActivityResult: requestCode="+requestCode+", Activity.RESULT_OK="+Activity.RESULT_OK, Toast.LENGTH_SHORT).show();
        if(requestCode==READ_REQUEST_CODE || requestCode== Activity.RESULT_OK)
            if(data!=null){
                String path = data.getData().getPath();
                path = path.substring(path.indexOf(":")+1);
                Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();

                // read file content
                String text = readFile(path);
                // put text to EditText
                var1.setText(text);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request permission
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
        }

        // UI initialization
        var1 = findViewById(R.id.editVar1);
        var2 = findViewById(R.id.editVar2);
        outText = findViewById(R.id.out);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnOpenFile = findViewById(R.id.btnOpenFile);
        btnOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileIntent = new Intent();
                fileIntent.setAction(Intent.ACTION_GET_CONTENT);
//                fileIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//                fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                fileIntent.setType("*/*");
//                fileIntent.setType("file/*");
//                fileIntent.setType("text/*");
                startActivityForResult(fileIntent,READ_REQUEST_CODE);
            }
        });

        // add python
        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        Python py = Python.getInstance();
        // add reference to python script
//        final PyObject pyObject = py.getModule("script");
        PyObject pyObject = py.getModule("ggnumber");

        var1.setText(pyObject.callAttr("get_python_constant","SAMPLE_TEXT_FOR_BENCH").toString());
        var2.setText(pyObject.callAttr("get_python_constant","PHRASE_FOR_SEARCH").toString());

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // add reference to python function 'main' (in 'script.py') with its arguments
                PyObject pobj = pyObject.callAttr("main", var1.getText().toString(), var2.getText().toString());
                // print result
                outText.setText(pobj.toString());

                 */

                execution_time = System.nanoTime();
                PyObject pobj = pyObject.callAttr("count_occurences_in_text", var2.getText().toString(), var1.getText().toString());
                execution_time = System.nanoTime() - execution_time;

                outText.setText("There is(are) "+pobj.toString()+" occurence(s). Execution time = "+(float)execution_time/1e6+"ms");

            }
        });
    }
}