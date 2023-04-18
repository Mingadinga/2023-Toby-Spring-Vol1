package learningtest.pointcut;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PointcutExpressionTest {

    @Test
    public void methodSignaturePointcut() throws Exception {
        System.out.println(Target.class.getMethod("minus", int.class, int.class));

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int learningtest.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");

        // Target.minus
        assertThat(pointcut.getClassFilter().matches(Target.class)
                && pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true));

        // Target.plus
        assertThat(pointcut.getClassFilter().matches(Target.class)
                && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false));



    }
}
