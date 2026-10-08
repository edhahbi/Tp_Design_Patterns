public class FraisPremium implements FraisStrategy{
    @Override
    public double calculerMontant(double montant) {
        assert montant >= 0;
        return 0;
    }
}
