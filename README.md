##### Implementing a Blackjack Multyplayer Card Game in Java (The description contains the thesis in Hungarian language)

##### EÖTVÖS LORÁND TUDOMÁNYEGYETEM

##### INFORMATIKAI KAR

```
Információs Rendszerek Tanszék
```
# Blackjack kártyajáték implementálása

### Témavezető: Készítette:



## Budapest, 2019


## Tartalom



- Bevezetés..................................................................................................................
- Felhasználói dokumentáció
   - Rendszerkövetelmények........................................................................................
   - Állományok
   - Adatbázis
   - Szerver indítása
   - Használat
      - Bejelentkezés
      - Regisztráció
      - Lobby
      - Játék menete
      - Kör vége és pontozás
      - Alkalmazásból való kilépés
- Fejlesztői dokumentáció
   - A feladat specifikációja
   - Adatbázis diagram:
   - Az adattáblák
      - userdata tábla
      - player tábla
   - Fejlesztői környezet
   - Megoldási terv
      - Kommunikáció................................................................................................
      - Kliens oldal
      - Szerver oldal
      - Játék logika
      - A fejlesztés folyamata
- Részletek
   - Szerver oldal uml-diagramja
   - Kliens oldal uml-diagramja
   - PanelBox-ból leszármazó panelek
   - A server.logic package
      - Suit és Rank osztály
      - Card osztály
      - Deck osztály
      - Shoe osztály
      - Hand osztály
   - RuleHand osztály
   - Player osztály
   - ServerTable osztáy
- A server.gameServer package
   - GameServer osztály
   - ServerListener osztály
   - Validate osztály
   - WatingRoom osztály
- A server.chatServer
   - ChatServer és ChatServerListener osztály
   - ChatPartner osztály
- A server package
   - BCrypt és BcryptHashing osztáy
   - DatabaseInitalizer osztály
- A dao package
   - DefaultDao<T extends Player> osztály
   - PlayerDao extends DefaultDao<Player> osztály
- A view.panels
   - Absztrakt PanelBox extends VBox osztály
   - • BetBox
   - • BothBox
   - • ContinueBox..............................................................................................
   - • DoubleDownBox
   - • HitStayBox
   - • InsuranceBox
   - • ChatBox osztály.........................................................................................
   - • DealerBox osztály......................................................................................
   - • ChatPane osztály
   - • MessageSendBox osztály
   - • InfoBox osztály
   - • ControllerEmpty osztály
   - Table osztály és a TableView osztály
   - Lobby és a LobbyView osztály
   - PlayerBox osztály
- A login package
   - Login osztály
      - IpController osztály
      - LoginFxmlcontroller osztály
      - RegisterFxmlController osztály
      - MessagePopUp osztály
   - A client package
      - TimeOut és TimeOutTask osztály
      - GUITimer osztály
      - CardFactory osztály
      - LobbyController osztály
      - ClientModel osztály
      - ClientController osztály
      - ChatThread és ChatModel osztály
- Tesztelés
   - Bejelentkezés
   - Lobby
   - Játék
      - 2 vagy több játékosnál
   - Chat
- Fejlesztési lehetőségek
- Hivatkozások


##### 5

## Bevezetés..................................................................................................................

A szakdolgozat a Blackjack nevű kártyajáték megvalósítása a kliens-szerver modell
alapján, ahol a szerver tölti be a bank(osztó) szerepét, míg a kliensek a játékosokét. A
játékot egy hagyományos BlackJack asztalon játszák több 52 lapos francia paklival
(Jokerek nélkül). A klienshez tartozik egy grafikus felület, ahol a játékasztal, valamint az
eddig gyűjtött nyereményeket láthatjuk, a szerver csak információkat közöl a kliensekkel.
Egy játékot legfeljebb 4 játékos játszhat, akik külön-külön az osztó ellen játszanak,
tehát nem egymás ellen. A játék előtt a kliensek csatlakoznak a szerverhez, ami után egy
várakozó szobába kerülnek. A játék akkor kezdődik el, ha egy játékos(kliens) csatlakozik
a szerverhez ezután választ egy elérhető játékasztalt. A játék során először minden játékos
megkapja a pénzét, ezután megteszik a játékosok a tétjeiket ezt követően a szerver minden
játkosnak és saját magának is oszt egy lapot színével felfelé és oszt ugyanilyen módon
még egy kört a játkosoknak de ebbe a körbe saját magának a már színnel lefelé oszt lapot
így a játékosok nem tudják, hogy milyen a 2.lapja az osztónak. A játékos úgy nyerhet, ha
az ő lapjai közelebb vannak összértékben 21 - hez mint az osztójé viszont azt nem haladják
meg. A játékos addig kérhet lapot, amíg nem éri el a 21-et ha túllépi akkor a játékos
vesztett az osztó csak ezután kérhet lapot ugyanezen a szabályok mentén. (A pontos
játékszabály: [ 1 ]) Amennyiben a játékos nyert akkor a kivételesesetektől eltekintve a
feltett tét kétszeresét nyeri, ha az osztó nyert akkor a játékos elbukta a feltett tétet, ami a
bankhoz kerül.
Egy játékos mindaddig folytatja a játékot ameddig el nem fogy a pénze, ki nem lép a
játékból vagy bezárja az alkalmazást, ebben az esetben a játék folytatódik, ha tétrakás
előtt lépett ki a játékos akkor a minimális tétet rakja fel a szerver a nevében, illetve mindig
a megállást választja a játékos helyett. Amennyiben egy játékos kilépett a körvégén
automatikusan lekerül az asztalról, ekkor több játékos esetén a többi játékos sem fogja
tovább látni. A játékos a játékok között láthatja az eredménytáblát. A játékot Java nyelven
fogom megvalósítani. A játékosok és a játékok eredményei mySQL adatbázisban fognak
tárolódni. A kliensek a szerverrel socket-eken keresztül kommunikálnak, szöveges
üzenetek formájában. A játék grafikus felülete JavaFx nyelven íródott.


## Felhasználói dokumentáció

### Rendszerkövetelmények........................................................................................

A játék platform független Java nyelven íródott. A játék futtatásához szükséges
legalább Java SE Runtime Environment 8. A játék(kliens) futtatásához, nincs szükség
telepítésre csak a futtatható állományra illetve, hogy csatlakozzon a szerverhez, aminek
az elérési címe a játék elején beállítható (alapértelmezetten localhoston a 4444 illetve
4445 - ös port), a szerver futtatásához szükséges egy blackjack mySQL adatbázis a
nbuser,nbuser felhasználónév-jelszó párossal (localhost:3306/blackjack) táblákat a
DatabaseInit állomány hozza létre. Hardver követelményei minimálisak. Ajánlott
képernyő felbontás minimum 1366x768.

### Állományok

- Szerver.jar
- Kliens.jar
- DatabaseInit.jar

### Adatbázis

Az adatbázis létrehozását XAMMP webszerver-szoftver csomaggal ajánlom [ 5 ],
illetve a weboldalon található egy telepítési útmatató is. Ha az előbbi szoftvert választjuk
az adatbázis telepítésére, akkor az útmutató szerint telepítsük a programot, ezután nyissuk
meg a programot és indítsuk el az Apache,MySQL és Tomcat service-öket.

```
1. ábra
```

Ezután kattintsunk a MySQL admin gombjára ezt követően egy webes felületen fogja
megjeleníteni a kezelőfelületet.

```
2. ábra
```
A weboldal baloldalán található listából kattintsunk az Új pontra ,majd a megjelenített
ablakban az Adatbázis neve helyére írjuk be, hogy blackjack és kattintsunk a létrehozásra

Ha már létrehoztunk egy adatbázist a localhost:3306 címen, szerver számára (Pl
XAMMP webszerver-szoftver csomaggal), akkor futtassuk parancssorból a a
DatabaseInit programot , ami legenerálja az adatbázis tábláit illetve létrehoz 4 teszt
felhasználót.


```
3. ábra
```
(Későbbiekben az adatbázisban módosíthatók a játékosok adatai ezen a felületen). A
teszt játékosok adatai :

```
Felhasználónév Jelszó
test1 test
test2 test
test3 test
test4 test
```

### Szerver indítása

A szerver indítása parancssorból történik, ahol paraméterben kell megadni a szerver
két portját vagy paraméter nélkül indítani ekkor a 4444 illetve 4445 porton fog elindulni.
(Pl java -jar Szerver.jar 1234 1234, vagy java - jar Szerver.jar). A szervert a parancssorba
a Ctrl+C billentyűparancs bevitelével lehet leállítani.

```
4. ábra
```
### Használat

#### Bejelentkezés

A játék futtatása történhet parancssori futtattassál, ez esetben a célhelyen kell egy
parancssort megnyitni és következő parancsot begépelni: java - jar Kliens.jar. A játék
futtatható ezenfelül a szokásos módon, az alkalmazásra való dupla kattintással. A játékos
a program futtatása után egy bejelentkező ablakot fog látni, ahol megadhatja a szerver
elérhetőségét.


```
5. ábra
```
Ha a szerver nem elérhető a megadott címen a felhasználónak felugrik egy hibaüzenet,
ha helyes a felhasználó tovább lép a bejelentkezési ablakra, ahol bejelentkezhet az
adataival vagy regisztrálhat (Register gombra kattintva) a szükséges adatok megadásával.
Ha a felhasználó hibás adatokat ad meg bejelentkezésnél, akkor hibaüzenet kap a
rendszertől helytelen felhasználónév, illetve helytelen jelszó esetén.

```
6. ábra
```

#### Regisztráció

A Regisztráció során a felhasználónak meg kell adnia a nevét, egy felhasználónevet,
egy e-mail címet, egy jelszót, illetve a születési dátumát. A program kétszer kéri be az e-
mail címet, illetve a jelszót az elgépelés elkerülése végett, ha nem egyezik a két mező a
rendszer figyelmezteti a felhasználót az eltérésre.

7_. ábra_
Az összes adat kitöltése kötelező, ha a felhasználó kihagyna egy mezőt erre is
figyelmezteti a program. Ezen felül az e-mail címnek és a felhasználónévnek egyedinek
kell lenniük, azok még nem szerepelhetnek az adatbázisban, ha mégis szerepelnek a
program felhívja a felhasználó figyelmét erre és egy hibaüzenetet küld, a javítandó
mezőről. Ha a regisztráció sikeres volt, akkor a felhasználó kap egy üzenetet, ezután a
bejelentkező felületre kerül, ahol az új felhasználói fiókjával bejelentkezhet minden
játékos kezdőtőkéje: 2500 $.


```
8. ábra
```
#### Lobby

```
A felhasználó sikeres bejelentkezés után a Lobby-ba kerül ,
```
9_. ábra_
ahol láthatja a saját adatait, chat-elhet a többi játékossal, megnézheti a játékosok
eredményét, illetve csatlakozhat egy asztalhoz játék céljából.


10_. ábra_
A játékosok eredményét az oszlopnévre kattintva tudja rendezni, csak olyan asztalhoz
csatlakozhat, ahol a minimális tét kisebb, mint a játékos pénze.

```
11. ábra
```
Egy játékos akkor kerül a játéktérre, ha az asztalon vége van az előző körnek, ha a
felhasználó várakozik egy kör végére addig nem választhat másik asztal csak akkor, ha a
kiválasztott asztalon végez a játékkal, ahonnét visszakerül a Lobby-ba és újra választhat
asztalt.


#### Játék menete

```
12. ábra
```
A játékot egy, vagy legfeljebb 4 játékos játssza, illetve a szerver. A játékost egy
információs panelben értesíti a játék fejleményeiről ez a panel az osztó lapjai előtt jelenik
meg.

```
13. ábra
Több játékos esetén a játékos saját panele a csatlakozás sorrendje szerint jelenik meg.
```
```
14. ábra
```
Először minden játékos megteszi a tétjeit, aminek minden asztalnál nagyobbnak kell
lennie a minimum tétnél.


15_. ábra_
Egy játékos esetén a szerver a játékosnak oszt egy lapot, több játékos esetén minden
játékosnak oszt egy lapot, ezután magának is oszt egy lapot színnel felfelé ezután
megismétlődik az előző kör annyi változással, hogy az osztó magának egy kártyát oszt
színnel lefelé.

16_. ábra_
A játék ezután kezdődik, ha valamelyik játékos lapjainak értéke 21 (blackjack), akkor
nyert (kivéve, ha az osztónak is 21-e van ekkor döntetlen és visszakapja a feltett értéket)
ilyenkor a megtett tét 1,5 szeresét kapja meg.

```
17. ábra
```

Ha a játékosnak a lapjainak értéke nem 21, akkor 3 választása van (+1 speciális eset)
választásra 15 másodperc álrendelkezésre, amit az információs panelben egy számláló
mutat a játékosnak:

```
18. ábra
```
- dupláz ekkor a feltett pénzét megduplázza és ezután már csak egy lapot kap;
- lapot kér ezt a lépését addig ismétli, ameddig meg nem áll vagy lapjai értéke meg
nem haladja a 21-et;
- illetve megállhat ekkor a következő játékos vagy osztó következik.

```
19. ábra
```
+1 eset: Ha az osztónak az első lapja ász akkor a játékos biztosítást köthet az osztó
következő lapjára, ez a feltett tét fele.

```
20. ábra
```

#### Kör vége és pontozás

A játékos akkor nyer, ha lapjai értéke nem haladják meg a 21-et illetve az osztó
lapjainak értéke vagy meghaladják a 21-et, vagy a játékos lapjainak értéke közelebb
vannak a 21-hez.
A kifizetés a következőképpen zajlik:

- Ha a játékos normál módon nyer 1:1 arányban kap pénzt a téthez viszonyítva
- Ha a játékos első két lapjának értéke 21, akkor 3:2 arányban
- Ha duplázott, akkor a tétet duplázta és az első esett érvényes rá a továbbiakban
- Ha a biztosítást kötött 1:1 arányban kap pénzt a biztosítási téthez viszonyítva,
    ha az osztónak Blackjackje van.

```
Ha a játékos veszít, akkor a feltett tétet elveszíti.
Ezután a játékos eldöntheti, hogy szeretne tovább játszani.
```
```
21. ábra
Ha nem kívánja folytatni a játékot, akkor a játékos átkerül a Lobby-ba. Ha a
játékos folytatja a játékot,
akkor a várakozó játékosok csatlakoznak és megjelennek a játékos grafikus felületén
és a játék folytatódik. (Több játékos esetén a távozó játékos panele eltűnik a
kezelőfelületről.)
```

```
22. ábra
```
#### Alkalmazásból való kilépés

A felhasználó bármikor bezárhatja az alkalmazást az ablak bezárásával, viszont, ha ezt
a játékasztalon teszi, akkor a szerver kénytelen lejátszani helyette a játékot, aminél nagy
eséllyel veszít. Ajánlott a Lobby-ba bezárni az alkalmazást a játék elhagyása után, ekkor
a játékos statisztikái változatlanok maradnak.


## Fejlesztői dokumentáció

### A feladat specifikációja

A programnál a megvalósítandó cél egy grafikus felülettel rendelkező, többszemélyes,
kliens-szerver alapú Blackjack kártyajáték implementálása volt. A játéknak lehetővé
kellett tennie, hogy egy vagy több ember tudjon játszani a programmal, akár
párhuzamosan több asztalnál is. A program létrehozásánál a grafikus felületet próbáltam,
úgy megalkotni, hogy minden képernyő típus mellett használható legyen és felhasználó
barát legyen. Az információ közlése automatikus és könnyen értelmezhető. Az osztó
implementálásánál fontos volt, hogy a valós játékszabályok szerint játsszon. A
játékosoknál egy-két speciális esetet kihagytam a megjelenítés miatt vagy, mert nem
általánosan elfogadott lépés. A feladat elkészítése során az alap játék került
implementálásra (split hand,surrender kivételével).Fontos szempont volt, hogy a játék
több asztalon is fusson, ezáltal több játékos részt tudjon venni a játékban.

```
Elvárt funkciók:
Felhasználók kezelése:
```
- Játékosok adatainak tárolása adatbázisban
- Játékosok adatainak automatikus frissítése
- Játékosok jelszavainak biztonságos tárolása
- Játékosok bejelentkezésének a kezelése

```
Szabályok megvalósítása:
```
- Eredeti játékszabályok betartása
- Eredetivel megegyező kompenzációs rendszer
Irányítás, megjelenítés:
- Egyszerű, egyértelmű felület
- Fontos információk automatikus közlése
- Egyszerű tovább lépési opciók
- Egyszerű egérrel és billentyűzettel való vezérlés


### Adatbázis diagram:

```
23. ábra
```
### Az adattáblák

Az adatbázisban 2 tábla van: a userdata tábla és a player tábla.A userdata tárolja a
felhasználó által megadott adatokat, a player tábla tárolja játék során használt adatokat.

#### userdata tábla

- ID: az elsődleges nem nulla azonosító, egy egész számot tartalmazó azonosító
- USERNAME: a felhasználó felhasználónevét tároló mező (szöveges
    formátum)
- PASSWORD: a felhasználó jelszavát salted hash-ben tároló mező (szöveges
    mező)
- EMAIL: a felhasználó e-mail címét tároló mező (szöveges mező)
- BIRTH: a felhasználó születési dátumát tároló mező (dátum formátum)
- REGISTER: a regisztráció dátumát tároló mező (dátum formátum)

#### player tábla

- ID: az elsődleges nem nulla azonosító, egy egész számot tartalmazó azonosító
- USERNAME: a felhasználó felhasználónevét tároló mező (szöveges
    formátum)
- MONEY: a felhasználó pénz összegét tároló mező (double típus)
- WIN: a felhasználó által megnyert játékok száma (egész típus)
- LOSE: a felhasználó által elveszített játékok száma (egész típus)


### Fejlesztői környezet

A program Windows 10 alatt íródott, Java nyelven, a grafikus felület játékhoz tartozó
része JavaFx-ben íródott, a bejelentkezési felület Fxml-ben Scene Builder segítségével
[ 11 ], NetBeans compiler-rel. Az adatbázist XAMMP szoftver segítségével tudtam
vizsgálni.

### Megoldási terv

A játék megvalósításának a megkezdésénél két részre bontottam a programot, a szerver
részre, illetve a kliens részre. Ezután azokat kisebb egységre bontottam, amiket egyes
package-ek szimbolizálnak. A játék implementálása a játékhoz elengedhetetlen típusok
létrehozásával kezdődött. A megvalósításnál fontos szempont volt a modularitás, mind a
funkcionális, mind a megjelenítésért felelős osztályoknál.

#### Kommunikáció................................................................................................

A program socketeken kommunikál. A kommunikáció karakterláncokkal történik,
amit kisebb karakter láncokra bont a kliens és ezeket a komponenseket a vezérlőosztály
értelmez egy nagyobb switchen keresztül és ez alapján frissíti a model-osztályt, illetve
ezt átadja a grafikus felületnek megjelenítésre.

```
24. ábra
```
#### Kliens oldal

A kliens oldal az MVC-architektúra alapján íródott, így szét válik a megjelenítés, az
adattárolás és a vezérlés. Egymástól elkülönülve, egymást értesítve működnek a
különböző komponensek. [ 5 ]


```
25. ábra
```
#### Szerver oldal

A szerver oldal megvalósítása során a modularitás megteremtése volt a cél. A játék
logikáját részegységekre bontva, külön egy asztalra bontottam és asztal viselkedését a
játékosok lépéseitől tettem függővé. A szerver implementációja egy egyszerű szerver-
socket felkészítve több kliens fogadására, amit kiegészítettem egy validációs illetve egy
várakozó osztállyal. A szerver adattárolása egy mySQL adatbázisban történik, amihez
egy Data Access Object-tel fér hozzá a szerver. A DAO egy JDBC connector-ral
csatlakozik az adatbázishoz.

```
26. ábra
```
#### Játék logika

A játék logikája teljes mértékben a Blackjack szabályaira támaszkodik. Mivel a játékos
csak és kizárólag az osztó ellen játszik, ezért csak az osztó viselkedését kell szimulálnunk,
viszont az osztó szabályok szerint játszik ezért nekünk az a célunk, hogy ezt
implementáljuk. Ha az osztó lapjainak értéke meghaladja a 17-et akkor az osztó megáll
egyébként lapot kér (ha softhand-je van és meg haladja a 21-et akkor levonunk 10-et az
értékből (soft hand ha van a kezében egy Ász)). Ezen felül arra figyelünk, hogy a játékos
mikor veszít, illetve nyer, ahogy a bevezetésben már részleteztük. A játék logikája
leegyszerűsítve: egy CountDown latch segítségével megvárjuk az összes tétet, végig
iterálunk a játékosoknak és osztunk egy lapot, ezután az osztó is kap egy lapot és újra


végrehajtjuk ezt, de az osztó 2.lapjának most a hátoldalát jelenítjük meg. Ezután egy
olyan iteráció következik, ahol a játékos addig játszik amíg megnem áll vagy veszít,
esetleg dupláz ekkor még egy lapot kap és duplázódik a tétje (ha az osztó első lapja ász
biztosítást köthet), ezután folytatjuk a következő játékossal az iterációt ugyanezzel a
sorrenddel...így tovább az utolsó játékosig ezután az osztó következik a fentebb említett
logika alapján. Ezután még egy iteráció következik, aminél ellenőrizzük, hogy ki szeretné
folytatni a játékot és a bent maradó játékosok ezt a logikát követve játszanak tovább. Ha
valaki kilép a játék közben a szerver úgy játszik, hogy a minimális tétet teszi meg helyette
és ha rá kerül a sor megáll, így valószínűleg veszít a játékos, de akár nyerhet is, a kör
végén eltávolításra kerül az asztalról. A játékosnak minden választásra 15 másodperce
van, ha letelik a 15 másodperc, akkor a kilépéskor használt lépések hajtódnak végre. A
játék kommunikációja socketeken történik ,ahol szöveges kommunikáció folyik, a
szerveren fut a játék a klienstől csak az egyes lépések opcióit várja. A kliens grafikus
felülete a szöveges üzenet tartalma szerint változik.

```
A szerver oldal:
```
- A server.logic package tartalmazza a játék logikáját és a hozzá tartozó típusok
    implementációját.
- A server.gameServer package tartalmazza szerver azon részét , amely biztosítja
    a játékosok csatlakozását és a játékon kívüli kommunikációt illetve a socketek
    kezelését.
- A server.chatServer package chat-szerver implementációja.
- A server package-ben található BCrypt osztályok a jelszó salted hash-ét
    előállító illetve ellenőrző osztályai, illetve az adatbázis tábláinak létrehozásáért
    felelős osztály. (Nem saját osztályok)
- A dao package (Data Acces Object) felel a szerver és az adatbázis kapcsolatáért
    és az adatok kezeléséért.
A kliens oldal:
- A client package itt található a játékos számára a játék és a chat logikáját
tartalmazó osztályok, illetve a modellek.
- A view tartalmazza az asztal, illetve a lobby felültért felelős osztályokat
- A view.panels package a megjelenítésnél használt képernyő paneleket
tartalmazza


#### A fejlesztés folyamata

A kliens oldal tervezésekor az MVC-architektúrához próbáltam tartani magam és
ez alapján kezdtem neki a kód megírásának. A kód megírásának az első fázisában arra
törekedtem, hogy a szerver oldalon az összes olyan osztályt megírjam, ami a játék során
használt objektumokat leírják (kártya, pakli, shoe, asztal, játékos) ezt követően a játék
logikáját leíró osztályokat készítettem el. A kód írás első célja az volt, hogy a
legalapvetőbb játékfunkciók működjenek és kommunikáljon egymással a szerver és egy
kliens program. Az első fázis megvalósítása közben a próbáltam sok módszert alkalmazni
a kommunikációra, de végül a szöveges változat mellett döntöttem mert így nem kellet
semmilyen megkötést se figyelembe venni. A szerver oldalt felkészítettem több kliens
kiszolgálására. A második fázis volt a felhasználók kezelése. A mySQL adatbázis mellett
azért döntöttem, mert könnyű hozzáférést biztosít az adatokhoz és sok szoftver biztosít
hozzá grafikus felületet, ami a tesztelésnél és a tervezésnél megkönnyítette a dolgom.
Azért esett a választásom a két adattáblára, mert azt szerettem volna, hogy a játéknak
lenne egy külön csak a neki fenttartott tábla, amit egyszerűen módosíthat míg a másik
tábla a játék számára irreleváns adatokat, személyes adatokat tárol. Ezután létrehoztam
FXML-ben 2 ablakot, amik az adatbevitelt szolgálták és megírtam a hozzájuk tartozó
controller osztályokat. Ezt követően a szerver oldalt is fel kellett készíteni az adatok
fogadására, illetve azoknak a tarolására. A szerver oldalon előszőr létrehoztam egy DAO-
t és ehhez írtam egy validáló osztályt, ami a DAO-n keresztül lekéri a szükséges adatok
a bejelentkezéshez és ellenőrzi az adatok helyeségét és értesíti a felhasználót. Amikor az
adatbázisba felkerültek már az első adatok, akkor a következő lépés az volt, hogy hogy
tároljam el a jelszavakat, mert azokat bárki láthatta az adatbázisban. Ekkor találtam rá a
Salted Hash nevű titkosításra, ami biztosítja, hogy a tárolt karakterláncok alapján az
eredeti jelszó ne legyen visszafejthető. A program grafikus megjelenítéséhez először a
Java Swinget szerettem volna alkalmazni, viszont elég hamar kijöttek a Swing hátrányai,
mindent előre definiálni kellett és az eszköztára véleményem szerint idejét múlt. Miután
a Swing nem volt számomra alkalmas, ezért a JavaFx-re esett a választásom, ami nagyobb
mozgásteret engedett és dinamikusan változó kezelő felületet lehetet vele létrehozni.
Elkülönítve kezdtem el írni a kezelőfelületet, ami így jobban tesztelhető volt és gyorsabb
is volt a fejlesztése. A grafikus felület kidolgozásánál próbáltam minél kisebb modulokra
szétbontani az asztalt, így jobban tudtam szétválasztani a későbbi funkciókat, szempont
volt, hogy egy panel csak egy logikai egységet reprezentáljon (például tétrakás, lap
kérés...). A fejlesztés legnehezebb része az volt, hogy a meglévő komponenseimet
összeillesszem és azoknak a működését szinkronba hozzam. Ezután elvégeztem a
szükséges kiegészítéseket a programon, aminél figyelembe vettem, hogy egy felhasználó
miként viselkedhet és az ezekből fakadó hiba lehetőségeket próbáltam kiküszöbölni.
Ennek a folyamat során az volt a célom, hogy a játékos ne tudjon többször bejelentkezni
ugyanazzal a fiókkal egy időben, illetve, ha játék közben kilépne akkor a szerver ezt
kezelje és folytassa a játékot, ezen felül az alkalmazás kapott egy időzítőt, így, ha
valamelyik játékos elhagyná a számítógépet játék közben a szerver lejátssza helyette a
játékot és a kör végén leveszi az asztalról. A tesztelés során előjöttek kisebb hibák, ezeket
sikerült kisebb módosításokkal kiküszöbölni. A fejlesztés végén még bekerült egy logfile
írás funkció minden asztalnak külön logfile-ja van.


## Részletek

### Szerver oldal uml-diagramja

```
A digitális adathordozón nagyobb felbontásban megtalálhatók a diagramok.
```
```
27. ábra
```

### Kliens oldal uml-diagramja

```
28. ábra
```

### PanelBox-ból leszármazó panelek

```
29. ábra
```
### A server.logic package

#### Suit és Rank osztály

A lapok színének a megvalósítására létrehoztam egy Suit felsoroló típust, amelynek
lehetséges értékei a CLUBS, HEARTS, SPADES, DIAMONDS.
Egy másik Rank felsoroló típust a lapok értékének hoztam létre aminek az értékei
lehetnek ACE(1), TWO(2)...TEN(10),JACK(10)...,KING(10) a zárójelben szereplő
érték a lap értéke. Egy toString függvénnyel lekérhető a neve (kártyák előállítása miatt),
illetve egy getValue függvénnyel elérhető a lap értéke.


```
30. ábra
```
#### Card osztály

```
31. ábra
```
```
A Card osztály egy kártyát valósít meg.
Fontosabb Adattagok:
```
- Suit suit: A kártya színét reprezentálja.
- Rank rank: A kártya értékét reprezentálja.


#### Deck osztály

```
32. ábra
```
A Deck osztály legenerál mind a négy színből 13 különböző értékű kártyát (Card) és
eltárolja azt.
Fontosabb Adattagok:

- LinkedList<Card> deck: A kártyákat tároló lista.
Fontosabb Függvények:
- Card getLastCard(): Visszatér a pakli utolsó kártyájával és törli azt.

#### Shoe osztály

```
33. ábra
```
A Blackjacknél kártya tároló reprezentálása ez egy olyan objektum, amiben bizonyos
számú pakli van bekeverve.
Fontosabb Adattagok:

- LinkedList<Card> shoe: A bekevert paklikat tartalmazó lista.
- int numOfDecks: Hány pakliból álljon a Shoe.
Fontosabb Függvények:
- Shoe(int numOfDecks): (konstruktor) Legenerál annyi paklit ammenyi a
paraméter.
- Card dealNextCard(): Visszatér a shoe utolsó lapjával és törli belőle azt.
Fontosabb Eljárások:
- shuffle(): Megkeveri a shoe-t.


- addDeck(Deck deck): Hozzáad egy paklit a shoe-hoz.

#### Hand osztály

```
34. ábra
```
```
A játékos kezének az alaptulajdonságait reprezentálja.
Fontosabb Adattagok:
```
- LinkedList<Card> hand: A játékos kártyái.
Fontosabb Függvények:
- int getValue() : A lapok értékével tér vissza.
- int getSize(): A lapok számával tér vissza.
- Card getCard(int index) : Az index helyen lévő kártyával tér vissza.
- int rankCount(Rank rank): Rank értékű kártyából hány darab van.
Fontosabb Eljárások:
- addCard(Card card): card kártya hozzáadása

### RuleHand osztály

```
35. ábra
```
A Hand osztályt terjeszti ki a játékhoz szükséges függvényekkel, metódusokkal
adattagokkal.


Fontosabb Adattagok:

- int bet: A kézre vonatkozó tét
- boolean doubleDown: Duplázott-e a játékos
- Card doubleDownCard: A duplázásnál kapott kártya

Fontosabb Függvények:

- int greaterValue(): Ha van ász a játékos kezébe ász akkor annak az értéke 11
    és nem 1 addig amíg nem lépi túl a 21-et,ez a függvény 11 - gyel számolja az
    ász értékét és úgy számolja a lapok értékét.
- boolean isSoftHand(): Van-e ász a lapok között?


### Player osztály

```
36. ábra
```
A játékost reprezentáló osztály, itt található egy játék kör és a játékosra vonatkozó
logika. Ez az osztály határozza meg, hogy mi következik egy adott lépés után. Ez az
osztály külön Thread-ként fut, a működésé a játékos viselkedését szimulálja a klienstől
vár választ és az alapján folytatja a játékot.


Fontosabb Adattagok:

- static final double BLACKJACK_PAYOUT: Blackjack esetén a kifizetési
    szorzó.
- static final String SERV_AUTH: Szerver üzenetek prefixe.
- CountDownLatch startLatch: Az összes játékos készen áll-e (akkor 0).
- CountDownLatch betLatch: Az összes játékos megtette már a tétjét (akkor 0).
- CountDownLatch insuranceBetLatch: Kötöttek-e már biztosítást (akkor 0).
- CountDownLatch dealLatch: Kapott-e mindenki lapot (akkor 0)..
- CountDownLatch dealerTurnLatch: Osztóra várakozás
- CountDownLatch firstPlayerCardLatch: Játékosok első lapjára várakozó latch.
- CountDownLatch secondPlayerCardLatch: Játékosok második lapjára
    várakozó latch.
- CountDownLatch firstDealerCardLatch Az osztó első lapjára várakozó latch
- boolean left: Kilépett-e a játékból.

Fontosabb Eljárások:

- run():Leírja mit csináljon az osztály a futása alatt, belépéskor küld egy üzenetet
    a kliensnek, ezután meghívja a playBlackjack() eljárást, ha a játékos nem
    folytatja a játékot küld egy üzenetet illetve, ha vissza akar lépni a Lobbyba
    akkor lekéri a szükséges adatokat. A játék elhagyása előtt tájékoztatja az
    asztalt.
- playBlackjack(): Egy kör pontos menetét írja le: törli az előző kör adatait; vár
    a többi játékosra, megtesszi a tétet, vár a többi játékosra, vár a lapokra, vár a
    többi játékos lapjára, elkezdi a játékot, vár a többi játékosra, vár az osztóra,
    eredmények, körvége.
- reset(): Előző kör adatainak törlése.
- getBet(): A kliens oldalról várja a tétet és ellenőrzi, hogy nagyobb-e a
    minimum tétnél illetve van-e annyi pénzünk, ha igen beállítja megtett tétnek,
    ha nem újra kéri és tájékoztatja a klienst a hibáról.
- newRound(): Törli az előző játékból maradt lapokat és annak értékeit és erről
    tájékoztatja a klienst. Ezután ellenőrzi, hogy az osztó első lapja ász-e, ha igen
    megvárja a biztosításokat. Ha 21 - e van az osztónak feltett tét kétszeres a
    nyeremény, a játék végén derül ki.


- insuranceTurn(): Ellenőrzi, hogy akar-e biztosítást kötni a játékos, ha igen
    akkor feltett tétjének a felét teszi meg(ha van elég pénze).
- yourTurn(RuleHand aHand) : Ha a játékos a soron következő választhat az első
    körnél, hogy dupláz-e, ha nem lapot kérhet addig ameddig nem veszít vagy
    megállhat a paraméter split hand-re vonatkozik, de még nincs implementálva
    funkció.
- doubleDownOption(RuleHand aHand): A lapjai alapján választhat a játékos,
    hogy dupláz, lapot kér vagy megáll.
- hitStayOption(RuleHand aHand): A lapjai alapján választhat a játékos lapot
    kér vagy megáll.
- doubleDown(RuleHand aHand): Ha a játékos duplázz ez a függvény fut le a
    duplázás szabályai szerint.
- hitStand(RuleHand aHand): A lapkérés és a megállásért felelős függvény
- dealerTurn(): Az osztó lapjainak elküldése illetve ha blackjackje van az
    osztónak akkor a biztosítási összeget is elküldi.
- resultTurn(RuleHand aHand) :A kör eredményeinek elküldése.
- continueTurn(): Ellenőrzi,hogy a játékos akar-e tovább játszani, ha nem akkor
    a játékos átkerül a Lobbyba.
- getAnswer(): A játékostól várja a válaszokat, ha kilép a játékos vagy valami
    hiba lép fel akkor LEAVE válasszal tér vissza és ezalapján a szerver kilépteti
    és lejátssza a játékot helyette.
- send(String rawString): Rendezi és elküldi az üzenetet a kliensnek, illetve a
    logfájlba beleírja a játékos lépését.
- *LatchCountDown(): Egyet visszaszámol.


### ServerTable osztáy

```
37. ábra
```
Ez az osztály reprezentál egy asztalt, tartozik hozzá egy osztó, illetve egy játékkört
valósít meg a körben minden folyamatot megismétel minden játékoson. Az asztal csak
akkor végez műveleteket, ha legalább egy játékos játszik rajta. Egy asztalhoz négy játékos
ülhet le. Minden asztal egy külön folyamat.


Fontosabb Adattagok:

- final Integer minBet: Minimális tét az asztalon.
- numOfDecks: Paklik az asztalnál.
- final int minimumCardsBeforeShuffle: Shoe minimális mérete.
- final AdditionalHand dealersHand: Az osztó lapjai.
- final LinkedList<Player> table: Asztalnál lévő játékosok.
- final ArrayList<Player> waiters: A játékra váró felhasználók.
- File logFile: Logfile.
- FileWriter fileWriter: Logfile számára fenttartott filewriter.

```
Fontosabb Metódusok:
```
- playBlackjack(): Megfeleltethető a Playerben található azonos nevű
    metódusnak, csak itt az asztal vár a játékosokra és nem a játékosok egymásra.
    Annyi kiegészítéssel, hogy itt a körvégén a játékosok adataival frissül az
    adatbázis, illetve új játékosok csatlakozhatnak (newArrivals metódus).
- setup(): Minden kör elején a változók alaphelyzetbe állítását végzi el.
- countDown (String string) string paraméter szerint a választott latch-en egyet
    visszaszámol.
- dealFirstRound() A játékosoknak és az osztónak oszt 2 lapot a metódus.
- addPlayerToTable(Player player): Hozzáad az asztalhoz egy új játékost és indít
    neki egy folyamatot.
- newArrivals(): Ez a metódus felelős a várakozó játékosok hozzáadásáért az
    asztalhoz.
- writeLog(String message): Ez a metódus a logfile-ba írja a játékos adatait.

Fontosabb Függvények:

- Card dealCard(): Egy lappal tér vissza a shoe tetejéről , ha a shoe elfogyna,
    létrehoz egy új shoe-t.
- Card dealerFirstCard(): Az osztó első, ismert lapjával tér vissza.
- boolean addPlayerToWaiters(Player player): Ez a függvény hozzáadja a
    várakozókhoz a játékost, a visszatérési érték a hozzáadás sikerességét jelzi.

## A server.gameServer package


### GameServer osztály

```
38. ábra
```
Ez az osztály felel a játékszerver indításáért, ez az osztály hozza létre az asztalokat,
illetve sikeres belépés után a felhasználónak ez az osztály biztosítja a játékos adatait és
tárolja, hogy éppen ki játszik. Ennek az osztálynak a futtatásával indul el a szerver.

Fontosabb Adattagok:

- ServerSocket serverSocket: Szerver socket
- final int serverPort: Játékszerver portja
- final int chatPort: Chatszerver portja
- List<Player> onlinePlayers: Online játékosok
- List<ServerTable> tables: Asztalok
- ChatServer chatServer: Chatszerver
- ServerListener serverListener: Játékosokat fogadó osztály
- Thread serverListenerThread Játékosokat fogadó folyamat


GameServer(int serverPort,int chatPort): Beállít két portot egyet a chat-,egyet a
játékszervernek.

Fontosabb Eljárások:

- startServer(): Ez az eljárás felel a szerver indításáért indít egy új folyamatot a
    kliensek fogadására,
- createTable():Létrehozza a játék asztalokat.
- sendDataForLobby(Player player): A kliensnek elküldi a Lobbyhoz tartozó
    információkat.
- addPlayer(Socket socket,Player player): Ez az eljárás validáció után hozzáadja a
    játkékost(player) az online játékosokhoz és elküldi a kezdés előtti információkat.
- sendTableList(Socket socket,PrintWriter out):Ez az eljárás elküldi az asztalok
    listáját.
- sendLeaderBoard(Socket socket,PrintWriter out): Ez az eljárás elküldi a
    leaderboard-ot.
- removeOfflineClient(Socket socket): A paraméterben kapott socket-tel
    rendelkező játékost törli az online játékosok közül.
- main(String[] args): A futtatásért felelős main metódus.

```
Fontosabb Metódusok:
```
- String getClientMessage(Socket socket):A kliens üzenetét fogadó metódus
    esetleges hibák kezelése.
- boolean isOnline(String username): A username felhasználónevű játékos
    online van-e?


### ServerListener osztály

```
39. ábra
```
Ez a folyamat felel a kliens kapcsolódásáért. Hallgat a szerver-porton és ha egy kliens
csatlakozik létrehoz neki egy új folyamatot, ahol a validáció történik.

```
Fontosabb Adattagok:
```
- final GameServer server: Játékszerver
- final ServerSocket serverSocket: Szerver socket
A konstruktor a paramétereket elmenti.

```
Fontosabb Eljárások:
```
- acceptClients():Hallgat a megadott porton, ha új kliens érkezik új validációs
    folyamatot indít a Validate osztállyal.

### Validate osztály

```
40. ábra
```
Ez az osztály felel azért, hogy ellenőrizze a játékos bejelentkezési adatai vagy a
regisztrációs adatai megfelelőek, ha nem akkor tájékoztatja a hibás adatról a klienst, ha
megfelelő átadja az adatait a WaitingRoom osztálynak.


```
Fontosabb Adattagok:
```
- GameServer server: a játékszerver
A konstruktor elmenti a paramétereket és megpróbálja a socket ki- és bemenetét elérni.
Fontosabb Függvények:
- String getMessage(): A kliens válaszára váró függvény.
- boolean processAnswer(String message): A message üzenet alapján
megpróbálja validálni az adatokat, ha bejelentkezés történik ellenőrzi, hogy a
jelszó egyezik-e a tárolt hash-sel és létezik-e a felhasználó illetve már nincs-e
online. Regisztrációnál ellenőrzi, hogy a szükséges mezők egyediek-e (email,
username), ha az adatok megfelelőek akkor, létre hozz egy új sort az
adatbázisban. Ha sikeres a belépés akkor az adatbázis alapján legenerál egy új
játékost és átadja a WaitingRoom osztálynak. Hibaesetén értesíti a klienst.

### WatingRoom osztály

Ez az osztály a klienstől várja a választ arra vonatkozóan melyik asztalhoz szeretne
csatlakozni, majd a szerveren keresztül megpróbál csatlakozni az asztalhoz, ha nem
sikerül, akkor az osztály tovább vár egy megfelelő válaszra.

```
Fontosabb Adattagok:
```
- GameServer server: A játékszerver
- Player player: A játékost szimbolizáló változó
A konstruktor elmenti, illetve beállítja az adattagokat.

```
Fontosabb Eljárások:
```
- getChoice() : Feldolgozza a kliens válaszát addig fut ameddig nem választ egy
    olyan asztalt, amihez tud csatlakozni.


## A server.chatServer

### ChatServer és ChatServerListener osztály

```
41. ábra
```
Ez a két osztály majdnem teljesen megegyezik a gameServer osztályban található két
játék osztállyal csak funkcióját tekintve a chat-ért felelős.

### ChatPartner osztály

```
42. ábra
```

Egy kliens üzenet feldolgozásáért felelős osztály. Először beállítja a kliens nevét utána
a tőle jövő üzeneteket továbbítja a játékosnak. Az osztály addig fut ameddig a socket
kapcsolata aktív.

## A server package

### BCrypt és BcryptHashing osztáy

Nem saját osztályok. A jelszó titkosításáért, illetve a titkosított jelszó
összehasonlításáért felelős osztályok. Copyright (c) 2006 Damien Miller
djm@mindrot.org.[ 7 ]

### DatabaseInitalizer osztály

43_. ábra_
Ez az osztály felel az adattáblák létrehozásáért, a localhost 3306 portján működik.
Létrehozza a szükséges táblákat (törli a régieket) illetve feltölti teszt játékosokkal. Egy


JDBC connectorral csatlakozik az adatbázishoz és a teszt adatokat a DAO-n keresztül
hozza létre.

## A dao package

### DefaultDao<T extends Player> osztály

```
44. ábra
```
A Player játékoshoz tartozó DAO objektum, ami az adatbázis műveleteket bonyolítja
le. Egy JDBC connector-ral csatlakozik a mySQL adatbázishoz.

```
Fontosabb adattagok:
```
- static final String DATABASE_URL: az adatbázis URL-je (localhost 3306
    portján működik).


Fontosabb Függvények:

- T findPlayerByUsername(String username): Megkeresi az adott
    felhasználónevű játékost és visszatér vele.
- T findPlayerById(Integer id): Megkeresi az adott azonosítójú játékost.
- String alreadyContains(String username,String email): Megvizsgálja, hogy
    szerepel-e az adatbázisban az email vagy a jelszó és egy hibaüzenettel tér
    vissza ha igen ,ha nem egy konfirmációsüzenettel.
- List<String>findLoginDatas(String username):Megkeresi az adott felhasználó
    névhez tartozó adatokat.

Fontosabb Metódusok:

- delete(int id): Törli a megadott azonosítóval rendelkező felhasználót az
    adatbázisból.
- update(T entity): Az adott felhasználó adatait frissíti az adatbázisban a
    paraméterben kapott objektum alapján.
- createPlayer(String username,String password,String email,String name,String
    birthDate): Létrehoz egy játékost az adatbázisban.


### PlayerDao extends DefaultDao<Player> osztály

45_. ábra_
A DefaultDao osztályt terjeszti ki. Itt tároljuk az adatbázis kapcsoltakhoz szükséges
SQL utasításokat. A statikus adattagok az SQL utasítások. Ezen kívül egy adattag van a
játékosok regisztráláskor kapott pénze az int startingMoney. Tartalmazz egy statikus
DefaultPlayerDaoHolder osztályt melynek egy INSTANCE adattagja van, ami egy
PlayerDao példánnyal tér vissza;

```
Fontosabb Függvények:
```
- static PlayerDao getInstance() : Az osztály pédányosításával tér vissza.
- Player fromResultSet(ResultSet resultSet): A resultSet halmazból létrehoz egy
    játékost.
- Statement fromEntity(String query, Player player, boolean withId): Egy
    játékos adataiból létrehoz egy Statement-et egy adatbázisművelethez.


- Statement fromDatasDataTable(String username,String password,String
    email,String name,String birthDay): A paraméterekből létrehoz egy Statement-
    et egy adatbázisművelethez.
- Statement fromDatasPlayerTable(ResultSet resultSet): A resultset halmazból
    létrehoz egy Statement-et a player táblából egy adatbázisművelethez.

## A view.panels

### Absztrakt PanelBox extends VBox osztály

```
46. ábra
```
Ez az osztály felel a GUI irányításáért felelős panelekért. Ebből az osztályból
származnak le az irányitó panelek.

- BetBox osztály: felelős a tétrakás folyamatáért
- BothBox osztály: A hit, stand, double down vagy split hand opciók választásáért
- ContinueBox osztály: A játék végén a folytatásért
- DoubleDownBox osztály: A hit, stand, double down opciók választásáért
- HitStayBox osztály: A hit, stand opciók választásáért
- InsuranceBox osztály: Biztosításért felelős


```
47. ábra
```
További panelek:

- ChatBox osztály: A chat megjelenítésére szolgáló panel
- DealerBox osztály: Az osztó lapjait tartalmazó, illetve az információs panelt
    tartalmazó panel.
- ChatPane osztály: A ChatBox része az üzeneteket megjelenítő panel.
- MessageSendBox osztály: A ChatBox része ebből a panelből küldhetünk üzenetet.
- InfoBox osztály: Az információk kiírásáért felelős osztály.
- ControllerEmpty osztály: Egy szegéllyel rendelkező AnchorPane egy üres panel,
    ami akkor jelenik meg a játékosnak, ha nem ő a soron következő.


48_. ábra_


### Table osztály és a TableView osztály

```
49. ábra
```

A Table osztály felelős az GUI megjelenéséért ez az osztálya írja le az asztalt és a
modell-en keresztül küldi el az utasításokat a szervernek és a TableView osztály jeleníti
meg.
Fontosabb Adattagok:

- final ClientController controller: Az irányitóosztály
- final ChatModel chatModel: Chat modellje
- final int id: A játékos és egyben a panel azonosítója
- HBox playersBox: A játékosokat megjelenítő box
- ObservableList players: Az előző adattag módosítólistája
- VBox controllBox egy üres boksz ennek a helyére kerülnek az
    írányitópanelek

```
A következő adattagok a fentebb említett panelek példányai:
```
- MessageSendBox sendBox;
- BetBox betBox
- BothBox bothBox
- HitStayBox hitStayBox
- InsuranceBox insuranceBox
- DealerBox dealerBox
- DoubleDownBox doubleDownBox
- ContinueBox continueBox

```
Fontosabb Eljárások:
```
- init(): Tábla előszítése.
- addNewPlayer(PlayerBox playerBox): Új játékost ad a táblához.
- addChatBox(ChatBox chatBox): ChatBox-ot ad a táblához
    A következő eljárások a nevében szereplő funkciókat végrehajtó paneleket
    cseréli a tábla bal oldalán:
       o setBet()
       o setInsurance()
       o setDoubleDown()
       o setBoth()


```
o setHitStay()
o setContinue()
```
- changePlayerBox(String id,PlayerBox playerBox): Lecseréli az id azonosítójú
    playerBoxot a paraméterben megadottra.
- removePlayer(String id): Törli az adott azonosítójú játékos panelját.
- addCardToDealerHand(String url): Egy kártyát generál és az osztó kezébe adja
- setMoneyForPlayer(int id, Double money): Beállítja az adott azonosítójú
    játékos pénzét.
- addCardToPlayerFirstHand(int id, String url): Egy kártyát generál és az adott
    azonosítójú játékos kezébe adja.
- newRound(): Felkészíti a táblát egy új körre.
- setUpPanels(): A játék során használt panelek létrehozása.
- setDealerHandValue(String value): Az osztó lapjainak értékének beállítása
- setInfo(String info): Információs panel beállítása.
Fontosabb Függvények:
- PlayerBox findPlayerById(int id): Az adott indexű játékos paneljával tér vissza
- int findIndex(int id): Megkeresi az adott azonosítójú játékos players lista
indexét.
- boolean exit(): Bezárja a GUI-t.


### Lobby és a LobbyView osztály

```
50. ábra
```
Ez a két osztály felel a Lobby kinézetért és megjelenéséért, érdemi számítás nem
történik csak megjelenítés, illetve adattárolás.


### PlayerBox osztály

```
51. ábra
```
```
Ez az osztály jelenít meg egy játékost az asztalon.
Fontosabb Adattagok:
```
- final String username: Felhasználónév
- final Integer id: Azonosító
- final String money: A játékos pénze.
    Megjelenítő címkék:
       o Label minimumLabel
       o Label usernameLabel
       o Label handValueLabel
       o Label betLabel
       o Label winLabel


```
o Label moneyLabel
```
```
Játékosok kezeit megjelenítő Boxok:
o HBox firstCardHolder
o HBox secondCardHolder
```
- VBox infoBox : Információs panel
- VBox splitHand: Nincs implementálva a split hand
    Módositó listák:
       o ObservableList playerBoxList
       o ObservableList firstCardList
       o ObservableList secondCardList
       o ObservableList infoList
       o ObservableList splitHandList
- int firstHandValue,secondHandValue: Adott kéz kártyáinak értéke
- StringBuilder winMessage: Győzelem esetén a szöveg
- primaryScreenBounds: A megjelenítő monitor méretei

A konstruktor PlayerBox(int id,String username,String money) elmenti a játékos
adatait ezután előkészíti a panelt az init()-tel.

```
Fontosabb Eljárások:
```
- setHandValue(String index,String value): Beállítja az adott indexű kéznek az
    értékét.
- setBetValue(String bet): Beállítja az adott licitet a kézre.
- addCardToFirstHand(Label card): Hozzáad egy lapot a játékos kezéhez.
- addHand(String index): Hozzá ad a játékoshoz még egy kezet(paklit) az adott
    indexre.
- removeHand(String index): Eltávolítja a megadott kezet.
- removeCardFromHand(String handIndex): Eltávolítja az utolsó lapot a megadott
    kézből.
- addCardToHand(String index,String url): Hozzáad egy lapot a megadott kézhez.
- addCardToSecondHand(Label card): Hozzáad egy lapot a játékos 2.kezéhez
- newRound(): Törli a játékos kezeit.


## A login package

Az itt található osztályok és fxml fájlok a bejelentkezési ablakokat tartalmazzák,
csak adatokat kérnek be, illetve, ha szervertől hibaüzenetet kapnak megjelenítik azt a
felhasználó számára.

### Login osztály

```
52. ábra
```
Ennek az osztálynak a main metódusának meghívásával indul el a program. A start
metódus jeleníti meg szerver adatainak megadására szolgáló ablakot.

#### IpController osztály

```
53. ábra
```
A szerver adatainak megadására szolgáló ablak controller osztálya. Ha sikeres a
csatlakozás a szerverhez, akkor a megjeleníti a bejelentkezési felületet.


#### LoginFxmlcontroller osztály

```
54. ábra
```
A bejelentkezési felület controller osztálya. Ha sikeres a bejelentkezés, ez az osztály
indítja el a LobbyController-t. Ha a regisztráció gombra kattintunk megjeleníti a
regisztrációs felületet.

#### RegisterFxmlController osztály

```
55. ábra
```
A regisztrációs felület controller osztálya. Ha a játékos a visszalépés gombra kattint
vagy sikeres a regisztráció a bejelentkezési feletet nyitja meg.


#### MessagePopUp osztály

```
56. ábra
```
```
Ez az osztály felelős a felugró ablakok előállításáért.
```
### A client package

#### TimeOut és TimeOutTask osztály

```
57. ábra
```
A TimeOut osztály meghívásakor létrehoz egy időzítőt, ami 15 másodperc elteltével
lép a játékos helyett a paraméterben kapott parancsot átadja a TimeOutTask-nak ami
elküldi a szervernek, leállítja az időzítőt és az irányitó panelt kiüríti.

#### GUITimer osztály

```
58. ábra
```
```
Az információs panelben megjelenő időzítő.
```
#### CardFactory osztály

```
59. ábra
```

Ez az osztály felel a kártya grafikus előállításáért, a konstruktor kap egy URL-t és az
alapján megkeresi a laphoz tartozó png-fájlt.

#### LobbyController osztály

```
60. ábra
```
```
Ez az osztály gyűjti össze az adatokat Lobby felállításához.
```
```
Fontosabb Adattagok:
```
- List<String> datas: Játékos adatai
- List<List<String>> leaderBoardDatas: Leaderboard adatai
- List<List<String>> tableDatas: Asztalok adatai
- String name,money,win,lose: A játékos adattáblában tárolt adatai
- final ChatModel chatModel
- ChatBox chatbox
Két konstruktora van az első LobbyController(Socket gameSocket, Socket chatSocket,
BufferedReader in, PrintWriter out) akkor hívódik meg, amikor a játékos bejelentkezik.


A második LobbyController(Socket gameSocket, BufferedReader in, PrintWriter out,
ChatModel chatModel) amikor a játékos kilép a játékból.
Fontosabb Eljárások:

- setChatMessage: Chatablak hozzáadása.
- refresh(): Adatok frissítése.
- getPlayerInfo(): Adatok lekérdezése.
- getTableListInfo(): A táblaadatok frissítése.
- getLeaderBoardInfo(): A ranglista adatok frissítése..
- getServerMessage(): Szerverüzenet.
- joinTable(int tableNum): Csatlakozás játékasztalhoz.
- sendToServer(String message): A szerverüzenet küldése.
- leaveGame(): Játék elhagyása.

#### ClientModel osztály

```
61. ábra
```
Ez az osztály tárolja a játékosok adatait és ez alapján frissíti a GUI-t a controller-
osztály.

```
Fontosabb Adattagok:
```
- LinkedList<PlayerBox> playersPanel: itt tárolódnak a játékosok
Fontosabb Függvények:
- String getServerMessage(): A szerverüzenet fogadás.
- ObservableList getHand(String playerId, String index): Az adott játékos adott
kezével tér vissza.


- PlayerBox getPlayerBox(String playerId): A játékost szimbolizáló panellel tér
    vissza.
Fontosabb Eljárások:
- sendClientMessage(String message): Válasz küldése.
- addHandPanel(String playerId,String index): Játékos kezét szimbolizáló panel
hozzáadása.
- removeHandPanel (String playerId, String index): Kezet szimbolizáló panel
eltávoltása.
- addPlayerBox(String id,String username,String money): Játékost szimbolizáló
panel hozzáadása.
- removePlayer(String id): A játékost szimbolizáló panel eltávolítása.

#### ClientController osztály

```
62. ábra
```
Ez osztály felelős a modell és GUI közötti kommunikációért, illetve a szerverüzenetek
feldolgozásáért.

```
Fontosabb Adattagok:
```
- ClientModel: Model
- TableView: View
- final int tableNum: Asztal sorszáma
- Table table: Asztal
- ChatModel: ChatModel
- final Socket: GameSocket
- final LobbyController: LobbyController
- CountDownLatch latch: A játék végén nullázódó latch.
- TimeOut timer: Minden választási lehetőségnél elinduló időzítő.
- GUITimer guiTimer: A GUI-n megjelenő idözítő.


```
Fontosabb Eljárások:
```
- updateGUI (String message): A GUI manipulációjáért felelős függvény az
    üzenet alapján.
- getServerMessage(): Szerverparancsok feldolgozása és a GUI módosítását
    meghívó elájárás.
- sendClientMessage(String clientMessage): A válasz küldése a
    szervernek.Időzitő leállítása.
- gameOver():A játék végén lefutó függvény, visszairányít a lobbyba.
- leaveGame():A játékból való kilépéskor lefutó függvény.
- startTimers(String command): Ez az eljárás indítja el az időzítőket.

#### ChatThread és ChatModel osztály

```
63. ábra
```
Ez a két osztály felelős a chat működésért alapjában megegyezik a játékszerver
működésével csak annyi funkcionális különbséggel, hogy egyetlen feladat a kliensek
közti szöveges kommunikáció.


## Tesztelés

### Bejelentkezés

```
Esemény Várt eredmény
csatlakozás localhost: 4444,4445 portra
(nem fut a szerver a megadott helyen)
```
```
Hiba üzenet: nem található a szerver
```
```
csatlakozás localhost: 4444,4 445 portra(fut a
szerver a megadott helyen)
```
```
Bejelentkezési ablak
```
```
Bejelentkezés nem létező felhasználóval Hiba nem létezik a felhasználó
Bejelentkezés rossz jelszóval
Hiba rossz jelszó
```
```
Regisztráció helyes adatokkal Üzenet jelentkezzek be (adatbázis frissült)
```
```
Regisztráció helytelen adatokkal (végig
próbálva üresmezőkre illetve nem egyező
jelszó vagy email párosra)
```
```
hiba írja be a hiányzó mezőt vagy a két cella
nem egyezik
```
```
Regisztrációval már létező
felhasználónévvel majd már létező e-mail
címmel
```
```
Mindkét esetben hiba, már létezik a
felhasználónév vagy az e-mail cím
```
```
Bejelentkezés már online felhasználóval Hiba valaki már bejelentkezett
Előző esett után kilépés a játékból, majd újra
próbálkozás
```
```
Lobbyba jutunk
```
```
Regisztrációs felületen a vissza feliratra
kattintás
```
```
Visszalép a bejelentkezésre.
```

### Lobby

```
Esemény Várt eredmény
Chat üzenet megjelenik az üzenet
Leaderboard fejlécének végig kattintása a táblázat módosul az oszlopnév szerint
csatlakozás olyan asztalhoz amihez nincs
elég pénze a játékosnak
```
```
nincs elég pénze üzenet
```
```
Asztal kiválasztása majd Ok gomb
megnyomása
```
```
Csatlakozás a játékhoz
```
```
Asztal kiválasztása majd Ok gomb
megnyomása valaki játszik közben
```
```
Üzenet valaki játszik majd kör végén
Csatlakozás a játékhoz, a másik
játékosnak megjelenik az új játékos és
minden folytatódik.A csatlakozó
játékosnak megjelenik az ablakon lévő
játékos
Egyszerre csatlakozás az asztalhoz 3 új
játékossal
```
```
Először NullPointerExceptiont jelzett a
program kliens oldalon nem találta a
játékos panelját a Gui.Megoldott a
hibaforrása az volt, hogy a várakozók csak
a játékost értesítették, egymást nem.
Ezután minden játékos látta egymást.
```

### Játék

#### 2 vagy több játékosnál

```
Esemény Várt eredmény
Tét megtétele Vár a következő játékosra utána lapokat
oszt a szerver ezután hit, stand double
down ha nincs blackjack-je a játékosnak
Ha ő a soron következő játékos játék
hit/stand opció választása
```
```
Ha túlmegy a 21-en vagy megáll a
következő játékos következik vagy az
osztó
Osztó lapjai láthatóvá válnak Játék vége, ki nyert, pénz kiutalása,
folytatás panel
```
```
egyik játékos nem folytatja Eltűnik az asztalról
Egyik játékos kör elején kilép
A szerver végig játssza a játékot
```
```
Játék vége Mindkét játékos adatai frissültek az
adatbázisban
Kilépés játék közben A szerver lejátssza helyette a játékot,
adatok frissülnek az adatbázisban
Tét tevése Lapok után hit, stand, vagy double down
opció,összeg levonása
Double down opció Egy lapot kap és dupázódik a játékos tétje
2 játékos 2 különböző asztalon játszik Minden megfelelően működik,nincs
szerver probléma
Egy körben több játékos elhagyja a játékot A játékosok listáján az egyik Player
osztály üzenetet szeretet volna
küldeni,miközben egy játékos
eltávolította magát ez egy
ConcurrentModificationException
kivételt generált. A kivétel kezelve lett,
ezután a probléma már nem állt fent.
```

```
Valamilyen opció választása A számláló leáll, új opciónál újraindul
Belépés a játékba, majd nem választottam
a lehetőségek közül.
```
```
15 másodperc múlva feltette a minimális
tétet a szerver, a lapok megkapása után az
én körömben 15 másodperc után megállt,
a kör végén 15 másodperc után ledobott az
asztalról a szerver.
```
### Chat

```
Esemény Várt eredmény
Belépéskor Lobbyban chat üzenet Mindkét játékosnak megjelenik
Játék megkezdése után üzenet Mindkét játékosnak megjelenik
```

## Fejlesztési lehetőségek

A játék során nem sikerült implementálnom a split hand funkciót,ami egy olyan
lépés ,ami akkor válik elérhetővé ha a játékos első kétlapjának mintája megegyezik(pl.:
Ász-Ász), ekkor ketté oszthatja a lapjait és két kezére külön játszhat. A másik ilyen
funkció, ami nem minden helyen elfogadott a Surrender opció ez könnyeben
implementálható, ennek az opciónak a kijátszásával az első két lap után feladhatjuk a
játékot ilyenkor a megtett tét felét visszakapjuk és a maradék a bankhoz kerül.
A játék során feltételezzük, hogy ha valaki játszik ő vagy a kör végén kiszáll vagy
kilép a játékból, de semmiképp nem hagyja ott a játékot nyitott ablaknál, mert a többi
játékos ekkor vár rá, hogy megtegye a lépését, ezt kiküszöbölendő egy időzítőt lehetne
implementálni a szerver oldalra, ami egyszerűen tovább lépteti a játékot. Ezen felül
különböző kártyajátékokat is lehetne implementálni a játékba kiterjesztve a programot
egy Casino-vá illetve webesfelületre átültetni a játékot.
A grafikus felületen lehetne animációkat alkalmazni az osztásra, illetve
realisztikusabban ábrázolni az asztalt például zsetonok használatával.


## Hivatkozások

https://hu.wikipedia.org/wiki/Huszonegy
1 .Játék szabályok
Elérhető: 2019.május

https://www.callicoder.com/javafx-registration-form-gui-tutorial/
2 .Creating a registration form in JavaFX
Elérhető: 2019.május

https://people.inf.elte.hu/mozsik/2016172/progtech2/
3 .Mózsi Krisztián honlapja
Elérhető: 2019.május

[http://kitlei.web.elte.hu/](http://kitlei.web.elte.hu/)
4 .Kitlei Róbert honlapja
Elérhető: 2019.május

https://www.apachefriends.org/hu/index.html
5 .XAMMP letöltési hely, illetve telepítési útmutató
Elérhető: 2019.május

https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller
6 .Model–view–controller
Elérhető: 2019.május

[http://www.mindrot.org](http://www.mindrot.org)
7 .BCrypt
Elérhető: 2019.május

https://stackoverflow.com/
8.
Elérhető: 2019.május

https://github.com/
9.
Elérhető: 2019.május

https://github.com/teddyteh/Multiplayer-Card-Game
10.Multiplayer-Card-Game by teddyteh
Elérhető: 2019.május

https://gluonhq.com/products/scene-builder/
11 .Scene Builder alkalmazás oldala
Elérhető: 2019.május


