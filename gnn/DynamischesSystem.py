import matplotlib.pyplot as plt

def loop(x):
    t = 0.01
    t_max = 10
    x_values = []
    y_values = []

    t_start = 0
    while t_start < t_max:
        x_neu = x + t*(x-(x**3))
        x = x_neu
        print(x_neu)
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
