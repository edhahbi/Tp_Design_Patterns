public class ServiceNotification implements Observerateur {
    @Override
    public void notifier(CompteBancaire compte, double montant) {
        System.out.println("Dépôt de " + montant
                + " effectué. Nouveau solde : " + compte.getSolde());
    }
}