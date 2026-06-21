JAK ZAINSTALOWAĆ (za pomocą Mavena)

1. Utwórz projekt za pomocą Mavena
2. Dodaj zależności w pliku pom.xml
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>

<dependencies>
<dependency>
	    <groupId>com.github.derowic</groupId>
	    <artifactId>Game_Library_Java_Swing</artifactId>
	    <version>Tag</version>
</dependency>
</dependencies>
```
3. Odśwież Mavena, aby zainstalować (lub kliknij „Instaluj”)

4. how export to fatJar
   Otwórz terminal w folderze projektu.
   Wpisz: mvn clean package.
   W folderze target/ znajdziesz plik np. MojaGra-1.0-SNAPSHOT.jar.
   Możesz go sprawdzić wpisując: java -jar nazwa_pliku.jar. Jeśli gra ruszy – połowa sukcesu za Tobą!


Opcja A: Launch4j (Najprostsza na Linux Mint)
Launch4j to program, który "owija" JARa w EXE.
Zainstaluj go: sudo apt install launch4j.
Uruchom i ustaw:
Output file: MojaGra.exe
Jar: Twój plik z folderu target/.
JRE Tab: W polu "Min JRE version" wpisz 17 (lub wersję, której używasz).
Kliknij ikonę koła zębatego, aby zbudować EXE.
Uwaga: To rozwiązanie wymaga, aby gracz miał zainstalowaną Javę na Windowsie.


Opcja B: jpackage (Profesjonalna - "Wbudowana Java")
To narzędzie (dostępne w JDK od wersji 14) dołącza do gry mini-wersję Javy. Gracz nic nie musi instalować. Ponieważ jesteś na Linuxie, a chcesz EXE, najlepiej użyć darmowego serwera GitHub Actions, aby zbudował to za Ciebie na Windowsie.
Jeśli jednak masz dostęp do komputera z Windowsem, wpisz tam w konsoli:

jpackage --name "MojaGra" --input . --main-jar MojaGra.jar --main-class pl.sgl.engine.Main2 --type exe --win-shortcut --icon resources/icon.ico