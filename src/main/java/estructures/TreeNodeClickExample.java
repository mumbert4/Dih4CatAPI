package estructures;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeNodeClickExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JTree Node Click Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Crear nodos del árbol
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
            DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("Child 1");
            DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("Child 2");
            DefaultMutableTreeNode subChild = new DefaultMutableTreeNode("SubChild 1");

            root.add(child1);
            root.add(child2);
            child1.add(subChild);

            // Crear árbol
            JTree tree = new JTree(root);

            // Añadir TreeSelectionListener
            tree.addTreeSelectionListener(e -> {
                TreePath path = e.getPath(); // Obtiene el camino al nodo seleccionado
                DefaultMutableTreeNode selectedNode =
                        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); // Nodo seleccionado

                if (selectedNode != null) {
                    System.out.println("Nodo seleccionado: " + selectedNode.getUserObject());
                }
            });

            // Mostrar árbol
            frame.add(new JScrollPane(tree));
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}