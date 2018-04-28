package id.suksesit.pensil;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import de.hdodenhof.circleimageview.CircleImageView;
import id.suksesit.pensil.helper.BuilderManager;

public class MainActivity extends AppCompatActivity {

    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public static String user_id = "";//kesini
    public static String umur = "";
    public static String kelas = "";
    public static String nama = "Pengguna Baru";
    public static String sekolah = "";
    public static String tglLhr = "";
    public static String picture = "aaaaa_photoprof.png";
    public static String nilai_ss = "0";
    public static String predikat_ss = "";
    public static String saran = "";
    public static String namaBoom = "Beranda";
    public static CircleImageView fotoSiswa;
    @SuppressLint("StaticFieldLeak")
    public static FrameLayout fl;
    RelativeLayout llayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Beranda");
        }

        cekLogin();

        fl = findViewById(R.id.frame_container);
        llayout = findViewById(R.id.llayout);
        BoomMenuButton bmbMenu = findViewById(R.id.bmbMenu);
        bmbMenu.setBackPressListened(true);
        bmbMenu.setUse3DTransformAnimation(false);
        bmbMenu.setDuration(400);
        for (int i = 0; i < bmbMenu.getPiecePlaceEnum().pieceNumber(); i++) {
            bmbMenu.addBuilder(BuilderManager.getTextOutsideCircleButtonBuilder());
            if (i == 0) {
                namaBoom = "Profil";
            } else if (i == 1) {
                namaBoom = "Soal Psikologi";
            } else if (i == 2) {
                namaBoom = "Hasil Psikologi";
            } else if (i == 3) {
                namaBoom = "Bagikan";
            } else if (i == 4) {
                namaBoom = "Tentang Kami";
            } else if (i == 5) {
                namaBoom = "Keluar";
            }
        }
        bmbMenu.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                if (index == 0) {
                    fragment = new HomeActivity();
                    callFragment(fragment);
                    getSupportActionBar().setTitle("Beranda");
                } else if (index == 1) {
                    startActivity(new Intent(MainActivity.this, ProfilActivity.class));
                } else if (index == 2) {
                    if (nilai_ss.equals("0") || HomeActivity.noSoal != 15) {
                        if (umur.equals("")) {
                            Snackbar.make(boomButton, "Ubah data kamu lebih dulu di menu profil", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            showDialogSoal();
                        }
                    } else {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Kamu sudah mengisi kuisioner");
                        alertDialogBuilder
                                .setMessage("Kamu ingin mengisi ulang ?")
                                .setIcon(R.drawable.ic_shock)
                                .setCancelable(false)
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        HomeActivity.noSoal = 1;
                                        SoalActivity.nilai = 0;
                                        showDialogSoal();
                                        dialog.cancel();
                                        resetStatus();
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                } else if (index == 3) {
                    if (umur.equals("")) {
                        Snackbar.make(boomButton, "Ubah data kamu lebih dulu di menu profil", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        startActivity(new Intent(MainActivity.this, HasilActivity.class));
                    }
                } else if (index == 4) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Tes psikologi anak anda di sini : http://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else if (index == 5) {
                    startActivity(new Intent(MainActivity.this, TentangKamiActivity.class));
                } else if (index == 6) {
                    showDialog();
                }
            }

            @Override
            public void onBackgroundClick() {
                System.out.print(picture);
            }

            @Override
            public void onBoomWillHide() {

            }

            @Override
            public void onBoomDidHide() {

            }

            @Override
            public void onBoomWillShow() {

            }

            @Override
            public void onBoomDidShow() {

            }
        });

        fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            fragment = new HomeActivity();
            callFragment(fragment);
        }

        resetStatus();
    }

    private void resetStatus() {
        SoalDB soalDB = new SoalDB(this);
        soalDB.getWritableDatabase();
        soalDB.updateSoalAll();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.act_hubkami) {
            finish();
            startActivity(new Intent(MainActivity.this, HubungiKamiActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Keluar dari aplikasi?");
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setIcon(R.drawable.ic_exit2)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        namaBoom = "Beranda";
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogSoal() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Apakah kamu sudah siap ?");
        alertDialogBuilder
                .setMessage("Klik Sudah jika kamu sudah siap !")
                .setIcon(R.drawable.ic_smile)
                .setCancelable(false)
                .setPositiveButton("Sudah", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        startActivity(new Intent(MainActivity.this, SoalActivity.class));
                    }
                })
                .setNegativeButton("Belum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void cekLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "");
        nama = sharedPreferences.getString("nama", "");
        tglLhr = sharedPreferences.getString("tgl_lahir", "");
        umur = sharedPreferences.getString("umur", "");
        picture = sharedPreferences.getString("picture", "");
        sekolah = sharedPreferences.getString("sekolah", "");
        kelas = sharedPreferences.getString("kelas", "");
        nilai_ss = sharedPreferences.getString("nilai_ss", "");
        predikat_ss = sharedPreferences.getString("predikat_ss", "");
        saran = sharedPreferences.getString("saran", "");
        Log.d("SAVE SESSION", user_id + " " + nama + " " + umur + " " + tglLhr + " " + picture + " " + kelas + " " + sekolah + " " +
                nilai_ss + " " + predikat_ss + " " + saran);

    }
}
