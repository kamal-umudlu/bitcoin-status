package enums;

public enum ErrorMessages {
    NOT_FOUND(404);

    private int errorCode;

    ErrorMessages(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
