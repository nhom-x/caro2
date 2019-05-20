
package caro;

import com.sun.javafx.collections.ObservableListWrapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class SceneController implements Initializable {
    private static double BOARD_WIDTH = 390;
    private static double BOARD_HEIGHT = 390;
    private static double CELL_SIZE = 25;
    private static Color board_background = Color.WHITE;
    private static int pad = 8;
    private Canvas board;
    private int board_size; //number of rows and columns
    private int level;  //hard level when playing with computer
    private GraphicsContext board_gc;
    private int playingMap[][];
    Player players[];
    private int mainPointStep = 5;
    private int minorPointStep = 1;
    private int turn = 1;   //1: first player or 2: second
    
    
    @FXML
    HBox pane_board;

    @FXML
    TextField tf_name;
    @FXML
    TextField tf_name1;

    @FXML
    ComboBox cb_size;
    @FXML
    ComboBox cb_level;

    @FXML
    Label lb_message;
    
     @FXML
    private void handleClosing(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleNewGame(ActionEvent event) {
        drawBoard(board_size, board_gc);
        resetBoard(board_size);
        players[1] = new Player();
        players[2] = new Player();
        String name1 = tf_name.getText();
        String name2 = tf_name1.getText();
        players[1].setName(name1);
        players[2].setName(name2);
        board.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("Clicked at (" + event.getX()+","+event.getY()+")");
                int r = (int) ((event.getX() - pad) / (CELL_SIZE + 4)), c = (int) ((event.getY() - pad) / (CELL_SIZE + 4));
                if (r < board_size && c < board_size) {
                    if (playingMap[r][c] == 0) {
                        if (turn == 1) {
                            drawCross(r, c);
                            playingMap[r][c] = turn;
                            if (checkWinner(r, c)) {
                                //System.out.println("Winner!");
                                lb_message.setText(players[turn].getName() + " wins!");
                                board.setOnMouseClicked(null);
                            }
                            if (checkDraw()) {
                                lb_message.setText("Draw!");
                                board.setOnMouseClicked(null);
                            } else {
                                turn = 2;
                            }

                        } else {
                            drawCircle(r, c);
                            playingMap[r][c] = turn;
                            if (checkWinner(r, c)) {
                                //System.out.println("Winner!");
                                lb_message.setText(players[turn].getName() + " wins!");
                                board.setOnMouseClicked(null);
                            }
                            if (checkDraw()) {
                                lb_message.setText("Draw!");
                                board.setOnMouseClicked(null);
                            } else {
                                turn = 1;
                            }
                        }
                    }
                }
            }
        });
        //lb_message.setText("");
    }

    @FXML
    private void newGameWithComputer(ActionEvent event) {
        drawBoard(board_size, board_gc);
        resetBoard(board_size);

        players[1] = new Player();
        players[2] = new Player();
        players[1].setName("You");
        players[2].setName("Computer");
        board.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("Clicked at (" + event.getX()+","+event.getY()+")");
                int r = (int) ((event.getX() - pad) / (CELL_SIZE + 4)), c = (int) ((event.getY() - pad) / (CELL_SIZE + 4));
                //Humain Player playing
                if (r < board_size && c < board_size && playingMap[r][c] == 0) {
                    if (!playAt(1, r, c)) {
                        board.setOnMouseClicked(null);
                        //Computer Player playing;
                        if (!computerPlays(2)) {
                            board.setOnMouseClicked(this);
                        }
                    }
                }

            }
        });
    }

    //return true if one player wins after this play
     private boolean playAt(int turn, int r, int c) {
        if (r < board_size && c < board_size) {
            if (playingMap[r][c] == 0) {
                if (turn == 1) {

                    drawCross(r, c);
                    playingMap[r][c] = turn;
                    if (checkWinner(r, c)) {
                        //System.out.println("Winner!");
                        lb_message.setText(players[turn].getName() + " wins!");
                        board.setOnMouseClicked(null);
                        return true;
                    } else if (checkDraw()) {
                        lb_message.setText("Draw!");
                        board.setOnMouseClicked(null);
                    } else {
                        turn = 2;
                    }

                } else {
                    drawCircle(r, c);
                    playingMap[r][c] = turn;
                    if (checkWinner(r, c)) {
                        //System.out.println("Winner!");
                        lb_message.setText(players[turn].getName() + " wins!");
                        board.setOnMouseClicked(null);
                        return true;
                    }
                    if (checkDraw()) {
                        lb_message.setText("Draw!");
                        board.setOnMouseClicked(null);
                    } else {
                        turn = 1;
                    }
                }
            }
        }
        return false;
    }
   

    private Position findBestPosition(int turn, int playingMap[][], int hardLevel) {
        Position p = new Position();
        //int r = -1, c = -1;
        List<Position> availablePos = new Vector<>();
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if (playingMap[i][j] == 0) {
                    //p.setRow(i);
                    //p.setColumn(j); 
                    int point = getPointAt(turn, i, j, playingMap, board_size);
                    Position pt = new Position(i, j);
                    if (point >= 4*mainPointStep) {
                        point = 6*mainPointStep;
                    }
                    pt.setPoint(point);
                    availablePos.add(pt);
                    //break;
                }
            }
            //if (p.getRow()>=0) break;
        }
        if (!availablePos.isEmpty()) {

            Comparator compareProc = new Comparator<Position>() {
                @Override
                public int compare(Position o1, Position o2) {
                    if (o1.getPoint() == o2.getPoint()) {
                        return 0;
                    } else if (o1.getPoint() < o2.getPoint()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            };

            switch (hardLevel) {
                case 1: //easy level
                    int N = availablePos.size();
                    Random rand = new Random();
                    int k = rand.nextInt(N);
                    p = availablePos.get(k);
                    break;
                case 2: //intermediate level
                    availablePos.sort(compareProc);
                    p = availablePos.get(0);
                    break;
                case 3: //hard level
                case 4:
                    for (int i = 0; i < availablePos.size(); i++) {
                        Position p1 = availablePos.get(i);
                        int oppPoint = getOpponentPoint(turn, p1.getRow(), p1.getColumn(), playingMap);
                        if (oppPoint >= 3) {
                            p1.setPoint(oppPoint + 1);
                        }
                    }

                    availablePos.sort(compareProc);
                    p = availablePos.get(0);
                //break;

            }

        }
        return p;
    }

    private int getOpponentPoint(int turn, int row, int column, int playingMap[][]) {
        int count = 0;  //count the number of positions next to (row, column)
        int i = row, j = column;
        int r_count = 0;
        //checking by row
        j = column - 1;
        while (j >= 0 && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            r_count = r_count + mainPointStep;
            j--;
        }
        if (j > 0) {
            r_count++;
        }
        j = column + 1;
        while (j < board_size && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            r_count = r_count + mainPointStep;
            j++;
        }
        if (j < board_size - 1) {
            r_count++;
        }

        int c_count = 0;
        //checking by column
        i = row - 1;
        j = column;
        while (i >= 0 && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            c_count = c_count + mainPointStep;
            i--;
        }
        if (i > 0) {
            c_count++;
        }

        i = row + 1;
        while (i < board_size && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            c_count = c_count + mainPointStep;
            i++;
        }
        if (i < board_size - 1) {
            c_count++;
        }

        //checking by first diagonal
        int fd_count = 0;
        i = row - 1;
        j = column - 1;
        while (i >= 0 && j >= 0 && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            fd_count = fd_count + mainPointStep;
            i--;
            j--;
        }
        if (i > 0 && j > 0) {
            fd_count++;
        }

        i = row + 1;
        j = column + 1;
        while (i < board_size && j < board_size && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            fd_count = fd_count + mainPointStep;
            i++;
            j++;
        }
        if (i < board_size - 1 && j < board_size - 1) {
            fd_count++;
        }

        //checking by second diagonal
        int sd_count = 0;
        i = row + 1;
        j = column - 1;
        while (i < board_size && j >= 0 && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            sd_count = sd_count + mainPointStep;
            i++;
            j--;
        }
        if (i < board_size - 1 && j > 0) {
            sd_count++;
        }

        i = row - 1;
        j = column + 1;
        while (i >= 0 && j < board_size && playingMap[i][j] != 0 && playingMap[i][j] != turn) {
            sd_count = sd_count + mainPointStep;
            i--;
            j++;
        }
        if (i > 0 && j < board_size - 1) {
            sd_count++;
        }

        return Math.max(Math.max(r_count, c_count), Math.max(fd_count, sd_count));
    }

    //return true if computer wins after this play
    private boolean computerPlays(int turn) {
        //find suitable cell (r,c)
        boolean ret = false;
        //int hardLevel = 2;
        Position p = findBestPosition(turn, playingMap, level);
        if (p.getRow() >= 0) {
            ret = playAt(turn, p.getRow(), p.getColumn());
        } else {
            lb_message.setText("Computer lost!");
        }
        return ret;
    }

    //the match is draw if no pass left and no one wins
    private boolean checkDraw() {
        //boolean r = false;
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if (playingMap[i][j] == 0) {
                    //break;
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    private void handleBoardSizeChanged(ActionEvent event) {
        int n = 7;
        if (cb_size.getValue().equals("7x7")) {
            board_size = 7;
        }
        if (cb_size.getValue().equals("9x9")) {
            board_size = 9;
        }
        if (cb_size.getValue().equals("11x11")) {
            board_size = 11;
        }
        if (cb_size.getValue().equals("13x13")) {
            board_size = 13;
        }
        drawBoard(board_size, board_gc);
        resetBoard(board_size);
        //System.out.println("Size changed");
    }

    @FXML
    private void handleLevelChanged(ActionEvent event) {
        level = 1;
        if (cb_level.getValue().equals("Easy")) {
            level = 1;
        }
        if (cb_level.getValue().equals("Intermediate")) {
            level = 2;
        }
        if (cb_level.getValue().equals("Hard")) {
            level = 3;
        }

        if (cb_level.getValue().equals("Very Hard")) {
            level = 4;
        }

    }

    private void drawCross(int row, int col) {
        int inner_pad = 3;
        int border_width = 4;
        board_gc.clearRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setFill(board_background);
        board_gc.fillRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setStroke(Color.RED);
        board_gc.setLineWidth(3.0);
        board_gc.strokeLine(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad, pad + row * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + col * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad);
        board_gc.strokeLine(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + row * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad);
    }

    private void drawCircle(int row, int col) {
        int inner_pad = 3;
        int border_width = 4;
        board_gc.clearRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setFill(board_background);
        board_gc.fillRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setStroke(Color.BLACK);
        board_gc.setLineWidth(3.0);
        board_gc.strokeOval(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad, CELL_SIZE - 2 * inner_pad, CELL_SIZE - 2 * inner_pad);
    }

    private void drawBoard(int n, GraphicsContext gc) {
        gc.clearRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        gc.setFill(board_background);
        //int pad = 4;
        double x = 0, y = 0;
        //double w = 25, h = 25;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                gc.fillRect(pad + i * (CELL_SIZE + 4), pad + j * (CELL_SIZE + 4), CELL_SIZE, CELL_SIZE);
            }
        }
        board_gc.setLineWidth(3.0);
        board_gc.setStroke(Color.RED);
        gc.strokeRect(2, 2, pad + n * (CELL_SIZE + 4), pad + n * (CELL_SIZE + 4));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Rectangle r = new Rectangle(30, 30);
        players = new Player[3];
        List<String> items = new ArrayList<>();
        items.add("7x7");
        items.add("9x9");
        items.add("11x11");
        items.add("13x13");
        //ObservableList items = new ObservableListWrapper();
        cb_size.setItems(new ObservableListWrapper(items));
        cb_size.setValue("9x9");

        List<String> items2 = new ArrayList<>();
        items2.add("Easy");
        items2.add("Intermediate");
        items2.add("Hard");
        items2.add("Very Hard");
        //items.add("13x13");
        //ObservableList items = new ObservableListWrapper();
        cb_level.setItems(new ObservableListWrapper(items2));
        cb_level.setValue("Intermediate");
        level = 2;

        board = new Canvas(BOARD_WIDTH, BOARD_HEIGHT);
        //pane_main.setCenter(board);
        pane_board.getChildren().add(board);

        board_gc = board.getGraphicsContext2D();
        board_size = 9;
        drawBoard(board_size, board_gc);
        resetBoard(board_size);
        //lb_message.setText("");

    }

    private void resetBoard(int size) {
        //jPanel_board.setVisible(false);

        if (playingMap != null) {
            playingMap = null;
        }
        playingMap = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                playingMap[i][j] = 0;
            }
        }
        lb_message.setText("");
    }

    private boolean checkWinner(int row, int column) {
        int current = playingMap[row][column];
        if (current == 0) {
            return false;
        }
        int count = 1;
        int i = row, j = column;
        //checking by row
        j = column - 1;
        while (j >= 0 && playingMap[i][j] == current && count < 5) {
            count++;
            j--;
        }
        j = column + 1;
        while (j < board_size && playingMap[i][j] == current && count < 5) {
            count++;
            j++;
        }

        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }
        //checking by column
        i = row - 1;
        j = column;
        while (i >= 0 && playingMap[i][j] == current && count < 5) {
            count++;
            i--;
        }

        i = row + 1;
        while (i < board_size && playingMap[i][j] == current && count < 5) {
            count++;
            i++;
        }

        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }
        //checking by first diagonal
        i = row - 1;
        j = column - 1;
        while (i >= 0 && j >= 0 && playingMap[i][j] == current && count < 5) {
            count++;
            i--;
            j--;
        }

        i = row + 1;
        j = column + 1;
        while (i < board_size && j < board_size && playingMap[i][j] == current && count < 5) {
            count++;
            i++;
            j++;
        }
        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }
        //checking by second diagonal
        i = row + 1;
        j = column - 1;
        while (i < board_size && j >= 0 && playingMap[i][j] == current && count < 5) {
            count++;
            i++;
            j--;
        }

        i = row - 1;
        j = column + 1;
        while (i >= 0 && j < board_size && playingMap[i][j] == current && count < 5) {
            count++;
            i--;
            j++;
        }

        if (count >= 5) {
            return true;
        } else {
            return false;
        }

    }

    //check the point at (row, column) if play turn
    private int getPointAt(int turn, int row, int column, int playingMap[][], int board_size) {

        int count = 0;  //count the number of positions next to (row, column)
        int i = row, j = column;
        int r_count = 0;
        //checking by row
        j = column - 1;
        while (j >= 0 && playingMap[i][j] == turn) {
            r_count = r_count + mainPointStep;
            j--;
        }
        if (j > 0) {
            r_count++;
        }

        j = column + 1;
        while (j < board_size && playingMap[i][j] == turn) {
            r_count = r_count + mainPointStep;
            j++;
        }
        if (j < board_size - 1) {
            r_count++;
        }

        int c_count = 0;
        //checking by column
        i = row - 1;
        j = column;
        while (i >= 0 && playingMap[i][j] == turn) {
            c_count = c_count + mainPointStep;
            i--;
        }
        if (i > 0) {
            c_count++;
        }

        i = row + 1;
        while (i < board_size && playingMap[i][j] == turn) {
            c_count = c_count + mainPointStep;
            i++;
        }
        if (i < board_size - 1) {
            c_count++;
        }
        //checking by first diagonal
        int fd_count = 0;
        i = row - 1;
        j = column - 1;
        while (i >= 0 && j >= 0 && playingMap[i][j] == turn) {
            fd_count = fd_count + mainPointStep;
            i--;
            j--;
        }
        if (i > 0 && j > 0) {
            fd_count++;
        }

        i = row + 1;
        j = column + 1;
        while (i < board_size && j < board_size && playingMap[i][j] == turn) {
            fd_count = fd_count + mainPointStep;
            i++;
            j++;
        }
        if (i < board_size - 1 && j < board_size - 1) {
            fd_count++;
        }
        //checking by second diagonal
        int sd_count = 0;
        i = row + 1;
        j = column - 1;
        while (i < board_size && j >= 0 && playingMap[i][j] == turn) {
            sd_count = sd_count + mainPointStep;
            i++;
            j--;
        }

        if (i < board_size - 1 && j > 0) {
            sd_count++;
        }
        i = row - 1;
        j = column + 1;
        while (i >= 0 && j < board_size && playingMap[i][j] == turn) {
            sd_count = sd_count + mainPointStep;
            i--;
            j++;
        }
        if (i > 0 && j < board_size - 1) {
            sd_count++;
        }

        return Math.max(Math.max(r_count, c_count), Math.max(fd_count, sd_count));

    }
}
      
