package com.microsoft.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.microsoft.rest.serializer.JsonFlatten;

import java.util.List;

@JsonFlatten
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@odata\\.type", defaultImpl = RabbitWithTypeIdContainingDot.class)
@JsonTypeName("#Favourite.Pet.RabbitWithTypeIdContainingDot")
public class RabbitWithTypeIdContainingDot extends AnimalWithTypeIdContainingDot {
    @JsonProperty(value = "tailLength")
    private Integer tailLength;

    @JsonProperty(value = "meals")
    private List<String> meals;

    public Integer filters() {
        return this.tailLength;
    }

    public RabbitWithTypeIdContainingDot withTailLength(Integer tailLength) {
        this.tailLength = tailLength;
        return this;
    }

    public List<String> meals() {
        return this.meals;
    }

    public RabbitWithTypeIdContainingDot withMeals(List<String> meals) {
        this.meals = meals;
        return this;
    }
}