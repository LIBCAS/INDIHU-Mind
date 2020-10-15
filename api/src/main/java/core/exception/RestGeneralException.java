package core.exception;

import core.rest.config.RestErrorCodeEnum;
import core.util.Utils;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public abstract class RestGeneralException extends GeneralException {

    @NotNull
    protected RestErrorCodeEnum codedEnum;
    protected Map<String, String> details = new HashMap<>();


    public RestGeneralException(RestErrorCodeEnum codedEnum) {
        this.codedEnum = codedEnum;
    }

    public RestGeneralException(RestErrorCodeEnum codedEnum, String id) {
        this.codedEnum = codedEnum;
        this.details = Utils.asMap("id", id);
    }

    public RestGeneralException(RestErrorCodeEnum codedEnum, Map<String, String> details) {
        this.codedEnum = codedEnum;
        this.details = details;
    }


    /**
     * E.g.:
     * <pre>BadArgument: Email již existuje, args:{ "email": "example@inqool.cz", "random": "random property"}</pre>
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName()).append(":");
        result.append(codedEnum.getMessage());
        if (details != null && !details.isEmpty()) {
            result.append(", args:{ ");
            result.append(details.entrySet().stream().map(
                    (e -> e.getKey() + ": " + e.getValue())
            ).collect(Collectors.joining(", ")));
        }
        result.append(" }");
        return result.toString();
    }

}
