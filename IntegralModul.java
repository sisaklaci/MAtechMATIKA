import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Integrálás modul: Riemann-téglalapok élő animációja, numerikus és (ha lehet) szimbolikus eredmény. */
public class IntegralModul extends JPanel {

    private final JTextField fMezo = Tema.mezo("x^2");
    private final JTextField aMezo = Tema.mezo("0");
    private final JTextField bMezo = Tema.mezo("3");
    private final JComboBox<String> modValaszto = new JComboBox<>();
    private final JSlider nCsuszka = new JSlider(1, 200, 10);
    private final JLabel nErtek = new JLabel();
    private final JLabel riemannOsszeg = ujKeplet();
    private final JLabel pontosErtek = ujKeplet();
    private final JLabel antiderivaltKeplet = ujKeplet();
    private final JLabel hibaLabel = new JLabel(" ");
    private final Grafikon grafikon = new Grafikon();

    private JLabel cimLabel, leirasLabel, fxAlcim, aCimke, bCimke;
    private JLabel riemannAlcim, exactAlcim, antiderivLabel, approxSorCimke, exactSorCimke;
    private JButton rajzolBtn;

    private Kifejezes aktualisF;

    public IntegralModul() {
        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setBackground(Tema.HATTER);

        JPanel fejlec = new JPanel();
        fejlec.setLayout(new BoxLayout(fejlec, BoxLayout.Y_AXIS));
        fejlec.setOpaque(false);
        cimLabel = Tema.cim(I18n.t("module.integral.title"));
        fejlec.add(cimLabel);
        leirasLabel = new JLabel(I18n.t("integ.desc"));
        leirasLabel.setFont(Tema.SZOVEG_FONT);
        leirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        leirasLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        fejlec.add(leirasLabel);
        add(fejlec, BorderLayout.NORTH);

        add(epitVezerlo(), BorderLayout.WEST);
        add(grafikon, BorderLayout.CENTER);

        fMezo.addActionListener(e -> ujraszamol());
        aMezo.addActionListener(e -> ujraszamol());
        bMezo.addActionListener(e -> ujraszamol());
        modValaszto.addActionListener(e -> frissitAbra());
        nCsuszka.addChangeListener(e -> frissitAbra());

        ujraszamol();
    }

    private static JLabel ujKeplet() {
        JLabel l = new JLabel(" ");
        l.setFont(Tema.KEPLET_FONT);
        l.setForeground(Tema.SZOVEG);
        return l;
    }

    private void toltModValaszto() {
        int kivalasztott = modValaszto.getSelectedIndex();
        modValaszto.setModel(new DefaultComboBoxModel<>(new String[]{
                I18n.t("integ.mode.left"), I18n.t("integ.mode.right"), I18n.t("integ.mode.mid")}));
        modValaszto.setSelectedIndex(kivalasztott < 0 ? 0 : kivalasztott);
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

        JPanel hatarok = new JPanel(new GridLayout(1, 2, 8, 0));
        hatarok.setOpaque(false);
        hatarok.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel aPanel = new JPanel(new BorderLayout(4, 0)); aPanel.setOpaque(false);
        aCimke = new JLabel(I18n.t("label.a"));
        aPanel.add(aCimke, BorderLayout.WEST); aPanel.add(aMezo, BorderLayout.CENTER);
        JPanel bPanel = new JPanel(new BorderLayout(4, 0)); bPanel.setOpaque(false);
        bCimke = new JLabel(I18n.t("label.b"));
        bPanel.add(bCimke, BorderLayout.WEST); bPanel.add(bMezo, BorderLayout.CENTER);
        hatarok.add(aPanel);
        hatarok.add(bPanel);
        bemenetKartya.add(Box.createVerticalStrut(8));
        bemenetKartya.add(hatarok);

        hibaLabel.setForeground(new Color(200, 50, 50));
        hibaLabel.setFont(Tema.SZOVEG_FONT.deriveFont(12f));
        bemenetKartya.add(hibaLabel);

        bemenetKartya.add(Box.createVerticalStrut(10));
        JPanel preszetek = new JPanel(new GridLayout(0, 2, 6, 6));
        preszetek.setOpaque(false);
        String[] pelda = {"x^2", "sin(x)", "x^3 - x", "exp(x)/4", "cos(x)", "sqrt(x)"};
        for (String p2 : pelda) {
            JButton b = new JButton(p2);
            b.setFont(Tema.SZOVEG_FONT);
            b.setFocusPainted(false);
            b.addActionListener(e -> { fMezo.setText(p2); ujraszamol(); });
            preszetek.add(b);
        }
        bemenetKartya.add(preszetek);

        rajzolBtn = Tema.gomb(I18n.t("btn.plot"), Tema.INTEGRALAS);
        rajzolBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rajzolBtn.addActionListener(e -> ujraszamol());
        bemenetKartya.add(Box.createVerticalStrut(10));
        bemenetKartya.add(rajzolBtn);

        JPanel teglalapKartya = Tema.kartya();
        teglalapKartya.setLayout(new BoxLayout(teglalapKartya, BoxLayout.Y_AXIS));
        teglalapKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        riemannAlcim = Tema.alcim(I18n.t("integ.riemann.title"));
        teglalapKartya.add(riemannAlcim);
        teglalapKartya.add(Box.createVerticalStrut(6));
        toltModValaszto();
        modValaszto.setAlignmentX(Component.LEFT_ALIGNMENT);
        modValaszto.setFont(Tema.SZOVEG_FONT);
        teglalapKartya.add(modValaszto);
        teglalapKartya.add(Box.createVerticalStrut(8));
        nErtek.setFont(Tema.SZOVEG_FONT);
        teglalapKartya.add(nErtek);
        nCsuszka.setOpaque(false);
        nCsuszka.setAlignmentX(Component.LEFT_ALIGNMENT);
        teglalapKartya.add(nCsuszka);
        teglalapKartya.add(Box.createVerticalStrut(6));
        approxSorCimke = new JLabel(I18n.t("integ.approx_prefix"));
        approxSorCimke.setFont(Tema.SZOVEG_FONT);
        teglalapKartya.add(soritott(approxSorCimke, riemannOsszeg));

        JPanel eredmenyKartya = Tema.kartya();
        eredmenyKartya.setLayout(new BoxLayout(eredmenyKartya, BoxLayout.Y_AXIS));
        eredmenyKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        exactAlcim = Tema.alcim(I18n.t("integ.exact.title"));
        eredmenyKartya.add(exactAlcim);
        eredmenyKartya.add(Box.createVerticalStrut(8));
        exactSorCimke = new JLabel(I18n.t("integ.exact_prefix"));
        exactSorCimke.setFont(Tema.SZOVEG_FONT);
        eredmenyKartya.add(soritott(exactSorCimke, pontosErtek));
        eredmenyKartya.add(Box.createVerticalStrut(6));
        antiderivLabel = new JLabel(I18n.t("integ.antideriv.label"));
        antiderivLabel.setFont(Tema.SZOVEG_FONT);
        antiderivLabel.setForeground(Tema.HALVANY_SZOVEG);
        eredmenyKartya.add(antiderivLabel);
        eredmenyKartya.add(antiderivaltKeplet);

        p.add(bemenetKartya);
        p.add(Box.createVerticalStrut(14));
        p.add(teglalapKartya);
        p.add(Box.createVerticalStrut(14));
        p.add(eredmenyKartya);
        p.add(Box.createVerticalGlue());
        return new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        cimLabel.setText(I18n.t("module.integral.title"));
        leirasLabel.setText(I18n.t("integ.desc"));
        fxAlcim.setText(I18n.t("label.fx"));
        aCimke.setText(I18n.t("label.a"));
        bCimke.setText(I18n.t("label.b"));
        rajzolBtn.setText(I18n.t("btn.plot"));
        riemannAlcim.setText(I18n.t("integ.riemann.title"));
        toltModValaszto();
        approxSorCimke.setText(I18n.t("integ.approx_prefix"));
        exactAlcim.setText(I18n.t("integ.exact.title"));
        exactSorCimke.setText(I18n.t("integ.exact_prefix"));
        antiderivLabel.setText(I18n.t("integ.antideriv.label"));
        ujraszamol();
    }

    private void ujraszamol() {
        hibaLabel.setText(" ");
        try {
            aktualisF = Kifejezeselemzo.elemez(fMezo.getText());
            double a = Double.parseDouble(aMezo.getText().trim().replace(',', '.'));
            double b = Double.parseDouble(bMezo.getText().trim().replace(',', '.'));
            if (b <= a) throw new IllegalArgumentException(I18n.t("err.b_gt_a"));

            double pontos = MatElemzes.szimpson(aktualisF.fuggveny(), a, b, 400);
            pontosErtek.setText(String.format(I18n.szamLocale(), "%.5f", pontos));

            try {
                Kifejezes F = MatElemzes.probalkozzAntiderivalt(aktualisF.egyszerusit()).egyszerusit().egyszerusit();
                antiderivaltKeplet.setText("<html>" + F.toString() + I18n.t("integ.antideriv.suffix") + "</html>");
            } catch (MatElemzes.NemTudjukKivetel ex) {
                antiderivaltKeplet.setText(I18n.t("integ.antideriv.none"));
            }

            double margo = (b - a) * 0.3 + 1;
            grafikon.autoY(aktualisF.fuggveny(), a - margo, b + margo);
            frissitAbra();
        } catch (Exception ex) {
            hibaLabel.setText(ex.getMessage());
        }
    }

    private void frissitAbra() {
        if (aktualisF == null) return;
        try {
            double a = Double.parseDouble(aMezo.getText().trim().replace(',', '.'));
            double b = Double.parseDouble(bMezo.getText().trim().replace(',', '.'));
            int n = nCsuszka.getValue();
            nErtek.setText(I18n.f("integ.rect_count", n));
            String mod = modValaszto.getSelectedIndex() == 0 ? "bal" : modValaszto.getSelectedIndex() == 1 ? "jobb" : "kozep";

            double osszeg = MatElemzes.riemann(aktualisF.fuggveny(), a, b, n, mod);
            riemannOsszeg.setText(String.format(I18n.szamLocale(), "%.5f", osszeg));

            grafikon.clear();
            grafikon.addTerulet(aktualisF.fuggveny(), a, b, new Color(200, 200, 200));
            grafikon.addTeglalapok(aktualisF.fuggveny(), a, b, n, mod, Tema.INTEGRALAS);
            grafikon.addFuggveny(aktualisF.fuggveny(), Tema.DERIVALAS, 2.4f);
            grafikon.addFuggolegesVonal(a, Tema.TENGELY);
            grafikon.addFuggolegesVonal(b, Tema.TENGELY);
            grafikon.repaint();
        } catch (Exception ignored) {
        }
    }
}
