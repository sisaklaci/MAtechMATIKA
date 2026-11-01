# MAtechMATIKA

Interaktív matematika-oktató alkalmazás (Java Swing), négy témakörrel:
**deriválás**, **integrálás**, **valószínűségszámítás** és **statisztika**.
Tetszőleges egyváltozós függvényt beírva a program élőben rajzolja a
grafikont, és szimbolikus/numerikus magyarázatot ad a műveletről.

## Futtatás

A program saját, beágyazott Java futtatókörnyezettel rendelkezik, külön
Java-telepítés nem szükséges.

1. Nyisd meg a [`dist/MAtechMATIKA`](dist/MAtechMATIKA) mappát.
2. Indítsd el a **`MAtechMATIKA.exe`** fájlt (Windows).

### Fordítás forrásból

```
javac -encoding UTF-8 *.java
java Main
```

## Főbb funkciók

- **Deriválás**: függvény és érintője élőben, szimbolikus derivált és
  szabály-magyarázat (`DerivaltModul`)
- **Integrálás**: Riemann-téglalapok élő animációja, numerikus (Simpson-
  szabály) és — ha lehetséges — szimbolikus eredmény (`IntegralModul`,
  `MatElemzes.szimpson`)
- **Valószínűségszámítás**: kombinatorika, binomiális és normális eloszlás,
  szimuláció (`ValoszinusegModul`)
- **Statisztika**: leíró statisztika + hisztogram, korreláció és lineáris
  regresszió (`StatisztikaModul`)
- Egyváltozós matematikai kifejezések szintaxisfás kiértékelése
  (`Kifejezes`, `Kifejezeselemzo`)
- Újrafelhasználható derékszögű koordináta-rendszeres rajzvászon a négy
  modulhoz (`Grafikon`)
- Többnyelvű felület (`I18n`)

## Mappaszerkezet

- [`Main.java`](Main.java) — alkalmazás belépési pontja, navigáció a
  témakörök között
- [`DerivaltModul.java`](DerivaltModul.java), [`IntegralModul.java`](IntegralModul.java),
  [`ValoszinusegModul.java`](ValoszinusegModul.java), [`StatisztikaModul.java`](StatisztikaModul.java)
  — a négy témakör
- [`Kifejezes.java`](Kifejezes.java), [`Kifejezeselemzo.java`](Kifejezeselemzo.java),
  [`MatElemzes.java`](MatElemzes.java) — kifejezés-kiértékelés és numerikus
  segédfüggvények
- [`Grafikon.java`](Grafikon.java) — koordináta-rendszeres rajzvászon
- [`dist/`](dist/) — futtatható Windows-csomag (exe + bundlingolt Java runtime)

## Licenc

A projekt a [PolyForm Noncommercial 1.0.0](LICENSE) licenc alatt áll: szabadon
nézhető, módosítható és terjeszthető, de csak nem kereskedelmi célra.
