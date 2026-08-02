# geneanet-scraper

_**Heavy refactoring in progress!**_

Outil d'extraction d'un arbre présent sur [Geneanet](https://geneanet.org/) puis conversion en fichier [Gedcom](https://fr.wikipedia.org/wiki/GEDCOM).

## Mise en garde

Le [web scraping](https://fr.wikipedia.org/wiki/Web_scraping) est une pratique visant à récolter des données d'un site web en automatisant les processus de navigation et de collecte.
Le droit d'auteur s'applique sur internet et la réutilisation des données récoltées est soumise à des conditions.
De plus cette méthode peut engendrer un traffic important et ainsi nuire au site en question.
La plateforme peut se réserver le droit de sanctionner cette pratique comme elle le souhaite. En fournissant votre cookie de connexion, la plateforme est capable de faire le lien avec votre compte.
Par ailleurs cet outil est susceptible de ne pas fonctionner comme attendu, ou de ne pas fonctionner du tout.

L'auteur ne pourrait en aucun cas être tenu responsable des potentiels problèmes engendrés par l'utilisation de cet outil (mais espère néanmoins qu'il sera utile aux utilisateurs responsables).

## Prérequis

- [sbt](https://www.scala-sbt.org/) (le projet cible Scala 2.12.10, voir [`build.sbt`](build.sbt))
- Un compte Geneanet avec accès à l'arbre à extraire

## Configuration

Toute la configuration se fait actuellement en modifiant directement [`Main.scala`](src/main/scala/gscraper/Main.scala) avant de lancer le projet :

```scala
val cookieValue = ""   // Cookie de session Geneanet (voir ci-dessous)
val userAgent = ""     // User-Agent à utiliser pour les requêtes HTTP
val url = ""           // URL de la fiche de la personne de départ
```

- **`url`** doit être l'URL d'une fiche individu Geneanet en français (`lang=fr`) — un `assert` au démarrage bloque toute autre locale.
- **`cookieValue`** s'obtient en étant connecté à Geneanet dans un navigateur, puis en copiant l'en-tête `Cookie` envoyé par le navigateur (outils de développement, onglet réseau, sur une requête vers `geneanet.org`). Ce cookie identifie votre compte : voir la mise en garde ci-dessus.
- **`userAgent`** doit correspondre au User-Agent de ce même navigateur.

### Filtrage des personnes à visiter (`pathMatcher`)

```scala
val pathMatcher = "A*D*S?".r
```

Pendant le scraping, chaque personne visitée porte un chemin de relation construit à partir de la personne de départ, avec un caractère par lien parcouru :

- `A` : ancêtre (parent)
- `D` : descendant (enfant)
- `S` : conjoint(e)

`pathMatcher` est une regex appliquée à ce chemin pour décider si une personne doit être visitée. L'exemple par défaut (`A*D*S?`) autorise : uniquement des ancêtres, puis uniquement des descendants, avec au plus un conjoint à la fin — ce qui correspond à l'arbre direct d'un individu et aux conjoints de ses ancêtres/descendants directs, sans repartir dans les branches des conjoints. Adapter cette regex permet d'élargir ou de restreindre l'arbre extrait.

## Utilisation

```bash
sbt run
```

Le scraping démarre depuis `url`, explore récursivement parents, enfants et conjoints selon `pathMatcher`, puis écrit le résultat au format GEDCOM dans `test5.ged` à la racine du projet (nom de fichier actuellement fixé dans `Main.scala`).

La progression est affichée sur la sortie standard : `[I: <individus>, F: <familles>, I': <requêtes en cours>] Received <url>`. En cas d'erreur (page bloquée par le pare-feu Geneanet, page inattendue, etc.), le scraping s'arrête et la trace de l'exception est affichée.
