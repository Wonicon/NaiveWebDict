package Communication;

import java.net.*;
import java.io.*;

public abstract class Task {
  abstract void handle(int taskID, Socket conn, DataInputStream in);
}
