package com.example.webdulich.dto;

import com.example.webdulich.entity.PaymentOrder;

import java.util.List;

public class PaymentInvoiceView {

    private PaymentOrder paymentOrder;
    private List<PaymentInvoiceItem> items;

    public PaymentInvoiceView(PaymentOrder paymentOrder, List<PaymentInvoiceItem> items) {
        this.paymentOrder = paymentOrder;
        this.items = items;
    }

    public PaymentOrder getPaymentOrder() {
        return paymentOrder;
    }

    public void setPaymentOrder(PaymentOrder paymentOrder) {
        this.paymentOrder = paymentOrder;
    }

    public List<PaymentInvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<PaymentInvoiceItem> items) {
        this.items = items;
    }

    public int getTourCount() {
        return items == null ? 0 : items.size();
    }

    public int getReviewCount() {
        if (items == null) {
            return 0;
        }

        return (int) items.stream()
                .filter(PaymentInvoiceItem::isReviewed)
                .count();
    }
}
