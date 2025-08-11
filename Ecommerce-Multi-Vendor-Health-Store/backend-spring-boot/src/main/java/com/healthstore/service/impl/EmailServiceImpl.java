package com.healthstore.service.impl;

import com.healthstore.model.Order;
import com.healthstore.model.OrderItem;
import com.healthstore.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of the EmailService interface using JavaMailSender.
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.email.templates.path:classpath:/templates/email/}")
    private String emailTemplatesPath;
    
    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean sendOrderConfirmation(Order order) {
        String subject = String.format("Order Confirmation - #%s", order.getOrderNumber());
        
        Map<String, Object> variables = createCommonOrderVariables(order);
        variables.put("emailType", "orderConfirmation");
        variables.put("title", "Order Confirmation");
        
        String content = processEmailTemplate("order-confirmation", variables);
        
        return sendEmail(order.getUser().getEmail(), subject, content);
    }

    @Override
    public boolean sendPaymentConfirmation(Order order) {
        String subject = String.format("Payment Received - Order #%s", order.getOrderNumber());
        
        Map<String, Object> variables = createCommonOrderVariables(order);
        variables.put("emailType", "paymentConfirmation");
        variables.put("title", "Payment Confirmation");
        
        String content = processEmailTemplate("payment-confirmation", variables);
        
        return sendEmail(order.getUser().getEmail(), subject, content);
    }

    @Override
    public boolean sendShippingConfirmation(Order order) {
        String subject = String.format("Your Order #%s Has Shipped!", order.getOrderNumber());
        
        Map<String, Object> variables = createCommonOrderVariables(order);
        variables.put("emailType", "shippingConfirmation");
        variables.put("title", "Your Order Has Shipped!");
        
        String content = processEmailTemplate("shipping-confirmation", variables);
        
        return sendEmail(order.getUser().getEmail(), subject, content);
    }

    @Override
    public boolean sendRefundConfirmation(Order order, double refundAmount) {
        String subject = String.format("Refund Processed - Order #%s", order.getOrderNumber());
        
        Map<String, Object> variables = createCommonOrderVariables(order);
        variables.put("emailType", "refundConfirmation");
        variables.put("title", "Refund Processed");
        variables.put("refundAmount", formatCurrency(refundAmount));
        
        String content = processEmailTemplate("refund-confirmation", variables);
        
        return sendEmail(order.getUser().getEmail(), subject, content);
    }

    @Override
    public boolean sendPaymentFailureNotification(Order order, String errorMessage) {
        String subject = String.format("Payment Issue - Order #%s", order.getOrderNumber());
        
        Map<String, Object> variables = createCommonOrderVariables(order);
        variables.put("emailType", "paymentFailure");
        variables.put("title", "Payment Issue");
        variables.put("errorMessage", errorMessage);
        
        String content = processEmailTemplate("payment-failure", variables);
        
        return sendEmail(order.getUser().getEmail(), subject, content);
    }

    @Override
    public boolean sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML
            
            mailSender.send(message);
            log.info("Email sent to {} with subject: {}", toEmail, subject);
            return true;
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Creates a map of common variables used in order-related email templates.
     */
    private Map<String, Object> createCommonOrderVariables(Order order) {
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("order", order);
        variables.put("orderNumber", order.getOrderNumber());
        variables.put("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        variables.put("formattedTotal", formatCurrency(order.getTotalAmount().doubleValue()));
        variables.put("shippingAddress", order.getShippingAddress());
        variables.put("orderItems", order.getOrderItems());
        variables.put("baseUrl", baseUrl);
        
        // Calculate item count
        int itemCount = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        variables.put("itemCount", itemCount);
        
        return variables;
    }
    
    /**
     * Processes an email template with the given variables.
     */
    private String processEmailTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
    
    /**
     * Formats a monetary amount as a currency string.
     */
    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}
