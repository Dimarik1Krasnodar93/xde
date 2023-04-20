package com.xde.handlers;

/**
 * обработчики событий
 * */
public class EventHandler {
    /**
     * Получить приоритет события (для оптимизации)
     * */
    public static int getPriority(String status) {
        switch (status) {
            case "S": return 0;
            case "SO": return 0;
            case "WVO": return 3;
            case "SPF": return 3;
            case "I": return 1;
            case "IPF": return 2;
            case "V": return 3;
            case "PV": return 3;
            case "IER": return 2;
            case "VPF": return 5;
            case "VPP": return 5;
            case "VPA": return 4;
            case "VRP": return 5;
            case "VRE": return 3;
            case "VA": return 3;
            case "VAP": return 5;
            case "XV": return 3;
            case "NS": return 3;
            case "NI": return 5;
            case "N": return 6;
            case "NR": return 6;
            case "D": return 7;
            case "E": return 0;
            case "PX": return 9;
            case "RSO": return 0;
            case "RS": return -1;
            case "RV": return -1;
            case "RX": return -1;
            case "WNO": return 5;
            case "PNS": return 6;
            case "WIO": return 6;
            case "PN": return 6;
            case "WRO": return 6;
            case "PNR": return 6;
            case "PS": return 0;
            case "DRT": return 0;
            case "ADS": return 0;
            case "ADI": return 0;
            case "NULL": return -100;
            case "GPG": return 0;
            case "GPD": return 0;
            case "GE": return 0;
            case "GTG": return 0;
            case "GTE": return 0;
            case "CS": return 0;
            case "CI": return 0;
            case "CV": return 0;
            case "CX": return 0;
            case "R": return 0;
            case "W": return 0;
            case "99": return 0;
            default: return 100;
        }
    }
}
