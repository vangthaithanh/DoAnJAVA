package com.example.webdulich.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(length = 80)
    private String category;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String author;

    private LocalDate publishedDate;

    @Column(length = 300)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    public BlogPost() {
    }

    public BlogPost(String title, String category, String imageUrl, String author, LocalDate publishedDate,
                    String summary, String content) {
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl;
        this.author = author;
        this.publishedDate = publishedDate;
        this.summary = summary;
        this.content = content;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public String getAuthor() { return author; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public String getSummary() { return summary; }
    public String getContent() { return content; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setContent(String content) { this.content = content; }
}
