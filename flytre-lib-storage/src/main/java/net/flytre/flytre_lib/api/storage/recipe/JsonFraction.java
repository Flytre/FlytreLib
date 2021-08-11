package net.flytre.flytre_lib.api.storage.recipe;

import com.google.gson.JsonObject;

import java.util.Set;


/**
 * A fraction that can be created from Json.
 * Fractions are standardized so that they are always simplified and with a positive denominator
 */
public class JsonFraction {

    private static final Set<String> NUMERATOR_ALIASES = Set.of("n", "num", "numerator");
    private static final Set<String> DENOMINATOR_ALIASES = Set.of("d", "den", "denominator");

    private final int numerator;
    private final int denominator;

    public JsonFraction(int n, int d) {

        /*
        Proper standardization of fractions:
        -Denominator is always positive
        -Fractions are always fully simplified
         */

        int gcd = gcd(n, d);
        if (d < 0) {
            n *= -1;
            d *= -1;
        }
        if (d == 0)
            throw new AssertionError("Fraction denominator cannot be 0!");
        numerator = n / gcd;
        denominator = d / gcd;
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int t = a;
            a = b;
            b = t % b;
        }
        return a;
    }

    public static JsonFraction fromJson(JsonObject object) {

        Integer n = null;
        for (String key : NUMERATOR_ALIASES)
            if (object.has(key))
                n = object.get(key).getAsInt();

        if (n == null)
            throw new IllegalArgumentException("No numerator found in: " + object);

        int d = 1;
        for (String key : DENOMINATOR_ALIASES)
            if (object.has(key))
                d = object.get(key).getAsInt();
        return new JsonFraction(n, d);
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        JsonFraction fraction = (JsonFraction) o;

        return numerator == fraction.numerator && denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        int result = numerator;
        result = 31 * result + denominator;
        return result;
    }

    @Override
    public String toString() {
        return numerator + " / " + denominator;
    }
}
