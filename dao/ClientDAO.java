package dao;

import config.DatabaseConnection;
import model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO {

    public Client save(Client client) throws SQLException {
        String sql = "INSERT INTO client (nom, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.nom());
            stmt.setString(2, client.email());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Client(rs.getLong(1), client.nom(), client.email());
                }
            }
        }
        return client;
    }

    public void update(Client client) throws SQLException {
        String sql = "UPDATE client SET nom = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.nom());
            stmt.setString(2, client.email());
            stmt.setLong(3, client.id());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM client WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Optional<Client> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        }
        return clients;
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
            rs.getLong("id"),
            rs.getString("nom"),
            rs.getString("email")
        );
    }
}