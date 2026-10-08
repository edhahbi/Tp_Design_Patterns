public class FraisStandard implements FraisStrategy{
    @Override
    public double calculerMontant(double montant) {
        assert montant >= 0;
        double r = montant * .01;
        assert r>= 0;
        return r;
    }
    
}
