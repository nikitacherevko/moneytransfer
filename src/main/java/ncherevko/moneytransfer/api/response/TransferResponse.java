package ncherevko.moneytransfer.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferResponse {
    @JsonProperty
    private boolean success;
    @JsonProperty
    private String description;

    @JsonCreator
    public TransferResponse(@JsonProperty("success") boolean success, @JsonProperty("description") String description) {
        this.success = success;
        this.description = description;
    }

    private TransferResponse(boolean success) {
        this.success = success;
    }

    public static TransferResponse success() {
        return new TransferResponse(true);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDescription() {
        return description;
    }
}
