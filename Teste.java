import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        Properties config = new Properties();
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            config.load(new FileInputStream("src/main/resources/application.properties"));

            String outputDir = config.getProperty("output.directory");
            String dbUrl = config.getProperty("db.url");
            String dbUser = config.getProperty("db.user");
            String dbPassword = config.getProperty("db.password");

            System.out.print("Digite a data inicial (yyyy-MM-dd): ");
            LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), formatter);

            System.out.print("Digite a data final (yyyy-MM-dd): ");
            LocalDate dataFim = LocalDate.parse(scanner.nextLine(), formatter);

            if (dataFim.isBefore(dataInicio)) {
                System.err.println("❌ A data final não pode ser anterior à data inicial.");
                return;
            }

            if (dataFim.isAfter(dataInicio.plusYears(1))) {
                System.err.println("❌ O intervalo não pode ultrapassar 1 ano.");
                return;
            }

            System.out.println("Conectando ao banco de dados...");
            List<ExtratoLinha> dados = ExtratoRepository.buscarDoBanco(dbUrl, dbUser, dbPassword, dataInicio, dataFim);

            System.out.println("Linhas recuperadas: " + dados.size());
            System.out.println("Salvando arquivos em: " + outputDir);

            long inicio = System.currentTimeMillis();
            OfxExporter.salvar(dados, outputDir + "/extrato.ofx");

            long fim = System.currentTimeMillis();
            System.out.printf("Conversão finalizada em %.2f segundos.%n", (fim - inicio) / 1000.0);

        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }
}

public static List<ExtratoLinha> buscarDoBanco(String url, String user, String password, LocalDate inicio, LocalDate fim) {
    List<ExtratoLinha> linhas = new ArrayList<>();

    String sql = "SELECT data, descricao, tipo, valor, saldo FROM extrato WHERE data BETWEEN ? AND ? ORDER BY data";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDate(1, java.sql.Date.valueOf(inicio));
        stmt.setDate(2, java.sql.Date.valueOf(fim));

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String data = rs.getString("data");
                String descricao = rs.getString("descricao");
                String tipo = rs.getString("tipo");
                double valor = rs.getDouble("valor");
                double saldo = rs.getDouble("saldo");

                linhas.add(new ExtratoLinha(data, descricao, tipo, valor, saldo));
            }
        }

    } catch (SQLException e) {
        System.err.println("❌ Erro ao acessar o banco de dados: " + e.getMessage());
    }

    return linhas;
}
