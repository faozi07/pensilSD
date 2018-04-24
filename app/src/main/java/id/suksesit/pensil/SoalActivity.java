package id.suksesit.pensil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.suksesit.pensil.helper.Http;
import id.suksesit.pensil.helper.ResetStatus;
import id.suksesit.pensil.helper.UbahNilaiSS;
import id.suksesit.pensil.R;

public class SoalActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private Button btnLanjut;
    private TextView nosoal, pertanyaan, status, id_angket,nil,jnsSoal;
    private RadioButton sering, selalu, kadang, tdkpernah;
    private RadioGroup rbGrup;
    String rendah = "RENDAH", sedang = "SEDANG", tinggi = "TINGGI", n = "";
    String ketRen = "Sikap sosial akan kepedulian terhadap diri sendiri, teman sejawat, dan lingkungan perlu diperbaiki segera " +
            "dan menyeluruh, supaya anak bisa terselamatkan dari perilaku dan kepribadian yang buruk",
            ketSed= "Sikap sosial akan kepedulian terhadap diri sendiri, teman sejawat, dan lingkungan perlu ditingkatkan supaya " +
                    "sikap sosial dapat lebih maksimal. Apa yang sudah baik dipertahankan dan yang dirasa belum baik perlu ditingkatkan",
            ketTing="Sikap sosial akan kepedulian terhadap diri sendiri, teman sejawat, dan lingkungan perlu dipertahankan sehingga bisa " +
                    "dijadikan teladan bagi orang lain";
    private RelativeLayout llayout;
    private int jmlprtnyaan = 1;
    public static int nilai = 0,niltambah = 0;
    public static String id_angket1 = "";
    SoalDB soalDB;
    public static ArrayList<modelSoal> arraySoal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soal);
        soalDB = new SoalDB(this);
        llayout = findViewById(R.id.llayout);
        nosoal = findViewById(R.id.JmlPrtnyaan);
        nil = findViewById(R.id.tnilai);
        status = findViewById(R.id.stts);
        id_angket = findViewById(R.id.id_angket);
        pertanyaan = findViewById(R.id.pertanyaan);
        jnsSoal = findViewById(R.id.jenis_soal);
        sering = findViewById(R.id.rSering);
        selalu = findViewById(R.id.rSelalu);
        kadang = findViewById(R.id.rKadang);
        tdkpernah = findViewById(R.id.rTdkPernah);
        rbGrup = findViewById(R.id.rbGrup);
        btnLanjut = findViewById(R.id.btnNext);
        rbGrup.setOnCheckedChangeListener(this);
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                rbGrup.clearCheck();
                if (n.equals("")) {
                    Toast.makeText(SoalActivity.this, "Pilih Jawaban Kamu ya :)", Toast.LENGTH_SHORT).show();
                } else  {
//                ubahStatus();
                    nilai = nilai + niltambah;
                    jmlprtnyaan = jmlprtnyaan + 1;
                    nosoal.setText(String.valueOf(jmlprtnyaan));
                    if(jmlprtnyaan == 15 || nosoal.getText() == "15") {
                        btnLanjut.setText("selesai");
                        btnLanjut.setBackgroundColor(Color.rgb(102, 51, 153));
                    } else if (jmlprtnyaan == 16 || nosoal.getText() == "16") {
                        nosoal.setText("15");
                        HomeActivity.noSoal = 15;
//                        HasilActivity.nilai = String.valueOf(nilai);
                        pertanyaan.setText("");
                        llayout.setVisibility(View.INVISIBLE);
                        showDialog();
                    }
                    tampil();
                    MainActivity.nilai_ss = String.valueOf(nilai);
                    nil.setText(String.valueOf(nilai));
                    ubahNilai();
                    n="";
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Soal Kuisioner");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        showDialog2();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        tampil();
    }

    public void resetStatus() {
        new ResetStatus(this).execute(
        );
    }

    public void onCheckedChanged(RadioGroup group,
                                 int checkedId) {
        if (checkedId == R.id.rSelalu) {
            if (jnsSoal.getText().toString().equals("positif")) {
                niltambah = 8;
                n = "0";
            } else if (jnsSoal.getText().toString().equals("negatif")) {
                niltambah = 2;
                n = "0";
            }
        } else if (checkedId == R.id.rSering) {
            if (jnsSoal.getText().toString().equals("positif")) {
                niltambah = 6;
                n = "0";
            } else if (jnsSoal.getText().toString().equals("negatif")) {
                niltambah = 4;
                n = "0";
            }
        } else if (checkedId == R.id.rKadang) {
            if (jnsSoal.getText().toString().equals("positif")) {
                niltambah = 4;
                n = "0";
            } else if (jnsSoal.getText().toString().equals("negatif")) {
                niltambah = 6;
                n = "0";
            }
        } else if (checkedId == R.id.rTdkPernah) {
            if (jnsSoal.getText().toString().equals("positif")) {
                niltambah = 2;
                n = "0";
            } else if (jnsSoal.getText().toString().equals("negatif")) {
                niltambah = 8;
                n = "0";
            }
        }
    }

    private void ubahNilai() {

        String nil2="",kes="",sar="";
        if (nilai >= 30 && nilai <= 60) {
            nil2 = nil.getText().toString();
            kes=rendah;
            sar=ketRen;
            saveNilai(nil.getText().toString(),rendah,ketRen);
        } else if (nilai >= 61 && nilai <= 90) {
            nil2 = nil.getText().toString();
            kes=sedang;
            sar=ketSed;
            saveNilai(nil.getText().toString(),sedang,ketSed);
        } else if (nilai >= 91) {
            nil2 = nil.getText().toString();
            kes=tinggi;
            sar=ketTing;
            saveNilai(nil.getText().toString(),tinggi,ketTing);
        }
        new UbahNilaiSS(this).execute(
                nil2,kes,sar
        );

    }

    private void saveNilai(String nilai, String predikat, String saran) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("nilai_ss", nilai);
        editor.putString("predikat_ss", predikat);
        editor.putString("saran", saran);

        MainActivity.nilai_ss = nilai;
        MainActivity.predikat_ss = predikat;
        MainActivity.saran = saran;

        editor.apply();
    }

    public void tampil() {
        soalDB.showAllSoal();
        for (int i=0;i<arraySoal.size();i++) {
            modelSoal ms = arraySoal.get(i);
            pertanyaan.setText(ms.getPertanyaan());
            jnsSoal.setText(ms.getKategori());
        }
    }

    private void showDialog2(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Batal isi soal psikologi ?");
        alertDialogBuilder
                .setMessage("Apa kamu serius untuk batal isi soal ?")
                .setIcon(R.drawable.ic_shock)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finish();
                        MainActivity.namaBoom = "Beranda";
                        resetStatus();
                        startActivity(new Intent(SoalActivity.this,MainActivity.class));
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        showDialog2();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Terima Kasih...");
        alertDialogBuilder
                .setMessage("Tugasmu Sudah Selesai :)")
                .setIcon(R.drawable.ic_success)
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        MainActivity.namaBoom = "Beranda";
                        startActivity(new Intent(SoalActivity.this, MainActivity.class));
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        MainActivity.nilai_ss = String.valueOf(nil.getText().toString());
    }
}