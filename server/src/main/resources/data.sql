-- 게임 종류 초기 데이터
INSERT INTO game_types (id, code, name, description) VALUES
(1, 'CODE_QUIZ', '코드 퀴즈', '코드의 틀린 라인을 찾아 상대방에게 장애물을 보내는 게임');

-- 코드퀴즈 문제 데이터

-- 문제 1: Java - 세미콜론 누락
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Calculator {
    private int result;

    public void add(int number) {
        result += number
    }

    public int getResult() {
        return result;
    }
}',
'5',
'5번 라인: result += number 뒤에 세미콜론(;)이 누락되었습니다.',
'JAVA'
);

-- 문제 2: Java - 변수명 오타
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class User {
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.emial = email;
    }

    public String getEmail() {
        return email;
    }
}',
'7',
'7번 라인: this.emial은 this.email이어야 합니다. (변수명 오타)',
'JAVA'
);

-- 문제 3: Java - 잘못된 return 타입
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class StringUtils {
    public static int getLength(String str) {
        if (str == null) {
            return null;
        }
        return str.length(,
'JAVA'
);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}',
'4',
'4번 라인: return 타입이 int인데 null을 반환할 수 없습니다. return 0 또는 예외를 던져야 합니다.'
);

-- 문제 4: 배열 인덱스 범위 초과
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class ArrayProcessor {
    public static int findMax(int[] numbers) {
        int max = numbers[0];

        for (int i = 0; i <= numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        return max;
    }
}',
'5',
'5번 라인: i <= numbers.length는 배열 인덱스 범위를 초과합니다. i < numbers.length여야 합니다.'
,
'JAVA'
);

-- 문제 5: equals vs == 비교
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class LoginService {
    private static final String ADMIN_PASSWORD = "admin123";

    public boolean isAdminLogin(String password) {
        if (password == ADMIN_PASSWORD) {
            return true;
        }
        return false;
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
}',
'5',
'5번 라인: 문자열 비교는 == 대신 equals()를 사용해야 합니다. password.equals(ADMIN_PASSWORD)가 올바릅니다.'
,
'JAVA'
);

-- 문제 6: Java - NullPointerException 가능성
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class StringProcessor {
    public String toUpperCase(String input) {
        return input.toUpperCase(,
'JAVA'
);
    }

    public String toLowerCase(String input) {
        if (input != null) {
            return input.toLowerCase();
        }
        return "";
    }
}',
'3',
'3번 라인: input이 null일 경우 NullPointerException이 발생합니다. null 체크가 필요합니다.'
);

-- 문제 7: Java - 무한 루프
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Counter {
    public void countDown(int start) {
        int i = start;
        while (i >= 0) {
            System.out.println(i,
'JAVA'
);
        }
        System.out.println("Done!");
    }
}',
'5',
'5번 라인: i를 감소시키지 않아 무한 루프가 발생합니다. i--가 필요합니다.'
);

-- 문제 8: Java - 잘못된 메서드 오버라이드
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class CustomList extends ArrayList<String> {
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj,
'JAVA'
);
    }

    public int hashcode() {
        return super.hashCode();
    }
}',
'7',
'7번 라인: hashcode()가 아니라 hashCode()여야 합니다. 오버라이드가 되지 않습니다.'
);

-- 문제 9: Java - 잘못된 제네릭 사용
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'import java.util.*;

public class DataProcessor {
    public List<Integer> processNumbers() {
        List numbers = new ArrayList(,
'JAVA'
);
        numbers.add(1);
        numbers.add(2);
        return numbers;
    }
}',
'5',
'5번 라인: Raw type을 사용하고 있습니다. List<Integer>로 타입을 명시해야 합니다.'
);

-- 문제 10: Java - 리소스 누수
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'import java.io.*;

public class FileReader {
    public String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path),
'JAVA'
);
        String line = reader.readLine();
        return line;
    }
}',
'6',
'6번 라인: reader를 close()하지 않아 리소스 누수가 발생합니다. try-with-resources를 사용해야 합니다.'
);

-- 문제 11: Python - 들여쓰기 오류
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def calculate_sum(numbers):
    total = 0
    for num in numbers:
        total += num
     print(f"Current total: {total}")
    return total',
'5',
'5번 라인: 들여쓰기가 잘못되었습니다. print문이 for 블록 안에 있어야 합니다.'
,
'PYTHON'
);

-- 문제 12: Python - 변수명 오타
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'class Student:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def get_info(self):
        return f"{self.name} is {self.aga} years old"',
'7',
'7번 라인: self.aga는 self.age의 오타입니다.'
,
'PYTHON'
);

-- 문제 13: Python - 리스트 인덱스 오류
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def get_first_and_last(items):
    if len(items) > 0:
        first = items[0]
        last = items[len(items)]
        return first, last
    return None, None',
'4',
'4번 라인: 리스트 인덱스는 0부터 시작하므로 items[len(items)]는 범위를 벗어납니다. items[len(items)-1] 또는 items[-1]이어야 합니다.'
,
'PYTHON'
);

-- 문제 14: Python - 잘못된 비교 연산자
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def check_equality(a, b):
    if a = b:
        return True
    return False

def check_greater(a, b):
    return a > b',
'2',
'2번 라인: 비교 연산자는 ==이어야 합니다. =는 대입 연산자입니다.'
,
'PYTHON'
);

-- 문제 15: Python - 딕셔너리 키 오류
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def process_user(user_data):
    name = user_data["name"]
    age = user_data["age"]
    email = user_data["email"]

    return {
        "name": name,
        "age": age,
        "contact": email
    }',
'4',
'4번 라인: user_data에 "email" 키가 없을 경우 KeyError가 발생합니다. user_data.get("email")을 사용하거나 키 존재 여부를 확인해야 합니다.'
,
'PYTHON'
);

-- 문제 16: Java - Switch문 break 누락
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class DayConverter {
    public String getDayType(int day) {
        String type = "";
        switch (day) {
            case 1:
            case 7:
                type = "Weekend";
            default:
                type = "Weekday";
        }
        return type;
    }
}',
'7',
'7번 라인: break;가 누락되어 fall-through가 발생합니다. Weekend 케이스 후 break가 필요합니다.'
,
'JAVA'
);

-- 문제 17: Java - 정수 나눗셈 오류
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Calculator {
    public double calculateAverage(int sum, int count) {
        if (count == 0) {
            return 0.0;
        }
        return sum / count;
    }
}',
'6',
'6번 라인: 정수 나눗셈으로 소수점이 버려집니다. (double)sum / count 또는 sum / (double)count로 캐스팅해야 합니다.'
,
'JAVA'
);

-- 문제 18: Java - Static 메서드에서 인스턴스 변수 접근
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Counter {
    private int count = 0;

    public static void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}',
'5',
'5번 라인: static 메서드에서 인스턴스 변수 count에 접근할 수 없습니다. count도 static이어야 하거나 메서드를 non-static으로 변경해야 합니다.'
,
'JAVA'
);

-- 문제 19: Python - 가변 기본 인자
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def add_item(item, items=[]):
    items.append(item)
    return items

result1 = add_item(1)
result2 = add_item(2)',
'1',
'1번 라인: 가변 기본 인자(mutable default argument)는 함수 호출 간에 공유됩니다. items=None을 사용하고 함수 내에서 if items is None: items = []로 초기화해야 합니다.'
,
'PYTHON'
);

-- 문제 20: Python - is vs == 혼동
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def check_value(value):
    if value is 100:
        return "Found"
    return "Not found"

def check_empty(lst):
    return lst is []',
'2',
'2번 라인: 값 비교에는 ==을 사용해야 합니다. is는 객체 동일성을 비교합니다. value == 100이 올바릅니다.'
,
'PYTHON'
);

-- 문제 21: Java - 배열 초기화 오류
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class ArrayDemo {
    public int[] createArray() {
        int[] numbers = new int[5];
        numbers[0] = 1;
        numbers[1] = 2;
        numbers[5] = 3;
        return numbers;
    }
}',
'6',
'6번 라인: 배열 크기가 5이므로 인덱스는 0~4입니다. numbers[5]는 ArrayIndexOutOfBoundsException을 발생시킵니다.'
,
'JAVA'
);

-- 문제 22: Java - 잘못된 예외 처리
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class FileProcessor {
    public void processFile(String path) {
        try {
            // 파일 처리 로직
        } catch (Exception e) {
            // 예외 무시
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage(),
'JAVA'
);
        }
    }
}',
'7',
'7번 라인: IOException은 Exception의 하위 클래스이므로 이미 5번 라인에서 catch되었습니다. unreachable code입니다.'
);

-- 문제 23: Python - 잘못된 문자열 포맷팅
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def format_message(name, age):
    message = "Hello, {name}! You are {age} years old."
    return message

def format_price(price):
    return f"Price: ${price:.2f}"',
'2',
'2번 라인: 일반 문자열에서 {}는 변수로 치환되지 않습니다. f"Hello, {name}!"처럼 f-string을 사용해야 합니다.'
,
'PYTHON'
);

-- 문제 24: Python - 잘못된 메서드 정의
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'class Calculator:
    def add(a, b):
        return a + b

    def multiply(self, a, b):
        return a * b',
'2',
'2번 라인: 인스턴스 메서드의 첫 번째 매개변수는 self여야 합니다. def add(self, a, b):가 올바릅니다.'
,
'PYTHON'
);

-- 문제 25: Java - Finally 블록 실수
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class ResourceHandler {
    public String readData() {
        String data = "";
        try {
            data = "Some data";
            return data;
        } finally {
            data = null;
        }
    }
}',
'8',
'8번 라인: finally 블록에서 data를 null로 설정해도 이미 return된 값에는 영향을 주지 않지만, 혼란을 야기합니다. 불필요한 코드입니다.'
,
'JAVA'
);

-- 문제 26: Python - 클로저 변수 바인딩
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def create_multipliers():
    multipliers = []
    for i in range(5):
        multipliers.append(lambda x: x * i)
    return multipliers

funcs = create_multipliers()
print(funcs[0](2))  # 예상: 0, 실제: 8',
'4',
'4번 라인: 람다는 i의 값이 아닌 변수 i를 참조합니다. 루프가 끝나면 i=4가 되어 모든 람다가 4를 사용합니다. lambda x, i=i: x * i로 수정해야 합니다.'
,
'PYTHON'
);

-- 문제 27: Java - 논리 연산자 단락 평가 오해
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Validator {
    public boolean isValidUser(String username) {
        return username != null & username.length() > 0;
    }

    public boolean hasAccess(User user) {
        return user != null && user.isActive(,
'JAVA'
);
    }
}',
'3',
'3번 라인: &는 비트 연산자입니다. 논리 연산에는 &&를 사용해야 합니다. username이 null이면 username.length()에서 NullPointerException이 발생합니다.'
);

-- 문제 28: Python - 글로벌 변수 수정
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'counter = 0

def increment():
    counter = counter + 1
    return counter

def reset():
    global counter
    counter = 0',
'4',
'4번 라인: global 키워드 없이 글로벌 변수를 수정할 수 없습니다. UnboundLocalError가 발생합니다. global counter를 함수 시작 부분에 추가해야 합니다.'
,
'PYTHON'
);

-- 문제 29: Java - equals와 hashCode 불일치
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Person {
    private String name;
    private int age;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        Person p = (Person) obj;
        return this.name.equals(p.name) && this.age == p.age;
    }
}',
'9',
'9번 라인: equals()를 오버라이드했지만 hashCode()를 오버라이드하지 않았습니다. HashMap/HashSet에서 문제가 발생할 수 있습니다. hashCode()도 구현해야 합니다.'
,
'JAVA'
);

-- 문제 30: Python - 딕셔너리 순회 중 수정
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def remove_negatives(data):
    for key in data:
        if data[key] < 0:
            del data[key]
    return data

result = remove_negatives({"a": 1, "b": -2, "c": 3})',
'4',
'4번 라인: 딕셔너리를 순회하면서 항목을 삭제하면 RuntimeError가 발생합니다. for key in list(data.keys()): 또는 딕셔너리 컴프리헨션을 사용해야 합니다.'
,
'PYTHON'
);

-- 문제 31: Java - Comparable 구현 오류
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Score implements Comparable<Score> {
    private int value;

    public Score(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Score other) {
        return this.value - other.value;
    }
}',
'10',
'10번 라인: 정수 오버플로우 위험이 있습니다. Integer.compare(this.value, other.value)를 사용하는 것이 안전합니다.'
,
'JAVA'
);

-- 문제 32: Python - 잘못된 슬라이싱
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'def get_middle_elements(lst):
    if len(lst) < 3:
        return []
    start = len(lst) / 2 - 1
    end = len(lst) / 2 + 1
    return lst[start:end]',
'4,5',
'4, 5번 라인: 파이썬3에서 /는 float 나눗셈입니다. 리스트 인덱스는 정수여야 하므로 //를 사용해야 합니다.'
,
'PYTHON'
);

-- 문제 33: Java - 동시성 문제
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'public class Counter {
    private int count = 0;

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}',
'5',
'5번 라인: 멀티스레드 환경에서 count++는 원자적 연산이 아닙니다. synchronized 키워드나 AtomicInteger를 사용해야 합니다.'
,
'JAVA'
);

-- 문제 34: Python - super() 호출 누락
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'class Parent:
    def __init__(self, name):
        self.name = name

class Child(Parent):
    def __init__(self, name, age):
        self.age = age',
'7',
'7번 라인: 부모 클래스의 __init__을 호출하지 않아 self.name이 초기화되지 않습니다. super().__init__(name)을 추가해야 합니다.'
,
'PYTHON'
);

-- 문제 35: Java - Optional 잘못된 사용
INSERT INTO code_questions (code, incorrect_lines, explanation, language) VALUES (
'import java.util.Optional;

public class UserService {
    public Optional<String> getUserName(Long userId) {
        String name = findUserById(userId,
'JAVA'
);
        return Optional.of(name);
    }

    private String findUserById(Long id) {
        return null; // DB 조회
    }
}',
'6',
'6번 라인: Optional.of()는 null을 허용하지 않습니다. name이 null이면 NullPointerException이 발생합니다. Optional.ofNullable(name)을 사용해야 합니다.'
);