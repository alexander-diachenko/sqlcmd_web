package ua.com.juja.positiv.sqlcmd.command.secondary;

import ua.com.juja.positiv.sqlcmd.command.Command;
import ua.com.juja.positiv.sqlcmd.command.NullFormat;
import ua.com.juja.positiv.sqlcmd.view.View;

public class Unsupported extends NullFormat implements Command {

    private View view;

    public Unsupported(View view) {
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return true;
    }

    @Override
    public void process(String command) {
        view.write(String.format("Команды '%s' не существует.", command));
    }
}