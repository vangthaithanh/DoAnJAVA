package com.example.webdulich.service;

import com.example.webdulich.entity.ContactMessage;
import com.example.webdulich.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public ContactMessage save(ContactMessage contactMessage) {
        return contactMessageRepository.save(contactMessage);
    }
}
