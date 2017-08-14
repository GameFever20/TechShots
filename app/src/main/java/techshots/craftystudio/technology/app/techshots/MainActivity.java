package techshots.craftystudio.technology.app.techshots;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;
import java.util.Collections;

import utils.FirebaseHandler;
import utils.NewsArticle;
import utils.ZoomOutPageTransformer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    boolean isMoreArticleAvailable = true;
    boolean isLoadingMore = false;
    int articleFetchLimit = 5;

    ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<>();


    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mPager = (ViewPager) findViewById(R.id.mainActivity_viewPager);
        initializeViewPager();
        openDynamicLink();


    }

    private void openDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("DeepLink", "onSuccess: " + deepLink);

                            String newsArticleID = deepLink.getQueryParameter("shotID");
                            Toast.makeText(MainActivity.this, "newsArticle id " + newsArticleID, Toast.LENGTH_SHORT).show();

                            downloadNewsArticle(newsArticleID);

                        } else {
                            Log.d("DeepLink", "onSuccess: ");

                            downloadNewsArticle();
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DeepLink", "getDynamicLink:onFailure", e);

                    }
                });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void downloadNewsArticle() {
        new FirebaseHandler().downloadNewsArticleList(articleFetchLimit, new FirebaseHandler.OnNewsArticleListener() {
            @Override
            public void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful) {
                if (isSuccessful) {


                    for (NewsArticle newsArticle : newsArticleArrayList) {
                        MainActivity.this.newsArticleArrayList.add(newsArticle);
                    }

                    mPagerAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onNewsArticle(NewsArticle newsArticle, boolean isSuccessful) {

            }
        });
    }

    private void downloadNewsArticle(String newsArticleID) {

        final FirebaseHandler firebaseHandler = new FirebaseHandler();


        firebaseHandler.downloadNewsArticle(newsArticleID, new FirebaseHandler.OnNewsArticleListener() {
            @Override
            public void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful) {

            }

            @Override
            public void onNewsArticle(NewsArticle newsArticle, boolean isSuccessful) {
                if (isSuccessful) {
                    newsArticleArrayList.add(newsArticle);
                    mPagerAdapter.notifyDataSetChanged();
                }
                downloadNewsArticle();
            }
        });


    }

    private void downloadMoreNewsArticle() {


        new FirebaseHandler().downloadNewsArticleList(articleFetchLimit, newsArticleArrayList.get(newsArticleArrayList.size()-1).getNewsArticleID(), new FirebaseHandler.OnNewsArticleListener() {
            @Override
            public void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful) {

                for (NewsArticle newsArticle : newsArticleArrayList) {
                    MainActivity.this.newsArticleArrayList.add(newsArticle);
                }
                mPagerAdapter.notifyDataSetChanged();

                if (articleFetchLimit - newsArticleArrayList.size() > 4) {
                    isMoreArticleAvailable = false;
                }
                isLoadingMore = false;

            }

            @Override
            public void onNewsArticle(NewsArticle newsArticle, boolean isSuccessful) {

            }
        });

    }


    private void initializeViewPager() {

// Instantiate a ViewPager and a PagerAdapter.

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());


    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == newsArticleArrayList.size() - 3 && !isLoadingMore) {
                downloadMoreNewsArticle();
            }
            return NewsArticleFragment.newInstance(newsArticleArrayList.get(position));

        }

        @Override
        public int getCount() {
            return newsArticleArrayList.size();
        }
    }


}
