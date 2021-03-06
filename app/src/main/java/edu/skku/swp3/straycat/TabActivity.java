package edu.skku.swp3.straycat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import edu.skku.swp3.straycat.Donation.DonationMainFragment;
import edu.skku.swp3.straycat.Map.CatListActivity;
import edu.skku.swp3.straycat.Map.CatListItem;
import edu.skku.swp3.straycat.Map.CustomInfoWindowAdapter;
import edu.skku.swp3.straycat.Upload.ShareActivity;
import edu.skku.swp3.straycat.Setting.Setting;
import edu.skku.swp3.straycat.Feed.PostFragment;

public class TabActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentManager fragmentManager;
    private View fragmentHolder;
    private final SupportMapFragment supportMapFragment = new SupportMapFragment();
    private final DonationMainFragment donationMainFragment = new DonationMainFragment();
    private static final int MY_LOCATION_REQUEST_CODE = 11;
    private PostFragment postFragment;
    private GoogleMap mMap;
    private int idx = 0;

    private ListView listView;

    private HashMap<String, ArrayList<CatListItem>> catListMap = new HashMap<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_feed:
                    transaction.replace(R.id.nav_fragment, new PostFragment(), "feed_main").commit();
                    return true;
                case R.id.nav_map:
                    fragment = fragmentManager.findFragmentByTag("map");
                    if (fragment == null) {
                        transaction.add(R.id.nav_fragment, supportMapFragment, "map").commit();
                    } else {
                        transaction.replace(R.id.nav_fragment, fragment, "map").commit();
                    }
                    return true;
                case R.id.nav_plus:
                    Intent intent = new Intent(TabActivity.this, ShareActivity.class);
                    startActivityForResult(intent,3000);
                    return true;
                case R.id.nav_donation:
                    transaction.replace(R.id.nav_fragment, donationMainFragment, "donation_main").commit();
                    return true;
                case R.id.nav_setting:
                    transaction.replace(R.id.nav_fragment,new Setting(),"setting").commit();
                    return true;
            }

            transaction.commit();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = (ListView) findViewById(R.id.lv_tag_cat);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentHolder = findViewById(R.id.nav_fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.nav_fragment, new PostFragment());
        transaction.commit();
        removeShiftMode(navigation);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        supportMapFragment.getMapAsync(this);

    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }



    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @SuppressLint("RestrictedApi")
    void removeShiftMode(BottomNavigationView  view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);

                item.setShiftingMode(false);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//       ArrayList<Marker> markers = new ArrayList<Marker>();
        LatLng center = new LatLng(36.427397, 128.064719);
        LatLng testMarker = new LatLng(37.553042, 126.986792);

        Marker test = mMap.addMarker(new MarkerOptions()
                .position(testMarker)
                .title("5")
                .snippet("15")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot)));



//        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(37.275774, 127.010741))
//        .title("mark2")));
//        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(35.160694, 129.066059))
//        .title("mark3")));
//        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(35.155051, 126.829565))
//        .title("mark4")));
//        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(37.548287, 128.483212))
//        .title("mark5")));

//        for (Marker marker : markers) {
//            marker.setVisible(true);
//
//            //marker.remove(); <-- works too!
//        }

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(TabActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        test.showInfoWindow();
        test.setVisible(true);


        ArrayList<CatListItem> catList = new ArrayList<>();
        catList.add(new CatListItem(R.drawable.cat_image1,"성균관대 신관 A동 앞","러시안 블루","나비"));
        catList.add(new CatListItem(R.drawable.cat_image2,"호매실도서관 앞","터키쉬 앙고라","디도냥이"));

        catListMap.put(test.getId(), catList);

        Log.d("TabActivity", " " + test.getId());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(testMarker, 13));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                TabActivity.this.onInfoWindowClick(marker);
            }
        });
    }
    public void onInfoWindowClick(Marker marker) {
        ArrayList<CatListItem> catList = catListMap.get(marker.getId());
        Intent intent = new Intent(this, CatListActivity.class);
        Log.d("TabActivity", " " + marker.getId());
        intent.putExtra("catList", catList);
        startActivity(intent);
    }
    // 처리된 결과 코드 (resultCode) 가 RESULT_OK 이면 requestCode 를 판별해 결과 처리를 진행한다.
    // 전달준다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                // MainActivity 에서 요청할 때 보낸 요청 코드 (3000)
                case 3000:
                    postFragment.addData(data.getStringExtra("result"));
                    break;
            }
        }
    }

}