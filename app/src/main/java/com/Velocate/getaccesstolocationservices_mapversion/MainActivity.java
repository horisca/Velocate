package com.Velocate.getaccesstolocationservices_mapversion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar implementation
        Toolbar toolbar = findViewById(R.id.toolbarus);
        setSupportActionBar(toolbar);

        button4 = findViewById(R.id.button4);

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Menu is implemented for subsequently including the information button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    //Method for creating dialog with information button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //item1 corresponds to info button. By pressing this button a dialog showing information related to the privacy policy is shown. The user can then press the information button and will be redirected to the link below
        if (item.getItemId() == R.id.item1) {
            TextView textView = (new AlertDialog.Builder(this)
                    .setTitle("Privacy statement")
                    .setMessage(Html.fromHtml("<p>\n" + "To use this app, you must accept the <a href=\"https://www.google.com/\">privacy policy</a>.</p>"))
                    .setCancelable(false)
                    .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show()
                    .findViewById(android.R.id.message));
            assert textView != null;
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return super.onOptionsItemSelected(item);
    }
}
