package com.gokulnc.smartcgpapredictor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gokulnc.GPACalculator.CompleteCourse;

import java.io.IOException;
import java.io.InputStream;

public class GPApredictor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ScrollView layout;
    LinearLayout l;
    EditText[] et;
    EditText cgpa;
    Button predict;
    float gpa[];
    float target_cgpa;
    short credits[];
    short creditsTotal;
    CompleteCourse cc;
    String dept= "cse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpapredictor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        readAppData();

        setUI();
    }

    void readAppData() {
        AssetManager assetManager = getAssets();

        try {
            InputStream input = assetManager.open(dept+".txt");
            cc = new CompleteCourse(input);
            cc.fetchSemData();
            input.close();
        } catch (IOException e) {
            AlertDialog.Builder error =  new AlertDialog.Builder(this);
            error.setTitle("Error");
            error.setMessage(e.getStackTrace().toString());
            error.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {  }
            });
            error.create();
            error.show();
            e.printStackTrace();
        }


    }

    void setUI() {
        layout = (ScrollView) findViewById(R.id.scrollview_gpapredictor);
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        et = new EditText[8];
        TextView note = new TextView(this);
        note.setText("Enter the details below: \n");
        for(int i=0; i<8; ++i) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            et[i] = new EditText(this);
            et[i].setHint("Sem "+(i+1)+" GPA");
            et[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ll.addView(et[i]);
            l.addView(ll);
        }
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        cgpa = new EditText(this);
        cgpa.setHint("Target Final CGPA to achieve");
        cgpa.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ll.addView(cgpa);

        predict = new Button(this);
        predict.setText("Predict GPA");
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                predictGPA();
            }
        });
        ll.addView(predict);

        l.addView(ll);
        layout.addView(l);
    }

    void predictGPA() {

        if(!validate()) return;

        cc.saveGPAsWithoutGrades(gpa);

        float gpa = 0.0f;
        try {
            gpa = cc.calculatePrediction(target_cgpa);
        } catch (Exception e) {
            AlertDialog.Builder error =  new AlertDialog.Builder(this);
            error.setTitle("Error");
            error.setMessage(e.getStackTrace().toString());
            error.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {  }
            });
            error.create();
            error.show();
            e.printStackTrace();
        }

        AlertDialog.Builder result = new AlertDialog.Builder(this);
        result.setTitle("Result");
        String msg = "GPA Required: "+String.format("%.2f", gpa)+"\n\n";
        if(gpa <= 10.0)
            msg += "You need to obtain "+String.format("%.2f", gpa)+" GPA in all the remaining semesters to obtain your desired CGPA of "+target_cgpa;
        else msg += "Well, this is mathematically impossible. So, you'll never obtain the CGPA "+target_cgpa
                +", unless ofcourse if you can travel back in time and get better GPAs in your previous semesters.";
        result.setMessage(msg);
        result.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {  }
        });
        result.create();
        result.show();

    }

    boolean validate() {
        gpa = new float[8];
        for(int i=0; i<8; i++) {
            try {
                gpa[i] = Float.parseFloat(et[i].getText().toString());
            } catch(NumberFormatException e) {
                gpa[i] = 0.0f;
            }

            if(gpa[i] > 10.0  || gpa[i]<0) {
                AlertDialog.Builder warning = new AlertDialog.Builder(this);
                warning.setTitle("Error at Sem "+(i+1)+" GPA!");
                warning.setMessage("The GPA value must be between 0 and 10");
                warning.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {  }
                });
                warning.create();
                warning.show();
                return false;
            }

            if(i!=0 && gpa[i] > 0.0 && gpa[i-1]==0.0) {
                AlertDialog.Builder warning = new AlertDialog.Builder(this);
                warning.setTitle("Error at Sem "+(i+1)+" GPA!");
                warning.setMessage("Either enter the missing GPA in between, or remove GPA's after Sem "+i);
                warning.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {  }
                });
                warning.create();
                warning.show();
                return false;
            }
        }
        try {
            target_cgpa =  Float.parseFloat(cgpa.getText().toString());
            if(target_cgpa > 10.0) throw new NumberFormatException("CGPA is greater than 10, lol");
        } catch(NumberFormatException e) {
            AlertDialog.Builder warning = new AlertDialog.Builder(this);
            warning.setTitle("Error at reading CGPA!");
            warning.setMessage("Ensure CGPA value is valid and is between 0 and 10");
            warning.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {  }
            });
            warning.create();
            warning.show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpapredictor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
