package com.esprit.microservice.events.Services;

import com.esprit.microservice.events.Entities.BlogPost;
import com.esprit.microservice.events.Repositories.BlogPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BlogPostServiceImpl implements IBlogPostService {

    private final BlogPostRepository blogPostRepository;

    @Override
    public BlogPost createPostorupdate(BlogPost post) {
        return blogPostRepository.save(post);
    }

    @Override
    public void deletePost(long id) {
        blogPostRepository.deleteById(id);
    }

    @Override
    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public BlogPost getPostById(long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BlogPost not found with id: " + id));
    }
}