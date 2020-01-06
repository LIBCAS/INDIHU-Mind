package cz.cas.lib.vzb.exception;

import core.exception.GeneralRollbackException;

public class UserQuotaReachedException extends GeneralRollbackException {
    public UserQuotaReachedException(double userQuotaKb) {
        super(String.format("limit : %.2f MB", userQuotaKb/1000));
    }
}
