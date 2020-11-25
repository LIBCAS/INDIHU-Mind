package cz.cas.lib.vzb.exception;

import core.exception.RestGeneralException;
import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

public class UserQuotaReachedException extends RestGeneralException {

    public UserQuotaReachedException(RestErrorCodeEnum code, double userQuotaKb) {
        super(code, Utils.asMap("limit", String.format("%.2f MB", userQuotaKb / 1000)));
    }


    public enum ErrorCode implements RestErrorCodeEnum {
        USER_QUOTA_REACHED("Limit úložiště uživatele překročen");

        @Getter private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }
}
