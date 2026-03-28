package com.multigenesystask.entity;

import com.multigenesystask.constants.PaymentStatus;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class PaymentDetails {

	private String paymentMethod;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private String payementId;

	private String razorpayPaymentLinkId;

	private String razorpayPaymentLinkReferenceId;

	private String razorpayPaymentLinkStatus;

	private String razorpayPaymentId;

}
