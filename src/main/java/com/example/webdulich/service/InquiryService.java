package com.example.webdulich.service;

import com.example.webdulich.entity.Inquiry;
import com.example.webdulich.repository.InquiryRepository;
import org.springframework.stereotype.Service;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public Inquiry save(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }
}
