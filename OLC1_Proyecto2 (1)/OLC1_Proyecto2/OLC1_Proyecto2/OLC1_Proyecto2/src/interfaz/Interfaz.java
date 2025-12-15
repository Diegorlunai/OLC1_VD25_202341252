package interfaz;

import analizadores.Parser;
import analizadores.Scanner;
import analizadores.TokenLista; // Importar la lista de tokens
import arbol.Instruccion;
import java.io.StringReader;
import java.util.LinkedList;
import javax.swing.*;
import java.awt.*;
import main.Salida;
import simbolo.TablaSimbolos;
import excepciones.Errores; 

public class Interfaz extends JFrame {

    private JTextArea areaCodigo;
    private JTextArea areaConsola;
    private JButton btnCompilar;
    private JButton btnReporte; 
    private JButton btnReporteTabla; 
    private JButton btnReporteTokens; // Nuevo Botón
    
    // Para guardar la memoria después de la ejecución
    private TablaSimbolos entornoFinal; 

    public Interfaz() {
        super("Mi Compilador - Fase 2");
        setSize(1100, 700); // Un poco más ancho para que quepan los botones
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        // --- 1. PANEL SUPERIOR (BOTONES) ---
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        // Botón Compilar
        btnCompilar = new JButton("▶ Compilar");
        btnCompilar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCompilar.setBackground(new Color(50, 205, 50)); 
        btnCompilar.setForeground(Color.WHITE);
        btnCompilar.setPreferredSize(new Dimension(130, 35));
        btnCompilar.addActionListener(e -> compilar());
        
        // Botón Reporte de Errores
        btnReporte = new JButton("📄 Errores");
        btnReporte.setFont(new Font("Arial", Font.BOLD, 14));
        btnReporte.setBackground(new Color(255, 69, 0)); 
        btnReporte.setForeground(Color.WHITE);
        btnReporte.setPreferredSize(new Dimension(130, 35));
        btnReporte.addActionListener(e -> generarReporteErrores());
        
        // Botón Reporte Tabla de Símbolos
        btnReporteTabla = new JButton("💾 Símbolos");
        btnReporteTabla.setFont(new Font("Arial", Font.BOLD, 14));
        btnReporteTabla.setBackground(new Color(60, 179, 113)); 
        btnReporteTabla.setForeground(Color.WHITE);
        btnReporteTabla.setPreferredSize(new Dimension(130, 35));
        btnReporteTabla.addActionListener(e -> {
            if(entornoFinal != null) {
                generarReporteTabla(entornoFinal);
            } else {
                JOptionPane.showMessageDialog(this, "Debe compilar primero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Botón Reporte de Tokens (NUEVO)
        btnReporteTokens = new JButton("🔠 Tokens");
        btnReporteTokens.setFont(new Font("Arial", Font.BOLD, 14));
        btnReporteTokens.setBackground(new Color(30, 144, 255)); // Azul
        btnReporteTokens.setForeground(Color.WHITE);
        btnReporteTokens.setPreferredSize(new Dimension(130, 35));
        btnReporteTokens.addActionListener(e -> generarReporteTokens());
        
        panelBotones.add(btnCompilar);
        panelBotones.add(btnReporte);
        panelBotones.add(btnReporteTabla);
        panelBotones.add(btnReporteTokens); // Agregar al panel
        add(panelBotones, BorderLayout.NORTH);

        // --- 2. PANEL CENTRAL (CÓDIGO Y CONSOLA) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Área de Código (Arriba)
        areaCodigo = new JTextArea();
        areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Editor de Código"));
        
        // Área de Consola (Abajo)
        areaConsola = new JTextArea();
        areaConsola.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaConsola.setBackground(Color.BLACK);
        areaConsola.setForeground(Color.GREEN);
        areaConsola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(areaConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola de Salida"));

        splitPane.setTopComponent(scrollCodigo);
        splitPane.setBottomComponent(scrollConsola);
        splitPane.setDividerLocation(450); 
        
        add(splitPane, BorderLayout.CENTER);
    }

    private void compilar() {
        String codigo = areaCodigo.getText();
        
        // 1. Limpiar todo antes de compilar
        Salida.limpiar(); 
        Errores.limpiar(); 
        TokenLista.limpiar(); // LIMPIAR TOKENS
        areaConsola.setText("");

        try {
            // 2. Ejecutar análisis
            TablaSimbolos entornoGlobal = new TablaSimbolos();
            Scanner scanner = new Scanner(new StringReader(codigo));
            Parser parser = new Parser(scanner);
            
            parser.parse();
            
            // 3. Ejecutar AST si no hay errores fatales
            if (Errores.listaErrores.isEmpty()) { 
                LinkedList<Instruccion> ast = parser.AST;
                if (ast != null) {
                    for (Instruccion ins : ast) {
                        if (ins != null) ins.ejecutar(entornoGlobal);
                    }
                }
            } else {
                 areaConsola.append("❌ NO SE EJECUTÓ: Se encontraron errores léxicos o sintácticos.\n");
            }
            
            // 4. Guardar entorno final
            this.entornoFinal = entornoGlobal; 
            
            // 5. Mostrar salida
            for (String linea : Salida.listaSalida) {
                areaConsola.append(linea + "\n");
            }
            areaConsola.append("\n--- Ejecución Finalizada ---");
            
            if (!Errores.listaErrores.isEmpty()) {
                areaConsola.append("\n\n⚠️ Se encontraron " + Errores.listaErrores.size() + " errores. Ver reporte HTML.");
            }

        } catch (Exception e) {
            areaConsola.append("❌ Error Grave: El análisis falló completamente. " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Reporte Errores
    private void generarReporteErrores() {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("ReporteErrores.html");
            writer.write("<html><head><style>table{border-collapse: collapse; width: 80%; margin: 20px auto;} th, td {text-align: left; padding: 10px;} tr:nth-child(even){background-color: #f2f2f2} th {background-color: #d32f2f; color: white;}</style></head><body>");
            writer.write("<h1>Reporte de Errores</h1>");
            
            if (Errores.listaErrores.isEmpty()) {
                writer.write("<p>✅ No se encontraron errores.</p>");
            } else {
                writer.write("<table border='1'><tr><th>Tipo</th><th>Descripcion</th><th>Linea</th><th>Columna</th></tr>");
                for (excepciones.Errores err : excepciones.Errores.listaErrores) {
                    writer.write("<tr>");
                    writer.write("<td>" + err.tipo + "</td>");
                    writer.write("<td>" + err.descripcion + "</td>");
                    writer.write("<td>" + err.linea + "</td>");
                    writer.write("<td>" + err.columna + "</td>");
                    writer.write("</tr>");
                }
                writer.write("</table>");
            }
            writer.write("</body></html>");
            writer.close();
            java.awt.Desktop.getDesktop().open(new java.io.File("ReporteErrores.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Reporte Tabla Símbolos
    private void generarReporteTabla(TablaSimbolos ts) {
        try {
            java.util.HashMap<String, simbolo.TablaSimbolos.Simbolo> simbolos = ts.obtenerTodos(); 
            java.io.FileWriter writer = new java.io.FileWriter("ReporteTablaSimbolos.html");
            writer.write("<html><head><style>table{border-collapse: collapse; width: 80%; margin: 20px auto;} th, td {text-align: left; padding: 10px;} tr:nth-child(even){background-color: #f2f2f2} th {background-color: #4CAF50; color: white;}</style></head><body>");
            writer.write("<h1>Reporte de Tabla de Símbolos</h1>");
            
            if (simbolos.isEmpty()) {
                writer.write("<p>Tabla vacía.</p>");
            } else {
                writer.write("<table border='1'><tr><th>ID</th><th>Valor</th><th>Tipo</th><th>Entorno</th></tr>");
                for (java.util.Map.Entry<String, simbolo.TablaSimbolos.Simbolo> entry : simbolos.entrySet()) {
                    simbolo.TablaSimbolos.Simbolo s = entry.getValue();
                    writer.write("<tr>");
                    writer.write("<td>" + s.getId() + "</td>");
                    writer.write("<td>" + s.getValor() + "</td>");
                    writer.write("<td>" + s.getTipo() + "</td>");
                    writer.write("<td>" + s.getEntorno() + "</td>");
                    writer.write("</tr>");
                }
                writer.write("</table>");
            }
            writer.write("</body></html>");
            writer.close();
            java.awt.Desktop.getDesktop().open(new java.io.File("ReporteTablaSimbolos.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- NUEVO MÉTODO: REPORTE DE TOKENS ---
    private void generarReporteTokens() {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("ReporteTokens.html");
            writer.write("<html><head><style>table{border-collapse: collapse; width: 90%; margin: 20px auto;} th, td {border: 1px solid #ddd; padding: 8px; text-align: left;} th {background-color: #3f51b5; color: white;} tr:nth-child(even){background-color: #f2f2f2;}</style></head><body>");
            writer.write("<h1 style='text-align:center;'>Reporte de Tokens</h1>");
            
            if (TokenLista.lista.isEmpty()) {
                writer.write("<p style='text-align:center;'>No hay tokens registrados. Compile el código primero.</p>");
            } else {
                writer.write("<table><tr><th>Lexema</th><th>Tipo (Token)</th><th>Línea</th><th>Columna</th></tr>");
                for (TokenLista.TokenDato t : TokenLista.lista) {
                    writer.write("<tr>");
                    writer.write("<td>" + t.lexema + "</td>");
                    writer.write("<td>" + t.tipo + "</td>");
                    writer.write("<td>" + t.linea + "</td>");
                    writer.write("<td>" + t.columna + "</td>");
                    writer.write("</tr>");
                }
                writer.write("</table>");
            }
            
            writer.write("</body></html>");
            writer.close();
            
            JOptionPane.showMessageDialog(this, "¡Reporte de Tokens generado!");
            java.awt.Desktop.getDesktop().open(new java.io.File("ReporteTokens.html"));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte de tokens: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Interfaz().setVisible(true);
        });
    }
}