package utils;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by bunny on 09/08/17.
 */

public class NewsArticle implements Serializable {

    String newsArticleTitle, newsArticleDescription, newsArticleTag ,newsArticleCategory, newsArticleCompany, newsArticleSource, newsArticleSourceLink, newsArticleImageLink, newsArticleImageAddress, newsArticleID;

    int companyIndex ,newsCategoryIndex ,newsViews ,newsLikes ;
    long timeInMillis ;
    boolean adsView =false;


    public NewsArticle() {
    }

    public String getNewsArticleTitle() {
        return newsArticleTitle;
    }

    public void setNewsArticleTitle(String newsArticleTitle) {
        this.newsArticleTitle = newsArticleTitle;
    }

    public String getNewsArticleDescription() {
        return newsArticleDescription;
    }

    public void setNewsArticleDescription(String newsArticleDescription) {
        this.newsArticleDescription = newsArticleDescription;
    }

    public String getNewsArticleTag() {
        return newsArticleTag;
    }

    public void setNewsArticleTag(String newsArticleTag) {
        this.newsArticleTag = newsArticleTag;
    }

    public String getNewsArticleCategory() {
        return newsArticleCategory;
    }

    public void setNewsArticleCategory(String newsArticleCategory) {
        this.newsArticleCategory = newsArticleCategory;
    }

    public String getNewsArticleCompany() {
        return newsArticleCompany;
    }

    public void setNewsArticleCompany(String newsArticleCompany) {
        this.newsArticleCompany = newsArticleCompany;
    }

    public String getNewsArticleSource() {
        return newsArticleSource;
    }

    public void setNewsArticleSource(String newsArticleSource) {
        this.newsArticleSource = newsArticleSource;
    }

    public String getNewsArticleSourceLink() {
        return newsArticleSourceLink;
    }

    public void setNewsArticleSourceLink(String newsArticleSourceLink) {
        this.newsArticleSourceLink = newsArticleSourceLink;
    }

    public String getNewsArticleImageLink() {
        return newsArticleImageLink;
    }

    public void setNewsArticleImageLink(String newsArticleImageLink) {
        this.newsArticleImageLink = newsArticleImageLink;
    }

    public String getNewsArticleImageAddress() {
        return newsArticleImageAddress;
    }

    public void setNewsArticleImageAddress(String newsArticleImageAddress) {
        this.newsArticleImageAddress = newsArticleImageAddress;
    }

    public String getNewsArticleID() {
        return newsArticleID;
    }

    public void setNewsArticleID(String newsArticleID) {
        this.newsArticleID = newsArticleID;
    }

    public int getCompanyIndex() {
        return companyIndex;
    }

    public void setCompanyIndex(int companyIndex) {
        this.companyIndex = companyIndex;
    }

    public int getNewsCategoryIndex() {
        return newsCategoryIndex;
    }

    public void setNewsCategoryIndex(int newsCategoryIndex) {
        this.newsCategoryIndex = newsCategoryIndex;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public int getNewsViews() {
        return newsViews;
    }

    public void setNewsViews(int newsViews) {
        this.newsViews = newsViews;
    }

    public int getNewsLikes() {
        return newsLikes;
    }

    public void setNewsLikes(int newsLikes) {
        this.newsLikes = newsLikes;
    }

    public boolean isAdsView() {
        return adsView;
    }

    public void setAdsView(boolean adsView) {
        this.adsView = adsView;
    }


    public String resolveTime(){
        Calendar calendar = Calendar.getInstance();


        long currenttime = calendar.getTimeInMillis();


        //calculate difference in time
        //long timeDifference = (currenttime - newsTime);

        if ((currenttime - timeInMillis) <= 0 || timeInMillis <= 1493013649175l) {
            return "";
        }

        long numberOfHour = (currenttime - timeInMillis) / 3600000;
        if (numberOfHour == 0) {
            return "now";
        } else if (numberOfHour < 24) {
            return String.valueOf(numberOfHour) + " hour ago";
        } else {

            long numberOfDays = numberOfHour / 24;

            if (numberOfDays < 7) {
                return String.valueOf(numberOfDays) + " day ago";
            } else {

                long numberOfWeek = numberOfDays / 7;
                if (numberOfWeek <= 4) {
                    return String.valueOf(numberOfWeek) + " week ago";
                } else {

                    long numberOfMonth = numberOfWeek / 4;
                    if (numberOfMonth <= 12) {
                        return String.valueOf(numberOfMonth) + " month ago";
                    } else {

                        long numberOfYear = numberOfMonth / 12;

                        return String.valueOf(numberOfYear) + " year ago";

                    }

                }

            }

        }

    }
}
