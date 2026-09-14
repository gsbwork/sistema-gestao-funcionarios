package com.saam.gestao;

import com.saam.gestao.config.DatabaseInitializer;
import com.saam.gestao.exception.DatabaseException;
import com.saam.gestao.view.LoginView;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicacao. Inicializa o banco de dados (SQL nativo)
 * e exibe a tela de Login.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Mantem o Look and Feel padrao caso o do sistema nao esteja disponivel.
        }

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseInitializer.initializeDatabase();
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(null,
                        "Nao foi possivel conectar ao banco de dados PostgreSQL.\n" + e.getMessage(),
                        "Erro de Conexao", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            new LoginView().setVisible(true);
        });
    }
}
