package com.esprit.microservice.events.Controllers;

import com.esprit.microservice.events.Entities.Comment;
import com.esprit.microservice.events.Services.ICommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")

@AllArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PostMapping("/add")
    public Comment addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    // ✏️ Modifier un commentaire
    @PutMapping("/update/{id}")
    public Comment updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return commentService.updateComment(id, comment);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    // 📃 Récupérer tous les commentaires
    @GetMapping("/all")
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    // 🔍 Récupérer un commentaire par id
    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }


}
