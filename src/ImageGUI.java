import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageGUI extends JFrame {

    private JLabel originalLabel;
    private JLabel processedLabel;
    private JComboBox<String> filterSelector;
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private double[][] currentDCTMatrix;

    public ImageGUI() {
        setTitle("Processador de Imagem");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior com botões
        JPanel topPanel = new JPanel();
        JButton loadButton = new JButton("Carregar Imagem");
        JButton processButton = new JButton("Aplicar Filtro");
        JButton saveButton = new JButton("Salvar Imagem Processada");
        JButton useProcessedAsOriginalButton = new JButton("Usar Processada como Original"); 
        
        filterSelector = new JComboBox<>(new String[] {
            "Inverter Cores",
            "Equalizar Histograma",
            "Ruído Sal e Pimenta",
            "Filtro de Média",
            "Filtro de Mediana",
            "Binarização",
            "Limiarização",
            "Laplaciano",
            "Sobel",
            "Compressão Dinâmica",
            "Pseudo-Cor",
            "Otsu Binarização",
            "Otsu Limiarização",
            "Skeletonização (Zhang-Suen)",
            "Equalização HSL",
            "DCT",
            "IDCT",
            "Filtro Máximo",
            "Filtro Mínimo",
            "Filtro Ponto Médio"
        });

        topPanel.add(loadButton);
        topPanel.add(filterSelector);
        topPanel.add(processButton);
        topPanel.add(saveButton);
        topPanel.add(useProcessedAsOriginalButton); // Adiciona o novo botão

        add(topPanel, BorderLayout.NORTH);

        // Painel central com as imagens
        JPanel imagePanel = new JPanel(new GridLayout(1, 2));
        originalLabel = new JLabel("Imagem Original", JLabel.CENTER);
        processedLabel = new JLabel("Imagem Processada", JLabel.CENTER);
        imagePanel.add(originalLabel);
        imagePanel.add(processedLabel);

        add(imagePanel, BorderLayout.CENTER);

        // Ações dos botões
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    originalImage = ImageIO.read(selectedFile);
                    if (originalImage != null) {
                        originalLabel.setIcon(new ImageIcon(originalImage.getScaledInstance(350, -1, java.awt.Image.SCALE_SMOOTH)));
                        processedLabel.setIcon(null); // Limpa a imagem processada ao carregar uma nova
                        processedImage = null; // Zera a imagem processada
                    }
                    currentDCTMatrix = null; // Reset DCT matrix on new image load
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + ex.getMessage());
                }
            }
        });

        processButton.addActionListener(e -> {
            if (originalImage == null) {
                JOptionPane.showMessageDialog(this, "Carregue uma imagem primeiro.");
                return;
            }

            String selectedFilter = (String) filterSelector.getSelectedItem();

            try {
                if ("Inverter Cores".equals(selectedFilter)) {
                    InvertedImage handler = new InvertedImage("");
                    int[][][] matrix = handler.getInvertedRGBMatrix(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Equalizar Histograma".equals(selectedFilter)) {
                    EqualizedImage handler = new EqualizedImage("");
                    int[][][] matrix = handler.getEqualizedRGBMatrix(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Ruído Sal e Pimenta".equals(selectedFilter)) {
                    SalEPimenta handler = new SalEPimenta("");
                    int[][][] matrix = handler.getSaltAndPepperRGBMatrix(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Filtro de Média".equals(selectedFilter)) {
                    MediaFilter handler = new MediaFilter("");
                    int[][][] matrix = handler.applyMediaFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Filtro de Mediana".equals(selectedFilter)) {
                    MedianFilter handler = new MedianFilter("");
                    int[][][] matrix = handler.applyMedianFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Binarização".equals(selectedFilter)) {
                    String input = JOptionPane.showInputDialog(this, "Informe o limiar (0 a 255):", "128");
                    if (input != null) {
                        try {
                            int threshold = Integer.parseInt(input);
                            if (threshold < 0 || threshold > 255) {
                                JOptionPane.showMessageDialog(this, "O limiar deve estar entre 0 e 255.");
                                return;
                            }
                            BinarizedImage handler = new BinarizedImage("");
                            int[][][] matrix = handler.applyBinarizationFilter(originalImage, threshold);
                            processedImage = handler.buildImageFromRGBMatrix(matrix);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Valor inválido para limiar.");
                            return;
                        }
                    } else {
                        return;  // Usuário cancelou o input
                    }
                } else if ("Limiarização".equals(selectedFilter)) {
                    String input = JOptionPane.showInputDialog(this, "Informe o limiar (0 a 255):", "128");
                    if (input != null) {
                        try {
                            int threshold = Integer.parseInt(input);
                            if (threshold < 0 || threshold > 255) {
                                JOptionPane.showMessageDialog(this, "O limiar deve estar entre 0 e 255.");
                                return;
                            }
                            LimiarizationImage handler = new LimiarizationImage("");
                            int[][][] matrix = handler.applyLimiarizationFilter(originalImage, threshold);
                            processedImage = handler.buildImageFromRGBMatrix(matrix);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Valor inválido para limiar.");
                            return;
                        }
                    } else {
                        return;  // Usuário cancelou o input
                    }
                } else if ("Laplaciano".equals(selectedFilter)) {
                    LaplacianFilter handler = new LaplacianFilter("");
                    int[][][] matrix = handler.applyLaplacianFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                }
                else if ("Sobel".equals(selectedFilter)) {
                    SobelFilter handler = new SobelFilter("");
                    int[][][] matrix = handler.applySobelFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                }          
                else if ("Compressão Dinâmica".equals(selectedFilter)) {
                    String inputC = JOptionPane.showInputDialog(this, "Informe o valor de c (ex: 1.0):", "1.0");
                    String inputGamma = JOptionPane.showInputDialog(this, "Informe o valor de γ (gamma) (ex: 0.5):", "0.5");
                    if (inputC != null && inputGamma != null) {
                        try {
                            double c = Double.parseDouble(inputC);
                            double gamma = Double.parseDouble(inputGamma);
                
                            DynamicRangeCompression handler = new DynamicRangeCompression("");
                            int[][][] matrix = handler.applyDynamicRangeCompressionFilter(originalImage, c, gamma);
                            processedImage = handler.buildImageFromRGBMatrix(matrix);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Valores inválidos para c ou gamma.");
                            return;
                        }
                    } else {
                        return;  // Cancelado
                    }
                } else if ("Pseudo-Cor".equals(selectedFilter)) {
                    PseudoColor handler = new PseudoColor();
                    processedImage = handler.applyPseudoColor(originalImage);
                } else if ("Otsu Binarização".equals(selectedFilter)) {
                    OtsuThreshold handler = new OtsuThreshold();
                    processedImage = handler.binarize(originalImage);
                } else if ("Otsu Limiarização".equals(selectedFilter)) {
                    OtsuThreshold handler = new OtsuThreshold();
                    processedImage = handler.limiarize(originalImage);
                } else if ("Skeletonização (Zhang-Suen)".equals(selectedFilter)) {
                    ZhangSuenSkeletonizer handler = new ZhangSuenSkeletonizer();
                    processedImage = handler.skeletonize(originalImage);
                } else if ("Equalização HSL".equals(selectedFilter)) {
                    HSLUtils handler = new HSLUtils();
                    processedImage = handler.equalizeHslLuminance(originalImage);
                } else if ("DCT".equals(selectedFilter)) {
                    DCTUtils handler = new DCTUtils();
                    currentDCTMatrix = handler.dct2D(originalImage);
                    int N = currentDCTMatrix.length;
                    BufferedImage dctImage = new BufferedImage(N, N, BufferedImage.TYPE_INT_RGB);
                    double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++) {
                            if (currentDCTMatrix[i][j] < min) min = currentDCTMatrix[i][j];
                            if (currentDCTMatrix[i][j] > max) max = currentDCTMatrix[i][j];
                        }
                    if (max == min) {
                        max = min + 1; 
                    }
                    for (int i = 0; i < N; i++)
                        for (int j = 0; j < N; j++) {
                            int val = (int) ((currentDCTMatrix[i][j] - min) / (max - min) * 255);
                            val = Math.max(0, Math.min(255, val));
                            int rgb = (val << 16) | (val << 8) | val;
                            dctImage.setRGB(i, j, rgb);
                        }
                    processedImage = dctImage;
                } else if ("IDCT".equals(selectedFilter)) {
                    if (currentDCTMatrix == null) {
                        JOptionPane.showMessageDialog(this, "Execute DCT primeiro para obter os coeficientes.");
                        return;
                    }
                    DCTUtils handler = new DCTUtils();
                    processedImage = handler.idct2D(currentDCTMatrix);
                } else if ("Filtro Máximo".equals(selectedFilter)) {
                    MaxFilter handler = new MaxFilter();
                    int[][][] matrix = handler.applyMaxFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Filtro Mínimo".equals(selectedFilter)) {
                    MinFilter handler = new MinFilter();
                    int[][][] matrix = handler.applyMinFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                } else if ("Filtro Ponto Médio".equals(selectedFilter)) {
                    MidpointFilter handler = new MidpointFilter();
                    int[][][] matrix = handler.applyMidpointFilter(originalImage);
                    processedImage = handler.buildImageFromRGBMatrix(matrix);
                }          

                processedLabel.setIcon(new ImageIcon(processedImage.getScaledInstance(350, -1, java.awt.Image.SCALE_SMOOTH)));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar imagem: " + ex.getMessage());
                ex.printStackTrace(); 
            }
        });

        useProcessedAsOriginalButton.addActionListener(e -> {
            if (processedImage == null) {
                JOptionPane.showMessageDialog(this, "Não há imagem processada para usar como original.");
                return;
            }
            originalImage = new BufferedImage(processedImage.getWidth(), processedImage.getHeight(), processedImage.getType());
            Graphics2D g = originalImage.createGraphics();
            g.drawImage(processedImage, 0, 0, null);
            g.dispose();

            originalLabel.setIcon(new ImageIcon(originalImage.getScaledInstance(350, -1, java.awt.Image.SCALE_SMOOTH)));
            
            processedLabel.setIcon(null);
            processedImage = null;
            
            DCTUtils handler = new DCTUtils();
            currentDCTMatrix = handler.dct2D(originalImage); 

            JOptionPane.showMessageDialog(this, "Imagem processada movida para o painel de Imagem Original.");
        });


        saveButton.addActionListener(e -> {
            if (processedImage == null) {
                JOptionPane.showMessageDialog(this, "Não há imagem processada para salvar.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Imagem");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(processedImage, "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "Imagem salva com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar imagem: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageGUI gui = new ImageGUI();
            gui.setVisible(true);
        });
    }
}