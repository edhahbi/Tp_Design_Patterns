
public class CompteBancaire {
    private double solde;
    private final double decouvertMax;

    private void verifierInvariant() {
        assert solde >= -decouvertMax : "Invariant violé : solde < -découvert max";
    }

    public CompteBancaire(double soldeInitial, double decouvertMax) {
        if (decouvertMax < 0) {
            throw new IllegalArgumentException("Pré : decouvertMax >= 0");
        }
        if (soldeInitial < -decouvertMax) {
            throw new IllegalArgumentException("Pré : solde initial invalide");
        }
        this.solde = soldeInitial;
        this.decouvertMax = decouvertMax;
        verifierInvariant();
    }

    void deposer(double montant, FraisStrategy strategy) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Pré : montant > 0");
        }
        double frais = calculerFrais(montant, strategy);
        if (frais > montant) {
            throw new IllegalArgumentException("Frais de dépôt invalides");
        }
        solde += montant - frais;
        verifierInvariant();
    }

    void retirer(double montant, FraisStrategy strategy) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Pré : montant > 0");
        }
        double montantTotal = montant + calculerFrais(montant, strategy);
        if (solde - montantTotal < -decouvertMax) {
            throw new IllegalStateException("Pré : découvert dépassé");
        }
        solde -= montantTotal;
        verifierInvariant();
    }

    private double calculerFrais(double montant, FraisStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException("La stratégie de frais est obligatoire");
        }
        double frais = strategy.calculerMontant(montant);
        if (frais < 0) {
            throw new IllegalArgumentException("Les frais ne peuvent pas être négatifs");
        }
        return frais;
    }

    public double getSolde() {
        return solde;
    }


}
