package id.suksesit.pensil;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.suksesit.pensil.helper.Http;
import id.suksesit.pensil.helper.Permission;
import id.suksesit.pensil.helper.ScalingUtilities;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    LinearLayout llayout;
    private PopupWindow mPopupImage;
    Bitmap bitmap;
    Dialog myDialog;
    private Context mContexts;
    final int GALLERY_REQUEST = 22131;
    Uri uri;
    LinearLayout mRevealView;
    private boolean hidden = true;
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    final int CAMERA_REQUEST = 13323;
    public static TextView sekolah, nama, tglLhr, kelas, umur, resetUser;
    Button btnUbah, btnChangeImage;
    ImageView image_update;
    CircleImageView fotoSiswa;
    public int rotate = 0;
    String strMyImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sekolah = findViewById(R.id.tSekolah);
        nama = findViewById(R.id.tNama);
        fotoSiswa = findViewById(R.id.foto_siswa);
        tglLhr = findViewById(R.id.tTglLhr);
        kelas = findViewById(R.id.tKelas);
        resetUser = findViewById(R.id.tReset);
        btnUbah = findViewById(R.id.btnUbah);
        llayout = findViewById(R.id.llayout);
        btnChangeImage = findViewById(R.id.changeImage);
        umur = findViewById(R.id.tUmur);
        mContexts = getApplicationContext();
        umur.setText("Umur\n\n" + MainActivity.umur);
        nama.setText(MainActivity.nama); //ini ngambilnya dr session td, ga ngeload dr server
        tglLhr.setText("Tgl Lahir\n\n" + MainActivity.tglLhr);
        kelas.setText("Kelas : " + MainActivity.kelas);
        sekolah.setText("Sekolah\n" + MainActivity.sekolah);
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        cameraPhoto = new CameraPhoto(getApplicationContext());
        //pilih foto
        mRevealView = findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);

        ImageButton gallery_btn = findViewById(R.id.gallery_img_btn);
        ImageButton photo_btn = findViewById(R.id.photo_img_btn);

        gallery_btn.setOnClickListener(this);
        photo_btn.setOnClickListener(this);

        if (MainActivity.kelas.equals("") && MainActivity.sekolah.equals("")) {
            sekolah.setVisibility(View.GONE);
            kelas.setVisibility(View.GONE);
        } else {
            sekolah.setVisibility(View.VISIBLE);
            kelas.setVisibility(View.VISIBLE);
        }
        SharedPreferences spUser = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        Glide.with(this).load(spUser.getString("picture",""))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.d("message ","Gagal ubah foto"+e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(fotoSiswa);
        Log.d("Log picture", Http.picture + MainActivity.picture);
        fotoSiswa.setVisibility(View.VISIBLE);
        fotoSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImage();
            }
        });
        llayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
                fotoSiswa.setVisibility(View.VISIBLE);
            }
        });
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = mRevealView.getLeft();
                int cy = mRevealView.getLeft();
                int tinggi = 0,lebar = 0;

                int radius = 0;
                if (mRevealView.getWidth() == 0 || mRevealView.getHeight() == 0) {
                    tinggi = 200;
                    lebar = 500;
                    radius = Math.max(lebar, tinggi);
                } else {
                    radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
                }
                //Below Android LOLIPOP Version
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(700);

                    SupportAnimator animator_reverse = animator.reverse();

                    if (hidden) {
                        mRevealView.setVisibility(View.VISIBLE);
                        fotoSiswa.setVisibility(View.INVISIBLE);
                        animator.start();
                        hidden = false;
                    } else {
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {
                                fotoSiswa.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onAnimationEnd() {
                                mRevealView.setVisibility(View.INVISIBLE);
                                fotoSiswa.setVisibility(View.VISIBLE);
                                hidden = true;
                            }
                            @Override
                            public void onAnimationCancel() {}
                            @Override
                            public void onAnimationRepeat() {}
                        });
                        animator_reverse.start();
                    }
                }
                // Android LOLIPOP And ABOVE Version
                else {
                    if (hidden) {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                        anim.setDuration(700);
                        anim.start();
                        mRevealView.setVisibility(View.VISIBLE);
                        hidden = false;
                    } else {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                fotoSiswa.setVisibility(View.VISIBLE);
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;
                            }
                        });
                        anim.start();
                    }
                }
            }
        });

        resetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("nama", "Pengguna Baru");
                editor.putString("picture", "aaaaa_photoprof.png");
                editor.putString("tgl_lahir", "");
                editor.putString("umur", "");
                editor.putString("kelas", "");
                editor.putString("sekolah", "");
                editor.putString("nilai_ss", "0");
                editor.putString("predikat_ss", "");
                editor.putString("saran", "");

                MainActivity.nama = "Pengguna Baru";
                MainActivity.picture = "aaaaa_photoprof.png";
                MainActivity.umur = "";
                MainActivity.tglLhr = "";
                MainActivity.kelas = "";
                MainActivity.sekolah = "";
                MainActivity.namaBoom = "Beranda";
                MainActivity.nilai_ss = "0";
                MainActivity.predikat_ss = "";
                MainActivity.saran = "";

                editor.apply();
                finish();
                startActivity(new Intent(ProfilActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Berhasil reset data pengguna", Toast.LENGTH_LONG).show();
            }
        });

        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilActivity.this, UbahProfilActivity.class));
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profil " + MainActivity.nama);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 13323: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                        cameraPhoto.addToGallery();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("permission granted  ", "permission granted");
                } else {
                    Log.d("permission denied  ", "permission denied");
                }
                return;
            }
            case 22131: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fotoSiswa.setVisibility(View.VISIBLE);
                    startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                    Log.d("permission granted  ", "permission granted");
                } else {
                    Log.d("permission denied  ", "permission denied");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void hideRevealView() {
        if (mRevealView.getVisibility() == View.VISIBLE) {
            mRevealView.setVisibility(View.GONE);
            hidden = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                callCamera();
            } else {
                getGalery(data);
            }
        }
    }

    public void getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        try {
            context.getContentResolver().notifyChange(imageUri, null);

            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 0;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    rotate = 0;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String dateToString(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        return df.format(date);
    }

    private void decodeFile(String path) {
        Bitmap scaledBitmap;
        try {
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, 500, 500, ScalingUtilities.ScalingLogic.FIT);
            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 500, 500, ScalingUtilities.ScalingLogic.FIT);

            Matrix matrix = new Matrix();
            if (rotate == 90) {
                matrix.setRotate(90);
            } else if (rotate == 270) {
                matrix.setRotate(270);
            } else if (rotate == 180) {
                matrix.setRotate(180);
            }
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            // Store to tmp file

            String storage = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(storage + "/PensilSD");
            if (!mFolder.exists()) {
                if (mFolder.mkdir()) {
                    System.out.println("mFolder created");
                } else {
                    System.out.println("mFolder failed to create");
                }
            } else {
                if (mFolder.delete()) {
                    if (mFolder.mkdir()) {
                        System.out.println("mFolder created");
                    } else {
                        System.out.println("mFolder failed to create");
                    }
                } else {
                    System.out.println("mFolder failed to delete");
                }
            }

            String s = dateToString(new Date())+".png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(f);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            rotatedBitmap.recycle();
        } catch (Throwable e) {
            Log.e("LoadImage Error", String.valueOf(e));
        }

        if (strMyImagePath == null) {
            strMyImagePath = path;
        }
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public void getGalery(Intent data) {
        try {
            myDialog = new Dialog(ProfilActivity.this);
            myDialog.setContentView(R.layout.update_foto);
            myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            myDialog.setCancelable(true);
            Button save = myDialog.findViewById(R.id.button_update_profilepicture);
            image_update = myDialog.findViewById(R.id.img_profile_update);
            myDialog.show();

            if (data.getData() == null) {
                uri.toString();
            } else {
                uri = data.getData();
            }
            galleryPhoto.setPhotoUri(uri);
            final String photoPath = galleryPhoto.getPath();
            bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();

            image_update.setImageBitmap(bitmap);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveFoto(photoPath);
                    myDialog.dismiss();
                    fotoSiswa.setImageBitmap(bitmap);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Gagal memilih foto", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void callCamera() {
        final String photoTake = cameraPhoto.getPhotoPath();
        File avatarFileA = new File(photoTake);
        Uri avatarUri = Uri.fromFile(avatarFileA);
        getCameraPhotoOrientation(this,avatarUri,photoTake);
        decodeFile(photoTake);
        try {
            myDialog = new Dialog(ProfilActivity.this);
            myDialog.setContentView(R.layout.update_foto);
            myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            myDialog.setCancelable(true);
            Button save = myDialog.findViewById(R.id.button_update_profilepicture);
            image_update = myDialog.findViewById(R.id.img_profile_update);
            myDialog.show();

            bitmap = ImageLoader.init().from(strMyImagePath).requestSize(512, 512).getBitmap();
            image_update.setImageBitmap(bitmap);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveFoto(strMyImagePath);
                    myDialog.cancel();
                    fotoSiswa.setImageBitmap(bitmap);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Gagal memuat foto", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("InflateParams")
    private void popupImage() {
        LayoutInflater inflater = (LayoutInflater) mContexts.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = null;
        if (inflater != null) {
            customView = inflater.inflate(R.layout.popup_foto, null);
        }
        mPopupImage = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupImage.setElevation(5.0f);
        }

        ImageButton closeButton = null;
        if (customView != null) {
            closeButton = customView.findViewById(R.id.ib_close);
        }
        ImageView fotoFull = null;
        if (customView != null) {
            fotoFull = customView.findViewById(R.id.foto_siswa);
        }
        Picasso.with(ProfilActivity.this)
                .load(Http.picture + MainActivity.picture)//
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoFull);

        if (closeButton != null) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupImage.dismiss();
                }
            });
        }

        mPopupImage.showAtLocation(llayout, Gravity.CENTER, 0, 0);
    }

    private void saveFoto(String foto) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("picture", foto);
        MainActivity.picture = foto;
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        hideRevealView();
        switch (v.getId()) {
            case R.id.gallery_img_btn:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        fotoSiswa.setVisibility(View.VISIBLE);
                        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(ProfilActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    fotoSiswa.setVisibility(View.VISIBLE);
                    startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                }
                break;
            case R.id.photo_img_btn:
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (Permission.checkCameraPermission(ProfilActivity.this) && Permission.checkWriteExStoragePermission(ProfilActivity.this)) {
                            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                            cameraPhoto.addToGallery();
                        } else {
                            ActivityCompat.requestPermissions(ProfilActivity.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1003);
                        }
                    } else {
                        startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                        cameraPhoto.addToGallery();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while taking photos", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
