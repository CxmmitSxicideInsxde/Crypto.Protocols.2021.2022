import unittest
import SecondLab

class TestExponRing(unittest.TestCase):
    # Тестирование возведение целого числа в степень в кольце вычетов
    def test_expon_ring(self):
        self.assertEqual(9, SecondLab.exponRing(3, 2, 10))
        self.assertEqual(2, SecondLab.exponRing(2, 3, 6))
        self.assertEqual(1, SecondLab.exponRing(3, 2, 8))
    
    # Тестирование вычисление наибольшего общего делителя двух целых чисел
    def test_max_com_div(self):
        self.assertEqual(50, SecondLab.MaxComDiv(150, 250))
        self.assertEqual(1, SecondLab.MaxComDiv(70000, 13241))
        self.assertEqual(700, SecondLab.MaxComDiv(700,1400))
    
    # Тестирование вычисление обратного значения в кольце вычетов
    def test_Ret_Elem_Ring(self):
        self.assertEqual(20, SecondLab.RetElemRing(15, 23))
        self.assertEqual(7, SecondLab.RetElemRing(18, 25))
        self.assertEqual(11, SecondLab.RetElemRing(6,13))
    
    def test_Simple_Num(self):
        self.assertEqual(True, SecondLab.SimpleNum(3, 5))
        self.assertEqual(True, SecondLab.SimpleNum(11, 3))
        self.assertEqual(False, SecondLab.SimpleNum(1, 12))

    # Тестирование генерация большого простого числа
    def test_Gen_Large_Num(self):
        print('Сгенерированное простое число:', SecondLab.GenLargeNum(1650, 1800, 15))
        print('Сгенерированное простое число:', SecondLab.GenLargeNum(1100, 5500, 21))
        print('Сгенерированное простое число:', SecondLab.GenLargeNum(3500, 8450, 10))
