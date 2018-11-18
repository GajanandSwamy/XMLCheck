package com.example.gajanand.xmlcheck;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 2;

    String[] mPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    File mediaStorageDir, file;

    private EditText ip, port, url;
    private CheckBox checkBox;
    private Button button;

    ITSettingsModel itSettingsModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.ip_ims);
        port = findViewById(R.id.port_ims);
        url = findViewById(R.id.url_its);
        checkBox = findViewById(R.id.cb_ims);
        button = findViewById(R.id.btn);
        itSettingsModel = new ITSettingsModel();


        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                    != MockPackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[1])
                            != MockPackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[2])
                            != MockPackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[3])
                            != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        mPermission, REQUEST_CODE_PERMISSION);

                // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyDirName");

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        Log.d("Gajanand", "onCreate: " + mediaStorageDir.getAbsolutePath());


        file = new File("/storage/emulated/0/MyDirName/Dummy.xml");


        generateXML(itSettingsModel);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ITSettingsModel itSettingsModel = new ITSettingsModel();

                final String ipp = ip.getText().toString().trim();
                String prt = port.getText().toString().trim();
                if (checkBox.isChecked()) {
                    itSettingsModel.setIMS_sslCheck("true");
                } else
                    itSettingsModel.setIMS_sslCheck("false");

                String urrl = url.getText().toString().trim();
                itSettingsModel.setIMS_Ip(ipp);
                itSettingsModel.setIMS_port(prt);

                itSettingsModel.setITS_Url(urrl);

                generateXML(itSettingsModel);

                Toast.makeText(MainActivity.this, "Done BRo", Toast.LENGTH_SHORT).show();

            }
        });

        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream inputStream = new FileInputStream(file);

                    parse(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public boolean parse(InputStream is) {
        XmlPullParserFactory factory;

        XmlPullParser parser;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        if (tagname.equalsIgnoreCase("IMS_IP")) {
                            Log.d("Gajanand", "parse: " + parser.nextText());
                        }
                        if (tagname.equalsIgnoreCase("IMS_PORT")) {
                            Log.d("Gajanand", "parse: " + parser.nextText());
                        }
                        if (tagname.equalsIgnoreCase("IMS_SSL")) {
                            Log.d("Gajanand", "parse: " + parser.nextText());
                        }
                        if (tagname.equalsIgnoreCase("ITS_URL")) {
                            Log.d("Gajanand", "parse: " + parser.nextText());
                        }


                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println("kishan XmlPullParserException " + e.toString());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("kishan IOException " + e.toString());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("kishan Exception " + e.toString());
            return false;
        }

        return true;
    }


    private void generateXML(ITSettingsModel itSettingsModel) {


        try {

            XmlSerializer xmlSerializer = Xml.newSerializer();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);


            xmlSerializer.startTag("", DefinesClass.SETTINGS_IP);


            xmlSerializer.startTag("", DefinesClass.IMS);

            xmlSerializer.startTag("", DefinesClass.IMS_IP);

            if (itSettingsModel.getIMS_Ip() == null) {
                xmlSerializer.text("192.168.1.50");
            } else
                xmlSerializer.text(itSettingsModel.getIMS_Ip());


            xmlSerializer.endTag("", DefinesClass.IMS_IP);

            xmlSerializer.startTag("", DefinesClass.IMS_PORT);
            if (itSettingsModel.getIMS_port() == null) {
                xmlSerializer.text("8080");
            } else
                xmlSerializer.text(itSettingsModel.getIMS_port());
            xmlSerializer.endTag("", DefinesClass.IMS_PORT);

            xmlSerializer.startTag("", DefinesClass.IMS_SSL);
            if (itSettingsModel.getIMS_sslCheck() == null) {
                xmlSerializer.text("false");
            } else
                xmlSerializer.text(itSettingsModel.getIMS_sslCheck());
            xmlSerializer.endTag("", DefinesClass.IMS_SSL);

            xmlSerializer.endTag("", DefinesClass.IMS);

            xmlSerializer.startTag("", DefinesClass.ITS);

            xmlSerializer.startTag("", DefinesClass.ITS_URL);
            if (itSettingsModel.getITS_Url() == null) {
                xmlSerializer.text("http://220.227.176.235:8080/doa/");
            } else
                xmlSerializer.text(itSettingsModel.getITS_Url());
            xmlSerializer.endTag("", DefinesClass.ITS_URL);

            xmlSerializer.endTag("", DefinesClass.ITS);

            xmlSerializer.endTag("", DefinesClass.SETTINGS_IP);


            xmlSerializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Gajanand", "generateXML: " + e.toString());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 4 &&
                    grantResults[0] == MockPackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == MockPackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == MockPackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == MockPackageManager.PERMISSION_GRANTED) {

                // Success Stuff here

                Log.d("Gajanand", "onRequestPermissionsResult: got it seems");

            }
        }

    }

}
