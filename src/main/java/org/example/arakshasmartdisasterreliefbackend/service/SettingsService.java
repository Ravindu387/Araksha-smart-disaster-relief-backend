package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.entity.Settings;
import org.example.arakshasmartdisasterreliefbackend.repository.SettingsRepository;
import org.example.arakshasmartdisasterreliefbackend.dto.request.SettingsDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsService {

    private final SettingsRepository repository;

    public SettingsService(SettingsRepository repository) {
        this.repository = repository;
    }

    public SettingsDTO getSettings() {

        Optional<Settings> settingsOpt = repository.findAll().stream().findFirst();

        if (settingsOpt.isPresent()) {
            Settings s = settingsOpt.get();
            return mapToDTO(s);
        }

        // If no settings exist → create default
        Settings defaultSettings = new Settings();
        repository.save(defaultSettings);
        return mapToDTO(defaultSettings);
    }

    public SettingsDTO updateSettings(SettingsDTO dto) {

        Settings settings = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        settings.setFirstName(dto.getFirstName());
        settings.setLastName(dto.getLastName());
        settings.setEmail(dto.getEmail());
        settings.setPhone(dto.getPhone());
        settings.setJobTitle(dto.getJobTitle());
        settings.setLocation(dto.getLocation());
        settings.setBio(dto.getBio());
        settings.setTimezone(dto.getTimezone());
        settings.setLanguage(dto.getLanguage());

        settings.setTwoFactorEnabled(dto.getTwoFactorEnabled());
        settings.setSessionTimeout(dto.getSessionTimeout());
        settings.setPasswordExpiry(dto.getPasswordExpiry());

        settings.setAutoAssignVolunteers(dto.getAutoAssignVolunteers());
        settings.setAiRecommendations(dto.getAiRecommendations());
        settings.setGpsTracking(dto.getGpsTracking());
        settings.setPublicAlerts(dto.getPublicAlerts());

        repository.save(settings);

        return mapToDTO(settings);
    }

    private SettingsDTO mapToDTO(Settings s) {
        return SettingsDTO.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .phone(s.getPhone())
                .jobTitle(s.getJobTitle())
                .location(s.getLocation())
                .bio(s.getBio())
                .timezone(s.getTimezone())
                .language(s.getLanguage())
                .twoFactorEnabled(s.getTwoFactorEnabled())
                .sessionTimeout(s.getSessionTimeout())
                .passwordExpiry(s.getPasswordExpiry())
                .autoAssignVolunteers(s.getAutoAssignVolunteers())
                .aiRecommendations(s.getAiRecommendations())
                .gpsTracking(s.getGpsTracking())
                .publicAlerts(s.getPublicAlerts())
                .build();
    }
}