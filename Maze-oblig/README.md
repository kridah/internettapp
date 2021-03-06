# Maze - oblig i internettapplikasjoner H2020
@author Kristoffer Dahl - kda068

## Systemkrav
Kodens GUI kjører i en Java applet*. Støtten for applet ble fjernet etter Java 9.0.4. Du må derfor ha denne
 versjonen for å kjøre programmet.
En bug i Applet gjorde at vinduet med labyrinten ble tegnet flere ganger i samme vindu. 
Endret implementasjon til JApplet som har løst problemet.
 
## Installasjon
-- IntelliJ --\
I utviklingen av koden brukte jeg IntelliJ IDEA 2020.2. Denne oppgaven er lagt opp til å være en modul i et prosjekt.
Java SDK må settes til versjon 9.0.4. Denne kan lastes ned fra Java-arkivet til Oracle.
Da skal det være å bygge prosjektet og kjøre følgende klasser i gitt rekkefølge;
 - RMIServer
 - Maze
 - PlayerSimulator

Maze og PLayerSimulator skal kunne kjøres i parallell, ie. flere GUI og simulerte spillere kan legges til under kjøring,
så lenge RMIServer kjører.

-- Eclipse --\
Eclipse-installasjonen på mitt system nekter å finne/importere flere av modulene som koden krever.
Jeg har derfor ikke kjørt eller testet koden i Eclipse.
 
## Kjøring
RMIServer-klassen starter en tjenerinstans på port 9000 som klienter kan koble seg til. Tjenerens standardadresse
er 127.0.0.1 eller localhost. Man kan konfigurere annet tjenernavn i koden.
Maze-klassen tegner opp GUI av labyrinten. En bruker blir opprettet i samme klassen. Denne er ikke mulig å styre
manuelt, men den går på autopilot.

PlayerSimulator-klassen lar deg sette inn en gitt mengde virtuelle spillere. Antallet må endres i koden under parameteret
NUMBER_OF_CLIENTS. Oppgaven krever minimum 20 klienter. Ved mer enn 500 klienter virker det som systemet begynner
 å slite med store forsinkelser i oppdateringen.
I det jeg skulle skrive om systemressurser, og hvor tilsynelatende lav CPU bruk var (30-40 %), samt minne bruk (~300 MB per 
 instans av PlayerSimulator med 100 klienter), så fikk systemkjernen panikk, og krasjet.
 
## Løsningen
Min løsning benytter en tjener/klient-løsning hvor tjeneren holder kontroll på hver enkelt klient (hver klient er en tråd)
 og tjeneren sender oppdatering til alle klientene for hvert trekk. Alle klientene er automatisert, og beveger seg samtidig.
 Det sendes et callback til klienten hvert definerte tidsintervall. Jeg har satt det til 500 millisekund i min kode.
 Denne løsningen er suboptimal, fordi det betyr at tjeneren må sende en melding til hver klient. En bedre løsning ville vært
  å bruke multicast, hvor tjeneren sender ut en melding som er lik for alle klientene. Alle klientene skal jo ha samme 
  informasjon uansett.
  
Klienter som har gått gjennom labyrinten logges ut av tjeneren for å frigi ressurser.
 
## Dokumentasjon
Koden er skrevet for å være selvforklarende. Kommentarer er lagt til hvor det ikke nødvendigvis er opplagt hva som skjer.

## Kjente feil
 - Det kan oppstå ConcurrentModificationException. Da må RMIServer startes på nytt
 - Maze tegnes ikke opp.
 - Maze tegnes flere ganger i samme vindu. Endret fra Applet til JApplet i Maze-klassen ser ut til å ha løst problemet.
 - Starter man med >= 200 klienter i PlayerSimulator, så kan det skje en exception. Løses ved å kjøre PlayerSimulator igjen.
 