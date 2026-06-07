package com.example.webdulich.service;

import com.example.webdulich.entity.BlogPost;
import com.example.webdulich.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    public BlogService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public List<BlogPost> findLatestThree() {
        return blogPostRepository.findTop3ByOrderByPublishedDateDesc();
    }

    public List<BlogPost> findAll() {
        return blogPostRepository.findAllByOrderByPublishedDateDesc();
    }

    public Optional<BlogPost> findById(Long id) {
        return blogPostRepository.findById(id);
    }
}
