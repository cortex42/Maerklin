package de.cortex42.maerklin.framework.packetlistener;

/**
 * Created by ivo on 16.12.15.
 */
public class ConfigDataMissingRequestResponseException extends ConfigDataException {
    public ConfigDataMissingRequestResponseException() {
    }

    public ConfigDataMissingRequestResponseException(String message) {
        super(message);
    }

    public ConfigDataMissingRequestResponseException(Throwable cause) {
        super(cause);
    }

    public ConfigDataMissingRequestResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigDataMissingRequestResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
