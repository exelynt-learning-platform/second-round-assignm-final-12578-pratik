package com.multigenesystask.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Order;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentGatewayService {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    private RazorpayClient getClient() throws RazorpayException {
        return new RazorpayClient(apiKey, apiSecret);
    }

    public PaymentLink createPaymentLink(Order order) throws RazorpayException {

        RazorpayClient razorpay = getClient();

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", order.getTotalPrice() * 100);
        paymentLinkRequest.put("currency", "INR");

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

        paymentLinkRequest.put(
            "callback_url",
            "http://localhost:4200/payment-success?order_id=" + order.getId()
        );
        paymentLinkRequest.put("callback_method", "get");

        return razorpay.paymentLink.create(paymentLinkRequest);
    }

    public Payment fetchPayment(String paymentId) throws RazorpayException {
        RazorpayClient razorpay = getClient();
        return razorpay.payments.fetch(paymentId);
    }

    public PaymentLink fetchPaymentLink(String paymentLinkId) throws RazorpayException {
        RazorpayClient razorpay = getClient();
        return razorpay.paymentLink.fetch(paymentLinkId);
    }
}