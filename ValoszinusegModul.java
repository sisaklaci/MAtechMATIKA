import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

/** Valószínűségszámítás modul: kombinatorika, binomiális és normális eloszlás, szimuláció. */
public class ValoszinusegModul extends JPanel {

    private JLabel cimLabel, leirasLabel;
    private JTabbedPane fulek;

    private JLabel combAlcim, combNCimke, combKCimke, combFaktCaption, combPermCaption, combKombCaption, combChartCim;
    private Runnable combFrissit;

    private JLabel binomAlcim, binomNLabel, binomPLabel, binomExpCaption, binomStdCaption;
    private Runnable binomFrissit;

    private JLabel normAlcim, normRangeAlcim, normACimke, normBCimke, normMuLabel, normSzigmaLabel, normProbCaption;
    private Runnable normFrissit;

    private JLabel simAlcim, simLeirasLabel, simCountCimke, simResultCaption, simPLabel;
    private JButton simStartBtn;
    private JSlider simPCsuszka;

    public ValoszinusegModul() {
        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setBackground(Tema.HATTER);

        JPanel fejlec = new JPanel();
        fejlec.setLayout(new BoxLayout(fejlec, BoxLayout.Y_AXIS));
        fejlec.setOpaque(false);
        cimLabel = Tema.cim(I18n.t("module.probability.title_full"));
        fejlec.add(cimLabel);
        leirasLabel = new JLabel(I18n.t("prob.desc"));
        leirasLabel.setFont(Tema.SZOVEG_FONT);
        leirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        leirasLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        fejlec.add(leirasLabel);
        add(fejlec, BorderLayout.NORTH);

        fulek = new JTabbedPane();
        fulek.setFont(Tema.ALCIM_FONT.deriveFont(14f));
        fulek.addTab(I18n.t("prob.tab.combinatorics"), kombinatorikaFul());
        fulek.addTab(I18n.t("prob.tab.binomial"), binomialisFul());
        fulek.addTab(I18n.t("prob.tab.normal"), normalisFul());
        fulek.addTab(I18n.t("prob.tab.simulation"), szimulacioFul());
        add(fulek, BorderLayout.CENTER);
    }

    /** Nyelvváltás után újraépíti a modul összes feliratát és újraszámol az aktuális nyelven. */
    public void nyelvValtozott() {
        cimLabel.setText(I18n.t("module.probability.title_full"));
        leirasLabel.setText(I18n.t("prob.desc"));
        fulek.setTitleAt(0, I18n.t("prob.tab.combinatorics"));
        fulek.setTitleAt(1, I18n.t("prob.tab.binomial"));
        fulek.setTitleAt(2, I18n.t("prob.tab.normal"));
        fulek.setTitleAt(3, I18n.t("prob.tab.simulation"));

        combAlcim.setText(I18n.t("prob.comb.title"));
        combNCimke.setText(I18n.t("label.n"));
        combKCimke.setText(I18n.t("label.k"));
        combFaktCaption.setText(I18n.t("prob.comb.factorial"));
        combPermCaption.setText(I18n.t("prob.comb.permutation"));
        combKombCaption.setText(I18n.t("prob.comb.combination"));
        combChartCim.setText(I18n.t("prob.comb.chart_title"));
        combFrissit.run();

        binomAlcim.setText(I18n.t("prob.binom.title"));
        binomExpCaption.setText(I18n.t("prob.binom.expected"));
        binomStdCaption.setText(I18n.t("prob.binom.stddev"));
        binomFrissit.run();

        normAlcim.setText(I18n.t("prob.normal.title"));
        normRangeAlcim.setText(I18n.t("prob.normal.range_title"));
        normACimke.setText(I18n.t("label.a"));
        normBCimke.setText(I18n.t("label.b"));
        normProbCaption.setText(I18n.t("prob.normal.prob_prefix"));
        normFrissit.run();

        simAlcim.setText(I18n.t("prob.sim.title"));
        simLeirasLabel.setText(I18n.t("prob.sim.desc"));
        simCountCimke.setText(I18n.t("prob.sim.count_label"));
        simStartBtn.setText(I18n.t("prob.sim.start_btn"));
        simResultCaption.setText(I18n.t("prob.sim.result_label"));
        simPLabel.setText(I18n.f("prob.sim.p", String.format(I18n.szamLocale(), "%.2f", simPCsuszka.getValue() / 100.0)));
    }

    // ================================================================ Kombinatorika

    private JComponent kombinatorikaFul() {
        JPanel keret = new JPanel(new BorderLayout(14, 14));
        keret.setOpaque(false);

        JPanel vezerlo = new JPanel();
        vezerlo.setLayout(new BoxLayout(vezerlo, BoxLayout.Y_AXIS));
        vezerlo.setOpaque(false);
        vezerlo.setPreferredSize(new Dimension(340, 10));

        JPanel kartya = Tema.kartya();
        kartya.setLayout(new BoxLayout(kartya, BoxLayout.Y_AXIS));
        kartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        combAlcim = Tema.alcim(I18n.t("prob.comb.title"));
        kartya.add(combAlcim);
        kartya.add(Box.createVerticalStrut(10));

        JSpinner nSpin = new JSpinner(new SpinnerNumberModel(10, 0, 60, 1));
        JSpinner kSpin = new JSpinner(new SpinnerNumberModel(3, 0, 60, 1));
        combNCimke = new JLabel(I18n.t("label.n"));
        combKCimke = new JLabel(I18n.t("label.k"));
        kartya.add(sorPanel(combNCimke, nSpin));
        kartya.add(Box.createVerticalStrut(6));
        kartya.add(sorPanel(combKCimke, kSpin));
        kartya.add(Box.createVerticalStrut(12));

        JLabel faktKep = ertekLabel(), permKep = ertekLabel(), kombKep = ertekLabel();
        combFaktCaption = magyarazoCaption(I18n.t("prob.comb.factorial"));
        combPermCaption = magyarazoCaption(I18n.t("prob.comb.permutation"));
        combKombCaption = magyarazoCaption(I18n.t("prob.comb.combination"));
        kartya.add(magyarazoSor(combFaktCaption, faktKep));
        kartya.add(magyarazoSor(combPermCaption, permKep));
        kartya.add(magyarazoSor(combKombCaption, kombKep));

        Grafikon grafikon = new Grafikon();
        grafikon.setAblak(-1, 21, 0, 1);

        combFrissit = () -> {
            int n = (Integer) nSpin.getValue();
            int k = (Integer) kSpin.getValue();
            faktKep.setText(formaz(MatElemzes.faktorialis(n)));
            permKep.setText(k <= n ? formaz(MatElemzes.permutacio(n, k)) : "-");
            kombKep.setText(k <= n ? formaz(MatElemzes.kombinacio(n, k)) : "-");

            double[] kozepek = new double[n + 1];
            double[] magassagok = new double[n + 1];
            double maxErtek = 1;
            for (int i = 0; i <= n; i++) {
                kozepek[i] = i;
                magassagok[i] = MatElemzes.kombinacio(n, i);
                maxErtek = Math.max(maxErtek, magassagok[i]);
            }
            grafikon.setAblak(-1, n + 1, -maxErtek * 0.05, maxErtek * 1.15);
            grafikon.clear();
            grafikon.addOszlopok(kozepek, magassagok, 0.7, Tema.VALOSZINUSEG);
            if (k >= 0 && k <= n) grafikon.addFuggolegesVonal(k, Tema.TENGELY);
            grafikon.repaint();
        };
        nSpin.addChangeListener(e -> combFrissit.run());
        kSpin.addChangeListener(e -> combFrissit.run());
        combFrissit.run();

        combChartCim = new JLabel(I18n.t("prob.comb.chart_title"));
        combChartCim.setFont(Tema.SZOVEG_FONT);
        combChartCim.setForeground(Tema.HALVANY_SZOVEG);
        combChartCim.setBorder(new EmptyBorder(0, 0, 6, 0));

        vezerlo.add(kartya);
        vezerlo.add(Box.createVerticalGlue());

        JPanel jobbOldal = new JPanel(new BorderLayout());
        jobbOldal.setOpaque(false);
        jobbOldal.add(combChartCim, BorderLayout.NORTH);
        jobbOldal.add(grafikon, BorderLayout.CENTER);

        keret.add(vezerlo, BorderLayout.WEST);
        keret.add(jobbOldal, BorderLayout.CENTER);
        return keret;
    }

    // ================================================================ Binomiális eloszlás

    private JComponent binomialisFul() {
        JPanel keret = new JPanel(new BorderLayout(14, 14));
        keret.setOpaque(false);

        JPanel vezerlo = new JPanel();
        vezerlo.setLayout(new BoxLayout(vezerlo, BoxLayout.Y_AXIS));
        vezerlo.setOpaque(false);
        vezerlo.setPreferredSize(new Dimension(340, 10));

        JPanel kartya = Tema.kartya();
        kartya.setLayout(new BoxLayout(kartya, BoxLayout.Y_AXIS));
        kartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        binomAlcim = Tema.alcim(I18n.t("prob.binom.title"));
        kartya.add(binomAlcim);
        kartya.add(Box.createVerticalStrut(10));

        JSlider nCsuszka = new JSlider(1, 60, 20);
        JSlider pCsuszka = new JSlider(0, 100, 50);
        binomNLabel = new JLabel(); binomPLabel = new JLabel();
        kartya.add(binomNLabel); kartya.add(nCsuszka);
        kartya.add(Box.createVerticalStrut(8));
        kartya.add(binomPLabel); kartya.add(pCsuszka);

        kartya.add(Box.createVerticalStrut(12));
        JLabel varhatoLabel = ertekLabel(), szorasLabel = ertekLabel();
        binomExpCaption = magyarazoCaption(I18n.t("prob.binom.expected"));
        binomStdCaption = magyarazoCaption(I18n.t("prob.binom.stddev"));
        kartya.add(magyarazoSor(binomExpCaption, varhatoLabel));
        kartya.add(magyarazoSor(binomStdCaption, szorasLabel));

        Grafikon grafikon = new Grafikon();

        binomFrissit = () -> {
            int n = nCsuszka.getValue();
            double p = pCsuszka.getValue() / 100.0;
            binomNLabel.setText(I18n.f("prob.binom.n", n));
            binomPLabel.setText(I18n.f("prob.binom.p", String.format(I18n.szamLocale(), "%.2f", p)));
            varhatoLabel.setText(String.format(I18n.szamLocale(), "%.2f", n * p));
            szorasLabel.setText(String.format(I18n.szamLocale(), "%.2f", Math.sqrt(n * p * (1 - p))));

            double[] kozepek = new double[n + 1];
            double[] magassagok = new double[n + 1];
            double maxErtek = 1e-6;
            for (int k = 0; k <= n; k++) {
                kozepek[k] = k;
                magassagok[k] = MatElemzes.binomialisPMF(n, p, k);
                maxErtek = Math.max(maxErtek, magassagok[k]);
            }
            grafikon.setAblak(-1, n + 1, -maxErtek * 0.05, maxErtek * 1.2);
            grafikon.clear();
            grafikon.addOszlopok(kozepek, magassagok, 0.7, Tema.VALOSZINUSEG);
            grafikon.addFuggolegesVonal(n * p, Tema.TENGELY);
            grafikon.repaint();
        };
        nCsuszka.addChangeListener(e -> binomFrissit.run());
        pCsuszka.addChangeListener(e -> binomFrissit.run());
        binomFrissit.run();

        vezerlo.add(kartya);
        vezerlo.add(Box.createVerticalGlue());
        keret.add(vezerlo, BorderLayout.WEST);
        keret.add(grafikon, BorderLayout.CENTER);
        return keret;
    }

    // ================================================================ Normális eloszlás

    private JComponent normalisFul() {
        JPanel keret = new JPanel(new BorderLayout(14, 14));
        keret.setOpaque(false);

        JPanel vezerlo = new JPanel();
        vezerlo.setLayout(new BoxLayout(vezerlo, BoxLayout.Y_AXIS));
        vezerlo.setOpaque(false);
        vezerlo.setPreferredSize(new Dimension(340, 10));

        JPanel kartya = Tema.kartya();
        kartya.setLayout(new BoxLayout(kartya, BoxLayout.Y_AXIS));
        kartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        normAlcim = Tema.alcim(I18n.t("prob.normal.title"));
        kartya.add(normAlcim);
        kartya.add(Box.createVerticalStrut(10));

        JSlider muCsuszka = new JSlider(-200, 200, 0);
        JSlider szigmaCsuszka = new JSlider(10, 300, 100);
        normMuLabel = new JLabel(); normSzigmaLabel = new JLabel();
        kartya.add(normMuLabel); kartya.add(muCsuszka);
        kartya.add(Box.createVerticalStrut(8));
        kartya.add(normSzigmaLabel); kartya.add(szigmaCsuszka);

        kartya.add(Box.createVerticalStrut(14));
        normRangeAlcim = Tema.alcim(I18n.t("prob.normal.range_title"));
        kartya.add(normRangeAlcim);
        kartya.add(Box.createVerticalStrut(6));
        JTextField aMezo = Tema.mezo("-1");
        JTextField bMezo = Tema.mezo("1");
        JPanel hatarok = new JPanel(new GridLayout(1, 2, 8, 0));
        hatarok.setOpaque(false);
        hatarok.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel aP = new JPanel(new BorderLayout(4, 0)); aP.setOpaque(false);
        normACimke = new JLabel(I18n.t("label.a"));
        aP.add(normACimke, BorderLayout.WEST); aP.add(aMezo, BorderLayout.CENTER);
        JPanel bP = new JPanel(new BorderLayout(4, 0)); bP.setOpaque(false);
        normBCimke = new JLabel(I18n.t("label.b"));
        bP.add(normBCimke, BorderLayout.WEST); bP.add(bMezo, BorderLayout.CENTER);
        hatarok.add(aP); hatarok.add(bP);
        kartya.add(hatarok);
        kartya.add(Box.createVerticalStrut(8));
        JLabel valoszinusegLabel = ertekLabel();
        normProbCaption = magyarazoCaption(I18n.t("prob.normal.prob_prefix"));
        kartya.add(magyarazoSor(normProbCaption, valoszinusegLabel));

        Grafikon grafikon = new Grafikon();

        normFrissit = () -> {
            double mu = muCsuszka.getValue() / 20.0;
            double szigma = szigmaCsuszka.getValue() / 100.0;
            normMuLabel.setText(I18n.f("prob.normal.mu", String.format(I18n.szamLocale(), "%.2f", mu)));
            normSzigmaLabel.setText(I18n.f("prob.normal.sigma", String.format(I18n.szamLocale(), "%.2f", szigma)));

            double a, b;
            try {
                a = Double.parseDouble(aMezo.getText().trim().replace(',', '.'));
                b = Double.parseDouble(bMezo.getText().trim().replace(',', '.'));
            } catch (Exception ex) { a = mu - szigma; b = mu + szigma; }

            double p = MatElemzes.normalisCDF(b, mu, szigma) - MatElemzes.normalisCDF(a, mu, szigma);
            valoszinusegLabel.setText(String.format(I18n.szamLocale(), "%.4f", p));

            java.util.function.DoubleUnaryOperator f = x -> MatElemzes.normalisPDF(x, mu, szigma);
            grafikon.setAblak(mu - 4 * szigma - 1, mu + 4 * szigma + 1, 0, MatElemzes.normalisPDF(mu, mu, szigma) * 1.25);
            grafikon.clear();
            grafikon.addTerulet(f, a, b, Tema.VALOSZINUSEG);
            grafikon.addFuggveny(f, Tema.VALOSZINUSEG, 2.6f);
            grafikon.repaint();
        };
        muCsuszka.addChangeListener(e -> normFrissit.run());
        szigmaCsuszka.addChangeListener(e -> normFrissit.run());
        aMezo.addActionListener(e -> normFrissit.run());
        bMezo.addActionListener(e -> normFrissit.run());
        normFrissit.run();

        vezerlo.add(kartya);
        vezerlo.add(Box.createVerticalGlue());
        keret.add(vezerlo, BorderLayout.WEST);
        keret.add(grafikon, BorderLayout.CENTER);
        return keret;
    }

    // ================================================================ Szimuláció (nagy számok törvénye)

    private JComponent szimulacioFul() {
        JPanel keret = new JPanel(new BorderLayout(14, 14));
        keret.setOpaque(false);

        JPanel vezerlo = new JPanel();
        vezerlo.setLayout(new BoxLayout(vezerlo, BoxLayout.Y_AXIS));
        vezerlo.setOpaque(false);
        vezerlo.setPreferredSize(new Dimension(340, 10));

        JPanel kartya = Tema.kartya();
        kartya.setLayout(new BoxLayout(kartya, BoxLayout.Y_AXIS));
        kartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        simAlcim = Tema.alcim(I18n.t("prob.sim.title"));
        kartya.add(simAlcim);
        kartya.add(Box.createVerticalStrut(6));
        simLeirasLabel = new JLabel(I18n.t("prob.sim.desc"));
        simLeirasLabel.setFont(Tema.SZOVEG_FONT);
        simLeirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        kartya.add(simLeirasLabel);

        kartya.add(Box.createVerticalStrut(12));
        simPCsuszka = new JSlider(1, 99, 50);
        simPLabel = new JLabel();
        kartya.add(simPLabel);
        kartya.add(simPCsuszka);

        kartya.add(Box.createVerticalStrut(10));
        JComboBox<String> dobasszamValaszto = new JComboBox<>(new String[]{"200", "1000", "5000", "20000"});
        dobasszamValaszto.setSelectedIndex(2);
        simCountCimke = new JLabel(I18n.t("prob.sim.count_label"));
        kartya.add(sorPanel(simCountCimke, dobasszamValaszto));

        kartya.add(Box.createVerticalStrut(10));
        simStartBtn = Tema.gomb(I18n.t("prob.sim.start_btn"), Tema.VALOSZINUSEG);
        simStartBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        kartya.add(simStartBtn);

        kartya.add(Box.createVerticalStrut(10));
        JLabel vegEredmeny = ertekLabel();
        simResultCaption = magyarazoCaption(I18n.t("prob.sim.result_label"));
        kartya.add(magyarazoSor(simResultCaption, vegEredmeny));

        Grafikon grafikon = new Grafikon();
        grafikon.setAblak(1, 1000, 0, 1);

        simPCsuszka.addChangeListener(e -> simPLabel.setText(
                I18n.f("prob.sim.p", String.format(I18n.szamLocale(), "%.2f", simPCsuszka.getValue() / 100.0))));
        simPLabel.setText(I18n.f("prob.sim.p", String.format(I18n.szamLocale(), "%.2f", 0.50)));

        simStartBtn.addActionListener(e -> {
            double p = simPCsuszka.getValue() / 100.0;
            int n = Integer.parseInt((String) dobasszamValaszto.getSelectedItem());
            Random veletlen = new Random();
            double[] xs = new double[n];
            double[] arany = new double[n];
            int fejSzamlalo = 0;
            for (int i = 0; i < n; i++) {
                if (veletlen.nextDouble() < p) fejSzamlalo++;
                xs[i] = i + 1;
                arany[i] = fejSzamlalo / (double) (i + 1);
            }
            vegEredmeny.setText(String.format(I18n.szamLocale(), "%.4f", arany[n - 1]));

            grafikon.setAblak(1, n, 0, 1);
            grafikon.clear();
            grafikon.addEgyenes(0, p, new Color(200, 60, 60));
            grafikon.addVonal(xs, arany, Tema.VALOSZINUSEG, 2f);
            grafikon.repaint();
        });

        vezerlo.add(kartya);
        vezerlo.add(Box.createVerticalGlue());
        keret.add(vezerlo, BorderLayout.WEST);
        keret.add(grafikon, BorderLayout.CENTER);
        return keret;
    }

    // ================================================================ segédek

    private static JLabel ertekLabel() {
        JLabel l = new JLabel("-");
        l.setFont(Tema.KEPLET_FONT);
        l.setForeground(Tema.SZOVEG);
        return l;
    }

    private static String formaz(double v) {
        if (v == Math.floor(v) && Math.abs(v) < 1e15) return String.format(I18n.szamLocale(), "%,.0f", v);
        return String.format(I18n.szamLocale(), "%.4f", v);
    }

    private JPanel sorPanel(JLabel cimkeLabel, JComponent mezo) {
        JPanel sor = new JPanel(new BorderLayout(8, 0));
        sor.setOpaque(false);
        sor.setAlignmentX(Component.LEFT_ALIGNMENT);
        sor.setMaximumSize(new Dimension(500, 30));
        cimkeLabel.setFont(Tema.SZOVEG_FONT);
        sor.add(cimkeLabel, BorderLayout.WEST);
        sor.add(mezo, BorderLayout.CENTER);
        return sor;
    }

    private static JLabel magyarazoCaption(String szoveg) {
        JLabel c = new JLabel(szoveg);
        c.setFont(Tema.SZOVEG_FONT.deriveFont(12f));
        c.setForeground(Tema.HALVANY_SZOVEG);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private JPanel magyarazoSor(JLabel caption, JLabel ertek) {
        JPanel sor = new JPanel();
        sor.setLayout(new BoxLayout(sor, BoxLayout.Y_AXIS));
        sor.setOpaque(false);
        sor.setAlignmentX(Component.LEFT_ALIGNMENT);
        sor.setBorder(new EmptyBorder(6, 0, 0, 0));
        ertek.setAlignmentX(Component.LEFT_ALIGNMENT);
        sor.add(caption);
        sor.add(ertek);
        return sor;
    }
}
