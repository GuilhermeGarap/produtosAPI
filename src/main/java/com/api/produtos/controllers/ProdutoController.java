package com.api.produtos.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.api.produtos.domain.produto.DadosCadastroProduto;
import com.api.produtos.domain.produto.DadosDetalhamentoProduto;
import com.api.produtos.domain.produto.DadosListaProduto;
import com.api.produtos.domain.produto.Produto;
import com.api.produtos.domain.produto.ProdutoEstoqueRequest;
import com.api.produtos.domain.produto.ProdutoRepository;
import com.api.produtos.services.ProdutoService;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    
    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ProdutoService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<DadosDetalhamentoProduto> cadastrar(@RequestBody @Valid DadosCadastroProduto dados, UriComponentsBuilder uriBuilder) {
        var produto = new Produto(dados);
        Produto produtoSalvo = service.cadastrarProduto(produto);

        var uri = uriBuilder.path("/produto/cadastrarProduto/{id}").buildAndExpand(produtoSalvo.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoProduto(produtoSalvo));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<DadosListaProduto>> listar() {
        List<Produto> produtos = repository.findAll();

        List<DadosListaProduto> dadosProdutos = produtos.stream().map(DadosListaProduto::new).collect(Collectors.toList());

        return ResponseEntity.ok(dadosProdutos);
    }

    @PostMapping("atualizarEstoque/{id}")
    public ResponseEntity<Produto> atualizarEstoque(
        @PathVariable Long id,
        @RequestBody @Valid ProdutoEstoqueRequest request) {

        Produto produtoAtualizado = service.atualizarEstoque(id, request.getQuantidadeAlterada());
        return ResponseEntity.ok(produtoAtualizado);
    }

    @PutMapping("editar/{id}")
    public ResponseEntity<Produto> editarProduto(
        @PathVariable Long id,
        @RequestBody @Valid Produto dadosAtualizados) {

        Produto produto = service.editarProduto(id, dadosAtualizados);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/estoque/excel")
    public void exportarEstoqueExcel(HttpServletResponse response) throws IOException {
        List<Produto> produtos = repository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estoque de Produtos");

        // Criar estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        // Cabeçalho
        Row header = sheet.createRow(0);
        header.setHeight((short) 500);
        String[] colunas = {"ID", "Nome do Produto", "Código de Barras", "Preço de Venda", "Preço de Custo", "Estoque Disponível"};
        for (int i = 0; i < colunas.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dados
        int rowNum = 1;
        for (Produto produto : produtos) {
            Row row = sheet.createRow(rowNum++);
            row.setHeight((short) 300);
            
            org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
            cell0.setCellValue(produto.getId());
            cell0.setCellStyle(dataStyle);
            
            org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
            cell1.setCellValue(produto.getNomeDoProduto());
            cell1.setCellStyle(dataStyle);
            
            org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
            cell2.setCellValue(produto.getCodigoBarras());
            cell2.setCellStyle(dataStyle);
            
            org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
            cell3.setCellValue(produto.getPrecoVenda().doubleValue());
            cell3.setCellStyle(dataStyle);
            
            org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
            cell4.setCellValue(produto.getPrecoCusto().doubleValue());
            cell4.setCellStyle(dataStyle);
            
            org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
            cell5.setCellValue(produto.getDisponivelEmEstoque());
            cell5.setCellStyle(dataStyle);
        }

        // Auto-dimensionar colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_estoque_produtos.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/estoque/pdf")
    public void exportarEstoquePdf(HttpServletResponse response) throws IOException {
        List<Produto> produtos = repository.findAll();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_estoque_produtos.pdf");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Cores
            Color headerColor = new DeviceRgb(0, 100, 0); // Verde escuro
            Color borderColor = new DeviceRgb(0, 0, 0); // Preto

            // Bordas
            Border thinBorder = new SolidBorder(borderColor, 0.5f);

            // Criar tabela
            Table table = new Table(new float[]{1, 3, 2, 2, 2, 2});
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            table.setMarginTop(20);
            table.setMarginBottom(20);

            // Cabeçalho
            String[] headers = {"ID", "Nome do Produto", "Código de Barras", "Preço de Venda", "Preço de Custo", "Estoque"};
            for (String header : headers) {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(headerColor)
                    .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setBorder(thinBorder)
                    .setPadding(8);
                table.addHeaderCell(cell);
            }

            // Dados
            for (Produto produto : produtos) {
                table.addCell(createCell(String.valueOf(produto.getId()), thinBorder));
                table.addCell(createCell(produto.getNomeDoProduto(), thinBorder));
                table.addCell(createCell(produto.getCodigoBarras(), thinBorder));
                table.addCell(createCell(String.valueOf(produto.getPrecoVenda()), thinBorder));
                table.addCell(createCell(String.valueOf(produto.getPrecoCusto()), thinBorder));
                table.addCell(createCell(String.valueOf(produto.getDisponivelEmEstoque()), thinBorder));
            }

            document.add(table);
        }
    }

    private com.itextpdf.layout.element.Cell createCell(String text, Border border) {
        return new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(text))
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setBorder(border)
            .setPadding(6);
    }
} 