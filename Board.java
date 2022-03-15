import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Board {
    private static int rowSize, totalBombs, seconds;
    private static Timer timer;
    private static JFrame difficultyFrame, mainFrame;
    private static int tileCount, clickedCount, flagCount;
    private static boolean firstClick, flagToggle;
    private static JButton toggleFlag;
    private static JLabel difficultyText, flagText;
    private static Tile[][] tiles;
    static final Color DEFAULT_COLOR = new Color(207, 255, 242);
    static final Color DEACTIVATED_COLOR = new Color(240, 243, 250);
    private static final Font FONT = new Font("Arial", Font.BOLD, 15);
    private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private static final ImageIcon FLAG_ICON = new ImageIcon(classLoader.getResource("flag.png"));
    private static final ImageIcon WIN_ICON = new ImageIcon(classLoader.getResource("win.png"));
    private static final ImageIcon BOMB_ICON = new ImageIcon(classLoader.getResource("bomb.png"));
    static final ImageIcon ONE_ICON = new ImageIcon(classLoader.getResource("1blue.png"));
    static final ImageIcon TWO_ICON = new ImageIcon(classLoader.getResource("2green.png"));
    final static ImageIcon THREE_ICON = new ImageIcon(classLoader.getResource("3red.png"));
    static final ImageIcon FOUR_ICON = new ImageIcon(classLoader.getResource("4purple.png"));
    static final ImageIcon FIVE_ICON = new ImageIcon(classLoader.getResource("5brown.png"));
    static final ImageIcon SIX_ICON = new ImageIcon(classLoader.getResource("6teal.png"));
    static final ImageIcon SEVEN_ICON = new ImageIcon(classLoader.getResource("7gray.png"));
    static final ImageIcon EIGHT_ICON = new ImageIcon(classLoader.getResource("8yellow.png"));
    static ImageIcon bombTile;

    public static void main(String[] args) {
        Board.selectDifficulty();
    }

    public static void selectDifficulty() {
        Font diffFont = new Font("Arial", Font.BOLD, 40);
        difficultyFrame = new JFrame("Select Difficulty");
        difficultyFrame.setPreferredSize(new Dimension(400, 400));
        JPanel diffPanel = new JPanel(new GridLayout(3, 0));
        JButton easy = new JButton("Easy");
        easy.setFont(diffFont);
        easy.setFocusable(false);
        easy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setUp(Difficulty.EASY);
            }
        });
        easy.setBackground(new Color(189, 255, 153));
        JButton med = new JButton("Medium");
        med.setFocusable(false);
        med.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setUp(Difficulty.MEDIUM);
            }
        });
        med.setBackground(new Color(255, 252, 153));
        med.setFont(diffFont);
        JButton hard = new JButton("Hard");
        hard.setBackground(new Color(255, 153, 153));
        hard.setFocusable(false);
        hard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setUp(Difficulty.HARD);
            }
        });
        hard.setFont(diffFont);
        diffPanel.add(easy);
        diffPanel.add(med);
        diffPanel.add(hard);
        difficultyFrame.add(diffPanel);
        difficultyFrame.pack();
        difficultyFrame.setVisible(true);
    }

    private static void setUp(Difficulty selection) {
        difficultyFrame.dispose(); //Delete difficulty selection frame
        mainFrame = new JFrame();
        mainFrame.addKeyListener(new FlagListener());
        mainFrame.setTitle("Bombs!"); // Frame title
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false); // User can't resize the frame
        JPanel upper = new JPanel(new GridLayout(0, 5)); //Upper panel with information and buttonMap
        upper.setBorder(new EmptyBorder(0, 5, 0, 0));
        flagCount = 0; //Number of flags currently on the board
        firstClick = true; //Whether or not the next click is the first one
        flagToggle = false; //Toggling tiles to become flags
        tileCount = 0; //Tile number
        clickedCount = 0; //Clicked tile number
        seconds = 0; //How many seconds have passed since first click

        // Upper panel's first component describes the difficulty level
        difficultyText = new JLabel();
        difficultyText.setFont(FONT);
        switch (selection) {
        case EASY:
            rowSize = 10;
            totalBombs = 10;
            difficultyText.setText("Bombs: 10  ");
            break;
        case MEDIUM:
            rowSize = 15;
            totalBombs = 40;
            difficultyText.setText("Bombs: 40  ");
            break;
        case HARD:
            rowSize = 22;
            totalBombs = 99;
            difficultyText.setText("Bombs: 99  ");
            break;
        default:
            break;
        }
        upper.add(difficultyText);

        //Upper second component
        flagText = new JLabel("Flags: " + flagCount); //Displays the number of flags
        flagText.setFont(FONT);
        upper.add(flagText);

        //Upper third component displays how many seconds have passed
        JLabel time = new JLabel("Time: 0");
        time.setFont(FONT);
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                seconds++;
                time.setText("Time: " + seconds);
                if (seconds == 999) revealAll(false);
            }
        });
        upper.add(time);

        // Fourth component allows adding flags to the board
        toggleFlag = new JButton(FLAG_ICON);
        toggleFlag.setEnabled(false);
        toggleFlag.setFocusable(false);
        toggleFlag.setFont(FONT);
        toggleFlag.setBackground(Color.WHITE);
        toggleFlag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleFlag();
            }
        });
        upper.add(toggleFlag);

        //Upper fifth component resets the game
        JButton restart = new JButton("Reset");
        restart.setFocusable(false);
        restart.setFont(FONT);
        restart.setBackground(Color.WHITE);
        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                timer.stop();
                selectDifficulty();
            }
        });
        upper.add(restart);

        // Game tiles
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new GridLayout(rowSize, rowSize));
        tiles = new Tile[rowSize][rowSize];
        // Creating each tile and storing the keys in the 2D array
        for (int x = 0; x < rowSize; x++) {
            for (int y = 0; y < rowSize; y++) {
               tiles[x][y] = new Tile();
            }
        }
        // Creating each button corresponding to each tile
        for (int xX = 0; xX < rowSize; xX++) {
        	for (int yY = 0; yY < rowSize; yY++) {
        	int x = xX;
        	int y = yY;
            tiles[x][y].getButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (flagToggle) {
                        if (tiles[x][y].getButton().getIcon() != null &&
                        	tiles[x][y].getButton().getIcon().equals(FLAG_ICON)) {
                        	tiles[x][y].getButton().setIcon(null);
                            flagCount--;
                            flagText.setText("Flags: " + flagCount);
                        } else {
                        	tiles[x][y].getButton().setIcon(FLAG_ICON);
                            flagCount++;
                            flagText.setText("Flags: " + flagCount);
                        }
                    } else {
                        if (firstClick) {
                            toggleFlag.setEnabled(true);
                            tiles[x][y].setBomb(false);
                            if (x > 0) {
                            	tiles[x - 1][y].setBomb(false);
                                if (y > 0) tiles[x - 1][y - 1].setBomb(false);
                                if (x < rowSize - 1) tiles[x - 1][y + 1].setBomb(false);
                            }
                            if (x < rowSize - 1) {
                            	tiles[x + 1][y].setBomb(false);
                                if (y > 0) tiles[x + 1][y - 1].setBomb(false);
                                if (y < rowSize - 1) tiles[x + 1][y + 1].setBomb(false);
                            }
                            if (y > 0) tiles[x][y - 1].setBomb(false);
                            if (y < rowSize - 1) tiles[x][y + 1].setBomb(false);
                            setBombs();
                            firstClick = false;
                            timer.restart();
                        }
                        explode(x, y);
                    }
                }
            });
            tiles[x][y].getButton().setPreferredSize(new Dimension(35, 35));
            tilePanel.add(tiles[x][y].getButton());
        } }
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(upper, BorderLayout.NORTH);
        panel.add(tilePanel, BorderLayout.SOUTH);
        mainFrame.add(panel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private static void setBombs() {
        int bombs = 0;
        Random rand = new Random();
        int rX, rY;
        while (bombs < totalBombs) {
            rX = rand.nextInt(rowSize - 1);
            rY = rand.nextInt(rowSize - 1);
            if (!tiles[rX][rY].isBomb() && !tiles[rX][rY].cannotBeBomb()) {
            	tiles[rX][rY].setBomb(true);
                bombs++;
                if (rX > 0) {
                	tiles[rX - 1][rY].incrementSurrounded();
                	if (rY > 0) tiles[rX - 1][rY - 1].incrementSurrounded();
                	if (rY < rowSize - 1) tiles[rX - 1][rY + 1].incrementSurrounded();
                } if (rX < rowSize - 1) {
                	tiles[rX + 1][rY].incrementSurrounded();
                	if (rY > 0) tiles[rX + 1][rY - 1].incrementSurrounded();
                	if (rY < rowSize - 1) tiles[rX + 1][rY + 1].incrementSurrounded();;
                }
                if (rY > 0) tiles[rX][rY - 1].incrementSurrounded();;
            	if (rY < rowSize - 1) tiles[rX][rY + 1].incrementSurrounded();
            }
        }
    }

    private static void explode(int x, int y) {
        if (tiles[x][y].getButton().isEnabled()) {
            if (tiles[x][y].getButton().getIcon() != null && tiles[x][y].getButton().getIcon().equals(FLAG_ICON) &&
                !tiles[x][y].isBomb()) {
            	tiles[x][y].getButton().setDisabledIcon(null);
                flagCount--;
                flagText.setText("Flags: " + flagCount);
            }
            clickedCount++;
            tiles[x][y].clicked();
            if (tiles[x][y].isBomb()) revealAll(false);
            else if (clickedCount == tileCount - totalBombs) revealAll(true);
            if (tiles[x][y].getSurrounded() == 0) {
                if (x > 0) {
                    explode(x - 1, y);
                    if (y > 0) explode(x - 1, y - 1);
                    if (y < rowSize - 1) explode(x - 1, y + 1);
                }
                if (x < rowSize - 1) {
                    explode(x + 1, y);
                    if (y > 0) explode(x + 1, y - 1);
                    if (y < rowSize - 1) explode(x + 1, y + 1);
                }
                if (y > 0) explode(x, y - 1);
                if (y < rowSize - 1) explode(x, y + 1);
            }
        }
    }

    private static void revealAll(boolean win) {
        timer.stop();
        toggleFlag.setEnabled(false);
        if (win) {
            difficultyText.setText("You win!");
            difficultyText.setForeground(Color.BLUE);
            bombTile = WIN_ICON;
        } else {
            difficultyText.setText("You lose!");
            difficultyText.setForeground(Color.RED);
            bombTile = BOMB_ICON;
        }
        for (int x = 0; x < rowSize; x++) {
        	for (int y = 0; y < rowSize; y++) {
        		tiles[x][y].getButton().setEnabled(false);
                if (tiles[x][y].isBomb()) tiles[x][y].clicked();
        } }
    }

	public static void toggleFlag() {
		if (!firstClick) {
            if (flagToggle) toggleFlag.setBackground(Color.WHITE); //Adding flags is deactivated
            else toggleFlag.setBackground(Color.CYAN); //Adding flags is activated
            flagToggle = !flagToggle;
		}
	}
}