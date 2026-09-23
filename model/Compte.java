package model;

public abstract sealed class Compte permits CompteCourant, CompteEpargne {
    private Long id;
    private String numero;
    private double solde;
    private Long idClient;

    public Compte(Long id, String numero, double solde, Long idClient) {
        this.id = id;
        this.numero = numero;
        this.solde = solde;
        this.idClient = idClient;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }
}