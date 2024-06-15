import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Abstração e Interfaces
interface Emprestavel {
    void emprestar();
    void devolver();
}

// Classe Livro
class Livro implements Emprestavel {
    private String titulo;
    private String autor;
    private String isbn;
    private boolean emprestado;

    public Livro(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.emprestado = false;
    }

    // Getters e Setters
    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isEmprestado() {
        return emprestado;
    }

    @Override
    public void emprestar() {
        if (!emprestado) {
            emprestado = true;
            System.out.println(titulo + " foi emprestado.");
        } else {
            System.out.println(titulo + " já está emprestado.");
        }
    }

    @Override
    public void devolver() {
        if (emprestado) {
            emprestado = false;
            System.out.println(titulo + " foi devolvido.");
        } else {
            System.out.println(titulo + " não está emprestado.");
        }
    }
}

// Classe abstrata Pessoa
abstract class Pessoa {
    private String nome;
    private String id;

    public Pessoa(String nome, String id) {
        this.nome = nome;
        this.id = id;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    // Método abstrato
    public abstract void mostrarInformacoes();
}

// Classe Usuário que herda de Pessoa
class Usuario extends Pessoa {
    private List<Livro> livrosEmprestados;

    public Usuario(String nome, String id) {
        super(nome, id);
        this.livrosEmprestados = new ArrayList<>();
    }

    public void emprestarLivro(Livro livro) {
        if (!livrosEmprestados.contains(livro)) {
            livro.emprestar();
            livrosEmprestados.add(livro);
        } else {
            System.out.println("Livro já emprestado ao usuário.");
        }
    }

    public void devolverLivro(Livro livro) {
        if (livrosEmprestados.contains(livro)) {
            livro.devolver();
            livrosEmprestados.remove(livro);
        } else {
            System.out.println("Livro não foi emprestado por este usuário.");
        }
    }

    public List<Livro> getLivrosEmprestados() {
        return livrosEmprestados;
    }

    @Override
    public void mostrarInformacoes() {
        System.out.println("Usuário: " + getNome() + ", ID: " + getId());
        if (livrosEmprestados.isEmpty()) {
            System.out.println("Nenhum livro emprestado.");
        } else {
            System.out.println("Livros emprestados:");
            for (Livro livro : livrosEmprestados) {
                System.out.println("- " + livro.getTitulo());
            }
        }
    }
}

// Classe Bibliotecario que herda de Pessoa
class Bibliotecario extends Pessoa {
    public Bibliotecario(String nome, String id) {
        super(nome, id);
    }

    @Override
    public void mostrarInformacoes() {
        System.out.println("Bibliotecário: " + getNome() + ", ID: " + getId());
    }

    public void catalogarLivro(Livro livro) {
        System.out.println("Catalogando livro: " + livro.getTitulo());
    }
}

class IdGenerator {
    private static int proximoIdUsuario = 1; // Inicia o contador de IDs de usuário
    private static int proximoIdLivro = 1;   // Inicia o contador de IDs de livro

    // Métodos para obter o próximo ID para cada tipo
    public static int proximoIdUsuario() {
        return proximoIdUsuario++;
    }

    public static int proximoIdLivro() {
        return proximoIdLivro++;
    }

}

// Classe principal
public class Biblioteca {
    private List<Livro> livros;
    private List<Usuario> usuarios;
    private List<Bibliotecario> bibliotecarios;
    private static final String FILE_NAME = "biblioteca.txt";
    IdGenerator idGenerator = new IdGenerator();
    private List<String> acoesRegistradas;

    public Biblioteca() {
        livros = new ArrayList<>();
        usuarios = new ArrayList<>();
        bibliotecarios = new ArrayList<>();
        acoesRegistradas = new ArrayList<>();
    }

    public void adicionarLivro(Livro livro) {
        livros.add(livro);
    }

    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }


    public void listarUsuarios() {
        System.out.println("Lista de Usuários:");
        for (Usuario usuario : usuarios) {
            usuario.mostrarInformacoes();
        }
    }

    public void listarLivros() {
        System.out.println("Lista de Livros:");
        for (Livro livro : livros) {
            System.out.println("Título: " + livro.getTitulo() + ", Autor: " + livro.getAutor() + ", ISBN: " + livro.getIsbn() + ", Emprestado: " + livro.isEmprestado());
        }
    }

    public void listarEmprestimos() {
        System.out.println("Lista de Empréstimos:");
        for (Usuario usuario : usuarios) {
            if (!usuario.getLivrosEmprestados().isEmpty()) {
                System.out.println("Usuário: " + usuario.getNome());
                for (Livro livro : usuario.getLivrosEmprestados()) {
                    System.out.println("   Livro: " + livro.getTitulo() + " (ISBN: " + livro.getIsbn() + ")");
                }
            }
        }
    }

    public void listarDevolucoes() {
        System.out.println("Lista de Devoluções:");
        // Simplificação: Neste exemplo, listamos apenas os livros não emprestados.
        for (Livro livro : livros) {
            if (!livro.isEmprestado()) {
                System.out.println("Livro: " + livro.getTitulo() + " (ISBN: " + livro.getIsbn() + ")");
            }
        }
    }

    public void listarAcoesRegistradas() {
        System.out.println("Ações Registradas:");
        for (String acao : acoesRegistradas) {
            System.out.println(acao);
        }
    }

    private void lerArquivo() {
        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {
            stream.forEach(line -> {
                acoesRegistradas.add(line);  // Adiciona cada linha ao histórico de ações registradas
                String[] parts = line.split(":");
                if (parts.length < 2) {
                    return;
                }
                String tipo = parts[0].trim();
                String detalhes = parts[1].trim();

                switch (tipo.toLowerCase()) {
                    case "livro adicionado":
                        // Formato esperado: Livro Adicionado: Título do Livro por Autor (ISBN: 1234567890)
                        String[] detalhesLivro = detalhes.split(" por ");
                        if (detalhesLivro.length < 2) {
                            return;
                        }
                        String tituloLivro = detalhesLivro[0].trim();
                        String autorEIsbn = detalhesLivro[1].trim();
                        int inicioISBN = autorEIsbn.indexOf("(ISBN: ");
                        if (inicioISBN == -1) {
                            return;
                        }
                        String autor = autorEIsbn.substring(0, inicioISBN).trim();
                        String isbn = autorEIsbn.substring(inicioISBN + 7, autorEIsbn.length() - 1).trim();
                        Livro livro = new Livro(tituloLivro, autor, isbn);
                        adicionarLivro(livro);
                        break;

                    case "usuário registrado":
                        // Implemente a lógica para "Usuário Registrado" de maneira similar
                        break;

                    case "livro emprestado":
                        // Implemente a lógica para "Livro Emprestado" de maneira similar
                        break;

                    case "livro devolvido":
                        // Implemente a lógica para "Livro Devolvido" de maneira similar
                        break;

                    default:
                        System.err.println("Tipo de entrada desconhecido: " + tipo);
                }
            });
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.lerArquivo();
        Scanner scanner = new Scanner(System.in);
        int opcao;


        System.out.println("Feito por Bruno da silveira rodrigues - 3 periodo, para a materia de POO do Prof Jefersom");

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            while (true) {
                System.out.println("Escolha uma Opção:");
                System.out.println("1 - Cadastrar livro");
                System.out.println("2 - Cadastrar usuário");
                System.out.println("3 - Emprestar livro");
                System.out.println("4 - Devolver livro");
                System.out.println("5 - Listar Usuários");
                System.out.println("6 - Listar Livros");
                System.out.println("7 - Listar Empréstimos");
                System.out.println("8 - Listar Devoluções");
                System.out.println("9 - Listar Ações Registradas");
                System.out.println("10 - Sair");

                opcao = scanner.nextInt();
                scanner.nextLine();  // Consumir nova linha

                switch (opcao) {
                    case 1:
                        System.out.println("Digite o título do livro:");
                        String titulo = scanner.nextLine();
                        System.out.println("Digite o autor do livro:");
                        String autor = scanner.nextLine();
                        System.out.println("Digite o ISBN do livro:");
                        String isbn = scanner.nextLine();

                        Livro livro = new Livro(titulo, autor, isbn);
                        biblioteca.adicionarLivro(livro);
                        writer.println("Livro Adicionado: " + titulo + " por " + autor + " (ISBN: " + isbn + ")");
                        writer.flush();
                        System.out.println("Livro cadastrado com sucesso.");
                        break;

                    case 2:
                        System.out.println("Digite o nome do usuário:");
                        String nomeUsuario = scanner.nextLine();
                        System.out.println("Digite o ID do usuário:");
                        String idUsuario = scanner.nextLine();

                        Usuario usuario = new Usuario(nomeUsuario, idUsuario);
                        biblioteca.registrarUsuario(usuario);
                        writer.println("Usuário Registrado: " + nomeUsuario + " (ID: " + idUsuario + ")");
                        writer.flush();
                        System.out.println("Usuário cadastrado com sucesso.");
                        break;

                    case 3:
                        System.out.println("Digite o ID do usuário:");
                        String idUsuarioEmprestimo = scanner.nextLine();
                        Usuario usuarioEmprestimo = biblioteca.usuarios.stream()
                                .filter(u -> u.getId().equals(idUsuarioEmprestimo))
                                .findFirst()
                                .orElse(null);

                        if (usuarioEmprestimo != null) {
                            System.out.println("Digite o título do livro:");
                            String tituloLivroEmprestimo = scanner.nextLine();
                            Livro livroEmprestimo = biblioteca.livros.stream()
                                    .filter(l -> l.getTitulo().equals(tituloLivroEmprestimo))
                                    .findFirst()
                                    .orElse(null);

                            if (livroEmprestimo != null) {
                                usuarioEmprestimo.emprestarLivro(livroEmprestimo);
                                writer.println("Livro Emprestado: " + tituloLivroEmprestimo + " para " + usuarioEmprestimo.getNome());
                                writer.flush();
                            } else {
                                System.out.println("Livro não encontrado.");
                            }
                        } else {
                            System.out.println("Usuário não encontrado.");
                        }
                        break;

                    case 4:
                        System.out.println("Digite o ID do usuário:");
                        String idUsuarioDevolucao = scanner.nextLine();
                        Usuario usuarioDevolucao = biblioteca.usuarios.stream()
                                .filter(u -> u.getId().equals(idUsuarioDevolucao))
                                .findFirst()
                                .orElse(null);

                        if (usuarioDevolucao != null) {
                            System.out.println("Digite o título do livro:");
                            String tituloLivroDevolucao = scanner.nextLine();
                            Livro livroDevolucao = biblioteca.livros.stream()
                                    .filter(l -> l.getTitulo().equals(tituloLivroDevolucao))
                                    .findFirst()
                                    .orElse(null);

                            if (livroDevolucao != null) {
                                usuarioDevolucao.devolverLivro(livroDevolucao);
                                writer.println("Livro Devolvido: " + tituloLivroDevolucao + " por " + usuarioDevolucao.getNome());
                                writer.flush();
                            } else {
                                System.out.println("Livro não encontrado.");
                            }
                        } else {
                            System.out.println("Usuário não encontrado.");
                        }
                        break;

                    case 5:
                        biblioteca.listarUsuarios();
                        break;

                    case 6:
                        biblioteca.listarLivros();
                        break;

                    case 7:
                        biblioteca.listarEmprestimos();
                        break;

                    case 8:
                        biblioteca.listarDevolucoes();
                        break;

                    case 9:
                        biblioteca.listarAcoesRegistradas();
                        break;

                    case 10:
                        System.out.println("Saindo do sistema...");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}
