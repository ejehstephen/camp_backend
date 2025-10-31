//re_QikVV5qS_9fjvKbq2qSjRnDZxnTdJs6o8

package com.campnest.campnest_backend.config;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private static final String API_KEY = System.getenv("RESEND_API_KEY");

    public void sendOtp(String to, String otp) {
        try {
            String json = """
                {
                    "from": "CampNest <onboarding@resend.dev>",
                    "to": ["%s"],
                    "subject": "Your CampNest Verification Code",
                    "html": "<h3>Your OTP Code is: %s</h3><p>Expires in 2 minutes.</p>"
                }
                """.formatted(to, otp);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("✅ Resend Response: " + response.body());

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
