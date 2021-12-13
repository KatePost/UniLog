package com.AK.unilog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseUpdateFormModel {

    @NotNull
    private String courseNumber;

    @Size(max = 40, message = "The title must be under 40 characters")
    private String title;

    @Size(max = 500, message = "The description must be less than 500 characters")
    private String description;

    @Min(value = 50, message = "Enter a price equal to or greater than $50.00")
    private Double price;

}
