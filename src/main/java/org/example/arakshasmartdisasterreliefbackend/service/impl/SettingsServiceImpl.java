package org.example.arakshasmartdisasterreliefbackend.service.impl;

import org.example.arakshasmartdisasterreliefbackend.dto.request.SettingsDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Settings;
import org.example.arakshasmartdisasterreliefbackend.repository.SettingsRepository;
import org.example.arakshasmartdisasterreliefbackend.service.SettingsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository repository;

    public SettingsServiceImpl(SettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public SettingsDTO getSettings() {

        Optional<Settings> settingsOpt = repository.findAll()
                .stream()
                .findFirst();

        if (settingsOpt.isPresent()) {
            return mapToDTO(settingsOpt.get());
        }

        // create default if not exists
        Settings defaultSettings = new Settings();
        Settings saved = repository.save(defaultSettings);

        return mapToDTO(saved);
    }

    @Override
    public SettingsDTO updateSettings(SettingsDTO dto) {

        Settings settings;
        if (dto.getId() == null) {
            settings = repository.findAll().stream().findFirst().orElseGet(Settings::new);
        } else {
            settings = repository.findById(dto.getId())
                    .orElseGet(() -> repository.findAll().stream().findFirst().orElseGet(Settings::new));
        }

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

        Settings updated = repository.save(settings);

        return mapToDTO(updated);
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