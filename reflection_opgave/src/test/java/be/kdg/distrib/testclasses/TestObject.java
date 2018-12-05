package be.kdg.distrib.testclasses;

public class TestObject {
    private String string;
    private int integer;
    private char character;
    private boolean bool;

    public TestObject() {
    }

    public TestObject(String string, int integer, char character, boolean bool) {
        this.string = string;
        this.integer = integer;
        this.character = character;
        this.bool = bool;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
