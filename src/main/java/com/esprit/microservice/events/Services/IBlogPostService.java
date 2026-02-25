package com.esprit.microservice.events.Services;

import com.esprit.microservice.events.Entities.BlogPost;

import java.util.List;

public interface IBlogPostService {

    BlogPost createPostorupdate(BlogPost post);

    void deletePost(long id);
    List<BlogPost> getAllPosts();
    BlogPost getPostById(long id);


}
