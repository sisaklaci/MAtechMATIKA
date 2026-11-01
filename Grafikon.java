import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * Újrafelhasználható derékszögű koordináta-rendszeres rajzvászon.
 * A négy modul (derivált, integrál, valószínűség, statisztika) mind ezt
 * használja: tengelyek+rács automatikusan rajzolódnak, a tartalmat pedig
 * "rétegekként" (Rajzelem) lehet hozzáadni, majd clear()-rel törölni.
 */
public class Grafikon extends JPanel {

    @FunctionalInterface
    public interface Rajzelem {
        void rajzol(Graphics2D g, Grafikon keret);
    }

    private double xMin = -10, xMax = 10, yMin = -10, yMax = 10;
    private final List<Rajzelem> elemek = new ArrayList<>();

    private static final int BAL_MARGO = 55;
    private static final int JOBB_MARGO = 20;
    private static final int FELSO_MARGO = 16;
    private static final int ALSO_MARGO = 38;

    public Grafikon() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 420));
    }

    public void setAblak(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin; this.xMax = xMax; this.yMin = yMin; this.yMax = yMax;
    }

    /** Automatikusan beállítja az y-tartományt a függvény [xMin,xMax]-on felvett értékei alapján. */
    public void autoY(DoubleUnaryOperator f, double xMin, double xMax) {
        this.xMin = xMin; this.xMax = xMax;
        double lo = Double.POSITIVE_INFINITY, hi = Double.NEGATIVE_INFINITY;
        for (int i = 0; i <= 200; i++) {
            double x = xMin + (xMax - xMin) * i / 200.0;
            double y;
            try { y = f.applyAsDouble(x); } catch (Exception e) { continue; }
            if (Double.isFinite(y)) { lo = Math.min(lo, y); hi = Math.max(hi, y); }
        }
        if (!Double.isFinite(lo) || !Double.isFinite(hi) || hi - lo < 1e-9) { lo = -5; hi = 5; }
        double tartalek = (hi - lo) * 0.15 + 0.3;
        this.yMin = lo - tartalek;
        this.yMax = hi + tartalek;
    }

    public void clear() { elemek.clear(); }
    public void add(Rajzelem e) { elemek.add(e); }

    public double getXMin() { return xMin; }
    public double getXMax() { return xMax; }
    public double getYMin() { return yMin; }
    public double getYMax() { return yMax; }

    // ---------------------------------------------------------- koordináta-transzformáció

    public double pxX(double x) {
        double szelesseg = getWidth() - BAL_MARGO - JOBB_MARGO;
        return BAL_MARGO + (x - xMin) / (xMax - xMin) * szelesseg;
    }

    public double pxY(double y) {
        double magassag = getHeight() - FELSO_MARGO - ALSO_MARGO;
        return FELSO_MARGO + (yMax - y) / (yMax - yMin) * magassag;
    }

    // ---------------------------------------------------------- kényelmi réteg-hozzáadók

    public void addFuggveny(DoubleUnaryOperator f, Color szin, float vastagsag) {
        add((g, kr) -> rajzolFuggveny(g, kr, f, szin, vastagsag));
    }

    public void addTerulet(DoubleUnaryOperator f, double a, double b, Color szin) {
        add((g, kr) -> rajzolTerulet(g, kr, f, a, b, szin));
    }

    public void addTeglalapok(DoubleUnaryOperator f, double a, double b, int n, String mod, Color szin) {
        add((g, kr) -> rajzolTeglalapok(g, kr, f, a, b, n, mod, szin));
    }

    public void addErintoegyenes(double x0, double y0, double meredekseg, Color szin) {
        add((g, kr) -> {
            double x1 = kr.xMin, x2 = kr.xMax;
            double y1 = y0 + meredekseg * (x1 - x0);
            double y2 = y0 + meredekseg * (x2 - x0);
            g.setColor(szin);
            g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{7f, 5f}, 0f));
            g.draw(new Line2D.Double(kr.pxX(x1), kr.pxY(y1), kr.pxX(x2), kr.pxY(y2)));
        });
    }

    public void addEgyenes(double meredekseg, double tengelymetszet, Color szin) {
        add((g, kr) -> {
            double y1 = meredekseg * kr.xMin + tengelymetszet;
            double y2 = meredekseg * kr.xMax + tengelymetszet;
            g.setColor(szin);
            g.setStroke(new BasicStroke(2.2f));
            g.draw(new Line2D.Double(kr.pxX(kr.xMin), kr.pxY(y1), kr.pxX(kr.xMax), kr.pxY(y2)));
        });
    }

    public void addPont(double x, double y, Color szin, int sugar) {
        add((g, kr) -> {
            g.setColor(szin);
            double px = kr.pxX(x), py = kr.pxY(y);
            g.fill(new Ellipse2D.Double(px - sugar, py - sugar, sugar * 2, sugar * 2));
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(new Ellipse2D.Double(px - sugar, py - sugar, sugar * 2, sugar * 2));
        });
    }

    /** Tört vonal (polyline) rajzolása adatpontokból - pl. szimuláció futó átlaga. */
    public void addVonal(double[] xs, double[] ys, Color szin, float vastagsag) {
        add((g, kr) -> {
            g.setColor(szin);
            g.setStroke(new BasicStroke(vastagsag, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D.Double ut = new Path2D.Double();
            for (int i = 0; i < xs.length; i++) {
                double px = kr.pxX(xs[i]), py = kr.pxY(ys[i]);
                if (i == 0) ut.moveTo(px, py); else ut.lineTo(px, py);
            }
            g.draw(ut);
        });
    }

    public void addPontok(double[] xs, double[] ys, Color szin) {
        add((g, kr) -> {
            g.setColor(szin);
            for (int i = 0; i < xs.length; i++) {
                double px = kr.pxX(xs[i]), py = kr.pxY(ys[i]);
                g.fill(new Ellipse2D.Double(px - 4, py - 4, 8, 8));
            }
        });
    }

    public void addFuggolegesVonal(double x, Color szin) {
        add((g, kr) -> {
            g.setColor(szin);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
            g.draw(new Line2D.Double(kr.pxX(x), kr.pxY(kr.yMin), kr.pxX(x), kr.pxY(kr.yMax)));
        });
    }

    /** Oszlopdiagram - középpontok és magasságok (pl. diszkrét eloszlás vagy hisztogram). */
    public void addOszlopok(double[] kozepek, double[] magassagok, double szelesseg, Color szin) {
        add((g, kr) -> {
            for (int i = 0; i < kozepek.length; i++) {
                double x0 = kozepek[i] - szelesseg / 2.0, x1 = kozepek[i] + szelesseg / 2.0;
                double py0 = kr.pxY(0), py1 = kr.pxY(magassagok[i]);
                double px0 = kr.pxX(x0), px1 = kr.pxX(x1);
                Rectangle2D.Double r = new Rectangle2D.Double(Math.min(px0, px1), Math.min(py0, py1), Math.abs(px1 - px0), Math.abs(py1 - py0));
                g.setColor(uj_alfa(szin, 170));
                g.fill(r);
                g.setColor(szin.darker());
                g.setStroke(new BasicStroke(1.2f));
                g.draw(r);
            }
        });
    }

    private static Color uj_alfa(Color c, int alfa) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), alfa); }

    // ---------------------------------------------------------- belső rajzoló-logika

    private void rajzolFuggveny(Graphics2D g, Grafikon kr, DoubleUnaryOperator f, Color szin, float vastagsag) {
        g.setColor(szin);
        g.setStroke(new BasicStroke(vastagsag, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int szelessegPx = getWidth() - BAL_MARGO - JOBB_MARGO;
        Double elozoPx = null, elozoPy = null;
        double hatarMagasErtek = Math.abs(yMax - yMin) * 60 + 1e6;
        for (int px = 0; px <= szelessegPx; px++) {
            double x = xMin + (xMax - xMin) * px / (double) szelessegPx;
            double y;
            try { y = f.applyAsDouble(x); } catch (Exception ex) { y = Double.NaN; }
            boolean ervenyes = Double.isFinite(y) && Math.abs(y) < hatarMagasErtek;
            if (ervenyes) {
                double cpx = BAL_MARGO + px, cpy = kr.pxY(y);
                if (elozoPx != null) {
                    g.draw(new Line2D.Double(elozoPx, elozoPy, cpx, cpy));
                }
                elozoPx = cpx; elozoPy = cpy;
            } else {
                elozoPx = null; elozoPy = null;
            }
        }
    }

    private void rajzolTerulet(Graphics2D g, Grafikon kr, DoubleUnaryOperator f, double a, double b, Color szin) {
        if (b < a) { double t = a; a = b; b = t; }
        int lepesek = Math.max(20, (int) (Math.abs(kr.pxX(b) - kr.pxX(a))));
        Path2D.Double ut = new Path2D.Double();
        boolean elso = true;
        for (int i = 0; i <= lepesek; i++) {
            double x = a + (b - a) * i / (double) lepesek;
            double y;
            try { y = f.applyAsDouble(x); } catch (Exception ex) { y = 0; }
            if (!Double.isFinite(y)) y = 0;
            double px = kr.pxX(x), py = kr.pxY(y);
            if (elso) { ut.moveTo(px, kr.pxY(0)); ut.lineTo(px, py); elso = false; }
            else ut.lineTo(px, py);
        }
        ut.lineTo(kr.pxX(b), kr.pxY(0));
        ut.closePath();
        g.setColor(uj_alfa(szin, 95));
        g.fill(ut);
        g.setColor(szin);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(ut);
    }

    private void rajzolTeglalapok(Graphics2D g, Grafikon kr, DoubleUnaryOperator f, double a, double b, int n, String mod, Color szin) {
        if (n <= 0) return;
        double szelesseg = (b - a) / n;
        for (int i = 0; i < n; i++) {
            double x0 = a + i * szelesseg, x1 = x0 + szelesseg;
            double xMinta;
            switch (mod) {
                case "jobb": xMinta = x1; break;
                case "kozep": xMinta = (x0 + x1) / 2.0; break;
                default: xMinta = x0;
            }
            double y;
            try { y = f.applyAsDouble(xMinta); } catch (Exception ex) { y = 0; }
            double px0 = kr.pxX(x0), px1 = kr.pxX(x1), py0 = kr.pxY(0), py1 = kr.pxY(y);
            Rectangle2D.Double r = new Rectangle2D.Double(Math.min(px0, px1), Math.min(py0, py1), Math.abs(px1 - px0), Math.abs(py1 - py0));
            g.setColor(uj_alfa(szin, 110));
            g.fill(r);
            g.setColor(szin.darker());
            g.setStroke(new BasicStroke(1f));
            g.draw(r);
        }
    }

    // ---------------------------------------------------------- tengelyek, rács

    private static double szepLepes(double tartomany, int kivantSzam) {
        if (tartomany <= 0) return 1;
        double nyers = tartomany / kivantSzam;
        double magnitudo = Math.pow(10, Math.floor(Math.log10(nyers)));
        double norm = nyers / magnitudo;
        double lepes;
        if (norm < 1.5) lepes = 1;
        else if (norm < 3) lepes = 2;
        else if (norm < 7) lepes = 5;
        else lepes = 10;
        return lepes * magnitudo;
    }

    private static String szamFormazas(double v) {
        if (Math.abs(v) < 1e-9) return "0";
        if (v == Math.floor(v) && Math.abs(v) < 1e9) return String.format(Locale.ROOT, "%d", (long) v);
        return String.format(Locale.ROOT, "%.2f", v);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int szel = getWidth(), mag = getHeight();

        // rács
        g.setColor(Tema.RACS);
        g.setStroke(new BasicStroke(1f));
        double xLepes = szepLepes(xMax - xMin, 10);
        double yLepes = szepLepes(yMax - yMin, 8);

        double xKezdet = Math.ceil(xMin / xLepes) * xLepes;
        for (double x = xKezdet; x <= xMax; x += xLepes) {
            double px = pxX(x);
            g.draw(new Line2D.Double(px, FELSO_MARGO, px, mag - ALSO_MARGO));
        }
        double yKezdet = Math.ceil(yMin / yLepes) * yLepes;
        for (double y = yKezdet; y <= yMax; y += yLepes) {
            double py = pxY(y);
            g.draw(new Line2D.Double(BAL_MARGO, py, szel - JOBB_MARGO, py));
        }

        // tengelyek
        g.setColor(Tema.TENGELY);
        g.setStroke(new BasicStroke(1.8f));
        if (yMin <= 0 && yMax >= 0) {
            double py0 = pxY(0);
            g.draw(new Line2D.Double(BAL_MARGO, py0, szel - JOBB_MARGO, py0));
        }
        if (xMin <= 0 && xMax >= 0) {
            double px0 = pxX(0);
            g.draw(new Line2D.Double(px0, FELSO_MARGO, px0, mag - ALSO_MARGO));
        }

        // címkék
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g.setColor(Tema.HALVANY_SZOVEG);
        for (double x = xKezdet; x <= xMax; x += xLepes) {
            if (Math.abs(x) < xLepes / 1000.0) continue;
            String s = szamFormazas(x);
            g.drawString(s, (float) (pxX(x) - g.getFontMetrics().stringWidth(s) / 2.0), (float) (mag - ALSO_MARGO + 15));
        }
        for (double y = yKezdet; y <= yMax; y += yLepes) {
            if (Math.abs(y) < yLepes / 1000.0) continue;
            String s = szamFormazas(y);
            g.drawString(s, (float) (BAL_MARGO - g.getFontMetrics().stringWidth(s) - 6), (float) (pxY(y) + 4));
        }

        // tartalom-rétegek
        Shape regiClip = g.getClip();
        g.setClip(BAL_MARGO, FELSO_MARGO, szel - BAL_MARGO - JOBB_MARGO, mag - FELSO_MARGO - ALSO_MARGO);
        for (Rajzelem e : elemek) e.rajzol(g, this);
        g.setClip(regiClip);

        // keret
        g.setColor(Tema.VONAL);
        g.setStroke(new BasicStroke(1f));
        g.draw(new Rectangle2D.Double(BAL_MARGO, FELSO_MARGO, szel - BAL_MARGO - JOBB_MARGO, mag - FELSO_MARGO - ALSO_MARGO));
    }
}
