package learningtest_callback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// 1차 적용 - BufferedReaderCallback
// 공 : 파일 리소스 준비하고 예외처리하고 닫기
// 차 : br을 받아 파일로 무언가를 함

// 2차 적용 - LineCallback
// 공 : 라인을 하나씩 읽어 누적 계산
// 차 : 합 혹은 곱

// 3차 적용 - LineCallback<T>
// 공 : 라인을 하나씩 읽어 누적 계산
// 차 : 타입, 연산

public class Calculator {
    public Integer calcSum(String filePath) throws IOException {
//        1차 개선
//        BufferedReaderCallback sumCallback = br -> {
//            Integer sum = 0;
//            String line = null;
//            while((line = br.readLine()) != null)
//                sum += Integer.valueOf(line);
//            return sum;
//        };
//        return fileReadTemplate(filePath, sumCallback);

//        2차 개선
        LineCallback<Integer> multiplyCallback = (line, value) -> value + Integer.valueOf(line);
        return lineReadTemplate(filePath, multiplyCallback, 0);
    }

    public int calcMultiply(String filePath) throws IOException {
//        1차 개선
//        BufferedReaderCallback multiplyCallback = br -> {
//            Integer multiply = 0;
//            String line = null;
//            while((line = br.readLine()) != null)
//                multiply *= Integer.valueOf(line);
//            return multiply;
//        };
//        return fileReadTemplate(numFilePath, multiplyCallback);

//        2차 개선
        LineCallback<Integer> sumCallback = (line, value) -> value * Integer.valueOf(line);
        return lineReadTemplate(filePath, sumCallback, 0);
    }

    // 3차 개선 - 제네릭스를 이용한 문자열 연산
    public String concatenateStrings(String filePath) throws IOException {
        LineCallback<String> concatenateCallback = (line, value) -> value + line;
        return lineReadTemplate(filePath, concatenateCallback, "");
    }

    // 2차 개선 템플릿
    private <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));
            T res = initVal;
            String line = null;
            while((line = br.readLine()) != null)
                callback.doSomethingWithLine(line, res);
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
    }

    // 1차 개선 템플릿
    private Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException { // method di
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));
            return callback.doSomethingWithReader(br);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
    }
}
