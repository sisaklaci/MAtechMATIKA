import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Közös színek, betűtípusok és egyszerű UI-segédek a MAtechMATIKA alkalmazáshoz. */
public final class Tema {
    private Tema() {}

    public static final Color HATTER = new Color(246, 247, 250);
    public static final Color PANEL_HATTER = Color.WHITE;
    public static final Color OLDALSAV_HATTER = new Color(30, 33, 48);
    public static final Color OLDALSAV_SZOVEG = new Color(230, 232, 240);
    public static final Color SZOVEG = new Color(35, 38, 48);
    public static final Color HALVANY_SZOVEG = new Color(110, 115, 130);
    public static final Color VONAL = new Color(222, 225, 232);

    public static final Color DERIVALAS = new Color(43, 108, 223);
    public static final Color INTEGRALAS = new Color(23, 156, 112);
    public static final Color VALOSZINUSEG = new Color(168, 85, 221);
    public static final Color STATISZTIKA = new Color(222, 128, 27);

    public static final Color RACS = new Color(230, 232, 238);
    public static final Color TENGELY = new Color(90, 95, 110);

    public static final Font CIM_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    public static final Font ALCIM_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font SZOVEG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font KEPLET_FONT = new Font("Consolas", Font.PLAIN, 16);
    public static final Font OLDALSAV_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    public static JLabel cim(String szoveg) {
        JLabel l = new JLabel(szoveg);
        l.setFont(CIM_FONT);
        l.setForeground(SZOVEG);
        return l;
    }

    public static JLabel alcim(String szoveg) {
        JLabel l = new JLabel(szoveg);
        l.setFont(ALCIM_FONT);
        l.setForeground(SZOVEG);
        return l;
    }

    public static JPanel kartya() {
        JPanel p = new JPanel();
        p.setBackground(PANEL_HATTER);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VONAL, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        return p;
    }

    public static JButton gomb(String szoveg, Color szin) {
        JButton b = new JButton(szoveg);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        b.setBackground(szin);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JTextField mezo(String kezdeti) {
        JTextField f = new JTextField(kezdeti);
        f.setFont(KEPLET_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VONAL, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }
}
