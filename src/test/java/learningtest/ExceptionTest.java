package learningtest;
import java.util.Scanner;


public class ExceptionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = readInt(scanner);
        System.out.println("입력된 정수: " + num);
    }

    private static int readInt(Scanner scanner) {
        int num = 0;
        boolean isNum = false;
        while (!isNum) {
            System.out.print("정수를 입력하세요: ");
            String input = scanner.nextLine();
            try {
                num = Integer.parseInt(input);
                isNum = true;
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다. 다시 입력하세요.");
            }
        }
        return num;
    }

}
