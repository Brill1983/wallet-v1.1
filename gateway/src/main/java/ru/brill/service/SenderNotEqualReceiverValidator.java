package ru.brill.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.brill.transaction.dto.TransactionDto;

public class SenderNotEqualReceiverValidator implements ConstraintValidator<SenderNotEqualReceiver, TransactionDto> {

    @Override
    public boolean isValid(TransactionDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getSenderWalletId() == null || dto.getReceiverWalletId() == null) {
            return true;
        }
        if (dto.getSenderWalletId().equals(dto.getReceiverWalletId())) {
            return false;
        }
        return true;
    }
}
