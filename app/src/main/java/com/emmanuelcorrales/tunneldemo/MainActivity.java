package com.emmanuelcorrales.tunneldemo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import tunnel.VpnController;
import tunnel.VpnProfile;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int START_VPN_PROFILE = 3454;
    private static final int FILE_SELECT_CODE = 0;

    private VpnProfile mVpnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showFileChooser();
        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.select_file)), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.install_file_manager, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    importFromFile(uri);
                    break;

                case START_VPN_PROFILE:
                    VpnController.launchVpn(this, mVpnProfile);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importFromFile(Uri uri) {
        mVpnProfile = VpnController.importUri(this, uri);
        Intent intent = VpnController.prepareVpnLaunch(this, mVpnProfile);
        if (intent != null) {
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException e) {
                Log.d(TAG, getString(R.string.no_vpn_support_image));
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }
}
