package com.learn.kdnn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.learn.kdnn.databinding.ActivityMainBinding;
import com.learn.kdnn.model.User;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (!isInternetConected()) {
            this.showConnectInternetDialog();
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getUserInfo();
        }
        //update bag counter
        updateBagCounter(viewModel.getBag().getValue().size());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                ,R.id.nav_product_details
                R.id.nav_home, R.id.nav_gallery, R.id.nav_favorites)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_product_details || destination.getId() == R.id.nav_bag) {
                getSupportActionBar().hide();
            } else {
                getSupportActionBar().show();

            }
        });

        //view shopping cart
        binding.appBarMain.shoppingCard.setOnClickListener(v -> navController.navigate(R.id.action_nav_home_to_nav_bag));
        //acount setting
        binding.appBarMain.accountCircle.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, binding.appBarMain.accountCircle);
            popupMenu.inflate(R.menu.account_setting);
            popupMenu.show();
            boolean isLogged = FirebaseAuth.getInstance().getCurrentUser() == null;
            if (isLogged) {
                MenuItem logout = popupMenu.getMenu().getItem(2);
                logout.setTitle("Login");
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                return handleAccountPopupItemMenuClick(item);
            });
        });


//        showUpdateInfoDialog();

    }

    private User getUserInfo() {

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {
                    Map<String, Object> data = value.getData();
                    User user = null;
                    if (value.exists() && data != null) {
                        user = new User();
                        user.setAddress((String) data.get("address"));
                        user.setPhoneNumber((String) data.get("phone"));
                    }
                    Log.d(TAG, "getUserInfo: " + user.toString());
                    viewModel.getUser().setValue(user);
                });
        return null;
    }

    private boolean handleAccountPopupItemMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout: {
                FirebaseAuth.getInstance().signOut();
                startLoginActivity();
                break;
            }
            case R.id.item_my_account: {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent i = new Intent(this, AccountActivity.class);
                    startActivity(i);
                } else {
                    startLoginActivity();
                }

                break;
            }
            case R.id.item_my_order: {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent i = new Intent(this, AccountActivity.class);
                    i.putExtra(AccountActivity.ARG_VIEW_PAGER_POS,1);
                    startActivity(i);
                } else {
                    startLoginActivity();
                }
                break;
            }
        }
        return true;
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        this.finish();
    }

    private void showUpdateInfoDialog() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Map<String, Object> data = snapshot.getData();
                        Log.d(TAG, "onSuccess: " + data.toString());
                    } else {
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_require_update_info, null, false));
                        dialog.show();
                        ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.closeRequireUpdateInfo);
                        Button btnOpenUpdate = (Button) dialog.findViewById(R.id.openUpdateInfoDialog);
                        btnClose.setOnClickListener(v -> dialog.dismiss());
                        btnOpenUpdate.setOnClickListener(v -> {
                        });
                        Log.d(TAG, "onSuccess: " + "no such data");
                    }
                })
                .addOnFailureListener(e -> {
//                    No internet
                });
    }


    private boolean isInternetConected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }

    private void showConnectInternetDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Please connect to network first")
                .setCancelable(false)
                .setPositiveButton("Connect", ((dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))))
                .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                .show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    public NavController getMainNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    }

    public void updateBagCounter(int i) {
        binding.appBarMain.countBagItem.setText(String.valueOf(i));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "HIHIH", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}