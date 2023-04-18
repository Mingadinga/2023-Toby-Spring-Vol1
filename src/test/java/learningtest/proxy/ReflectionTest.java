package learningtest.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReflectionTest {

    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        // length
        assertThat(name.length(), is(6));

        Method lengthMethod = String.class.getMethod("length"); // Method: 메소드 메타정보, 메소드 실행 기능
        assertThat((Integer)lengthMethod.invoke(name), is(6)); // invoke(obj, arg) : obj.메소드(arg)

        // charAt
        assertThat(name.charAt(0), is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name, 0), is('S'));

    }

//    @Test
//    public void simpleProxy() {
//        Hello hello = new HelloTarget();
//        assertThat(hello.sayHello("Min"), is("Hello Min"));
//        assertThat(hello.sayHi("Min"), is("Hi Min"));
//        assertThat(hello.sayThankYou("Min"), is("Thank You Min"));
//
//        Hello proxiedHello = new HelloUppercase(new HelloTarget());
//        assertThat(proxiedHello.sayHello("Min"), is("HELLO MIN"));
//        assertThat(proxiedHello.sayHi("Min"), is("HI MIN"));
//        assertThat(proxiedHello.sayThankYou("Min"), is("THANK YOU MIN"));
//    }

    @Test
    public void dynamicProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance( // newProxyInstance에 의해 만들어지는 오브젝트는 Hello 타입
                getClass().getClassLoader(), // 다이나믹 프록시 클래스 로딩에 사용하는 클래스 로더
                new Class[] {Hello.class}, // 구현할 인터페이스
                new UppercaseHandler(new HelloTarget())); // InvocationHandler

        assertThat(proxiedHello.sayHello("Min"), is("HELLO MIN"));
        assertThat(proxiedHello.sayHi("Min"), is("HI MIN"));
        assertThat(proxiedHello.sayThankYou("Min"), is("THANK YOU MIN"));
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();
        assertThat(proxiedHello.sayHello("Min"), is("HELLO MIN"));
        assertThat(proxiedHello.sayHi("Min"), is("HI MIN"));
        assertThat(proxiedHello.sayThankYou("Min"), is("THANK YOU MIN"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }
    }

    static interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {

        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }

    @Test
    public void classNamePointcutAdvisor() {

        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT"); // 클래스 이름이 HelloT로 시작하는 것만 선정
                    }
                };
            }
        };
        classMethodPointcut.setMappedName("sayH*");

        // 테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);
//        checkAdviced(new HelloWorld(), classMethodPointcut, true);
//        checkAdviced(new HelloToby(), classMethodPointcut, true);

    }

    public void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        } else {
            assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }



    }


}
