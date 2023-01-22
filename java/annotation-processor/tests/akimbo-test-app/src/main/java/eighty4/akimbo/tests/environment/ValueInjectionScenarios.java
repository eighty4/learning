package eighty4.akimbo.tests.environment;

import eighty4.akimbo.annotation.Value;
import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.annotation.http.RequestMapping;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@HttpService
@RequestMapping("/values")
public class ValueInjectionScenarios {

    private final Integer intValue;
    private final Short shortValue;
    private final Long longValue;
    private final Double doubleValue;
    private final Float floatValue;
    private final BigInteger bigIntegerValue;
    private final BigDecimal bigDecimalValue;
    private final Boolean booleanTrueValue;
    private final Boolean booleanOneValue;
    private final Boolean booleanYesValue;
    private final Boolean booleanFalseValue;
    private final Boolean booleanZeroValue;
    private final Boolean booleanNoValue;
    private final UUID uuidValue;

    static class ValueData<T> {
        private T property;

        public ValueData(T property) {
            this.property = property;
        }

        public T getProperty() {
            return property;
        }

        public void setProperty(T property) {
            this.property = property;
        }
    }

    private final String stringValue;

    @Inject
    public ValueInjectionScenarios(@Value("my-app.string-value") String stringValue,
                                   @Value("my-app.integer-value") Integer intValue,
                                   @Value("my-app.short-value") Short shortValue,
                                   @Value("my-app.long-value") Long longValue,
                                   @Value("my-app.double-value") Double doubleValue,
                                   @Value("my-app.float-value") Float floatValue,
                                   @Value("my-app.big-integer-value") BigInteger bigIntegerValue,
                                   @Value("my-app.big-decimal-value") BigDecimal bigDecimalValue,
                                   @Value("my-app.boolean-true-value") Boolean booleanTrueValue,
                                   @Value("my-app.boolean-one-value") Boolean booleanOneValue,
                                   @Value("my-app.boolean-yes-value") Boolean booleanYesValue,
                                   @Value("my-app.boolean-false-value") Boolean booleanFalseValue,
                                   @Value("my-app.boolean-zero-value") Boolean booleanZeroValue,
                                   @Value("my-app.boolean-no-value") Boolean booleanNoValue,
                                   @Value("my-app.uuid-value") UUID uuidValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
        this.shortValue = shortValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.floatValue = floatValue;
        this.bigIntegerValue = bigIntegerValue;
        this.bigDecimalValue = bigDecimalValue;
        this.booleanTrueValue = booleanTrueValue;
        this.booleanOneValue = booleanOneValue;
        this.booleanYesValue = booleanYesValue;
        this.booleanFalseValue = booleanFalseValue;
        this.booleanZeroValue = booleanZeroValue;
        this.booleanNoValue = booleanNoValue;
        this.uuidValue = uuidValue;
    }

    @RequestMapping("/string")
    public ValueData<String> stringValue() {
        return new ValueData<>(stringValue);
    }

    @RequestMapping("/integer")
    public ValueData<Integer> intValue() {
        return new ValueData<>(intValue);
    }

    @RequestMapping("/short")
    public ValueData<Short> shortValue() {
        return new ValueData<>(shortValue);
    }

    @RequestMapping("/long")
    public ValueData<Long> longValue() {
        return new ValueData<>(longValue);
    }

    @RequestMapping("/double")
    public ValueData<Double> doubleValue() {
        return new ValueData<>(doubleValue);
    }

    @RequestMapping("/float")
    public ValueData<Float> floatValue() {
        return new ValueData<>(floatValue);
    }

    @RequestMapping("/bigInteger")
    public ValueData<BigInteger> bigIntegerValue() {
        return new ValueData<>(bigIntegerValue);
    }

    @RequestMapping("/bigDecimal")
    public ValueData<BigDecimal> bigDecimalValue() {
        return new ValueData<>(bigDecimalValue);
    }

    @RequestMapping("/booleanTrue")
    public ValueData<Boolean> booleanTrueValue() {
        return new ValueData<>(booleanTrueValue);
    }

    @RequestMapping("/booleanOne")
    public ValueData<Boolean> booleanOneValue() {
        return new ValueData<>(booleanOneValue);
    }

    @RequestMapping("/booleanYes")
    public ValueData<Boolean> booleanYesValue() {
        return new ValueData<>(booleanYesValue);
    }

    @RequestMapping("/booleanFalse")
    public ValueData<Boolean> booleanFalseValue() {
        return new ValueData<>(booleanFalseValue);
    }

    @RequestMapping("/booleanZero")
    public ValueData<Boolean> booleanZeroValue() {
        return new ValueData<>(booleanZeroValue);
    }

    @RequestMapping("/booleanNo")
    public ValueData<Boolean> booleanNoValue() {
        return new ValueData<>(booleanNoValue);
    }

    @RequestMapping("/uuid")
    public ValueData<UUID> uuidValue() {
        return new ValueData<>(uuidValue);
    }
}
