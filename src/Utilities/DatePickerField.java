/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

/**
 *
 * @author User
 */

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DatePickerField extends JPanel {

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // ── Listener ──────────────────────────────────────────────────────────────

    public interface DateChangeListener {
        void onDateSelected(LocalDate date);
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private LocalDate         selectedDate = null;
    private DateTimeFormatter displayFmt   = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private String            placeholder  = "MM/DD/YYYY";

    private int   arc            = 12;
    private Color bgColor        = Color.WHITE;
    private Color borderNormal   = new Color(209, 213, 219);
    private Color borderFocus    = new Color(37,  99,  235);
    private Color textColor      = new Color(17,  24,  39);
    private Color placeholderClr = new Color(156, 163, 175);

    private boolean open    = false;
    private boolean hovered = false;

    private YearMonth viewMonth     = YearMonth.now();
    private boolean   showMonthYear = false;

    private JPopupMenu         popupMenu;
    private JPanel             calContentPanel;
    private ComponentListener  windowMoveListener;
    private DateChangeListener dateListener;

    // ── Constructor ───────────────────────────────────────────────────────────

    public DatePickerField() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, 40));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { toggle(); }
        });
    }

    // ── Popup control ─────────────────────────────────────────────────────────

    private void toggle() {
        if (open) hideCalendar();
        else      showCalendar();
    }

    private void showCalendar() {
        open = true;
        repaint();
        if (selectedDate != null) viewMonth = YearMonth.from(selectedDate);

        final int W = Math.max(getWidth(), getPreferredSize().width);

        popupMenu = new JPopupMenu();
        popupMenu.setBackground(bgColor);
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));

        calContentPanel = new JPanel(new BorderLayout(0, 0));
        calContentPanel.setBackground(bgColor);
        calContentPanel.setPreferredSize(new Dimension(W, 300));
        refreshCalContent();
        popupMenu.add(calContentPanel);

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                detachWindowListener(); open = false; repaint();
            }
            @Override public void popupMenuCanceled(PopupMenuEvent e) {
                detachWindowListener(); open = false; repaint();
            }
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
        });

        attachWindowListener();
        popupMenu.show(this, 0, getHeight() + 3);
    }

    private void hideCalendar() {
        detachWindowListener();
        if (popupMenu != null) { popupMenu.setVisible(false); popupMenu = null; }
        open = false;
        repaint();
    }

    // ── In-place month navigation ─────────────────────────────────────────────

    private void navigateTo(YearMonth month) {
        viewMonth = month;
        refreshCalContent();
    }

    private void refreshCalContent() {
        if (calContentPanel == null) return;
        calContentPanel.removeAll();
        calContentPanel.add(buildHeader(),                                   BorderLayout.NORTH);
        calContentPanel.add(showMonthYear ? buildMonthGrid() : buildGrid(), BorderLayout.CENTER);
        calContentPanel.add(buildFooter(),                                  BorderLayout.SOUTH);
        calContentPanel.revalidate();
        calContentPanel.repaint();
    }

    // ── Window move listener (closes popup on parent drag) ────────────────────

    private void attachWindowListener() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner == null) return;
        windowMoveListener = new ComponentAdapter() {
            @Override public void componentMoved(ComponentEvent e)   { hideCalendar(); }
            @Override public void componentResized(ComponentEvent e) { hideCalendar(); }
        };
        owner.addComponentListener(windowMoveListener);
    }

    private void detachWindowListener() {
        if (windowMoveListener == null) return;
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner != null) owner.removeComponentListener(windowMoveListener);
        windowMoveListener = null;
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        return showMonthYear ? buildMonthYearHeader() : buildCalendarHeader();
    }

    private JPanel buildBaseHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(borderFocus);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 48));
        return header;
    }

    private JPanel buildCalendarHeader() {
        JPanel header = buildBaseHeader();

        JLabel prevBtn = buildNavBtn("\u2039");
        prevBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigateTo(viewMonth.minusMonths(1)); }
        });

        JLabel nextBtn = buildNavBtn("\u203A");
        nextBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigateTo(viewMonth.plusMonths(1)); }
        });

        String titleText = viewMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                           + " " + viewMonth.getYear() + " \u25BE";
        JLabel title = new JLabel(titleText, SwingConstants.CENTER) {
            boolean th = false;
            {
                setOpaque(false);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { th = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { th = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        showMonthYear = true;
                        refreshCalContent();
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                if (th) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        header.add(prevBtn, BorderLayout.WEST);
        header.add(title,   BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMonthYearHeader() {
        JPanel header = buildBaseHeader();

        JLabel prevBtn = buildNavBtn("\u2039");
        prevBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigateTo(viewMonth.minusYears(1)); }
        });

        JLabel nextBtn = buildNavBtn("\u203A");
        nextBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigateTo(viewMonth.plusYears(1)); }
        });

        JLabel yearLbl = new JLabel(String.valueOf(viewMonth.getYear()), SwingConstants.CENTER) {
            boolean th = false;
            {
                setOpaque(false);
                setFont(new Font("Segoe UI", Font.BOLD, 15));
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { th = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { th = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        showMonthYear = false;
                        refreshCalContent();
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                if (th) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        header.add(prevBtn, BorderLayout.WEST);
        header.add(yearLbl, BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);
        return header;
    }

    private JLabel buildNavBtn(String arrow) {
        return new JLabel(arrow, SwingConstants.CENTER) {
            boolean nh = false;
            {
                setOpaque(false);
                setPreferredSize(new Dimension(44, 48));
                setFont(new Font("Segoe UI", Font.PLAIN, 26));
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { nh = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { nh = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                if (nh) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(5, 8, getWidth() - 10, getHeight() - 16, 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
    }

    // ── Day grid ──────────────────────────────────────────────────────────────

    private JPanel buildGrid() {
        JPanel grid = new JPanel(new GridLayout(7, 7, 2, 2));
        grid.setBackground(bgColor);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 6, 4, 6));

        for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(new Color(107, 114, 128));
            grid.add(lbl);
        }

        LocalDate first    = viewMonth.atDay(1);
        int       startCol = first.getDayOfWeek().getValue() % 7;
        LocalDate today    = LocalDate.now();

        for (int i = 0; i < 42; i++) {
            LocalDate day   = first.minusDays(startCol).plusDays(i);
            boolean inMonth = YearMonth.from(day).equals(viewMonth);
            boolean isToday = day.equals(today);
            boolean isSel   = day.equals(selectedDate);
            grid.add(buildDayCell(day, inMonth, isToday, isSel));
        }
        return grid;
    }

    private JPanel buildDayCell(LocalDate day, boolean inMonth, boolean isToday, boolean isSel) {
        JPanel cell = new JPanel(new GridBagLayout()) {
            boolean ch = false;
            {
                setOpaque(false);
                setCursor(inMonth ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                  : Cursor.getDefaultCursor());
                if (inMonth) {
                    addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) { ch = true;  repaint(); }
                        @Override public void mouseExited (MouseEvent e) { ch = false; repaint(); }
                        @Override public void mouseClicked(MouseEvent e) {
                            selectedDate = day;
                            if (dateListener != null) dateListener.onDateSelected(day);
                            hideCalendar();
                            DatePickerField.this.repaint();
                        }
                    });
                }
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int sz = Math.min(getWidth(), getHeight()) - 6;
                int ox = (getWidth()  - sz) / 2;
                int oy = (getHeight() - sz) / 2;
                if (isSel) {
                    g2.setColor(borderFocus);
                    g2.fillOval(ox, oy, sz, sz);
                } else if (ch && inMonth) {
                    g2.setColor(new Color(243, 244, 246));
                    g2.fillOval(ox, oy, sz, sz);
                } else if (isToday) {
                    g2.setColor(new Color(219, 234, 254));
                    g2.fillOval(ox, oy, sz, sz);
                }
                if (isToday && !isSel) {
                    g2.setColor(borderFocus);
                    g2.fillOval(getWidth() / 2 - 2, oy + sz - 5, 4, 4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        JLabel num = new JLabel(String.valueOf(day.getDayOfMonth()), SwingConstants.CENTER);
        num.setFont(new Font("Segoe UI", isSel ? Font.BOLD : Font.PLAIN, 13));
        num.setForeground(
            isSel   ? Color.WHITE :
            isToday ? borderFocus :
            inMonth ? textColor   :
            new Color(209, 213, 219)
        );
        cell.add(num);
        return cell;
    }

    // ── Month grid (month/year picker) ──────────────────────────────────────

    private JPanel buildMonthGrid() {
        final String[] MONTHS = {"Jan","Feb","Mar","Apr","May","Jun",
                                  "Jul","Aug","Sep","Oct","Nov","Dec"};
        JPanel grid = new JPanel(new GridLayout(3, 4, 8, 8));
        grid.setBackground(bgColor);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 12, 8, 12));

        YearMonth nowYM = YearMonth.now();
        for (int m = 1; m <= 12; m++) {
            final YearMonth ym  = YearMonth.of(viewMonth.getYear(), m);
            boolean isSel       = selectedDate != null && YearMonth.from(selectedDate).equals(ym);
            boolean isNow       = ym.equals(nowYM);
            grid.add(buildMonthCell(MONTHS[m - 1], ym, isSel, isNow));
        }
        return grid;
    }

    private JPanel buildMonthCell(String label, YearMonth ym, boolean isSel, boolean isNow) {
        JPanel cell = new JPanel(new GridBagLayout()) {
            boolean ch = false;
            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { ch = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { ch = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        viewMonth     = ym;
                        showMonthYear = false;
                        refreshCalContent();
                    }
                });
            }
            @Override public Dimension getPreferredSize() { return new Dimension(0, 42); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int rw = getWidth() - 4, rh = getHeight() - 4;
                if (isSel) {
                    g2.setColor(borderFocus);
                    g2.fillRoundRect(2, 2, rw, rh, 8, 8);
                } else if (isNow) {
                    g2.setColor(new Color(219, 234, 254));
                    g2.fillRoundRect(2, 2, rw, rh, 8, 8);
                    g2.setColor(borderFocus);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(2, 2, rw - 1, rh - 1, 8, 8);
                    g2.setStroke(new BasicStroke(1f));
                } else if (ch) {
                    g2.setColor(new Color(243, 244, 246));
                    g2.fillRoundRect(2, 2, rw, rh, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", isSel ? Font.BOLD : Font.PLAIN, 13));
        lbl.setForeground(isSel ? Color.WHITE : isNow ? borderFocus : textColor);
        cell.add(lbl);
        return cell;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        footer.setBackground(bgColor);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(243, 244, 246)));

        JLabel todayBtn = new JLabel("Today") {
            boolean fh = false;
            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(borderFocus);
                setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { fh = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { fh = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        selectedDate = LocalDate.now();
                        if (dateListener != null) dateListener.onDateSelected(selectedDate);
                        hideCalendar();
                        DatePickerField.this.repaint();
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                if (fh) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(239, 246, 255));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        footer.add(todayBtn);
        return footer;
    }

    // ── Paint (field itself) ──────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 5 * i));
            g2.fill(new RoundRectangle2D.Float(i / 2f, i, w - i, h - i + 1f, arc, arc));
        }

        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        Color bdr = open ? borderFocus : borderNormal;
        float sw  = open ? 2f : 1.5f;
        if (open) {
            g2.setColor(new Color(borderFocus.getRed(), borderFocus.getGreen(), borderFocus.getBlue(), 28));
            g2.setStroke(new BasicStroke(sw + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));
        }
        g2.setColor(bdr);
        g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Float(sw / 2f, sw / 2f, w - sw, h - sw, arc, arc));

        String display = selectedDate != null ? selectedDate.format(displayFmt) : placeholder;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(selectedDate != null ? textColor : placeholderClr);
        FontMetrics fm = g2.getFontMetrics();
        int textAreaW = w - 14 - 40;
        g2.setClip(14, 0, Math.max(0, textAreaW), h);
        g2.drawString(display, 14, (h + fm.getAscent() - fm.getDescent()) / 2);
        g2.setClip(null);

        drawCalIcon(g2, w - 34, (h - 18) / 2, 18);
        g2.dispose();
    }

    private void drawCalIcon(Graphics2D g2, int x, int y, int sz) {
        g2.setColor(open || hovered ? borderFocus : placeholderClr);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawRoundRect(x, y + 3, sz, sz - 3, 3, 3);
        g2.drawLine(x + 1, y + 7, x + sz - 1, y + 7);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 4,      y, x + 4,      y + 5);
        g2.drawLine(x + sz - 4, y, x + sz - 4, y + 5);
        g2.setStroke(new BasicStroke(1.4f));
        int dotY = y + 11;
        int step = sz / 3;
        for (int col = 0; col < 3; col++) {
            g2.fillOval(x + 3 + col * step, dotY, 3, 3);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public LocalDate getDate()                              { return selectedDate; }
    public void      setDate(LocalDate d)                  { this.selectedDate = d; if (d != null) viewMonth = YearMonth.from(d); repaint(); }
    public String    getFormattedDate()                    { return selectedDate != null ? selectedDate.format(displayFmt) : ""; }
    public void      setDateChangeListener(DateChangeListener l) { this.dateListener = l; }

    // ── JavaBeans Properties ──────────────────────────────────────────────────

    public int    getArc()                                  { return arc; }
    public void   setArc(int v)                            { this.arc = v; repaint(); }
    public Color  getBgColor()                             { return bgColor; }
    public void   setBgColor(Color c)                      { this.bgColor = c; repaint(); }
    public Color  getBorderNormal()                        { return borderNormal; }
    public void   setBorderNormal(Color c)                 { this.borderNormal = c; repaint(); }
    public Color  getBorderFocus()                         { return borderFocus; }
    public void   setBorderFocus(Color c)                  { this.borderFocus = c; repaint(); }
    public Color  getTextColor()                           { return textColor; }
    public void   setTextColor(Color c)                    { this.textColor = c; repaint(); }
    public Color  getPlaceholderColor()                    { return placeholderClr; }
    public void   setPlaceholderColor(Color c)             { this.placeholderClr = c; repaint(); }
    public String getPlaceholder()                         { return placeholder; }
    public void   setPlaceholder(String s)                 { this.placeholder = s; repaint(); }
}
