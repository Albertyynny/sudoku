package br.com.dio.gui;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class SudokuGUI extends JFrame {
    private static final int BOARD_LIMIT = 9;
    private static final int CELL_SIZE = 50;
    private static final int GRID_SIZE = CELL_SIZE * BOARD_LIMIT;
    
    private Board board;
    private Instant gameStartTime;
    private JTextField[][] cells;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton checkButton;
    private JButton resetButton;
    private JButton finishButton;
    private JButton hintButton;
    private Map<String, String> positions;
    private Map<String, Integer> solution;
    
    public SudokuGUI(String[] args) {
        
        positions = new HashMap<>();
        solution = new HashMap<>();
        
        
        String[] easyConfig = {
            
            "0,0;4,true", "1,0;7,true", "2,0;9,true", "3,0;5,true", "4,0;8,true", "5,0;6,true", "6,0;2,true", "7,0;3,false", "8,0;1,false",
            
            "0,1;1,true", "1,1;3,true", "2,1;5,true", "3,1;4,false", "4,1;7,true", "5,1;2,true", "6,1;8,false", "7,1;9,true", "8,1;6,true",
            
            "0,2;2,true", "1,2;6,true", "2,2;8,true", "3,2;9,false", "4,2;1,true", "5,2;3,true", "6,2;7,false", "7,2;4,false", "8,2;5,true",
            
            "0,3;5,true", "1,3;1,true", "2,3;3,true", "3,3;7,true", "4,3;6,false", "5,3;4,true", "6,3;9,false", "7,3;8,true", "8,3;2,true",
            
            "0,4;8,true", "1,4;9,true", "2,4;7,true", "3,4;1,true", "4,4;2,true", "5,4;5,true", "6,4;3,false", "7,4;6,true", "8,4;4,true",
           
            "0,5;6,true", "1,5;4,true", "2,5;2,true", "3,5;3,true", "4,5;9,false", "5,5;8,true", "6,5;1,true", "7,5;5,false", "8,5;7,true",
           
            "0,6;7,true", "1,6;5,true", "2,6;4,true", "3,6;2,false", "4,6;3,true", "5,6;9,true", "6,6;6,false", "7,6;1,true", "8,6;8,true",
           
            "0,7;9,true", "1,7;8,true", "2,7;1,true", "3,7;6,false", "4,7;4,true", "5,7;7,true", "6,7;5,false", "7,7;2,true", "8,7;3,true",
            
            "0,8;3,true", "1,8;2,true", "2,8;6,true", "3,8;8,true", "4,8;5,true", "5,8;1,false", "6,8;4,true", "7,8;7,false", "8,8;9,true"
        };
        
        
        initializeSolution();
        
        
        if (args != null && args.length > 0) {
            try {
                positions = Stream.of(args)
                    .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                    ));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao processar argumentos. Usando tabuleiro fácil predefinido.",
                    "Erro de Inicialização", 
                    JOptionPane.ERROR_MESSAGE);
                loadDefaultPositions(easyConfig);
            }
        } else {
            loadDefaultPositions(easyConfig);
        }
        
        setTitle("Sudoku - Modo Fácil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        
        JPanel gridPanel = new JPanel(new GridLayout(BOARD_LIMIT, BOARD_LIMIT));
        gridPanel.setPreferredSize(new Dimension(GRID_SIZE, GRID_SIZE));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        
        cells = new JTextField[BOARD_LIMIT][BOARD_LIMIT];
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                
                
                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == BOARD_LIMIT - 1 || row % 3 == 2) ? 2 : 1;
                int right = (col == BOARD_LIMIT - 1 || col % 3 == 2) ? 2 : 1;
                
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                
                
                final int finalRow = row;
                final int finalCol = col;
                cells[row][col].addKeyListener(new java.awt.event.KeyAdapter() {
                    public void keyTyped(java.awt.event.KeyEvent evt) {
                        char c = evt.getKeyChar();
                        
                        if (!(c >= '1' && c <= '9')) {
                            evt.consume();
                        } else if (cells[finalRow][finalCol].getText().length() >= 1) {
                            evt.consume();
                        }
                    }
                });
                
                gridPanel.add(cells[row][col]);
            }
        }
        
        
        JPanel controlPanel = new JPanel();
        startButton = new JButton("Iniciar Jogo");
        checkButton = new JButton("Verificar Status");
        resetButton = new JButton("Limpar Jogo");
        finishButton = new JButton("Finalizar Jogo");
        hintButton = new JButton("Dica");
        
        controlPanel.add(startButton);
        controlPanel.add(checkButton);
        controlPanel.add(resetButton);
        controlPanel.add(finishButton);
        controlPanel.add(hintButton);
        
        
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Bem-vindo ao Sudoku Fácil! Clique em 'Iniciar Jogo' para começar.");
        statusPanel.add(statusLabel);
        
        
        add(gridPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
        
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkGameStatus();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishGame();
            }
        });
        
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                provideHint();
            }
        });
        
        
        checkButton.setEnabled(false);
        resetButton.setEnabled(false);
        finishButton.setEnabled(false);
        hintButton.setEnabled(false);
        
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeSolution() {
        
        solution.put("0,0", 4); solution.put("1,0", 7); solution.put("2,0", 9); 
        solution.put("3,0", 5); solution.put("4,0", 8); solution.put("5,0", 6); 
        solution.put("6,0", 2); solution.put("7,0", 3); solution.put("8,0", 1);
        
        solution.put("0,1", 1); solution.put("1,1", 3); solution.put("2,1", 5); 
        solution.put("3,1", 4); solution.put("4,1", 7); solution.put("5,1", 2); 
        solution.put("6,1", 8); solution.put("7,1", 9); solution.put("8,1", 6);
        
        solution.put("0,2", 2); solution.put("1,2", 6); solution.put("2,2", 8); 
        solution.put("3,2", 9); solution.put("4,2", 1); solution.put("5,2", 3); 
        solution.put("6,2", 7); solution.put("7,2", 4); solution.put("8,2", 5);
        
        solution.put("0,3", 5); solution.put("1,3", 1); solution.put("2,3", 3); 
        solution.put("3,3", 7); solution.put("4,3", 6); solution.put("5,3", 4); 
        solution.put("6,3", 9); solution.put("7,3", 8); solution.put("8,3", 2);
        
        solution.put("0,4", 8); solution.put("1,4", 9); solution.put("2,4", 7); 
        solution.put("3,4", 1); solution.put("4,4", 2); solution.put("5,4", 5); 
        solution.put("6,4", 3); solution.put("7,4", 6); solution.put("8,4", 4);
        
        solution.put("0,5", 6); solution.put("1,5", 4); solution.put("2,5", 2); 
        solution.put("3,5", 3); solution.put("4,5", 9); solution.put("5,5", 8); 
        solution.put("6,5", 1); solution.put("7,5", 5); solution.put("8,5", 7);
        
        solution.put("0,6", 7); solution.put("1,6", 5); solution.put("2,6", 4); 
        solution.put("3,6", 2); solution.put("4,6", 3); solution.put("5,6", 9); 
        solution.put("6,6", 6); solution.put("7,6", 1); solution.put("8,6", 8);
        
        solution.put("0,7", 9); solution.put("1,7", 8); solution.put("2,7", 1); 
        solution.put("3,7", 6); solution.put("4,7", 4); solution.put("5,7", 7); 
        solution.put("6,7", 5); solution.put("7,7", 2); solution.put("8,7", 3);
        
        solution.put("0,8", 3); solution.put("1,8", 2); solution.put("2,8", 6); 
        solution.put("3,8", 8); solution.put("4,8", 5); solution.put("5,8", 1); 
        solution.put("6,8", 4); solution.put("7,8", 7); solution.put("8,8", 9);
    }
    
    private void loadDefaultPositions(String[] config) {
        for (String conf : config) {
            String[] parts = conf.split(";");
            positions.put(parts[0], parts[1]);
        }
    }
    
    private Map<String, String> createDefaultPositions() {
        Map<String, String> defaultPositions = new HashMap<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                defaultPositions.put(i + "," + j, "0,false");
            }
        }
        return defaultPositions;
    }
    
    private void startGame() {
        if (nonNull(board)) {
            statusLabel.setText("O jogo já foi iniciado");
            return;
        }
        
        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                initializeCell(spaces, i, j);
            }
        }
        
        board = new Board(spaces);
        gameStartTime = Instant.now();
        
        
        startButton.setEnabled(false);
        checkButton.setEnabled(true);
        resetButton.setEnabled(true);
        finishButton.setEnabled(true);
        hintButton.setEnabled(true);
        
        statusLabel.setText("O jogo começou! Preencha os números de 1 a 9. Use o botão 'Dica' se precisar de ajuda.");
        
        
        highlightEmptyCells();
    }
    
    private void highlightEmptyCells() {
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                if (cells[i][j].isEditable()) {
                    cells[i][j].setBackground(new Color(255, 255, 204)); 
                }
            }
        }
    }
    
    private void initializeCell(List<List<Space>> spaces, int i, int j) {
        String key = i + "," + j;
        String positionConfig = positions.get(key);
        
        int expected = 0;
        boolean fixed = false;
        
        if (positionConfig != null) {
            String[] parts = positionConfig.split(",");
            expected = Integer.parseInt(parts[0]);
            fixed = Boolean.parseBoolean(parts[1]);
        }
        
        Space currentSpace = new Space(expected, fixed);
        spaces.get(i).add(currentSpace);
        
        
        if (fixed && expected > 0) {
            cells[i][j].setText(String.valueOf(expected));
            cells[i][j].setEditable(false);
            cells[i][j].setBackground(new Color(220, 220, 255)); 
            cells[i][j].setForeground(Color.BLUE);
        } else {
            cells[i][j].setText("");
            cells[i][j].setEditable(true);
            cells[i][j].setBackground(Color.WHITE);
            cells[i][j].setForeground(Color.BLACK);
        }
    }
    
    private void checkGameStatus() {
        if (isNull(board)) {
            statusLabel.setText("O jogo ainda não foi iniciado");
            return;
        }
        
        updateBoardFromUI();
        
        
        if (board.hasErrors()) {
            statusLabel.setText("Atenção: o jogo contém erros. Verifique os números destacados em vermelho.");
            highlightErrors();
        } else {
            statusLabel.setText("Ótimo! O jogo não contém erros. Continue preenchendo os espaços vazios.");
            resetCellColors();
            highlightEmptyCells();
        }
    }
    
    private void highlightErrors() {
        
        resetCellColors();
        
        
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                if (cells[i][j].isEditable() && !cells[i][j].getText().isEmpty()) {
                    try {
                        int value = Integer.parseInt(cells[i][j].getText());
                        String key = j + "," + i;
                        if (solution.containsKey(key) && solution.get(key) != value) {
                            cells[i][j].setBackground(new Color(255, 200, 200)); 
                        }
                    } catch (NumberFormatException e) {
                        
                    }
                }
            }
        }
    }
    
    private void resetCellColors() {
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                if (cells[i][j].isEditable()) {
                    cells[i][j].setBackground(Color.WHITE);
                } else {
                    cells[i][j].setBackground(new Color(220, 220, 255)); 
                }
            }
        }
    }
    
    private void resetGame() {
        if (isNull(board)) {
            statusLabel.setText("O jogo ainda não foi iniciado");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja limpar seu jogo e perder todo seu progresso?", 
                "Confirmar Reset", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            board.reset();
            
            
            for (int i = 0; i < BOARD_LIMIT; i++) {
                for (int j = 0; j < BOARD_LIMIT; j++) {
                    if (cells[i][j].isEditable()) {
                        cells[i][j].setText("");
                    }
                }
            }
            
            highlightEmptyCells();
            statusLabel.setText("Jogo reiniciado. Todos os valores não fixos foram removidos.");
        }
    }
    
    private void finishGame() {
        if (isNull(board)) {
            statusLabel.setText("O jogo ainda não foi iniciado");
            return;
        }
        
        updateBoardFromUI();
        
        
        if (board.hasErrors()) {
            statusLabel.setText("Seu jogo contém erros, verifique os números destacados e corrija-os.");
            highlightErrors();
            return;
        }
        
        
        if (board.gameIsFinished()) {
            Instant endTime = Instant.now();
            Duration gameDuration = Duration.between(gameStartTime, endTime);
            long hours = gameDuration.toHours();
            long minutes = gameDuration.toMinutesPart();
            long seconds = gameDuration.toSecondsPart();
            
            String timeMessage = String.format("Tempo total de jogo: %02d:%02d:%02d", hours, minutes, seconds);
            
            JOptionPane.showMessageDialog(this, 
                    "Parabéns! Você concluiu o jogo!\n" + timeMessage, 
                    "Jogo Finalizado", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            
            board = null;
            gameStartTime = null;
            
            
            startButton.setEnabled(true);
            checkButton.setEnabled(false);
            resetButton.setEnabled(false);
            finishButton.setEnabled(false);
            hintButton.setEnabled(false);
            
            resetCellColors();
            statusLabel.setText("Jogo finalizado. Clique em 'Iniciar Jogo' para começar um novo jogo.");
        } else {
            statusLabel.setText("Você ainda precisa preencher alguns espaços vazios para completar o jogo.");
            highlightEmptyCells();
        }
    }
    
    private void provideHint() {
        if (isNull(board)) {
            statusLabel.setText("O jogo ainda não foi iniciado");
            return;
        }
        
        
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                if (cells[i][j].isEditable() && cells[i][j].getText().isEmpty()) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        
        if (emptyCells.isEmpty()) {
            statusLabel.setText("Não há mais dicas disponíveis. Todos os espaços já estão preenchidos.");
            return;
        }
        
        
        int[] selectedCell = emptyCells.get((int)(Math.random() * emptyCells.size()));
        int row = selectedCell[0];
        int col = selectedCell[1];
        
        
        String key = col + "," + row;
        int correctValue = solution.get(key);
        
        
        cells[row][col].setText(String.valueOf(correctValue));
        cells[row][col].setBackground(new Color(200, 255, 200)); 
        
        
        board.changeValue(col, row, correctValue);
        
        statusLabel.setText("Dica fornecida! Célula na linha " + (row + 1) + ", coluna " + (col + 1) + 
                           " preenchida com o valor " + correctValue + ".");
    }
    
    private void updateBoardFromUI() {
        
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                String text = cells[i][j].getText().trim();
                if (!text.isEmpty() && cells[i][j].isEditable()) {
                    try {
                        int value = Integer.parseInt(text);
                        if (value >= 1 && value <= 9) {
                            board.changeValue(j, i, value);
                        }
                    } catch (NumberFormatException e) {
                        
                    }
                } else if (text.isEmpty() && cells[i][j].isEditable()) {
                    board.clearValue(j, i);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI(args);
            gui.setVisible(true);
        });
    }
}