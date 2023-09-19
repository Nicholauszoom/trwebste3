package com.example.demo.Service;

import com.example.demo.email.EmailSender;
import com.example.demo.email.EmailService;
import com.example.demo.model.Comment;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService 
{
   

    @Autowired
    CommentRepository commentRepository;

     @Autowired
private EmailService emailService;

public  void CommentController(CommentRepository commentRepository, EmailService emailService) {
    this.commentRepository = commentRepository;
    this.emailService = emailService;
}


    public ResponseEntity<Comment> sendComment(Comment comment) {
        

        Comment savedComment = commentRepository.save(comment);

    // Send email notification
    emailService.send("nicholauszoom95@gmail.com", "A new comment has been posted: " + savedComment.getMessage());

    return ResponseEntity.ok(savedComment);


    }
    public List<Comment> getAllComments(){return commentRepository.findAll();}

    public void deleteCommentById(int id){commentRepository.deleteById(id);}


    public Optional<Comment> getCommentById(int id){return commentRepository.findById(id);}




}

