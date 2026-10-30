package com.labcontrol.app;

import com.labcontrol.UI.FrmPrincipal;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrmPrincipal principal = new FrmPrincipal();
            principal.setLocationRelativeTo(null);
            principal.setVisible(true);
        });
    }
}
