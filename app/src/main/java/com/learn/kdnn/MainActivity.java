package com.learn.kdnn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.learn.kdnn.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ViewModelProvider(this).get(MainViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                ,R.id.nav_product_details
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        navi
//        ngationView.setNavigationItemSelectedListener(this);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_product_details || destination.getId() == R.id.nav_bag) {
                getSupportActionBar().hide();
            } else {
                getSupportActionBar().show();
            }
        });

        binding.appBarMain.shoppingCard.setOnClickListener(v -> navController.navigate(R.id.action_nav_home_to_nav_bag));
        binding.appBarMain.accountCircle.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, binding.appBarMain.accountCircle);
            popupMenu.inflate(R.menu.account_setting);
            popupMenu.show();
            boolean isLogged = FirebaseAuth.getInstance().getCurrentUser() == null;
            if (isLogged) {
                MenuItem logout =   popupMenu.getMenu().getItem(2);
                logout.setTitle("Login");
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_logout) {
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                        this.finish();
                }
                return false;
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        Toast.makeText(this, "MENU ID: " + item.getItemId(), Toast.LENGTH_SHORT).show();
        Log.d("DS", "onNavigationItemSelected: " + "hihi");
        return true;
    }

    public NavController getMainNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    }


}