package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.account.sid:YOUR_TWILIO_ACCOUNT_SID}")
    private String accountSid;

    @Value("${twilio.auth.token:YOUR_TWILIO_AUTH_TOKEN}")
    private String authToken;

    @Value("${twilio.phone.number:YOUR_TWILIO_PHONE_NUMBER}")
    private String fromPhoneNumber;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendSms(String to, String message) {
        log.info("Preparing to send SMS to: {}", to);

        if (isTwilioConfigMissing()) {
            log.info("[SIMULATED SMS] Send Success -> To: {}, Message: \"{}\"", to, message);
            return;
        }

        try {
            String url = String.format("https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json", accountSid);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(accountSid, authToken);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("To", to);
            map.add("From", fromPhoneNumber);
            map.add("Body", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            restTemplate.postForObject(url, request, String.class);
            log.info("SMS successfully dispatched to: {}", to);
        } catch (Exception e) {
            log.error("Twilio SMS transmission failed to: {}", to, e);
        }
    }

    @Override
    public void sendVolunteerAssignmentSms(String volunteerPhone, String volunteerName, String requestId, String location) {
        String msg = String.format("Hi %s, you have been assigned to Emergency Request %s at %s. Please view the dashboard for instructions. - Araksha Relief",
                volunteerName, requestId, location);
        sendSms(volunteerPhone, msg);
    }

    @Override
    public void sendEmergencyCreatedSms(String adminPhone, String requestId, String type, String location) {
        String msg = String.format("CRITICAL ALERT: New %s emergency reported. Request ID: %s. Location: %s. Dispatch volunteers immediately. - Araksha Ops",
                type, requestId, location);
        sendSms(adminPhone, msg);
    }

    @Override
    public void sendShelterNearlyFullSms(String managerPhone, String shelterName, int occupied, int capacity) {
        String msg = String.format("WARNING: Shelter '%s' is nearly full (%d/%d beds occupied). Consider redirecting citizen groups. - Araksha Shelters",
                shelterName, occupied, capacity);
        sendSms(managerPhone, msg);
    }

    @Override
    public void sendResourceShortageSms(String supervisorPhone, String itemName, int count, int minStock) {
        String msg = String.format("STOCK ALERT: Resource '%s' count falls to %d (Min: %d). Immediate procurement required. - Araksha Inventory",
                itemName, count, minStock);
        sendSms(supervisorPhone, msg);
    }

    private boolean isTwilioConfigMissing() {
        return accountSid == null || accountSid.trim().isEmpty() || "YOUR_TWILIO_ACCOUNT_SID".equals(accountSid)
                || authToken == null || authToken.trim().isEmpty() || "YOUR_TWILIO_AUTH_TOKEN".equals(authToken)
                || fromPhoneNumber == null || fromPhoneNumber.trim().isEmpty() || "YOUR_TWILIO_PHONE_NUMBER".equals(fromPhoneNumber);
    }
}
