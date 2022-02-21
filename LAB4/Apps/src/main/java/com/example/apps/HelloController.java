package com.example.apps;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javafx.fxml.FXML;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class HelloController {
    private ServerSocket ServSock = null;
    private int port1 = 3501;
    private int port2 = 3502;
    private Socket socket = null;
    private Socket socket1 = null;
    private Socket socket2 = null;
    private boolean IsConnect = false;
    private int P = 0, G =0, A = 0, H = 0, R = 0, K = 0;
    private int ex_P = 0, ex_G = 0, ex_H = 0, C1 = 0, C2 = 0, ex_C1 = 0, ex_C2 = 0, ex_K = 0;
    private int Ra = 0, Rb = 0;
    private String HashM = null;


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
            }
            catch (Exception x) {
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
                    line = Encryption(line,K);
                    out.writeUTF(line); // Отсылаем текст
                    TEXT_FIELD.setText(null); // Очищаем строку
                    out.flush(); // Поток передает данные
                } catch (IOException e) {
                    DEBUG_AREA.appendText("\nПроверьте подключение!");
                }
            }
        });
    }

    public static String Encryption (String Text, int Key){
        String encrypt = "";
        char m;
        for(int i = 0; i < Text.length(); i++){
            char t = Text.charAt(i);
            int n = t^Key;
            m = (char)Integer.parseInt(String.valueOf(n));
            encrypt += m;
        }
        return encrypt;
    }
    public static String Decryption (String Text, int Key){
        String decrypt = "";
        char m;
        for(int i = 0; i < Text.length(); i++){
            char t = Text.charAt(i);
            int n = t^Key;
            m = (char)Integer.parseInt(String.valueOf(n));
            decrypt += m;
        }
        return decrypt;
    }


//    public static int Shifr_C1 (int ex_G, int R, int ex_P){
//        int res = Stepen(ex_G,R,ex_P);
//        return res;
//    }
//    public static int Shifr_C2 (int M, int ex_H, int R, int ex_P){
//        int result = (M * Stepen(ex_H, R, ex_P)) % ex_P;
//        return result;
//    }
//    public int Deshifr_C (int C1, int C2, int P, int A){
//        int result = (C2 * modInverse(Stepen(C1,A,P),P)) % P;
//        return result;
//    }

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
    public static int modInverse(int a, int m){ // Обратный элемент по модулю
        a = a % m;
        for (int x = 1; x < m; x++)
            if ((a * x) % m == 1)return x;  // Перебором проверяем выполнение
        return 1;
    }
    private static boolean TestRabMill(int number, int mod){
        if(mod <= 4) return false;
        int rNumber = 2 + (int)(Math.random() % (mod - 4)); // Выбираем случайное число в отрезке
        int modNumber = Stepen(rNumber, number, mod); // Возводим число в степень по модулю
        if(modNumber == 1 || modNumber == mod - 1) return true;

        while (number != mod - 1){
            modNumber = (modNumber * modNumber) % mod;
            number = number * 2;
            if(modNumber == 1 ) return false;
            if(modNumber == mod - 1 ) return true;
        }
        return false;
    }
    public static int Stepen(int x, int y, int p) {
        // Возведение в степень по модулю
        int res = 1;

        x = x % p;

        if (x == 0)
            return 0;

        while (y > 0)
        {

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
            d = d/2;
        for (int i = 0; i < k; i++) //Тест Миллера – Рабина с k итерациями
            if (!TestRabMill(d, n)) return false;
        return true;
    }
    public static int generationLargeNumber(){
        // Генерация числа
        int min = 1100, max = 4500, k = 5;
        int i = min + (int)(Math.random() * max);
        for( ; i <= max; i ++) { if(isPrime(i, k)) return i; }
        for( ; i >= min; i --) { if(isPrime(i, k)) return i; }
        return -1;
    }


    public static String md5Custom(String st) { // ХЭШ-Функция, метод MD5
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

/*======================================================================================================================
        ==============================================================================================================*/








    private class ReadServer extends Thread {
        @Override
        public void run() {
            try {
                socket = ServSock.accept(); // Ожидание подключения
                DEBUG_AREA.appendText("\nКлиет подключен.");

                K = 8855; // Общий секретный ключ
                DEBUG_AREA.appendText("\nОбщий секретный ключ K: " + K);


                Rb = generationLargeNumber(); // Рандомное число Боба
                List l1 = new ArrayList();

                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);

                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
              int Ra1 = in.readInt(); // Получили рандомное число от Алисы
                l1.add(Ra1); // Рандомное число Алисы
                l1.add(Rb); // Рандомное число Боба
                l1.add(in_username); // Имя Боба
                l1.add(K); // Добавляем Наш Общий ключ K, для вычисления ХЭШ-Функции
                HashM = md5Custom(l1.toString()); // Высчитываем ХЭШ-Функцию Боба
                DEBUG_AREA.appendText("\nХэш-функция БОБА Hk(Ra, Rb, B) = " + HashM);
                out.writeInt(Rb); // Отсылаем рандомное число Боба
                out.writeUTF(HashM); // Отсылаем ХЭШ Боба
                out.writeUTF(in_username); // Отправляем имя боба алисе
                out.flush(); // Поток передает данные
                ex_username = in.readUTF(); // Считываем имя алисы
                String HashAlice = in.readUTF(); // Считываем ХЭШ Алисы
                DEBUG_AREA.appendText("\nХэш-функция Алисы Hk(Rb, A) = " + HashAlice);
                List l4 = new ArrayList();
                l4.add(Rb); // Рандомное число Боба
                l4.add(ex_username); // Имя Алисы
                l4.add(K); // Добавляем Наш Общий ключ K, для вычисления ХЭШ-Функции
                HashM = md5Custom(l4.toString()); // Высчитываем ХЭШ-Функцию Боба
                if (!HashM.equals(HashAlice)){ // Сравниваем ХЭШи и если они не совпадают пишем в чат об этом
                    CHAT_AREA.appendText("\nПришел Хэш от Алисы Hk(Rb, Имя Алисы): " + HashAlice + "\nНаш Хэш(Боба) Hk(Rb, Имя Алисы): " + HashM);
                    CHAT_AREA.appendText("\nПредупреждение!\nВозможно, вы соединились не с Алисой, т.к ХЭШи не совпадают!\nПроверьте ключ!");
                }
                if(HashM.equals(HashAlice)){ // Сравниваем ХЭШи и если они совпадают, пишем в чат об этом
                    CHAT_AREA.appendText("\nПришел Хэш от Алисы Hk(Rb, Имя Алисы): " + HashAlice + "\nНаш Хэш(Боба) Hk(Rb, Имя Алисы): " +HashM);
                    CHAT_AREA.appendText("\nХЭШи совпадают! Соединились с Алисой!");
                }
                String line = null; //Создаем пустую строку
                while (true) {
                    line = in.readUTF(); // Ожидаем текст от пользователя
                    line = Decryption(line,K); // Дешифруем сообщение
                    CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                    //DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                }
            } catch (IOException  e) {
                DEBUG_AREA.appendText("Исключение Bob");
            }
        }
    }

    private class ReadClient extends Thread {
        @Override
        public void run() {
            try {
                K = 8855; // Общий секретный ключ
                DEBUG_AREA.appendText("\nОбщий секретный ключ K: " + K);
                List l2 = new ArrayList();
                Ra = generationLargeNumber(); // Случайное число Ra Алисы
                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);

                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);

                out.writeInt(Ra); // Отправляем число Ra бобу
               int Rb1 = in.readInt(); // Получаем число Rb боба
                String HashBob = in.readUTF();
                DEBUG_AREA.appendText("\nПолучили Хэш Боба Hk(Ra, Rb, B) = " + HashBob);
                ex_username = in.readUTF(); // Считываем имя боба
                out.writeUTF(in_username); // Отправляем имя алисы бобу
                out.flush(); // Поток передает данные
                l2.add(Ra); //Рандомное число Алисы
                l2.add(Rb1); // Полученное рандомное число Боба
                l2.add(ex_username); // Имя Боба
                l2.add(K); // Добавляем Наш Общий ключ K, для вычисления ХЭШ-Функции
                HashM = md5Custom(l2.toString()); // Вычисляем Хэш
                DEBUG_AREA.appendText("\nХэш вычисленный Алисой Hk(Ra,Rb,Имя Боба):" + HashM);
                if (!HashM.equals(HashBob)){
                    CHAT_AREA.appendText("\nПришел Хэш от Боба Hk(Ra,Rb,Имя Боба): " + HashBob + "\nНаш Хэш(Алисы) Hk(Ra,Rb,Имя Боба): " + HashM);
                    CHAT_AREA.appendText("\nПредупреждение!\nВозможно, вы соединились не с Бобом, т.к ХЭШи не совпадают!\nПроверьте ключ!");
                    List l3 = new ArrayList();
                    l3.add(Rb1); // Полученное рандомное число Боба
                    l3.add(in_username); // Имя Алисы
                    l3.add(K); // Добавляем Наш Общий ключ K, для вычисления ХЭШ-Функции
                    HashM = md5Custom(l3.toString()); // Высчитываем ХЭШ-Функцию Алисы
                    DEBUG_AREA.appendText("\nХэш-функция Алисы Hk(Rb, A) = " + HashM);
                    out.writeUTF(HashM);
                    out.flush();
                }
                if (HashM.equals(HashBob)){
                    CHAT_AREA.appendText("\nПришел Хэш от Боба Hk(Ra,Rb,Имя Боба): " + HashBob + "\nНаш Хэш(Алисы) Hk(Ra,Rb,Имя Боба): " + HashM);
                    CHAT_AREA.appendText("\nХЭШи совпадают! Соединились с Бобом!");
                    List l3 = new ArrayList();
                    l3.add(Rb1); // Полученное рандомное число Боба
                    l3.add(in_username); // Имя Алисы
                    l3.add(K); // Добавляем Наш Общий ключ K, для вычисления ХЭШ-Функции
                    HashM = md5Custom(l3.toString()); // Высчитываем ХЭШ-Функцию Алисы
                    DEBUG_AREA.appendText("\nХэш-функция Алисы Hk(Rb, A) = " + HashM);
                    out.writeUTF(HashM);
                    out.flush();
                }
                    String line = null; // Создаем пустую строку
                    while (true) {
                        line = in.readUTF(); // Ожидаем текст от пользователя.
                        line = Decryption(line,K); // Дешифруем сообщение
                        CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                        // DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                    }
            } catch (IOException  e) {
                DEBUG_AREA.appendText("Исключение Alice");
            }
        }
    }
}