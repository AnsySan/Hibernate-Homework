package org.AnsySan.account;

public abstract class AbstractAccountService {

    protected void validatePositiveId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }
    }

    protected void validatePositiveAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }

    protected void validate(Integer accountId, Integer amount) {
        validatePositiveId(accountId);
        validatePositiveAmount(amount);
    }
}
