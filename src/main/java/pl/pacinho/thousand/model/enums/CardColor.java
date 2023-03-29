package pl.pacinho.thousand.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CardColor {

    BLACK("#000000"),
    RED("#FF0000");

    @Getter
    private final String hex;

}
