package com.converter;

import java.io.BufferedWriter; import java.io.FileWriter; import java.io.IOException; import java.time.LocalDate; import java.time.LocalDateTime; import java.time.ZoneOffset; import java.time.format.DateTimeFormatter; import java.util.List;

public class OfxExporter {

private static final DateTimeFormatter OFX_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
private static final int MAX_LINHAS_POR_ARQUIVO = 30000;

public static void salvar(List<ExtratoLinha> linhas, String caminhoBase) {
    int totalArquivos = (int) Math.ceil((double) linhas.size() / MAX_LINHAS_POR_ARQUIVO);

    for (int i = 0; i < totalArquivos; i++) {
        int inicio = i * MAX_LINHAS_POR_ARQUIVO;
        int fim = Math.min(inicio + MAX_LINHAS_POR_ARQUIVO, linhas.size());
        List<ExtratoLinha> subLista = linhas.subList(inicio, fim);

        String caminhoArquivo = caminhoBase.replace(".ofx", "_parte" + (i + 1) + ".ofx");
        salvarArquivo(subLista, caminhoArquivo);
    }
}

private static void salvarArquivo(List<ExtratoLinha> linhas, String caminhoArquivo) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {

        writer.write("OFXHEADER:100\n");
        writer.write("DATA:OFXSGML\n");
        writer.write("VERSION:102\n");
        writer.write("SECURITY:NONE\n");
        writer.write("ENCODING:USASCII\n");
        writer.write("CHARSET:1252\n");
        writer.write("COMPRESSION:NONE\n");
        writer.write("OLDFILEUID:NONE\n");
        writer.write("NEWFILEUID:NONE\n\n");
        writer.write("<OFX>\n");

        writer.write("    <SIGNONMSGSRSV1>\n");
        writer.write("        <SONRS>\n");
        writer.write("            <STATUS>\n");
        writer.write("                <CODE>0\n");
        writer.write("                <SEVERITY>INFO\n");
        writer.write("            </STATUS>\n");
        writer.write("            <DTSERVER>" + dataAtualFormatada() + "[-3:GMT]\n");
        writer.write("            <LANGUAGE>ENG\n");
        writer.write("            <FI>\n");
        writer.write("                <ORG>SANTANDER\n");
        writer.write("                <FID>SANTANDER\n");
        writer.write("            </FI>\n");
        writer.write("        </SONRS>\n");
        writer.write("    </SIGNONMSGSRSV1>\n");

        writer.write("    <BANKMSGSRSV1>\n");
        writer.write("        <STMTTRNRS>\n");
        writer.write("            <TRNUID>1\n");
        writer.write("            <STATUS>\n");
        writer.write("                <CODE>0\n");
        writer.write("                <SEVERITY>INFO\n");
        writer.write("            </STATUS>\n");
        writer.write("            <STMTRS>\n");
        writer.write("                <CURDEF>BRC\n");
        writer.write("                <BANKACCTFROM>\n");
        writer.write("                    <BANKID>033\n");
        writer.write("                    <ACCTID>0319130084955\n");
        writer.write("                    <ACCTTYPE>CHECKING\n");
        writer.write("                </BANKACCTFROM>\n");
        writer.write("                <BANKTRANLIST>\n");

        String dataInicio = linhas.isEmpty() ? dataAtualFormatada() : formatarDataOFX(linhas.get(0).data);
        String dataFim = linhas.isEmpty() ? dataAtualFormatada() : formatarDataOFX(linhas.get(linhas.size() - 1).data);

        writer.write("                    <DTSTART>" + dataInicio + "[-3:GMT]\n");
        writer.write("                    <DTEND>" + dataFim + "[-3:GMT]\n");

        int fitId = 100000;
        for (ExtratoLinha linha : linhas) {
            writer.write("                    <STMTTRN>\n");
            writer.write("                        <TRNTYPE>" + (linha.tipo.equalsIgnoreCase("CREDITO") ? "CREDIT" : "DEBIT") + "\n");
            writer.write("                        <DTPOSTED>" + formatarDataOFX(linha.data) + "[-3:GMT]\n");
            writer.write("                        <TRNAMT>" + (linha.tipo.equalsIgnoreCase("DEBITO") ? "-" : "") + String.format("%.2f", linha.valor).replace(",", ".") + "\n");
            writer.write("                        <FITID>" + fitId + "\n");
            writer.write("                        <CHECKNUM>" + fitId + "\n");
            writer.write("                        <PAYEEID>0\n");
            writer.write("                        <MEMO>" + linha.descricao + "\n");
            writer.write("                    </STMTTRN>\n");
            fitId++;
        }

        writer.write("                </BANKTRANLIST>\n");

        double saldoFinal = linhas.isEmpty() ? 0.0 : linhas.get(linhas.size() - 1).saldo;
        writer.write("                <LEDGERBAL>\n");
        writer.write("                    <BALAMT>" + String.format("%.2f", saldoFinal).replace(",", ".") + "\n");
        writer.write("                    <DTASOF>" + dataFim + "[-3:GMT]\n");
        writer.write("                </LEDGERBAL>\n");

        writer.write("            </STMTRS>\n");
        writer.write("        </STMTTRNRS>\n");
        writer.write("    </BANKMSGSRSV1>\n");
        writer.write("</OFX>\n");

        System.out.println("✅ OFX gerado com sucesso: " + caminhoArquivo);

    } catch (IOException e) {
        System.err.println("❌ Erro ao gerar OFX: " + e.getMessage());
    }
}

private static String formatarDataOFX(String dataOriginal) {
    if (dataOriginal.matches("\\d{14}\-\\d+:GMT\")) {
        return dataOriginal.substring(0, 14);
    }
    if (dataOriginal.matches("\\d{14}")) {
        return dataOriginal;
    }
    LocalDateTime dateTime = LocalDate.parse(dataOriginal, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        .atTime(12, 0);
    return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
}

private static String dataAtualFormatada() {
    return LocalDateTime.now(ZoneOffset.of("-03:00")).format(OFX_DATE_FORMAT);
}

}

