package com.AK.unilog.model;

import com.AK.unilog.service.Semester;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionUpdateFormModel {

    private Long id;

    private Semester semester;

    @Nullable
    @Min(value = 10, message = "At least 10 seats must be available")
    @Max(value = 50, message = "No more than 50 seats may be available")
    private int seatsAvailable; //FIXME don't allow to be changed if there are students registered

    @Min(2021)
    @Max(2100)
    private int year;

    private boolean disabled = false;

}
