package org.example.arakshasmartdisasterreliefbackend.config;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.entity.*;
import org.example.arakshasmartdisasterreliefbackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmergencyRequestRepository requestRepository;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final InventoryRepository inventoryRepository;
    private final AllocationRepository allocationRepository;
    private final NeedRepository needRepository;
    private final EmergencyNeedRepository emergencyNeedRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Shelters
        if (shelterRepository.count() == 0) {
            shelterRepository.save(Shelter.builder()
                    .name("Houston Community Center")
                    .address("Houston, TX")
                    .capacity(100)
                    .occupied(32)
                    .status("Available")
                    .amenities("Wifi, Power, Water")
                    .latitude(BigDecimal.valueOf(29.76))
                    .longitude(BigDecimal.valueOf(-95.36))
                    .lastUpdated(LocalDateTime.now())
                    .build());
            shelterRepository.save(Shelter.builder()
                    .name("Denver Red Cross Hub")
                    .address("Denver, CO")
                    .capacity(300)
                    .occupied(80)
                    .status("Available")
                    .amenities("Wifi, Power, Water")
                    .latitude(BigDecimal.valueOf(39.73))
                    .longitude(BigDecimal.valueOf(-104.99))
                    .lastUpdated(LocalDateTime.now())
                    .build());
            shelterRepository.save(Shelter.builder()
                    .name("Chicago Metro Shelter")
                    .address("Chicago, IL")
                    .capacity(250)
                    .occupied(39)
                    .status("Available")
                    .amenities("Wifi, Power, Water")
                    .latitude(BigDecimal.valueOf(41.87))
                    .longitude(BigDecimal.valueOf(-87.62))
                    .lastUpdated(LocalDateTime.now())
                    .build());
        }

        // 2. Seed Inventory
        if (inventoryRepository.count() == 0) {
            inventoryRepository.save(new Inventory(null, "Food Kits", "Food", 8420, 10000, "kits", 1580, 1000));
            inventoryRepository.save(new Inventory(null, "Water (Liters)", "Water", 15600, 20000, "L", 4400, 2000));
            inventoryRepository.save(new Inventory(null, "Medical Kits", "Medical", 1240, 2000, "kits", 760, 500));
        }

        // 3. Seed Volunteers
        if (volunteerRepository.count() == 0) {
            volunteerRepository.save(Volunteer.builder().name("Lisa Chen").location("Houston, TX").skills(new ArrayList<>(Arrays.asList("Medical", "Logistics"))).status("Available").rating(4.9).tasks(15).phone("555-0101").build());
            volunteerRepository.save(Volunteer.builder().name("Michael Davis").location("San Jose, CA").skills(new ArrayList<>(Arrays.asList("Search & Rescue"))).status("Available").rating(4.7).tasks(8).phone("555-0102").build());
            volunteerRepository.save(Volunteer.builder().name("Sarah Connor").location("Miami, FL").skills(new ArrayList<>(Arrays.asList("Medical", "First Aid"))).status("Available").rating(4.8).tasks(22).phone("555-0103").build());
            volunteerRepository.save(Volunteer.builder().name("Diana Foster").location("Phoenix, AZ").skills(new ArrayList<>(Arrays.asList("Logistics"))).status("Available").rating(4.6).tasks(12).phone("555-0104").build());
            volunteerRepository.save(Volunteer.builder().name("James Wright").location("Houston, TX").skills(new ArrayList<>(Arrays.asList("Water Rescue", "First Aid"))).status("On Duty").rating(4.9).tasks(19).phone("555-0105").build());
        }

        // 4. Seed EmergencyRequests
        if (requestRepository.count() == 0) {
            requestRepository.save(EmergencyRequest.builder().requestId("ER-2847").citizenName("Alice Smith").emergencyType("Flood").priority("Critical").status("Pending").location("Houston, TX").requestTime(LocalDateTime.now()).build());
            requestRepository.save(EmergencyRequest.builder().requestId("ER-2844").citizenName("Bob Jones").emergencyType("Earthquake").priority("Critical").status("Pending").location("San Jose, CA").requestTime(LocalDateTime.now()).build());
            requestRepository.save(EmergencyRequest.builder().requestId("ER-2843").citizenName("Charlie Brown").emergencyType("Hurricane").priority("High").status("Assigned").location("Miami, FL").assignedVolunteer("James Wright").requestTime(LocalDateTime.now()).build());
            requestRepository.save(EmergencyRequest.builder().requestId("ER-2841").citizenName("David Miller").emergencyType("Flood").priority("Medium").status("Pending").location("Phoenix, AZ").requestTime(LocalDateTime.now()).build());
        }

        // 5. Seed Allocations
        if (allocationRepository.count() == 0) {
            allocationRepository.save(new Allocation(null, "James Wright assigned to ER-2843 (Hurricane, Miami)", "14:38", "volunteer"));
            allocationRepository.save(new Allocation(null, "Food Kits (200 units) dispatched to ER-2843 (Miami)", "14:22", "resource"));
            allocationRepository.save(new Allocation(null, "Houston Community Center linked to response for ER-2847", "14:05", "shelter"));
            allocationRepository.save(new Allocation(null, "Medical team (3 volunteers) allocated to ER-2844", "13:48", "volunteer"));
            allocationRepository.save(new Allocation(null, "Water supply (5,000L) dispatched from Phoenix depot", "13:12", "resource"));
        }

        // 6. Seed Needs and Emergency Needs
        if (needRepository.count() == 0) {
            Need waterRescue = needRepository.save(new Need(null, "Water Rescue", "Water rescue equipment and personnel"));
            Need foodKits = needRepository.save(new Need(null, "Food Kits", "Emergency food supplies"));
            Need blankets = needRepository.save(new Need(null, "Blankets", "Warm blankets"));
            Need medical = needRepository.save(new Need(null, "Medical", "Medical kits and personnel"));
            Need searchRescue = needRepository.save(new Need(null, "Search & Rescue", "Search and rescue operations"));
            Need evacuation = needRepository.save(new Need(null, "Evacuation", "Evacuation logistics"));
            Need tents = needRepository.save(new Need(null, "Tents", "Temporary shelter tents"));
            Need water = needRepository.save(new Need(null, "Water", "Drinking water bottles/liters"));

            EmergencyRequest req2847 = requestRepository.findByRequestId("ER-2847").orElse(null);
            EmergencyRequest req2844 = requestRepository.findByRequestId("ER-2844").orElse(null);
            EmergencyRequest req2843 = requestRepository.findByRequestId("ER-2843").orElse(null);
            EmergencyRequest req2841 = requestRepository.findByRequestId("ER-2841").orElse(null);

            if (req2847 != null) {
                emergencyNeedRepository.save(new EmergencyNeed(null, req2847, waterRescue, 1));
                emergencyNeedRepository.save(new EmergencyNeed(null, req2847, foodKits, 1));
                emergencyNeedRepository.save(new EmergencyNeed(null, req2847, blankets, 1));
            }
            if (req2844 != null) {
                emergencyNeedRepository.save(new EmergencyNeed(null, req2844, medical, 1));
                emergencyNeedRepository.save(new EmergencyNeed(null, req2844, searchRescue, 1));
            }
            if (req2843 != null) {
                emergencyNeedRepository.save(new EmergencyNeed(null, req2843, evacuation, 1));
                emergencyNeedRepository.save(new EmergencyNeed(null, req2843, tents, 1));
                emergencyNeedRepository.save(new EmergencyNeed(null, req2843, foodKits, 1));
            }
            if (req2841 != null) {
                emergencyNeedRepository.save(new EmergencyNeed(null, req2841, evacuation, 1));
                emergencyNeedRepository.save(new EmergencyNeed(null, req2841, water, 1));
            }
        }

        // 7. Seed Notifications — connected to real data from other pages
        if (notificationRepository.count() == 0) {
            // Alerts — from Emergency Requests
            Notification n1 = new Notification();
            n1.setCategory("alerts");
            n1.setSeverity("critical");
            n1.setTitle("Category 4 Hurricane Alert");
            n1.setBadge("Critical");
            n1.setDescription("Hurricane Helena upgraded to Category 4. Expected landfall Florida Keys in 6 hours. Activate evacuation protocols immediately.");
            n1.setTime("2 min ago");
            n1.setRead(false);
            notificationRepository.save(n1);

            Notification n2 = new Notification();
            n2.setCategory("alerts");
            n2.setSeverity("high");
            n2.setTitle("Flash Flood Warning — Houston");
            n2.setBadge("High");
            n2.setDescription("National Weather Service issued flash flood warning for Harris County. 12 active requests in affected zone. Related: ER-2847 (Flood, Houston, TX).");
            n2.setTime("18 min ago");
            n2.setRead(false);
            notificationRepository.save(n2);

            // Assignments — from Volunteers + Emergency Requests
            Notification n3 = new Notification();
            n3.setCategory("assignments");
            n3.setSeverity("info");
            n3.setTitle("Volunteer Assignment Confirmed");
            n3.setBadge("Info");
            n3.setDescription("James Wright (V-1024) assigned to ER-2847 (Flood, Houston TX). ETA: 8 minutes.");
            n3.setTime("38 min ago");
            n3.setRead(false);
            notificationRepository.save(n3);

            Notification n4 = new Notification();
            n4.setCategory("assignments");
            n4.setSeverity("success");
            n4.setTitle("Task Completed");
            n4.setBadge("Success");
            n4.setDescription("Anna Rodriguez marked ER-2843 complete. 3 citizens evacuated to SH-104 Miami-Dade Center.");
            n4.setTime("1h ago");
            n4.setRead(false);
            notificationRepository.save(n4);

            // Inventory — from Inventory levels
            Notification n5 = new Notification();
            n5.setCategory("inventory");
            n5.setSeverity("critical");
            n5.setTitle("Critical Stock Alert: Medical Kits");
            n5.setBadge("Critical");
            n5.setDescription("Medical Kits stock at 1,240 units — below the 1,500 threshold. Immediate restocking required.");
            n5.setTime("45 min ago");
            n5.setRead(false);
            notificationRepository.save(n5);

            Notification n6 = new Notification();
            n6.setCategory("inventory");
            n6.setSeverity("high");
            n6.setTitle("Food Kits Dispatched");
            n6.setBadge("High");
            n6.setDescription("200 Food Kits dispatched to ER-2843 (Hurricane, Miami). Remaining stock: 8,420 kits.");
            n6.setTime("1h ago");
            n6.setRead(false);
            notificationRepository.save(n6);

            // Shelters — from Shelter capacity
            Notification n7 = new Notification();
            n7.setCategory("shelters");
            n7.setSeverity("high");
            n7.setTitle("Shelter Near Capacity: Houston Community Center");
            n7.setBadge("High");
            n7.setDescription("Houston Community Center at 68% capacity (32 of 100 beds free). Linked to ER-2847 flood response.");
            n7.setTime("25 min ago");
            n7.setRead(false);
            notificationRepository.save(n7);
        }
    }
}