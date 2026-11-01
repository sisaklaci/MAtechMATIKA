import java.util.function.DoubleUnaryOperator;

/**
 * Egyváltozós (x) matematikai kifejezések szintaxisfája.
 * Minden csomópont tud:
 *  - kiértékelni egy adott x helyen,
 *  - szimbolikusan deriválni (a deriválási szabályok szerint),
 *  - egyszerűsíteni (alapvető azonosságok: +0, *1, *0, konstans-összevonás),
 *  - szöveggé alakulni (zárójelezve, ha a precedencia megkívánja).
 */
public abstract class Kifejezes {

    public abstract double ertek(double x);

    public abstract Kifejezes derivalt();

    public abstract Kifejezes egyszerusit();

    /** Operátor-precedencia a zárójelezéshez (nagyobb szám = erősebben köt). */
    public abstract int precedencia();

    public DoubleUnaryOperator fuggveny() {
        return this::ertek;
    }

    protected String zarojelezve(Kifejezes gyerek, boolean ha_egyenlo_is) {
        String s = gyerek.toString();
        boolean kell = ha_egyenlo_is
                ? gyerek.precedencia() <= this.precedencia()
                : gyerek.precedencia() < this.precedencia();
        return kell ? "(" + s + ")" : s;
    }

    // ================================================================ Szám

    public static final class Szam extends Kifejezes {
        public final double ertek;

        public Szam(double ertek) { this.ertek = ertek; }

        @Override public double ertek(double x) { return ertek; }
        @Override public Kifejezes derivalt() { return new Szam(0); }
        @Override public Kifejezes egyszerusit() { return this; }
        @Override public int precedencia() { return 100; }

        @Override public String toString() {
            if (ertek == Math.floor(ertek) && !Double.isInfinite(ertek) && Math.abs(ertek) < 1e15) {
                return Long.toString((long) ertek);
            }
            return String.valueOf(ertek);
        }
    }

    // ================================================================ Változó (x)

    public static final class Valtozo extends Kifejezes {
        @Override public double ertek(double x) { return x; }
        @Override public Kifejezes derivalt() { return new Szam(1); }
        @Override public Kifejezes egyszerusit() { return this; }
        @Override public int precedencia() { return 100; }
        @Override public String toString() { return "x"; }
    }

    // ================================================================ Összeg

    public static final class Osszeg extends Kifejezes {
        public final Kifejezes bal, jobb;
        public Osszeg(Kifejezes bal, Kifejezes jobb) { this.bal = bal; this.jobb = jobb; }

        @Override public double ertek(double x) { return bal.ertek(x) + jobb.ertek(x); }
        @Override public Kifejezes derivalt() { return new Osszeg(bal.derivalt(), jobb.derivalt()); }
        @Override public int precedencia() { return 10; }

        @Override public Kifejezes egyszerusit() {
            Kifejezes b = bal.egyszerusit(), j = jobb.egyszerusit();
            if (b instanceof Szam && ((Szam) b).ertek == 0) return j;
            if (j instanceof Szam && ((Szam) j).ertek == 0) return b;
            if (b instanceof Szam && j instanceof Szam) return new Szam(((Szam) b).ertek + ((Szam) j).ertek);
            return new Osszeg(b, j);
        }

        @Override public String toString() { return zarojelezve(bal, false) + " + " + zarojelezve(jobb, false); }
    }

    // ================================================================ Különbség

    public static final class Kulonbseg extends Kifejezes {
        public final Kifejezes bal, jobb;
        public Kulonbseg(Kifejezes bal, Kifejezes jobb) { this.bal = bal; this.jobb = jobb; }

        @Override public double ertek(double x) { return bal.ertek(x) - jobb.ertek(x); }
        @Override public Kifejezes derivalt() { return new Kulonbseg(bal.derivalt(), jobb.derivalt()); }
        @Override public int precedencia() { return 10; }

        @Override public Kifejezes egyszerusit() {
            Kifejezes b = bal.egyszerusit(), j = jobb.egyszerusit();
            if (j instanceof Szam && ((Szam) j).ertek == 0) return b;
            if (b instanceof Szam && j instanceof Szam) return new Szam(((Szam) b).ertek - ((Szam) j).ertek);
            if (b instanceof Szam && ((Szam) b).ertek == 0) return new Negalt(j).egyszerusit();
            return new Kulonbseg(b, j);
        }

        @Override public String toString() { return zarojelezve(bal, false) + " - " + zarojelezve(jobb, true); }
    }

    // ================================================================ Szorzat

    public static final class Szorzat extends Kifejezes {
        public final Kifejezes bal, jobb;
        public Szorzat(Kifejezes bal, Kifejezes jobb) { this.bal = bal; this.jobb = jobb; }

        @Override public double ertek(double x) { return bal.ertek(x) * jobb.ertek(x); }
        @Override public Kifejezes derivalt() {
            return new Osszeg(new Szorzat(bal.derivalt(), jobb), new Szorzat(bal, jobb.derivalt()));
        }
        @Override public int precedencia() { return 20; }

        @Override public Kifejezes egyszerusit() {
            Kifejezes b = bal.egyszerusit(), j = jobb.egyszerusit();
            if ((b instanceof Szam && ((Szam) b).ertek == 0) || (j instanceof Szam && ((Szam) j).ertek == 0)) return new Szam(0);
            if (b instanceof Szam && ((Szam) b).ertek == 1) return j;
            if (j instanceof Szam && ((Szam) j).ertek == 1) return b;
            if (b instanceof Szam && j instanceof Szam) return new Szam(((Szam) b).ertek * ((Szam) j).ertek);
            return new Szorzat(b, j);
        }

        @Override public String toString() { return zarojelezve(bal, false) + " * " + zarojelezve(jobb, false); }
    }

    // ================================================================ Hányados

    public static final class Hanyados extends Kifejezes {
        public final Kifejezes bal, jobb;
        public Hanyados(Kifejezes bal, Kifejezes jobb) { this.bal = bal; this.jobb = jobb; }

        @Override public double ertek(double x) { return bal.ertek(x) / jobb.ertek(x); }
        @Override public Kifejezes derivalt() {
            Kifejezes szamlalo = new Kulonbseg(new Szorzat(bal.derivalt(), jobb), new Szorzat(bal, jobb.derivalt()));
            Kifejezes nevezo = new Hatvany(jobb, new Szam(2));
            return new Hanyados(szamlalo, nevezo);
        }
        @Override public int precedencia() { return 20; }

        @Override public Kifejezes egyszerusit() {
            Kifejezes b = bal.egyszerusit(), j = jobb.egyszerusit();
            if (b instanceof Szam && ((Szam) b).ertek == 0) return new Szam(0);
            if (j instanceof Szam && ((Szam) j).ertek == 1) return b;
            if (b instanceof Szam && j instanceof Szam && ((Szam) j).ertek != 0) return new Szam(((Szam) b).ertek / ((Szam) j).ertek);
            return new Hanyados(b, j);
        }

        @Override public String toString() { return zarojelezve(bal, false) + " / " + zarojelezve(jobb, true); }
    }

    // ================================================================ Hatvány (alap^kitevő)

    public static final class Hatvany extends Kifejezes {
        public final Kifejezes alap, kitevo;
        public Hatvany(Kifejezes alap, Kifejezes kitevo) { this.alap = alap; this.kitevo = kitevo; }

        @Override public double ertek(double x) { return Math.pow(alap.ertek(x), kitevo.ertek(x)); }
        @Override public int precedencia() { return 30; }

        @Override public Kifejezes derivalt() {
            if (kitevo instanceof Szam) {
                // hatványszabály: (u^n)' = n * u^(n-1) * u'
                double n = ((Szam) kitevo).ertek;
                Kifejezes uNminus1 = new Hatvany(alap, new Szam(n - 1));
                return new Szorzat(new Szorzat(new Szam(n), uNminus1), alap.derivalt());
            }
            // általános eset: (u^v)' = u^v * (v' * ln(u) + v * u'/u)
            Kifejezes lnU = new FuggvenyHivas("ln", alap);
            Kifejezes tag1 = new Szorzat(kitevo.derivalt(), lnU);
            Kifejezes tag2 = new Szorzat(kitevo, new Hanyados(alap.derivalt(), alap));
            return new Szorzat(this, new Osszeg(tag1, tag2));
        }

        @Override public Kifejezes egyszerusit() {
            Kifejezes a = alap.egyszerusit(), k = kitevo.egyszerusit();
            if (k instanceof Szam && ((Szam) k).ertek == 1) return a;
            if (k instanceof Szam && ((Szam) k).ertek == 0) return new Szam(1);
            if (a instanceof Szam && k instanceof Szam) return new Szam(Math.pow(((Szam) a).ertek, ((Szam) k).ertek));
            return new Hatvany(a, k);
        }

        @Override public String toString() { return zarojelezve(alap, true) + "^" + zarojelezve(kitevo, true); }
    }

    // ================================================================ Negálás (-u)

    public static final class Negalt extends Kifejezes {
        public final Kifejezes u;
        public Negalt(Kifejezes u) { this.u = u; }

        @Override public double ertek(double x) { return -u.ertek(x); }
        @Override public Kifejezes derivalt() { return new Negalt(u.derivalt()); }
        @Override public int precedencia() { return 25; }

        @Override public Kifejezes egyszerusit() {
            Kifejezes b = u.egyszerusit();
            if (b instanceof Szam) return new Szam(-((Szam) b).ertek);
            if (b instanceof Negalt) return ((Negalt) b).u;
            return new Negalt(b);
        }

        @Override public String toString() { return "-" + zarojelezve(u, true); }
    }

    // ================================================================ Függvényhívás: sin, cos, tan, exp, ln, sqrt, abs

    public static final class FuggvenyHivas extends Kifejezes {
        public final String nev;
        public final Kifejezes u;

        public FuggvenyHivas(String nev, Kifejezes u) { this.nev = nev; this.u = u; }

        @Override public double ertek(double x) {
            double v = u.ertek(x);
            switch (nev) {
                case "sin": return Math.sin(v);
                case "cos": return Math.cos(v);
                case "tan": return Math.tan(v);
                case "exp": return Math.exp(v);
                case "ln": return Math.log(v);
                case "sqrt": return Math.sqrt(v);
                case "abs": return Math.abs(v);
                default: throw new KifejezesHiba(I18n.f("err.unknown_function", nev));
            }
        }

        @Override public Kifejezes derivalt() {
            Kifejezes kulsoDerivalt;
            switch (nev) {
                case "sin": kulsoDerivalt = new FuggvenyHivas("cos", u); break;
                case "cos": kulsoDerivalt = new Negalt(new FuggvenyHivas("sin", u)); break;
                case "tan": kulsoDerivalt = new Hanyados(new Szam(1), new Hatvany(new FuggvenyHivas("cos", u), new Szam(2))); break;
                case "exp": kulsoDerivalt = new FuggvenyHivas("exp", u); break;
                case "ln": kulsoDerivalt = new Hanyados(new Szam(1), u); break;
                case "sqrt": kulsoDerivalt = new Hanyados(new Szam(1), new Szorzat(new Szam(2), new FuggvenyHivas("sqrt", u))); break;
                case "abs": kulsoDerivalt = new Hanyados(u, new FuggvenyHivas("abs", u)); break;
                default: throw new KifejezesHiba(I18n.f("err.unknown_function", nev));
            }
            return new Szorzat(kulsoDerivalt, u.derivalt());
        }

        @Override public Kifejezes egyszerusit() { return new FuggvenyHivas(nev, u.egyszerusit()); }
        @Override public int precedencia() { return 100; }
        @Override public String toString() { return nev + "(" + u.toString() + ")"; }
    }

    public static final class KifejezesHiba extends RuntimeException {
        public KifejezesHiba(String uzenet) { super(uzenet); }
    }
}
