package id.suksesit.pensil;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends Fragment {

    RelativeLayout view;
    public static TextView nama,slmtdtng;
    public static CircleImageView fotoSiswa;
    FirebaseFirestore db;
    SoalDB soalDB;

    public static int noSoal = 1;
    public static ArrayList<modelSoal> arraySoal = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = (RelativeLayout) inflater.inflate(R.layout.activity_home, container, false);
        getActivity().setTitle("Home");
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        slmtdtng = view.findViewById(R.id.tSlmtDtng);
        nama = view.findViewById(R.id.tNama);
        fotoSiswa = view.findViewById(R.id.foto_siswa);
        saveProfile(MainActivity.nama);
        if(MainActivity.umur.equals("") || MainActivity.picture.equals("")) {
            slmtdtng.setText("Selamat Datang");
            nama.setText("Pengguna Baru");
            MainActivity.picture = "aaaaa_photoprof.png";
            Picasso.with(getActivity())
                    .load(R.drawable.ic_photoprof)
                    .placeholder(R.drawable.ic_photoprof)
                    .error(R.drawable.ic_photoprof)
                    .into(fotoSiswa);
        }
        else {
            slmtdtng.setText("Selamat Datang");
            nama.setText(MainActivity.nama);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            Picasso.with(getActivity())
                    .load(sharedPreferences.getString("picture",""))
                    .placeholder(R.drawable.ic_photoprof)
                    .error(R.drawable.ic_photoprof)
                    .into(fotoSiswa);
        }
        getData();
        return view;
    }

    private void saveProfile(String nama) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("nama", nama);
        MainActivity.nama = nama;

        editor.apply();
    }

    private void getData() {
        soalDB = new SoalDB(getActivity());
        final ProgressDialog pLoading = new ProgressDialog(getActivity());
        pLoading.setTitle("Memuat data ...");
        pLoading.setMessage("Silahkan tunggu sejenak");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("kuisioner").whereEqualTo("jenis", "soal")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }

                                if (arraySoal != null && arraySoal.size()>0) {
                                    arraySoal.clear();
                                }
                                soalDB.dropTable();
                                SQLiteDatabase sqlDb = soalDB.getWritableDatabase();
                                soalDB.onCreate(sqlDb);
                                if (querySnapshot != null) {
                                    for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                                        if (change.getType() == DocumentChange.Type.ADDED) {
                                            soalDB.insertSoal(
                                                    Integer.parseInt(String.valueOf(change.getDocument().getData().get("id"))),
                                                    String.valueOf(change.getDocument().get("kategori")),
                                                    String.valueOf(change.getDocument().get("pertanyaan")),
                                                    String.valueOf(change.getDocument().get("status"))
                                            );
                                            pLoading.dismiss();
                                        }

                                        String source = querySnapshot.getMetadata().isFromCache() ?
                                                "local cache" : "server";
                                        Log.d("Tag ", "Data fetched from " + source);
                                    }
                                    soalDB.showAllSoal();
                                    Log.d("array soal : ",arraySoal.toString());
                                }

                            }
                        });
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pLoading.isShowing()) {
                    pLoading.dismiss();
                }
            }
        },4000);
    }
}
