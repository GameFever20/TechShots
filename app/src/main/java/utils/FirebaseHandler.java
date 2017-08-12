package utils;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bunny on 09/08/17.
 */

public class FirebaseHandler {

    private FirebaseDatabase mDatabase;

    public FirebaseHandler() {
        mDatabase = FirebaseDatabase.getInstance();

    }


    public void downloadNewsArticle(final String newsArticleID, final OnNewsArticleListener onNewsArticleListener) {

        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/" + newsArticleID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                NewsArticle newsArticle = dataSnapshot.getValue(NewsArticle.class);

                if (newsArticle != null) {
                    newsArticle.setNewsArticleID(dataSnapshot.getKey());
                }
                onNewsArticleListener.onNewsArticle(newsArticle, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onNewsArticleListener.onNewsArticle(null, false);
            }
        });


    }

    public void downloadNewsArticleList(int limitTo, final OnNewsArticleListener onNewsArticleListener) {
        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/");

        Query myref2 = myRef.limitToLast(limitTo);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<NewsArticle>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    NewsArticle newsArticle = snapshot.getValue(NewsArticle.class);
                    if (newsArticle != null) {
                        newsArticle.setNewsArticleID(snapshot.getKey());
                    }
                    newsArticleArrayList.add(newsArticle);

                }

                Collections.reverse(newsArticleArrayList);

                onNewsArticleListener.onNewsArticleList(newsArticleArrayList, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onNewsArticleListener.onNewsArticleList(null, false);

            }
        });
    }

    public void downloadNewsArticleList(int limitTo, String lastNewsArticleID, final OnNewsArticleListener onNewsArticleListener) {
        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/");

        Query myref2 = myRef.limitToLast(limitTo).endAt(lastNewsArticleID);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<NewsArticle>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    NewsArticle newsArticle = snapshot.getValue(NewsArticle.class);
                    if (newsArticle != null) {
                        newsArticle.setNewsArticleID(snapshot.getKey());
                    }
                    newsArticleArrayList.add(newsArticle);

                }

                newsArticleArrayList.remove(newsArticleArrayList.size()-1);
                Collections.reverse(newsArticleArrayList);

                onNewsArticleListener.onNewsArticleList(newsArticleArrayList, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onNewsArticleListener.onNewsArticleList(null, false);

            }
        });
    }


    public void uploadLike(Like like, final OnLikeListener onLikeListener) {
        DatabaseReference myRef = mDatabase.getReference().child("likes/");
        myRef.push().setValue(like).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onLikeListener.onLikeUpload(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                onLikeListener.onLikeUpload(true);
            }
        });


    }

    public interface OnNewsArticleListener {
        void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful);

        void onNewsArticle(NewsArticle newsArticle, boolean isSuccessful);
    }


    public interface OnLikeListener {
        void onLikeUpload(boolean isSuccessful);
    }

}
