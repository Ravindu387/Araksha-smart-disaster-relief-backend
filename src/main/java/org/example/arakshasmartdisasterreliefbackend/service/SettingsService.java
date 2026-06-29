package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.SettingsDTO;

public interface SettingsService {

    SettingsDTO getSettings();

    SettingsDTO updateSettings(SettingsDTO dto);
}