package id.suksesit.pensil;

import android.annotation.SuppressLint;
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

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class ProfileActivity extends AppCompatActivity {

    MainActivity H = new MainActivity();

    LinearLayout view, llayout;
    private PopupWindow mPopupImage;

    Bitmap bitmap;
    Dialog myDialog;
    private Context mContexts;
    final int GALLERY_REQUEST = 22131;

    GalleryPhoto galleryPhoto;
    public static TextView sekolah, nama, tglLhr, kelas, umur;
    Button btnUbah, btnChangeImage;
    ImageView image_update;
    CircleImageView fotoSiswa;
    public static String photoPath = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profil");
        }
        sekolah = findViewById(R.id.tSekolah);
        nama = findViewById(R.id.tNama);
        fotoSiswa = findViewById(R.id.foto_siswa);
        tglLhr = findViewById(R.id.tTglLhr);
        kelas = findViewById(R.id.tKelas);
        btnUbah = findViewById(R.id.btnUbah);
        llayout = findViewById(R.id.llayout);
        btnChangeImage = findViewById(R.id.changeImage);
        umur = findViewById(R.id.tUmur);
        mContexts = getApplicationContext();
        umur.setText("Umur\n\n" + MainActivity.umur);
        nama.setText(MainActivity.nama); //ini ngambilnya dr session td, ga ngeload dr server
        tglLhr.setText("Tanggal Lahir\n\n" + MainActivity.tglLhr);
        kelas.setText("Kelas\n\n" + MainActivity.kelas);
        sekolah.setText("Sekolah\n\n" + MainActivity.sekolah);
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        setTitle("Profil " + MainActivity.nama);
        Picasso.with(this) // ini mas
                .load(Http.picture + MainActivity.picture)//
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoSiswa);
        Log.d("Log picture", Http.picture + MainActivity.picture);

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            myDialog = new Dialog(ProfileActivity.this);
            myDialog.setContentView(R.layout.update_foto);
            if (myDialog.getWindow() != null) {
                myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                myDialog.setCancelable(true);
            }

            Button save = myDialog.findViewById(R.id.button_update_profilepicture);
            image_update = myDialog.findViewById(R.id.img_profile_update);
            myDialog.show();

            Uri uri = data.getData();
            galleryPhoto.setPhotoUri(uri);
            photoPath = galleryPhoto.getPath();
            bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
            fotoSiswa.setImageBitmap(bitmap);

            image_update.setImageBitmap(bitmap);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveFoto(photoPath);
                    myDialog.dismiss();
                    fotoSiswa.setImageBitmap(bitmap);
                    MainActivity.fotoSiswa.setImageBitmap(bitmap);
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

        @SuppressLint("InflateParams")
        View customView = inflater.inflate(R.layout.popup_foto, null);
        mPopupImage = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupImage.setElevation(5.0f);
        }
        RelativeLayout relativeLayout = customView.findViewById(R.id.rl_custom_layout);
        ImageButton closeButton = customView.findViewById(R.id.ib_close);
        ImageView fotoFull = customView.findViewById(R.id.foto_siswa);
        Picasso.with(ProfileActivity.this)
                .load(Http.picture + MainActivity.picture)//
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
        MainActivity.picture = foto;
        editor.apply();
    }
}
