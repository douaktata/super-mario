# TP Design Pattern DÉCORATEUR - Super Mario Power-ups 🎮🍄

**Établissement :** ENICarthage (2ème année IRM)  
**Sujet :** Démonstration complète et innovante du Design Pattern **Décorateur** (Java JDK 11+ & Interface Web 3D).

---

## 📐 Architecture du Projet

Le projet sépare strictement le modèle de domaine (géré par Java) et la vue (rendu Web 3D par Three.js). Le JavaScript frontend ne contient **aucune logique de calcul de statistiques** : toutes les valeurs proviennent directement de l'API REST exposée par le serveur HTTP Java embarqué.

```
                  +-----------------------------------+
                  |        Navigateur Web (HUD 3D)    |
                  |     Three.js / Studio Animation   |
                  +-----------------------------------+
                                    |
                    GET /api/add?p=...  |  GET /api/state
                                    v
                  +-----------------------------------+
                  |     MarioServer.java (HTTP 8080)  |
                  +-----------------------------------+
                                    |
                                    v
            +-----------------------------------------------+
            |          Pattern DÉCORATEUR (Java)            |
            |                                               |
            |            <<interface>> Character            |
            |                      ^                        |
            |         +------------+------------+           |
            |         |                         |           |
            |       Mario             CharacterDecorator    |
            |   (Base: 100/10/10)               ^           |
            |                         +---------+---------+ |
            |                         |                   | |
            |                 SuperMushroom           FireFlower |
            |                 CapeFeather            StarDecorator|
            +-----------------------------------------------+
```

---

## 📁 Structure des Fichiers

```
TP_mario/
├── src/
│   └── mario/
│       └── decorator/
│           ├── Character.java              # Interface composant de base
│           ├── Mario.java                  # Composant concret (Sante=100, Puissance=10, Vitesse=10)
│           ├── CharacterDecorator.java     # Décorateur abstrait (délégation pass-through)
│           ├── SuperMushroomDecorator.java # Décorateur (+50 Sante)
│           ├── FireFlowerDecorator.java    # Décorateur (+30 Puissance)
│           ├── CapeFeatherDecorator.java   # Décorateur (+5 Puissance, +20 Vitesse)
│           ├── StarDecorator.java          # Décorateur (+100 Puissance, +15 Vitesse)
│           ├── Main.java                   # Classe de démonstration CLI
│           └── MarioServer.java            # Serveur HTTP embarqué (REST API & Fichiers Web)
├── web/
│   ├── index.html                          # Single-page Web 3D (HUD Galaxie, Three.js)
│   └── model/                              # Modèle 3D local (scene.gltf, scene.bin, textures/)
├── README.md                               # Ce guide
└── REPONSES.md                             # Réponses aux 6 questions théoriques du TP
```

---

## 📦 Installation et Modèle 3D

Les fichiers du modèle 3D doivent être situés dans le dossier `web/model/` :
- `web/model/scene.gltf`
- `web/model/scene.bin`
- `web/model/textures/`

*(Le projet embarque déjà ces fichiers et les copie automatiquement dans `web/model/`).*

---

## 🚀 Compilation et Exécution

Toutes les commandes s'exécutent depuis la **racine du projet** (`TP_mario/`).

### 1. Compilation des classes Java (Zéro dépendance)

```bash
javac -Xlint:all -d out src/mario/decorator/*.java
```

### 2. Démonstration CLI & Preuve de Non-Régression (`Main.java`)

```bash
java -cp out mario.decorator.Main
```

**Sortie attendue dans la console :**
```text
=== DEBUT TEST DECORATEUR SUPER MARIO ===
Mario | Sante=100 | Puissance=10 | Vitesse=10
Mario + Champignon | Sante=150 | Puissance=10 | Vitesse=10
Mario + Champignon + Fleur de Feu + Cape | Sante=150 | Puissance=45 | Vitesse=30
Mario + Champignon + Fleur de Feu + Cape + Etoile | Sante=150 | Puissance=145 | Vitesse=45

--- Preuve de non-regression ---
Base Mario | Sante=100 | Puissance=10 | Vitesse=10
=== FIN TEST DECORATEUR ===
```

### 3. Lancement de la Serveur Web & Interface 3D (`MarioServer.java`)

```bash
java -cp out mario.decorator.MarioServer
```

Le serveur démarre sur **`http://localhost:8080`** (ou `8081` si 8080 est déjà occupé par le système) et ouvre automatiquement votre navigateur web.

---

## 🌐 Endpoints de l'API REST Java

- `GET /api/state` : Renvoie l'état courant du personnage en JSON `{description, health, power, speed, stack, javaExpression}`.
- `GET /api/add?p=mushroom|fire|cape|star` : Applique un nouveau décorateur par-dessus l'état actuel et renvoie le nouvel état.
- `GET /api/reset` : Réinitialise le personnage au Mario de base.

---

## 🎨 Modèle 3D & Crédits CC-BY

Ce projet utilise le modèle 3D **"Mario Galaxy Full Animations"** créé par **Sm64**.

**Mention obligatoire de licence :**
> This work is based on "Mario Galaxy Full Animations" (https://sketchfab.com/3d-models/mario-galaxy-full-animations-314e71faeadb4750850f607d8d78d2ff) by Sm64 (https://sketchfab.com/publicidadrios) licensed under CC-BY-4.0 (http://creativecommons.org/licenses/by/4.0/)
