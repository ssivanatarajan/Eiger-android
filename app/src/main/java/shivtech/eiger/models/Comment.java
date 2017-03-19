package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 16-03-2017.
 */

public class Comment {

    private String commentedBy;
    private String commentContent;


    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
