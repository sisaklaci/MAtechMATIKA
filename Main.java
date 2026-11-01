import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MAtechMATIKA - interaktív matematika-oktató alkalmazás.
 * Témakörök: deriválás, integrálás, valószínűségszámítás, statisztika.
 */
public class Main extends JFrame {

    private final CardLayout kartyak = new CardLayout();
    private final JPanel tartalom = new JPanel(kartyak);
    private JButton aktivGomb;

    private final DerivaltModul derivaltModul = new DerivaltModul();
    private final IntegralModul integralModul = new IntegralModul();
    private final ValoszinusegModul valoszinusegModul = new ValoszinusegModul();
    private final StatisztikaModul statisztikaModul = new StatisztikaModul();

    private final Map<String, JButton> navGombok = new LinkedHashMap<>();
    private final Map<Lang, JToggleButton> nyelvGombok = new EnumMap<>(Lang.class);
    private JLabel alcimLabel, nyelvCimke, labasLabel, homeCimLabel, homeLeirasLabel;
    private final JLabel[] kartyaCimLabel = new JLabel[4];
    private final JLabel[] kartyaLeirasLabel = new JLabel[4];
    private static final String[] KARTYA_CIM_KULCS = {
            "module.derivative.title", "module.integral.title", "module.probability.title_short", "module.statistics.title"};
    private static final String[] KARTYA_LEIRAS_KULCS = {
            "home.card.derivative.desc", "home.card.integral.desc", "home.card.probability.desc", "home.card.statistics.desc"};

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new Main().setVisible(true);
        });
    }

    public Main() {
        super(I18n.t("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.HATTER);

        tartalom.add(fooldalPanel(), "home");
        tartalom.add(derivaltModul, "derivalas");
        tartalom.add(integralModul, "integral");
        tartalom.add(valoszinusegModul, "valoszinuseg");
        tartalom.add(statisztikaModul, "statisztika");

        add(epitOldalsav(), BorderLayout.WEST);
        add(tartalom, BorderLayout.CENTER);

        setSize(1500, 920);
        setLocationRelativeTo(null);
    }

    private void valtas(String kulcs, JButton gomb) {
        kartyak.show(tartalom, kulcs);
        if (aktivGomb != null) aktivGomb.setBackground(Tema.OLDALSAV_HATTER);
        gomb.setBackground(new Color(50, 54, 74));
        aktivGomb = gomb;
    }

    private JComponent epitOldalsav() {
        JPanel sav = new JPanel();
        sav.setLayout(new BoxLayout(sav, BoxLayout.Y_AXIS));
        sav.setBackground(Tema.OLDALSAV_HATTER);
        sav.setPreferredSize(new Dimension(230, 10));
        sav.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel logo = new JLabel("∫ MAtechMATIKA");
        logo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sav.add(logo);
        alcimLabel = new JLabel(I18n.t("app.subtitle"));
        alcimLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        alcimLabel.setForeground(new Color(160, 165, 185));
        alcimLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        alcimLabel.setBorder(new EmptyBorder(2, 0, 24, 0));
        sav.add(alcimLabel);

        JButton home = oldalsavGomb(I18n.t("nav.home"), "home");
        JButton derivalas = oldalsavGomb(I18n.t("nav.derivative"), "derivalas");
        JButton integral = oldalsavGomb(I18n.t("nav.integral"), "integral");
        JButton valoszinuseg = oldalsavGomb(I18n.t("nav.probability"), "valoszinuseg");
        JButton statisztika = oldalsavGomb(I18n.t("nav.statistics"), "statisztika");
        navGombok.put("home", home);
        navGombok.put("derivalas", derivalas);
        navGombok.put("integral", integral);
        navGombok.put("valoszinuseg", valoszinuseg);
        navGombok.put("statisztika", statisztika);

        home.addActionListener(e -> valtas("home", home));
        derivalas.addActionListener(e -> valtas("derivalas", derivalas));
        integral.addActionListener(e -> valtas("integral", integral));
        valoszinuseg.addActionListener(e -> valtas("valoszinuseg", valoszinuseg));
        statisztika.addActionListener(e -> valtas("statisztika", statisztika));

        sav.add(home);
        sav.add(Box.createVerticalStrut(18));
        sav.add(derivalas);
        sav.add(Box.createVerticalStrut(8));
        sav.add(integral);
        sav.add(Box.createVerticalStrut(8));
        sav.add(valoszinuseg);
        sav.add(Box.createVerticalStrut(8));
        sav.add(statisztika);
        sav.add(Box.createVerticalGlue());

        sav.add(epitNyelvValaszto());
        sav.add(Box.createVerticalStrut(10));

        labasLabel = new JLabel(footerHtml());
        labasLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        labasLabel.setForeground(new Color(130, 135, 155));
        sav.add(labasLabel);

        valtas("home", home);
        return sav;
    }

    private String footerHtml() {
        return "<html><div style='width:180px'>" + I18n.t("nav.footer") + "</div></html>";
    }

    private JComponent epitNyelvValaszto() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        nyelvCimke = new JLabel(I18n.t("nav.lang_label"));
        nyelvCimke.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        nyelvCimke.setForeground(new Color(130, 135, 155));
        nyelvCimke.setAlignmentX(Component.LEFT_ALIGNMENT);
        nyelvCimke.setBorder(new EmptyBorder(0, 0, 6, 0));
        p.add(nyelvCimke);

        JPanel racs = new JPanel(new GridLayout(2, 4, 4, 4));
        racs.setOpaque(false);
        racs.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup csoport = new ButtonGroup();
        for (Lang l : Lang.values()) {
            JToggleButton b = new JToggleButton(new I18n.ZaszloIkon(l), l == I18n.nyelv);
            b.setToolTipText(l.nev);
            b.setFocusPainted(false);
            b.setMargin(new Insets(2, 2, 2, 2));
            b.addActionListener(e -> { I18n.valt(l); nyelvValtozott(); });
            csoport.add(b);
            racs.add(b);
            nyelvGombok.put(l, b);
        }
        p.add(racs);
        return p;
    }

    /** Minden az aktuális nyelven újraépül: fejléc, oldalsáv, főoldal, és minden modul. */
    private void nyelvValtozott() {
        setTitle(I18n.t("app.title"));
        alcimLabel.setText(I18n.t("app.subtitle"));
        nyelvCimke.setText(I18n.t("nav.lang_label"));
        labasLabel.setText(footerHtml());
        navGombok.get("home").setText(I18n.t("nav.home"));
        navGombok.get("derivalas").setText(I18n.t("nav.derivative"));
        navGombok.get("integral").setText(I18n.t("nav.integral"));
        navGombok.get("valoszinuseg").setText(I18n.t("nav.probability"));
        navGombok.get("statisztika").setText(I18n.t("nav.statistics"));
        for (Map.Entry<Lang, JToggleButton> e : nyelvGombok.entrySet()) {
            e.getValue().setToolTipText(e.getKey().nev);
            e.getValue().setSelected(e.getKey() == I18n.nyelv);
        }

        homeCimLabel.setText(I18n.t("home.title"));
        homeLeirasLabel.setText(I18n.t("home.desc"));
        for (int i = 0; i < 4; i++) {
            kartyaCimLabel[i].setText(I18n.t(KARTYA_CIM_KULCS[i]));
            kartyaLeirasLabel[i].setText(kartyaLeirasHtml(I18n.t(KARTYA_LEIRAS_KULCS[i])));
        }

        derivaltModul.nyelvValtozott();
        integralModul.nyelvValtozott();
        valoszinusegModul.nyelvValtozott();
        statisztikaModul.nyelvValtozott();

        revalidate();
        repaint();
    }

    private JButton oldalsavGomb(String szoveg, String kulcs) {
        JButton b = new JButton(szoveg);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(Tema.OLDALSAV_FONT);
        b.setForeground(Tema.OLDALSAV_SZOVEG);
        b.setBackground(Tema.OLDALSAV_HATTER);
        b.setBorder(new EmptyBorder(10, 14, 10, 10));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JComponent fooldalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Tema.HATTER);
        p.setBorder(new EmptyBorder(40, 48, 40, 48));

        homeCimLabel = new JLabel(I18n.t("home.title"));
        homeCimLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        homeCimLabel.setForeground(Tema.SZOVEG);
        homeCimLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(homeCimLabel);

        homeLeirasLabel = new JLabel(I18n.t("home.desc"));
        homeLeirasLabel.setFont(Tema.SZOVEG_FONT);
        homeLeirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        homeLeirasLabel.setBorder(new EmptyBorder(14, 0, 36, 0));
        homeLeirasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(homeLeirasLabel);

        JPanel racs = new JPanel(new GridLayout(2, 2, 22, 22));
        racs.setOpaque(false);
        racs.setAlignmentX(Component.LEFT_ALIGNMENT);
        racs.setMaximumSize(new Dimension(980, 420));

        racs.add(temaKartya(0, "d/dx", Tema.DERIVALAS, "derivalas"));
        racs.add(temaKartya(1, "∫", Tema.INTEGRALAS, "integral"));
        racs.add(temaKartya(2, "%", Tema.VALOSZINUSEG, "valoszinuseg"));
        racs.add(temaKartya(3, "Σ", Tema.STATISZTIKA, "statisztika"));

        p.add(racs);
        return new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private String kartyaLeirasHtml(String leiras) {
        return "<html><div style='width:280px'>" + leiras + "</div></html>";
    }

    private JPanel temaKartya(int index, String jel, Color szin, String celKulcs) {
        JPanel kartya = new JPanel(new BorderLayout(16, 0));
        kartya.setBackground(Color.WHITE);
        kartya.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.VONAL, 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        kartya.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel jelLabel = new JLabel(jel, SwingConstants.CENTER);
        jelLabel.setOpaque(true);
        jelLabel.setBackground(szin);
        jelLabel.setForeground(Color.WHITE);
        jelLabel.setFont(new Font("Consolas", Font.BOLD, 26));
        jelLabel.setPreferredSize(new Dimension(64, 64));

        JPanel szovegPanel = new JPanel();
        szovegPanel.setOpaque(false);
        szovegPanel.setLayout(new BoxLayout(szovegPanel, BoxLayout.Y_AXIS));
        JLabel cimLabel = new JLabel(I18n.t(KARTYA_CIM_KULCS[index]));
        cimLabel.setFont(Tema.ALCIM_FONT);
        cimLabel.setForeground(Tema.SZOVEG);
        cimLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel leirasLabel = new JLabel(kartyaLeirasHtml(I18n.t(KARTYA_LEIRAS_KULCS[index])));
        leirasLabel.setFont(Tema.SZOVEG_FONT.deriveFont(12.5f));
        leirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        leirasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leirasLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        szovegPanel.add(cimLabel);
        szovegPanel.add(leirasLabel);
        kartyaCimLabel[index] = cimLabel;
        kartyaLeirasLabel[index] = leirasLabel;

        kartya.add(jelLabel, BorderLayout.WEST);
        kartya.add(szovegPanel, BorderLayout.CENTER);

        MouseAdapter kattintas = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { kartyak.show(tartalom, celKulcs); }
            @Override public void mouseEntered(MouseEvent e) { kartya.setBackground(new Color(250, 250, 253)); }
            @Override public void mouseExited(MouseEvent e) { kartya.setBackground(Color.WHITE); }
        };
        kartya.addMouseListener(kattintas);
        for (Component c : new Component[]{jelLabel, szovegPanel, cimLabel, leirasLabel}) {
            c.addMouseListener(kattintas);
        }

        return kartya;
    }
}
