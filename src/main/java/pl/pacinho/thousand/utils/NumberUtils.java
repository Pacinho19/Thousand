package pl.pacinho.thousand.utils;

public class NumberUtils {
    public static int roundToNearest10(int n) {
        int rem = n % 10;
        if (rem > 5) {
            return n + (10 - rem);
        }
        return n - rem;

    }
}