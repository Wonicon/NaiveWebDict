package Communication;

import java.net.*;
import java.io.*;

public abstract class Task {
  abstract void handle(Socket conn, DataInputStream in);
}
