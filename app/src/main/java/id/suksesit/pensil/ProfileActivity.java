package id.suksesit.pensil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.suksesit.pensil.helper.Http;
import id.suksesit.pensil.R;

public class ProfileActivity extends AppCompatActivity {

    MainActivity H = new MainActivity();

    String URL = Http.url + "penso/profile.php?user_id=" + H.user_id;
    String jsonResult;
    LinearLayout view, llayout;
    private PopupWindow mPopupImage;
    public RelativeLayout mRelativeLayout, mReveralView;

    private boolean hidden = true;
    int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    Dialog myDialog;
    private Context mContexts;
    final int GALLERY_REQUEST = 22131;

    Toolbar toolbar;
    GalleryPhoto galleryPhoto;
    public static TextView sekolah, nama, tglLhr, kelas, umur;
    Button btnUbah, btnChangeImage;
    ImageView image_update;
    CircleImageView fotoSiswa;
    int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sekolah = (TextView) findViewById(R.id.tSekolah);
        nama = (TextView) findViewById(R.id.tNama);
        fotoSiswa = (CircleImageView) findViewById(R.id.foto_siswa);
        tglLhr = (TextView) findViewById(R.id.tTglLhr);
        kelas = (TextView) findViewById(R.id.tKelas);
        btnUbah = (Button) findViewById(R.id.btnUbah);
        llayout = (LinearLayout) findViewById(R.id.llayout);
        ImageButton closeButton = (ImageButton) findViewById(R.id.ib_close);
        btnChangeImage = (Button) findViewById(R.id.changeImage);
        umur = (TextView) findViewById(R.id.tUmur);
        mContexts = getApplicationContext();
        umur.setText("Umur\n\n" + H.umur);
        nama.setText(H.nama); //ini ngambilnya dr session td, ga ngeload dr server
        tglLhr.setText("Tanggal Lahir\n\n" + H.tglLhr);
        kelas.setText("Kelas\n\n" + H.kelas);
        sekolah.setText("Sekolah\n\n" + H.sekolah);
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        Log.d("log url", URL);

        setTitle("Profil " + H.nama);
        Picasso.with(this) // ini mas
                .load(Http.picture + H.picture)//
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoSiswa);
        Log.d("Log picture", Http.picture + H.picture);

        fotoSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImage();
            }
        });

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });

        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UbahProfilActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
//        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            myDialog = new Dialog(ProfileActivity.this);
            myDialog.setContentView(R.layout.update_foto);
            myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            myDialog.setCancelable(true);

            Button save = (Button) myDialog.findViewById(R.id.button_update_profilepicture);
            image_update = (ImageView) myDialog.findViewById(R.id.img_profile_update);
            myDialog.show();

            Uri uri = data.getData();
            if (uri == null) {

            }
            galleryPhoto.setPhotoUri(uri);
            String photoPath = galleryPhoto.getPath();
            bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
            fotoSiswa.setImageBitmap(bitmap);

            image_update.setImageBitmap(bitmap);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog loading = ProgressDialog.show(ProfileActivity.this, "Ubah Foto...", "Tunggu yah :) ...", false, false);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Http.url + "penso/update_picture.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String result) {
                                    loading.dismiss();
                                    Log.d("Log result", result);

                                    if (!result.toLowerCase().equals("failed")) {
                                        saveFoto(result);
                                        Toast.makeText(ProfileActivity.this, "Berhasil Ubah Gambar", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Gagal Ubah Gambar", Toast.LENGTH_LONG).show();
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
                    RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
                    requestQueue.add(stringRequest);
                    myDialog.cancel();
                    fotoSiswa.setImageBitmap(bitmap);
                    H.fotoSiswa.setImageBitmap(bitmap);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, 0);

        return encodedImage;
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
        Picasso.with(ProfileActivity.this)
                .load(Http.picture + H.picture)//
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoFull);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupImage.dismiss();
//                        finish();
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        mPopupImage.dismiss();
//                        finish();
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
}
