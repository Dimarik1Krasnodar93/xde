package com.xde;

import lombok.Getter;

@Getter
public class Status {
    private String status;
    private boolean printForm;

    public Status(String status) {
        this.status = status;
        switch (status) {
            case "IPF":
                status = "I";
                printForm = true;
                break;
            case "VPF":
                status = "V";
                printForm = true;
                break;
            case "VPP":
                status = "VPA";
                printForm = true;
            case "VRP":
                status = "VRE";
                printForm = true;
            case "VAP":
                status = "VA";
                printForm = true;
            default: status = "NULL";
        }
    }

    public boolean getPrintForm() {
        return printForm;
    }
}
