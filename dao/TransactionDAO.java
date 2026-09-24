package dao;

import config.DatabaseConnection;
import model.Transaction;
import model.TypeTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public Transaction save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transaction (date, montant, type, lieu, id_compte) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(transaction.date()));
            stmt.setDouble(2, transaction.montant());
            stmt.setString(3, transaction.type().name());
            stmt.setString(4, transaction.lieu());
            stmt.setLong(5, transaction.idCompte());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Transaction(
                        rs.getLong(1),
                        transaction.date(),
                        transaction.montant(),
                        transaction.type(),
                        transaction.lieu(),
                        transaction.idCompte()
                    );
                }
            }
        }
        return transaction;
    }

    public List<Transaction> findByCompteId(Long idCompte) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE id_compte = ? ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCompte);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    public List<Transaction> findAll() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transaction ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getLong("id"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getDouble("montant"),
            TypeTransaction.valueOf(rs.getString("type")),
            rs.getString("lieu"),
            rs.getLong("id_compte")
        );
    }
}