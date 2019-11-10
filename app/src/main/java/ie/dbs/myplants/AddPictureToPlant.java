package ie.dbs.myplants;


import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AddPictureToPlant extends Activity {
    private static final int PICK_IMAGE_FROM_GALLERY=5;
    private static final int REQUEST_IMAGE_CAPTURE=6;
    private ArrayList<Uri> mArrayUri;
    private ImageView preview;
    private boolean isProfilePic;
    private String plantID;
    private boolean modify=false;
    private Button modify_picture_date;
    private String timeStamp;
    private int index=0;
    private Uri photoURI;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        Button takePicture = findViewById(R.id.takePictureButton);
        Button addFromGallery =findViewById(R.id.chooseFromGalleryButton);
        preview=findViewById(R.id.preview);
        mArrayUri=new ArrayList<>();
        Button save =findViewById(R.id.saveImages);
        Button cancel = findViewById(R.id.cancel);
        modify_picture_date=findViewById(R.id.pic_modify_date);
        plantID=getIntent().getStringExtra("plantID");
        modify=getIntent().getBooleanExtra("modify",false);
        timeStamp=" ";



        addFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent pickIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent=Intent.createChooser(intent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE_FROM_GALLERY);

            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(Utils.applicationContext, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);*/
                   /* values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    mArrayUri.clear();
                    mArrayUri.add( getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mArrayUri.get(0));
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);*/
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            photoURI = FileProvider.getUriForFile(AddPictureToPlant.this,
                                    "ie.dbs.myplants.fileprovider",
                                    photoFile);
                            mArrayUri.clear();
                            mArrayUri.add(photoURI);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            Log.v("UriContent", photoURI.toString());
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    try {

                        if(isProfilePic) {
                            if(!modify) {
                                Intent intent = new Intent(AddPictureToPlant.this, AddPlant.class);
                                intent.putExtra("image", mCurrentPhotoPath);
                                intent.putExtra("modify", false);
                                intent.putExtra("plantID", plantID);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(AddPictureToPlant.this, AddPlant.class);
                                intent.putExtra("image", mCurrentPhotoPath);
                                intent.putExtra("modify", true);
                                intent.putExtra("plantID", plantID);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else
                        {
                            Intent intent=new Intent(AddPictureToPlant.this, SinglePlant.class);
                            intent.putExtra("image", mCurrentPhotoPath);
                            intent.putExtra("plantID", plantID);
                            startActivity(intent);
                            finish();
                        }
                    }
                    catch (Exception ex)
                    {Log.v("error saving pic", ex.getMessage());
                    Toast.makeText(getApplicationContext(), "Couldn't save picture", Toast.LENGTH_SHORT).show();}


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
                        Utils.deletePic(mCurrentPhotoPath);
                        Intent intent=new Intent(AddPictureToPlant.this, AddPlant.class);
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


        modify_picture_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

    }

    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour=calendar.get(Calendar.HOUR_OF_DAY);
        final int minute=calendar.get(Calendar.MINUTE);
        final int second=calendar.get(Calendar.SECOND);
            final DatePickerDialog datePicker =
                    new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(final DatePicker view, final int year, final int month,
                                              final int dayOfMonth) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd/MM/yy");
                            calendar.set(year, month, dayOfMonth,hour, minute, second);
                            Date myDate = calendar.getTime();
                            String dateString = simpleDateFormat1.format(myDate);
                            modify_picture_date.setText(dateString);
                            timeStamp=simpleDateFormat.format(myDate);
                        }
                    }, year, month, day); // set date picker to current date
            datePicker.getDatePicker().setMaxDate(new Date().getTime());
            datePicker.show();

            datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(final DialogInterface dialog) {
                    timeStamp=" ";
                    dialog.dismiss();
                }
            });
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        try {
            isProfilePic=getIntent().getBooleanExtra("IsProfilePicture", true);
            //if picking image from gallery selected
            if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK
                    && null != data) {
                try {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (data.getData() != null) {
                        mArrayUri.clear();
                        Uri ImageUri = data.getData();
                        mArrayUri.add(ImageUri);
                        Cursor cursor = getContentResolver().query(ImageUri, filePathColumn, null, null, null);
                        String imageEncoded;
                        if(cursor!=null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            cursor.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(imageEncoded);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                            preview.setImageBitmap(bitmap);
                            mCurrentPhotoPath=Utils.createDirectoryAndSaveFile(bitmap, this, timeStamp);
                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //if taking pic from camera selected
            else if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK /*&& data!=null*/)
            {
                try {
                    Uri contentUri=FileProvider.getUriForFile(getApplicationContext(), "ie.dbs.myplants.fileprovider", new File(mCurrentPhotoPath));
                    preview.setImageURI(contentUri);


                } catch (Exception ex)
                {ex.printStackTrace();
                Log.v("dataException", ex.getMessage());}
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
  /*  @Override
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
*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File(Utils.applicationContext.getExternalFilesDir(null) + "/MyPlants/");
        File file = new File(storageDir, "picture" +
                timeStamp + ".jpeg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = file.getAbsolutePath();
        return file;
    }


}
