---
title: "MenuSwipe"
subtitle: "Dossier de lancement — M1 MIAGE Apprentissage — v0.1.0-SNAPSHOT"
author:
  - "Hajar Jelthi, Rayane Kachbi"
date: "20 octobre 2026"
---

<!--
Dossier R1 : quatre pages, hors annexe selon les consignes transmises.
Compilation : make -C doc
Confirmer le statut GitHub Pages et la validation de l'équipe.
Vérifier la pagination avec le style.css du modèle.
-->

# Équipe, comptes développeurs et dépôt

| Membre | Compte GitHub | Email des commits (`git config user.email`) |
|---|---|---|
| Hajar Jelthi | [@Hajar-JLT2](https://github.com/Hajar-JLT2) | jelthihajar@gmail.com |
| Rayane Kachbi | [@Rayanekachbi](https://github.com/Rayanekachbi) | kachbirayane27@gmail.com |

**Dépôt :** <https://github.com/Rayanekachbi/menuswipe>  
**Page de projet :** URL et statut GitHub Pages à confirmer avant le dépôt.

Nous vérifierons que nos emails Git sont associés à nos comptes GitHub. Nous sommes actuellement deux dans l’équipe ; nous devons encore régulariser sa composition avec l’équipe enseignante.

Nous réalisons chacun nos fonctionnalités de bout en bout, sans séparation des rôles front/back. Nous prévoyons une branche et une pull request par feature, avec relecture croisée.

# Projet et caractère innovant

**Problème et public.** MenuSwipe s’adresse aux étudiants et actifs souhaitant organiser leurs repas selon leurs goûts, leur budget et les ingrédients disponibles. Le besoin retenu est de coordonner les recettes de la semaine pour limiter les achats et les restes de conditionnements.

**Proposition.** L’utilisateur découvre des recettes sous forme de cartes qu’il apprécie ou rejette. L’application adapte les recommandations et calcule un menu sous contraintes. Un « j’aime » actualise les préférences ; seul l’ajout confirmé au menu réserve des ingrédients.

**Entité pivot : le menu hebdomadaire.** Ses recettes alimentent les calculs de quantités, de substitutions, de courses et de préparation. Une recette pourra être recommandée parce qu’elle réutilise un ingrédient déjà mobilisé par un autre repas.

**Différenciation recherchée.** Le projet associe découverte personnalisée et planification globale explicable. Il calcule des sélections, affectations et ordonnancements au-delà de la consultation de recettes. La comparaison avec les solutions existantes reste à approfondir ; aucune nouveauté absolue n’est revendiquée.

L’objectif est de réduire les restes estimés, sans garantir zéro gaspillage. Les recettes, prix et plans de magasin seront des données de démonstration, sans connexion commerciale en temps réel.

# Scénarios d’usage

1. **F1 — Découverte.** En tant qu’utilisateur, j’apprécie ou rejette des recettes afin d’obtenir un flux adapté à mes goûts et au menu en cours.
2. **F2 — Composition.** En tant qu’étudiant, je renseigne mon budget, mon stock et le nombre de repas afin d’obtenir un menu limitant les achats et les restes.
3. **F3 — Substitution.** En tant qu’utilisateur, je demande un remplacement d’ingrédient afin d’adapter une recette à mes disponibilités.
4. **F4 — Préparation.** En tant qu’utilisateur, je renseigne mes équipements afin d’organiser plusieurs préparations sans conflit de ressources.
5. **F5 — Calendrier.** En tant qu’utilisateur, je renseigne les dates limites afin de placer les repas dans des créneaux compatibles.
6. **F6 — Courses.** En tant qu’utilisateur, je consulte une liste ordonnée selon un magasin de démonstration afin de réduire mes déplacements.

# Plan des features

Chaque feature traverse **la BD, le back, l’API REST et le front**. Elle comprend une migration Flyway, un algorithme métier, des endpoints et un scénario démontrable. Elle est portée par **une seule personne**.

Le plan correspond à l’effectif actuel : **2 membres × 3 itérations = 6 fonctionnalités majeures**, sous réserve de la régularisation du groupe.

| Version | Hajar Jelthi | Rayane Kachbi |
|---|---|---|
| **v0.1.0 — 24/11** | **F1. Flux adaptatif de recettes** — *LinUCB* | **F2. Composition du menu sous budget** — *branch-and-bound* |
| **v0.2.0 — 08/12** | **F3. Substitution d’ingrédients** — *filtrage métier et classement par similarité cosinus* | **F4. Organisation du batch cooking** — *ordonnancement par liste sous contraintes de ressources* |
| **v1.0.0 — 19/01** | **F5. Calendrier des repas** — *flot à coût minimal* | **F6. Parcours de courses** — *Dijkstra, plus proche voisin et amélioration 2-opt* |

**Périmètre.** F1 adapte les recommandations aux réactions et au menu. F2 choisit les repas en intégrant stock, paquets entiers et restes. F3 recherche des remplacements compatibles avec le contexte culinaire et les quantités, puis les soumet à confirmation. F4 ordonne les opérations selon leurs dépendances et les équipements disponibles. F5 affecte les repas aux créneaux admissibles, avec dates et coûts de placement fixés. F6 construit et améliore le parcours associé aux courses du menu.

**Cohérence.** Les six fonctionnalités se rattachent au menu hebdomadaire. Les modules échangent par leurs ports. La fiche F3 précisera le traitement des contraintes et la propagation des substitutions aux quantités et au menu, afin de dépasser un simple calcul de score.

**Limites et validation.** Les choix sont prévisionnels. Chaque fiche précisera complexité, alternatives, cas limites et invariants. Les recherches combinatoires seront bornées ; les heuristiques ne garantiront pas l’optimalité. Les tests couvriront budget, stocks non négatifs, exclusions et conflits de ressources, avec graines fixes et petites instances de référence.

# Architecture

**Front → API REST → cas d’utilisation et domaine → persistance → BD.**

| Couche | Choix | Remarques |
|---|---|---|
| Front | HTML, CSS, JavaScript natif | `front/` ; client de l’API REST uniquement |
| API REST | Spring Boot 4 / Spring MVC, OpenAPI / Swagger UI | `/api/v1` ; erreurs RFC 9457 `problem+json` |
| Back | Java 21, Spring Boot 4, Spring Data JPA, Bean Validation | Architecture hexagonale |
| BD | PostgreSQL 17 ; H2 pour les tests | Flyway ; une migration par feature |
| Qualité | JUnit 5, AssertJ, ArchUnit, JaCoCo, Codecov, SonarCloud | CI GitHub Actions |
| Conteneurs | Docker Compose | BD dès v0.1.0 ; application en v0.2.0 |

**Architecture hexagonale.** Chaque module distingue `domain` en Java pur, `application` avec ports et services sans Spring, `adapter` pour REST/JPA et `config` pour l’assemblage. Les dépendances pointent vers le domaine ; les modules échangent uniquement par leurs ports. ArchUnit contrôle ces règles.

**Modèle commun.** Menu, repas, recettes, ingrédients et quantités constituent le socle. Stocks, conditionnements, prix indicatifs et unités normalisées permettent les calculs. Le coût porte sur les paquets entiers à acheter ; les restes sont évalués par ingrédient. Les dates sont renseignées, sans inférence de sécurité alimentaire.

**Démonstration.** Le profil `demo` chargera quelques dizaines d’entrées via un script SQL répétable et idempotent dans `src/main/resources/db/demo/`.

**Qualité du code.** Clean Code, SOLID, objets valeur immuables, invariants à la construction et patterns justifiés : Strategy et Adapter lorsqu’ils répondent à un besoin. Les précautions OWASP comprennent validation, échappement, requêtes paramétrées et secrets hors dépôt. Le quality gate SonarCloud devra être vert.

**Livraison.** La CI exécutera build, tests, couverture et documentation. Les tags `v*` déclencheront la release du JAR et du PDF. `site/` alimentera GitHub Pages. La compatibilité PostgreSQL sera vérifiée en complément des tests H2.

# Démarche pour chaque feature

Les étapes s’enchaînent **dans cet ordre**, après validation de la précédente.

| Étape | Ce qu’on fait | Terminé quand |
|---|---|---|
| **1. Idéation** | Besoin, scénario, algorithme, complexité, alternatives, impacts BD/API et critères ; issue et fiche `doc/features/`. | Fiche validée par un autre membre. |
| **2. Back** | Migration, domaine Java pur, ports, services, persistance et assemblage. | Compilation et ArchUnit réussis. |
| **3. Tests** | Cas nominaux, limites, invariants, propriétés à graine fixe ; faux adaptateurs et persistance. | `./gradlew build` passe ; chaque critère est couvert. |
| **4. API REST** | Endpoints, DTO validés, erreurs, OpenAPI et tests API. | Swagger UI disponible ; tests verts. |
| **5. Front** | Écrans consommant uniquement l’API REST. | Scénario vérifié ; PR relue ; fiche et araignée complétées. |

# Protocole d’usage de l’IA

**Principe : l’IA nous assiste, nous restons les auteurs.** Nous devons comprendre, expliquer, modifier et refaire sans aide le cœur de nos fonctionnalités. Nous écrivons nous-mêmes le cœur des algorithmes.

Nous avançons étape par étape avec l’IA : elle explique avant de produire, puis nous décidons et vérifions que nous avons compris. Nous versionnerons nos règles dans `CLAUDE.md`.

| Activité | Ce que fait l’IA | Ce que nous faisons |
|---|---|---|
| Idéation et conception | Critique et propose des options | Nous choisissons, justifions et validons la fiche |
| Algorithme | Explique et relit après une tentative | Nous concevons et écrivons le cœur |
| Code technique | Propose un premier jet | Nous relisons chaque ligne, adaptons et vérifions |
| Tests | Suggère des cas | Nous définissons les propriétés et vérifions la détection des défauts |
| CI/CD et conteneurs | Propose workflows et Dockerfile | Nous comprenons, exécutons et gérons les secrets |
| Documentation | Structure et reformule | Nous vérifions les faits et validons |

**Nos règles.** Nous ne committons pas de code que nous ne comprenons pas, ne transmettons pas de secrets ni de données personnelles à l’IA, ne committons pas sous une autre identité et ne présentons pas comme humain un travail produit par l’IA.

**Traçabilité.** Dans chaque PR, nous précisons l’outil utilisé, nos demandes, les éléments repris, modifiés ou rejetés et les vérifications que nous avons réalisées. Le fichier `doc/part-ia.json` alimente le graphe en araignée.

| Niveau | Compétences | Plafond proposé |
|---|---|---|
| Haut niveau | Idéation, conception, spécification, algorithmique, validation | 30 % par axe |
| Bas niveau | Code, tests, CI/CD, rédaction, débogage | 70 % par axe |

Nous devons encore valider ces plafonds. Ils sont distincts de nos contributions réelles, que nous mesurons lorsque c’est pertinent ou estimons et expliquons sinon. Nous déclarerons tout dépassement. Ces plafonds ne nous autorisent pas à déléguer le cœur algorithmique.

**Usage pour le R1.** ChatGPT nous a aidés à ajuster la conception et a contribué à une grande partie de la rédaction initiale. Nous devons encore valider les valeurs par compétence après relecture.

**Documentation.** Nous compilons la source `doc/dossier.md` avec `make -C doc` pour produire `doc/dossier.pdf`, que nous versionnons ensuite. Nous déposons chacun individuellement le rendu collectif.

# Annexe : part IA / part humaine {#annexe-part-ia}

![](part-ia.svg){.araignee}

**Périmètre : préparation du dossier R1.** Nous avons apporté l’idée initiale. L’IA nous a aidés à l’approfondir, puis à rédiger et mettre en forme le dossier.

Les valeurs ci-dessous sont des estimations qualitatives proposées par l’assistant. Elles ne résultent pas de mesures et nous ne les avons pas encore validées.

| Activité | Part IA proposée | Part humaine complémentaire |
|---|---:|---:|
| Idéation et extensions | 40 % | 60 % |
| Conception | 60 % | 40 % |
| Spécification | 75 % | 25 % |
| Validation documentaire | 50 % | 50 % |
| Rédaction | 90 % | 10 % |

**Limites.** Nous calculons la part humaine par complément à 100 %. Notre validation documentaire ne signifie pas que nous avons testé le logiciel. Nous n’assimilons pas les activités non réalisées à une contribution humaine de 100 %.

**Suivi à compléter.** Le suivi du cours comporte également l’algorithmique, le code, les tests, la CI/CD et le débogage. Nous devons renseigner ces axes selon les travaux réellement réalisés et signaler les activités non évaluées.

**Dépassements.** Nos estimations dépassent les plafonds envisagés. Nous devons en discuter et les commenter honnêtement, sans les réduire artificiellement.

**Validation de l’équipe :** nous devons encore confirmer ces estimations ensemble.

<!--
Le graphique est généré depuis doc/part-ia.json.
Ne pas conserver les valeurs d'exemple du modèle.
Vérifier que le graphique et le tableau correspondent aux
valeurs finalement validées par l'équipe.
-->