package techshots.craftystudio.technology.app.techshots;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.InviteEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Collections;

import utils.AppRater;
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

    int adShowCounter;

    InterstitialAd mInterstitialAd;

    ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<>();

    boolean isSplashScreen =true ;




    private boolean pendingInterstitialAd;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        openDynamicLink();

        setContentView(R.layout.splash_main);

    }

    public void initializeActivity(){
        setContentView(R.layout.activity_main);

        isSplashScreen =false;
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


        FirebaseMessaging.getInstance().subscribeToTopic("subscribed");

        MobileAds.initialize(this, "ca-app-pub-8455191357100024~6634740792");

        initialiseInterstitialAds();

        AppRater.app_launched(this);
    }

    private void initialiseInterstitialAds() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/8786553020");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAdTimer(45000);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                interstitialAdTimer(45000);

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                adShowCounter = 0;
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

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
                            //Toast.makeText(MainActivity.this, "newsArticle id " + newsArticleID, Toast.LENGTH_SHORT).show();

                            downloadNewsArticle(newsArticleID);

                            try{
                                Answers.getInstance().logInvite(new InviteEvent().putMethod("Daynamic link").putCustomAttribute("news Id",newsArticleID));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else {
                            Log.d("DeepLink", "onSuccess: ");
                            try {
                                String newsArticleID = getIntent().getStringExtra("newsArticleID");
                                if (newsArticleID != null) {
                                    downloadNewsArticle(newsArticleID);

                                    try{
                                        Answers.getInstance().logInvite(new InviteEvent().putMethod("push Notification").putCustomAttribute("news Id",newsArticleID));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    downloadNewsArticle();
                                }
                            } catch (Exception e) {
                                downloadNewsArticle();
                            }


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
                        downloadNewsArticle();

                    }
                });
    }


    @Override
    public void onBackPressed() {
        if (!isSplashScreen) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
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

        if (id == R.id.nav_share){
            onShareClick();
        }else if(id ==R.id.nav_suggestion){
            onSuggestion();
        }else if (id == R.id.nav_rate_us){
            onRateUs();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onShareClick() {

        int applicationNameId = this.getApplicationInfo().labelRes;
        final String appPackageName = this.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(applicationNameId));
        String text = "Install Tech shots : \n";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.putExtra(Intent.EXTRA_TEXT, text + "\n " + link);
        startActivity(Intent.createChooser(i, "Share App :"));
    }

    private void onSuggestion() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion From Tech shots ");
        emailIntent.setType("text/plain");

        startActivity(Intent.createChooser(emailIntent, "Send mail From..."));
    }

    private void onRateUs() {

        final String appPackageName = this.getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));

    }




    private void downloadNewsArticle() {
        new FirebaseHandler().downloadNewsArticleList(articleFetchLimit, new FirebaseHandler.OnNewsArticleListener() {
            @Override
            public void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful) {
                if (isSuccessful) {


                    for (NewsArticle newsArticle : newsArticleArrayList) {
                        MainActivity.this.newsArticleArrayList.add(newsArticle);
                    }

                    if (isSplashScreen){
                        initializeActivity();
                    }

                    mPagerAdapter.notifyDataSetChanged();
                    initializeAds(3);


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
                if (isSplashScreen){
                    initializeActivity();
                    //set activity content and remove splash screen
                }
                if (isSuccessful) {
                    newsArticleArrayList.add(newsArticle);
                    mPagerAdapter.notifyDataSetChanged();
                }
                downloadNewsArticle();


            }
        });


    }

    private void downloadMoreNewsArticle() {


        new FirebaseHandler().downloadNewsArticleList(articleFetchLimit, newsArticleArrayList.get(newsArticleArrayList.size() - 1).getNewsArticleID(), new FirebaseHandler.OnNewsArticleListener() {
            @Override
            public void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful) {

                for (NewsArticle newsArticle : newsArticleArrayList) {
                    MainActivity.this.newsArticleArrayList.add(newsArticle);
                }

                if (isSplashScreen){
                    initializeActivity();
                }

                mPagerAdapter.notifyDataSetChanged();

                if (articleFetchLimit - newsArticleArrayList.size() > 4) {
                    isMoreArticleAvailable = false;
                }
                isLoadingMore = false;

                initializeAds(3);

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


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                checkInterstitialAd();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void checkInterstitialAd() {
        if (adShowCounter > 5 && pendingInterstitialAd) {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    public void interstitialAdTimer(long waitTill) {
        pendingInterstitialAd = false;

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                pendingInterstitialAd = true;
            }
        };


        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, waitTill);


    }

    private void initializeAds(int count) {

       /* int x = 0;
        for (int i = 0; i < newsArticleArrayList.size(); i++) {
            x++;

            NewsArticle newsArticle =newsArticleArrayList.get(i);

            if (newsArticle.isAdsView()){
                x=0;
            }

            if (x == count) {
                NewsArticle adNewsArticle = new NewsArticle();
                adNewsArticle.setAdsView(true);
                newsArticleArrayList.add(i, adNewsArticle);

            }

        }


        mPagerAdapter.notifyDataSetChanged();*/
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            adShowCounter++;

            if (position == newsArticleArrayList.size() - 3 && !isLoadingMore) {
                downloadMoreNewsArticle();
            }
            NewsArticle newsArticle = newsArticleArrayList.get(position);
            if (newsArticle.isAdsView()) {
                return AdsFragment.newInstance("", "");
            } else {
                return NewsArticleFragment.newInstance(newsArticle);
            }


        }

        @Override
        public int getCount() {
            return newsArticleArrayList.size();
        }
    }


}
