package my.project.digital_library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:biblioteca.db";

    // Inicializa o banco de dados e cria a tabela se não existir
    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS livros ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL,"
                + " caminho TEXT NOT NULL,"
                + " progresso INTEGER DEFAULT 0"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar banco de dados: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Método para salvar um novo livro
    public static void salvarLivro(String nome, String caminho) {
        String sql = "INSERT INTO livros(nome, caminho, progresso) VALUES(?, ?, 0)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, caminho);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar livro: " + e.getMessage());
        }
    }

    // Método para carregar todos os livros com ID
    public static List<Livro> listarLivrosCompletos() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, nome FROM livros";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livros.add(new Livro(rs.getInt("id"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros completos: " + e.getMessage());
        }
        return livros;
    }

    // Método para buscar livros filtrados por nome retornando Objetos Livro
    public static List<Livro> buscarLivrosObjetosPorNome(String termoBusca) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, nome FROM livros WHERE nome LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + termoBusca + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    livros.add(new Livro(rs.getInt("id"), rs.getString("nome")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar objetos livros: " + e.getMessage());
        }
        return livros;
    }

    // Método para buscar TODOS os livros cadastrados
    public static List<String> listarLivros() {
        List<String> livros = new ArrayList<>();
        String sql = "SELECT nome FROM livros";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livros.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        }
        return livros;
    }

    // Método para buscar livros filtrados pelo nome
    public static List<String> buscarLivrosPorNome(String termoBusca) {
        List<String> livros = new ArrayList<>();
        String sql = "SELECT nome FROM livros WHERE nome LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // O "%" antes e depois faz com que encontre qualquer parte do nome
            pstmt.setString(1, "%" + termoBusca + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    livros.add(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar livros: " + e.getMessage());
        }
        return livros;
    }

    // Método para atualizar o progresso (pode ser usado futuramente)
    public static void atualizarProgresso(String nome, int progresso) {
        String sql = "UPDATE livros SET progresso = ? WHERE nome = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, progresso);
            pstmt.setString(2, nome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar progresso: " + e.getMessage());
        }
    }

    // Método para buscar o caminho do PDF pelo nome do livro
    public static String buscarCaminhoLivro(String nome) {
        String sql = "SELECT caminho FROM livros WHERE nome = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("caminho");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar caminho do livro: " + e.getMessage());
        }
        return null;
    }
}