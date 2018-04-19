import matplotlib.pyplot as plt

def loop(x):
    t = 0.01
    t_max = 10
    x_values = list(range(int(t_max/t +1)))
    y_values = list(range(400))
    t_start = 0
    while t_start < t_max:
        x_neu = x + t*(x-x**3)
        x = x_neu
        print(x-x**3)
        print("Vergangene Zeit", t_start)
        t_start = t_start + t
        x_values.append(t_start)
        y_values.append(x_neu)
    plt.plot(x_values, y_values)
    plt.ylabel('y')
    plt.show()

if __name__ == '__main__':
    loop(-7)
    print("--------------------------------")
    loop(-0.2)
    print("--------------------------------")
    loop(8)
    print("--------------------------------")
