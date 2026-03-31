package com.multigenesystask.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Order;
import com.multigenesystask.entity.PaymentDetails;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.repository.OrderRepository;
import com.multigenesystask.response.ApiResponse;
import com.multigenesystask.response.PaymentLinkResponse;
import com.multigenesystask.service.OrderService;
import com.multigenesystask.service.PaymentGatewayService;
import com.multigenesystask.user.domain.OrderStatus;
import com.multigenesystask.user.domain.PaymentStatus;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/api")
public class PaymentController {

	private final PaymentGatewayService paymentGatewayService;
	private final OrderService orderService;
	private final OrderRepository orderRepository;
	
	private static final String PAYMENT_STATUS_PAID = "paid";

	// Explicit constructor injection — no Lombok DI conflict
	public PaymentController(PaymentGatewayService paymentGatewayService, OrderService orderService,
			OrderRepository orderRepository) {
		this.paymentGatewayService = paymentGatewayService;
		this.orderService = orderService;
		this.orderRepository = orderRepository;
	}

	/**
	 * Creates a Razorpay payment link for the given order. Stores the Razorpay
	 * order_id back on the order for later correlation.
	 */
	@PostMapping("/payments/{orderId}")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId)
			throws RazorpayException, OrderException {

		Order order = orderService.findOrderById(orderId);

		PaymentLink payment = paymentGatewayService.createPaymentLink(order);

		String paymentLinkId = payment.get("id");
		String paymentLinkUrl = payment.get("short_url");

		// Fetch back the Razorpay order_id and persist it on our order
		order.setOrderId(paymentLinkId);
		orderRepository.save(order);

		PaymentLinkResponse res = new PaymentLinkResponse(paymentLinkUrl, paymentLinkId);
		return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
	}

	/**
	 * Razorpay callback endpoint.
	 *
	 * Razorpay redirects here after payment with these signed query params:
	 * razorpay_payment_id, razorpay_payment_link_id,
	 * razorpay_payment_link_reference_id, razorpay_payment_link_status,
	 * razorpay_signature
	 *
	 * The signature is verified with HMAC-SHA256 using our API secret BEFORE
	 * touching the database. This prevents anyone from faking a payment by calling
	 * this URL directly with a fake payment_id.
	 */
	@GetMapping("/payments")
    @PreAuthorize("isAuthenticated() or hasIpAddress('0.0.0.0/0')")
	public ResponseEntity<ApiResponse> redirect(@RequestParam("razorpay_payment_id") String paymentId,
			@RequestParam("razorpay_payment_link_id") String paymentLinkId,
			@RequestParam("razorpay_payment_link_reference_id") String referenceId,
			@RequestParam("razorpay_payment_link_status") String paymentLinkStatus,
			@RequestParam("razorpay_signature") String razorpaySignature) throws RazorpayException, OrderException {

		// Step 1: Verify signature FIRST — reject immediately if invalid
		boolean isValid = paymentGatewayService.verifyPaymentSignature(paymentLinkId, referenceId, paymentLinkStatus,
				paymentId, razorpaySignature);

		if (!isValid) {
			return new ResponseEntity<>(new ApiResponse("Invalid payment signature. Request rejected.", false),
					HttpStatus.BAD_REQUEST);
		}

		// Step 2: Only trust and process after signature is confirmed
		Order order = orderRepository.findByOrderId(paymentLinkId)
				.orElseThrow(() -> new OrderException("Order not found for payment link: " + paymentLinkId));

		if (PAYMENT_STATUS_PAID.equals(paymentLinkStatus) && paymentId != null && !paymentId.isEmpty()) {

			if (order.getPaymentDetails() == null) {
				order.setPaymentDetails(new PaymentDetails());
			}

			PaymentDetails pd = order.getPaymentDetails();

			if (pd.getStatus() != PaymentStatus.COMPLETED) {

				pd.setPaymentId(paymentId);
				pd.setStatus(PaymentStatus.COMPLETED);
				pd.setRazorpayPaymentLinkId(paymentLinkId);
				pd.setRazorpayPaymentLinkReferenceId(referenceId);
				pd.setRazorpayPaymentLinkStatus(paymentLinkStatus);
				pd.setRazorpayPaymentId(paymentId);
				pd.setPaymentMethod("RAZORPAY");

				order.setOrderStatus(OrderStatus.PLACED);

				orderRepository.save(order);
			}
		}

		ApiResponse res = new ApiResponse("Your order has been placed successfully", true);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
}
