import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Block extends GameObject {

	public Block(int x, int y, ID id, SpriteSheet ss) {
		super(x, y, id, ss);
	}

	public void tick() {

	}

	public void render(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(x, y, 32, 32);

	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 32, 32);
	}

}
