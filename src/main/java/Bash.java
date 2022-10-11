import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;

import java.util.Scanner;

public class Bash {

    public static boolean isTesting = false;

    //Список комманд
    private final static String cd_command = "cd";
    private final static String pwd_command = "pwd";
    private final static String ls_command = "ls";
    private final static String cat_command = "cat";

    private final static String quit_command = "quit";
    private final static String help_command = "help";
    private final static String nano_command = "nano";
    private final static String clear_command = "clear";
    private final static String mkdir_command = "mkdir";
    private final static String rm_command = "rm";

    //Сепаратор для TrueZIP
    private final static String separator = "tar|tar.gz|zip"; //cat lol.txt

    //Пути в файловой системе
    private static String current_folder = "/";
    private static String root_current_folder = "/";
    private static String main_root = "";

    //Основной образ файловой системы
    private static TFile fileSystem;

    //Детектор сигнатур архивных файлов
    private static TArchiveDetector detector;

    //Основной поток ввода
    private static Scanner in;

    //Помощь
    private static final String help_out =
            "Для перехода в корневую дерикторию используйте - \"cd /\"\n" +
                    "\n" +
                    "Для перехода на один уровень вверх по каталогу используйте - \"cd ..\"\n" +
                    "\n" +
                    "Чтобы получить полный путь к вашему текущему каталогу, используйте pwd, не имеет ключей\n" +
                    "\n" +
                    "Чтобы выйти используйте quit\n" +
                    "\n" +
                    "Чтобы войти в nano - nano, чтобы выйти из nano - введите имя редактирумого файла\n" +
                    "\n" +
                    "Чтобы осмотреться в папке используйте ls, ключ -a для расширенной информации\n" +
                    "\n" +
                    "Чтобы вывести содержиомое файла используйте cat, -n - нумерование строк\n" +
                    "\n" +
                    "Чтобы создать папку используте mkdir <имя папки>\n" +
                    "\n" +
                    "Чтобы удалить папку/файл используте rm <имя удаляемого объекта>";

    private static void ls(String... args) {

        TFile oldFileSystem = fileSystem;
        String old_root = root_current_folder;
        String old_folder = current_folder;

        boolean allInfo = false;
        boolean no = false;
        if (args.length > 0) {
            for (String cmd : args) {
                if (cmd.equals("-a")) allInfo = true;
                if (cmd.startsWith("/") | cmd.startsWith(".")) {
                    cd(cmd);
                    no = true;
                }
            }
        }


        for (String name : fileSystem.list()) {
            if (allInfo)
                if (new TFile(fileSystem.getAbsoluteFile() + "/" + name).isFile())
                    System.out.print("Файл - ");
                else
                    System.out.print("Папка - ");
            System.out.println(name + " ");
        }
        if (no) {
            root_current_folder = old_root;
            current_folder = old_folder;
        }
        fileSystem = oldFileSystem;

    }

    public static void main(String[] args) {

    }
}