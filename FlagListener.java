import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FlagListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_SHIFT) Board.toggleFlag();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
