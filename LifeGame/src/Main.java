import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {

		Window w = new Window();
		w.display();

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					w.doLife();
					w.display();

				}
			}
		}).start();
	}
}
