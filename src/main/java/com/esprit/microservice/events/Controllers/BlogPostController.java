package com.esprit.microservice.events.Controllers;

import com.esprit.microservice.events.Entities.BlogPost;
import com.esprit.microservice.events.Services.IBlogPostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blogpost")
@AllArgsConstructor
public class BlogPostController {

    private final IBlogPostService blogPostService;

    @PostMapping("/addblogpost")
    public BlogPost addBlogPost(@RequestBody BlogPost post) {
        return blogPostService.createPostorupdate(post);
    }

    @DeleteMapping("/delete/{idblogpost}")
    public void deleteBlogPost(@PathVariable int idblogpost) {
        blogPostService.deletePost(idblogpost);
    }

    @GetMapping("/findallblog")
    public List<BlogPost> findAllBlog() {
        return blogPostService.getAllPosts();
    }

    @GetMapping("/findblogbyid/{idblog}")
    public BlogPost findBlogById(@PathVariable int idblog) {
        return blogPostService.getPostById(idblog);
    }
}