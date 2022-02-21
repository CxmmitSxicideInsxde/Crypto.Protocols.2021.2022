import socket

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#Подключаем сокет к порту, через который прослушивается сервер
serv_addr = ('127.0.0.1', 5365)
print('Подключились к серверу {} с портом {}'.format(*serv_addr))
sock.connect(serv_addr)

while True:
    message = input('Введите сообщение которое хотите отправить серверу\n')
    print(f'Отправили на сервер сообщение: {message}')
    messages = message.encode()
    #Отправляем наше сообщение
    sock.sendall(messages)

    len_received = 0
    len_messages = len(messages)
    while len_received < len_messages:
        data = sock.recv(1024)
        len_received += len(data)
        message1 = data.decode()
        print(f'Получили ответное сообщение от сервера: {message1}')