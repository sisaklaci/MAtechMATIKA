import javax.swing.Icon;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

/** Nyelv-azonosítók: az enum sorrendje rögzíti az oszlopok sorrendjét a fordítási táblában. */
enum Lang {
    HU("Magyar"), EN("English"), DE("Deutsch"), ES("Español"), FR("Français"), PL("Polski"), ZH("中文"), JA("日本語");

    final String nev;

    Lang(String nev) { this.nev = nev; }
}

/** Egyszerű kulcs alapú fordítási tár, futásidőben váltható nyelvvel, elmentve a következő induláshoz. */
final class I18n {
    private I18n() { }

    static Lang nyelv = betoltottNyelv();

    private static Lang betoltottNyelv() {
        try {
            return Lang.valueOf(Preferences.userNodeForPackage(I18n.class).get("nyelv", Lang.HU.name()));
        } catch (Exception ignored) {
            return Lang.HU;
        }
    }

    static void valt(Lang uj) {
        nyelv = uj;
        try {
            Preferences.userNodeForPackage(I18n.class).put("nyelv", uj.name());
        } catch (Exception ignored) {
            // a nyelv ettől még az aktuális futásban érvényes marad, csak nem marad meg újraindítás után
        }
    }

    /** A tizedesjel nyelvenként: angolban, kínaiban és japánban pont, a többi nálunk is vessző-konvenciójú. */
    static Locale szamLocale() {
        return (nyelv == Lang.EN || nyelv == Lang.ZH || nyelv == Lang.JA) ? Locale.US : new Locale("hu", "HU");
    }

    /** Lefordított szöveg az aktuális nyelven; ismeretlen kulcsnál a kulcs magát adja vissza. */
    static String t(String kulcs) {
        String[] r = T.get(kulcs);
        return r == null ? kulcs : r[nyelv.ordinal()];
    }

    /** Mint t(), de %s / %d helyőrzőkkel behelyettesítve. */
    static String f(String kulcs, Object... args) {
        return String.format(t(kulcs), args);
    }

    // ================================================================ fordítási tábla
    // kulcs|magyar|angol|német|spanyol|francia|lengyel|kínai|japán

    private static final String TABLA = """
            app.title|MAtechMATIKA - interaktív matematika-oktató|MAtechMATIKA - interactive math tutor|MAtechMATIKA - interaktiver Mathe-Tutor|MAtechMATIKA - tutor interactivo de matemáticas|MAtechMATIKA - tuteur de mathématiques interactif|MAtechMATIKA - interaktywny nauczyciel matematyki|MAtechMATIKA - 互动数学教学软件|MAtechMATIKA - インタラクティブ数学学習アプリ
            app.subtitle|matematika - látványosan|math - visually|Mathematik - anschaulich|matemáticas - de forma visual|les mathématiques - en images|matematyka - w obrazach|数学 - 直观呈现|数学を、目で見てわかる

            nav.home|⌂  Főoldal|⌂  Home|⌂  Startseite|⌂  Inicio|⌂  Accueil|⌂  Strona główna|⌂  首页|⌂  ホーム
            nav.derivative|d/dx  Deriválás|d/dx  Derivatives|d/dx  Ableitung|d/dx  Derivadas|d/dx  Dérivation|d/dx  Różniczkowanie|d/dx  导数|d/dx  微分
            nav.integral|∫  Integrálás|∫  Integrals|∫  Integration|∫  Integrales|∫  Intégration|∫  Całkowanie|∫  积分|∫  積分
            nav.probability|%  Valószínűség|%  Probability|%  Wahrscheinlichkeit|%  Probabilidad|%  Probabilité|%  Prawdopodobieństwo|%  概率|%  確率
            nav.statistics|Σ  Statisztika|Σ  Statistics|Σ  Statistik|Σ  Estadística|Σ  Statistique|Σ  Statystyka|Σ  统计|Σ  統計
            nav.footer|Oktatási célú szemléltető program.|Educational demonstration program.|Lehrprogramm zu Demonstrationszwecken.|Programa educativo de demostración.|Logiciel de démonstration à but pédagogique.|Program edukacyjny o charakterze demonstracyjnym.|用于教学演示的程序。|教育目的のデモ用プログラムです。
            nav.lang_label|Nyelv|Language|Sprache|Idioma|Langue|Język|语言|言語

            module.derivative.title|Deriválás|Derivatives|Ableitung|Derivadas|Dérivation|Różniczkowanie|导数|微分
            module.integral.title|Integrálás|Integrals|Integration|Integrales|Intégration|Całkowanie|积分|積分
            module.probability.title_full|Valószínűségszámítás|Probability Theory|Wahrscheinlichkeitsrechnung|Cálculo de probabilidades|Calcul des probabilités|Rachunek prawdopodobieństwa|概率论|確率論
            module.probability.title_short|Valószínűség|Probability|Wahrscheinlichkeit|Probabilidad|Probabilité|Prawdopodobieństwo|概率|確率
            module.statistics.title|Statisztika|Statistics|Statistik|Estadística|Statistique|Statystyka|统计|統計

            home.title|Üdvözöl a MAtechMATIKA!|Welcome to MAtechMATIKA!|Willkommen bei MAtechMATIKA!|¡Bienvenido a MAtechMATIKA!|Bienvenue dans MAtechMATIKA !|Witamy w MAtechMATIKA!|欢迎使用 MAtechMATIKA!|MAtechMATIKA へようこそ!
            home.desc|<html><div style='width:900px'>Ez egy interaktív matematika-oktató alkalmazás, amely négy témakört dolgoz fel szemléletes, kipróbálható ábrákkal: <b>deriválás</b>, <b>integrálás</b>, <b>valószínűségszámítás</b> és <b>statisztika</b>. Válassz egy témát a bal oldali menüből, vagy kattints az alábbi kártyák egyikére!</div></html>|<html><div style='width:900px'>This is an interactive math tutoring application that covers four topics with illustrative, hands-on diagrams: <b>derivatives</b>, <b>integrals</b>, <b>probability theory</b> and <b>statistics</b>. Choose a topic from the menu on the left, or click one of the cards below!</div></html>|<html><div style='width:900px'>Dies ist eine interaktive Mathe-Lernanwendung, die vier Themenbereiche mit anschaulichen, ausprobierbaren Diagrammen behandelt: <b>Ableitung</b>, <b>Integration</b>, <b>Wahrscheinlichkeitsrechnung</b> und <b>Statistik</b>. Wähle ein Thema aus dem Menü links, oder klicke auf eine der Karten unten!</div></html>|<html><div style='width:900px'>Esta es una aplicación interactiva de enseñanza de matemáticas que trata cuatro temas con gráficos ilustrativos e interactivos: <b>derivadas</b>, <b>integrales</b>, <b>cálculo de probabilidades</b> y <b>estadística</b>. Elige un tema en el menú de la izquierda o haz clic en una de las tarjetas de abajo.</div></html>|<html><div style='width:900px'>Il s'agit d'une application interactive d'enseignement des mathématiques qui aborde quatre thèmes à l'aide de schémas illustratifs et interactifs : la <b>dérivation</b>, l'<b>intégration</b>, le <b>calcul des probabilités</b> et la <b>statistique</b>. Choisissez un thème dans le menu de gauche, ou cliquez sur l'une des cartes ci-dessous !</div></html>|<html><div style='width:900px'>To interaktywna aplikacja do nauki matematyki, która obejmuje cztery tematy za pomocą obrazowych, interaktywnych wykresów: <b>różniczkowanie</b>, <b>całkowanie</b>, <b>rachunek prawdopodobieństwa</b> i <b>statystykę</b>. Wybierz temat z menu po lewej stronie lub kliknij jedną z poniższych kart!</div></html>|<html><div style='width:900px'>这是一款互动式数学教学应用程序,通过直观、可操作的图形展示四个主题:<b>导数</b>、<b>积分</b>、<b>概率论</b>和<b>统计</b>。请从左侧菜单选择一个主题,或点击下方的卡片之一!</div></html>|<html><div style='width:900px'>このアプリは、わかりやすく実際に操作できる図を使って4つのテーマを学べるインタラクティブな数学学習アプリです:<b>微分</b>、<b>積分</b>、<b>確率論</b>、<b>統計</b>。左側のメニューからテーマを選ぶか、下のカードのいずれかをクリックしてください!</div></html>
            home.card.derivative.desc|A pillanatnyi változási sebesség és az érintő vizsgálata - élő grafikonnal.|Exploring the instantaneous rate of change and the tangent line - with a live graph.|Untersuchung der momentanen Änderungsrate und der Tangente - mit Live-Grafik.|Estudio de la tasa de cambio instantánea y de la recta tangente, con un gráfico en vivo.|Étude du taux de variation instantané et de la tangente - avec un graphique en direct.|Badanie chwilowej szybkości zmian i stycznej - z wykresem na żywo.|探索瞬时变化率与切线 - 配有实时图形。|瞬間の変化率と接線をライブグラフで調べます。
            home.card.integral.desc|A görbe alatti terület: Riemann-téglalapoktól a pontos integrálig.|The area under the curve: from Riemann rectangles to the exact integral.|Die Fläche unter der Kurve: von Riemann-Rechtecken bis zum exakten Integral.|El área bajo la curva: de los rectángulos de Riemann a la integral exacta.|L'aire sous la courbe : des rectangles de Riemann à l'intégrale exacte.|Pole pod krzywą: od prostokątów Riemanna do dokładnej całki.|曲线下的面积:从黎曼矩形到精确积分。|曲線の下の面積:リーマン長方形から正確な積分まで。
            home.card.probability.desc|Kombinatorika, binomiális és normális eloszlás, szimulációk.|Combinatorics, binomial and normal distributions, simulations.|Kombinatorik, Binomial- und Normalverteilung, Simulationen.|Combinatoria, distribuciones binomial y normal, simulaciones.|Combinatoire, distributions binomiale et normale, simulations.|Kombinatoryka, rozkład dwumianowy i normalny, symulacje.|组合数学、二项分布与正态分布、模拟实验。|組み合わせ論、二項分布と正規分布、シミュレーション。
            home.card.statistics.desc|Leíró statisztika, hisztogram, korreláció és regresszió.|Descriptive statistics, histogram, correlation and regression.|Beschreibende Statistik, Histogramm, Korrelation und Regression.|Estadística descriptiva, histograma, correlación y regresión.|Statistique descriptive, histogramme, corrélation et régression.|Statystyka opisowa, histogram, korelacja i regresja.|描述性统计、直方图、相关与回归分析。|記述統計、ヒストグラム、相関と回帰。

            label.fx|f(x) =|f(x) =|f(x) =|f(x) =|f(x) =|f(x) =|f(x) =|f(x) =
            label.a|a =|a =|a =|a =|a =|a =|a =|a =
            label.b|b =|b =|b =|b =|b =|b =|b =|b =
            label.n|n =|n =|n =|n =|n =|n =|n =|n =
            label.k|k =|k =|k =|k =|k =|k =|k =|k =
            btn.plot|Ábrázolás|Plot|Darstellen|Graficar|Tracer|Narysuj|绘图|描画
            btn.calculate|Számítás|Calculate|Berechnen|Calcular|Calculer|Oblicz|计算|計算
            label.result|Eredmény|Result|Ergebnis|Resultado|Résultat|Wynik|结果|結果

            deriv.desc|<html>A derivált egy függvény <b>pillanatnyi változási sebességét</b> (meredekségét) adja meg minden pontban. Írj be egy függvényt, és nézd meg, hogyan simul az érintő a görbéhez, miközben mozgatod a pontot!</html>|<html>The derivative gives the <b>instantaneous rate of change</b> (slope) of a function at every point. Enter a function and see how the tangent line hugs the curve as you move the point!</html>|<html>Die Ableitung gibt die <b>momentane Änderungsrate</b> (Steigung) einer Funktion an jedem Punkt an. Gib eine Funktion ein und beobachte, wie sich die Tangente an die Kurve anschmiegt, während du den Punkt bewegst!</html>|<html>La derivada indica la <b>tasa de cambio instantánea</b> (pendiente) de una función en cada punto. Introduce una función y observa cómo la recta tangente se ajusta a la curva mientras mueves el punto.</html>|<html>La dérivée donne le <b>taux de variation instantané</b> (la pente) d'une fonction en chaque point. Saisissez une fonction et observez comment la tangente épouse la courbe lorsque vous déplacez le point !</html>|<html>Pochodna wyraża <b>chwilową szybkość zmian</b> (nachylenie) funkcji w każdym punkcie. Wpisz funkcję i zobacz, jak styczna przylega do krzywej podczas przesuwania punktu!</html>|<html>导数描述的是一个函数在每一点的<b>瞬时变化率</b>(斜率)。输入一个函数,移动该点,观察切线如何贴合曲线!</html>|<html>導関数は、関数の各点における<b>瞬間の変化率</b>(傾き)を表します。関数を入力し、点を動かしながら接線が曲線にどのように沿っていくかを見てみましょう!</html>
            deriv.fx_eq|f(x) = |f(x) = |f(x) = |f(x) = |f(x) = |f(x) = |f(x) = |f(x) =
            deriv.fprime_eq|f'(x) = |f'(x) = |f'(x) = |f'(x) = |f'(x) = |f'(x) = |f'(x) = |f'(x) =
            deriv.tangent.title|Érintő az x₀ pontban|Tangent line at point x₀|Tangente im Punkt x₀|Tangente en el punto x₀|Tangente au point x₀|Styczna w punkcie x₀|在 x₀ 点处的切线|x₀ における接線
            deriv.x0|x₀ = %s|x₀ = %s|x₀ = %s|x₀ = %s|x₀ = %s|x₀ = %s|x₀ = %s|x₀ = %s
            deriv.slope_prefix|meredekség = |slope = |Steigung = |pendiente = |pente = |nachylenie = |斜率 = |傾き =

            rule.sum_diff|<b>Összeg-/különbségszabály:</b> tagonként deriválunk, az eredményeket pedig összeadjuk/kivonjuk.|<b>Sum/difference rule:</b> differentiate term by term, then add/subtract the results.|<b>Summen-/Differenzregel:</b> wir leiten gliedweise ab und addieren/subtrahieren dann die Ergebnisse.|<b>Regla de la suma/diferencia:</b> derivamos término a término y luego sumamos/restamos los resultados.|<b>Règle de la somme/différence :</b> on dérive terme à terme, puis on additionne/soustrait les résultats.|<b>Zasada sumy/różnicy:</b> różniczkujemy wyraz po wyrazie, a następnie dodajemy/odejmujemy wyniki.|<b>和差法则:</b> 逐项求导,然后将结果相加或相减。|<b>和・差の法則:</b> 各項を個別に微分し、その結果を足したり引いたりします。
            rule.product|<b>Szorzatszabály:</b> (f·g)' = f'·g + f·g'|<b>Product rule:</b> (f·g)' = f'·g + f·g'|<b>Produktregel:</b> (f·g)' = f'·g + f·g'|<b>Regla del producto:</b> (f·g)' = f'·g + f·g'|<b>Règle du produit :</b> (f·g)' = f'·g + f·g'|<b>Zasada iloczynu:</b> (f·g)' = f'·g + f·g'|<b>乘积法则:</b> (f·g)' = f'·g + f·g'|<b>積の法則:</b> (f·g)' = f'·g + f·g'
            rule.quotient|<b>Hányadosszabály:</b> (f/g)' = (f'·g − f·g') / g²|<b>Quotient rule:</b> (f/g)' = (f'·g − f·g') / g²|<b>Quotientenregel:</b> (f/g)' = (f'·g − f·g') / g²|<b>Regla del cociente:</b> (f/g)' = (f'·g − f·g') / g²|<b>Règle du quotient :</b> (f/g)' = (f'·g − f·g') / g²|<b>Zasada ilorazu:</b> (f/g)' = (f'·g − f·g') / g²|<b>商法则:</b> (f/g)' = (f'·g − f·g') / g²|<b>商の法則:</b> (f/g)' = (f'·g − f·g') / g²
            rule.power|<b>Hatványszabály:</b> (xⁿ)' = n·xⁿ⁻¹|<b>Power rule:</b> (xⁿ)' = n·xⁿ⁻¹|<b>Potenzregel:</b> (xⁿ)' = n·xⁿ⁻¹|<b>Regla de la potencia:</b> (xⁿ)' = n·xⁿ⁻¹|<b>Règle de la puissance :</b> (xⁿ)' = n·xⁿ⁻¹|<b>Zasada potęgi:</b> (xⁿ)' = n·xⁿ⁻¹|<b>幂法则:</b> (xⁿ)' = n·xⁿ⁻¹|<b>べき乗の法則:</b> (xⁿ)' = n·xⁿ⁻¹
            rule.power_chain|<b>Hatványszabály + láncszabály:</b> az alap maga is függvény, ezért a belső deriváltjával is szorozni kell.|<b>Power rule + chain rule:</b> the base is itself a function, so we must also multiply by its derivative.|<b>Potenzregel + Kettenregel:</b> die Basis ist selbst eine Funktion, daher muss auch mit ihrer Ableitung multipliziert werden.|<b>Regla de la potencia + regla de la cadena:</b> la base es a su vez una función, así que también hay que multiplicar por su derivada.|<b>Règle de la puissance + règle de la chaîne :</b> la base est elle-même une fonction, il faut donc aussi multiplier par sa dérivée.|<b>Zasada potęgi + zasada łańcuchowa:</b> podstawa jest sama funkcją, więc trzeba też pomnożyć przez jej pochodną.|<b>幂法则 + 链式法则:</b> 底数本身也是一个函数,因此还需要乘以它的导数。|<b>べき乗の法則+連鎖律:</b> 底自体も関数であるため、その導関数も掛ける必要があります。
            rule.basic_function|<b>Alapderivált:</b> a(z) %s(x) jól ismert deriváltját használjuk.|<b>Basic derivative:</b> we use the well-known derivative of %s(x).|<b>Grundableitung:</b> wir verwenden die bekannte Ableitung von %s(x).|<b>Derivada básica:</b> usamos la conocida derivada de %s(x).|<b>Dérivée de base :</b> on utilise la dérivée bien connue de %s(x).|<b>Podstawowa pochodna:</b> używamy dobrze znanej pochodnej %s(x).|<b>基本导数:</b> 我们使用 %s(x) 的常见导数公式。|<b>基本の導関数:</b> %s(x) のよく知られた導関数を使用します。
            rule.chain|<b>Láncszabály:</b> (f(g(x)))' = f'(g(x))·g'(x) - a külső és belső függvényt is deriváljuk.|<b>Chain rule:</b> (f(g(x)))' = f'(g(x))·g'(x) - we differentiate both the outer and the inner function.|<b>Kettenregel:</b> (f(g(x)))' = f'(g(x))·g'(x) - wir leiten sowohl die äußere als auch die innere Funktion ab.|<b>Regla de la cadena:</b> (f(g(x)))' = f'(g(x))·g'(x) - derivamos tanto la función externa como la interna.|<b>Règle de la chaîne :</b> (f(g(x)))' = f'(g(x))·g'(x) - on dérive à la fois la fonction externe et la fonction interne.|<b>Zasada łańcuchowa:</b> (f(g(x)))' = f'(g(x))·g'(x) - różniczkujemy zarówno funkcję zewnętrzną, jak i wewnętrzną.|<b>链式法则:</b> (f(g(x)))' = f'(g(x))·g'(x) - 需要同时对外层函数和内层函数求导。|<b>連鎖律:</b> (f(g(x)))' = f'(g(x))·g'(x) - 外側の関数と内側の関数の両方を微分します。
            rule.basic_const|Alapderivált: konstans deriváltja 0, x deriváltja 1.|Basic derivative: the derivative of a constant is 0, the derivative of x is 1.|Grundableitung: die Ableitung einer Konstante ist 0, die Ableitung von x ist 1.|Derivada básica: la derivada de una constante es 0, la derivada de x es 1.|Dérivée de base : la dérivée d'une constante est 0, la dérivée de x est 1.|Podstawowa pochodna: pochodna stałej wynosi 0, pochodna x wynosi 1.|基本导数:常数的导数为 0,x 的导数为 1。|基本の導関数:定数の導関数は0、xの導関数は1です。

            integ.desc|<html>A határozott integrál a görbe <b>alatti területet</b> méri. Figyeld meg, hogy minél több téglalapot használunk, a közelítő összeg annál közelebb kerül a pontos területhez!</html>|<html>The definite integral measures the <b>area under the curve</b>. Notice that the more rectangles we use, the closer the approximating sum gets to the exact area!</html>|<html>Das bestimmte Integral misst die <b>Fläche unter der Kurve</b>. Beobachte, dass die Näherungssumme der exakten Fläche immer näher kommt, je mehr Rechtecke wir verwenden!</html>|<html>La integral definida mide el <b>área bajo la curva</b>. Observa que cuantos más rectángulos usemos, más se acerca la suma aproximada al área exacta.</html>|<html>L'intégrale définie mesure l'<b>aire sous la courbe</b>. Observez que plus on utilise de rectangles, plus la somme approchée se rapproche de l'aire exacte !</html>|<html>Całka oznaczona mierzy <b>pole pod krzywą</b>. Zauważ, że im więcej prostokątów użyjemy, tym bardziej suma przybliżona zbliża się do dokładnego pola!</html>|<html>定积分衡量的是<b>曲线下的面积</b>。可以发现,使用的矩形越多,近似和就越接近精确的面积!</html>|<html>定積分は<b>曲線の下の面積</b>を測るものです。長方形の数を増やすほど、近似の合計が正確な面積に近づくことに注目してください!</html>
            integ.mode.left|bal végpont|left endpoint|linker Endpunkt|extremo izquierdo|borne gauche|lewy punkt|左端点|左端点
            integ.mode.right|jobb végpont|right endpoint|rechter Endpunkt|extremo derecho|borne droite|prawy punkt|右端点|右端点
            integ.mode.mid|középpont|midpoint|Mittelpunkt|punto medio|point milieu|punkt środkowy|中点|中点
            integ.riemann.title|Riemann-közelítés|Riemann approximation|Riemann-Näherung|Aproximación de Riemann|Approximation de Riemann|Przybliżenie Riemanna|黎曼近似|リーマン近似
            integ.rect_count|Téglalapok száma: n = %d|Number of rectangles: n = %d|Anzahl der Rechtecke: n = %d|Número de rectángulos: n = %d|Nombre de rectangles : n = %d|Liczba prostokątów: n = %d|矩形数量:n = %d|長方形の数:n = %d
            integ.approx_prefix|közelítő összeg ≈ |approximating sum ≈ |Näherungssumme ≈ |suma aproximada ≈ |somme approchée ≈ |suma przybliżona ≈ |近似和 ≈ |近似の合計 ≈
            integ.exact.title|Pontos eredmény|Exact result|Exaktes Ergebnis|Resultado exacto|Résultat exact|Dokładny wynik|精确结果|正確な結果
            integ.exact_prefix|∫ f(x)dx ≈ |∫ f(x)dx ≈ |∫ f(x)dx ≈ |∫ f(x)dx ≈ |∫ f(x)dx ≈ |∫ f(x)dx ≈ |∫ f(x)dx ≈ |∫ f(x)dx ≈
            integ.antideriv.label|Antiderivált F(x):|Antiderivative F(x):|Stammfunktion F(x):|Antiderivada F(x):|Primitive F(x) :|Funkcja pierwotna F(x):|原函数 F(x):|原始関数 F(x):
            integ.antideriv.suffix| + C| + C| + C| + C| + C| + C| + C| + C
            integ.antideriv.none|<html><i>(ehhez a függvényhez a program egyszerű táblázata nem talált zárt alakot - csak numerikus eredmény áll rendelkezésre)</i></html>|<html><i>(the program's simple lookup table found no closed form for this function - only the numerical result is available)</i></html>|<html><i>(für diese Funktion hat die einfache Tabelle des Programms keine geschlossene Form gefunden - nur das numerische Ergebnis ist verfügbar)</i></html>|<html><i>(la sencilla tabla del programa no encontró una forma cerrada para esta función; solo está disponible el resultado numérico)</i></html>|<html><i>(le tableau simplifié du programme n'a trouvé aucune forme explicite pour cette fonction - seul le résultat numérique est disponible)</i></html>|<html><i>(dla tej funkcji prosta tabela programu nie znalazła postaci zwartej - dostępny jest tylko wynik numeryczny)</i></html>|<html><i>(程序的简易查表未能为该函数找到封闭形式 - 仅提供数值结果)</i></html>|<html><i>(この関数については、プログラムの簡易対応表では閉じた形の式が見つかりませんでした - 数値結果のみ利用可能です)</i></html>
            err.b_gt_a|b-nek nagyobbnak kell lennie, mint a.|b must be greater than a.|b muss größer als a sein.|b debe ser mayor que a.|b doit être supérieur à a.|b musi być większe niż a.|b 必须大于 a。|b は a より大きくなければなりません。

            prob.desc|<html>Mennyi esély van rá? A valószínűségszámítás a bizonytalan események törvényszerűségeit írja le - a kombinatorikától a nevezetes eloszlásokig.</html>|<html>What are the odds? Probability theory describes the regularities of uncertain events - from combinatorics to the well-known distributions.</html>|<html>Wie wahrscheinlich ist es? Die Wahrscheinlichkeitsrechnung beschreibt die Gesetzmäßigkeiten unsicherer Ereignisse - von der Kombinatorik bis zu den bekannten Verteilungen.</html>|<html>¿Cuáles son las probabilidades? El cálculo de probabilidades describe las regularidades de los eventos incertos - desde la combinatoria hasta las distribuciones más conocidas.</html>|<html>Quelles sont les chances ? Le calcul des probabilités décrit les régularités des événements incertains - de la combinatoire aux distributions célèbres.</html>|<html>Jakie są szanse? Rachunek prawdopodobieństwa opisuje prawidłowości zdarzeń niepewnych - od kombinatoryki do znanych rozkładów.</html>|<html>机会有多大?概率论描述的是不确定事件的规律 - 从组合数学到各种经典分布。</html>|<html>可能性はどれくらい?確率論は、不確かな事象の規則性を記述します - 組み合わせ論から有名な分布まで。</html>
            prob.tab.combinatorics|Kombinatorika|Combinatorics|Kombinatorik|Combinatoria|Combinatoire|Kombinatoryka|组合数学|組み合わせ論
            prob.tab.binomial|Binomiális eloszlás|Binomial distribution|Binomialverteilung|Distribución binomial|Distribution binomiale|Rozkład dwumianowy|二项分布|二項分布
            prob.tab.normal|Normális eloszlás|Normal distribution|Normalverteilung|Distribución normal|Distribution normale|Rozkład normalny|正态分布|正規分布
            prob.tab.simulation|Szimuláció|Simulation|Simulation|Simulación|Simulation|Symulacja|模拟|シミュレーション
            prob.comb.title|n elem, k kiválasztva|n elements, k chosen|n Elemente, k ausgewählt|n elementos, k elegidos|n éléments, k choisis|n elementów, k wybranych|n 个元素中选取 k 个|n 個の要素から k 個を選ぶ
            prob.comb.factorial|n! (permutáció, n elemből)|n! (permutations of n elements)|n! (Permutationen von n Elementen)|n! (permutaciones de n elementos)|n! (permutations de n éléments)|n! (permutacje n elementów)|n!(n 个元素的排列)|n!(n 個の要素の順列)
            prob.comb.permutation|P(n,k) = n!/(n−k)!  (ismétlés nélküli variáció)|P(n,k) = n!/(n−k)!  (permutations without repetition)|P(n,k) = n!/(n−k)!  (Variationen ohne Wiederholung)|P(n,k) = n!/(n−k)!  (variaciones sin repetición)|P(n,k) = n!/(n−k)!  (arrangements sans répétition)|P(n,k) = n!/(n−k)!  (wariacje bez powtórzeń)|P(n,k) = n!/(n−k)!(无重复排列)|P(n,k) = n!/(n−k)!(重複のない順列)
            prob.comb.combination|C(n,k) = n!/(k!(n−k)!)  (kombináció)|C(n,k) = n!/(k!(n−k)!)  (combinations)|C(n,k) = n!/(k!(n−k)!)  (Kombinationen)|C(n,k) = n!/(k!(n−k)!)  (combinaciones)|C(n,k) = n!/(k!(n−k)!)  (combinaisons)|C(n,k) = n!/(k!(n−k)!)  (kombinacje)|C(n,k) = n!/(k!(n−k)!)(组合)|C(n,k) = n!/(k!(n−k)!)(組み合わせ)
            prob.comb.chart_title|C(n, 0..n) - a Pascal-háromszög n-edik sora|C(n, 0..n) - the n-th row of Pascal's triangle|C(n, 0..n) - die n-te Zeile des Pascal'schen Dreiecks|C(n, 0..n) - la n-ésima fila del triángulo de Pascal|C(n, 0..n) - la n-ième ligne du triangle de Pascal|C(n, 0..n) - n-ty wiersz trójkąta Pascala|C(n, 0..n) - 帕斯卡三角形的第 n 行|C(n, 0..n) - パスカルの三角形の n 段目
            prob.binom.title|B(n, p): n kísérlet, p siker-valószínűség|B(n, p): n trials, p probability of success|B(n, p): n Versuche, p Erfolgswahrscheinlichkeit|B(n, p): n ensayos, p probabilidad de éxito|B(n, p) : n essais, p probabilité de succès|B(n, p): n prób, p prawdopodobieństwo sukcesu|B(n, p):n 次试验,成功概率为 p|B(n, p):試行回数 n、成功確率 p
            prob.binom.n|n = %d|n = %d|n = %d|n = %d|n = %d|n = %d|n = %d|n = %d
            prob.binom.p|p = %s|p = %s|p = %s|p = %s|p = %s|p = %s|p = %s|p = %s
            prob.binom.expected|Várható érték  E[X] = n·p|Expected value  E[X] = n·p|Erwartungswert  E[X] = n·p|Valor esperado  E[X] = n·p|Espérance  E[X] = n·p|Wartość oczekiwana  E[X] = n·p|期望值  E[X] = n·p|期待値  E[X] = n·p
            prob.binom.stddev|Szórás  σ = √(n·p·(1−p))|Standard deviation  σ = √(n·p·(1−p))|Standardabweichung  σ = √(n·p·(1−p))|Desviación típica  σ = √(n·p·(1−p))|Écart type  σ = √(n·p·(1−p))|Odchylenie standardowe  σ = √(n·p·(1−p))|标准差  σ = √(n·p·(1−p))|標準偏差  σ = √(n·p·(1−p))
            prob.normal.title|N(μ, σ) - haranggörbe|N(μ, σ) - bell curve|N(μ, σ) - Glockenkurve|N(μ, σ) - curva de campana|N(μ, σ) - courbe en cloche|N(μ, σ) - krzywa dzwonowa|N(μ, σ) - 钟形曲线|N(μ, σ) - 釣鐘曲線
            prob.normal.mu|μ = %s|μ = %s|μ = %s|μ = %s|μ = %s|μ = %s|μ = %s|μ = %s
            prob.normal.sigma|σ = %s|σ = %s|σ = %s|σ = %s|σ = %s|σ = %s|σ = %s|σ = %s
            prob.normal.range_title|P(a < X < b)|P(a < X < b)|P(a < X < b)|P(a < X < b)|P(a < X < b)|P(a < X < b)|P(a < X < b)|P(a < X < b)
            prob.normal.prob_prefix|valószínűség ≈|probability ≈|Wahrscheinlichkeit ≈|probabilidad ≈|probabilité ≈|prawdopodobieństwo ≈|概率 ≈|確率 ≈
            prob.sim.title|A nagy számok törvénye|The law of large numbers|Das Gesetz der großen Zahlen|La ley de los grandes números|La loi des grands nombres|Prawo wielkich liczb|大数定律|大数の法則
            prob.sim.desc|<html>Dobj fel egy (esetleg cinkelt) érmét sokszor egymás után. Figyeld meg, hogy a fejek aránya egyre jobban megközelíti az elméleti valószínűséget, ahogy nő a dobások száma!</html>|<html>Flip a (possibly biased) coin many times in a row. Notice how the proportion of heads gets closer and closer to the theoretical probability as the number of flips grows!</html>|<html>Wirf eine (vielleicht gezinkte) Münze viele Male nacheinander. Beobachte, wie sich der Anteil von Kopf immer mehr der theoretischen Wahrscheinlichkeit annähert, je mehr Würfe es gibt!</html>|<html>Lanza una moneda (posiblemente trucada) muchas veces seguidas. Observa cómo la proporción de caras se acerca cada vez más a la probabilidad teórica a medida que aumenta el número de lanzamientos.</html>|<html>Lancez une pièce (peut-être truquée) de nombreuses fois de suite. Observez comment la proportion de faces se rapproche de plus en plus de la probabilité théorique à mesure que le nombre de lancers augmente !</html>|<html>Rzuć (możliwie nieuczciwą) monetą wiele razy z rzędu. Zauważ, jak udział orłów coraz bardziej zbliża się do teoretycznego prawdopodobieństwa, gdy liczba rzutów rośnie!</html>|<html>连续多次抛掷一枚(可能不均匀的)硬币。观察随着抛掷次数增加,正面出现的比例是如何逐渐接近理论概率的!</html>|<html>(偏りがあるかもしれない)コインを何度も連続して投げてみましょう。投げる回数が増えるにつれて、表が出る割合が理論上の確率にどんどん近づいていく様子を観察してください!</html>
            prob.sim.p|elméleti valószínűség p = %s|theoretical probability p = %s|theoretische Wahrscheinlichkeit p = %s|probabilidad teórica p = %s|probabilité théorique p = %s|teoretyczne prawdopodobieństwo p = %s|理论概率 p = %s|理論上の確率 p = %s
            prob.sim.count_label|Dobások száma:|Number of flips:|Anzahl der Würfe:|Número de lanzamientos:|Nombre de lancers :|Liczba rzutów:|抛掷次数:|投げる回数:
            prob.sim.start_btn|Szimuláció indítása|Start simulation|Simulation starten|Iniciar simulación|Démarrer la simulation|Uruchom symulację|开始模拟|シミュレーション開始
            prob.sim.result_label|fejek aránya a végén:|final proportion of heads:|Kopf-Anteil am Ende:|proporción final de caras:|proportion finale de faces :|udział orłów na końcu:|最终正面比例:|最終的な表の割合:

            stat.desc|<html>A statisztika adatokból von le következtetéseket. Írj be saját adatokat, vagy generálj véletlen mintát, és nézd meg az eloszlás alakját, illetve két változó kapcsolatát!</html>|<html>Statistics draws conclusions from data. Enter your own data or generate a random sample, and see the shape of the distribution and the relationship between two variables!</html>|<html>Die Statistik zieht Schlüsse aus Daten. Gib eigene Daten ein oder erzeuge eine Zufallsstichprobe und betrachte die Form der Verteilung sowie den Zusammenhang zwischen zwei Variablen!</html>|<html>La estadística extrae conclusiones a partir de datos. Introduce tus propios datos o genera una muestra aleatoria, y observa la forma de la distribución y la relación entre dos variables.</html>|<html>La statistique tire des conclusions à partir de données. Saisissez vos propres données ou générez un échantillon aléatoire, et observez la forme de la distribution ainsi que la relation entre deux variables !</html>|<html>Statystyka wyciąga wnioski z danych. Wpisz własne dane lub wygeneruj losową próbę i zobacz kształt rozkładu oraz związek między dwiema zmiennymi!</html>|<html>统计学从数据中得出结论。输入你自己的数据,或生成一组随机样本,观察分布的形状以及两个变量之间的关系!</html>|<html>統計学はデータから結論を導き出します。自分のデータを入力するか、ランダムなサンプルを生成して、分布の形や2つの変数の関係を見てみましょう!</html>
            stat.tab.descriptive|Leíró statisztika|Descriptive statistics|Beschreibende Statistik|Estadística descriptiva|Statistique descriptive|Statystyka opisowa|描述性统计|記述統計
            stat.tab.regression|Korreláció és regresszió|Correlation and regression|Korrelation und Regression|Correlación y regresión|Corrélation et régression|Korelacja i regresja|相关与回归|相関と回帰
            stat.desc.data_label|Adatok (szóközzel/vesszővel elválasztva)|Data (separated by spaces/commas)|Daten (durch Leerzeichen/Kommas getrennt)|Datos (separados por espacios o comas)|Données (séparées par des espaces/virgules)|Dane (oddzielone spacjami/przecinkami)|数据(用空格/逗号分隔)|データ(スペースまたはカンマで区切る)
            stat.desc.random_btn|Új véletlen minta (40 elem)|New random sample (40 values)|Neue Zufallsstichprobe (40 Werte)|Nueva muestra aleatoria (40 valores)|Nouvel échantillon aléatoire (40 valeurs)|Nowa losowa próba (40 wartości)|新的随机样本(40 个数值)|新しいランダムサンプル(40個)
            stat.desc.count|Elemszám (n)|Sample size (n)|Stichprobenumfang (n)|Tamaño de la muestra (n)|Taille de l'échantillon (n)|Liczba elementów (n)|样本量 (n)|データ数 (n)
            stat.desc.mean|Átlag|Mean|Mittelwert|Media|Moyenne|Średnia|平均值|平均
            stat.desc.median|Medián|Median|Median|Mediana|Médiane|Mediana|中位数|中央値
            stat.desc.stddev|Szórás|Standard deviation|Standardabweichung|Desviación típica|Écart type|Odchylenie standardowe|标准差|標準偏差
            stat.desc.variance|Szórásnégyzet|Variance|Varianz|Varianza|Variance|Wariancja|方差|分散
            stat.desc.min|Minimum|Minimum|Minimum|Mínimo|Minimum|Minimum|最小值|最小値
            stat.desc.max|Maximum|Maximum|Maximum|Máximo|Maximum|Maksimum|最大值|最大値
            stat.desc.range|Terjedelem|Range|Spannweite|Rango|Étendue|Rozstęp|极差|範囲
            stat.desc.chart_title|Hisztogram|Histogram|Histogramm|Histograma|Histogramme|Histogram|直方图|ヒストグラム
            err.need2_data|Legalább 2 adat kell.|At least 2 data points are required.|Es werden mindestens 2 Datenpunkte benötigt.|Se necesitan al menos 2 datos.|Il faut au moins 2 données.|Potrzeba co najmniej 2 danych.|至少需要 2 个数据。|少なくとも2つのデータが必要です。
            stat.reg.title|Két változó: x és y|Two variables: x and y|Zwei Variablen: x und y|Dos variables: x e y|Deux variables : x et y|Dwie zmienne: x i y|两个变量:x 和 y|2つの変数:x と y
            stat.reg.x_label|x adatok:|x data:|x-Daten:|datos de x:|données x :|dane x:|x 数据:|x データ:
            stat.reg.y_label|y adatok:|y data:|y-Daten:|datos de y:|données y :|dane y:|y 数据:|y データ:
            stat.reg.eq_title|Regressziós egyenes: y = a·x + b|Regression line: y = a·x + b|Regressionsgerade: y = a·x + b|Recta de regresión: y = a·x + b|Droite de régression : y = a·x + b|Linia regresji: y = a·x + b|回归直线:y = a·x + b|回帰直線:y = a·x + b
            stat.reg.slope_label|meredekség (a):|slope (a):|Steigung (a):|pendiente (a):|pente (a) :|nachylenie (a):|斜率 (a):|傾き (a):
            stat.reg.intercept_label|tengelymetszet (b):|intercept (b):|Achsenabschnitt (b):|ordenada en el origen (b):|ordonnée à l'origine (b) :|wyraz wolny (b):|截距 (b):|切片 (b):
            stat.reg.corr_label|korrelációs együttható (r):|correlation coefficient (r):|Korrelationskoeffizient (r):|coeficiente de correlación (r):|coefficient de corrélation (r) :|współczynnik korelacji (r):|相关系数 (r):|相関係数 (r):
            stat.reg.explain|<html><br>Az <b>r</b> ∈ [−1, 1] azt mutatja, mennyire szoros a lineáris kapcsolat: 1 közelében erős pozitív, −1 közelében erős negatív, 0 közelében nincs lineáris kapcsolat.</html>|<html><br>The value <b>r</b> ∈ [−1, 1] shows how strong the linear relationship is: near 1 it's a strong positive relationship, near −1 a strong negative one, and near 0 there is no linear relationship.</html>|<html><br>Der Wert <b>r</b> ∈ [−1, 1] zeigt, wie eng der lineare Zusammenhang ist: nahe 1 ein starker positiver, nahe −1 ein starker negativer, nahe 0 kein linearer Zusammenhang.</html>|<html><br>El valor <b>r</b> ∈ [−1, 1] indica cuán estrecha es la relación lineal: cerca de 1 una relación positiva fuerte, cerca de −1 una relación negativa fuerte, y cerca de 0 ninguna relación lineal.</html>|<html><br>La valeur <b>r</b> ∈ [−1, 1] indique à quel point la relation linéaire est étroite : proche de 1, une forte relation positive ; proche de −1, une forte relation négative ; proche de 0, aucune relation linéaire.</html>|<html><br>Wartość <b>r</b> ∈ [−1, 1] pokazuje, jak silny jest związek liniowy: w pobliżu 1 silny związek dodatni, w pobliżu −1 silny związek ujemny, a w pobliżu 0 brak związku liniowego.</html>|<html><br><b>r</b> ∈ [−1, 1] 的值表示线性关系的紧密程度:接近 1 表示强正相关,接近 −1 表示强负相关,接近 0 则表示没有线性关系。</html>|<html><br><b>r</b> ∈ [−1, 1] の値は線形関係の強さを示します:1に近いほど強い正の関係、−1に近いほど強い負の関係、0に近いと線形関係はありません。</html>
            err.xy_mismatch|Az x és y lista hossza nem egyezik.|The x and y lists have different lengths.|Die Listen x und y haben unterschiedliche Längen.|Las listas de x e y no tienen la misma longitud.|Les listes x et y n'ont pas la même longueur.|Listy x i y mają różną długość.|x 和 y 列表的长度不一致。|x と y のリストの長さが一致しません。
            err.need2_pairs|Legalább 2 adatpár kell.|At least 2 data pairs are required.|Es werden mindestens 2 Datenpaare benötigt.|Se necesitan al menos 2 pares de datos.|Il faut au moins 2 paires de données.|Potrzeba co najmniej 2 par danych.|至少需要 2 对数据。|少なくとも2組のデータが必要です。

            err.unknown_function|Ismeretlen függvény: %s|Unknown function: %s|Unbekannte Funktion: %s|Función desconocida: %s|Fonction inconnue : %s|Nieznana funkcja: %s|未知函数:%s|不明な関数:%s
            err.unexpected_char|Váratlan karakter a(z) %d. pozíción: '%s'|Unexpected character at position %d: '%s'|Unerwartetes Zeichen an Position %d: '%s'|Carácter inesperado en la posición %d: '%s'|Caractère inattendu à la position %d : « %s »|Nieoczekiwany znak na pozycji %d: '%s'|第 %d 个位置出现意外字符:'%s'|%d 文字目に予期しない文字があります:「%s」
            err.missing_close_paren|Hiányzó záró zárójel a(z) %d. pozíción.|Missing closing parenthesis at position %d.|Fehlende schließende Klammer an Position %d.|Falta el paréntesis de cierre en la posición %d.|Parenthèse fermante manquante à la position %d.|Brak zamykającego nawiasu na pozycji %d.|第 %d 个位置缺少右括号。|%d 文字目に閉じ括弧がありません。
            err.expected_open_paren|'%s' után '(' várt a(z) %d. pozíción.|Expected '(' after '%s' at position %d.|Nach '%s' wurde an Position %d '(' erwartet.|Se esperaba '(' después de '%s' en la posición %d.|« ( » attendu après « %s » à la position %d.|Po '%s' oczekiwano '(' na pozycji %d.|在 '%s' 之后、第 %d 个位置应为 '('。|「%s」の後、%d 文字目に「(」が必要です。
            err.unknown_identifier|Ismeretlen azonosító: '%s' (csak 'x', és a sin/cos/tan/exp/ln/sqrt/abs függvények megengedettek).|Unknown identifier: '%s' (only 'x' and the functions sin/cos/tan/exp/ln/sqrt/abs are allowed).|Unbekannter Bezeichner: '%s' (erlaubt sind nur 'x' sowie die Funktionen sin/cos/tan/exp/ln/sqrt/abs).|Identificador desconocido: '%s' (solo se permiten 'x' y las funciones sin/cos/tan/exp/ln/sqrt/abs).|Identifiant inconnu : « %s » (seuls 'x' et les fonctions sin/cos/tan/exp/ln/sqrt/abs sont autorisés).|Nieznany identyfikator: '%s' (dozwolone jest tylko 'x' oraz funkcje sin/cos/tan/exp/ln/sqrt/abs).|未知标识符:'%s'(只允许使用 'x' 以及 sin/cos/tan/exp/ln/sqrt/abs 函数)。|不明な識別子:「%s」('x' および sin/cos/tan/exp/ln/sqrt/abs 関数のみ使用できます)。
            err.unexpected_end|Váratlanul véget ért a kifejezés.|The expression ended unexpectedly.|Der Ausdruck endete unerwartet.|La expresión terminó inesperadamente.|L'expression s'est terminée de manière inattendue.|Wyrażenie nieoczekiwanie się zakończyło.|表达式意外结束。|式が予期せず終了しました。
            err.unexpected_char2|Nem várt karakter a(z) %d. pozíción: '%s'|Unexpected character at position %d: '%s'|Unerwartetes Zeichen an Position %d: '%s'|Carácter inesperado en la posición %d: '%s'|Caractère inattendu à la position %d : « %s »|Nieoczekiwany znak na pozycji %d: '%s'|第 %d 个位置出现意外字符:'%s'|%d 文字目に予期しない文字があります:「%s」
            err.antideriv.product|Két, x-et tartalmazó tényező szorzatához (szorzat-szabály fordítottja) nincs egyszerű képlet.|There's no simple formula for a product of two factors that both contain x (reverse product rule).|Für ein Produkt zweier x-haltiger Faktoren (umgekehrte Produktregel) gibt es keine einfache Formel.|No existe una fórmula sencilla para el producto de dos factores que contienen x (regla del producto inversa).|Il n'existe pas de formule simple pour un produit de deux facteurs contenant x (règle du produit inversée).|Dla iloczynu dwóch czynników zawierających x (odwrócona zasada iloczynu) nie ma prostego wzoru.|对于两个都含有 x 的因子的乘积(乘积法则的逆运算),没有简单的公式。|x を含む2つの因数の積(積の法則の逆)には単純な公式がありません。
            err.antideriv.power|Ehhez a hatványkifejezéshez nincs egyszerű (tábla-alapú) antiderivált.|There is no simple (table-based) antiderivative for this power expression.|Für diesen Potenzausdruck gibt es keine einfache (tabellenbasierte) Stammfunktion.|No existe una antiderivada sencilla (basada en tablas) para esta expresión de potencia.|Il n'existe pas de primitive simple (basée sur une table) pour cette expression de puissance.|Dla tego wyrażenia potęgowego nie ma prostej (tabelarycznej) funkcji pierwotnej.|该幂表达式没有简单的(基于查表的)原函数。|このべき乗の式には単純な(表に基づく)原始関数がありません。
            err.antideriv.fraction|Ehhez a törtkifejezéshez nincs egyszerű (tábla-alapú) antiderivált.|There is no simple (table-based) antiderivative for this fractional expression.|Für diesen Bruchausdruck gibt es keine einfache (tabellenbasierte) Stammfunktion.|No existe una antiderivada sencilla (basada en tablas) para esta expresión fraccionaria.|Il n'existe pas de primitive simple (basée sur une table) pour cette expression fractionnaire.|Dla tego wyrażenia w postaci ułamka nie ma prostej (tabelarycznej) funkcji pierwotnej.|该分式表达式没有简单的(基于查表的)原函数。|この分数式には単純な(表に基づく)原始関数がありません。
            err.antideriv.function|Ehhez a függvényhez nincs egyszerű (tábla-alapú) antiderivált.|There is no simple (table-based) antiderivative for this function.|Für diese Funktion gibt es keine einfache (tabellenbasierte) Stammfunktion.|No existe una antiderivada sencilla (basada en tablas) para esta función.|Il n'existe pas de primitive simple (basée sur une table) pour cette fonction.|Dla tej funkcji nie ma prostej (tabelarycznej) funkcji pierwotnej.|该函数没有简单的(基于查表的)原函数。|この関数には単純な(表に基づく)原始関数がありません。
            err.antideriv.unknown|Ismeretlen kifejezéstípus.|Unknown expression type.|Unbekannter Ausdruckstyp.|Tipo de expresión desconocido.|Type d'expression inconnu.|Nieznany typ wyrażenia.|未知的表达式类型。|不明な式の型です。
            """;

    private static final Map<String, String[]> T = new HashMap<>();

    static {
        for (String sor : TABLA.split("\n")) {
            sor = sor.strip();
            if (sor.isEmpty() || sor.startsWith("#")) continue;
            String[] r = sor.split("\\|", -1);
            if (r.length != 9) throw new IllegalStateException("Hibás fordítási sor (" + r.length + " mező): " + sor);
            T.put(r[0], Arrays.copyOfRange(r, 1, 9));
        }
    }

    // ================================================================ zászlóikon

    /** Egyszerűsített zászlóikon (3:2 képarány) a nyelvválasztó gombokhoz. */
    static class ZaszloIkon implements Icon {
        static final int SZ = 20, M = 14;
        private final Lang nyelv;

        ZaszloIkon(Lang nyelv) { this.nyelv = nyelv; }

        @Override public int getIconWidth() { return SZ; }
        @Override public int getIconHeight() { return M; }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);
            switch (nyelv) {
                case HU: savok(g, new Color(206, 29, 39), Color.WHITE, new Color(67, 111, 78)); break;
                case DE: savok(g, Color.BLACK, new Color(222, 0, 40), new Color(255, 206, 0)); break;
                case PL: savok(g, Color.WHITE, new Color(220, 20, 60)); break;
                case FR: oszlopok(g, new Color(0, 36, 125), Color.WHITE, new Color(237, 41, 57)); break;
                case ES: spanyol(g); break;
                case ZH: kinai(g); break;
                case JA: japan(g); break;
                default: angol(g); break;
            }
            g.setColor(new Color(0, 0, 0, 90));
            g.drawRect(0, 0, SZ - 1, M - 1);
            g.dispose();
        }

        private void savok(Graphics2D g, Color... szinek) {
            int h = M / szinek.length;
            for (int i = 0; i < szinek.length; i++) {
                g.setColor(szinek[i]);
                int yy = i * h, hh = i == szinek.length - 1 ? M - yy : h;
                g.fillRect(0, yy, SZ, hh);
            }
        }

        private void oszlopok(Graphics2D g, Color... szinek) {
            int w = SZ / szinek.length;
            for (int i = 0; i < szinek.length; i++) {
                g.setColor(szinek[i]);
                int xx = i * w, ww = i == szinek.length - 1 ? SZ - xx : w;
                g.fillRect(xx, 0, ww, M);
            }
        }

        private void spanyol(Graphics2D g) {
            g.setColor(new Color(170, 21, 27));
            g.fillRect(0, 0, SZ, M);
            g.setColor(new Color(241, 191, 0));
            g.fillRect(0, M / 4, SZ, M / 2);
        }

        /** Egyszerűsített kínai zászló: piros alap, egy nagyobb és négy kisebb sárga csillaggal. */
        private void kinai(Graphics2D g) {
            g.setColor(new Color(222, 41, 16));
            g.fillRect(0, 0, SZ, M);
            g.setColor(new Color(255, 222, 0));
            csillag(g, 5.5, 5, 2.9);
            csillag(g, 10.5, 2.2, 1.0);
            csillag(g, 12.5, 4.6, 1.0);
            csillag(g, 12.2, 7.6, 1.0);
            csillag(g, 10, 9.6, 1.0);
        }

        /** Egyszerűsített japán zászló: fehér alap, piros körrel középen (hinomaru). */
        private void japan(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, SZ, M);
            g.setColor(new Color(188, 0, 45));
            int d = (int) Math.round(M * 0.66);
            g.fillOval((SZ - d) / 2, (M - d) / 2, d, d);
        }

        private void csillag(Graphics2D g, double cx, double cy, double r) {
            Polygon p = new Polygon();
            for (int i = 0; i < 5; i++) {
                double a1 = Math.PI / 2 + i * 2 * Math.PI / 5;
                p.addPoint((int) Math.round(cx + r * Math.cos(a1)), (int) Math.round(cy - r * Math.sin(a1)));
                double a2 = a1 + Math.PI / 5;
                p.addPoint((int) Math.round(cx + r * 0.38 * Math.cos(a2)), (int) Math.round(cy - r * 0.38 * Math.sin(a2)));
            }
            g.fillPolygon(p);
        }

        /** Egyszerűsített „Union Jack”: kék alapon fehér–piros átlók és kereszt. */
        private void angol(Graphics2D g) {
            g.setColor(new Color(0, 36, 125));
            g.fillRect(0, 0, SZ, M);
            g.setStroke(new BasicStroke(4f));
            g.setColor(Color.WHITE);
            g.drawLine(0, 0, SZ, M);
            g.drawLine(0, M, SZ, 0);
            g.setStroke(new BasicStroke(2f));
            g.setColor(new Color(207, 20, 43));
            g.drawLine(0, 0, SZ, M);
            g.drawLine(0, M, SZ, 0);
            g.setColor(Color.WHITE);
            g.fillRect(0, M / 2 - 3, SZ, 6);
            g.fillRect(SZ / 2 - 4, 0, 8, M);
            g.setColor(new Color(207, 20, 43));
            g.fillRect(0, M / 2 - 1, SZ, 2);
            g.fillRect(SZ / 2 - 2, 0, 4, M);
        }
    }
}
