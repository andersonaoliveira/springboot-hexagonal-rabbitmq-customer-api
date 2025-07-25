BufferedWriter writer = new BufferedWriter(new FileWriter("extrato.csv"));
Random random = new Random();
LocalDate start = LocalDate.of(2023, 1, 1);
double saldo = 1000.00;

for (int i = 0; i < 100_000_000; i++) {
    LocalDate data = start.plusDays(random.nextInt(365));
    String descricao = "Compra " + random.nextInt(1000);
    String tipo = random.nextBoolean() ? "CREDITO" : "DEBITO";
    double valor = Math.round((random.nextDouble() * 500) * 100.0) / 100.0;
    saldo += tipo.equals("CREDITO") ? valor : -valor;
    writer.write(data + "," + descricao + "," + tipo + "," + valor + "," + saldo + "\n");
}
writer.close();
