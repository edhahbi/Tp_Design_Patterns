import java.util.ArrayList;
import java.util.List;

public class CompteOberservable extends CompteBancaire {
    private final List<Observerateur> observateurs = new ArrayList<>();

    public CompteOberservable(double soldeInitial, double decouvertMax) {
        super(soldeInitial, decouvertMax);
    }

    public void ajouterObservateur(Observerateur observateur) {
        if (observateur == null) {
            throw new NullPointerException("L'observateur est obligatoire");
        }
        observateurs.add(observateur);
    }

    public void retirerObservateur(Observerateur observateur) {
        observateurs.remove(observateur);
    }

    @Override
    void deposer(double montant, FraisStrategy strategy) {
        double ancienSolde = getSolde();
        super.deposer(montant, strategy);
        double montantCredite = getSolde() - ancienSolde;

        for (Observerateur observateur : observateurs) {
            observateur.notifier(this, montantCredite);
        }
    }

    @Override
    void retirer(double montant, FraisStrategy strategy) {
        double ancienSolde = getSolde();
        super.retirer(montant, strategy);
        double montantRetire = ancienSolde - getSolde();

        for (Observerateur observateur : observateurs) {
            observateur.notifier(this, montantRetire);
        }
    }
}
