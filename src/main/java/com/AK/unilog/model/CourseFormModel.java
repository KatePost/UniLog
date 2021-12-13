package com.AK.unilog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseFormModel {

    @Size(min = 7, max = 7, message = "The course number must be in format ABCD123")
    @NotNull (message = "The course number must be in format ABCD123")
    private String courseNumber;

    @NotEmpty(message = "The title must not be empty")
    @NotNull(message = "The title must not be empty")
    @Size(max = 40, message = "The title must be under 40 characters")
    private String title;

    @NotEmpty(message = "The description must not be empty")
    @NotNull(message = "The description must not be empty")
    @Size(max = 500, message = "The description must be less than 500 characters")
    private String description;

    @Min(value = 50, message = "Enter a price equal to or greater than $50.00")
    @NotNull(message = "Enter a price equal to or greater than $50.00")
    private Double price;

    private String prereq;
}
