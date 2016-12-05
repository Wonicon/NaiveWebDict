package Communication;

import java.io.DataInputStream;
import java.net.Socket;

public abstract class Task {
  abstract void handle(int taskID, Socket conn, DataInputStream in);
}
