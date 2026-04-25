JAK ZAINSTALOWAĆ (za pomocą Mavena)

1. Utwórz projekt za pomocą Mavena
2. Dodaj zależności w pliku pom.xml

3. Odśwież Mavena, aby zainstalować (lub kliknij „Instaluj”) <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

<dependency>
	    <groupId>com.github.derowic</groupId>
	    <artifactId>Game_Library_Java_Swing</artifactId>
	    <version>Tag</version>
	</dependency>