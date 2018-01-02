package org.freecore.fcrplayer2.components;

import org.freecore.fcrplayer2.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class MemoryMonitor extends JPanel implements Runnable {
    private Runtime runtime = Runtime.getRuntime();
    private Rectangle graphOutlineRect = new Rectangle();
    private Rectangle2D mfRect = new Rectangle2D.Float();
    private Rectangle2D muRect = new Rectangle2D.Float();
    private Line2D graphLine = new Line2D.Float();
    private Color graphColor = new Color(46, 139, 87);
    private Color mfColor = new Color(0, 100, 0);

    private Thread thread;
    private long updateInterval;
    private int width, height, columnInc, pts[], ptNum;
    private int ascent, descent; // Подъем шрифта, спуск шрифта.
    private BufferedImage bufferedImage;
    private Graphics2D graphics2D;
    private Font mainFont = GuiUtils.getFont("fonts/Hack-Regular.ttf", Font.PLAIN, 11);
    private Dimension size;

    @SuppressWarnings("WeakerAccess")
    public MemoryMonitor(int width, int height, long updateInterval) {
        this.width = width;
        this.height = height;
        this.updateInterval = updateInterval;
        size = new Dimension(width, height);

        super.setBackground(Color.BLACK);

        // По клику мыши на компоненте, он перестаёт обновлятся. Повторный клик для возобновления.
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thread == null) {
                    start();
                } else {
                    stop();
                }
            }
        });
    }

    @Override
    public Dimension getMinimumSize() {
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        return size;
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public void paint(Graphics graphics) {
        if (GuiUtils.isWindowIconified() && !this.isVisible()) return;
        if (graphics2D == null) return;

        graphics2D.setRenderingHint // Сглаживание текста
                (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics2D.setRenderingHint // Сглаживание графики
                (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setBackground(getBackground());
        graphics2D.clearRect(0, 0, width, height);
        float freeMemory = runtime.freeMemory();
        float totalMemory = runtime.totalMemory();
        //int kb = 1024; // Килобайты
        int mb = 1024 * 1024; // Магабайты
        float used = (totalMemory - freeMemory) / 8 / mb;
        float total = totalMemory / 8 / mb;

        String usedStr = String.valueOf(round(used, 4)) + "MB used";
        String totalStr = String.valueOf((int) total) + "MB allocated";

        // Рисуем строки
        graphics2D.setColor(Color.GREEN);
        graphics2D.drawString(totalStr, 4.0f, ascent + 0.5f);
        graphics2D.drawString(usedStr, 4, height - descent);

        // Вычисляем оставшийся размер
        float ascentSum = ascent + descent; // Сумма высот шрифтов.
        float remainingHeight = (height - (ascentSum * 2) - 0.5f);
        float blockHeight = remainingHeight / 10;
        float blockWidth = 20.0f;

        // Рисуем свободную память в столбце.
        graphics2D.setColor(mfColor);
        int MemUsage = (int) ((freeMemory / totalMemory) * 10);
        int i = 0;
        for (; i < MemUsage; i++) {
            mfRect.setRect(5, ascentSum + i * blockHeight, blockWidth, blockHeight - 1);
            graphics2D.fill(mfRect);
        }

        // Рисуем в этом же столбце занятую память.
        graphics2D.setColor(Color.GREEN);
        for (; i < 10; i++) {
            muRect.setRect(5, ascentSum + i * blockHeight, blockWidth, blockHeight - 1);
            graphics2D.fill(muRect);
        }

        // Рисуем сетку графика(та самая решетка на фоне графика :D)
        graphics2D.setColor(graphColor);
        int graphX = 30;
        int graphY = (int) ascentSum;
        int graphW = width - graphX - 5;
        int graphH = (int) remainingHeight;
        graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
        graphics2D.draw(graphOutlineRect);

        int graphRow = graphH / 10;

        // Рисуем линию графика.
        for (int j = graphY; j <= graphH + graphY; j += graphRow) {
            graphLine.setLine(graphX, j, graphX + graphW, j);
            graphics2D.draw(graphLine);
        }

        // Теперь рисуем анимацию движения сетки графика.
        int graphColumn = graphW / 15;
        if (columnInc == 0) columnInc = graphColumn;
        for (int j = graphX + columnInc; j < graphW + graphX; j += graphColumn) {
            graphLine.setLine(j, graphY, j, graphY + graphH);
            graphics2D.draw(graphLine);
        }
        --columnInc;

        if (pts == null) {
            pts = new int[graphW];
            ptNum = 0;
        } else if (pts.length != graphW) {
            int tmp[];
            if (ptNum < graphW) {
                tmp = new int[ptNum];
                System.arraycopy(pts, 0, tmp, 0, tmp.length);
            } else {
                tmp = new int[graphW];
                System.arraycopy(pts, pts.length - tmp.length, tmp, 0, tmp.length);
                ptNum = tmp.length - 2;
            }
            pts = new int[graphW];
            System.arraycopy(tmp, 0, pts, 0, tmp.length);
        } else {
            // Вот тут уже рисуем наш график.
            graphics2D.setColor(Color.YELLOW);
            pts[ptNum] = (int) (graphY + graphH * (freeMemory / totalMemory));
            for (int j = graphX + graphW - ptNum, k = 0; k < ptNum; k++, j++) {
                if (k != 0) {
                    if (pts[k] != pts[k - 1]) graphics2D.drawLine(j - 1, pts[k - 1], j, pts[k]);
                    else graphics2D.fillRect(j, pts[k], 1, 1);
                }
            }
            if (ptNum + 2 == pts.length) {
                // throw out oldest point
                System.arraycopy(pts, 1, pts, 0, ptNum - 1);
                --ptNum;
            } else {
                ptNum++;
            }
        }
        graphics.drawImage(bufferedImage, 0, 0, this);

    }

    @SuppressWarnings("WeakerAccess")
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("RuntimeMemoryMonitor");
        thread.start();
    }

    @SuppressWarnings("WeakerAccess")
    public synchronized void stop() {
        thread = null;
        super.notify();
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        // Вообще, можно заменить на if, но while выглядит куда безопасней.
        while (thread == currentThread && !super.isShowing() || super.getSize().width == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }

        while (thread == currentThread && super.isShowing()) {
            bufferedImage = (BufferedImage) createImage(width, height);
            graphics2D = bufferedImage.createGraphics();
            graphics2D.setFont(mainFont);
            FontMetrics fontMetrics = graphics2D.getFontMetrics(mainFont);
            ascent = fontMetrics.getAscent();
            descent = fontMetrics.getDescent();
            super.repaint();
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }

    // Очень быстрый метод сокращения(округления) чисел с плавающей точкой. Найден на просторах cyberforum'а.
    @SuppressWarnings("SameParameterValue")
    private float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }
}
