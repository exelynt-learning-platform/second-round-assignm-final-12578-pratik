package com.multigenesystask.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Order;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentGatewayService {

    private final RazorpayClient razorpay;
    private final String apiSecret;

    @Value("${app.payment.callback-url}")
    private String callbackUrl;

    
    @Value("${app.payment.currency}")
    private String currency;

    @Value("${app.payment.callback-method}")
    private String callbackMethod;
    // Single shared RazorpayClient — not created per request
    public PaymentGatewayService(
            @Value("${razorpay.api.key}") String apiKey,
            @Value("${razorpay.api.secret}") String apiSecret) throws RazorpayException {
        this.razorpay = new RazorpayClient(apiKey, apiSecret);
        this.apiSecret = apiSecret; // stored for HMAC signature verification
    }

    public PaymentLink createPaymentLink(Order order) throws RazorpayException {

        // Safe paise conversion — avoids floating-point rounding errors
        long amount = BigDecimal.valueOf(order.getTotalPrice())
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", amount);
        paymentLinkRequest.put("currency", currency);
        
        paymentLinkRequest.put("reference_id", order.getId().toString());

        JSONObject customer = new JSONObject();
        customer.put("name", order.getUser().getFirstName() + " " + order.getUser().getLastName());
        customer.put("contact", order.getUser().getMobile());
        customer.put("email", order.getUser().getEmail());
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("sms", true);
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("reminder_enable", true);

        // Callback URL from config — not hardcoded
        paymentLinkRequest.put("callback_url", callbackUrl);
        paymentLinkRequest.put("callback_method", callbackMethod);

        return razorpay.paymentLink.create(paymentLinkRequest);
    }

    /**
     * Verifies the Razorpay callback signature using HMAC-SHA256.
     *
     * Razorpay signs: paymentLinkId + "|" + referenceId + "|" + paymentId
     * using your API secret. If the computed hash matches razorpaySignature,
     * the request genuinely came from Razorpay.
     *
     * @return true if signature is valid, false if tampered or fake
     */
    public boolean verifyPaymentSignature(
            String paymentLinkId,
            String referenceId,
            String paymentLinkStatus,
            String paymentId,
            String razorpaySignature) {

        try {
            String payload = paymentLinkId + "|" + referenceId + "|" + paymentLinkStatus + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
           

            return Hex.encodeHexString(hash).equals(razorpaySignature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Razorpay signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    public Payment fetchPayment(String paymentId) throws RazorpayException {
        return razorpay.payments.fetch(paymentId);
    }

    public PaymentLink fetchPaymentLink(String paymentLinkId) throws RazorpayException {
        return razorpay.paymentLink.fetch(paymentLinkId);
    }
}
