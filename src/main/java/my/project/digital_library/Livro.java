package my.project.digital_library;

public class Livro {
    private int id;
    private String nome;

    public Livro(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
}