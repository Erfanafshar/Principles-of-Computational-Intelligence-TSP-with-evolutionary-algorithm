import java.awt.*;
import java.awt.event.*;

public class Draw extends Frame {

    private int[] x;
    private int[] y;
    private int num;

    Draw(double[][] cities, int[] sequence, int number) {
        super("Tsp");
        setSize(1500, 1000);
        setLocation(200, 0);
        setVisible(true);

        num = number;
        x = new int[number + 1];
        y = new int[number + 1];

        for (int i = 0; i < number; i++) {
            x[i] = ((int) cities[sequence[i]][1] - 24700) - (int) (((cities[sequence[i]][1] - 24700) - 750) / 2.0);
            y[i] = (-1 * (int) cities[sequence[i]][2]) + 51700;
        }

        x[number] = ((int) cities[sequence[0]][1] - 24700) - (int) (((cities[sequence[0]][1] - 24700) - 750) / 2.0);
        y[number] = (-1 * (int) cities[sequence[0]][2]) + 51700;

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  dispose();
                                  System.exit(0);
                              }
                          }
        );
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.red);
        g2d.drawPolyline(x, y, num + 1);

    }
}

