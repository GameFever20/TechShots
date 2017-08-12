package techshots.craftystudio.technology.app.techshots;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import utils.FirebaseHandler;
import utils.Like;
import utils.NewsArticle;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsArticleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsArticleFragment extends Fragment {
    private NewsArticle newsArticle;

    private OnFragmentInteractionListener mListener;

    public NewsArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsArticleFragment newInstance(NewsArticle newsArticle) {
        NewsArticleFragment fragment = new NewsArticleFragment();
        Bundle args = new Bundle();
        args.putSerializable("newsArticle", newsArticle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.newsArticle = (NewsArticle) getArguments().getSerializable("newsArticle");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_article, container, false);
        //initializeView

        TextView titleText = (TextView) view.findViewById(R.id.fragmentNewsArticle_title_textView);
        titleText.setText(newsArticle.getNewsArticleTitle());
        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("newsUrl", newsArticle.getNewsArticleSourceLink());
                startActivity(intent);
            }
        });


        TextView textView = (TextView) view.findViewById(R.id.fragmentNewsArticle_description_textView);
        textView.setText(newsArticle.getNewsArticleDescription());


        //show image
        ImageView imageView1 = (ImageView) view.findViewById(R.id.fragment_newsArticle_imageView);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("newsArticleImage/" + newsArticle.getNewsArticleID() + "/" + "main");


        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .crossFade(100)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView1);


        Button likeButton = (Button) view.findViewById(R.id.fragmentNewsArticle_like_view);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Like like = new Like();
                like.setNewsArticleID(newsArticle.getNewsArticleID());
                like.setNewsArticleTitle(newsArticle.getNewsArticleTitle());
                new FirebaseHandler().uploadLike(like, new FirebaseHandler.OnLikeListener() {
                    @Override
                    public void onLikeUpload(boolean isSuccessful) {
                        if (isSuccessful) {

                        }
                    }
                });
            }
        });


        Button shareButton = (Button) view.findViewById(R.id.fragmentNewsArticle_share_view);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onShareClick();
            }
        });

        return view;
    }

    private void onShareClick() {

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/Ae4Mhw?shotID=" + newsArticle.getNewsArticleID()))
                .setDynamicLinkDomain("te6xt.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("techshots.craftystudio.technology.app.techshots")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Example of a Dynamic Link")
                                .setDescription("This link works whether the app is installed or not!")
                                .setImageUrl(Uri.parse("https://play.google.com/store/apps/details?id=app.craftystudio.vocabulary.dailyeditorial"))
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("share")
                                .setMedium("social")
                                .setCampaign("example-promo")
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();

                            openShareDialog(shortLink);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void openShareDialog(Uri shortUrl) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortUrl
                + "\n\nshort link and flow link");
        startActivity(Intent.createChooser(sharingIntent, "Share Editorial via"));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
