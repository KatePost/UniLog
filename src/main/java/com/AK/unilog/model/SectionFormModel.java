package com.AK.unilog.model;

import com.AK.unilog.entity.Section;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionFormModel {

    @Column(length = 7, nullable = false)
    @Size(min = 7, max = 7)
    private String courseNumber;

    @NotNull
    private String semester;

    @Min(10)
    @Max(50)
    private int seatsAvailable;

    @Column(nullable = false)
    @Min(2021)
    @Max(2100)
    private int year;
}
