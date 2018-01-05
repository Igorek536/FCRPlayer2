package org.freecore.fcrplayer2.components;

import org.freecore.fcrplayer2.utils.GuiUtils;
import org.freecore.fcrplayer2.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * Heap memory monitor component.
 * Only for swing!
 * @dependencies: Utils, GuiUtils.
 * @author Igorek536, Oracle corp.
 * @version 1.0
 */

@SuppressWarnings("FieldCanBeLocal")
public class MemoryMonitor extends JPanel implements Runnable {

    // Colors

    private Color gridColor = new Color(65, 65, 65);
    private Color emptyBarColor = new Color(130, 130, 130);
    private Color fullBarColor = new Color(0, 0, 0);
    private Color fontColor = new Color(0, 0, 0);
    private Color lineColor = new Color(0, 0, 0);
    private Color backgroundColor = null;

    // Other

    private final Runtime runtime = Runtime.getRuntime();
    private final Rectangle graphOutlineRect = new Rectangle();
    private final Rectangle2D mfRect = new Rectangle2D.Float();
    private final Rectangle2D muRect = new Rectangle2D.Float();
    private final Line2D graphLine = new Line2D.Float();

    private Thread thread;
    private long updateInterval;
    private int width, height, columnInc, pts[], ptNum;
    private int ascent, descent;
    private BufferedImage bufferedImage;
    private Graphics2D graphics2D;
    private Font mainFont;
    private Dimension size;


    /**
     *
     * @param width component width
     * @param height component height
     * @param updateInterval update interval in ms
     * @param font font for text. Can be null!
     */
    public MemoryMonitor(int width, int height, long updateInterval, Font font) {
        this.width = width;
        this.height = height;
        this.updateInterval = updateInterval;
        this.mainFont = font;
        size = new Dimension(width, height);

        if (backgroundColor != null) super.setBackground(backgroundColor);

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

        graphics2D.setRenderingHint // Text antialiasing
                (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics2D.setRenderingHint // Graphics antialiasing
                (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setBackground(getBackground());
        graphics2D.clearRect(0, 0, width, height);
        float freeMemory = runtime.freeMemory();
        float totalMemory = runtime.totalMemory();
        int megaBytes = 1024 * 1024; // Megabytes
        float used = (totalMemory - freeMemory) / 8 / megaBytes;
        float total = totalMemory / 8 / megaBytes;

        String usedStr = String.valueOf(Utils.round(used, 4)) + "MB used";
        String totalStr = String.valueOf((int) total) + "MB allocated";

        // Draw text rows
        graphics2D.setColor(fontColor);
        graphics2D.drawString(totalStr, 4.0f, ascent + 0.5f);
        graphics2D.drawString(usedStr, 4, height - descent);

        // Calculating size
        float ascentSum = ascent + descent; // Сумма высот шрифтов.
        float remainingHeight = (height - (ascentSum * 2) - 0.5f);
        float blockHeight = remainingHeight / 10;
        float blockWidth = 20.0f;

        // Draw free memory bars
        graphics2D.setColor(emptyBarColor);
        int MemUsage = (int) ((freeMemory / totalMemory) * 10);
        int i = 0;
        for (; i < MemUsage; i++) {
            mfRect.setRect(5, ascentSum + i * blockHeight, blockWidth, blockHeight - 1);
            graphics2D.fill(mfRect);
        }

        // Draw busy memory bars
        graphics2D.setColor(fullBarColor);
        for (; i < 10; i++) {
            muRect.setRect(5, ascentSum + i * blockHeight, blockWidth, blockHeight - 1);
            graphics2D.fill(muRect);
        }

        // Draw graphics grid
        graphics2D.setColor(gridColor);
        int graphX = 30;
        int graphY = (int) ascentSum;
        int graphW = width - graphX - 5;
        int graphH = (int) remainingHeight;
        graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
        graphics2D.draw(graphOutlineRect);

        int graphRow = graphH / 10;

        // Draw line
        for (int j = graphY; j <= graphH + graphY; j += graphRow) {
            graphLine.setLine(graphX, j, graphX + graphW, j);
            graphics2D.draw(graphLine);
        }

        // Draw grid animation
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
            // Draw graphic
            graphics2D.setColor(lineColor);
            pts[ptNum] = (int) (graphY + graphH * (freeMemory / totalMemory));
            for (int j = graphX + graphW - ptNum, k = 0; k < ptNum; k++, j++) {
                if (k != 0) {
                    if (pts[k] != pts[k - 1]) {
                        graphics2D.drawLine(j - 1, pts[k - 1], j, pts[k]);
                    } else {
                        graphics2D.fillRect(j, pts[k], 1, 1);
                    }
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

    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("HeapMemoryMonitor");
        thread.start();
    }

    public synchronized void stop() {
        thread = null;
        super.notify();
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
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
}
