package com.example.vnpay.service;

import com.example.vnpay.config.Config;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final EmailService emailService;

    public int orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        var vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        var signValue = Config.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {

                var orderInfo = request.getParameter("vnp_OrderInfo");
                var paymentTime = request.getParameter("vnp_PayDate");
                var transactionId = request.getParameter("vnp_TransactionNo");
                var totalPrice = request.getParameter("vnp_Amount");

                // Extracting attributes from the model and formatting them into a string
                StringBuilder message = new StringBuilder();
                message.append("Order Info: ").append(orderInfo).append(", ");

                // Create a SimpleDateFormat object with the input format
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                // Create a SimpleDateFormat object with the desired output format
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

                try {
                    // Parse the input string to obtain a Date object
                    Date date = inputFormat.parse(paymentTime);

                    // Format the Date object to the desired output format
                    String formattedDate = outputFormat.format(date);

                    message.append("Payment Time: ").append(formattedDate).append(", ");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                message.append("Transaction ID: ").append(transactionId).append(", ");

                // Dividing the totalPrice by 100
                double totalPriceValue = Double.parseDouble(totalPrice); // Parse the String to a double
                double totalPriceDivided = totalPriceValue / 100; // Divide by 100

                // Formatting totalPriceDivided to add separators for thousands and drop trailing zeroes
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                String formattedTotalPrice = decimalFormat.format(totalPriceDivided);

                message.append("Total Price: ").append(formattedTotalPrice).append(".");

                // Send the response via email
                sendResponse(message);

                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    private void sendResponse(StringBuilder detail) {
        String subject = "VNPay payment Response";
        String body = "Data response: " + detail;
        // Sending the email
        emailService.sendEmail("receiver-email@gmail.com", subject, body);
    }
}
