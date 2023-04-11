package learningtest.factoryBean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    // 설정정보에 message에 MessageFactoryBean 타입을 주입하도록 설정했지만
    // 실제 주입되는 빈은 MessageFactoryBean의 제네릭으로 전달된 Message 타입이다
    @Test
    public void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        assertThat(message, is(Message.class));
        assertThat(((Message)message).getText(), is("Factory Bean"));
    }

    @Test
    public void getFactoryBean() throws Exception {
        Object factory = context.getBean("&message"); // 팩토리 빈 자체를 가져옴
        assertThat(factory, is(MessageFactoryBean.class));
    }

}
