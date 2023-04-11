package springbook.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> { // 범용적 사용을 위해 Object로 지정

    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface; // 다이나믹 프록시를 생성할 때 필요. UserService 외의 인터페이스를 가진 타깃도 적용 가능

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);

        return Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
