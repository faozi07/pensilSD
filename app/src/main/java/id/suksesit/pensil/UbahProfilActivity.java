package id.suksesit.pensil;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import id.suksesit.pensil.R;

public class UbahProfilActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    MainActivity H = new MainActivity();
    ProfilActivity Pf = new ProfilActivity();

    private Switch switch_sekolah;
    private TextView input6, input7;
    private Button btnSmpProfil, btnGantiSekolah;
    private EditText eUmur, eNama, eKelas, eTtl, eSekolah;
    int mYear, mMonth, mDay, tahun = 0;
    static final int DIALOG_ID = 0;
    private String[] arrMonth = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September"
            , "Oktober", "November", "Desember"};

    LinearLayout linear_switch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);input6 = findViewById(R.id.input6);
        input7 = findViewById(R.id.input7);
        btnSmpProfil = findViewById(R.id.btn_save);
        btnGantiSekolah = findViewById(R.id.btn_change_sekolah);
        eUmur = findViewById(R.id.tUmurs);
        eNama = findViewById(R.id.tNama);
        eKelas = findViewById(R.id.tKelas);
        eTtl = findViewById(R.id.tTglLhr);
        eSekolah = findViewById(R.id.tnamaSekolah);
        switch_sekolah = findViewById(R.id.switch_sekolah);
        linear_switch = findViewById(R.id.linear_switch);

        eUmur.setText(MainActivity.umur);
        eNama.setText(MainActivity.nama);
        eKelas.setText(MainActivity.kelas);
        eTtl.setText(MainActivity.tglLhr);
        eSekolah.setText(MainActivity.sekolah);
        showDialogTtl();
        final Calendar c = Calendar.getInstance();
        tahun = c.get(Calendar.YEAR);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        switch_sekolah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    input6.setTextColor(getResources().getColor(R.color.colorPrimary));
                    linear_switch.setVisibility(View.VISIBLE);
                } else {
                    input6.setTextColor(Color.parseColor("#999999"));
                    linear_switch.setVisibility(View.GONE);
                }
            }
        });

        btnSmpProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eNama.getText().toString().equals("") || eUmur.getText().toString().equals("") ||
                        eTtl.getText().toString().equals("")){
                    Snackbar.make(v, "Isi data dengan lengkap", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (eNama.getText().toString().equals("Pengguna Baru")) {
                    Snackbar.make(v, "Ganti nama kamu dengan benar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    ProfilActivity.nama.setText(eNama.getText().toString());
                    ProfilActivity.tglLhr.setText("Tgl Lahir\n\n" +eTtl.getText().toString());
                    ProfilActivity.umur.setText("Umur\n\n"+eUmur.getText().toString());
                    HomeActivity.nama.setText(eNama.getText().toString());
                    saveProfile(eNama.getText().toString(),eTtl.getText().toString(),eUmur.getText().toString());
                    Toast.makeText(getApplicationContext(),"Data diri berhasil diubah", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        btnGantiSekolah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eKelas.getText().toString().equals("") || eSekolah.getText().toString().equals("")){
                    Snackbar.make(v, "Isi data dengan lengkap", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    ProfilActivity.kelas.setText("Kelas : " +eKelas.getText().toString());
                    ProfilActivity.sekolah.setText("Sekolah\n" + eSekolah.getText().toString());
                    saveSekolah(eKelas.getText().toString(),eSekolah.getText().toString());
                    Toast.makeText(getApplicationContext(),"Data sekolah berhasil diubah", Toast.LENGTH_LONG).show();
                    finish();
                    ProfilActivity.kelas.setVisibility(View.VISIBLE);
                    ProfilActivity.sekolah.setVisibility(View.VISIBLE);
                }
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ubah Profil");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void saveProfile(String nama, String ttl, String umur) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("nama", nama);
        editor.putString("tgl_lahir", ttl);
        editor.putString("umur", umur);

        MainActivity.nama = nama;
        MainActivity.umur = umur;
        MainActivity.tglLhr = ttl;

        editor.apply();
    }

    private void saveSekolah(String kelas, String sekolah) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("kelas", kelas);
        editor.putString("sekolah", sekolah);

        MainActivity.kelas = kelas;
        MainActivity.sekolah = sekolah;

        editor.apply();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new DatePickerDialog(
                    this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener()

            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar cal = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                    setDate(cal);
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    String sdate = mDay+","+arrMonth[mMonth]+" "+mYear;

                    eTtl.setText(sdate);
                    eUmur.setText(String.valueOf(tahun-mYear)+" tahun");
                }
            };

    public void showDialogTtl() {
        eTtl = findViewById(R.id.tTglLhr);
        eTtl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialog(DIALOG_ID);
                return true;
            }
        });

    }

    private void setDate(final Calendar calendar) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((EditText) findViewById(R.id.tTglLhr)).setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year,month,dayOfMonth);
        setDate(cal);
    }

}
