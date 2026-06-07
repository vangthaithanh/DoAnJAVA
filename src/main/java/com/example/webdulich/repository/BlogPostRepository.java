package com.example.webdulich.repository;

import com.example.webdulich.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    List<BlogPost> findTop3ByOrderByPublishedDateDesc();

    List<BlogPost> findAllByOrderByPublishedDateDesc();
}
