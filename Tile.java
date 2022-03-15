import java.util.ArrayList;

import javax.swing.JButton;

public class Tile {

    boolean isBomb, cannotBeBomb;
    int surrounded;
    JButton button;

    ArrayList < Tile > neighbors;

    public Tile() {
        isBomb = false;
        cannotBeBomb = false;
        surrounded = 0;
        button = new JButton();
        button.setBackground(Board.DEFAULT_COLOR);
        button.setFocusable(false);
    }

    public void setBomb(boolean indicator) {
        if (indicator) {
        	isBomb = true;
        	surrounded = 999;
        }
        else cannotBeBomb = true;
    }
    
    public void clicked() {
    	if (!isBomb()) button.setBackground(Board.DEACTIVATED_COLOR);
    	switch (surrounded) {
        case 1:
            button.setIcon(Board.ONE_ICON);
            button.setDisabledIcon(Board.ONE_ICON);
            break;
        case 2:
            button.setIcon(Board.TWO_ICON);
            button.setDisabledIcon(Board.TWO_ICON);
            break;
        case 3:
            button.setIcon(Board.THREE_ICON);
            button.setDisabledIcon(Board.THREE_ICON);
            break;
        case 4:
            button.setIcon(Board.FOUR_ICON);
            button.setDisabledIcon(Board.FOUR_ICON);
            break;
        case 5:
            button.setIcon(Board.FIVE_ICON);
            button.setDisabledIcon(Board.FIVE_ICON);
            break;
        case 6:
            button.setIcon(Board.SIX_ICON);
            button.setDisabledIcon(Board.SIX_ICON);
            break;
        case 7:
            button.setIcon(Board.SEVEN_ICON);
            button.setDisabledIcon(Board.SEVEN_ICON);
            break;
        case 8:
            button.setIcon(Board.EIGHT_ICON);
            button.setDisabledIcon(Board.EIGHT_ICON);
            break;
    	case 999:
            button.setIcon(Board.bombTile);
            button.setDisabledIcon(Board.bombTile);
    		break;
    	default:
    		break;
    	}
    	button.setEnabled(false);
    }

    public boolean cannotBeBomb() {
        return cannotBeBomb;
    }
    
    public void incrementSurrounded() {
    	if (!isBomb) surrounded++;
    }

    public int getSurrounded() {
        return surrounded;
    }

    public boolean isBomb() {
        return isBomb;
    }
    
    public JButton getButton() {
    	return button;
    }
}