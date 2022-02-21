package com.example.apps;

import java.io.*;
import java.net.*;
import javafx.fxml.FXML;
import java.net.ServerSocket;
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


    public static int Shifr_C1 (int ex_G, int R, int ex_P){
        int res = Stepen(ex_G,R,ex_P);
        return res;
    }
    public static int Shifr_C2 (int M, int ex_H, int R, int ex_P){
        int result = (M * Stepen(ex_H, R, ex_P)) % ex_P;
        return result;
    }
    public int Deshifr_C (int C1, int C2, int P, int A){
        int result = (C2 * modInverse(Stepen(C1,A,P),P)) % P;
        return result;
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

    private class ReadServer extends Thread {
        @Override
        public void run() {
            try {
                socket1 = new Socket("127.0.0.1", port1);
                DEBUG_AREA.appendText("\nПодключаемся к тренту по порту: " +port1);
                P = generationLargeNumber(); // Генерация простого большого числа
                DEBUG_AREA.appendText("\nПростое большое число Боба P: " + P);
                G = GetRoot(P); // Первообразный корень
                DEBUG_AREA.appendText("\nПервообразный корень Боба G: " + G);
                A = (int)(Math.random()*(((P-2)-1)+1))+1; // закрытый ключ
                DEBUG_AREA.appendText("\nЗакрытый ключ Боба A: " + A);
                H = Stepen(G,A,P); // Возведение в степень по модулю
                DEBUG_AREA.appendText("\nh = g^a mod p  Боба = " + H);
                // Генерируем r - целое число из [1;p-1]
                R = (int)(Math.random()*(((P-1)-1)+1))+1;
                DEBUG_AREA.appendText("\nГенерируем r Боба: " + R);
                OutputStream sout1 = socket1.getOutputStream();
                DataOutputStream out1 = new DataOutputStream(sout1);
                out1.writeInt(P); // Передаем 3 компоненты открытого ключа Боба Тренту
                out1.writeInt(G);
                out1.writeInt(H);
                out1.flush(); // Поток передает данные
                DEBUG_AREA.appendText("\nОткрытый ключ боба: " + "[" + P + ";" + G + ";" + H + "]");
                socket = ServSock.accept(); // Ожидание подключения
                DEBUG_AREA.appendText("\nКлиет подключен.");

                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);

                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
                out.writeUTF(in_username); // Отправляем имя боба алисе
                out.flush(); // Поток передает данные
                ex_username = in.readUTF(); // Считываем имя алисы
                ex_C1 = in.readInt(); // Принимаем первую часть шифр.сеанс.ключа
                ex_C2 = in.readInt(); // Принимаем вторую часть шифр.сеанс.ключа
                DEBUG_AREA.appendText("=========================================");
                DEBUG_AREA.appendText("\nПолучили от Алисы зашифрованный ключ K: " + "[" + "C1:" + ex_C1 + ";"+ "C2:" + ex_C2 + "]"); // Выводим в дебаг
                ex_K = Deshifr_C(ex_C1, ex_C2, P, A); // Дешифруем ключ
                DEBUG_AREA.appendText("\nРасшифрованный ключ K: " + ex_K); // Выводим в дебаг
                K = ex_K;

                String line = null; //Создаем пустую строку
                while (true) {
                    line = in.readUTF(); // Ожидаем текст от пользователя
                    line = Decryption(line,K); // Дешифруем сообщение
                    CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                    DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                }
            } catch (IOException e) {
                DEBUG_AREA.appendText("Исключение Bob");
            }
        }
    }



    private class ReadClient extends Thread {
        @Override
        public void run() {
            try {
                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);

                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
                try {
                    socket2 = new Socket("127.0.0.1", port2);
                    DEBUG_AREA.appendText("\nПодключаемся к тренту по порту: " +port2);

                    InputStream sin2 = socket2.getInputStream();
                    DataInputStream in2 = new DataInputStream(sin2);

                    ex_P = in2.readInt(); // Ждем пока Трент пришлет 3 компоненты открытого ключа Боба
                    ex_G = in2.readInt();
                    ex_H = in2.readInt();
                }
                catch (IOException e){
                    DEBUG_AREA.appendText("Исключение Alice 111");
                }
                ex_username = in.readUTF(); // Считываем имя боба
                out.writeUTF(in_username); // Отправляем имя алисы бобу
                out.flush(); // Поток передает данные
                DEBUG_AREA.appendText("\nПришел открытый ключ от Боба: [" + ex_P + ";" + ex_G + ";" + ex_H + "]");
                DEBUG_AREA.appendText("=========================================");
                // Генерируем Случайный ключ K - целое число из [1;p-1]
                K = (int)(Math.random()*(((ex_P-1)-1)+1))+1;
                DEBUG_AREA.appendText("\nСлучайный ключ сген. Алисой: " + K);
                C1 = Shifr_C1(ex_G, K, ex_P); // Шифруем сеансовый ключ методом гамаля
                C2 = Shifr_C2(K, ex_H, K, ex_P); // Шифруем сеансовый ключ методом гамаля
                DEBUG_AREA.appendText("\nЗашифрованный ключ Алисы: [" + C1 + ";" + C2 + "]"); // Вывод зашифрованного сеансового ключа

                //Протокол одновременной передачи ключей и сообщений
                out.writeInt(C1); // Отправляем первую часть
                out.writeInt(C2); // Отправляем вторую часть
                String line = null; // Создаем пустую строку
                while (true) {
                    line = in.readUTF(); // Ожидаем текст от пользователя.
                    line = Decryption(line,K); // Дешифруем сообщение
                    CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
                    DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
                }
            } catch (IOException e) {
                DEBUG_AREA.appendText("Исключение Alice");
            }
        }
    }
}