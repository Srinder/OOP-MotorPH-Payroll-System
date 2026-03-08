package view;

public final class WindowNavigation {
    private static final String SKIP_RETURN_ON_CLOSE = "skipReturnToMainMenuOnClose";

    private WindowNavigation() {
    }

    public static void installReturnToMainMenuOnClose(javax.swing.JFrame frame) {
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                Object skip = frame.getRootPane().getClientProperty(SKIP_RETURN_ON_CLOSE);
                if (Boolean.TRUE.equals(skip)) {
                    return;
                }
                returnToMainMenu();
            }
        });
    }

    public static void suppressReturnToMainMenuOnClose(javax.swing.JFrame frame) {
        frame.getRootPane().putClientProperty(SKIP_RETURN_ON_CLOSE, Boolean.TRUE);
    }

    public static void returnToMainMenu() {
        java.awt.EventQueue.invokeLater(() -> {
            for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
                if (frame instanceof MainMenu && frame.isDisplayable()) {
                    frame.setVisible(true);
                    frame.toFront();
                    frame.requestFocus();
                    return;
                }
            }

            model.Employee currentUser = model.User.getLoggedInUser();
            if (currentUser != null) {
                MainMenu mainMenu = new MainMenu(currentUser);
                mainMenu.setLocationRelativeTo(null);
                mainMenu.setVisible(true);
            }
        });
    }
}
