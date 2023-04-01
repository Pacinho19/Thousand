package pl.pacinho.thousand.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberUtilsTest {

    @Test
    void checkRoundingUp(){
        Assertions.assertEquals(10, NumberUtils.roundToNearest10(9));
    }

    @Test
    void checkRoundingDown(){
        Assertions.assertEquals(10, NumberUtils.roundToNearest10(11));
    }

    @Test
    void checkRoundingDownBy5(){
        Assertions.assertEquals(20, NumberUtils.roundToNearest10(25));
    }
}