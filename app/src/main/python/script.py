def main(number1:str, number2:str):
    if number1=="":
        number1 = 0
    number2 = 0 if number2=="" else number2

    sum = int(number1) + int(number2)
    return f"Sum of {number1} and {number2} is {sum}"