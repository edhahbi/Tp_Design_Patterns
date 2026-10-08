---
Nom & Prenom: Edhahbi Noureddine
Classe: 2eme ING INFO
---
# Contrats de la classe `CompteBancaire`

*8 octobre 2026*

## 1. Objet

Ce document analyse la classe `CompteBancaire.java` selon la programmation par contrats. Un contrat décrit les conditions d'utilisation d'une méthode (préconditions), les garanties qu'elle fournit (postconditions) et les propriétés qui doivent rester vraies (invariants).

## 2. Contrats identifiés

### 2.1 Invariant de la classe

L'invariant principal est :

$$\texttt{solde} \geq -\texttt{decouvertMax}.$$

Autrement dit, le solde ne peut jamais dépasser le découvert autorisé. Il doit être vrai après la construction de l'objet et après chaque opération publique. La méthode privée `verifierInvariant()` le contrôle avec une assertion.

### 2.2 Constructeur

| Élément       | Contrat                                                                                                             |
| ------------- | ------------------------------------------------------------------------------------------------------------------- |
| Précondition  | `decouvertMax >= 0` et `soldeInitial >= -decouvertMax`. Sinon, le constructeur lève une `IllegalArgumentException`. |
| Postcondition | L'objet est initialisé avec `solde = soldeInitial` et `decouvertMax = decouvertMax`.                                |
| Invariant     | `solde >= -decouvertMax` est vrai dès que le constructeur termine normalement.                                      |

### 2.3 `deposer(double montant)`

| Élément | Contrat |
|---|---|
| Précondition | `montant > 0`. Dans le cas contraire, une `IllegalArgumentException` est levée. |
| Postcondition | Si l'ancien solde vaut $s$, le nouveau solde vaut `solde = s + montant`. Cette propriété est vérifiée par une assertion. |
| Invariant | `solde >= -decouvertMax` reste vrai après le dépôt. Comme un dépôt augmente le solde, il ne peut pas violer l'invariant si celui-ci était vrai avant l'appel. |

### 2.4 `retirer(double montant)`

| Élément | Contrat |
|---|---|
| Préconditions | `montant > 0` et `solde - montant >= -decouvertMax`. Le premier cas lève une `IllegalArgumentException`; le second lève une `IllegalStateException`. |
| Postcondition | Si l'ancien solde vaut $s$, le nouveau solde vaut `solde = s - montant`. Cette propriété est vérifiée par une assertion. |
| Invariant | `solde >= -decouvertMax` reste vrai après le retrait, puisque la précondition interdit de dépasser la limite autorisée. |

### 2.5 `getSolde()`

| Élément | Contrat |
|---|---|
| Précondition | Aucune : la lecture du solde est toujours autorisée sur un objet correctement construit. |
| Postcondition | La valeur retournée est le solde courant de l'objet : `result == solde`. |
| Invariant | L'appel ne modifie pas l'objet; l'invariant reste donc vrai. |

## 3. `assert` ou `throw new Exception` ?

**`assert`.** Une assertion exprime une propriété qui doit être vraie si le programme est correct, par exemple une postcondition ou un invariant interne. En Java, les assertions sont désactivées par défaut et ne sont activées qu'avec l'option `-ea`. Elles ne doivent donc pas servir à valider une donnée fournie par un appelant : le comportement du programme changerait selon les options de lancement.

**Exception explicite.** Une exception, par exemple `throw new IllegalArgumentException(...)`, fait partie du comportement normal et observable de l'API. Elle est toujours exécutée et convient donc aux préconditions contrôlables par l'appelant. Le type de l'exception décrit également la nature de l'erreur : argument invalide ou état incompatible.

**Application à la classe.** Dans cette classe, le choix est cohérent : les préconditions sont rejetées par des exceptions, tandis que les postconditions et l'invariant sont contrôlés par `assert`. Pour une application qui doit garantir ces propriétés même en production, on peut remplacer les assertions de sécurité par des exceptions explicites ou par une stratégie de validation toujours active.

## 4. Précondition renforcée et principe de substitution de Liskov

Le principe de substitution de Liskov (LSP) impose qu'une instance d'une sous-classe puisse remplacer une instance de la classe mère sans rendre invalides les usages corrects du type parent. Une sous-classe ne doit donc pas renforcer une précondition : elle doit accepter au moins tous les appels acceptés par la classe mère. Elle peut en revanche affaiblir une précondition (accepter davantage de cas) et renforcer une postcondition, sous réserve de préserver le contrat parent.

Par exemple, si une sous-classe de `CompteBancaire` redéfinit `deposer` et exige `montant >= 100`, elle refuse un appel `deposer(10)` pourtant valide pour le type parent. Un client écrit contre `CompteBancaire` peut alors échouer après substitution : la sous-classe viole le LSP. Le renforcement de cette précondition est donc interdit; une règle supplémentaire devrait être modélisée autrement, par exemple dans une méthode distincte ou une abstraction différente.

## 5. Tests JUnit qui violent les contrats

Les tests suivants utilisent JUnit 5 et vérifient que chaque précondition invalide est refusée. Ils ne cherchent pas à rendre le programme incorrect : ils provoquent volontairement une violation du contrat et vérifient la réaction documentée.


Les assertions de postcondition et d'invariant ne sont pas directement violées par un appel public valide dans l'implémentation fournie : elles servent à détecter une erreur interne du code. Pour les exécuter, il faut lancer les tests avec les assertions Java activées (`-ea`). Un test qui prétendrait provoquer directement l'invariant en modifiant `solde` ne serait pas un test valide de l'API, car ce champ est privé.

## 6. Gestion centralisée par la banque

`CompteBancaire` ne contient plus de singleton : chaque compte est créé avec son propre solde et son propre découvert autorisé. La classe `Banque` est le singleton, obtenu avec `Banque.getInstance()`. Elle associe un numéro unique à chaque compte et centralise les opérations : `ajouterCompte`, `deposer(numero, montant, strategie)` et `retier(numero, montant, strategie)`. Le numéro permet de choisir sans ambiguïté le compte concerné. Les opérations du compte sont package-private afin que les clients passent par la banque.

## 7. Conclusion

Les exceptions protègent les préconditions qui dépendent de l'appelant, tandis que les assertions documentent les garanties internes. L'invariant borne le découvert tout au long de la vie du compte. Enfin, une sous-classe respectant LSP ne peut pas exiger davantage de ses clients que la classe parent.

# Diagramme de classes UML

Le diagramme ci-dessous représente les classes principales du projet, leurs
héritages, leurs implémentations d'interfaces et leurs associations.

```mermaid
classDiagram
    direction TB

    class Banque {
        <<Singleton>>
        -Banque INSTANCE
        -Map~String, CompteBancaire~ comptes
        -Banque()
        +Banque getInstance()
        +void ajouterCompte(String numeroCompte, double soldeInitial, double decouvertMax)
        +void ajouterCompteObservable(String numeroCompte, double soldeInitial, double decouvertMax)
        +void deposer(String numeroCompte, double montant, FraisStrategy strategy)
        +void retier(String numeroCompte, double montant, FraisStrategy strategy)
        +double getSolde(String numeroCompte)
    }

    class CompteBancaire {
        -double solde
        -double decouvertMax
        +CompteBancaire(double soldeInitial, double decouvertMax)
        ~void deposer(double montant, FraisStrategy strategy)
        ~void retirer(double montant, FraisStrategy strategy)
        +double getSolde()
        -void verifierInvariant()
        -double calculerFrais(double montant, FraisStrategy strategy)
    }

    class CompteCourant {
        +CompteCourant(double soldeInitial, double decouvertMax)
    }

    class CompteEpargne {
        +CompteEpargne(double soldeInitial)
    }

    class CompteOberservable {
        -List~Observerateur~ observateurs
        +CompteOberservable(double soldeInitial, double decouvertMax)
        +void ajouterObservateur(Observerateur observateur)
        +void retirerObservateur(Observerateur observateur)
        ~void deposer(double montant, FraisStrategy strategy)
        ~void retirer(double montant, FraisStrategy strategy)
    }

    class CompteFactory {
        <<Factory>>
        -CompteFactory()
        +static CompteBancaire creer(TypeCompte type, double soldeInitial, double decouvertMax)
    }

    class TypeCompte {
        <<enumeration>>
        COURANT
        EPARGNE
    }

    class FraisStrategy {
        <<interface>>
        +double calculerMontant(double montant)
    }

    class FraisStandard {
        +double calculerMontant(double montant)
    }

    class FraisPremium {
        +double calculerMontant(double montant)
    }

    class Observerateur {
        <<interface>>
        +void notifier(CompteBancaire compte, double montant)
    }

    class ServiceNotification {
        +void notifier(CompteBancaire compte, double montant)
    }

    Banque "1" *-- "0..*" CompteBancaire : gère
    Banque ..> CompteOberservable : crée
    Banque ..> FraisStrategy : transmet

    CompteCourant --|> CompteBancaire
    CompteEpargne --|> CompteBancaire
    CompteOberservable --|> CompteBancaire

    CompteOberservable "1" o-- "0..*" Observerateur : notifie
    ServiceNotification ..|> Observerateur

    FraisStandard ..|> FraisStrategy
    FraisPremium ..|> FraisStrategy
    CompteBancaire ..> FraisStrategy : utilise

    CompteFactory ..> TypeCompte : reçoit
    CompteFactory ..> CompteBancaire : retourne
    CompteFactory ..> CompteCourant : crée
    CompteFactory ..> CompteEpargne : crée
```

## Légende

- `--|>` : héritage.
- `..|>` : implémentation d'une interface.
- `*--` : composition : la banque gère son ensemble de comptes.
- `o--` : agrégation : un compte observable référence plusieurs observateurs.
- `..>` : dépendance, notamment lors d'une création ou d'un appel de méthode.
- `~` : méthode accessible dans le paquetage.

Les tests JUnit de [CompteBancaireunitTests.java](./CompteBancaireunitTests.java)
vérifient notamment le singleton `Banque`, les stratégies de frais et les
types produits par `CompteFactory`; ils ne sont pas inclus comme classes métier
dans le diagramme.