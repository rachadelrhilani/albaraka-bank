package model;

import java.time.LocalDateTime;

public record Transaction(
    Long id,
    LocalDateTime date,
    double montant,
    TypeTransaction type,
    String lieu,
    Long idCompte
) {}