package ie.dbs.myplants;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddPlant extends AppCompatActivity {
    private Button takePicture;
    private Button addFromGallery;
    private Button save;
    private Button cancel;
    public static final int PICK_IMAGE_FROM_GALLERY=5;
    private ArrayList<Uri> mArrayUri;
    private String imageEncoded;
    private ImageView preview;
    int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        takePicture=findViewById(R.id.takePictureButton);
        addFromGallery= findViewById(R.id.chooseFromGalleryButton);
        preview=findViewById(R.id.preview);
        mArrayUri=new ArrayList<>();
        save=findViewById(R.id.saveImages);
        cancel=findViewById(R.id.cancel);


        addFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.AskForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, AddPlant.this);
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
                /*Intent getIntent= new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                Intent pickIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                Intent chooserIntent=Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE_FROM_GALLERY);*/
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size=mArrayUri.size();
                //int index=0;
                if(size>0) {
                    if ((size > 1) && (index < size-1)) {
                        index++;
                    }
                    else if((size>1)&& (index==size-1)) {
                        index=0;
                    }
                    try {
                        InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(mArrayUri.get(index));
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        preview.setImageBitmap(bitmap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else Toast.makeText(AddPlant.this, "Please select an image first", Toast.LENGTH_SHORT).show();


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.AskForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,AddPlant.this);
               // int plantNameIterator=Utils.checkHowManyFilesInDirectory(Utils.homeDirectory);
                for(int i=0;i<mArrayUri.size();i++)
                {
                    try {
                        InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(mArrayUri.get(i));
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //plantNameIterator++;
                        Utils.createDirectoryAndSaveFile(bitmap/*, "plant"+plantNameIterator+".jpg"*/);
                    }
                    catch (Exception ex)
                    {ex.printStackTrace();}
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog=new AlertDialog.Builder(AddPlant.this).create();
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Cancel saving pictures?");
                dialog.setButton(1,"Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(AddPlant.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setButton(2,"No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();                 }
                });
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
        if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK
                && null != data) {

            String [] filePathColumn={MediaStore.Images.Media.DATA};
            if(data.getData()!=null)
            {
                mArrayUri.clear();
                Uri ImageUri=data.getData();
                mArrayUri.add(ImageUri);
                Cursor cursor=getContentResolver().query(ImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded=cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap= BitmapFactory.decodeFile(imageEncoded);
                preview.setImageBitmap(bitmap);
            }
         else {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                mArrayUri.clear();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();
                    InputStream inputStream=getApplicationContext().getContentResolver().openInputStream(mArrayUri.get(0));
                    Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
                    preview.setImageBitmap(bitmap);

                }
                Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
            }
        }
    } else {
        Toast.makeText(this, "You haven't picked Image",
                Toast.LENGTH_LONG).show();
    }
} catch (Exception e) {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
        .show();
        }


                   /* imagesUriArrayList.clear();

                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        imagesUriArrayList.add(data.getData());
                    }
                    Log.v("SIZE", imagesUriArrayList.size() + "");
                    String[]filePathColumn= {MediaStore.Images.Media.DATA};

                    Cursor cursor=getContentResolver().query(imagesUriArrayList.get(1), filePathColumn, null, null,
                        null);
                    cursor.moveToFirst();
                    int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath=cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap bitmap=BitmapFactory.decodeFile(picturePath);
                    //bitmap = Bitmap.createScaledBitmap(bitmap,150, 150, true);
                    ImageView imageView=(ImageView)findViewById(R.id.preview);

                    imageView.setImageBitmap(bitmap);*/
                   /* adapter = new DataAdapter(MainActivity.this, imagesUriArrayList);
                    imageresultRecycletview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();*/


        }
        /*if (requestCode==PICK_IMAGE_FROM_GALLERY && resultCode== Activity.RESULT_OK) {
            if (data==null)
            {
                Log.v("Image Picker", "No Data Sent Back");
                return;
            }
            try {
                Uri selectedImage=data.getData();
                String[]filePathColumn= {MediaStore.Images.Media.DATA};

                Cursor cursor=getContentResolver().query(selectedImage, filePathColumn, null, null,
                        null);
                cursor.moveToFirst();
                int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
                String picturePath=cursor.getString(columnIndex);
                cursor.close();

                Bitmap bitmap= BitmapFactory.decodeFile(picturePath);
                ImageView imageView=(ImageView)findViewById(R.id.preview);

                imageView.setImageBitmap(bitmap);

                }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }*/

}
