package com.earthquake.se.timetodrop;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddNew_Item extends ActionBarActivity implements View.OnClickListener{

    private static Button CameraBtn;
    private static Button saveBtn;
    private static EditText editFoodName;
    private static Bitmap photo;
    private static ImageView imgView;
    private static Uri imageUri;
    private static String timeStamp;
    private static int CAMERA_ACTIVITY_REQ = 1;
    private static int GALLERY_REQ = 2;
    private static final String[] Image_Action = {"Camera", "Gallery"};

    FoodDb mHelper;
    SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new__item);
        mHelper = new FoodDb(this);
        mDb = mHelper.getWritableDatabase();
        initialWidget();
        CameraBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar_color)));
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }


    private void initialWidget() {
        saveBtn = (Button)  findViewById(R.id.saveBtn);
        CameraBtn = (Button) findViewById(R.id.CameraBtn);
        editFoodName = (EditText)findViewById(R.id.editText2);
        imgView = (ImageView) findViewById(R.id.imageView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new__item, menu);
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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.CameraBtn:
               // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNew_Item.this);
                builder.setTitle("Choose Existing");
                builder.setItems(Image_Action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = Image_Action[which];
                        if (Image_Action[which].equals("Camera")) {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                         //   String imageFileName = "IMG_" + timeStamp + ".jpg";
                            String imageFileName = "IMG_" + timeStamp + ".jpg";
                            File f = new File(Environment.getExternalStorageDirectory(), "DCIM/" + imageFileName);
                            imageUri = Uri.fromFile(f);
                            camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(camera, CAMERA_ACTIVITY_REQ);

                         /*   String imgname = Environment.getExternalStorageDirectory().getPath() + "/DCIM/testfile.jpg";
                            imageUri = Uri.fromFile(new File(imgname));
                            Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                            startActivityForResult(camera, CAMERA_ACTIVITY_REQ);
*/
                        }
                        else if (Image_Action[which].equals("Gallery")) {

                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,GALLERY_REQ);
                        }
                        }


                });
                builder.setNegativeButton("cancel", null);
                builder.create();
                builder.show();

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, CAMERA_ACTIVITY_REQ);
                break;


            case R.id.saveBtn:
                String foodName = editFoodName.getText().toString();
                if(foodName.length() != 0) {
                    Cursor mCursor = mDb.rawQuery("SELECT * FROM " + FoodDb.TABLE_NAME2
                            + " WHERE " + FoodDb.COL_Name + "='" + foodName + "'"
                            , null);
                    if (mCursor.getCount() == 0) {
                        mDb.execSQL("INSERT INTO " + FoodDb.TABLE_NAME2 + " ("
                                + FoodDb.COL_Name + ") VALUES ('" + foodName
                                + "');");
                    }


                    editFoodName.setText("");

                    Toast.makeText(getApplicationContext(), "Finish!!!", Toast.LENGTH_SHORT).show();
                        }
                else {
                    Toast.makeText(getApplicationContext(),"Please Input Item Name",Toast.LENGTH_SHORT).show();

                }


                }

    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

            if (requestCode == CAMERA_ACTIVITY_REQ && resultCode == RESULT_OK) {
                getContentResolver().notifyChange(imageUri, null);
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);// check width,height
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,200,200,true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                    imgView.setImageBitmap(rotatedBitmap);
                    Toast.makeText(getApplicationContext(), imageUri.getPath(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

               // Log.v("abc",uriimage.toString());
               // photo = (Bitmap) imageReturnedIntent.getExtras().get("data");


        } else if (resultCode == RESULT_OK && requestCode == GALLERY_REQ && imageReturnedIntent != null) {
                Uri uripath = imageReturnedIntent.getData();
                String[] arrFilePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uripath,arrFilePath,null,null,null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(arrFilePath[0]);
                String strPath = cursor.getString(columnIndex);
                cursor.close();
                photo =  BitmapFactory.decodeFile(strPath);
                imgView.setImageBitmap(photo);


             //  Toast.makeText(AddNew_Item.this,strPath.toString(),Toast.LENGTH_LONG).show(); // get path
        }
        }


   /* public void savePic(Bitmap pic){


       String imageFileName = "IMG_" + timeStamp + ".jpg";
       File f = new File(Environment.getExternalStorageDirectory(), "DCIM/" + imageFileName);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            pic.compress(Bitmap.CompressFormat.PNG,100,fos);
            Toast.makeText(AddNew_Item.this,"Save Pic Complete",Toast.LENGTH_LONG).show();
            fos.close();
        } catch (Exception e) {
            e.getMessage();
        }


    }*/
}
