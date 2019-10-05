package ie.dbs.myplants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class AddPictureToPlant extends AppCompatActivity {
    private Button takePicture;
    private Button addFromGallery;
    private Button save;
    private Button cancel;
    public static final int PICK_IMAGE_FROM_GALLERY=5;
    public static final int REQUEST_IMAGE_CAPTURE=6;
    private ArrayList<Uri> mArrayUri;
    private String imageEncoded;
    private ImageView preview;
    int index=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        takePicture=(Button)findViewById(R.id.takePictureButton);
        addFromGallery= (Button)findViewById(R.id.chooseFromGalleryButton);
        preview=(ImageView)findViewById(R.id.preview);
        mArrayUri=new ArrayList<>();
        save=(Button)findViewById(R.id.saveImages);
        cancel=(Button)findViewById(R.id.cancel);

        Utils.AskForPermission(Manifest.permission.CAMERA, AddPictureToPlant.this);
        Utils.AskForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, AddPictureToPlant.this);
        Utils.AskForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AddPictureToPlant.this);
        addFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FROM_GALLERY);
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(Utils.applicationContext, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }

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
                else Toast.makeText(AddPictureToPlant.this, "Please select an image first", Toast.LENGTH_SHORT).show();


            }
        });

        //TODO save pictures only when plant is added
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[]imagePath=new String[mArrayUri.size()];
                for(int i=0;i<mArrayUri.size();i++)
                {
                    try {
                        InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(mArrayUri.get(i));
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imagePath[i]=Utils.createDirectoryAndSaveFile(bitmap, AddPictureToPlant.this);
                        Intent intent=new Intent(AddPictureToPlant.this, AddPlant.class);
                        intent.putExtra("image", imagePath);
                        startActivity(intent);
                        finish();
                    }
                    catch (Exception ex)
                    {Log.v("error saving pic", ex.getMessage());
                    Toast.makeText(getApplicationContext(), "Couldn't save picture", Toast.LENGTH_SHORT).show();}
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog=new AlertDialog.Builder(AddPictureToPlant.this).create();
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Cancel saving pictures?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(AddPictureToPlant.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();                 }
                });
                dialog.show();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //if picking image from gallery selected
            if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK
                    && null != data) {
                try {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    //TODO why is it not updating automatically when only one pic is selected
                    if (data.getData() != null) {
                        mArrayUri.clear();
                        Uri ImageUri = data.getData();
                        mArrayUri.add(ImageUri);
                        Cursor cursor = getContentResolver().query(ImageUri, filePathColumn, null, null, null);
                        if(cursor!=null)
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(imageEncoded);
                        preview.setImageBitmap(bitmap);
                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            mArrayUri.clear();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                if(cursor!=null)
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded = cursor.getString(columnIndex);
                                cursor.close();
                                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(mArrayUri.get(0));
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                preview.setImageBitmap(bitmap);

                            }
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //if taking pic from camera selected
            else if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK && data!=null)
            {
                try {
                    Bundle extras = data.getExtras();
                    if(extras.get("data")!=null) {
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        preview.setImageBitmap(bitmap);
                        mArrayUri.clear();
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        if (bytes!=null)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                        mArrayUri.add(Uri.parse(path));
                    }
                } catch (Exception ex)
                {ex.printStackTrace();}
            }
            else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout: {
                final AlertDialog dialog=new AlertDialog.Builder(AddPictureToPlant.this).create();
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.mAuth.signOut();
                        Intent intent=new Intent(AddPictureToPlant.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            }

            default:
                break;
        }
        return true;
    }
}
