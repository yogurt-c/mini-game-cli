package io.yogurt.cli_mini_game.config;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.entity.CodeQuestion;
import io.yogurt.cli_mini_game.game.codequiz.repository.CodeQuestionRepository;
import io.yogurt.cli_mini_game.game.entity.GameType;
import io.yogurt.cli_mini_game.game.repository.GameTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 시작 시 초기 데이터 로드
 */
@Component
@Profile("!test")  // 테스트 환경에서는 실행하지 않음
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final GameTypeRepository gameTypeRepository;
    private final CodeQuestionRepository codeQuestionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing data...");

        // 게임 타입 초기화
        initGameTypes();

        // 코드 퀴즈 문제 초기화
        if (codeQuestionRepository.count() == 0) {
            initCodeQuestions();
            log.info("Loaded {} code questions", codeQuestionRepository.count());
        } else {
            log.info("Code questions already exist, skipping initialization");
        }
    }

    private void initGameTypes() {
        if (gameTypeRepository.findByCode("CODE_QUIZ").isEmpty()) {
            GameType codeQuiz = new GameType(
                "CODE_QUIZ",
                "코드 퀴즈",
                "코드의 틀린 라인을 찾아 상대방에게 장애물을 보내는 게임"
            );
            gameTypeRepository.save(codeQuiz);
            log.info("Created game type: CODE_QUIZ");
        }
    }

    private void initCodeQuestions() {
        // Java 문제들
        codeQuestionRepository.save(new CodeQuestion(
            "public class Calculator {\n" +
            "    private int result;\n" +
            "\n" +
            "    public void add(int number) {\n" +
            "        result += number\n" +
            "    }\n" +
            "\n" +
            "    public int getResult() {\n" +
            "        return result;\n" +
            "    }\n" +
            "}",
            "5",
            "5번 라인: result += number 뒤에 세미콜론(;)이 누락되었습니다.",
            Language.JAVA
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "public class User {\n" +
            "    private String username;\n" +
            "    private String email;\n" +
            "\n" +
            "    public User(String username, String email) {\n" +
            "        this.username = username;\n" +
            "        this.emial = email;\n" +
            "    }\n" +
            "\n" +
            "    public String getEmail() {\n" +
            "        return email;\n" +
            "    }\n" +
            "}",
            "7",
            "7번 라인: this.emial은 this.email이어야 합니다. (변수명 오타)",
            Language.JAVA
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "public class StringUtils {\n" +
            "    public static int getLength(String str) {\n" +
            "        if (str == null) {\n" +
            "            return null;\n" +
            "        }\n" +
            "        return str.length();\n" +
            "    }\n" +
            "}",
            "4",
            "4번 라인: int를 반환해야 하는데 null을 반환할 수 없습니다.",
            Language.JAVA
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "public class MathHelper {\n" +
            "    public static int divide(int a, int b) {\n" +
            "        return a / b;\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(divide(10, 0));\n" +
            "    }\n" +
            "}",
            "3",
            "3번 라인: 0으로 나누기를 방지하는 검증이 필요합니다.",
            Language.JAVA
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "public class Example {\n" +
            "    public void printNumbers() {\n" +
            "        for (int i = 0; i < 10; i++) {\n" +
            "            System.out.println(i)\n" +
            "        }\n" +
            "    }\n" +
            "}",
            "4",
            "4번 라인: System.out.println(i) 뒤에 세미콜론(;)이 누락되었습니다.",
            Language.JAVA
        ));

        // Python 문제들
        codeQuestionRepository.save(new CodeQuestion(
            "def calculate_average(numbers):\n" +
            "    total = sum(numbers)\n" +
            "    count = len(numbers)\n" +
            "    return total / count\n" +
            "\n" +
            "result = calculate_average([])",
            "4",
            "4번 라인: 빈 리스트에 대해 0으로 나누기 오류가 발생합니다.",
            Language.PYTHON
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "class Person:\n" +
            "    def __init__(self, name, age):\n" +
            "        self.name = name\n" +
            "        self.age = age\n" +
            "\n" +
            "    def greet(self):\n" +
            "        print(f'Hello, my name is {self.nam}')",
            "7",
            "7번 라인: self.nam은 self.name이어야 합니다.",
            Language.PYTHON
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "def find_max(numbers):\n" +
            "    max_num = numbers[0]\n" +
            "    for num in numbers:\n" +
            "        if num > max_num\n" +
            "            max_num = num\n" +
            "    return max_num",
            "4",
            "4번 라인: if문 끝에 콜론(:)이 누락되었습니다.",
            Language.PYTHON
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "def process_data(data):\n" +
            "    result = []\n" +
            "    for item in data:\n" +
            "        if item > 0:\n" +
            "        result.append(item * 2)\n" +
            "    return result",
            "5",
            "5번 라인: 들여쓰기가 잘못되었습니다.",
            Language.PYTHON
        ));

        codeQuestionRepository.save(new CodeQuestion(
            "class Counter:\n" +
            "    def __init__(self):\n" +
            "        self.count = 0\n" +
            "\n" +
            "    def increment(self):\n" +
            "        count += 1\n" +
            "\n" +
            "    def get_count(self):\n" +
            "        return self.count",
            "6",
            "6번 라인: count 앞에 self.가 빠졌습니다.",
            Language.PYTHON
        ));

        log.info("Initialized {} code questions", codeQuestionRepository.count());
    }
}
