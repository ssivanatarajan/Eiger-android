package shivtech.eiger.models;

import java.util.ArrayList;

/**
 * Created by Sivanatarajan on 16-03-2017.
 */

public class Post {
    private  String postedBy;
    private String postContent;
    private int likeCount;
    private boolean islikedByYou;
    private int postID;
    private ArrayList<Comment> comments;
    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean islikedByYou() {
        return islikedByYou;
    }

    public void setIslikedByYou(boolean islikedByYou) {
        this.islikedByYou = islikedByYou;
    }



    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
