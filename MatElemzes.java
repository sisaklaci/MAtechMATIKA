import java.util.function.DoubleUnaryOperator;

/** Numerikus és (korlátozott) szimbolikus matematikai segédfüggvények. */
public final class MatElemzes {
    private MatElemzes() {}

    // ================================================================ numerikus integrálás

    /** Simpson-szabály - pontos numerikus közelítés a határozott integrálra. */
    public static double szimpson(DoubleUnaryOperator f, double a, double b, int n) {
        if (n % 2 != 0) n++;
        if (n < 2) n = 2;
        double h = (b - a) / n;
        double osszeg = f.applyAsDouble(a) + f.applyAsDouble(b);
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            osszeg += f.applyAsDouble(x) * (i % 2 == 0 ? 2 : 4);
        }
        return osszeg * h / 3.0;
    }

    /** Riemann-összeg (bal/jobb/közép) - szemléltetéshez. */
    public static double riemann(DoubleUnaryOperator f, double a, double b, int n, String mod) {
        double szelesseg = (b - a) / n;
        double osszeg = 0;
        for (int i = 0; i < n; i++) {
            double x0 = a + i * szelesseg, x1 = x0 + szelesseg;
            double xMinta;
            switch (mod) {
                case "jobb": xMinta = x1; break;
                case "kozep": xMinta = (x0 + x1) / 2.0; break;
                default: xMinta = x0;
            }
            osszeg += f.applyAsDouble(xMinta) * szelesseg;
        }
        return osszeg;
    }

    // ================================================================ korlátozott szimbolikus antiderivált

    public static class NemTudjukKivetel extends Exception {
        public NemTudjukKivetel(String uzenet) { super(uzenet); }
    }

    /**
     * Egyszerű, tábla-alapú antiderivált-kereső: konstansok, x^n (n != -1),
     * sin, cos, exp, 1/x összegei és skalárszorosai - ennél bonyolultabb
     * kifejezésekhez nem ad szimbolikus eredményt (NemTudjukKivetel-t dob).
     */
    public static Kifejezes probalkozzAntiderivalt(Kifejezes k) throws NemTudjukKivetel {
        if (k instanceof Kifejezes.Szam) {
            double c = ((Kifejezes.Szam) k).ertek;
            return new Kifejezes.Szorzat(new Kifejezes.Szam(c), new Kifejezes.Valtozo());
        }
        if (k instanceof Kifejezes.Valtozo) {
            return new Kifejezes.Hanyados(new Kifejezes.Hatvany(new Kifejezes.Valtozo(), new Kifejezes.Szam(2)), new Kifejezes.Szam(2));
        }
        if (k instanceof Kifejezes.Osszeg) {
            Kifejezes.Osszeg o = (Kifejezes.Osszeg) k;
            return new Kifejezes.Osszeg(probalkozzAntiderivalt(o.bal), probalkozzAntiderivalt(o.jobb));
        }
        if (k instanceof Kifejezes.Kulonbseg) {
            Kifejezes.Kulonbseg o = (Kifejezes.Kulonbseg) k;
            return new Kifejezes.Kulonbseg(probalkozzAntiderivalt(o.bal), probalkozzAntiderivalt(o.jobb));
        }
        if (k instanceof Kifejezes.Negalt) {
            return new Kifejezes.Negalt(probalkozzAntiderivalt(((Kifejezes.Negalt) k).u));
        }
        if (k instanceof Kifejezes.Szorzat) {
            Kifejezes.Szorzat sz = (Kifejezes.Szorzat) k;
            if (sz.bal instanceof Kifejezes.Szam) return new Kifejezes.Szorzat(sz.bal, probalkozzAntiderivalt(sz.jobb));
            if (sz.jobb instanceof Kifejezes.Szam) return new Kifejezes.Szorzat(sz.jobb, probalkozzAntiderivalt(sz.bal));
            throw new NemTudjukKivetel(I18n.t("err.antideriv.product"));
        }
        if (k instanceof Kifejezes.Hatvany) {
            Kifejezes.Hatvany h = (Kifejezes.Hatvany) k;
            if (h.alap instanceof Kifejezes.Valtozo && h.kitevo instanceof Kifejezes.Szam) {
                double n = ((Kifejezes.Szam) h.kitevo).ertek;
                if (Math.abs(n + 1) < 1e-9) {
                    return new Kifejezes.FuggvenyHivas("ln", new Kifejezes.FuggvenyHivas("abs", new Kifejezes.Valtozo()));
                }
                return new Kifejezes.Hanyados(new Kifejezes.Hatvany(new Kifejezes.Valtozo(), new Kifejezes.Szam(n + 1)), new Kifejezes.Szam(n + 1));
            }
            throw new NemTudjukKivetel(I18n.t("err.antideriv.power"));
        }
        if (k instanceof Kifejezes.Hanyados) {
            Kifejezes.Hanyados h = (Kifejezes.Hanyados) k;
            if (h.bal instanceof Kifejezes.Szam && h.jobb instanceof Kifejezes.Valtozo) {
                double c = ((Kifejezes.Szam) h.bal).ertek;
                return new Kifejezes.Szorzat(new Kifejezes.Szam(c), new Kifejezes.FuggvenyHivas("ln", new Kifejezes.FuggvenyHivas("abs", new Kifejezes.Valtozo())));
            }
            throw new NemTudjukKivetel(I18n.t("err.antideriv.fraction"));
        }
        if (k instanceof Kifejezes.FuggvenyHivas) {
            Kifejezes.FuggvenyHivas f = (Kifejezes.FuggvenyHivas) k;
            if (f.u instanceof Kifejezes.Valtozo) {
                switch (f.nev) {
                    case "sin": return new Kifejezes.Negalt(new Kifejezes.FuggvenyHivas("cos", new Kifejezes.Valtozo()));
                    case "cos": return new Kifejezes.FuggvenyHivas("sin", new Kifejezes.Valtozo());
                    case "exp": return new Kifejezes.FuggvenyHivas("exp", new Kifejezes.Valtozo());
                }
            }
            throw new NemTudjukKivetel(I18n.t("err.antideriv.function"));
        }
        throw new NemTudjukKivetel(I18n.t("err.antideriv.unknown"));
    }

    // ================================================================ kombinatorika / valószínűség

    public static double faktorialis(int n) {
        double eredmeny = 1;
        for (int i = 2; i <= n; i++) eredmeny *= i;
        return eredmeny;
    }

    public static double kombinacio(int n, int k) {
        if (k < 0 || k > n) return 0;
        k = Math.min(k, n - k);
        double eredmeny = 1;
        for (int i = 0; i < k; i++) eredmeny = eredmeny * (n - i) / (i + 1);
        return Math.round(eredmeny * 1e6) / 1e6;
    }

    public static double permutacio(int n, int k) {
        if (k < 0 || k > n) return 0;
        double eredmeny = 1;
        for (int i = 0; i < k; i++) eredmeny *= (n - i);
        return eredmeny;
    }

    public static double binomialisPMF(int n, double p, int k) {
        return kombinacio(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    public static double poissonPMF(double lambda, int k) {
        return Math.exp(-lambda + k * Math.log(lambda) - logFaktorialis(k));
    }

    private static double logFaktorialis(int n) {
        double s = 0;
        for (int i = 2; i <= n; i++) s += Math.log(i);
        return s;
    }

    public static double normalisPDF(double x, double mu, double szigma) {
        double z = (x - mu) / szigma;
        return Math.exp(-0.5 * z * z) / (szigma * Math.sqrt(2 * Math.PI));
    }

    /** Standard normális eloszlásfüggvény (CDF), Abramowitz-Stegun közelítéssel. */
    public static double normalisCDF(double x, double mu, double szigma) {
        double z = (x - mu) / (szigma * Math.sqrt(2));
        return 0.5 * (1 + hibafuggveny(z));
    }

    private static double hibafuggveny(double x) {
        // erf(x) - Abramowitz-Stegun 7.1.26 közelítés, |hiba| < 1.5e-7
        double t = 1.0 / (1.0 + 0.3275911 * Math.abs(x));
        double y = 1.0 - (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t - 0.284496736) * t + 0.254829592) * t * Math.exp(-x * x);
        return x >= 0 ? y : -y;
    }

    // ================================================================ leíró statisztika

    public static double atlag(double[] adat) {
        double s = 0;
        for (double v : adat) s += v;
        return s / adat.length;
    }

    public static double median(double[] adat) {
        double[] rendezett = adat.clone();
        java.util.Arrays.sort(rendezett);
        int n = rendezett.length;
        return n % 2 == 1 ? rendezett[n / 2] : (rendezett[n / 2 - 1] + rendezett[n / 2]) / 2.0;
    }

    public static double szoras(double[] adat) {
        return Math.sqrt(variancia(adat));
    }

    public static double variancia(double[] adat) {
        double m = atlag(adat);
        double s = 0;
        for (double v : adat) s += (v - m) * (v - m);
        return s / (adat.length - 1);
    }

    public static double[] kvartilisek(double[] adat) {
        double[] r = adat.clone();
        java.util.Arrays.sort(r);
        return new double[]{percentilis(r, 0.25), percentilis(r, 0.5), percentilis(r, 0.75)};
    }

    private static double percentilis(double[] rendezett, double p) {
        double idx = p * (rendezett.length - 1);
        int alsó = (int) Math.floor(idx), felso = (int) Math.ceil(idx);
        if (alsó == felso) return rendezett[alsó];
        double frakcio = idx - alsó;
        return rendezett[alsó] * (1 - frakcio) + rendezett[felso] * frakcio;
    }

    /** Lineáris regresszió (legkisebb négyzetek): visszaadja [meredekség, tengelymetszet, r]. */
    public static double[] linearisRegresszio(double[] xs, double[] ys) {
        int n = xs.length;
        double atlagX = atlag(xs), atlagY = atlag(ys);
        double szamlalo = 0, nevezoX = 0, nevezoY = 0;
        for (int i = 0; i < n; i++) {
            double dx = xs[i] - atlagX, dy = ys[i] - atlagY;
            szamlalo += dx * dy;
            nevezoX += dx * dx;
            nevezoY += dy * dy;
        }
        double meredekseg = szamlalo / nevezoX;
        double tengelymetszet = atlagY - meredekseg * atlagX;
        double r = szamlalo / Math.sqrt(nevezoX * nevezoY);
        return new double[]{meredekseg, tengelymetszet, r};
    }
}
