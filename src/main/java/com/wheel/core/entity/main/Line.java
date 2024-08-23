package com.wheel.core.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wheel.core.utils.Constants.DELIMITER_FOR_LINE_WIN_POSITION;
import static com.wheel.core.utils.TableConstants.LINE_TABLE;

@Data
@Entity
@Table(name = LINE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int number;
    private String winPositions;

    public List<Integer> getWinPositions() {
        return Arrays.stream(winPositions.split(DELIMITER_FOR_LINE_WIN_POSITION))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
