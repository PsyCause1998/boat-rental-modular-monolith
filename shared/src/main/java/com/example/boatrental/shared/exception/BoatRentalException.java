package com.example.boatrental.shared.exception;

public abstract class BoatRentalException extends RuntimeException {

    private final ErrorCode code;

    protected BoatRentalException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }

    public ErrorType type() {
        return code.type();
    }

    public enum ErrorType {
        NOT_FOUND,
        CONFLICT,
        VALIDATION,
        INTERNAL
    }

    public enum ErrorCode {
        CUSTOMER_NOT_FOUND(ErrorType.NOT_FOUND),
        RENTAL_NOT_FOUND(ErrorType.NOT_FOUND),
        BOAT_NOT_AVAILABLE(ErrorType.CONFLICT),
        ACTIVE_RENTAL_ALREADY_EXISTS(ErrorType.CONFLICT),
        VALIDATION_ERROR(ErrorType.VALIDATION),
        INVALID_ARGUMENT(ErrorType.VALIDATION),
        INTERNAL_ERROR(ErrorType.INTERNAL);

        private final ErrorType type;

        ErrorCode(ErrorType type) {
            this.type = type;
        }

        public ErrorType type() {
            return type;
        }
    }
}

