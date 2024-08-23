package com.wheel.core.entity.main;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static com.wheel.core.utils.TableConstants.*;


@Data
@Entity
@Table(name = SPIN_TABLE)
public class SpinResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID_FIELD)
    private User user;
    private Timestamp spinDate;
    @OneToMany(mappedBy = SPIN_RESULT_ENTITY_FIELD, fetch = FetchType.LAZY)
    private List<Reel> reels;
    private boolean payed;
}
