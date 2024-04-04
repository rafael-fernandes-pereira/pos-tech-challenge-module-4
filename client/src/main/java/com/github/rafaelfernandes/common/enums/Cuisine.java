package com.github.rafaelfernandes.common.enums;

import lombok.Getter;

@Getter
public enum Cuisine {

    AMERICAN,
    ITALIAN,
    FRENCH,
    SPANISH,
    MEXICAN,
    CHINESE,
    JAPANESE,
    INDIAN,
    THAI,
    BRAZILIAN,
    PORTUGUESE,
    MEDITERRANEAN,
    ARABIC,
    GREEK,
    KOREAN,
    VIETNAMESE,
    PERUVIAN,
    ARGENTINIAN,
    CARIBBEAN,
    AFRICAN,
    FUSION,
    VEGETARIAN_VEGAN,
    ORGANIC,
    SEAFOOD,
    STEAKHOUSE,
    FOOD_TRUCK,
    FAST_FOOD,
    CAFETERIA,
    ICE_CREAM_SHOP,
    BAKERY;

    private final String name;

    Cuisine() {
        this.name = name().toUpperCase().replace("_", " ");
    }

}
