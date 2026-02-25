package com.esprit.microservice.events.Services;

import com.esprit.microservice.events.Entities.Comment;

import java.util.List;

public interface ICommentService {

    Comment addComment(Comment comment);

    // Update
    Comment updateComment(Long id, Comment comment);

    // Delete
    void deleteComment(Long id);

    // Get All
    List<Comment> getAllComments();

    // Get By Id
    Comment getCommentById(Long id);
}
