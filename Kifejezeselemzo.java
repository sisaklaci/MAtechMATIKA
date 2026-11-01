import java.util.*;

/**
 * Egyszerű rekurzív leszállós elemző (recursive descent parser), amely egy
 * x-ben felírt matematikai kifejezést (pl. "3*x^2 - sin(x)/2") alakít
 * Kifejezes-fává.
 *
 * Nyelvtan (lazán):
 *   kif    := tag (('+'|'-') tag)*
 *   tag    := hatvany (('*'|'/') hatvany | hatvany)*      (az utolsó: implicit szorzás, pl. "2x")
 *   hatvany:= unary ('^' hatvany)?                        (jobbra kötő)
 *   unary  := ('-'|'+') unary | elsodleges
 *   elsodleges := SZAM | 'x' | AZONOSITO '(' kif ')' | '(' kif ')'
 */
public class Kifejezeselemzo {

    private static final Set<String> FUGGVENYEK = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "exp", "ln", "log", "sqrt", "abs"));

    private final String szoveg;
    private int pos;

    private Kifejezeselemzo(String szoveg) {
        this.szoveg = szoveg;
        this.pos = 0;
    }

    public static Kifejezes elemez(String kifejezesSzoveg) {
        Kifejezeselemzo e = new Kifejezeselemzo(kifejezesSzoveg);
        e.szokoz();
        Kifejezes k = e.kif();
        e.szokoz();
        if (e.pos < e.szoveg.length()) {
            throw new Kifejezes.KifejezesHiba(I18n.f("err.unexpected_char", e.pos + 1, e.szoveg.charAt(e.pos)));
        }
        return k;
    }

    private void szokoz() {
        while (pos < szoveg.length() && Character.isWhitespace(szoveg.charAt(pos))) pos++;
    }

    private char aktualis() {
        return pos < szoveg.length() ? szoveg.charAt(pos) : '\0';
    }

    private boolean elfogad(char c) {
        szokoz();
        if (aktualis() == c) { pos++; return true; }
        return false;
    }

    private boolean kezdhetElsodlegest() {
        szokoz();
        char c = aktualis();
        return Character.isDigit(c) || c == '.' || c == '(' || Character.isLetter(c);
    }

    private Kifejezes kif() {
        Kifejezes bal = tag();
        while (true) {
            szokoz();
            if (elfogad('+')) {
                bal = new Kifejezes.Osszeg(bal, tag());
            } else if (elfogad('-')) {
                bal = new Kifejezes.Kulonbseg(bal, tag());
            } else {
                break;
            }
        }
        return bal;
    }

    private Kifejezes tag() {
        Kifejezes bal = hatvany();
        while (true) {
            szokoz();
            if (elfogad('*')) {
                bal = new Kifejezes.Szorzat(bal, hatvany());
            } else if (elfogad('/')) {
                bal = new Kifejezes.Hanyados(bal, hatvany());
            } else if (kezdhetElsodlegest()) {
                // implicit szorzás, pl. "2x" vagy "3sin(x)" vagy "2(x+1)"
                bal = new Kifejezes.Szorzat(bal, hatvany());
            } else {
                break;
            }
        }
        return bal;
    }

    private Kifejezes hatvany() {
        Kifejezes alap = unary();
        szokoz();
        if (elfogad('^')) {
            Kifejezes kitevo = hatvany(); // jobbra kötő
            return new Kifejezes.Hatvany(alap, kitevo);
        }
        return alap;
    }

    private Kifejezes unary() {
        szokoz();
        if (elfogad('-')) return new Kifejezes.Negalt(unary());
        if (elfogad('+')) return unary();
        return elsodleges();
    }

    private Kifejezes elsodleges() {
        szokoz();
        char c = aktualis();

        if (Character.isDigit(c) || c == '.') {
            int start = pos;
            while (pos < szoveg.length() && (Character.isDigit(szoveg.charAt(pos)) || szoveg.charAt(pos) == '.')) pos++;
            return new Kifejezes.Szam(Double.parseDouble(szoveg.substring(start, pos)));
        }

        if (c == '(') {
            pos++;
            Kifejezes belso = kif();
            szokoz();
            if (!elfogad(')')) throw new Kifejezes.KifejezesHiba(I18n.f("err.missing_close_paren", pos + 1));
            return belso;
        }

        if (Character.isLetter(c)) {
            int start = pos;
            while (pos < szoveg.length() && Character.isLetter(szoveg.charAt(pos))) pos++;
            String nev = szoveg.substring(start, pos).toLowerCase(Locale.ROOT);

            if (nev.equals("x")) return new Kifejezes.Valtozo();
            if (nev.equals("e") && !kovetkezoNyitoZarojel()) return new Kifejezes.Szam(Math.E);
            if (nev.equals("pi")) return new Kifejezes.Szam(Math.PI);

            String kanonikusNev = nev.equals("log") ? "ln" : nev;
            if (FUGGVENYEK.contains(nev)) {
                szokoz();
                if (!elfogad('(')) throw new Kifejezes.KifejezesHiba(I18n.f("err.expected_open_paren", nev, pos + 1));
                Kifejezes belso = kif();
                szokoz();
                if (!elfogad(')')) throw new Kifejezes.KifejezesHiba(I18n.f("err.missing_close_paren", pos + 1));
                return new Kifejezes.FuggvenyHivas(kanonikusNev, belso);
            }

            throw new Kifejezes.KifejezesHiba(I18n.f("err.unknown_identifier", nev));
        }

        if (c == '\0') throw new Kifejezes.KifejezesHiba(I18n.t("err.unexpected_end"));
        throw new Kifejezes.KifejezesHiba(I18n.f("err.unexpected_char2", pos + 1, c));
    }

    private boolean kovetkezoNyitoZarojel() {
        int p = pos;
        while (p < szoveg.length() && Character.isWhitespace(szoveg.charAt(p))) p++;
        return p < szoveg.length() && szoveg.charAt(p) == '(';
    }
}
