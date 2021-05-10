package com.example.textrecognition;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.File;

import ppapps.textrecognition.cropreceipt.CropReceiptActivity;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_RECEIPT_PATH = "RECEIPT_PATH";
    EditText mResultEt;
    ImageView mPreviewIv;

    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=1001;

    int LAUNCH_ACTIVITY_2 = 1;
    String result;

    String camerPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sample);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setSubtitle("Click + Button To Insert Images");
        mResultEt=findViewById(R.id.resultEt);
        mPreviewIv=findViewById(R.id.imageIv);

        //camera permission
        camerPermission=new String[]{Manifest.permission.CAMERA ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    //action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    //handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id== R.id.addImage) {
           showImageImportDialog();
        }
        if(id== R.id.settings){
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //item to displayin dialog
        String[] items={"Camera","Gallery"};
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    //camera option clicked
                    if(!checkCameraPermission()){
                        //camera permission not allowed,request it
                        requestCameraPermission();
                    }
                    else{
                        //permission allowed,take picture
                        openActivity2();
                    }

                }
                if (which==1)
                {
                    //gallery option clicked
                    if(!checkStoragePermission()){
                        //Storage permission not allowed,request it
                        requestStoragePermission();
                    }
                    else{
                        //permission allowed,take picture
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();//show dialog
    }

    private void openActivity2() {
        //intent to open Activity 2
        Intent intent = new Intent(this, Activity2.class);
        startActivityForResult(intent, LAUNCH_ACTIVITY_2);
        intent = getIntent();
        File file = new File(String.valueOf(intent.getStringArrayExtra("location")));
        Log.d("activity main", "value of result: "+file);
        //image_uri = file.toURI();
        //String fp = intent.getStringExtra("location");
        //image_uri = Uri.parse(result);
    }

    private void pickGallery() {
        //intent to pic image from gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera,it will also be saved to the storage to get high quality image
        //ContentValues values= new ContentValues();
        //values.put(MediaStore.Images.Media.TITLE,"NewPic");//title of image
        //values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");//description
        //image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        //startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,camerPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }


    //Handle permission Request

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //Handle image result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ACTIVITY_2)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                result=data.getStringExtra("message");
                Log.d("activity main", "result is: "+result);
            }
        }

        image_uri = Uri.parse("file://"+result);
        Log.d("activity main", "image uri is: "+image_uri);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image from gallery now crop it
                //CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON)//enable image guide line
                //        .start(this);
                startCropActivity(data.getData());
            }
            if (requestCode == LAUNCH_ACTIVITY_2) {
                //got image from camera now crop it
                //CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON)//enable image guide line
                //        .start(this);
                startCropActivity(image_uri);
            }
        }
        //get cropped images
        /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            /*if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();//get image uri
                //set image to image view
                mPreviewIv.setImageURI(resultUri);
                //get drawble bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    String t=null;
                    //get text from sb until there is no text
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        Log.d("activity main", "Final text: "+sb.toString());
                        t=sb.toString();
                        t=t.replaceAll("[^0-9]\\.", "");
                        Log.d("activity main", "value of t: "+t);
                        sb.append("\n");
                    }
                    //set text to edit text

                    //if decimal isn't considered
                    /*StringBuilder desired_result = new StringBuilder();
                    if (((t.length()-3)%2)==0)
                    {
                        desired_result.append(t.substring(0, 3));
                        desired_result.append("\n");
                        for (int i=3;i<t.length();i++)
                        {
                             desired_result.append(t.substring(i,i+2));
                             desired_result.append("\n");
                             i++;
                             if(i==t.length())
                                 break;
                        }
                    }
                    else if (((t.length()-3)%2)!=0)
                    {
                        desired_result.append(t.substring(0, 3));
                        desired_result.append("\n");
                        desired_result.append(t.substring(3, 6));
                        desired_result.append("\n");
                        for (int i=6;i<t.length();i++)
                        {
                            desired_result.append(t.substring(i,i+2));
                            desired_result.append("\n");
                            i++;
                            if(i==t.length())
                                break;
                        }
                    }
                    else
                    {
                        desired_result.append(t.substring(0, 2));
                        desired_result.append("\n");
                        desired_result.append(t.substring(2, 4));
                        desired_result.append("\n");
                        for (int i=4;i<t.length();i++)
                        {
                            desired_result.append(t.substring(i,i+2));
                            desired_result.append("\n");
                            i++;
                            if(i==t.length())
                                break;
                        }
                    }
                    Log.d("activity main","desired_result: "+desired_result.toString());*/
                    //mResultEt.setText(t);
                //}
            //} /*else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //if there is any error show it
                //Exception error = result.getError();
                //Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();

            //}
        //}
    }

    private void startCropActivity(Uri data)
    {
        Intent intent = new Intent(this, CropReceiptActivity.class);
        intent.putExtra(KEY_RECEIPT_PATH, data);
        startActivity(intent);
    }
}
