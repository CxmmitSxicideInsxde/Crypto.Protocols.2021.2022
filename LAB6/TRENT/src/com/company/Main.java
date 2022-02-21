package com.company;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.security.*;

public class Main {
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
        else if (a >= b) return gcd((a-b) >> 1, b);

            // А и B нечётные, A < B
        else return gcd(a, (b-a) >> 1);
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
        long i = min + (int) (Math.random() * max);
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

        return (long)(Math.random() * ++max) + min;
    }
    private static long  P = generationLargeNumber(), Q = generationLargeNumber(), // Держится в секрете
            mod = P*Q; // знают все
    public static void main(String[] args) throws Exception {
        int port1 = 3501; // Порт
        int port2 = 3502; // Порт
        int G = 0, s = 0, H = 0, OpenKeyTrent = 0, CloseKeyTrent = 0, CKey = 1;
        long V = 0;
//        BigInteger one = BigInteger.valueOf(1);
//        int OpenKeyB = 0, OpenKeyA =0;
//        // String nameAlice = "", nameBob = "";
//        for (int j = 0; j < 20; j++) {
//            long ss = randm(1, mod - 1);
//            while (gcd((int)ss, mod) != 1) {
//                ss = randm(1, mod - 1);
//            }
//            s = gcd((int)ss, mod);
//            //s = gcd(generationNumber(), mod);
//            System.out.println("\nP = " + P + " \nQ = " + Q + " \nmod = " + mod + " \nss = " + ss + " \ns = " + s);
//            long V = Stepen((int)ss, 2, mod);
//            System.out.println("\nV = " + V);
//        }
        try {
            ServerSocket ServSock = new ServerSocket(port1);
            System.out.println("\nОжидаем подключение Алисы");
            Socket socket = ServSock.accept(); // Ждем подключения
            System.out.println("\nПодключились к Алисе");
            OutputStream sout1 = socket.getOutputStream();
            DataOutputStream out1 = new DataOutputStream(sout1);
            InputStream sin1 = socket.getInputStream();
            DataInputStream in1 = new DataInputStream(sin1);
            out1.writeInt((int)mod);
            out1.flush();
            V = in1.readInt();
            System.out.println("\nОтправили Алисе mod " + mod);
            System.out.println("\nПолучили открытый ключ (V) Алисы: " + V);
            try {
                ServerSocket ServSock1 = new ServerSocket(port2);
                System.out.println("\nОжидаем подключение Боба");
                Socket socket2 = ServSock1.accept(); // Ждем подключения
                InputStream sin2 = socket2.getInputStream();
                DataInputStream in2 = new DataInputStream(sin2);
                OutputStream sout2 = socket2.getOutputStream();
                DataOutputStream out2 = new DataOutputStream(sout2);
                out2.writeInt((int)mod);
                out2.writeInt((int)V);
                out2.flush();
                System.out.println("\nОтправили Бобу mod " + mod);
                System.out.println("\nОткрытый ключ (V) Алисы Бобу: " + V);
            }
            catch (Exception x){
                x.printStackTrace();
            }
            System.out.println("Закрытие потока после отправки!");
            socket.close();
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
}
