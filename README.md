# Beispiele für Solr-Plugins

## Beschreibung

Dieses Projekt enthält einige funktionierende Solr-Plugins (für Solr 5.4).
Die Plugins sind an sich wenig sinnvoll, sie dienen vielmehr der Veranschaulichung
der Funktionsweise von verschiedenen Plugin-Arten. Der Code kann als Basis für 
umfangreichere Plugins genutzt werden.

## Kompilation

Es werden mindestens Java 7 und Maven 3 benötigt.

Die Kompilation wird gestartet mit:

``` mvn clean package -DskipTests ```

Die Unit-Tests müssen im ersten Lauf übersprungen werden. Die kompilierte 
.jar-Datei wird nicht im target-Verzeichnis erstellt, sondern direkt in 
dem Core-Verzeichnis der eingebetteten Solr-Instanz. Die .jar-Datei muss 
an dieser Stelle sein, damit die Unit-Tests ausgeführt werden können. Der 
zweite Lauf kann dann die Tests mitausführen:

``` mvn clean package ```