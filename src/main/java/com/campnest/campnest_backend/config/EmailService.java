package com.campnest.campnest_backend.config;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    // Load MailerSend API Key from environment variables or application.properties
    private static final String API_KEY = System.getenv("MAILERSEND_API_KEY");

    public void sendOtp(String to, String otp) {
        try {
            String json = """
                {
                    "from": {
                        "email": "no-reply@test-3m5jgro153xgdpyo.mlsender.net",
                        "name": "CampNest"
                    },
                    "to": [
                        {
                            "email": "%s"
                        }
                    ],
                    "subject": "Your CampNest Verification Code",
                    "html": "<h2>CampNest Verification</h2><p>Your 6-digit code is <b>%s</b>.</p><p>This code will expire in 2 minutes.</p>"
                }
                """.formatted(to, otp);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mailersend.com/v1/email"))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print server response for debugging
            System.out.println("✅ MailerSend Response: " + response.body());

        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
