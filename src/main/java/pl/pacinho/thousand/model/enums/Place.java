package pl.pacinho.thousand.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Place {

    _1(1, "#f1af09"),
    _2(2, "#C0C0C0"),
    _3(3, "#964B00"),
    _4(4, "#000000");

    @Getter
    private final int number;
    @Getter
    private final String color;

    public static Place findByNumber(int number) {
        return Arrays.stream(Place.values())
                .filter(p -> p.number == number)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bad place number: " + number));
    }
}
