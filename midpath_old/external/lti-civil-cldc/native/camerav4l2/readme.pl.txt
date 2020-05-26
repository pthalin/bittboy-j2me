Autor:  Marcin Rudowski
E-mail: mar_rud@poczta.onet.pl
Opis:   projekt w ramach przedmiotu ISR:
        Modul sterownika v4l2 dla serwera player

lista plikow:
 - camerav4l2/ - zrodla sterownika dla serwera player
 - cam-player/ - zrodla mini aplikacji przykladowej (wymagane biblioteki fltk)
 - clientlib/ - zrodla biblioteki latwego dostepu do nowych funkcji
 - dokumentacja.pdf - sprawozdanie z projektu
 - html/ - dokumentacja wygenerowana przez doxygen
 - bin/ - katalog ze skompilowana wersja poszczeglnych skladnikow:
    - driver/ - plik modulu camerav4l2.so wraz z przykladowym plikiem konfiguracyjnym
                example.cfg
    - lib/ - biblioteki klienckie (statyczne i dynamiczne)
    - include/ - pliki naglowkowe biblioteki
    - gui - przykladowa aplikacja wykorzystujaca wiekszosc nowych funkcji (wymagane fltk)

Kompilacja:
 - make - skompilowanie wszystkich skladnikow. Wyniki kopiowane do bin/
 - make clean - usuniecie wynikow kompilacji poza zawartoscia katalogu bin/
 - make doc - wygenerowanie dokumentacji do katalogu html/ na podstawie sterownika
   oraz bibliotek klienckich
