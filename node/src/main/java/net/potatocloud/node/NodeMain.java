package net.potatocloud.node;

public class NodeMain {

    public static void main(String[] args) {
        final long startupTime = System.currentTimeMillis();
        System.setProperty("nodeStartupTime", String.valueOf(startupTime));
        new Node();
    }

}
