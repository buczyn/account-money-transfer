package org.example.amt.service;

import org.example.amt.model.AccountSettings;

import java.math.BigDecimal;

/**
 * This implementation provides hardcoded settings. Normally should be persisted in account settings in DB.
 */
class HardcodedAccountSettingsService implements AccountSettingsService {

    private static final BigDecimal MAX_TRANSFER_AMOUNT = new BigDecimal(1000);

    @Override
    public AccountSettings getSettings(long id) {
        return AccountSettings.builder()
                .maxTransferAmount(MAX_TRANSFER_AMOUNT)
                .build();
    }
}
