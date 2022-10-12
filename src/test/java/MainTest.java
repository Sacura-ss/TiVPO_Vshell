import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    public MainTest() {
        Locale.setDefault(new Locale("en", "US"));
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test // тест команды ls: показываем все, что лежит в корне переданной файловой системы
    public void testLS() {

        String data = "ls";

        System.setIn(new ByteArrayInputStream(data.getBytes()));

        Bash.main(new String[]{"D:/test.zip"});

        assert outContent.toString().contains("colorlib-regform-4")
                & outContent.toString().contains("login-form-v2");
    }

    @Test //Тестируем команду mkdir s, s - название папки, и проверяем ее наличие в ls
    public void testMkdir(){

        String data = "mkdir s\r\nls";

        System.setIn(new ByteArrayInputStream(data.getBytes()));

        Bash.main(new String[]{"D:/test.zip"});

        assert outContent.toString().contains("s");

    }
    @Test
    public void testNano(){
        String data = "nano file2.txt\r\nHello from nano\r\nfile2.txt\r\ncat file2.txt";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Bash.main(new String[]{"D:/test.zip"});
        assert outContent.toString().contains("Hello from nano");
    }

    @Test //Тестируем команду cat
    public void testCat(){
        String data = "cat file.txt";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Bash.main(new String[]{"D:/test.zip"});
        assert outContent.toString().contains("Hello world!");
    }

    @Test //Тестируем команду pwd из корня
    public void testPwd(){
        String data = "pwd";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Bash.main(new String[]{"D:/test.zip"});
        assert outContent.toString().contains("~$/>/");
    }

    @Test
    public void testPwdNonRoot(){ //Тестируем команду pwd не из корня
        String data = "cd /colorlib-regform-4/js\r\npwd";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Bash.main(new String[]{"D:/test.zip"});
        assert outContent.toString().contains("/colorlib-regform-4/js");
    }

}
