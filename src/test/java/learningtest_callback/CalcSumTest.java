package learningtest_callback;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CalcSumTest {
    Calculator calculator;
    String numFilePath;


    @Before public void setUp() {
        calculator = new Calculator();
        numFilePath = getClass().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        int sum = calculator.calcSum(numFilePath);
        assertThat(sum, is(10));
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        int multiply = calculator.calcMultiply(numFilePath);
        assertThat(multiply, is(24));
    }
}
