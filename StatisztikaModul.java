import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

/** Statisztika modul: leíró statisztika + hisztogram, illetve korreláció/lineáris regresszió. */
public class StatisztikaModul extends JPanel {

    private static final String[] LEIRO_KULCS = {
            "stat.desc.count", "stat.desc.mean", "stat.desc.median", "stat.desc.stddev",
            "stat.desc.variance", "stat.desc.min", "stat.desc.max", "stat.desc.range"};

    private JLabel cimLabel, leirasLabel;
    private JTabbedPane fulek;

    private JLabel descAlcim, chartCim1;
    private JButton randomBtn, calcBtn1;
    private final JLabel[] leiroCaptionLabelek = new JLabel[8];
    private Runnable descSzamol;

    private JLabel regAlcim, xCimke, yCimke, regEqAlcim, slopeCaption, interceptCaption, corrCaption, explainLabel;
    private JButton calcBtn2;
    private Runnable regSzamol;

    public StatisztikaModul() {
        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setBackground(Tema.HATTER);

        JPanel fejlec = new JPanel();
        fejlec.setLayout(new BoxLayout(fejlec, BoxLayout.Y_AXIS));
        fejlec.setOpaque(false);
        cimLabel = Tema.cim(I18n.t("module.statistics.title"));
        fejlec.add(cimLabel);
        leirasLabel = new JLabel(I18n.t("stat.desc"));
        leirasLabel.setFont(Tema.SZOVEG_FONT);
        leirasLabel.setForeground(Tema.HALVANY_SZOVEG);
        leirasLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        fejlec.add(leirasLabel);
        add(fejlec, BorderLayout.NORTH);

        fulek = new JTabbedPane();
        fulek.setFont(Tema.ALCIM_FONT.deriveFont(14f));
        fulek.addTab(I18n.t("stat.tab.descriptive"), leiroFul());
        fulek.addTab(I18n.t("stat.tab.regression"), regresszioFul());
        add(fulek, BorderLayout.CENTER);
    }

    /** Nyelvváltás után újraépíti a modul összes feliratát és újraszámol az aktuális nyelven. */
    public void nyelvValtozott() {
        cimLabel.setText(I18n.t("module.statistics.title"));
        leirasLabel.setText(I18n.t("stat.desc"));
        fulek.setTitleAt(0, I18n.t("stat.tab.descriptive"));
        fulek.setTitleAt(1, I18n.t("stat.tab.regression"));

        descAlcim.setText(I18n.t("stat.desc.data_label"));
        randomBtn.setText(I18n.t("stat.desc.random_btn"));
        calcBtn1.setText(I18n.t("btn.calculate"));
        for (int i = 0; i < LEIRO_KULCS.length; i++) leiroCaptionLabelek[i].setText(I18n.t(LEIRO_KULCS[i]) + ":");
        chartCim1.setText(I18n.t("stat.desc.chart_title"));
        descSzamol.run();

        regAlcim.setText(I18n.t("stat.reg.title"));
        xCimke.setText(I18n.t("stat.reg.x_label"));
        yCimke.setText(I18n.t("stat.reg.y_label"));
        calcBtn2.setText(I18n.t("btn.calculate"));
        regEqAlcim.setText(I18n.t("stat.reg.eq_title"));
        slopeCaption.setText(I18n.t("stat.reg.slope_label"));
        interceptCaption.setText(I18n.t("stat.reg.intercept_label"));
        corrCaption.setText(I18n.t("stat.reg.corr_label"));
        explainLabel.setText(I18n.t("stat.reg.explain"));
        regSzamol.run();
    }

    // ================================================================ Leíró statisztika

    private JComponent leiroFul() {
        JPanel keret = new JPanel(new BorderLayout(14, 14));
        keret.setOpaque(false);

        JPanel vezerlo = new JPanel();
        vezerlo.setLayout(new BoxLayout(vezerlo, BoxLayout.Y_AXIS));
        vezerlo.setOpaque(false);
        vezerlo.setPreferredSize(new Dimension(360, 10));

        JPanel bemenetKartya = Tema.kartya();
        bemenetKartya.setLayout(new BoxLayout(bemenetKartya, BoxLayout.Y_AXIS));
        bemenetKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        descAlcim = Tema.alcim(I18n.t("stat.desc.data_label"));
        bemenetKartya.add(descAlcim);
        bemenetKartya.add(Box.createVerticalStrut(8));

        JTextArea adatTerulet = new JTextArea(6, 20);
        adatTerulet.setLineWrap(true);
        adatTerulet.setFont(Tema.KEPLET_FONT.deriveFont(13f));
        adatTerulet.setText(mintaAdatSztring(new Random(42), 40, 50, 12));
        JScrollPane adatScroll = new JScrollPane(adatTerulet);
        adatScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        adatScroll.setMaximumSize(new Dimension(5000, 140));
        bemenetKartya.add(adatScroll);

        bemenetKartya.add(Box.createVerticalStrut(8));
        randomBtn = new JButton(I18n.t("stat.desc.random_btn"));
        randomBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        randomBtn.setFont(Tema.SZOVEG_FONT);
        bemenetKartya.add(randomBtn);

        JLabel hibaLabel = new JLabel(" ");
        hibaLabel.setForeground(new Color(200, 50, 50));
        hibaLabel.setFont(Tema.SZOVEG_FONT.deriveFont(12f));
        bemenetKartya.add(hibaLabel);

        calcBtn1 = Tema.gomb(I18n.t("btn.calculate"), Tema.STATISZTIKA);
        calcBtn1.setAlignmentX(Component.LEFT_ALIGNMENT);
        bemenetKartya.add(Box.createVerticalStrut(8));
        bemenetKartya.add(calcBtn1);

        JPanel eredmenyKartya = Tema.kartya();
        eredmenyKartya.setLayout(new GridLayout(0, 2, 10, 6));
        eredmenyKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel[] ertekLabelek = new JLabel[8];
        for (int i = 0; i < LEIRO_KULCS.length; i++) {
            JLabel c = new JLabel(I18n.t(LEIRO_KULCS[i]) + ":");
            c.setFont(Tema.SZOVEG_FONT);
            c.setForeground(Tema.HALVANY_SZOVEG);
            leiroCaptionLabelek[i] = c;
            ertekLabelek[i] = new JLabel("-");
            ertekLabelek[i].setFont(Tema.KEPLET_FONT.deriveFont(14f));
            eredmenyKartya.add(c);
            eredmenyKartya.add(ertekLabelek[i]);
        }

        Grafikon grafikon = new Grafikon();

        descSzamol = () -> {
            hibaLabel.setText(" ");
            try {
                double[] adat = parszolAdat(adatTerulet.getText());
                if (adat.length < 2) throw new IllegalArgumentException(I18n.t("err.need2_data"));

                double minErtek = adat[0], maxErtek = adat[0];
                for (double v : adat) { minErtek = Math.min(minErtek, v); maxErtek = Math.max(maxErtek, v); }

                ertekLabelek[0].setText(String.valueOf(adat.length));
                ertekLabelek[1].setText(fmt(MatElemzes.atlag(adat)));
                ertekLabelek[2].setText(fmt(MatElemzes.median(adat)));
                ertekLabelek[3].setText(fmt(MatElemzes.szoras(adat)));
                ertekLabelek[4].setText(fmt(MatElemzes.variancia(adat)));
                ertekLabelek[5].setText(fmt(minErtek));
                ertekLabelek[6].setText(fmt(maxErtek));
                ertekLabelek[7].setText(fmt(maxErtek - minErtek));

                rajzolHisztogram(grafikon, adat);
            } catch (Exception ex) {
                hibaLabel.setText(ex.getMessage());
            }
        };

        randomBtn.addActionListener(e -> {
            adatTerulet.setText(mintaAdatSztring(new Random(), 40, 50, 12));
            descSzamol.run();
        });
        calcBtn1.addActionListener(e -> descSzamol.run());
        descSzamol.run();

        vezerlo.add(bemenetKartya);
        vezerlo.add(Box.createVerticalStrut(14));
        vezerlo.add(eredmenyKartya);
        vezerlo.add(Box.createVerticalGlue());

        chartCim1 = new JLabel(I18n.t("stat.desc.chart_title"));
        chartCim1.setFont(Tema.SZOVEG_FONT);
        chartCim1.setForeground(Tema.HALVANY_SZOVEG);
        chartCim1.setBorder(new EmptyBorder(0, 0, 6, 0));
        JPanel jobbOldal = new JPanel(new BorderLayout());
        jobbOldal.setOpaque(false);
        jobbOldal.add(chartCim1, BorderLayout.NORTH);
        jobbOldal.add(grafikon, BorderLayout.CENTER);

        keret.add(new JScrollPane(vezerlo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.WEST);
        keret.add(jobbOldal, BorderLayout.CENTER);
        return keret;
    }

    private void rajzolHisztogram(Grafikon grafikon, double[] adat) {
        double minErtek = adat[0], maxErtek = adat[0];
        for (double v : adat) { minErtek = Math.min(minErtek, v); maxErtek = Math.max(maxErtek, v); }
        if (maxErtek - minErtek < 1e-9) { minErtek -= 1; maxErtek += 1; }

        int binSzam = Math.max(5, Math.min(16, (int) Math.ceil(Math.sqrt(adat.length))));
        double binSzelesseg = (maxErtek - minErtek) / binSzam;
        double[] kozepek = new double[binSzam];
        double[] magassagok = new double[binSzam];
        for (int i = 0; i < binSzam; i++) kozepek[i] = minErtek + (i + 0.5) * binSzelesseg;
        for (double v : adat) {
            int idx = (int) ((v - minErtek) / binSzelesseg);
            if (idx >= binSzam) idx = binSzam - 1;
            if (idx < 0) idx = 0;
            magassagok[idx]++;
        }
        double maxMagassag = 1;
        for (double m : magassagok) maxMagassag = Math.max(maxMagassag, m);

        grafikon.setAblak(minErtek - binSzelesseg, maxErtek + binSzelesseg, -maxMagassag * 0.05, maxMagassag * 1.2);
        grafikon.clear();
        grafikon.addOszlopok(kozepek, magassagok, binSzelesseg * 0.9, Tema.STATISZTIKA);
        grafikon.repaint();
    }

    // ================================================================ Korreláció / regresszió

    private JComponent regresszioFul() {
        JPanel keret = new JPanel(new BorderLayout(14, 14));
        keret.setOpaque(false);

        JPanel vezerlo = new JPanel();
        vezerlo.setLayout(new BoxLayout(vezerlo, BoxLayout.Y_AXIS));
        vezerlo.setOpaque(false);
        vezerlo.setPreferredSize(new Dimension(360, 10));

        JPanel kartya = Tema.kartya();
        kartya.setLayout(new BoxLayout(kartya, BoxLayout.Y_AXIS));
        kartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        regAlcim = Tema.alcim(I18n.t("stat.reg.title"));
        kartya.add(regAlcim);
        kartya.add(Box.createVerticalStrut(8));

        xCimke = new JLabel(I18n.t("stat.reg.x_label"));
        kartya.add(xCimke);
        JTextArea xTerulet = new JTextArea(3, 20);
        xTerulet.setFont(Tema.KEPLET_FONT.deriveFont(13f));
        xTerulet.setLineWrap(true);
        JScrollPane xScroll = new JScrollPane(xTerulet);
        xScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        xScroll.setMaximumSize(new Dimension(5000, 80));
        kartya.add(xScroll);

        kartya.add(Box.createVerticalStrut(8));
        yCimke = new JLabel(I18n.t("stat.reg.y_label"));
        kartya.add(yCimke);
        JTextArea yTerulet = new JTextArea(3, 20);
        yTerulet.setFont(Tema.KEPLET_FONT.deriveFont(13f));
        yTerulet.setLineWrap(true);
        JScrollPane yScroll = new JScrollPane(yTerulet);
        yScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        yScroll.setMaximumSize(new Dimension(5000, 80));
        kartya.add(yScroll);

        Random r = new Random(7);
        double[] mintaX = new double[16];
        double[] mintaY = new double[16];
        StringBuilder xsb = new StringBuilder(), ysb = new StringBuilder();
        for (int i = 0; i < mintaX.length; i++) {
            mintaX[i] = i + 1;
            mintaY[i] = 2.3 * mintaX[i] + 4 + (r.nextDouble() - 0.5) * 8;
            xsb.append(Math.round(mintaX[i])).append(i < mintaX.length - 1 ? ", " : "");
            ysb.append(String.format(java.util.Locale.ROOT, "%.1f", mintaY[i])).append(i < mintaX.length - 1 ? ", " : "");
        }
        xTerulet.setText(xsb.toString());
        yTerulet.setText(ysb.toString());

        JLabel hibaLabel = new JLabel(" ");
        hibaLabel.setForeground(new Color(200, 50, 50));
        hibaLabel.setFont(Tema.SZOVEG_FONT.deriveFont(12f));
        kartya.add(hibaLabel);

        calcBtn2 = Tema.gomb(I18n.t("btn.calculate"), Tema.STATISZTIKA);
        calcBtn2.setAlignmentX(Component.LEFT_ALIGNMENT);
        kartya.add(Box.createVerticalStrut(8));
        kartya.add(calcBtn2);

        JPanel eredmenyKartya = Tema.kartya();
        eredmenyKartya.setLayout(new BoxLayout(eredmenyKartya, BoxLayout.Y_AXIS));
        eredmenyKartya.setAlignmentX(Component.LEFT_ALIGNMENT);
        regEqAlcim = Tema.alcim(I18n.t("stat.reg.eq_title"));
        eredmenyKartya.add(regEqAlcim);
        eredmenyKartya.add(Box.createVerticalStrut(8));
        JLabel aLabel = ertekLabel(), bLabel = ertekLabel(), rLabel = ertekLabel();
        slopeCaption = new JLabel(I18n.t("stat.reg.slope_label"));
        interceptCaption = new JLabel(I18n.t("stat.reg.intercept_label"));
        corrCaption = new JLabel(I18n.t("stat.reg.corr_label"));
        eredmenyKartya.add(sorKiir(slopeCaption, aLabel));
        eredmenyKartya.add(sorKiir(interceptCaption, bLabel));
        eredmenyKartya.add(sorKiir(corrCaption, rLabel));
        explainLabel = new JLabel(I18n.t("stat.reg.explain"));
        explainLabel.setFont(Tema.SZOVEG_FONT.deriveFont(12f));
        explainLabel.setForeground(Tema.HALVANY_SZOVEG);
        eredmenyKartya.add(explainLabel);

        Grafikon grafikon = new Grafikon();

        regSzamol = () -> {
            hibaLabel.setText(" ");
            try {
                double[] xs = parszolAdat(xTerulet.getText());
                double[] ys = parszolAdat(yTerulet.getText());
                if (xs.length != ys.length) throw new IllegalArgumentException(I18n.t("err.xy_mismatch"));
                if (xs.length < 2) throw new IllegalArgumentException(I18n.t("err.need2_pairs"));

                double[] eredmeny = MatElemzes.linearisRegresszio(xs, ys);
                aLabel.setText(fmt(eredmeny[0]));
                bLabel.setText(fmt(eredmeny[1]));
                rLabel.setText(fmt(eredmeny[2]));

                double minX = xs[0], maxX = xs[0];
                for (double v : xs) { minX = Math.min(minX, v); maxX = Math.max(maxX, v); }
                double margo = (maxX - minX) * 0.15 + 1;
                grafikon.autoY(x -> eredmeny[0] * x + eredmeny[1], minX - margo, maxX + margo);
                grafikon.clear();
                grafikon.addEgyenes(eredmeny[0], eredmeny[1], Tema.STATISZTIKA);
                grafikon.addPontok(xs, ys, Tema.DERIVALAS);
                grafikon.repaint();
            } catch (Exception ex) {
                hibaLabel.setText(ex.getMessage());
            }
        };
        calcBtn2.addActionListener(e -> regSzamol.run());
        regSzamol.run();

        vezerlo.add(kartya);
        vezerlo.add(Box.createVerticalStrut(14));
        vezerlo.add(eredmenyKartya);
        vezerlo.add(Box.createVerticalGlue());

        keret.add(new JScrollPane(vezerlo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.WEST);
        keret.add(grafikon, BorderLayout.CENTER);
        return keret;
    }

    // ================================================================ segédek

    private static double[] parszolAdat(String szoveg) {
        String[] darabok = szoveg.trim().split("[\\s,;]+");
        java.util.List<Double> lista = new java.util.ArrayList<>();
        for (String d : darabok) {
            if (d.isEmpty()) continue;
            lista.add(Double.parseDouble(d.replace(',', '.')));
        }
        double[] eredmeny = new double[lista.size()];
        for (int i = 0; i < eredmeny.length; i++) eredmeny[i] = lista.get(i);
        return eredmeny;
    }

    private static String mintaAdatSztring(Random r, int n, double atlag, double szoras) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            double v = atlag + r.nextGaussian() * szoras;
            sb.append(String.format(java.util.Locale.ROOT, "%.1f", v));
            if (i < n - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private static String fmt(double v) {
        return String.format(I18n.szamLocale(), "%.3f", v);
    }

    private static JLabel ertekLabel() {
        JLabel l = new JLabel("-");
        l.setFont(Tema.KEPLET_FONT);
        l.setForeground(Tema.SZOVEG);
        return l;
    }

    private JPanel sorKiir(JLabel cimkeLabel, JLabel ertekLabel) {
        JPanel sor = new JPanel(new BorderLayout());
        sor.setOpaque(false);
        sor.setAlignmentX(Component.LEFT_ALIGNMENT);
        cimkeLabel.setFont(Tema.SZOVEG_FONT);
        sor.add(cimkeLabel, BorderLayout.WEST);
        sor.add(ertekLabel, BorderLayout.CENTER);
        return sor;
    }
}
