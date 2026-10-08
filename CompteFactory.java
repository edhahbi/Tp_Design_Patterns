public final class CompteFactory {
    private CompteFactory() {
    }

    public static CompteBancaire creer(TypeCompte type,
                                       double soldeInitial,
                                       double decouvertMax) {
        if (type == null) {
            throw new IllegalArgumentException("Le type de compte est obligatoire");
        }

        var compte = switch (type) {
            case COURANT -> new CompteCourant(soldeInitial, decouvertMax);
            case EPARGNE -> {
                if (decouvertMax != 0.0) {
                    throw new IllegalArgumentException(
                            "Un compte épargne ne peut pas avoir de découvert");
                }
                yield new CompteEpargne(soldeInitial);
            }
        };

        return compte;
    }
}
