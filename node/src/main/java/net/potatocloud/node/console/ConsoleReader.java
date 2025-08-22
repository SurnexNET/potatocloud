package net.potatocloud.node.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.setup.Setup;
import org.jline.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

@RequiredArgsConstructor
public class ConsoleReader extends Thread {

    private final Console console;
    private final CommandManager commandManager;
    private final Node node;

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                final String input = console.getLineReader().readLine(console.getPrompt());

                final Screen currentScreen = Node.getInstance().getScreenManager().getCurrentScreen();
                final boolean isNodeScreen = currentScreen.getName().equals(Screen.NODE_SCREEN);

                if (isNodeScreen && input.isBlank()) {
                    // remove blank inputs
                    console.println(Ansi.ansi().cursorUpLine().eraseLine().cursorUp(1).toString());
                    continue;
                }

                if (isNodeScreen) {
                    // add executed commands into log file
                    node.getLogger().logCommand(input);

                    commandManager.executeCommand(input);
                    continue;
                }

                // the user is in a setup currently
                if (currentScreen.getName().contains("setup")) {
                    final Setup currentSetup = Node.getInstance().getSetupManager().getCurrentSetup();
                    if (currentSetup != null) {
                        currentSetup.handleInput(input);
                    }
                    continue;
                }

                if (input.equalsIgnoreCase("leave") || input.equalsIgnoreCase("exit")) {
                    Node.getInstance().getScreenManager().switchScreen(Screen.NODE_SCREEN);
                    continue;
                }

                final Service service = node.getServiceManager().getService(currentScreen.getName());
                if (service == null) {
                    continue;
                }

                service.executeCommand(input);
            }
        } catch (UserInterruptException e) {
            node.shutdown();
        } catch (EndOfFileException e) {
            console.clearScreen();
            console.updateScreen();
        }
    }
}