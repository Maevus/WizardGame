import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {

	/**
	 * Main Game class
	 */
	private static final long serialVersionUID = 1L;

	private boolean isRunning = false;
	private Thread thread;
	private Handler handler;
	private Camera camera;
	private SpriteSheet ss;

	private BufferedImage level = null;
	private BufferedImage sprite_sheet = null;
	private BufferedImage floor = null;

	public int ammo = 50;

	public Game() {
		new Window(1000, 563, "Wizardry", this);
		start();
		handler = new Handler();
		camera = new Camera(0, 0);

		this.addKeyListener(new KeyInput(handler));

		BufferedImageLoader loader = new BufferedImageLoader();
		level = loader.loadImage("/level.png");
		sprite_sheet = loader.loadImage("/sprite_sheet.png");
		
		ss = new SpriteSheet(sprite_sheet);
		
		floor = ss.grabImage(4,2, 32, 32);

		this.addMouseListener(new MouseInput(handler, camera, this, ss));
		
		loadLevel(level);

	}

	public void run() {
		// Game loop. Cycles thru and updates game 60 times p/s (60fps)
		System.out.println("Game::run() starting...");
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + delta); // Prints run info to console.
				frames = 0;

			}
		}
		stop();

	}

	public void tick() {
		for (int i = 0; i < handler.object.size(); i++) {
			if (handler.object.get(i).getId() == ID.Player) {
				camera.tick(handler.object.get(i));
			}
		}

		handler.tick();
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;

		g2d.translate(-camera.getX(), -camera.getY());

		for (int i = 0; i < 30*72; i += 32) {
			for (int j = 0; j < 30*72; j += 32) {
				g.drawImage(floor, i, j, null);
			}
		}
		
		handler.render(g);

		g2d.translate(camera.getX(), camera.getY());

		g.dispose();
		bs.show();
	}

	// loading the level
	private void loadLevel(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int pixel = image.getRGB(i, j);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				if (red == 255)
					handler.addObject(new Block(i * 32, j * 32, ID.Block, ss));

				if (blue == 255 && green == 0)
					handler.addObject(new Wizard(i * 32, j * 32, ID.Player, handler, this, ss));

				if (green == 255 && blue == 0)
					handler.addObject(new Enemy(i * 32, j * 32, ID.Enemy, handler, ss));

				if (green == 255 && blue == 255 && red == 0) {
					handler.addObject(new Crate(i * 32, j * 32, ID.Crate, ss));
				}
			}
		}

	}

	private void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void start() {
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	public static void main(String args[]) {
		new Game();
	}
}
