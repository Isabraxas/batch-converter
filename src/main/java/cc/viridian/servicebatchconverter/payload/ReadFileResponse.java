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
public class ReadFileResponse {
    private Integer duplicatedHeaders = 0;
    private Integer duplicatedDetails = 0;
    private Boolean hashExist;
    private Boolean isItWriteable;

    public void incrementDuplicatedHeaders(){
        duplicatedHeaders++;
    }

    public void incrementDuplicatedDetails(){
        duplicatedDetails++;
    }

}
