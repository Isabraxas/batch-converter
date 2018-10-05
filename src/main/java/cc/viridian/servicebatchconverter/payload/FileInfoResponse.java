package cc.viridian.servicebatchconverter.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class FileInfoResponse {
    private Integer errorsHeaders = 0;
    private Integer errorsDetails = 0;
    private Integer replacedHeaders = 0;
    private Integer replacedDetails = 0;
    private Integer insertedHeaders = 0;
    private Integer insertedDetails = 0;
    private Boolean hashExist;
    private Boolean isItWriteable;

    public void incrementDuplicatedHeaders() {
        replacedHeaders++;
    }

    public void incrementDuplicatedDetails() {
        replacedDetails++;
    }

    public void incrementErrorHeaders() {
        errorsHeaders++;
    }

    public void incrementErrorDetails() {
        errorsDetails++;
    }

    public void incrementInsertedHeaders() {
        insertedHeaders++;
    }

    public void incrementInsertedDetails(final Integer quantity) {
        insertedDetails = insertedDetails + quantity;
    }
    public void incremenDuplicatedDetails(final Integer quantity) {
        replacedDetails = replacedDetails + quantity;
    }
}
