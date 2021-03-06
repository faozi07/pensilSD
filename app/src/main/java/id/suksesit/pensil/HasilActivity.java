package id.suksesit.pensil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HasilActivity extends AppCompatActivity {

    LinearLayout view;
    TextView nama, kesimpulan, hasil,saran;
    CircleImageView fotoSiswa;
    ImageView emot;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Hasil Psikologi");
        }

        nama = findViewById(R.id.tNama);
        hasil = findViewById(R.id.hasilPsikologi);
        kesimpulan = findViewById(R.id.kesimpulan);
        saran = findViewById(R.id.saran);
        fotoSiswa = findViewById(R.id.foto_siswa);
        emot = findViewById(R.id.emot);
        nama.setText(MainActivity.nama);
        hasil.setText(MainActivity.nilai_ss);
        kesimpulan.setText(MainActivity.predikat_ss);
        saran.setText(MainActivity.saran);
        if (Integer.parseInt(hasil.getText().toString()) < 30) {
            Picasso.with(this)
                    .load(R.drawable.ic_shock)
                    .placeholder(R.drawable.ic_shock)
                    .error(R.drawable.ic_shock)
                    .into(emot);
            kesimpulan.setText(" ----- ");
            hasil.setText("-");
            saran.setText("Kamu belum jawab pertanyaan psikologi");
        }else if (Integer.parseInt(hasil.getText().toString()) >= 30 && Integer.parseInt(hasil.getText().toString()) <= 60) {
            Picasso.with(this)
                    .load(R.drawable.ic_sedih)
                    .placeholder(R.drawable.ic_sedih)
                    .error(R.drawable.ic_sedih)
                    .into(emot);
            kesimpulan.setTextColor(Color.rgb(207, 0, 15));
            hasil.setTextColor(Color.rgb(207, 0, 15));
            saran.setTextColor(Color.rgb(207, 0, 15));
        } else if (Integer.parseInt(hasil.getText().toString()) >= 61 && Integer.parseInt(hasil.getText().toString()) <= 90) {
            Picasso.with(this)
                    .load(R.drawable.ic_sedang)
                    .placeholder(R.drawable.ic_sedang)
                    .error(R.drawable.ic_sedang)
                    .into(emot);
            kesimpulan.setTextColor(Color.rgb(248, 148, 6));
            hasil.setTextColor(Color.rgb(248, 148, 6));
            saran.setTextColor(Color.rgb(248, 148, 6));
        } else if (Integer.parseInt(hasil.getText().toString()) >= 91) {
            Picasso.with(this)
                    .load(R.drawable.ic_tinggi)
                    .placeholder(R.drawable.ic_tinggi)
                    .error(R.drawable.ic_tinggi)
                    .into(emot);
            kesimpulan.setTextColor(Color.rgb(68,108,179));
            hasil.setTextColor(Color.rgb(68,108,179));
            saran.setTextColor(Color.rgb(68,108,179));
        }

        Picasso.with(this)
                .load(MainActivity.picture)
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoSiswa);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        MainActivity.namaBoom = "Beranda";
        startActivity(new Intent(HasilActivity.this,MainActivity.class));
    }
}
