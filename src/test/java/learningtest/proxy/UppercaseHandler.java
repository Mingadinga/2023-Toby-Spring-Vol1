package learningtest.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

    Object target; // 타깃 제한 없음

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object ret = method.invoke(target, args); // 타깃 위임
        // 대문자로 반환 가능한 조건 정의: 리턴타입과 메소드 선별
        if (ret instanceof String && method.getName().startsWith("say")) { // 반환값이 문자열이면 대문자로 반환
            return ((String)ret).toUpperCase();
        } else {
            return ret; // 아니면 그대로
        }

    }
}
