package com.carrotgarden.eclipse.space.console;

public class RunnerConsoleDocument {
  public static final int COMMAND = 0; // command text

  public static final int MESSAGE = 1; // message received

  public static final int ERROR = 2; // error received

  public static final int STATUS = 3; // status text

  public static final int DELIMITER = 4; // delimiter text between runs

  private int[] lineTypes;

  private String[] lines;

  private int writeIndex = 0;

  private int readIndex = 0;

  private static final int BUFFER_SIZE = 200;

  protected static class ConsoleLine {
    public String line;

    public int type;

    ConsoleLine(final String line, final int type) {
      this.line = line;
      this.type = type;
    }
  }

  public RunnerConsoleDocument() {
  }

  public void clear() {
    lineTypes = null;
    lines = null;
    writeIndex = 0;
    readIndex = 0;
  }

  public void appendConsoleLine(final int type, final String line) {
    if(lines == null) {
      lines = new String[BUFFER_SIZE];
      lineTypes = new int[BUFFER_SIZE];
    }
    lines[writeIndex] = line;
    lineTypes[writeIndex] = type;

    if(++writeIndex >= BUFFER_SIZE) {
      writeIndex = 0;
    }
    if(writeIndex == readIndex) {
      if(++readIndex >= BUFFER_SIZE) {
        readIndex = 0;
      }
    }
  }

  public ConsoleLine[] getLines() {
    if(isEmpty()) {
		return new ConsoleLine[0];
	}
    final ConsoleLine[] docLines = new ConsoleLine[readIndex > writeIndex ? BUFFER_SIZE : writeIndex];
    int index = readIndex;
    for(int i = 0; i < docLines.length; i++ ) {
      docLines[i] = new ConsoleLine(lines[index], lineTypes[index]);
      if(++index >= BUFFER_SIZE) {
        index = 0;
      }
    }
    return docLines;
  }

  public boolean isEmpty() {
    return writeIndex == readIndex;
  }
}
