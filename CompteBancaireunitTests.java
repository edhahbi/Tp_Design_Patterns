import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CompteBancaireTest {
    private static final FraisStrategy SANS_FRAIS = new FraisPremium();

    @Test
    void constructeurRefuseUnDecouvertNegatif() {
        assertThrows(IllegalArgumentException.class,
            () -> new CompteBancaire(0.0, -1.0));
    }

    @Test
    void constructeurRefuseUnSoldeInitialTropBas() {
        assertThrows(IllegalArgumentException.class,
            () -> new CompteBancaire(-101.0, 100.0));
    }

    @Test
    void depotRefuseUnMontantNul() {
        CompteBancaire compte = new CompteBancaire(0.0, 100.0);
        assertThrows(IllegalArgumentException.class,
            () -> compte.deposer(0.0, SANS_FRAIS));
    }

    @Test
    void depotRefuseUnMontantNegatif() {
        CompteBancaire compte = new CompteBancaire(0.0, 100.0);
        assertThrows(IllegalArgumentException.class, 
            () -> compte.deposer(-10.0, SANS_FRAIS));
    }

    @Test
    void retraitRefuseUnMontantNul() {
        CompteBancaire compte = new CompteBancaire(0.0, 100.0);
        assertThrows(IllegalArgumentException.class,
            () -> compte.retirer(0.0, SANS_FRAIS));
    }

    @Test
    void retraitRefuseUnMontantNegatif() {
        CompteBancaire compte = new CompteBancaire(0.0, 100.0);
        assertThrows(IllegalArgumentException.class,
            () -> compte.retirer(-10.0, SANS_FRAIS));
    }

    @Test
    void retraitRefuseLeDepassementDuDecouvert() {
        CompteBancaire compte = new CompteBancaire(0.0, 100.0);
        assertThrows(IllegalStateException.class,
            () -> compte.retirer(100.01, SANS_FRAIS));
    }

    @Test
    void banqueRetourneUneInstanceSingleton() {
        Banque banque = Banque.getInstance();

        assertNotNull(banque);
        assertSame(banque, Banque.getInstance());
    }

    @Test
    void strategiesCalculentDesFraisValides() {
        FraisStrategy fraisPremium = new FraisPremium();
        FraisStrategy fraisStandard = new FraisStandard();

        assertEquals(0.0, fraisPremium.calculerMontant(100.0));
        assertEquals(1.0, fraisStandard.calculerMontant(100.0), 0.000001);
        assertTrue(fraisPremium.calculerMontant(100.0) >= 0.0);
        assertTrue(fraisStandard.calculerMontant(100.0) >= 0.0);
    }

    @Test
    void factoryRetourneUnCompteCourantPourLeTypeCourant() {
        CompteBancaire compte =
            CompteFactory.creer(TypeCompte.COURANT, 100.0, 50.0);

        assertInstanceOf(CompteCourant.class, compte);
    }

    @Test
    void factoryRetourneUnCompteEpargnePourLeTypeEpargne() {
        CompteBancaire compte =
            CompteFactory.creer(TypeCompte.EPARGNE, 100.0, 0.0);

        assertInstanceOf(CompteEpargne.class, compte);
    }

    @Test
    void factoryRefuseUnTypeInvalide() {
        assertThrows(IllegalArgumentException.class,
            () -> CompteFactory.creer(null, 100.0, 0.0));
    }
}