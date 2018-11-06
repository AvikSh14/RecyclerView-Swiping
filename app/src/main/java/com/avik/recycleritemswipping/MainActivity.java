package com.avik.recycleritemswipping;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewAdapter viewAdapter;
    private String itemText;
    final String localePref = "localePref";
    private static final String LOCALE_KEY = "locale";
    private static final String BANGLA_LOCALE = "bn";
    private static final String ENGLISH_LOCALE = "en";
    private final String ITEM_TEXT = "item";
    FirebaseRemoteConfig remoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String languageProperty = "user_locale";
    long cacheExpiration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checkPermissions();
        updateUI();
    }

    private void updateUI() {
        recyclerView = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("onCreate", "called");
        updateList();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);
        Log.i("Config", "new config found");
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig.setConfigSettings(configSettings);
        remoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "Fetch Succeeded",
//                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            remoteConfig.activateFetched();
                            updateLocale();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                            Locale locale = new Locale(ENGLISH_LOCALE);
                            setLocale(locale);
                        }

                    }
                });
    }

    private void updateLocale() {
        String curLocale = remoteConfig.getString(LOCALE_KEY);
        // remoteConfig.
        itemText = remoteConfig.getString(ITEM_TEXT);
        Log.i("CurrentLocale", itemText);
        Toast.makeText(MainActivity.this, itemText,
                Toast.LENGTH_SHORT).show();
        Locale locale = new Locale(curLocale);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        onConfigurationChanged(config);
//        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //setContentView(R.layout.activity_main);
    }

    private boolean isAllPermissionGranted(int[] grants) {
        for (int grant : grants) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (isAllPermissionGranted(grantResults)) {
                downloadFileWithRetro();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            downloadFileWithRetro();
        }
    }

    private void downloadFileWithRetro() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://192.168.4.117:5458/").client(okHttpClient);
        Retrofit retrofit = builder.build();

        FileDownloadClient fileDownloadClient = retrofit.create(FileDownloadClient.class);

        Call<ResponseBody> call = fileDownloadClient.downloadFile();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_LONG).show();
                Log.d("ResponseMessage", t.getMessage());
            }
        });
    }

    private void updateList() {
        final List<RecyclerItem> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            RecyclerItem item = new RecyclerItem();
            items.add(item);
            //itemText = getResources().getString(R.string.item);
            items.get(i).setItemText(itemText + " " + (i + 1));
        }
        viewAdapter = new ViewAdapter(items);
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.notifyDataSetChanged();
        SwipeController swipeController = new SwipeController(viewAdapter, new SwipeControllerActions() {
            @Override
            public void viewDetails(int position) {
                Intent intent = new Intent(MainActivity.this, DummyActivity.class);
                intent.putExtra(getResources().getString(R.string.intent_data), items.get(position).getItemText());
                startActivity(intent);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.language_option_menu, menu);
        return true;
    }

    private void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        onConfigurationChanged(config);
        //getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.english:
                Locale locale = new Locale(ENGLISH_LOCALE);
                setLocale(locale);
                Toast.makeText(this, "Locale in English !", Toast.LENGTH_SHORT).show();
                mFirebaseAnalytics.setUserProperty(languageProperty, "en");
                remoteConfig.fetch(cacheExpiration)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    remoteConfig.activateFetched();
                                    updateLocale();
                                }
                            }
                        });
                // recreate();
                break;
            case R.id.bangla:
                Locale locale2 = new Locale(BANGLA_LOCALE);
                setLocale(locale2);
                Toast.makeText(this, "Locale in Bangla !", Toast.LENGTH_SHORT).show();
                mFirebaseAnalytics.setUserProperty(languageProperty, "bn");
                remoteConfig.fetch(cacheExpiration)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    remoteConfig.activateFetched();
                                    updateLocale();
                                }
                            }
                        });
                //  recreate();
                break;
        }
        return super.

                onOptionsItemSelected(item);
    }
}
