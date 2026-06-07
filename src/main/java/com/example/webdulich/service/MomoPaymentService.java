package com.example.webdulich.service;

import com.example.webdulich.config.MomoProperties;
import com.example.webdulich.dto.MomoCreatePaymentResponse;
import com.example.webdulich.entity.PaymentOrder;
import com.example.webdulich.entity.Property;
import com.example.webdulich.repository.PaymentOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MomoPaymentService {

    /*
     * Tài khoản MoMo demo.
     *
     * Số dư chỉ lưu tạm trong RAM.
     * Khi tắt app rồi chạy lại, số dư quay về mặc định.
     */
    private static final Map<String, String> DEMO_PASSWORDS = new LinkedHashMap<>();
    private static final Map<String, BigDecimal> DEMO_INITIAL_BALANCES = new LinkedHashMap<>();

    static {
        DEMO_PASSWORDS.put("0987654321", "123456");
        DEMO_PASSWORDS.put("0987654322", "123456");
        DEMO_PASSWORDS.put("0987654323", "123456");
        DEMO_PASSWORDS.put("0987654324", "123456");
        DEMO_PASSWORDS.put("0987654325", "123456");

        DEMO_INITIAL_BALANCES.put("0987654321", new BigDecimal("1000000"));      // 1 triệu
        DEMO_INITIAL_BALANCES.put("0987654322", new BigDecimal("10000000"));     // 10 triệu
        DEMO_INITIAL_BALANCES.put("0987654323", new BigDecimal("30000000"));     // 30 triệu
        DEMO_INITIAL_BALANCES.put("0987654324", new BigDecimal("50000000"));     // 50 triệu
        DEMO_INITIAL_BALANCES.put("0987654325", new BigDecimal("100000000"));    // 100 triệu
    }

    private final Map<String, BigDecimal> demoBalances = new LinkedHashMap<>();

    private final MomoProperties momoProperties;
    private final PaymentOrderRepository paymentOrderRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public MomoPaymentService(MomoProperties momoProperties,
                              PaymentOrderRepository paymentOrderRepository,
                              ObjectMapper objectMapper) {
        this.momoProperties = momoProperties;
        this.paymentOrderRepository = paymentOrderRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        resetDemoBalances();
    }

    private void resetDemoBalances() {
        demoBalances.clear();
        demoBalances.putAll(DEMO_INITIAL_BALANCES);
    }

    public boolean isDemoMode() {
        return momoProperties.isDemoMode();
    }

    public List<DemoAccountView> getDemoAccounts() {
        List<DemoAccountView> accounts = new ArrayList<>();

        for (Map.Entry<String, String> entry : DEMO_PASSWORDS.entrySet()) {
            String phone = entry.getKey();
            String password = entry.getValue();
            BigDecimal balance = demoBalances.getOrDefault(phone, BigDecimal.ZERO);

            accounts.add(new DemoAccountView(phone, password, balance));
        }

        return accounts;
    }

    @Transactional
    public PaymentOrder createDemoPayment(List<Property> cartTours, Long userId, String userName) {
        if (cartTours == null || cartTours.isEmpty()) {
            throw new IllegalArgumentException("Giỏ tour đang trống, không thể thanh toán.");
        }

        BigDecimal amountDecimal = calculateAmount(cartTours);

        if (amountDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không hợp lệ.");
        }

        String orderId = buildOrderId();
        String requestId = orderId;
        String orderInfo = "Thanh toán giỏ tour WebDuLich " + orderId;

        String cartTourIds = cartTours.stream()
                .map(Property::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setOrderId(orderId);
        paymentOrder.setRequestId(requestId);
        paymentOrder.setAmount(amountDecimal);
        paymentOrder.setStatus("PENDING");
        paymentOrder.setOrderInfo(orderInfo);
        paymentOrder.setCartTourIds(cartTourIds);
        paymentOrder.setUserId(userId);
        paymentOrder.setUserName(userName);
        paymentOrder.setResultCode(null);
        paymentOrder.setMessage("Đơn thanh toán demo MoMo đang chờ xử lý.");
        paymentOrder.setPayUrl("/payment/momo/demo?orderId=" + orderId);
        paymentOrder.setRawResponse("{\"demoMode\":true,\"status\":\"PENDING\"}");

        return paymentOrderRepository.save(paymentOrder);
    }

    @Transactional
    public PaymentOrder markDemoPaymentPaid(String orderId, String phone, String password) {
        PaymentOrder paymentOrder = paymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn thanh toán: " + orderId));

        if (!"PENDING".equalsIgnoreCase(paymentOrder.getStatus())) {
            throw new IllegalArgumentException("Đơn thanh toán này đã được xử lý, không thể thanh toán lại.");
        }

        String correctPassword = DEMO_PASSWORDS.get(phone);

        if (correctPassword == null || !correctPassword.equals(password)) {
            throw new IllegalArgumentException("Sai số điện thoại hoặc mật khẩu MoMo demo.");
        }

        BigDecimal amount = paymentOrder.getAmount() == null ? BigDecimal.ZERO : paymentOrder.getAmount();
        BigDecimal currentBalance = demoBalances.getOrDefault(phone, BigDecimal.ZERO);

        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Số dư không đủ. Số dư hiện tại: "
                            + formatMoney(currentBalance)
                            + ", cần thanh toán: "
                            + formatMoney(amount)
            );
        }

        BigDecimal newBalance = currentBalance.subtract(amount);
        demoBalances.put(phone, newBalance);

        paymentOrder.setStatus("PAID");
        paymentOrder.setResultCode(0);
        paymentOrder.setMessage("Thanh toán demo MoMo thành công. Tài khoản "
                + phone
                + " đã trừ "
                + formatMoney(amount)
                + ". Số dư còn lại: "
                + formatMoney(newBalance)
                + ".");
        paymentOrder.setTransId(System.currentTimeMillis());
        paymentOrder.setRawResponse("{\"demoMode\":true,"
                + "\"resultCode\":0,"
                + "\"phone\":\"" + phone + "\","
                + "\"amount\":\"" + amount + "\","
                + "\"remainingBalance\":\"" + newBalance + "\"}");

        return paymentOrderRepository.save(paymentOrder);
    }

    @Transactional
    public PaymentOrder markDemoPaymentCanceled(String orderId) {
        PaymentOrder paymentOrder = paymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn thanh toán: " + orderId));

        if ("PAID".equalsIgnoreCase(paymentOrder.getStatus())) {
            throw new IllegalArgumentException("Đơn đã thanh toán thành công, không thể hủy.");
        }

        paymentOrder.setStatus("CANCELED");
        paymentOrder.setResultCode(1000);
        paymentOrder.setMessage("Người dùng đã hủy thanh toán demo MoMo.");
        paymentOrder.setRawResponse("{\"demoMode\":true,\"resultCode\":1000,\"message\":\"Người dùng đã hủy thanh toán\"}");

        return paymentOrderRepository.save(paymentOrder);
    }

    @Transactional
    public PaymentOrder createMomoPayment(List<Property> cartTours, Long userId, String userName) {
        if (cartTours == null || cartTours.isEmpty()) {
            throw new IllegalArgumentException("Giỏ tour đang trống, không thể thanh toán.");
        }

        if (!momoProperties.isConfigured()) {
            throw new IllegalStateException("Chưa cấu hình MoMo. Vui lòng điền partnerCode, accessKey, secretKey, redirectUrl và ipnUrl trong application.properties.");
        }

        BigDecimal amountDecimal = calculateAmount(cartTours);
        long amount = amountDecimal.longValueExact();

        if (amount < 1000) {
            throw new IllegalArgumentException("Số tiền thanh toán MoMo tối thiểu là 1.000 VND.");
        }

        String orderId = buildOrderId();
        String requestId = orderId;
        String orderInfo = "Thanh toán giỏ tour WebDuLich " + orderId;

        String cartTourIds = cartTours.stream()
                .map(Property::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String extraData = buildExtraData(cartTourIds, cartTours.size());

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoProperties.getIpnUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + momoProperties.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + momoProperties.getRequestType();

        String signature = hmacSha256(rawSignature, momoProperties.getSecretKey());

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("partnerCode", momoProperties.getPartnerCode());
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", momoProperties.getRedirectUrl());
        requestBody.put("ipnUrl", momoProperties.getIpnUrl());
        requestBody.put("requestType", momoProperties.getRequestType());
        requestBody.put("extraData", extraData);
        requestBody.put("lang", momoProperties.getLang());
        requestBody.put("signature", signature);

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setOrderId(orderId);
        paymentOrder.setRequestId(requestId);
        paymentOrder.setAmount(amountDecimal);
        paymentOrder.setStatus("CREATED");
        paymentOrder.setOrderInfo(orderInfo);
        paymentOrder.setCartTourIds(cartTourIds);
        paymentOrder.setUserId(userId);
        paymentOrder.setUserName(userName);
        paymentOrderRepository.save(paymentOrder);

        try {
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(momoProperties.getEndpoint()))
                    .timeout(Duration.ofSeconds(35))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            MomoCreatePaymentResponse momoResponse = objectMapper.readValue(response.body(), MomoCreatePaymentResponse.class);

            paymentOrder.setRawResponse(response.body());
            paymentOrder.setResultCode(momoResponse.getResultCode());
            paymentOrder.setMessage(momoResponse.getMessage());
            paymentOrder.setPayUrl(momoResponse.getPayUrl());

            if (response.statusCode() >= 200
                    && response.statusCode() < 300
                    && momoResponse.getResultCode() != null
                    && momoResponse.getResultCode() == 0
                    && hasText(momoResponse.getPayUrl())) {
                paymentOrder.setStatus("PENDING");
                return paymentOrderRepository.save(paymentOrder);
            }

            paymentOrder.setStatus("CREATE_FAILED");
            paymentOrderRepository.save(paymentOrder);
            throw new IllegalStateException("MoMo không tạo được link thanh toán: " + safeMessage(momoResponse.getMessage()));
        } catch (Exception ex) {
            paymentOrder.setStatus("CREATE_FAILED");
            paymentOrder.setMessage(ex.getMessage());
            paymentOrderRepository.save(paymentOrder);
            throw new IllegalStateException("Không gọi được API MoMo: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public PaymentOrder updatePaymentResult(Map<String, String> params, boolean fromIpn) {
        String orderId = params.get("orderId");

        PaymentOrder paymentOrder = paymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn thanh toán: " + orderId));

        Integer resultCode = parseInteger(params.get("resultCode"));
        Long transId = parseLong(params.get("transId"));
        String message = params.get("message");

        paymentOrder.setResultCode(resultCode);
        paymentOrder.setTransId(transId);
        paymentOrder.setMessage(message);
        paymentOrder.setRawResponse(toJsonQuietly(params));

        boolean signatureValid = verifyResultSignature(params);

        if (!signatureValid) {
            paymentOrder.setStatus("INVALID_SIGNATURE");
            return paymentOrderRepository.save(paymentOrder);
        }

        if (resultCode != null && resultCode == 0) {
            paymentOrder.setStatus("PAID");
        } else {
            paymentOrder.setStatus(fromIpn ? "IPN_FAILED" : "PAYMENT_FAILED");
        }

        return paymentOrderRepository.save(paymentOrder);
    }

    public boolean verifyResultSignature(Map<String, String> params) {
        String signature = params.get("signature");

        if (!hasText(signature)) {
            return false;
        }

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + value(params, "amount")
                + "&extraData=" + value(params, "extraData")
                + "&message=" + value(params, "message")
                + "&orderId=" + value(params, "orderId")
                + "&orderInfo=" + value(params, "orderInfo")
                + "&orderType=" + value(params, "orderType")
                + "&partnerCode=" + value(params, "partnerCode")
                + "&payType=" + value(params, "payType")
                + "&requestId=" + value(params, "requestId")
                + "&responseTime=" + value(params, "responseTime")
                + "&resultCode=" + value(params, "resultCode")
                + "&transId=" + value(params, "transId");

        String expectedSignature = hmacSha256(rawSignature, momoProperties.getSecretKey());
        return expectedSignature.equalsIgnoreCase(signature);
    }

    public Optional<PaymentOrder> findByOrderId(String orderId) {
        return paymentOrderRepository.findByOrderId(orderId);
    }

    private BigDecimal calculateAmount(List<Property> cartTours) {
        return cartTours.stream()
                .map(Property::getPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private String buildOrderId() {
        String random = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase(Locale.ROOT);

        return "WD" + System.currentTimeMillis() + random;
    }

    private String buildExtraData(String cartTourIds, int cartCount) {
        Map<String, String> extra = new LinkedHashMap<>();
        extra.put("cartTourIds", cartTourIds);
        extra.put("cartCount", String.valueOf(cartCount));

        try {
            String json = objectMapper.writeValueAsString(extra);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException ex) {
            return "";
        }
    }

    private String hmacSha256(String data, String secretKey) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);
            byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Không tạo được chữ ký MoMo.", ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String safeMessage(String message) {
        return hasText(message) ? message : "Không rõ lỗi từ MoMo.";
    }

    private Integer parseInteger(String value) {
        try {
            return hasText(value) ? Integer.parseInt(value) : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long parseLong(String value) {
        try {
            return hasText(value) ? Long.parseLong(value) : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String value(Map<String, String> params, String key) {
        return params.getOrDefault(key, "");
    }

    private String toJsonQuietly(Map<String, String> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException ex) {
            return params.toString();
        }
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0đ";
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(amount) + "đ";
    }

    public static class DemoAccountView {
        private final String phone;
        private final String password;
        private final BigDecimal balance;

        public DemoAccountView(String phone, String password, BigDecimal balance) {
            this.phone = phone;
            this.password = password;
            this.balance = balance;
        }

        public String getPhone() {
            return phone;
        }

        public String getPassword() {
            return password;
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }
}