package com.wheel.core.entity.main;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WinHelper {
    private int winCounter;
    private int winValue;

    public void changeWinCounter() {
        winCounter--;
    }

    public boolean isWin() {
        return winCounter == 0;
    }
}
