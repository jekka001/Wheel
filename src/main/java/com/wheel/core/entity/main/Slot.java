package com.wheel.core.entity.main;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.wheel.core.utils.TableConstants.REEL_ID_FIELD;
import static com.wheel.core.utils.TableConstants.SLOT_TABLE;

@Data
@Entity
@Table(name = SLOT_TABLE)
@NoArgsConstructor
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int value;
    private int positionOnReel;
    @ManyToOne
    @JoinColumn(name = REEL_ID_FIELD)
    private Reel reel;

    public Slot(int value, int positionOnReel, Reel reel) {
        this.value = value;
        this.positionOnReel = positionOnReel;
        this.reel = reel;
    }
}
