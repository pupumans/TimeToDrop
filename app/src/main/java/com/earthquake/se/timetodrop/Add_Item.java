package com.earthquake.se.timetodrop;


import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Add_Item extends ActionBarActivity implements View.OnClickListener,SurfaceHolder.Callback
        , Camera.PictureCallback, Camera.ShutterCallback {
    Camera mCamera;
    SurfaceView mSurfaceView;
    SurfaceHolder surfaceHolder;
    boolean saveState = false;
    private String timeStamp;
    private static Button photoBtn;
    private static Button RetakeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__item);
        initialWidget();
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        photoBtn.setOnClickListener(this);
        RetakeBtn.setOnClickListener(this);
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    }
    private void initialWidget() {
        mSurfaceView = (SurfaceView) findViewById(R.id.cameraView);
        photoBtn = (Button) findViewById(R.id.photoBtn);
        RetakeBtn = (Button) findViewById(R.id.RePhotoBtn);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add__item, menu);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        photoBtn.setVisibility(View.VISIBLE);
        RetakeBtn.setVisibility(View.INVISIBLE);
        try {
            // open the camera
            mCamera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = mCamera.getParameters();
        param.setRotation(90);
        // modify parameter
        param.setRotation(90);
        param.setJpegQuality(100);
        mCamera.setParameters(param);
        mCamera.setDisplayOrientation(90);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photoBtn:
                saveState = true;
                mCamera.takePicture(null,null,Add_Item.this);
            case R.id.RePhotoBtn:
                photoBtn.setVisibility(View.VISIBLE);
                RetakeBtn.setVisibility(View.INVISIBLE);
                mCamera.startPreview();


        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    String imageFileName = "IMG_" + timeStamp + ".jpg";
    File imgFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TTD");
    imgFolder.mkdirs();
    File output = new File(imgFolder, imageFileName);
    Uri uri = Uri.fromFile(output);
    imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

    OutputStream os;
        try {
            os = getContentResolver().openOutputStream(uri);
            os.write(data);
            os.flush();
            os.close();
            Toast.makeText(getApplicationContext(), imageFileName, Toast.LENGTH_SHORT).show();
            photoBtn.setVisibility(View.INVISIBLE);
            RetakeBtn.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
        } catch (IOException e) { }
        Log.d("Camera","Restart Preview");
      //  refreshCamera();


    }

    @Override
    public void onShutter() {

    }


    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {

        }
    }

}
