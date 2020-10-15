package core.rest.config;

import core.exception.RestGeneralException;

/**
 * Declares fields to enrich JSON response body when {@link RestGeneralException} is thrown.
 * {@link Enum} should implement this interface.
 */
public interface RestErrorCodeEnum {

    /**
     * Provides indirect access to enum constant value
     *
     * @see Enum#name()
     */
    String name();

    /** Czech message to display on FE */
    String getMessage();

}
