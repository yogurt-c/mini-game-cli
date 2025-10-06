-- 초기 코드 문제 데이터
-- Spring Boot 시작 시 자동으로 실행됨

-- EASY 난이도 Java 문제
INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!")
    }
}', '3', '세미콜론(;) 누락', 'EASY', 'JAVA');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class Calculator {
    public int add(int a, int b) {
        return a + b
    }
}', '3', 'return문에 세미콜론 누락', 'EASY', 'JAVA');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class Test {
    public void printNumber() {
        int number = 10;
        System.out.println(number);
    }
}', '5', 'printNumber 메서드 닫는 중괄호 누락', 'EASY', 'JAVA');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class Loop {
    public static void main(String[] args) {
        for (int i = 0; i < 10 i++) {
            System.out.println(i);
        }
    }
}', '3', 'for문 조건식에 세미콜론 누락', 'EASY', 'JAVA');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class Array {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5}
        System.out.println(numbers[0]);
    }
}', '3', '배열 선언 후 세미콜론 누락', 'EASY', 'JAVA');

-- EASY 난이도 Python 문제
INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('def hello():
    print("Hello, World!")
hello()', '1', '함수 정의 뒤 콜론(:) 누락', 'EASY', 'PYTHON');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('for i in range(10)
    print(i)', '1', 'for문 뒤 콜론 누락', 'EASY', 'PYTHON');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('numbers = [1, 2, 3, 4, 5]
total = sum(numbers)
print(total', '3', 'print문 닫는 괄호 누락', 'EASY', 'PYTHON');

-- MEDIUM 난이도 Java 문제
INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class StringTest {
    public static void main(String[] args) {
        String text = "Hello";
        if (text.equals("Hello") {
            System.out.println("Match!");
        }
    }
}', '4', 'if문 조건식 닫는 괄호 누락', 'MEDIUM', 'JAVA');

INSERT INTO code_question (code, incorrect_lines, explanation, difficulty, language) VALUES
('public class NullCheck {
    public static void main(String[] args) {
        String name = null;
        if (name = null) {
            System.out.println("Name is null");
        }
    }
}', '4', '등호(=) 대신 비교 연산자(==) 사용해야 함', 'MEDIUM', 'JAVA');
