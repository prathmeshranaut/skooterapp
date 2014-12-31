package net.aayush.skooterapp.data;

import java.util.ArrayList;
import java.util.List;

public class PostData {
    private List<Post> mPosts = new ArrayList<Post>();

    public List<Post> getPosts() {
        return mPosts;
    }

    //TODO Fix this with JSON data
    public PostData() {
//        addItem(new Post("Post content 1", 100, 20, "5 minutes ago"));
//        addItem(new Post("Post content 2", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 3", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 4", 100, 20, "5 minutes ago"));
//        addItem(new Post("Post content 5", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 6", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 7", 100, 20, "5 minutes ago"));
//        addItem(new Post("Post content 8", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 9", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 10", 100, 20, "5 minutes ago"));
//        addItem(new Post("Post content 11", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 12", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 13", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 14", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 15", 100, 20, "5 minutes ago"));
//        addItem(new Post("Post content 16", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 17", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 18", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 19", 20, 10, "25 minutes ago"));
//        addItem(new Post("Post content 20", 100, 20, "5 minutes ago"));
//        addItem(new Post("Post content 21", 50, 20, "15 minutes ago"));
//        addItem(new Post("Post content 22", 20, 10, "25 minutes ago"));
    }

    private void addItem(Post item){
        mPosts.add(item);
    }
}
