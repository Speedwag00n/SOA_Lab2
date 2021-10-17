package ilia.nemankov.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CoordinatesDTO {

    public CoordinatesDTO(Long id) {
        this.id = id;
    }

    private Long id;

    private Double x;

    private Long y;
}
