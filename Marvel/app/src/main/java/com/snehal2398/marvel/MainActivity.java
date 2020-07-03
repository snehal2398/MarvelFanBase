package com.snehal2398.marvel;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static android.view.View.SCROLLBAR_POSITION_DEFAULT;
import static android.view.View.SCROLLBAR_POSITION_RIGHT;
import static android.view.View.SCROLL_INDICATOR_START;
import static android.view.View.SCROLL_INDICATOR_TOP;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<feedclass, RecyclerView.ViewHolder> recycleAdapter;
    private String tab;
    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int BANNER_AD_VIEW_TYPE = 1;
    private static final int ITEMS_PER_AD=5;


    //Back Press Action
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Do You Want To Exit ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                } else {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                });

            }
        });
        builder.show();

    }
    //Bottom Navigation Bar Selection
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.Feed:
                    tab="Feed";
                    cardData();
                    card1.setVisibility(View.VISIBLE);
                    recycleAdapter.startListening();
                    return true;
                case R.id.way:
                    tab="road";
                    cardData();
                    card1.setVisibility(View.GONE);
                    recycleAdapter.startListening();
                    return true;
                case R.id.comics:
                    tab="comics";
                    cardData();
                    card1.setVisibility(View.GONE);
                    recycleAdapter.startListening();
                    return true;
            }
            return false;
        }
    };
    //Variable Declaration
    private String EVENT_DATE_TIME = "2018-04-27 00:00:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private LinearLayout linear_layout_1, linear_layout_2;
    private TextView tv_days, tv_hour, tv_minute, tv_second;
    private Handler handler = new Handler();
    private Runnable runnable;
    private CardView card1;
    public int i=0;
    ImageView imageView;
    LinearLayoutManager mLayoutManager;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        imageView=(ImageView)findViewById(R.id.imageView);
        card1=(CardView)findViewById(R.id.card1);
        tab="Feed";
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("timer");
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("timerimage");
        // Time from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                EVENT_DATE_TIME=dataSnapshot.getValue(String.class);
               // Toast.makeText(getApplicationContext(),EVENT_DATE_TIME,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Image Url From Firebase
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String url=dataSnapshot.getValue(String.class);
                Picasso.with(getApplicationContext()).load(url).into(imageView);
                // Toast.makeText(getApplicationContext(),EVENT_DATE_TIME,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        cardData();
        card1.setVisibility(View.VISIBLE);
        MobileAds.initialize(this, "ca-app-pub-7413671897533686/2414850244");

        MobileAds.initialize(this, "ca-app-pub-7413671897533686/2457160389");
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-7413671897533686/2457160389");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        initUI();
        countDownStart();
        boolean connected = false;
        //For Checking Internetg Connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        if (!connected){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Internet Connection");
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
            });
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent in = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }
            });
            builder.show();
        }
        final int firstVisibleInListview;

        firstVisibleInListview = mLayoutManager.findFirstVisibleItemPosition();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState==SCROLL_STATE_DRAGGING) {
                    card1.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });

    }

    private void initUI() {
        linear_layout_1 = findViewById(R.id.linear_layout_1);
        linear_layout_2 = findViewById(R.id.linear_layout_2);
        tv_days = findViewById(R.id.tv_days);
        tv_hour = findViewById(R.id.tv_hour);
        tv_minute = findViewById(R.id.tv_minute);
        tv_second = findViewById(R.id.tv_second);
    }

    private void countDownStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //
                        tv_days.setText(String.format("%02d", Days));
                        tv_hour.setText(String.format("%02d", Hours));
                        tv_minute.setText(String.format("%02d", Minutes));
                        tv_second.setText(String.format("%02d", Seconds));
                    } else {
                        linear_layout_1.setVisibility(View.VISIBLE);
                        linear_layout_2.setVisibility(View.GONE);
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }






public void cardData(){

    mDatabase = FirebaseDatabase.getInstance().getReference().child(tab);
    mDatabase.keepSynced(true);

    recyclerView = (RecyclerView) findViewById(R.id.myRecycleView);

    DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child(tab);
    Query personsQuery = personsRef.orderByKey();

    recyclerView.hasFixedSize();
    mLayoutManager = new LinearLayoutManager(getApplicationContext());
    mLayoutManager.setReverseLayout(true);
    mLayoutManager.setStackFromEnd(true);

    recyclerView.setLayoutManager(mLayoutManager);

    FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<feedclass>().setQuery(personsQuery, feedclass.class).build();

    recycleAdapter=new FirebaseRecyclerAdapter<feedclass, RecyclerView.ViewHolder>(personsOptions) {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

         /*   if (viewType==AD_TYPE)
            {   AdView view1 = new AdView(MainActivity.this);
                view1.setAdSize(AdSize.SMART_BANNER);
                view1.setAdUnitId("ca-app-pub-7413671897533686/2414850244");

                float density = getApplicationContext().getResources().getDisplayMetrics().density;
                int height = Math.round(AdSize.BANNER.getHeight() * density);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,height);
                view1.setLayoutParams(params);
                try {
                    view1.loadAd(new AdRequest.Builder().build());

                }catch (Exception e){

                }
                return new MainActivity.FeedViewHolder(view1);
            }
           else*/
            //View view=null;
            RecyclerView.ViewHolder viewHolder = null;

            switch (viewType) {
                case MENU_ITEM_VIEW_TYPE:
              /*  i++;
                if (i==4)
                {
                    AdView adView = new AdView(getApplicationContext());
                    adView.setAdSize(AdSize.BANNER);
                    adView.setAdUnitId("ca-app-pub-7413671897533686/2414850244");
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                    i=0;
                    Toast.makeText(getApplicationContext(),"Here",Toast.LENGTH_SHORT).show();
                }
                */
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.feed_row, parent, false);
                    return new FeedViewHolder(view);

                    case BANNER_AD_VIEW_TYPE:
                        // fall through
                    default:
                        /*View bannerLayoutView = LayoutInflater.from(
                                parent.getContext()).inflate(R.layout.admob,
                                parent, false);
                        return new ViewHolderAdMob(bannerLayoutView);
*/
            }
return  viewHolder;
        }

     /*   @Override
        public void onDataChanged() {
            i++;
            if (i==4)
            {
                AdView adView = new AdView(getApplicationContext());
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId("ca-app-pub-7413671897533686/2414850244");
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                recyclerView.addView(adView);
                i=0;
                Toast.makeText(getApplicationContext(),"Here",Toast.LENGTH_SHORT).show();
            }
            super.onDataChanged();
        }*/

        @Override
        public int getItemViewType(int position) {

            return MENU_ITEM_VIEW_TYPE;
          // return (position % MainActivity.ITEMS_PER_AD == 0) ? BANNER_AD_VIEW_TYPE
        //            : MENU_ITEM_VIEW_TYPE;
        }



        @Override
        protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull final feedclass model) {
            int viewType = getItemViewType(position);

            switch (viewType) {
                case MENU_ITEM_VIEW_TYPE:
                    FeedViewHolder feedViewHolder = (FeedViewHolder) holder;
                    feedViewHolder.setTitle(model.getTitle());
                    feedViewHolder.setDesc(model.getDesc());
                    Date current_date = new Date();
                    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    String timeleft="";
                    Date event_date=current_date;
                    try {
                        if (model.getTime()!=null) {

                            event_date = dateFormat.parse(model.getTime());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    double diff =- event_date.getTime() + current_date.getTime();
                    diff/=3600000;

                    if (diff==0||diff<0.027)
                    {
                        timeleft="";
                    }
                    else if (diff<1&&diff>0.027)
                    {
                        timeleft=(int)(diff*60)+" min ago";
                    }
                    else if (diff==1)
                    {
                        timeleft="1 hr ago";
                    }
                    else if (diff<24&&diff>1)
                    {
                        timeleft=(int)diff+"hrs ago";
                    }
                    else if (diff==24)
                    {
                        timeleft="1 day ago";
                    }
                    else if (diff>=25)
                    {
                        diff/=24;
                        timeleft=(int)diff+"days ago";
                    }
                    feedViewHolder.setTime(timeleft);
                    feedViewHolder.setImage(getBaseContext(), model.getImage());
                    feedViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String url = model.getUrl();
                            Intent intent = new Intent(getApplicationContext(), FeedWeb.class);
                            intent.putExtra("id", url);
                            startActivity(intent);
                            i++;
                            if (i==4)
                            {
                                i=0;
                                if (mInterstitialAd.isLoaded())
                                {
                                    mInterstitialAd.show();
                                }
                            }
                        }
                    });
                case BANNER_AD_VIEW_TYPE:
                    // fall through
                default:
                 /*   ViewHolderAdMob bannerHolder = (ViewHolderAdMob) holder;
                   // AdView adView = (AdView) recyclerViewItems.get(position);
                    ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    if (adCardView.getChildCount() > 0) {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);*/
            }
            }



    };
    recyclerView.setAdapter(recycleAdapter);
}
    @Override
    public void onStart() {
        super.onStart();
        recycleAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recycleAdapter.stopListening();


    }
    public class ViewHolderAdMob extends RecyclerView.ViewHolder {

        public ViewHolderAdMob(View view) {
            super(view);


        }
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FeedViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setTime(String time){

            TextView post_time = (TextView)mView.findViewById(R.id.post_time);
            post_time.setText(time);
        }

    }

}
