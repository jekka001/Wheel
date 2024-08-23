package com.wheel.core.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface PayService {

    BigDecimal getUserBalance(HttpServletRequest request);

    boolean getPayFromUser(HttpServletRequest request);

    boolean payWinForLine(HttpServletRequest request, String spinResultId);
}
