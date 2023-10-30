import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import Conex.Conexion;

public class MediatecaApp {
    
    private static DefaultTableModel materialTableModel;
    private static JComboBox<String> materialComboBox; // Declarar materialComboBox como una variable de instancia


    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
            new MediatecaApp().createAndShowGUI();
        });
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Mediateca App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        frame.setLocationRelativeTo(null);


        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }
    
    private static void placeComponents(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        
        JLabel welcomeLabel = new JLabel("Bienvenido a Mediateca");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeLabel);

        JButton addButton = new JButton("a. Agregar Material");
        JButton modifyButton = new JButton("b.Modificar Material");
        JButton listButton = new JButton("c. Listar Materiales Disponibles");
        JButton deleteButton = new JButton("d. Borrar Material");
        JButton searchButton = new JButton("e. Buscar Material");
        JButton exitButton = new JButton("f. Salir");

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        modifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(addButton);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(modifyButton);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(listButton);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(deleteButton);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(searchButton);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(exitButton);
        panel.add(Box.createRigidArea(new Dimension(0, 35)));


        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String materialType = JOptionPane.showInputDialog("Tipo de material (Libro, Revista, CD, DVD):").toLowerCase();

                if ("libro".equals(materialType)) {
                    agregarLibro();
                } else if ("revista".equals(materialType)) {
                    agregarRevista();
                } else if ("cd".equals(materialType)) {
                    agregarCD();
                } else if ("dvd".equals(materialType)) {
                    agregarDVD();
                } else {
                    JOptionPane.showMessageDialog(null, "Tipo de material no válido");
                }
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String materialType = JOptionPane.showInputDialog("Tipo de material a modificar (Libro, Revista, CD, DVD):").toLowerCase();

                if ("libro".equals(materialType)) {
                    modificarLibro();
                } else if ("revista".equals(materialType)) {
                    modificarRevista();
                } else if ("cd".equals(materialType)) {
                    modificarCD();
                } else if ("dvd".equals(materialType)) {
                    modificarDVD();
                } else {
                    JOptionPane.showMessageDialog(null, "Tipo de material no válido");
                }
            }
        });

        listButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarMaterialesDisponibles();

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String materialType = (String) materialComboBox.getSelectedItem();

                if (materialType == null || materialType.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Seleccione un tipo de material.");
                    return;
                }

                //Obtener el código de identificación
                String codigoIdentificacion = JOptionPane.showInputDialog("Código de Identificación:");
                if (codigoIdentificacion == null || codigoIdentificacion.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El código de identificación es obligatorio.");
                    return;
                }

                //Construir una consulta SQL para eliminar el material
                String deleteSql = "DELETE FROM " + materialType.toLowerCase() + "s" +" WHERE CodigoIdentificacion = ?";

                //Ejecutar la consulta SQL para eliminar el material
                if (borrarMaterial(deleteSql, codigoIdentificacion)) {
                    JOptionPane.showMessageDialog(null, "Material eliminado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el material.");
                }

                //Actualizar la tabla de materiales disponibles
                listarMaterialesDisponibles();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String materialType = (String) materialComboBox.getSelectedItem();

                if (materialType == null || materialType.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Seleccione un tipo de material.");
                    return;
                }

                //Obtener el código de identificación
                String codigoIdentificacion = JOptionPane.showInputDialog("Código de Identificación:");
                if (codigoIdentificacion == null || codigoIdentificacion.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El código de identificación es obligatorio.");
                    return;
                }

                //Construir una consulta SQL para buscar el material por CodigoIdentificacion
                String searchSql = "SELECT * FROM " + materialType.toLowerCase() + "s" +" WHERE CodigoIdentificacion = ?";

                //Realizar la búsqueda
                buscarMaterialPorCodigo(searchSql, codigoIdentificacion, materialType);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        
        //Agregar un JComboBox para seleccionar el tipo de material
        String[] tiposDeMaterial = {"Libro", "Revista", "CD", "DVD"};
        materialComboBox = new JComboBox<>(tiposDeMaterial); 
        panel.add(materialComboBox);
        
        materialTableModel = new DefaultTableModel();
        materialTableModel.addColumn("Código Interno");
        materialTableModel.addColumn("Título");
        materialTableModel.addColumn("Autor/Artista/Director");
        materialTableModel.addColumn("Tipo");
        materialTableModel.addColumn("Unidades Disponibles");

        JTable materialTable = new JTable(materialTableModel);
        JScrollPane scrollPane = new JScrollPane(materialTable);
        panel.add(scrollPane);
        
        
    }
    
    
    private static void agregarLibro() {
        String titulo = JOptionPane.showInputDialog("Título:");
        String autor = JOptionPane.showInputDialog("Autor:");
        int paginas = Integer.parseInt(JOptionPane.showInputDialog("Número de Páginas:"));
        String editorial = JOptionPane.showInputDialog("Editorial:");
        String isbn = JOptionPane.showInputDialog("ISBN:");
        int anoPublicacion = Integer.parseInt(JOptionPane.showInputDialog("Año de Publicación:"));
        int unidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Unidades Disponibles:"));

        if (agregarMaterial("Libros", titulo, autor, paginas, editorial, isbn, anoPublicacion, unidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "Libro agregado con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar el libro");
        }
    }

    //Función para agregar una revista a la base de datos
    private static void agregarRevista() {
        String titulo = JOptionPane.showInputDialog("Título:");
        String editorial = JOptionPane.showInputDialog("Editorial:");
        String periodicidad = JOptionPane.showInputDialog("Periodicidad:");
        String fechaPublicacion = JOptionPane.showInputDialog("Fecha de Publicación (YYYY-MM-DD):");
        int unidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Unidades Disponibles:"));

        if (agregarMaterial("Revistas", titulo, editorial, periodicidad, fechaPublicacion, unidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "Revista agregada con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar la revista");
        }
    }

    //Función para agregar un CD a la base de datos
    private static void agregarCD() {
        String titulo = JOptionPane.showInputDialog("Título:");
        String artista = JOptionPane.showInputDialog("Artista:");
        String genero = JOptionPane.showInputDialog("Género:");
        String duracion = JOptionPane.showInputDialog("Duración (HH:MM:SS):");
        int numeroCanciones = Integer.parseInt(JOptionPane.showInputDialog("Número de Canciones:"));
        int unidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Unidades Disponibles:"));

        if (agregarMaterial("CDs", titulo, artista, genero, duracion, numeroCanciones, unidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "CD agregado con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar el CD");
        }
    }

    //Función para agregar un DVD a la base de datos
    private static void agregarDVD() {
        String titulo = JOptionPane.showInputDialog("Título:");
        String director = JOptionPane.showInputDialog("Director:");
        String duracion = JOptionPane.showInputDialog("Duración (HH:MM:SS):");
        String genero = JOptionPane.showInputDialog("Género:");
        int unidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Unidades Disponibles:"));

        if (agregarMaterial("DVDs", titulo, director, duracion, genero, unidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "DVD agregado con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar el DVD");
        }
    }

    //Función para agregar material a la base de datos
    private static boolean agregarMaterial(String tableName, Object... values) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mediateca";
        String user = "root";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);

            String sql = "";
            if ("Libros".equals(tableName)) {
                sql = "INSERT INTO Libros (Titulo, Autor, Paginas, Editorial, ISBN, AñoPublicacion, UnidadesDisponibles) VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else if ("Revistas".equals(tableName)) {
                sql = "INSERT INTO Revistas (Titulo, Editorial, Periodicidad, FechaPublicacion, UnidadesDisponibles) VALUES (?, ?, ?, ?, ?)";
            } else if ("CDs".equals(tableName)) {
                sql = "INSERT INTO CDs (Titulo, Artista, Genero, Duracion, NumeroCanciones, UnidadesDisponibles) VALUES (?, ?, ?, ?, ?, ?)";
            } else if ("DVDs".equals(tableName)) {
                sql = "INSERT INTO DVDs (Titulo, Director, Duracion, Genero, UnidadesDisponibles) VALUES (?, ?, ?, ?, ?)";
            }

            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof String) {
                    statement.setString(i + 1, (String) values[i]);
                } else if (values[i] instanceof Integer) {
                    statement.setInt(i + 1, (Integer) values[i]);
                }
            }

            int rowsAffected = statement.executeUpdate();

            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    private static void modificarLibro() {
        String titulo = JOptionPane.showInputDialog("Título del libro a modificar:");
        int nuevasUnidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Nuevas unidades disponibles:"));

        if (modificarMaterial("Libros", titulo, nuevasUnidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "Libro modificado con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al modificar el libro");
        }
    }

    private static void modificarRevista() {
        String titulo = JOptionPane.showInputDialog("Título de la revista a modificar:");
        int nuevasUnidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Nuevas unidades disponibles:"));

        if (modificarMaterial("Revistas", titulo, nuevasUnidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "Revista modificada con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al modificar la revista");
        }
    }

    private static void modificarCD() {
        String titulo = JOptionPane.showInputDialog("Título del CD a modificar:");
        int nuevasUnidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Nuevas unidades disponibles:"));

        if (modificarMaterial("CDs", titulo, nuevasUnidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "CD modificado con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al modificar el CD");
        }
    }

    private static void modificarDVD() {
        String titulo = JOptionPane.showInputDialog("Título del DVD a modificar:");
        int nuevasUnidadesDisponibles = Integer.parseInt(JOptionPane.showInputDialog("Nuevas unidades disponibles:"));

        if (modificarMaterial("DVDs", titulo, nuevasUnidadesDisponibles)) {
            JOptionPane.showMessageDialog(null, "DVD modificado con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al modificar el DVD");
        }
    }

    //Función para modificar las unidades disponibles del material en la base de datos
    private static boolean modificarMaterial(String tableName, String titulo, int nuevasUnidades) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mediateca";
        String user = "root";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);

            String sql = "UPDATE " + tableName + " SET UnidadesDisponibles = ? WHERE Titulo = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, nuevasUnidades);
            statement.setString(2, titulo);

            int rowsAffected = statement.executeUpdate();

            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void listarMaterialesDisponibles() {
        materialTableModel.setRowCount(0); // Limpiar la tabla antes de agregar datos

        String jdbcUrl = "jdbc:mysql://localhost:3306/mediateca";
        String user = "root";
        String password = "";
        String tipoMaterial = (String) materialComboBox.getSelectedItem(); // Obtener el tipo de material seleccionado

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);

            String sql = "";

            if ("Libro".equals(tipoMaterial)) {
                sql = "SELECT * FROM Libros";
                materialTableModel.setColumnCount(0);

                materialTableModel.addColumn("Codigo Identificacion");
                materialTableModel.addColumn("Titulo");
                materialTableModel.addColumn("Autor");
                materialTableModel.addColumn("Tipo");
                materialTableModel.addColumn("Paginas");
                materialTableModel.addColumn("Editorial");
                materialTableModel.addColumn("ISBN");
                materialTableModel.addColumn("Año Publicación");
                materialTableModel.addColumn("Unidades Disponibles");
            } else if ("Revista".equals(tipoMaterial)) {
                sql = "SELECT * FROM Revistas";
                materialTableModel.setColumnCount(0);
                materialTableModel.addColumn("Codigo Identificacion");
                materialTableModel.addColumn("Titulo");
                materialTableModel.addColumn("Editorial");
                materialTableModel.addColumn("Tipo");
                materialTableModel.addColumn("Periodicidad");
                materialTableModel.addColumn("Fecha Publicacion");
                materialTableModel.addColumn("Unidades Disponibles");

            } else if ("CD".equals(tipoMaterial)) {
                sql = "SELECT * FROM CDs";
                materialTableModel.setColumnCount(0);
                materialTableModel.addColumn("Codigo Identificacion");
                materialTableModel.addColumn("Titulo");
                materialTableModel.addColumn("Artista");
                materialTableModel.addColumn("Tipo");

                materialTableModel.addColumn("Genero");
                materialTableModel.addColumn("Duracion");
                materialTableModel.addColumn("Numero Canciones");
                materialTableModel.addColumn("Unidades Disponibles");
            } else if ("DVD".equals(tipoMaterial)) {
                sql = "SELECT * FROM DVDs";
                materialTableModel.setColumnCount(0);
                materialTableModel.addColumn("Codigo Identificacion");
                materialTableModel.addColumn("Titulo");
                materialTableModel.addColumn("Director");
                materialTableModel.addColumn("Tipo");
                materialTableModel.addColumn("Duracion");
                materialTableModel.addColumn("Genero");
                materialTableModel.addColumn("Unidades Disponibles");
            }

            //Llamamos a la función listarMaterialesDesdeTabla con la consulta SQL específica
            listarMaterialesDesdeTabla(materialTableModel, connection, sql);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Función para listar los materiales disponibles desde una tabla específica
    //Función para listar todos los datos de una tabla
    //Dentro de la función listarMaterialesDesdeTabla
    //Modificar la función listarMaterialesDesdeTabla para manejar diferentes tipos de materiales
    
    private static void listarMaterialesDesdeTabla(DefaultTableModel model, Connection connection, String sql) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            if (materialComboBox.getSelectedItem().equals("Libro")) {
                String codigoIdentificacion = resultSet.getString("CodigoIdentificacion");
                String titulo = resultSet.getString("Titulo");
                String autor = resultSet.getString("Autor");
                int paginas = resultSet.getInt("Paginas");
                String editorial = resultSet.getString("Editorial");
                String isbn = resultSet.getString("ISBN");
                int anoPublicacion = resultSet.getInt("AñoPublicacion");
                int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");



                model.addRow(new Object[]{
                    codigoIdentificacion, titulo, autor, "Libro", paginas, editorial, isbn, anoPublicacion, unidadesDisponibles
                });

            } else if (materialComboBox.getSelectedItem().equals("Revista")) {
                String codigoIdentificacion = resultSet.getString("CodigoIdentificacion");
                String titulo = resultSet.getString("Titulo");
                String editorial = resultSet.getString("Editorial");
                String periodicidad = resultSet.getString("Periodicidad");
                String fechaPublicacion = resultSet.getString("FechaPublicacion");
                int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                model.addRow(new Object[]{
                    codigoIdentificacion, titulo, editorial, "Revista", periodicidad, fechaPublicacion, unidadesDisponibles
                });
            } else if (materialComboBox.getSelectedItem().equals("CD")) {
                String codigoIdentificacion = resultSet.getString("CodigoIdentificacion");
                String titulo = resultSet.getString("Titulo");
                String artista = resultSet.getString("Artista");
                String genero = resultSet.getString("Genero");
                String duracion = resultSet.getString("Duracion");
                int numeroCanciones = resultSet.getInt("NumeroCanciones");
                int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                model.addRow(new Object[]{
                    codigoIdentificacion, titulo, artista, "CD", genero, duracion, numeroCanciones, unidadesDisponibles
                });
            } else if (materialComboBox.getSelectedItem().equals("DVD")) {
                String codigoIdentificacion = resultSet.getString("CodigoIdentificacion");
                String titulo = resultSet.getString("Titulo");
                String director = resultSet.getString("Director");
                String duracion = resultSet.getString("Duracion");
                String genero = resultSet.getString("Genero");
                int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                model.addRow(new Object[]{
                    codigoIdentificacion, titulo, director, "DVD", duracion, genero, unidadesDisponibles
                });
            }
        }

    }
    private static boolean borrarMaterial(String deleteSql, String codigoIdentificacion) {
    String jdbcUrl = "jdbc:mysql://localhost:3306/mediateca";
    String user = "root";
    String password = "";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);

            PreparedStatement statement = connection.prepareStatement(deleteSql);
            statement.setString(1, codigoIdentificacion);

            int rowsAffected = statement.executeUpdate();

            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static void buscarMaterialPorCodigo(String sql, String codigoIdentificacion, String materialType) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mediateca";
        String user = "root";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, codigoIdentificacion);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Mostrar los detalles del material encontrado
                if ("Libro".equals(materialType)) {
                    String titulo = resultSet.getString("Titulo");
                    String autor = resultSet.getString("Autor");
                    int paginas = resultSet.getInt("Paginas");
                    String editorial = resultSet.getString("Editorial");
                    String isbn = resultSet.getString("ISBN");
                    int anoPublicacion = resultSet.getInt("AñoPublicacion");
                    int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                    String mensaje = "Material encontrado:\n" +
                            "Título: " + titulo + "\n" +
                            "Autor: " + autor + "\n" +
                            "Páginas: " + paginas + "\n" +
                            "Editorial: " + editorial + "\n" +
                            "ISBN: " + isbn + "\n" +
                            "Año de Publicación: " + anoPublicacion + "\n" +
                            "Unidades Disponibles: " + unidadesDisponibles;
                    JOptionPane.showMessageDialog(null, mensaje);
                } else if ("Revista".equals(materialType)) {
                    String titulo = resultSet.getString("Titulo");
                    String editorial = resultSet.getString("Editorial");
                    String periodicidad = resultSet.getString("Periodicidad");
                    String fechaPublicacion = resultSet.getString("FechaPublicacion");
                    int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                    String mensaje = "Material encontrado:\n" +
                            "Título: " + titulo + "\n" +
                            "Editorial: " + editorial + "\n" +
                            "Periodicidad: " + periodicidad + "\n" +
                            "Fecha de Publicación: " + fechaPublicacion + "\n" +
                            "Unidades Disponibles: " + unidadesDisponibles;
                    JOptionPane.showMessageDialog(null, mensaje);
                } else if ("CD".equals(materialType)) {
                    String titulo = resultSet.getString("Titulo");
                    String artista = resultSet.getString("Artista");
                    String genero = resultSet.getString("Genero");
                    String duracion = resultSet.getString("Duracion");
                    int numeroCanciones = resultSet.getInt("NumeroCanciones");
                    int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                    String mensaje = "Material encontrado:\n" +
                            "Título: " + titulo + "\n" +
                            "Artista: " + artista + "\n" +
                            "Género: " + genero + "\n" +
                            "Duración: " + duracion + "\n" +
                            "Número de Canciones: " + numeroCanciones + "\n" +
                            "Unidades Disponibles: " + unidadesDisponibles;
                    JOptionPane.showMessageDialog(null, mensaje);
                } else if ("DVD".equals(materialType)) {
                    String titulo = resultSet.getString("Titulo");
                    String director = resultSet.getString("Director");
                    String duracion = resultSet.getString("Duracion");
                    String genero = resultSet.getString("Genero");
                    int unidadesDisponibles = resultSet.getInt("UnidadesDisponibles");

                    String mensaje = "Material encontrado:\n" +
                            "Título: " + titulo + "\n" +
                            "Director: " + director + "\n" +
                            "Duración: " + duracion + "\n" +
                            "Género: " + genero + "\n" +
                            "Unidades Disponibles: " + unidadesDisponibles;
                    JOptionPane.showMessageDialog(null, mensaje);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Material no encontrado.");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error en la búsqueda del material.");
        }
    }


}



