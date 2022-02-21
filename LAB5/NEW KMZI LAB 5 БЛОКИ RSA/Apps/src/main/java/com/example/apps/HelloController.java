package com.example.apps;

import java.io.*;
import java.time.Clock;
import java.net.*;
import javafx.fxml.FXML;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import static java.lang.Math.*;
import javafx.scene.control.TextField;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HelloController {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private ServerSocket ServSock = null;
    private int port1 = 3501;
    private int port2 = 3502;
    private Socket socket = null;
    private Socket socket1 = null;
    private Socket socket2 = null;
    private boolean IsConnect = false;
    private int P = 0, G = 0, A = 0, H = 0, R = 0, K = 0;
    public final static int EMPTY_CHAR = 1030;
    public final static int SPACE_NUMBER = 99;
    public static int FI = 150 * 282; // Функция Эейлера
    public static int mod = 151 * 283; // Модуль (P*Q)
    public static int CKey = 1; // Количество ключей которое будет сгенерировано
    private int OpenBobKey = 0, CloseBobKey = 0, OpenAliceKey = 0, CloseAliceKey = 0, OpenTrentKey = 0;
    private String BobPodT = "", AlicePodT = "", PodAliceBob = "";
    private long marktime = 0;


    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private TextArea CHAT_AREA;
    @FXML
    private Button CONNECTBTN;
    @FXML
    private Button CREATEBTN;
    @FXML
    private TextArea DEBUG_AREA;
    @FXML
    private TextField IP_FIELD;
    @FXML
    private TextField NickName;
    @FXML
    private TextField PORT_FIELD;
    @FXML
    private Button SENDBTN;
    @FXML
    private Button SETNAME;
    @FXML
    private TextField TEXT_FIELD;

    public String in_username = "Default";
    public String ex_username = "Default";

    @FXML
    void initialize() {
        DEBUG_AREA.appendText("Порт должен быть в диапозоне от 1025 до 65535");
        CREATEBTN.setOnAction(event -> {
            String s_port = PORT_FIELD.getText().trim();
            int port = Integer.parseInt(s_port);
            IsConnect = true;
            try {
                ServSock = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
                DEBUG_AREA.appendText("\nПорт для подключения: " + port + "\nОжидаем подключения..");
                new ReadServer().start(); // нить читающая сообщения из сокета в бесконечном цикле
            } catch (Exception x) {
                x.printStackTrace();
            }
        });
        SETNAME.setOnAction(event -> {
            if (IsConnect == false) {
                in_username = NickName.getText().trim();
                DEBUG_AREA.appendText("\nИмя изменено на '" + in_username + "'");
            }
        });
        CONNECTBTN.setOnAction(event -> {
            String serv_port = PORT_FIELD.getText().trim();
            int port = Integer.parseInt(serv_port); // здесь обязательно нужно указать порт к которому привязывается сервер.
            String serv_ip = IP_FIELD.getText().trim(); // это IP-адрес компьютера, где исполняется наша серверная программа.
            try {
                InetAddress ipAddress = InetAddress.getByName(serv_ip); // создаем объект который отображает вышеописанный IP-адрес.
                //Конвертируем IP
                DEBUG_AREA.appendText("\nПодключение к серверу " + serv_ip + ":" + port);
                socket = new Socket(ipAddress, port); // создаем сокет используя IP-адрес и порт сервера.
                new ReadClient().start(); // нить читающая сообщения из сокета в бесконечном цикле
                DEBUG_AREA.appendText("\nПодключение успешно!");
                IsConnect = true;
            } catch (Exception x) {
                DEBUG_AREA.appendText("\nПодключение не удалось!");
            }
        });
        SENDBTN.setOnAction(event -> {
            if (IsConnect == true) {
                try {
                    OutputStream sout = socket.getOutputStream();
                    DataOutputStream out = new DataOutputStream(sout);
                    String line = TEXT_FIELD.getText();
                    CHAT_AREA.appendText("\n[" + in_username + "]: " + line);
                    line = Encryption(line, K);
                    out.writeUTF(line); // Отсылаем текст
                    TEXT_FIELD.setText(null); // Очищаем строку
                    out.flush(); // Поток передает данные
                } catch (IOException e) {
                    DEBUG_AREA.appendText("\nПроверьте подключение!");
                }
            }
        });
    }


    public static int gcd(int a, int b) { // НОД двух чисел
        if (b == 0) return a;
        if (a == 0) return b;

        // А и B чётные
        if ((a & 1) == 0 && (b & 1) == 0) return gcd(a >> 1, b >> 1) << 1;

            // А чётное, B нечётное
        else if ((a & 1) == 0) return gcd(a >> 1, b);

            // A нечётное, B чётное
        else if ((b & 1) == 0) return gcd(a, b >> 1);

            // А и B нечётные, A >= B
        else if (a >= b) return gcd((a - b) >> 1, b);

            // А и B нечётные, A < B
        else return gcd(a, (b - a) >> 1);
    }

    public static String Encryption(String Text, int Key) {
        String encrypt = "";
        char m;
        for (int i = 0; i < Text.length(); i++) {
            char t = Text.charAt(i);
            int n = t ^ Key;
            m = (char) Integer.parseInt(String.valueOf(n));
            encrypt += m;
        }
        return encrypt;
    }

    public static String Decryption(String Text, int Key) {
        String decrypt = "";
        char m;
        for (int i = 0; i < Text.length(); i++) {
            char t = Text.charAt(i);
            int n = t ^ Key;
            m = (char) Integer.parseInt(String.valueOf(n));
            decrypt += m;
        }
        return decrypt;
    }

    //Метод получения псевдослучайного целого числа от min до max (включая max);
    public static int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

    public static int GetRoot(int p) {
        // Первообразный корень простого числа
        for (int i = 0; i < p; i++)
            if (IsRoot(p, i))
                return i;
        return 0;
    }

    public static boolean IsRoot(long p, long a) {
        // Первообразный корень простого числа
        if (a == 0 || a == 1)
            return false;
        long last = 1;

        Set<Long> set = new HashSet<>();
        for (long i = 0; i < p - 1; i++) {
            last = (last * a) % p;
            if (set.contains(last))
                return false;
            set.add(last);
        }
        return true;
    }

    public static int modInverse(int a, int m) { // Обратный элемент по модулю
        a = a % m;
        for (int x = 1; x < m; x++)
            if ((a * x) % m == 1) return x;  // Перебором проверяем выполнение
        return 1;
    }

    private static boolean TestRabMill(int number, int mod) {
        if (mod <= 4) return false;
        int rNumber = 2 + (int) (Math.random() % (mod - 4)); // Выбираем случайное число в отрезке
        int modNumber = Stepen(rNumber, number, mod); // Возводим число в степень по модулю
        if (modNumber == 1 || modNumber == mod - 1) return true;

        while (number != mod - 1) {
            modNumber = (modNumber * modNumber) % mod;
            number = number * 2;
            if (modNumber == 1) return false;
            if (modNumber == mod - 1) return true;
        }
        return false;
    }

    public static int Stepen(int x, int y, int p) {
        // Возведение в степень по модулю
        int res = 1;

        x = x % p;

        if (x == 0)
            return 0;

        while (y > 0) {

            if ((y & 1) != 0)
                res = (res * x) % p;

            y = y >> 1;
            x = (x * x) % p;
        }
        return res;
    }

    public static boolean isPrime(int n, int k) {
        //Проверка на простое число
        if (n <= 1 || n == 4) return false;
        if (n <= 3) return true; // если n == 2 или n == 3 - эти числа простые, возвращаем true
        int d = n - 1;
        while (d % 2 == 0) // Последовательное деление n-1 на 2 (Представляем n-1 в виде 2^s * t, где t - нечетное)
            d = d / 2;
        for (int i = 0; i < k; i++) //Тест Миллера – Рабина с k итерациями
            if (!TestRabMill(d, n)) return false;
        return true;
    }

    public static boolean Verify(String text, int OpenKey, int modul, String Ishod){
        boolean result = false;
        if (Enc(text, OpenKey, modul).equals(Ishod)) result = true;
        return result;
    }

//    public static String Sign(String Text, int CloseKey, int modul){ // Подпись
//        String res = "";
//        for (int i = 0; i < Text.length(); i ++){
//            char t = Text.charAt(i);
//            int m = (int)t;
//            int e = Stepen(m,CloseKey, modul);
//            char w = (char)Integer.parseInt(String.valueOf(e));
//            res += w;
//        }
//        return res;
//    }

    public static String Enc(String Text, int OpenKey, int modul){ // Шифрование RSA
        String module = Integer.toBinaryString(modul);
        Text = Coding(Text, module.length());
        String res = "";
        while(Text.length() > module.length()) { // Шифруем по блокам равным длине модуля
            int endIndex = module.length() - CompareLine(Text, module); // Обрезаем или нет блок с текстом
            res += (char)Stepen(Integer.parseInt(Text.substring(0, endIndex),2), OpenKey, modul);
            Text = Text.substring(endIndex);
        }
        res += (char)Stepen(Integer.parseInt(Text,2), OpenKey, modul);
        return res;
    }
    public static String textToBinary(String text, int ModLength) {
        String resultLine = "";
        for(int i = 0; i < text.length(); i ++)
        {
            resultLine += toDynamicBinaryString(text.charAt(i),ModLength);
        }
        return resultLine;
    }
    public static String binaryToText(String text, int ModLength) {
        String resultLine = "";
        String binary = "";
        for(int i = 0; i < text.length(); i += ModLength)
        {
            binary = "";
            for(int j = 0; j < ModLength && i+j < text.length(); j ++)binary = binary + text.charAt(i + j); // формируем двоичную запись
            resultLine += (char)Integer.parseInt(binary, 2); // Переводим в число, после в символ
        }
        return resultLine;
    }

    public static String Denc(String Text, int CloseKey, int modul){ // Дешифрование RSA
        String module = Integer.toBinaryString(modul);
        Text = textToBinary(Text, module.length());
        String res = "";
        while(Text.length() > module.length()) {
            int endIndex = module.length() - CompareLine(Text, module);
            res += toDynamicBinaryString(
                    Stepen(Integer.parseInt(Text.substring(0, endIndex),2), CloseKey, modul), module.length());

            Text = Text.substring(endIndex);
        }
        res += toDynamicBinaryString(
                Stepen(Integer.parseInt(Text,2), CloseKey, modul), module.length());

        return binaryToText(res, module.length());
    }

    public static String Coding(String text, int ModLength) { // Исходный текст, Длина модуля в двоичном виде
        // Перевод каждого символа в его код
        String result = "";
        for(int i = 0; i < text.length(); i ++)
        {
            result += toDynamicBinaryString(text.charAt(i), ModLength); // Обрабатываем символы согласно кодировке
        }
        return result;
    }
    public static String toDynamicBinaryString(int symbol, int ModLength) {
        // Добавляем незначащие нули, если не хватает на цельный блок
        return String.format("%"+ ModLength +"s", Integer.toBinaryString(symbol)).replace(' ','0');
    }
    public static int CompareLine(String text, String mod) {
        // Функция сравнения блока с текстом, превышает ли он длину модуля
        for(int i = 0; i < mod.length(); i ++) // Пробегаемся по длине модуля
        {
            if(mod.charAt(i) < text.charAt(i)) return 1;
            else if(mod.charAt(i) > text.charAt(i))return 0;
        }
        return 1;
    }


    public static int generationLargeNumber() {
        // Генерация числа
        int min = 1100, max = 4500, k = 5;
        int i = min + (int) (Math.random() * max);
        for (; i <= max; i++) {
            if (isPrime(i, k)) return i;
        }
        for (; i >= min; i--) {
            if (isPrime(i, k)) return i;
        }
        return -1;
    }

    private class ReadServer extends Thread {
        @Override
        public void run() {
            try {
                int NKey = rnd(7700, 9500); // С какого числа начнется генерация
                for (int i = 0; i < CKey; ) {
                    if ((gcd(NKey, FI) == 1) && (NKey < FI)) // Проверка на Взаимно простое с φ, открытый ключ < φ (возможнен случай незаконечнной генерации)
                    {
                        OpenBobKey = NKey; // Открытый ключ Трента
                        System.out.println("\nОткрытый ключ Боба: " + OpenBobKey);
                        CloseBobKey = modInverse(NKey, FI) % FI; // Закрытый ключ Трента
                        System.out.println("\nЗакрытый ключ Боба: " + CloseBobKey);
                        i++;
                    }
                    NKey++;
                }
//                String Ishod = "а б № ! % ? @ # s s";
//                String test = Sign(Ishod, 15679, mod); // 9019 открытый ключ
//                boolean testt = Verify("厚誽㏪誽㘆誽烊誽㯷誽餷誽υ誽に誽䀠誽䀠", 9019, mod, Ishod);
//                System.out.println("\n SIGN " + test + "\n Verify " + testt);
                long currentTime = System.currentTimeMillis() / 1000L; // Текущее время для последущей проверки метки
                try {
                    socket1 = new Socket("127.0.0.1", port1);
                    DEBUG_AREA.appendText("\nПодключаемся к тренту по порту: " + port1);
                    InputStream sin1 = socket1.getInputStream();
                    DataInputStream in1 = new DataInputStream(sin1);
                    OutputStream sout1 = socket1.getOutputStream();
                    DataOutputStream out1 = new DataOutputStream(sout1);
                    OpenTrentKey = in1.readInt(); // Считали открытый ключ Трента
                    System.out.println("\nПолучили открытый ключ Трента: " + OpenTrentKey);
                    out1.writeInt(OpenBobKey); // Отправили открытый ключ Боба
                    out1.flush();
                } catch (IOException e) {
                    DEBUG_AREA.appendText("Исключение Bob 111");
                }
                socket = ServSock.accept(); // Ожидание подключения
                DEBUG_AREA.appendText("\nКлиет подключен.");
                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);
                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
                out.writeUTF(in_username); // Отправляем имя боба алисе
                out.flush(); // Поток передает данные
                ex_username = in.readUTF(); // Считываем имя алисы
                DEBUG_AREA.appendText("\nПолучили имя Алисы: " + ex_username);
                String Ishod = in.readUTF(); // Получили от Алисы
                System.out.println("\n Ключ + Метка " +Ishod);
                String Ishod1 = in.readUTF(); // Получили от Алисы
                System.out.println("\n Ключ + Метка подписанные " +Ishod1);
                PodAliceBob = in.readUTF(); // Получили от Алисы
                System.out.println("\n Ключ + Метка подписанные+шифро " +PodAliceBob);
                BobPodT = in.readUTF(); // Получили от Алисы
                AlicePodT = in.readUTF(); // Получили от Алисы
                K = in.readInt();
                marktime = in.readInt();
                OpenAliceKey = in.readInt();
                PodAliceBob = Denc(PodAliceBob, CloseBobKey, mod); // Расшифровываем сообщение Алисы
                System.out.println("\nРасшифровали сообщение Алисы " + PodAliceBob);
                boolean IsCorrect = Verify(PodAliceBob, OpenAliceKey, mod, Ishod); // Подписанное сообщение алисой
                System.out.println("\n1 " +IsCorrect);
                boolean IsCorrect1 = Verify(BobPodT, OpenTrentKey, mod, String.join(",", in_username, Integer.toString(OpenBobKey))); // Открытый ключ боба подписанный трентом
                System.out.println("\n2 " +IsCorrect1);
                boolean IsCorrect2 = Verify(AlicePodT, OpenTrentKey, mod, String.join(",", ex_username, Integer.toString(OpenAliceKey))); // Открытый ключ алисы подписанный трентом
                System.out.println("\n3 " +IsCorrect2);
                String Check = String.join(",", Integer.toString(K), Integer.toString((int)marktime));
                boolean IsCorrect3 = (Check.equals(Ishod)); // Проверили что метка времени верная
                //boolean IsCorrect3 = Verify((String.join(",", Integer.toString(K), Integer.toString((int)marktime))), OpenAliceKey, mod, Ishod1); // Проверили что метка времени верная
                System.out.println("\n4 " +IsCorrect3);
                if ( (IsCorrect && IsCorrect1 && IsCorrect2) == true){
                    DEBUG_AREA.appendText("\nВсе полученные подписи верны!");
                    if(IsCorrect3 == true){
                        DEBUG_AREA.appendText("\nМетка времени верна!");
                        long TimeDiff = marktime - currentTime;
                        DEBUG_AREA.appendText("\nРазница во времени " + TimeDiff);
                        long Times = 300; // ограничение времени на проверку устаревания
                        if (TimeDiff <= Times){
                            DEBUG_AREA.appendText("\nМетка времени не просрочена!");
                            String line = null; //Создаем пустую строку
                            DEBUG_AREA.appendText("\nПолучили сеансовый ключ К " + K);
                            while (true) {
                                line = in.readUTF(); // Ожидаем текст от пользователя
                                line = Decryption(line, K); // Дешифруем сообщение
                                CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                                DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                            }
                        }
                        else {
                            K = rnd(15800, 60000);
                            DEBUG_AREA.appendText("\nМетка времени просрочена!!!");
                            String line = null; //Создаем пустую строку
                            DEBUG_AREA.appendText("\nИзменили сеансовый ключ К для безопасноти " + K);
                            while (true) {
                                line = in.readUTF(); // Ожидаем текст от пользователя
                                line = Decryption(line, K); // Дешифруем сообщение
                                CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                                DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                            }
                        }
                    }
                }
                else{
                    DEBUG_AREA.appendText("\nНе все полученные подписи совпадают!");
                    K = rnd(15800, 60000);
                    String line = null; //Создаем пустую строку
                    DEBUG_AREA.appendText("\nИзменили сеансовый ключ К для безопасноти " + K);
                    while (true) {
                        line = in.readUTF(); // Ожидаем текст от пользователя
                        line = Decryption(line, K); // Дешифруем сообщение
                        CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                        DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                    }
                }
            } catch (IOException e) {
                DEBUG_AREA.appendText("Исключение Bob");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class ReadClient extends Thread {
        @Override
        public void run() {
            try {
                int NKey = rnd(4800, 7700); // С какого числа начнется генерация
                for (int i = 0; i < CKey; ) {
                    if ((gcd(NKey, FI) == 1) && (NKey < FI)) // Проверка на Взаимно простое с φ, открытый ключ < φ (возможнен случай незаконечнной генерации)
                    {
                        OpenAliceKey = NKey; // Открытый ключ Трента
                        System.out.println("\nОткрытый ключ Алисы: " + OpenAliceKey);
                        CloseAliceKey = modInverse(NKey, FI) % FI; // Закрытый ключ Трента
                        System.out.println("\nЗакрытый ключ Алисы: " + CloseAliceKey);
                        i++;
                    }
                    NKey++;
                }
                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);
                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
                ex_username = in.readUTF(); // Считываем имя боба
                out.writeUTF(in_username); // Отправляем имя алисы бобу
                out.flush(); // Поток передает данные
                DEBUG_AREA.appendText("\nПолучили имя Боба: " + ex_username);
                try {
                    socket2 = new Socket("127.0.0.1", port2);
                    DEBUG_AREA.appendText("\nПодключаемся к тренту по порту: " + port2);
                    InputStream sin2 = socket2.getInputStream();
                    DataInputStream in2 = new DataInputStream(sin2);
                    OutputStream sout2 = socket2.getOutputStream();
                    DataOutputStream out2 = new DataOutputStream(sout2);
                    OpenTrentKey = in2.readInt(); // Получаем открытый ключ трента
                    System.out.println("\nПолучили открытый ключ Трента: " + OpenTrentKey);
                    out2.writeInt(OpenAliceKey);
                    out2.writeUTF(in_username); // Отсылаем имя Алисы
                    out2.writeUTF(ex_username); // Отсылаем имя Боба
                    out2.flush();
                    BobPodT = in2.readUTF(); // Подписанное имя + открытый ключ Боба
                    AlicePodT = in2.readUTF(); // Подписанное имя + открытый ключ Алисы
                    OpenBobKey = in2.readInt(); // Открытый ключ Боба
                    boolean IsCorrect = Verify(BobPodT, OpenTrentKey, mod, String.join(",", ex_username, Integer.toString(OpenBobKey))); // Проверяем подпись на верность (боба)
                    boolean IsCorrect1 = Verify(AlicePodT, OpenTrentKey, mod, String.join(",", in_username, Integer.toString(OpenAliceKey))); // Проверяем подпись на верность (алисы)
                    if ((IsCorrect && IsCorrect1) == true){
                        DEBUG_AREA.appendText("\nПолученные подписи совпадают!");
                        // Генерируем Случайный ключ K - целое число.
                        K = rnd(1, 15000);
                        DEBUG_AREA.appendText("\nСлучайный ключ сген. Алисой: " + K);
                        long unixTime = System.currentTimeMillis() / 1000L; // Вычисляем текущее время
                        DEBUG_AREA.appendText("\nМетка времени " + unixTime);
                        PodAliceBob = String.join(",", Integer.toString(K), Integer.toString((int)unixTime)); // Для подписи
                        out.writeUTF(PodAliceBob); // Для проверки
                        System.out.println("\nКлюч + Метка " + PodAliceBob);
                        PodAliceBob = Enc(PodAliceBob, CloseAliceKey, mod); // Подписали закрытым ключом Алисы случ. сеансовый ключ К + Метку времени
                        out.writeUTF(PodAliceBob); // Для проверки
                        System.out.println("\nКлюч + Метка подписанные " + PodAliceBob);
                        PodAliceBob = Enc(PodAliceBob, OpenBobKey, mod);
                        System.out.println("\nКлюч + Метка подписанные+шифро " + PodAliceBob);
                        out.writeUTF(PodAliceBob); // Подписанный закрытым ключом Алисы (К + Метка времени), зашифрованный открытым ключом боба
                        out.writeUTF(BobPodT); // Подписанный откр. ключ боба Трентом
                        out.writeUTF(AlicePodT); // Подписанный откр. ключ алисы Трентом
                        out.writeInt(K); // случ. сеанс. ключ
                        out.writeInt((int)unixTime); // Метка
                        out.writeInt(OpenAliceKey); // Открытый ключ для проверки
                        out.flush();
                        String line = null; // Создаем пустую строку
                        while (true) {
                            line = in.readUTF(); // Ожидаем текст от пользователя.
                            line = Decryption(line, K); // Дешифруем сообщение
                            CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                            DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                        }
                    }
                    else {
                        DEBUG_AREA.appendText("\nОдна или несколько подписей не совпадают!");
                    }
                } catch (IOException e) {
                    DEBUG_AREA.appendText("Исключение Alice 111");
                }
            } catch (IOException e) {
                DEBUG_AREA.appendText("Исключение Alice");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}