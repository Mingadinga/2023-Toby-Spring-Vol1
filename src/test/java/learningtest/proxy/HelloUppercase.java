package learningtest.proxy;

public class HelloUppercase implements Hello {

    Hello hello;

    public HelloUppercase(Hello hello) {
        this.hello = hello;
    }

    // 일반적인 프록시 작성의 문제 : 위임과 부가기능 중복
    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase(); // 위임과 부가기능 적용
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase(); // 여기서 부가기능은 위임 후에 적용
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
