package enums;

public enum ErrorMessages {
    NOT_FOUND(404),
    REQUESTED_CURRENCY_IS_NOT_VALID(5),
    COINDESK_API_SERVICE_FAILED(6),
    USER_INPUT_FOR_CURRENCY_IS_NULL(7),
    COINDESK_API_JSON_IS_NOT_VALID_AS_MENTION_IN_DOCUMENTATION(8),
    RESPONSE_BODY_OF_API_IS_EMPTY(9);


    private int errorCode;

    ErrorMessages(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
