import socket
import time

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#Привязка сокета к порту
serv_addr = ('127.0.0.1',5365)
print('Сервер запущена на  {} и порту {}'.format(*serv_addr))
sock.bind(serv_addr)
#Ожидаем подключения клиента
sock.listen(1)

while True:
    connection, client_address = sock.accept()
    print('Подключен к:', client_address)
    while True:
        message = connection.recv(1024)
        print(f'Получили сообщение от клиента: {message.decode()}')
        if message:
            print('Отправляем сообщение обратно клиенту.')
            connection.sendall(message)
        else:
            print('Нет сообщения от клиента:', client_address)
            connection.close()
            print('Закрыли соединение')
            print('Закрытие консоли через 1 секунду')
            time.sleep(1)
            exit()
        