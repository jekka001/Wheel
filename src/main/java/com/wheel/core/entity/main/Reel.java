package com.wheel.core.entity.main;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

import static com.wheel.core.utils.TableConstants.REEL_TABLE;
import static com.wheel.core.utils.TableConstants.SPIN_ID_FIELD;

@Data
@Entity
@Table(name = REEL_TABLE)
public class Reel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int positionOnWheel;
    @ManyToOne
    @JoinColumn(name = SPIN_ID_FIELD)
    private SpinResult spinResult;
}
