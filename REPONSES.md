# Réponses aux Questions Théoriques - TP Décorateur Super Mario

---

### Question 1 : Problème de l'Héritage Classique (Explosion Combinatoire)
**Question :** *Quel problème poserait une approche basée uniquement sur l'héritage classique pour gérer toutes les combinaisons possibles de power-ups ?*

**Réponse :**  
Une approche par héritage classique entraînerait une explosion combinatoire du nombre de sous-classes. Pour $N$ power-ups cumulables et combinables dans n'importe quel ordre, il faudrait créer $2^N$ (ou $N!$) sous-classes distinctes (`MarioAvecChampignonEtFleur`, `MarioAvecCapeEtEtoile`, etc.). Le code deviendrait impossible à maintenir et rigide. Le pattern Décorateur résout ce problème en composant dynamiquement les responsabilités à l'exécution.

---

### Question 2 : Double Relation "Est-Un" et "Possède-Un"
**Question :** *Expliquez la dualité de la relation entre un décorateur et le composant décoré (héritage vs composition).*

**Réponse :**  
Le décorateur entretient une double relation avec le composant :
1. **Relation "Est-Un" (Héritage d'interface) :** `CharacterDecorator` implémente `Character`, garantissant la transparence des types. Le décorateur peut ainsi être utilisé partout où un `Character` est attendu.
2. **Relation "Possède-Un" (Composition) :** `CharacterDecorator` possède un champ `protected Character wrappedCharacter`. Il encapsule une référence vers l'objet englobé et lui délègue les traitements de base avant d'y ajouter son propre comportement.

---

### Question 3 : Immutabilité du Personnage de Base (Preuve de Non-Régression)
**Question :** *Pourquoi l'objet `Mario` d'origine n'est-il jamais modifié lorsqu'on lui applique des décorateurs ?*

**Réponse :**  
Les décorateurs ne mutent jamais l'état interne de l'objet englobé. Ils se contentent d'envelopper l'instance existante dans une nouvelle couche qui intercepte les appels de méthodes et calcule les statistiques modifiées à la volée en ajoutant leur bonus au résultat renvoyé par `wrappedCharacter`. L'instance `Mario` initiale reste intacte en mémoire avec ses valeurs d'origine (100/10/10).

---

### Question 4 : Respect du Principe Ouvert/Fermé (OCP)
**Question :** *Comment ce pattern illustre-t-il le principe Ouvert/Fermé (Open/Closed Principle) des principes SOLID ?*

**Réponse :**  
Le pattern Décorateur illustre parfaitement l'OCP : l'architecture est **ouverte à l'extension** (on peut ajouter un nouveau power-up comme `IceFlowerDecorator` en créant simplement une nouvelle classe qui étend `CharacterDecorator`) et **fermée à la modification** (l'ajout de ce power-up ne nécessite aucune modification des classes existantes `Mario`, `Character` ou des décorateurs déjà écrits).

---

### Question 5 : Rôle et Utilité de la Classe Abstraite `CharacterDecorator`
**Question :** *Pourquoi utiliser une classe abstraite `CharacterDecorator` au lieu de faire hériter les décorateurs concrets directement de l'interface `Character` ?*

**Réponse :**  
La classe abstraite `CharacterDecorator` factorise le code de délégation par défaut (pass-through) pour toutes les méthodes de l'interface `Character`. Sans elle, chaque décorateur concret devrait réimplémenter manuellement la délégation des méthodes qu'il ne modifie pas, provoquant une duplication de code inutile. Elle est abstraite car elle ne représente aucun power-up concret et ne doit pas être instanciée directement.

---

### Question 6 : Exemple Concret dans la API Standard Java (`java.io`)
**Question :** *Donnez un exemple célèbre d'utilisation du pattern Décorateur dans la bibliothèque standard du JDK Java.*

**Réponse :**  
L'exemple le plus célèbre se trouve dans le package `java.io` avec la gestion des flux d'entrée/sortie :
```java
BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("data.txt")));
```
Ici, `FileInputStream` est le composant concret (lecture de raw bytes), `InputStreamReader` le décore pour convertir les octets en caractères, et `BufferedReader` le décore à son tour pour ajouter un tampon mémoire et la méthode `readLine()`.
