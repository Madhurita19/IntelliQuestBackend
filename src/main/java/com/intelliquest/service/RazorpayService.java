package com.intelliquest.service;

import com.intelliquest.dto.PaymentRequestDTO;
import com.intelliquest.model.Course;
import com.intelliquest.model.Enrollment;
import com.intelliquest.model.Payment;
import com.intelliquest.model.User;
import com.intelliquest.repository.CourseRepository;
import com.intelliquest.repository.EnrollmentRepository;
import com.intelliquest.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

    private final RazorpayClient razorpayClient;

    public RazorpayService(@Value("${razorpay.key_id}") String keyId,
                           @Value("${razorpay.key_secret}") String keySecret) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    public String createOrder(Double amountInRupees) throws Exception {
        int amountInPaise = (int) (amountInRupees * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);
        return order.toString();
    }   

    public void recordPaymentAndEnrollUser(PaymentRequestDTO request) throws Exception {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new Exception("User not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new Exception("Course not found"));

        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_DHZhCgGEIBimZS", "dwSwOBCgfCcyBkjuppyEGG2Y");
        com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());
        
        String method = razorpayPayment.get("method");
        String status = razorpayPayment.get("status");

        if (!"captured".equals(status)) {
            throw new Exception("Payment is not captured.");
        }

        // Local Payment entity
        Payment payment = new Payment();
        payment.setPaymentGatewayId(request.getRazorpayPaymentId());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(status);
        payment.setAmount(course.getPrice().doubleValue());

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setPayment(payment);

        enrollmentRepository.save(enrollment);
    }

}
