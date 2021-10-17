package ilia.nemankov.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
public class MovieDTO {
    private Long id;
    private String name;
    private CoordinatesDTO coordinates;
    @JsonbDateFormat(value = JsonbDateFormat.TIME_IN_MILLIS)
    private Date creationDate;
    private Integer oscarsCount;
    private Long goldenPalmCount;
    private Double totalBoxOffice;
    private String mpaaRating;
    private PersonDTO screenWriter;
    private String genre;
}
