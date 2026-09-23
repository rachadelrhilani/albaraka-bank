package model;

public final class CompteEpargne extends Compte {
    private double tauxInteret;

    public CompteEpargne(Long id, String numero, double solde, Long idClient, double tauxInteret) {
        super(id, numero, solde, idClient);
        this.tauxInteret = tauxInteret;
    }

    public double getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(double tauxInteret) { this.tauxInteret = tauxInteret; }
}