package com.company;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.security.*;

public class Main {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

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
        else if (a >= b) return gcd((a-b) >> 1, b);

            // А и B нечётные, A < B
        else return gcd(a, (b-a) >> 1);
    }
    public static int modInverse(int a, int m){ // Обратный элемент по модулю
        a = a % m;
        for (int x = 1; x < m; x++)
            if ((a * x) % m == 1)return x;  // Перебором проверяем выполнение
        return 1;
    }
    //Метод получения псевдослучайного целого числа от min до max (включая max);
    public static int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
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



    public static void main(String[] args) throws Exception {
        int port1 = 3501; // Порт для боба
        int port2 = 3502; // Порт для Алисы
        int P = 0, G = 0, H = 0, OpenKeyTrent = 0, CloseKeyTrent = 0, CKey = 1;
        int OpenKeyB = 0, OpenKeyA =0;
        String nameAlice = "", nameBob = "";
        int FI = 150 * 282; // Функция Эейлера
        int mod = 151 * 283; // Модуль (P*Q)
        int NKey= rnd(1, 4800); // С какого числа начнется генерация
        for(int i = 0; i < CKey;)
        {
            if((gcd(NKey, FI) == 1) && (NKey < FI)) // Проверка на Взаимно простое с φ, открытый ключ < φ (возможнен случай незаконечнной генерации)
            {
                OpenKeyTrent =  NKey; // Открытый ключ Трента
                System.out.println("\nОткрытый ключ Трента: " + OpenKeyTrent);
                CloseKeyTrent =  modInverse(NKey, FI) % FI; // Закрытый ключ Трента
                System.out.println("\nЗакрытый ключ Трента: " + CloseKeyTrent);
                i++;
            }
            NKey++;
        }
     try {
         ServerSocket ServSock = new ServerSocket(port1);
         System.out.println("\nОжидаем подключение Боба");
         Socket socket = ServSock.accept(); // Ждем подключения
         System.out.println("\nПодключились к бобу\n");
         OutputStream sout1 = socket.getOutputStream();
         DataOutputStream out1 = new DataOutputStream(sout1);
         InputStream sin1 = socket.getInputStream();
         DataInputStream in1 = new DataInputStream(sin1);
         out1.writeInt(OpenKeyTrent);
         out1.flush();
         OpenKeyB = in1.readInt();
         System.out.println("\nПолучили открытый ключ Боба: " + OpenKeyB);
         try {
             ServerSocket ServSock1 = new ServerSocket(port2);
             System.out.println("\nОжидаем подключение Алисы");
             Socket socket2 = ServSock1.accept(); // Ждем подключения
             InputStream sin2 = socket2.getInputStream();
             DataInputStream in2 = new DataInputStream(sin2);
             OutputStream sout2 = socket2.getOutputStream();
             DataOutputStream out2 = new DataOutputStream(sout2);
             out2.writeInt(OpenKeyTrent);
             out2.flush();
             OpenKeyA = in2.readInt();
             System.out.println("\nПолучили открытый ключ Алисы: " + OpenKeyA);
             nameAlice = in2.readUTF();
             nameBob = in2.readUTF();
             System.out.println("\nПолучили имя Алисы " + nameAlice + " имя Боба " +nameBob);
             String BobPod = String.join(",", nameBob, Integer.toString(OpenKeyB)); // Для подпись (Имя боба и его открытый ключ)
             String AlicePod = String.join(",", nameAlice, Integer.toString(OpenKeyA)); // Для подпись (Имя алисы и её открытый ключ)
             BobPod = Enc(BobPod, CloseKeyTrent, mod); // Подписали закрытым ключом Трента
             AlicePod = Enc(AlicePod, CloseKeyTrent, mod); // Подписали закрытым ключом Трента
             out2.writeUTF(BobPod);  // Отправили Алисе
             out2.writeUTF(AlicePod); // Отправили Алисе
             out2.writeInt(OpenKeyB); // Отправили Алисе
             out2.flush();
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
