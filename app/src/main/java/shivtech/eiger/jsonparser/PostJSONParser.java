package shivtech.eiger.jsonparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shivtech.eiger.models.Comment;
import shivtech.eiger.models.Post;

/**
 * Created by Sivanatarajan on 16-03-2017.
 */

public class PostJSONParser {
    String json;
    public PostJSONParser(String json)
    {
        this.json=json;
    }
    public ArrayList<Post> parsePosts()
    {
        ArrayList<Post> postList=new ArrayList<Post>();
        try {
            JSONArray postJSONArray=new JSONArray(json);
            for(int i=0;i<postJSONArray.length();i++)
            {
                Post post=new Post();
                JSONObject postObj=postJSONArray.getJSONObject(i);
                String postedBy=postObj.getString("userid");
                String postBody=postObj.getString("body");
                int likes=postObj.getInt("likes");
                int postid=postObj.getInt("postId");
                //JSONArray likedBy=postObj.getJSONArray("likedBY");

                JSONArray commentsJSONArray=postObj.getJSONArray("comments");
                ArrayList<Comment> commentArrayList=new ArrayList<Comment>();
                for(int j=0;i<commentsJSONArray.length();i++)
                {
                    JSONObject commentObj=commentsJSONArray.getJSONObject(i);
                    String comment=commentObj.getString("comment");
                    //String commentby_id=commentObj.getString("commentedby_id");
                    String commentedby_name=commentObj.getString("commentedby_name");
                    Comment commentmodel=new Comment();
                    commentmodel.setCommentContent(comment);
                    commentmodel.setCommentedBy(commentedby_name);
                    commentArrayList.add(commentmodel);
                }

                post.setPostID(postid);
                post.setPostContent(postBody);
                post.setLikeCount(likes);
                post.setPostedBy(postedBy);
                post.setComments(commentArrayList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postList;

    }
}
