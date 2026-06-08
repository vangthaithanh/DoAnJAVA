package com.example.webdulich.dto;

import com.example.webdulich.entity.Property;
import com.example.webdulich.entity.TourReview;

public class PaymentInvoiceItem {

    private Property tour;
    private TourReview review;

    public PaymentInvoiceItem(Property tour, TourReview review) {
        this.tour = tour;
        this.review = review;
    }

    public Property getTour() {
        return tour;
    }

    public void setTour(Property tour) {
        this.tour = tour;
    }

    public TourReview getReview() {
        return review;
    }

    public void setReview(TourReview review) {
        this.review = review;
    }

    public boolean isReviewed() {
        return review != null;
    }
}
