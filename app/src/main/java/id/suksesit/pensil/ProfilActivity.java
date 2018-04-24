package id.suksesit.pensil;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.suksesit.pensil.helper.Http;
import id.suksesit.pensil.helper.Permission;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    MainActivity H = new MainActivity();

    String URL = Http.url + "penso/profile.php?user_id=" + H.user_id;
    LinearLayout view, llayout;
    private PopupWindow mPopupImage;
    Bitmap bitmap;
    Dialog myDialog;
    private Context mContexts;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    final int GALLERY_REQUEST = 22131;
    Uri uri;
    Toolbar toolbar;
    LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton gallery_btn, photo_btn;
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    final int CAMERA_REQUEST = 13323;
    public static TextView sekolah, nama, tglLhr, kelas, umur, resetUser;
    Button btnUbah, btnChangeImage;
    ImageView image_update;
    CircleImageView fotoSiswa;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sekolah = (TextView) findViewById(R.id.tSekolah);
        nama = (TextView) findViewById(R.id.tNama);
        fotoSiswa = (CircleImageView) findViewById(R.id.foto_siswa);
        tglLhr = (TextView) findViewById(R.id.tTglLhr);
        kelas = (TextView) findViewById(R.id.tKelas);
        resetUser = (TextView) findViewById(R.id.tReset);
        btnUbah = (Button) findViewById(R.id.btnUbah);
        llayout = (LinearLayout) findViewById(R.id.llayout);
        btnChangeImage = (Button) findViewById(R.id.changeImage);
        umur = (TextView) findViewById(R.id.tUmur);
        mContexts = getApplicationContext();
        umur.setText("Umur\n\n" + H.umur);
        nama.setText(H.nama); //ini ngambilnya dr session td, ga ngeload dr server
        tglLhr.setText("Tgl Lahir\n\n" + H.tglLhr);
        kelas.setText("Kelas : " + H.kelas);
        sekolah.setText("Sekolah\n" + H.sekolah);
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        cameraPhoto = new CameraPhoto(getApplicationContext());
        //pilih foto
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);

        gallery_btn = (ImageButton) findViewById(R.id.gallery_img_btn);
        photo_btn = (ImageButton) findViewById(R.id.photo_img_btn);

        gallery_btn.setOnClickListener(this);
        photo_btn.setOnClickListener(this);

        Log.d("log url", URL);
        if (MainActivity.kelas == "" && MainActivity.sekolah == "") {
            sekolah.setVisibility(View.GONE);
            kelas.setVisibility(View.GONE);
        } else {
            sekolah.setVisibility(View.VISIBLE);
            kelas.setVisibility(View.VISIBLE);
        }
        Picasso.with(this) // ini mas
                .load(Http.picture + H.picture)//
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoSiswa);
        Log.d("Log picture", Http.picture + H.picture);
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

                H.nama = "Pengguna Baru";
                H.picture = "aaaaa_photoprof.png";
                H.umur = "";
                H.tglLhr = "";
                H.kelas = "";
                H.sekolah = "";
                H.namaBoom = "Beranda";
                H.nilai_ss = "0";
                H.predikat_ss = "";
                H.saran = "";

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil " + H.nama);
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
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/5331596856");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        finish();
        H.namaBoom = "Beranda";
        startActivity(new Intent(ProfilActivity.this, MainActivity.class));
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

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, 0);

        return encodedImage;
    }

    public void getGalery(Intent data) {
        try {
            myDialog = new Dialog(ProfilActivity.this);
            myDialog.setContentView(R.layout.update_foto);
            myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            myDialog.setCancelable(true);
            Button save = (Button) myDialog.findViewById(R.id.button_update_profilepicture);
            image_update = (ImageView) myDialog.findViewById(R.id.img_profile_update);
            myDialog.show();

            if (data.getData() == null) {
                uri.toString();
            } else {
                uri = data.getData();
            }
            galleryPhoto.setPhotoUri(uri);
            String photoPath = galleryPhoto.getPath();
            bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();

            image_update.setImageBitmap(bitmap);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog loading = ProgressDialog.show(ProfilActivity.this, "Ubah Foto...", "Tunggu yah :) ...", false, false);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Http.url + "penso/update_picture.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String result) {
                                    loading.dismiss();
                                    Log.d("Log result", result);

                                    if (!result.toLowerCase().equals("failed")) {
                                        saveFoto(result);
                                        HomeActivity.fotoSiswa.setImageBitmap(bitmap);
                                        Toast.makeText(ProfilActivity.this, "Berhasil Ubah Gambar", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ProfilActivity.this, "Gagal Ubah Gambar", Toast.LENGTH_LONG).show();
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    loading.dismiss();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            String image = getStringImage(bitmap);
                            Intent a = getIntent();
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("user_id", H.user_id);
                            params.put("picture", image);
                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    stringRequest.setShouldCache(false);
                    RequestQueue requestQueue = Volley.newRequestQueue(ProfilActivity.this);
                    requestQueue.add(stringRequest);
                    myDialog.cancel();
                    fotoSiswa.setImageBitmap(bitmap);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Gagal memilih foto", Toast.LENGTH_SHORT).show();
        }
    }

    public void callCamera() {
        try {
            myDialog = new Dialog(ProfilActivity.this);
            myDialog.setContentView(R.layout.update_foto);
            myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            myDialog.setCancelable(true);
            Button save = (Button) myDialog.findViewById(R.id.button_update_profilepicture);
            image_update = (ImageView) myDialog.findViewById(R.id.img_profile_update);
            myDialog.show();

            String photoTake = cameraPhoto.getPhotoPath();
            bitmap = ImageLoader.init().from(photoTake).requestSize(512, 512).getBitmap();
            image_update.setImageBitmap(bitmap);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog loading = ProgressDialog.show(ProfilActivity.this, "Ubah Foto...", "Tunggu yah :) ...", false, false);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Http.url + "penso/update_picture.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String result) {
                                    loading.dismiss();
                                    Log.d("Log result", result);

                                    if (!result.toLowerCase().equals("failed")) {
                                        saveFoto(result);
                                        HomeActivity.fotoSiswa.setImageBitmap(bitmap);
                                        Toast.makeText(ProfilActivity.this, "Berhasil Ubah Gambar", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ProfilActivity.this, "Gagal Ubah Gambar", Toast.LENGTH_LONG).show();
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    loading.dismiss();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            String image = getStringImage(bitmap);
                            Intent a = getIntent();
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("user_id", H.user_id);
                            params.put("picture", image);
                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    stringRequest.setShouldCache(false);
                    RequestQueue requestQueue = Volley.newRequestQueue(ProfilActivity.this);
                    requestQueue.add(stringRequest);
                    myDialog.cancel();
                    fotoSiswa.setImageBitmap(bitmap);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Gagal memuat foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void popupImage() {
        LayoutInflater inflater = (LayoutInflater) mContexts.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.popup_foto, null);
        mPopupImage = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupImage.setElevation(5.0f);
        }
        RelativeLayout relativeLayout = (RelativeLayout) customView.findViewById(R.id.rl_custom_layout);
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        ImageView fotoFull = (ImageView) customView.findViewById(R.id.foto_siswa);
        Picasso.with(ProfilActivity.this)
                .load(Http.picture + H.picture)//
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoFull);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupImage.dismiss();
            }
        });

        mPopupImage.showAtLocation(llayout, Gravity.CENTER, 0, 0);
    }

    private void saveFoto(String foto) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("picture", foto);
        H.picture = foto;
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
