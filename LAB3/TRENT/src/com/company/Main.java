package com.company;

import java.io.*;
import java.util.*;
import java.net.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        int port1 = 3501; // Порт для боба
        int port2 = 3502; // Порт для Алисы
        int P = 0, G = 0, H = 0;
     try {
         ServerSocket ServSock = new ServerSocket(port1);
         System.out.println("\nОжидаем подключение Боба");
         Socket socket = ServSock.accept(); // Ждем подключения
         System.out.println("\nПодключились к бобу\n");
         InputStream sin1 = socket.getInputStream();
         DataInputStream in1 = new DataInputStream(sin1);
             P = in1.readInt(); // Получаем от боба открытый ключ
             G = in1.readInt();
             H = in1.readInt();
             System.out.println("\nПолучили открытый ключ: "+"P = " + P + " G = "+ G +" H = " + H);
         try {
             ServerSocket ServSock1 = new ServerSocket(port2);
             System.out.println("\nОжидаем подключение Алисы");
             Socket socket2 = ServSock1.accept(); // Ждем подключения
             OutputStream sout2 = socket2.getOutputStream();
             DataOutputStream out2 = new DataOutputStream(sout2);
                 out2.writeInt(P); // Передаем от трента открытый ключ боба
                 out2.writeInt(G);
                 out2.writeInt(H);
                 out2.flush();
                 System.out.println("Отправили Алисе открытый код Боба: "+"P = " + P + " G = "+ G +" H = " + H);
                 System.out.println("Закрытие потока после отправки!");
                 socket2.close();
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
