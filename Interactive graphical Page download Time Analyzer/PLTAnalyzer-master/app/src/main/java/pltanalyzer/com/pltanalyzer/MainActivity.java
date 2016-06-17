package pltanalyzer.com.pltanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity implements View.OnClickListener{
    Button selectPageLoadTimeExperiment = null;

    @Override
    public void onClick(View v){
        Intent i = new Intent(MainActivity.this, SitesListView.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectPageLoadTimeExperiment = (Button) findViewById(R.id.bt);
        selectPageLoadTimeExperiment.setOnClickListener(this);
    }
}
