import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileWriter;
import de.schlichtherle.truezip.file.TFileReader;

import java.io.*;
import java.util.Arrays;
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

    //Блок функций реализующих комнды Bash
    private static void cd(String... args){
        if(args.length == 0) return;
        TFile oldFileSystem = fileSystem;
        String old_root = root_current_folder;
        String old_folder = current_folder;

        if(args[0].startsWith(".") & !args[0].startsWith("..")) {
            root_current_folder = args[0].replaceFirst(".", root_current_folder);
            root_current_folder = root_current_folder.replaceFirst("//", "/");
        }else
        if(args[0].startsWith(".."))
            root_current_folder = root_current_folder.substring(0, root_current_folder.lastIndexOf("/"));

        if(args[0].startsWith("/")) root_current_folder = args[0];

        String[] a = root_current_folder.split("/");
        if(a.length > 1)
            current_folder = "/" + a[a.length - 1];
        else
            current_folder = "/";

        fileSystem = new TFile(main_root + root_current_folder, detector);
        if(!fileSystem.exists()){
            System.out.println("Путь " + root_current_folder + " - не существует");
            root_current_folder = old_root;
            current_folder = old_folder;
            fileSystem = oldFileSystem;
        }

    }
    private static void nano(String... args) {
        if(args.length == 0) return;

        TFile oldFileSystem = fileSystem;

        if(args[0].startsWith(".")) {
            root_current_folder = args[0].replaceFirst(".", root_current_folder);
            root_current_folder = root_current_folder.replaceFirst("//", "/");
        }

        fileSystem = new TFile(main_root + root_current_folder + args[0], detector);
        if(!fileSystem.exists()) {
            try {
                fileSystem.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("nano vshell edition by megboyZZ");

        String in_file = "";

        Writer writer;
        try {
            writer = new TFileWriter(fileSystem);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        while(true){
            try {
                in_file = in.nextLine();
                if(in_file.equals(args[0])) break;
                writer.write(in_file + "\n");
            } catch (IOException ignored){
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileSystem = oldFileSystem;

    }
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

    private static void mkdir(String... args){
        if(args.length == 0) return;

        TFile oldFileSystem = fileSystem;

        fileSystem = new TFile(main_root + root_current_folder + args[0], detector);
        if(!fileSystem.exists())
            fileSystem.mkdir();

        fileSystem = oldFileSystem;

    }

    private static void cat(String... args){
        if(args.length == 0) return;
        boolean lineNumber = false;
        boolean no = false;
        String fileName = "";

        TFile oldFileSystem = fileSystem;
        String old_root = root_current_folder;
        String old_folder = current_folder;


        if(args.length > 0){
            for(String cmd : args) {
                if (cmd.equals("-n")) lineNumber = true;
                if ((cmd.startsWith("/") | cmd.startsWith(".")) & !new TFile(fileSystem.getAbsoluteFile() + "/" + cmd).isFile()){
                    cd(cmd);
                    no = true;
                } else fileName = cmd;
            }
        }


        fileSystem = new TFile(main_root + root_current_folder + fileName, detector);
        if(!fileSystem.exists()) {
            System.out.println("Файл " + args[0] + " - не существует");
            fileSystem = oldFileSystem;
            root_current_folder = old_root;
            return;
        }

        Reader reader;
        try {
            reader = new TFileReader(fileSystem);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileSystem = oldFileSystem;
            root_current_folder = old_root;
            return;
        }

        char[] buf = new char[256];
        int c = 0;
        while(true){
            try {
                c = reader.read(buf);
                if (c<=0) break;
            } catch (IOException e) {
                fileSystem = oldFileSystem;
                root_current_folder = old_root;
                e.printStackTrace();
            }

            if(c < 256){
                buf = Arrays.copyOf(buf, c);
            }
            if(lineNumber) {
                String[] str = (new String(buf)).split("\n");
                for(int i = 1; i <= str.length; i++)
                    System.out.println(i + " " + str[i-1]);
            }
            else
                System.out.print(buf);
        }

        try {
            reader.close();
        } catch (IOException e) {
            fileSystem = oldFileSystem;
            root_current_folder = old_root;
            e.printStackTrace();
        }

        if(no){
            root_current_folder = old_root;
            current_folder = old_folder;
        }
        fileSystem = oldFileSystem;

    }

    public static void main(String[] args) {
        //Блок проверок аргументов
        if(args.length == 0) System.out.println("Файл не передан");

        File file = new File(args[0]);

        if(!file.exists()) {
            System.out.println("Файл " + args[0] + " не существует.");
            return;
        }
        if(!file.isFile()) {
            System.out.println(args[0] + " - путь. Чтение не возможно.");
            return;
        }
        //Запуск
        System.out.println(
                "Vshell by megboyZZ v0.1\n" +
                        "Напечатайте help для получения помощи\n");

        //Инициализация функциональных полей
        detector = new TArchiveDetector(separator);

        fileSystem = new TFile(file, detector);

        main_root = fileSystem.getAbsolutePath();

        in = new Scanner(System.in);

        //Основной цикл Bash
        while(true) {

            System.out.print("~$" + current_folder + ">");
            String[] command = in.nextLine().split(" ");//cat lflfl lflflfl
            //Выбор команд Bash
            if (cd_command.equals(command[0])) {
                cd(Arrays.copyOfRange(command, 1, command.length));
            } else if (pwd_command.equals(command[0])) {
                System.out.println(root_current_folder);
            } else if (ls_command.equals(command[0])) {
                ls(Arrays.copyOfRange(command, 1, command.length));
            } else if (cat_command.equals(command[0])) {
                cat(Arrays.copyOfRange(command, 1, command.length));
            } else if (nano_command.equals(command[0])) {
                nano(Arrays.copyOfRange(command, 1, command.length));
            } else if (mkdir_command.equals(command[0])) {
                mkdir(Arrays.copyOfRange(command, 1, command.length));
                //case rm_command -> rm(Arrays.copyOfRange(command, 1, command.length));
            } else if (quit_command.equals(command[0])) {
                return;
            } else if (help_command.equals(command[0])) {
                System.out.println(help_out);
            } else if (clear_command.equals(command[0])) {
                for (int i = 0; i < 50; ++i) System.out.print("\n");
            } else {
                System.out.println("команда '" + command[0] + "' не найдена, проверьте написание");
            }
        }
    }
}