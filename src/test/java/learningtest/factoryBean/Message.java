package learningtest.factoryBean;

public class Message {

    String text;

    // 외부의 생성자 접근 차단
    private Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // 생성자 대신 사용할 수 있는 스태틱 팩토리 메소드 제공
    public static Message newMessage(String text) {
        return new Message(text);
    }

}
