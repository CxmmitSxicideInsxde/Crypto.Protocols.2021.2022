package com.company;

import java.io.*;
import java.util.*;
import java.net.*;
import java.net.Socket;
import java.net.ServerSocket;


public class Main {
    private static ServerSocket ServSock = null;
    private static int port = 3500;
    private static int port1 = 3501;
    private static int port2 = 3502;
    private static Socket socket = null;
    private static Socket socket1 = null;
    private static Socket socket2 = null;

    public static long gcd(long a, long b) { // НОД двух чисел
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

    public static long Stepen(long x, long y, long p) {
        // Возведение в степень по модулю
        long res = 1;

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

    public static long GetRoot(long p) {
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

    private static boolean TestRabMill(long number, long mod) {
        if (mod <= 4) return false;
        long rNumber = 2 + (int) (Math.random() % (mod - 4)); // Выбираем случайное число в отрезке
        long modNumber = Stepen(rNumber, number, mod); // Возводим число в степень по модулю
        if (modNumber == 1 || modNumber == mod - 1) return true;

        while (number != mod - 1) {
            modNumber = (modNumber * modNumber) % mod;
            number = number * 2;
            if (modNumber == 1) return false;
            if (modNumber == mod - 1) return true;
        }
        return false;
    }

    public static boolean isPrime(long n, long k) {
        //Проверка на простое число
        if (n <= 1 || n == 4) return false;
        if (n <= 3) return true; // если n == 2 или n == 3 - эти числа простые, возвращаем true
        long d = n - 1;
        while (d % 2 == 0) // Последовательное деление n-1 на 2 (Представляем n-1 в виде 2^s * t, где t - нечетное)
            d = d / 2;
        for (int i = 0; i < k; i++) //Тест Миллера – Рабина с k итерациями
            if (!TestRabMill(d, n)) return false;
        return true;
    }

    public static long generationLargeNumber() {
        // Генерация числа
        long min = 200, max = 1000, k = 5;
        long i = min + (long) (Math.random() * max);
        for (; i <= max; i++) {
            if (isPrime(i, k)) return i;
        }
        for (; i >= min; i--) {
            if (isPrime(i, k)) return i;
        }
        return -1;
    }

    public static long randm(long min, long max) {
        max -= min;

        return (long) (Math.random() * ++max) + min;
    }

    private static long s = 0, v = 0, mod = 0;
    public static int i = 0, success = 0, fail = 0, K = 0;
    private static String nameAlice = "Alice", nameBob = "Bob";


    public static void main(String[] args) throws IOException {
        long y = 0, r = 0, x = 0, e = 0;
        while (i != 1 || i != 2) {
            System.out.println("MAIN MENU");
            System.out.println("\nДля начала выберите пункт 3!(Перед выбором Алисы)");
            System.out.println("\n1 - Alice, \n2 - Bob, \n3 - K-Количество раундов");
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nВведите 1, 2 или 3:\n");
            if (scanner.hasNextInt()) {
                i = scanner.nextInt();
            }
            switch (i) {
                case 1:
                    ServSock = new ServerSocket(port);
                    try {
                        try {
                            socket1 = new Socket("127.0.0.1", port1);
                            System.out.println("\nПодключаемся к тренту по порту: " + port1);
                            InputStream sin1 = socket1.getInputStream();
                            DataInputStream in1 = new DataInputStream(sin1);
                            OutputStream sout1 = socket1.getOutputStream();
                            DataOutputStream out1 = new DataOutputStream(sout1);
                            mod = in1.readInt(); // Считали открытый ключ Трента
                            System.out.println("\nПолучили mod от Трента: " + mod);
                            while (gcd(s, mod) != 1) {
                                s = randm(1, mod - 1); //Взаимно простое с mod, где s принадлежит [1, mod - 1]
                            }
                            v = Stepen(s, 2, mod); // Будет передаваться T в качестве открытого ключа Алисы
                            out1.writeInt((int) v); // Отправили v Алисы тренту
                            out1.flush();
                            System.out.println("\nОтправили Тренту v = " + v);
                            System.out.println("\nСгенерированное s Алисой = " + s); //Для проверки
                        } catch (IOException i) {
                            System.out.println("\nИсключение Alice Trent");
                        }
                        System.out.println("\nОжидаем подключение Боба");
                        socket = ServSock.accept(); // Ожидание подключения
                        System.out.println("\nБоб подключен.");
                        InputStream sin = socket.getInputStream();
                        DataInputStream in = new DataInputStream(sin);
                        OutputStream sout = socket.getOutputStream();
                        DataOutputStream out = new DataOutputStream(sout);
                        out.writeUTF(nameAlice); // Отправляем имя Алисы Бобу
                        out.writeInt(K);
                        out.flush(); // Поток передает данные
                        nameBob = in.readUTF(); // Считываем имя Боба
                        System.out.println("\nПолучили имя Боба " + nameBob);
                            for (int j = 0; j < K; j++) {
                                r = randm(1, mod - 1);
                                x = Stepen(r, 2, mod); // Вычисляем x и передаем Бобу как доказательство
                                out.writeInt((int) x);
                                e = in.readInt();
                                System.out.println("\nПолучили от Боба бит e ["+j+"]= " + e);
                                if (e == 0) {
                                    y = r;
                                    System.out.println("\ny = r["+j+"]= " + y);
                                    out.writeInt((int) y);
                                } else {
                                    y = r * s % mod;
                                    System.out.println("\ny = (r*s mod n )["+j+"]= " + y);
                                    out.writeInt((int) y);
                                }
                        }
                    } catch (IOException i) {
                        System.out.println("\nИсключение Alice");
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                    break;
//                try {
//                    socket1 = new Socket("127.0.0.1", port1);
//                    System.out.println("\nПодключаемся к тренту(доверенному центру) по порту: " + port1);
//                    InputStream sin1 = socket1.getInputStream();
//                    DataInputStream in1 = new DataInputStream(sin1);
//                    OutputStream sout1 = socket1.getOutputStream();
//                    DataOutputStream out1 = new DataOutputStream(sout1);
//                    mod = in1.readInt();
//                    System.out.println("\nПолучили mod от доверенного центра: " + mod);
//                    while (gcd(s, mod) != 1) {
//                        s = randm(1, mod - 1); //Взаимно простое с mod, где s принадлежит [1, mod -1]
//                    }
//                    v = Stepen(s, 2, mod); // Будет передаваться T в качестве открытого ключа Алисы
//                    out1.writeInt((int) v); // Отправили Тренту(доверенному центру)
//                    out1.flush();
//                    System.out.println("\nОтправили Тренту V " + v);
//                    try {
//                        System.out.println("\nПорт для подключения Боба: " + port);
//                        socket = ServSock.accept(); // Ожидание подключения
//                        InputStream sin = socket.getInputStream();
//                        DataInputStream in = new DataInputStream(sin);
//                        OutputStream sout = socket.getOutputStream();
//                        DataOutputStream out = new DataOutputStream(sout);
//                        nameBob = in.readUTF();
//                        out.writeUTF(nameAlice);
//                        out.flush();
//                        System.out.println("\nПолучили имя Боба " + nameBob);
//                        //            try {
//                        //                socket1 = new Socket("127.0.0.1", port1);
//                        //                System.out.println("\nПодключаемся к тренту(доверенному центру) по порту: " + port1);
//                        //                InputStream sin2 = socket1.getInputStream();
//                        //                DataInputStream in2 = new DataInputStream(sin2);
//                        //                OutputStream sout2 = socket1.getOutputStream();
//                        //                DataOutputStream out2 = new DataOutputStream(sout2);
//                        //                mod = in2.readInt();
//                        //                System.out.println("\nПолучили mod от доверенного центра: " + mod);
//                        //                while (gcd(s, mod) != 1) {
//                        //                    s = randm(1, mod - 1); //Взаимно простое с mod, где s принадлежит [1, mod -1]
//                        //                }
//                        //                v = Stepen(s,2,mod); // Будет передаваться T в качестве открытого ключа Алисы
//                        //                out2.writeInt((int)v); // Отправили Тренту(доверенному центру)
//                        //                out.flush();
//                        ////                        String line = null; // Создаем пустую строку
//                        ////                        while (true) {
//                        ////                            line = in.readUTF(); // Ожидаем текст от пользователя.
//                        ////                            line = Decryption(line, K); // Дешифруем сообщение
//                        ////                            CHAT_AREA.appendText("\n[" + ex_username + "]: " + line); // Выводим в чат
//                        ////                            DEBUG_AREA.appendText("\nЗашифрованное сообщение: +[" + ex_username + "]: " + Encryption(line, K)); // Выводим в чат
//                        ////                        }
//                        //            } catch (IOException e) {
//                        //                System.out.println("Исключение Алиса Трент");
//                        //            }
//                        r = randm(1, mod - 1); // Выбирает случайное r
//                        System.out.println("\nr = " + r);
//                        x = Stepen(r, 2, mod); // Будет отсылаться Бобу (доказательство)
//                        System.out.println("\nx = " + x);
//                    } catch (IOException e) {
//                        System.out.println("Исключение Алиса Трент");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //============================================================================
                case 2:
                    System.out.println("\n==========================================");
                    socket = new Socket("127.0.0.1", port);
                    try {
                        InputStream sin = socket.getInputStream();
                        DataInputStream in = new DataInputStream(sin);
                        OutputStream sout = socket.getOutputStream();
                        DataOutputStream out = new DataOutputStream(sout);
                        nameAlice = in.readUTF(); // Считываем имя Алисы
                        out.writeUTF(nameBob); // Отправляем имя Боба Алисе
                        out.flush(); // Поток передает данные
                        K = in.readInt(); // Количество Раундов
                        System.out.println("\nПолучили имя Алисы: " + nameAlice);
                        try {
                            socket2 = new Socket("127.0.0.1", port2);
                            System.out.println("\nПодключаемся к тренту по порту: " + port2);
                            InputStream sin2 = socket2.getInputStream();
                            DataInputStream in2 = new DataInputStream(sin2);
                            OutputStream sout2 = socket2.getOutputStream();
                            DataOutputStream out2 = new DataOutputStream(sout2);
                            mod = in2.readInt();
                            v = in2.readInt();
                            System.out.println("\nПолучили от Трента mod = " + mod + "\nv Алисы = " + v);
                        } catch (IOException i) {
                            System.out.println("\nИсключение Bob 111");
                        }
                            for (int j = 0; j < K; j++) {
                                x = in.readInt();
                                System.out.println("\nПолучили от Алисы x["+j+"]= " + x);
                                e = new Random().nextLong(0, 2); // 0 или 1 для отправки Алисе
                                System.out.println("\nбит e["+j+"]= " + e);
                                out.writeInt((int) e); // Отсылаем результат
                                y = in.readInt();
                                if (y == 0) {
                                    System.out.println("\nАлисе не удалось доказать знание s! ["+j+"]");
                                    fail += 1; // Счетчик чтобы понять сколько раз Алисе не удалось доказать знание s
                                } else if (e == 0) {
                                    long y2 = Stepen(y, 2, mod);
                                    System.out.println("\ny2["+j+"]= " + y2); // Должны совпасть => Совпали то Боб удоствоверяется в знании Алисы
                                    if (y2 == x) {
                                        System.out.println("\nПодтверждено!["+j+"]" + "\nx["+j+"]= " + x);
                                        success += 1; // Счетчик чтобы понять сколько подтверждений было
                                    }
                                } else {
                                    long y2 = Stepen(y, 2, mod);
                                    long xv = x * Stepen(v, e, mod) % mod;
                                    System.out.println("\ny2["+j+"]= " + y2 + " \nxv["+j+"]= " + xv); // Должны совпасть => Совпали то Боб удоствоверяется в знании Алисы
                                    if (y2 == xv) {
                                        System.out.println("\nПодтверждено! ["+j+"]" + "\nx["+j+"]= " + x);
                                        success += 1; // Счетчик чтобы понять сколько подтверждений было
                                    }
                                }
                            }
                        System.out.println("\nУспешно: " + success + " Неуспешно: " + fail);
                        if (success == K && fail < 1){
                            System.out.println("\nВсе " + K + " раундов прошли успешно!");
                        }
                    } catch (IOException i) {
                        System.out.println("\nИсключение Bob");
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\nВведите количество раундов(положительное целое число).\n");
                    Scanner scanner1 = new Scanner(System.in);
                    if (scanner1.hasNextInt()) {
                        K = scanner1.nextInt();
                    }
                    break;
            }
        }
    }
}

//                try {
//                    socket2 = new Socket("127.0.0.1", port2);
//                    System.out.println("\nПодключаемся к тренту(доверенному центру) по порту: " + port2);
//                    InputStream sin2 = socket2.getInputStream();
//                    DataInputStream in2 = new DataInputStream(sin2);
//                    OutputStream sout2 = socket2.getOutputStream();
//                    DataOutputStream out2 = new DataOutputStream(sout2);
//                    mod = in2.readInt();
//                    v = in2.readInt();
//                    System.out.println("\nПолучили mod от доверенного центра: " + mod);
//                    System.out.println("\nПолучили v Алисы от доверенного центра: " + v);
//                try {
//                    socket = new Socket("127.0.0.1", port);
//                    System.out.println("\nПодключились к Алисе!");
//                    InputStream sin = socket.getInputStream();
//                    DataInputStream in = new DataInputStream(sin);
//                    OutputStream sout = socket.getOutputStream();
//                    DataOutputStream out = new DataOutputStream(sout);
//                    out.writeUTF(nameBob); // Отправляем имя Боба Алисе
//                    out.flush(); // Поток передает данные
//                    nameAlice = in.readUTF(); // Считываем имя Алисы
//                    System.out.println("\nПолучили имя Алисы: " + nameAlice);
//                } catch (IOException e) {
//                    System.out.println("Исключение Боб Трент");
//                }
//                } catch (IOException e) {
//                    System.out.println("Исключение Боб");
//                }catch (Exception e) {
//                    e.printStackTrace();
//