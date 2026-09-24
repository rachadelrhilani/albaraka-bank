
package dao;

import config.DatabaseConnection;
import model.Compte;
import model.CompteCourant;
import model.CompteEpargne;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteDAO {

    public Compte save(Compte compte) throws SQLException {
        String sql = "INSERT INTO compte (numero, solde, id_client, type_compte, decouvert_autorise, taux_interet) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, compte.getNumero());
            stmt.setDouble(2, compte.getSolde());
            stmt.setLong(3, compte.getIdClient());

            if (compte instanceof CompteCourant cc) {
                stmt.setString(4, "COURANT");
                stmt.setDouble(5, cc.getDecouvertAutorise());
                stmt.setDouble(6, 0.0);
            } else if (compte instanceof CompteEpargne ce) {
                stmt.setString(4, "EPARGNE");
                stmt.setDouble(5, 0.0);
                stmt.setDouble(6, ce.getTauxInteret());
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    compte.setId(rs.getLong(1));
                }
            }
        }
        return compte;
    }

    public void updateSolde(Long idCompte, double nouveauSolde) throws SQLException {
        String sql = "UPDATE compte SET solde = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nouveauSolde);
            stmt.setLong(2, idCompte);
            stmt.executeUpdate();
        }
    }

    public Optional<Compte> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM compte WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompte(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Compte> findByClientId(Long idClient) throws SQLException {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM compte WHERE id_client = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idClient);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comptes.add(mapResultSetToCompte(rs));
                }
            }
        }
        return comptes;
    }

    public List<Compte> findAll() throws SQLException {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM compte";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                comptes.add(mapResultSetToCompte(rs));
            }
        }
        return comptes;
    }

    private Compte mapResultSetToCompte(ResultSet rs) throws SQLException {
        String type = rs.getString("type_compte");
        if ("COURANT".equalsIgnoreCase(type)) {
            return new CompteCourant(
                rs.getLong("id"),
                rs.getString("numero"),
                rs.getDouble("solde"),
                rs.getLong("id_client"),
                rs.getDouble("decouvert_autorise")
            );
        } else {
            return new CompteEpargne(
                rs.getLong("id"),
                rs.getString("numero"),
                rs.getDouble("solde"),
                rs.getLong("id_client"),
                rs.getDouble("taux_interet")
            );
        }
    }
}