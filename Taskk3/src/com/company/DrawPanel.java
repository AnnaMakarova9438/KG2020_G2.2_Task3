package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    private ArrayList<Line> lines = new ArrayList<>();
    private ScreenConverter sc = new ScreenConverter(-2, 2, 4, 4, 750, 750);
    private ArrayList<IFigure> allFiguresList = new ArrayList<>();
    private IFigure editFigure = null;
    private IFigure currentFigure = null;
    private int numOfSides = 0;

    DrawPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(this);
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_BGR);
        sc.setScreenW(getWidth());
        sc.setScreenH(getHeight());
        Graphics gr = bi.createGraphics();
        gr.setColor(Color.WHITE);
        gr.fillRect(0, 0, getWidth(), getHeight());
        PixelDrawer pd = new BufferedImagePixelDrawer(bi);
        LineDrawer ld = new DDALineDrawer(pd);
        PolygonDrawer dp = new DrawPolygon(ld, sc);
        if (currentFigure != null) {
            drawPolygon(currentFigure, dp);
        }
        for (IFigure p : allFiguresList) {
            drawPolygon(p, dp);
        }
        if (editFigure != null) {
            drawMarkers((Graphics2D) gr);
        }
        gr.dispose();
        g.drawImage(bi, 0, 0, null);
    }

    private void drawLine(LineDrawer ld, Line l) {
        ld.drawLine(sc.r2s(l.getP1()), sc.r2s(l.getP2()), Color.BLACK);
    }

    private void drawPolygon(IFigure p, PolygonDrawer pd) {
        pd.drawPolygon(p, Color.BLACK);
    }

    private ScreenPoint prevDrag;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            dialogWindow();
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            for (IFigure p : allFiguresList) {
                if (p.checkIfClicked(sc.s2r(new ScreenPoint(e.getX(), e.getY())))) {
                    editFigure = p;
                }
            }
        }
    }

    private Line currentLine = null;
    private boolean transfer = false;
    private boolean scale = false;

    @Override
    public void mousePressed(MouseEvent e) {//действия при зажатии кнопок мыши
        if (numOfSides != 0) {
            if (editFigure == null) {//если фигуры нет
                if (e.getButton() == MouseEvent.BUTTON1) {//левой кнопки мыши рисовка новой фигуры
                    currentLine = new Line(sc.s2r(new ScreenPoint(e.getX(), e.getY())), sc.s2r(new ScreenPoint(e.getX(), e.getY())));
                    currentFigure = new Polygon(currentLine.getP1(), 0, numOfSides);
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    prevDrag = new ScreenPoint(e.getX(), e.getY());
                }
            } else {
                prevDrag = new ScreenPoint(e.getX(), e.getY());
                if (e.getButton() == MouseEvent.BUTTON3) {//правой кнопки мыши перемещение фигуры
                    if (clickToTranslationMarker(prevDrag, editFigure)) {//если включены маркеры
                        transfer = true;
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {//левой кнопки мыши увеличение/уменьшение размера фигуры
                    if (clickToScaleMarkers(prevDrag, editFigure)) {//если включены маркеры
                        scale = true;
                    }
                }
            }
        }
        repaint();
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (numOfSides != 0) {
            if (editFigure != null) {
                editFigure = null;
                scale = false;
                transfer = false;
            } else {
               if (e.getButton() == MouseEvent.BUTTON1) {
                    lines.add(currentLine);
                    if (currentFigure != null) {
                        allFiguresList.add(currentFigure);
                    }
                    currentLine = null;
                    currentFigure = null;
                }
            }
            prevDrag = null;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (numOfSides != 0) {
            ScreenPoint current = new ScreenPoint(e.getX(), e.getY());
            if (editFigure == null) {
                if (prevDrag != null) {
                    ScreenPoint delta = new ScreenPoint(current.getX() - prevDrag.getX(), current.getY() - prevDrag.getY());
                    RealPoint deltaReal = sc.s2r(delta);
                    RealPoint zeroReal = sc.s2r(new ScreenPoint(0, 0));
                    RealPoint vector = new RealPoint(deltaReal.getX() - zeroReal.getX(), deltaReal.getY() - zeroReal.getY());
                    sc.setX(sc.getX() - vector.getX());//конечные координаты точки
                    sc.setY(sc.getY() - vector.getY());
                    prevDrag = current;
                }
                if (currentLine != null) {
                    currentLine.setP2(sc.s2r(current));
                    currentFigure.setRadius(countRealRadius(sc.s2r(current), currentFigure));
                }
            } else {
                if (scale) {
                    editFigure.setRadius(countRealRadius(sc.s2r(current), editFigure));
                }
                if (transfer) {
                    editFigure.transfer(sc.s2r(current));
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        double scale = 1;
        double coef = clicks > 0 ? 0.9 : 1.1;
        for (int i = 0; i < Math.abs(clicks); i++) {
            scale *= coef;
        }
        sc.setW(sc.getW() * scale);
        sc.setH(sc.getH() * scale);
        repaint();
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {//удаление фигур клавишей Delete

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private boolean clickToScaleMarkers(ScreenPoint click, IFigure f) {
        List<RealPoint> markers = f.getMarkers();
        for (RealPoint mark : markers) {
            if ((sc.r2s(mark).getX() - getWidth()) < click.getX() && (sc.r2s(mark).getX() + getWidth() > click.getX()
                    && sc.r2s(mark).getY() - getHeight() < click.getY() && sc.r2s(mark).getY() + getHeight() > click.getY())) {
                return true;
            }
        }
        return false;
    }

    private boolean clickToTranslationMarker(ScreenPoint click, IFigure f) {    //центр - перемещение
        return (sc.r2s(f.getCenter()).getX() - getWidth()) < click.getX() && (sc.r2s(f.getCenter()).getX() + getWidth() > click.getX()
                && sc.r2s(f.getCenter()).getY() - getHeight() < click.getY() && sc.r2s(f.getCenter()).getY() + getHeight() > click.getY());
    }

    private double countRealRadius(RealPoint rp, IFigure f) {
        return Math.sqrt(Math.pow((rp.getX() - f.getCenter().getX()), 2) + Math.pow((rp.getY() - f.getCenter().getY()), 2));
    }

    private void drawMarkers(Graphics2D gr2) {//рисовка маркеров
        gr2.setColor(Color.red);
        gr2.fillRect(sc.r2s(editFigure.getCenter()).getX() - getWidth() / (40 * 2), sc.r2s(editFigure.getCenter()).getY() - getHeight() / (40 * 2), getWidth() / 40, getHeight() / 40);
        gr2.setColor(Color.black);
        gr2.drawRect(sc.r2s(editFigure.getCenter()).getX() - getWidth() / (40 * 2), sc.r2s(editFigure.getCenter()).getY() - getHeight() / (40 * 2), getWidth() / 40, getHeight() / 40);
    }


    private void dialogWindow() {//всплывающее окно(возвращает введенное кол-во сторон)
        boolean isNotCorrectNumOfSides = true;
        do {
            String result = JOptionPane.showInputDialog(
                    DrawPanel.this,
                    "Введите количество сторон");
            try {
                numOfSides = Integer.parseInt(result);
                if (numOfSides > 2) {
                    isNotCorrectNumOfSides = false;
                } else {
                    isNotCorrectNumOfSides = true;
                    JOptionPane.showMessageDialog(DrawPanel.this, "Введите число больше 2!");
                }
            } catch (NumberFormatException exception) {
                if (result == null) {
                    isNotCorrectNumOfSides = false;
                    if (numOfSides < 2) {
                        numOfSides = 0;
                    }
                } else {
                    JOptionPane.showMessageDialog(DrawPanel.this, "Некоректный ввод!");
                }
            }
        } while (isNotCorrectNumOfSides);
    }
}