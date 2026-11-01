import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/** Deriválás modul: függvény és érintője élőben, szimbolikus derivált és szabály-magyarázat. */
public class DerivaltModul extends JPanel {

    private final JTextField fMezo = Tema.mezo("x^3 - 3*x^2 + 2");
    private final JLabel fKiirva = ujKeplet();
    private final JLabel fVesszoKiirva = ujKeplet();
    private final JLabel erintoKiirva = ujKeplet();
    private final JLabel szabalyLabel = new JLabel(" ");
    private final JLabel hibaLabel = new JLabel(" ");
    private final JSlider x0Csuszka = new JSlider(-500, 500, 150);
    private final JLabel x0Ertek = new JLabel();
    private final Grafikon grafikon = new Grafikon();

    private JLabel cimLabel, leirasLabel, fxAlcim, eredmenyAlcim, erintoAlcim;
    private JLabel fSorCimke, fVesszoSorCimke, slopeSorCimke;
    private JButton rajzolBtn;

    private Kifejezes aktualisF;
    private Kifejezes aktualisFVesszo;

    public DerivaltModul() {
        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setBackground(Tema.HATTER);

        JPanel fejlec = new JPanel();
        fejlec.setLayout(new BoxLayout(fejlec, BoxLayout.Y_AXIS));
        fejlec.setOpaque(false);
        cimLabel = Tema.cim(I18n.t("module.derivative.title"));
        fejlec.add(cimLabel);
        leirasLabel = new JLabel(I18n.t("deriv.desc"));
        leirasLabel.setFont(Tema.SZOVEG_FONT);
        leirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        leirasLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        fejlec.add(leirasLabel);
        add(fejlec, BorderLayout.NORTH);

        add(epitVezerlo(), BorderLayout.WEST);
        add(grafikon, BorderLayout.CENTER);

        fMezo.addActionListener(e -> ujraszamol());
        x0Csuszka.addChangeListener(e -> frissitErinto());

        ujraszamol();
    }

    private static JLabel ujKeplet() {
        JLabel l = new JLabel(" ");
        l.setFont(Tema.KEPLET_FONT);
        l.setForeground(Tema.SZOVEG);
        return l;
    }

    private JComponent epitVezerlo() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(340, 10));

        JPanel bemenetKartya = Tema.kartya();
        bemenetKartya.setLayout(new BoxLayout(bemenetKartya, BoxLayout.Y_AXIS));
        bemenetKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        fxAlcim = Tema.alcim(I18n.t("label.fx"));
        bemenetKartya.add(fxAlcim);
        bemenetKartya.add(Box.createVerticalStrut(6));
        fMezo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bemenetKartya.add(fMezo);
        hibaLabel.setForeground(new Color(200, 50, 50));
        hibaLabel.setFont(Tema.SZOVEG_FONT.deriveFont(12f));
        bemenetKartya.add(hibaLabel);

        bemenetKartya.add(Box.createVerticalStrut(10));
        JPanel preszetek = new JPanel(new GridLayout(0, 2, 6, 6));
        preszetek.setOpaque(false);
        String[] pelda = {"x^2", "x^3 - 3*x^2", "sin(x)", "cos(x)*x", "exp(x)", "ln(x)", "1/x", "sqrt(x)"};
        for (String p2 : pelda) {
            JButton b = new JButton(p2);
            b.setFont(Tema.SZOVEG_FONT);
            b.setFocusPainted(false);
            b.addActionListener(e -> { fMezo.setText(p2); ujraszamol(); });
            preszetek.add(b);
        }
        bemenetKartya.add(preszetek);

        rajzolBtn = Tema.gomb(I18n.t("btn.plot"), Tema.DERIVALAS);
        rajzolBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rajzolBtn.addActionListener(e -> ujraszamol());
        bemenetKartya.add(Box.createVerticalStrut(10));
        bemenetKartya.add(rajzolBtn);

        JPanel eredmenyKartya = Tema.kartya();
        eredmenyKartya.setLayout(new BoxLayout(eredmenyKartya, BoxLayout.Y_AXIS));
        eredmenyKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        eredmenyAlcim = Tema.alcim(I18n.t("label.result"));
        eredmenyKartya.add(eredmenyAlcim);
        eredmenyKartya.add(Box.createVerticalStrut(8));
        fSorCimke = sorCimke(I18n.t("deriv.fx_eq"));
        eredmenyKartya.add(soritott(fSorCimke, fKiirva));
        eredmenyKartya.add(Box.createVerticalStrut(4));
        fVesszoSorCimke = sorCimke(I18n.t("deriv.fprime_eq"));
        eredmenyKartya.add(soritott(fVesszoSorCimke, fVesszoKiirva));
        eredmenyKartya.add(Box.createVerticalStrut(10));
        szabalyLabel.setFont(Tema.SZOVEG_FONT);
        szabalyLabel.setForeground(Tema.HALVANY_SZOVEG);
        eredmenyKartya.add(szabalyLabel);

        JPanel erintoKartya = Tema.kartya();
        erintoKartya.setLayout(new BoxLayout(erintoKartya, BoxLayout.Y_AXIS));
        erintoKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        erintoAlcim = Tema.alcim(I18n.t("deriv.tangent.title"));
        erintoKartya.add(erintoAlcim);
        erintoKartya.add(Box.createVerticalStrut(6));
        x0Csuszka.setAlignmentX(Component.LEFT_ALIGNMENT);
        x0Csuszka.setOpaque(false);
        erintoKartya.add(x0Csuszka);
        x0Ertek.setFont(Tema.SZOVEG_FONT);
        erintoKartya.add(x0Ertek);
        erintoKartya.add(Box.createVerticalStrut(4));
        slopeSorCimke = sorCimke(I18n.t("deriv.slope_prefix"));
        erintoKartya.add(soritott(slopeSorCimke, erintoKiirva));

        p.add(bemenetKartya);
        p.add(Box.createVerticalStrut(14));
        p.add(eredmenyKartya);
        p.add(Box.createVerticalStrut(14));
        p.add(erintoKartya);
        p.add(Box.createVerticalGlue());
        return new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private static JLabel sorCimke(String szoveg) {
        JLabel c = new JLabel(szoveg);
        c.setFont(Tema.SZOVEG_FONT);
        return c;
    }

    private JPanel soritott(JLabel cimkeLabel, JLabel ertekLabel) {
        JPanel sor = new JPanel(new BorderLayout());
        sor.setOpaque(false);
        sor.setAlignmentX(Component.LEFT_ALIGNMENT);
        sor.add(cimkeLabel, BorderLayout.WEST);
        sor.add(ertekLabel, BorderLayout.CENTER);
        return sor;
    }

    /** Nyelvváltás után újraépíti a modul összes feliratát és újraszámol az aktuális nyelven. */
    public void nyelvValtozott() {
        cimLabel.setText(I18n.t("module.derivative.title"));
        leirasLabel.setText(I18n.t("deriv.desc"));
        fxAlcim.setText(I18n.t("label.fx"));
        rajzolBtn.setText(I18n.t("btn.plot"));
        eredmenyAlcim.setText(I18n.t("label.result"));
        fSorCimke.setText(I18n.t("deriv.fx_eq"));
        fVesszoSorCimke.setText(I18n.t("deriv.fprime_eq"));
        erintoAlcim.setText(I18n.t("deriv.tangent.title"));
        slopeSorCimke.setText(I18n.t("deriv.slope_prefix"));
        ujraszamol();
    }

    private void ujraszamol() {
        hibaLabel.setText(" ");
        try {
            aktualisF = Kifejezeselemzo.elemez(fMezo.getText());
            aktualisFVesszo = aktualisF.derivalt().egyszerusit().egyszerusit().egyszerusit();
            fKiirva.setText(aktualisF.toString());
            fVesszoKiirva.setText(aktualisFVesszo.toString());
            szabalyLabel.setText("<html>" + szabalyMagyarazat(aktualisF) + "</html>");

            grafikon.autoY(aktualisF.fuggveny(), -8, 8);
            frissitAbra();
        } catch (Exception ex) {
            hibaLabel.setText(ex.getMessage());
        }
    }

    private void frissitErinto() {
        if (aktualisF == null) return;
        frissitAbra();
    }

    private void frissitAbra() {
        double x0 = x0Csuszka.getValue() / 50.0;
        x0Ertek.setText(I18n.f("deriv.x0", String.format(I18n.szamLocale(), "%.2f", x0)));

        double y0, meredek;
        try {
            y0 = aktualisF.ertek(x0);
            meredek = aktualisFVesszo.ertek(x0);
        } catch (Exception ex) {
            return;
        }
        erintoKiirva.setText(String.format(I18n.szamLocale(), "%.3f", meredek));

        grafikon.clear();
        grafikon.addFuggveny(aktualisF.fuggveny(), Tema.DERIVALAS, 2.6f);
        grafikon.addFuggveny(aktualisFVesszo.fuggveny(), new Color(230, 120, 30), 2.0f);
        grafikon.addErintoegyenes(x0, y0, meredek, new Color(30, 170, 90));
        grafikon.addPont(x0, y0, new Color(30, 170, 90), 6);
        grafikon.repaint();
    }

    private static String szabalyMagyarazat(Kifejezes k) {
        if (k instanceof Kifejezes.Osszeg || k instanceof Kifejezes.Kulonbseg)
            return I18n.t("rule.sum_diff");
        if (k instanceof Kifejezes.Szorzat)
            return I18n.t("rule.product");
        if (k instanceof Kifejezes.Hanyados)
            return I18n.t("rule.quotient");
        if (k instanceof Kifejezes.Hatvany) {
            Kifejezes.Hatvany h = (Kifejezes.Hatvany) k;
            if (h.alap instanceof Kifejezes.Valtozo)
                return I18n.t("rule.power");
            return I18n.t("rule.power_chain");
        }
        if (k instanceof Kifejezes.FuggvenyHivas) {
            Kifejezes.FuggvenyHivas f = (Kifejezes.FuggvenyHivas) k;
            if (f.u instanceof Kifejezes.Valtozo)
                return I18n.f("rule.basic_function", f.nev);
            return I18n.t("rule.chain");
        }
        if (k instanceof Kifejezes.Negalt) return szabalyMagyarazat(((Kifejezes.Negalt) k).u);
        return I18n.t("rule.basic_const");
    }
}
