package dih4cat.estructures;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScrollableTablesDemo extends JFrame {
    private JPanel panelContenedor;
    private JScrollPane scrollPane;
    private JButton botonAgregar;

    public ScrollableTablesDemo() {
        setTitle("Añadir JTables Dinámicamente");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel contenedor con BoxLayout vertical
        panelContenedor = new JPanel();
        panelContenedor.setLayout(new BoxLayout(panelContenedor, BoxLayout.Y_AXIS));

        // JScrollPane que envuelve el panel contenedor
        scrollPane = new JScrollPane(panelContenedor);
        add(scrollPane, BorderLayout.CENTER);

        // Botón para agregar tablas
        botonAgregar = new JButton("Agregar Tabla");
        botonAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTabla();
            }
        });

        add(botonAgregar, BorderLayout.SOUTH);
    }

    // Método para agregar una nueva JTable
    private void agregarTabla() {
        // Modelo de tabla de ejemplo
        String[] columnas = {"Columna 1", "Columna 2", "Columna 3"};
        Object[][] datos = {
                {"Dato 1", "Dato 2", "Dato 3"},
                {"Dato 4", "Dato 5", "Dato 6"}
        };

        JTable tabla = new JTable(new DefaultTableModel(datos, columnas));
        tabla.setPreferredScrollableViewportSize(new Dimension(350, 50));

        // Cada JTable se pone dentro de su propio JScrollPane
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        scrollTabla.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Se añade al panel contenedor
        panelContenedor.add(scrollTabla);
        panelContenedor.revalidate();  // Actualiza el layout
        panelContenedor.repaint();     // Redibuja la interfaz
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScrollableTablesDemo().setVisible(true);
        });
    }
}
