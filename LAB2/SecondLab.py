import random
# Возведение целого числа в степень в кольце
def exponRing(x, y, p):
    x = int(x)
    y = int(y)
    p = int(p)
    x = x % p # Изменить х если он больше или равен p
    result = int(1)
    if (x == 0): # Если х делится на p
        return 0
    while(y > 0):
        if ((y & 1) != 0):
            result = ((result * x) % p)
        y = (y >> 1) # y/2
        x = ((x * x) % p)
    return result

# Вычисление наибольшего общего делителя двух целых чисел (Алгоритм Евклида)
def MaxComDiv(p, q):
    p = int(p)
    q = int(q)
    if (q == 0):
        return p
    if (p == 0):
        return q
    
    # Если p и q четные
    if (((p & 1) == 0) and ((q & 1) == 0)):
        return MaxComDiv(p >> 1, q >> 1) << 1 

    # Если p четное, а q нечетное
    elif ((p & 1) == 0):
        return MaxComDiv(p >> 1, q)

    # Если p нечетное, а q четное
    elif ((q & 1) == 0):
        return MaxComDiv(p, q >> 1)

    # Если p и q нечетные, p >= q
    elif (p>=q):
        return MaxComDiv((p-q) >> 1, q)
    
    # Если p и q нечетные, p < q
    else:
        return MaxComDiv(p, (q-p) >> 1)

# Вычисление обратного значения в кольце вычетов (расширенный алгоритм Евклида)
def RetElemRing(a, m):
    a = int(a)
    m = int(m)
    x = int (1)
    a = a % m
    while (x < m):
        if ((a * x) % m == 1):
            return x
        x +=1
    return 1

# Алгоритм теста Рабина - Миллера
def TestRabMill(Num, Mod):
    Num = int (Num)
    Mod = int (Mod)
    if (Mod <= 4):
        return False
    randnum = int(2 + (random.random() % (Mod - 4)))
    modNum = int(exponRing(randnum, Num, Mod))
    if (modNum == 1 or modNum == Mod - 1):
        return True
    while (Num != (Mod - 1)):
        modNum = ((modNum * modNum) % Mod)
        Num = (Num * 2)
        if (modNum == 1):
            return False
        if (modNum == (Mod - 1)):
            return True
    return False

# Проверка на простое число
def SimpleNum(n, k):
    # Число
    n = int(n)

    # Количество итераций
    k = int(k)
    if ((n <= 1) or (n == 4)):
        return False
    if (n <= 3):
        return True
    d = int (n - 1)
    while (d % 2 == 0):
        d = d / 2
    i = int(0)
    while (i < k):
        if (not TestRabMill(d,n)):
            return False
        i += 1
        return True

# Генерация простого большого числа
def GenLargeNum(min, max, k):
    i = int(min + (random.random() * max))
    while (i <= max):
        if (SimpleNum(i,k)):
            return i
        i += 1
    while (i >= min):
        if (SimpleNum(i,k)):
            return i
        i -= 1
    return -1
