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
    private Integer duplicatedHeaders = 0;
    private Integer duplicatedDetails = 0;
    private Integer replacedHeaders = 0;
    private Integer replacedDetails = 0;
    private Boolean hashExist;
    private Boolean isItWriteable;

    public void incrementDuplicatedHeaders() {
        duplicatedHeaders++;
    }

    public void incrementDuplicatedDetails() {
        duplicatedDetails++;
    }

    public void incrementReplacedHeaders() {
        replacedHeaders++;
    }

    public void incrementReplacedDetails(Integer quantity) {
        replacedDetails = replacedDetails + quantity;
    }
}
