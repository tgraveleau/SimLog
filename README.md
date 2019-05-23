# SimLog
Projet SimLog de Jean-Michel Richer repris et amélioré. Ce projet utilise Maven pour gérer la compilation, gestionde dépendances et création d'éxécutables.
Il est nécessaire de posséder une version de Maven au moins égale à la 3.5.
Il faut se placer dans le dossier du projet avant d'éxécuter les commandes suivantes

# Dépendances 
Les dépendances présente dans ce projet sont les suivantes:
* JUnit version 3.8.1
* Opencsv version 4.1

# Documentation
Pour compiler:

`mvn compile`

Création d'éxécutable :

 `mvn package`

Pour exécuter:

`java -jar *nom de l'éxécutable*`
