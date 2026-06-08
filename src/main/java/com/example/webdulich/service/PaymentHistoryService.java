package com.example.webdulich.service;

import com.example.webdulich.dto.PaymentInvoiceItem;
import com.example.webdulich.dto.PaymentInvoiceView;
import com.example.webdulich.entity.PaymentOrder;
import com.example.webdulich.entity.Property;
import com.example.webdulich.entity.TourReview;
import com.example.webdulich.repository.PaymentOrderRepository;
import com.example.webdulich.repository.PropertyRepository;
import com.example.webdulich.repository.TourReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentHistoryService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PropertyRepository propertyRepository;
    private final TourReviewRepository tourReviewRepository;

    public PaymentHistoryService(PaymentOrderRepository paymentOrderRepository,
                                 PropertyRepository propertyRepository,
                                 TourReviewRepository tourReviewRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.propertyRepository = propertyRepository;
        this.tourReviewRepository = tourReviewRepository;
    }

    public List<PaymentInvoiceView> findPaidInvoicesByUser(Long userId) {
        if (userId == null) {
            return List.of();
        }

        return paymentOrderRepository
                .findByUserIdAndStatusIgnoreCaseOrderByCreatedAtDesc(userId, "PAID")
                .stream()
                .map(order -> buildInvoiceView(order, userId))
                .toList();
    }

    public Optional<PaymentInvoiceView> findPaidInvoiceDetail(Long userId, String orderId) {
        if (userId == null || orderId == null || orderId.isBlank()) {
            return Optional.empty();
        }

        return paymentOrderRepository
                .findByOrderIdAndUserIdAndStatusIgnoreCase(orderId, userId, "PAID")
                .map(order -> buildInvoiceView(order, userId));
    }

    public long countPaidOrdersByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }

        return paymentOrderRepository.countByUserIdAndStatusIgnoreCase(userId, "PAID");
    }

    public long countReviewsByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }

        return tourReviewRepository.countByUserId(userId);
    }

    @Transactional
    public void saveOrUpdateReview(Long userId,
                                   String userName,
                                   String orderId,
                                   Long propertyId,
                                   Integer rating,
                                   String content) {
        if (userId == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để đánh giá tour.");
        }

        if (propertyId == null) {
            throw new IllegalArgumentException("Thiếu tour cần đánh giá.");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Số sao đánh giá phải từ 1 đến 5.");
        }

        PaymentOrder paymentOrder = paymentOrderRepository
                .findByOrderIdAndUserIdAndStatusIgnoreCase(orderId, userId, "PAID")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn đã thanh toán."));

        List<Long> paidTourIds = parseCartTourIds(paymentOrder.getCartTourIds());

        if (!paidTourIds.contains(propertyId)) {
            throw new IllegalArgumentException("Tour này không nằm trong hóa đơn đã thanh toán.");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tour cần đánh giá."));

        TourReview review = tourReviewRepository
                .findByUserIdAndPaymentOrderIdAndPropertyId(userId, paymentOrder.getId(), propertyId)
                .orElseGet(TourReview::new);

        review.setPaymentOrderId(paymentOrder.getId());
        review.setOrderId(paymentOrder.getOrderId());
        review.setPropertyId(property.getId());
        review.setPropertyTitle(property.getTitle());
        review.setUserId(userId);
        review.setUserName(userName);
        review.setRating(rating);
        review.setContent(content == null ? "" : content.trim());

        tourReviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long userId, String orderId, Long propertyId) {
        if (userId == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để xóa đánh giá.");
        }

        PaymentOrder paymentOrder = paymentOrderRepository
                .findByOrderIdAndUserIdAndStatusIgnoreCase(orderId, userId, "PAID")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn đã thanh toán."));

        TourReview review = tourReviewRepository
                .findByUserIdAndPaymentOrderIdAndPropertyId(userId, paymentOrder.getId(), propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Chưa có đánh giá để xóa."));

        tourReviewRepository.delete(review);
    }

    private PaymentInvoiceView buildInvoiceView(PaymentOrder paymentOrder, Long userId) {
        List<Long> tourIds = parseCartTourIds(paymentOrder.getCartTourIds());

        if (tourIds.isEmpty()) {
            return new PaymentInvoiceView(paymentOrder, List.of());
        }

        List<Property> tours = propertyRepository.findAllById(tourIds);

        Map<Long, Property> tourMap = tours.stream()
                .collect(Collectors.toMap(Property::getId, tour -> tour));

        List<TourReview> reviews = tourReviewRepository.findByPaymentOrderIdAndUserId(paymentOrder.getId(), userId);

        Map<Long, TourReview> reviewMap = reviews.stream()
                .collect(Collectors.toMap(TourReview::getPropertyId, review -> review));

        List<PaymentInvoiceItem> items = new ArrayList<>();

        for (Long tourId : tourIds) {
            Property tour = tourMap.get(tourId);

            if (tour != null) {
                items.add(new PaymentInvoiceItem(tour, reviewMap.get(tourId)));
            }
        }

        return new PaymentInvoiceView(paymentOrder, items);
    }

    private List<Long> parseCartTourIds(String cartTourIds) {
        if (cartTourIds == null || cartTourIds.isBlank()) {
            return List.of();
        }

        List<Long> ids = new ArrayList<>();
        String[] parts = cartTourIds.split(",");

        for (String part : parts) {
            try {
                String value = part.trim();

                if (!value.isEmpty()) {
                    ids.add(Long.parseLong(value));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return ids;
    }
}
