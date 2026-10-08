import java.util.HashMap;
import java.util.Map;

public final class Banque {
    private static final Banque INSTANCE = new Banque();
    private final Map<String, CompteBancaire> comptes = new HashMap<>();

    private Banque() {
    }

    public static Banque getInstance() {
        return INSTANCE;
    }

    public void ajouterCompte(String numeroCompte, double soldeInitial, double decouvertMax) {
        ajouterCompte(numeroCompte, new CompteBancaire(soldeInitial, decouvertMax));
    }

    public void ajouterCompteObservable(String numeroCompte,
                                        double soldeInitial,
                                        double decouvertMax) {
        ajouterCompte(numeroCompte, new CompteOberservable(soldeInitial, decouvertMax));
    }

    private void ajouterCompte(String numeroCompte, CompteBancaire compte) {
        verifierNumero(numeroCompte);
        if (comptes.containsKey(numeroCompte)) {
            throw new IllegalArgumentException("Le compte existe déjà");
        }
        comptes.put(numeroCompte, compte);
    }

    public void deposer(String numeroCompte, double montant, FraisStrategy strategy) {
        trouverCompte(numeroCompte).deposer(montant, strategy);
    }

    public void retier(String numeroCompte, double montant, FraisStrategy strategy) {
        trouverCompte(numeroCompte).retirer(montant, strategy);
    }

    public double getSolde(String numeroCompte) {
        return trouverCompte(numeroCompte).getSolde();
    }

    private CompteBancaire trouverCompte(String numeroCompte) {
        verifierNumero(numeroCompte);
        CompteBancaire compte = comptes.get(numeroCompte);
        if (compte == null) {
            throw new IllegalArgumentException("Compte introuvable : " + numeroCompte);
        }
        return compte;
    }

    private void verifierNumero(String numeroCompte) {
        if (numeroCompte == null || numeroCompte.isBlank()) {
            throw new IllegalArgumentException("Le numéro de compte est obligatoire");
        }
    }
}
