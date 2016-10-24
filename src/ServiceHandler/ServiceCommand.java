package ServiceHandler;

/**
 * Created by degin on 2016/7/2.
 * 重启命令枚举类，将类和命令传入handleCommand，会对该类进行指定的命令操作。
 */
public enum ServiceCommand {
    NoCommand(0), ReBoot(1), Start(2), Stop(-1);

    ServiceCommand(int command) {
        this.command = command;
    }

    private int command;

    public int getCommand() {
        return command;
    }

    /**
     * 根据指定的command，对commander进行对应的操作
     * @param commander
     * @param command
     */
    public static void handleCommand(Commander commander, ServiceCommand command) {
        switch (command) {
            case ReBoot:
                commander.reboot();
                break;
            case Start:
                commander.start();
                break;
            case Stop:
                commander.stop();
                break;
            default:
                break;
        }
    }

    /*
     *@Date 2016/7/2
     *根据指定值返回一个command,如果没有找到这个值返回NoCommand
     */
    public static ServiceCommand parseFromInt(int command) {
        for (ServiceCommand cmd : ServiceCommand.values()
                ) {
            if (command == cmd.getCommand()) {
                return cmd;
            }
        }
        return ServiceCommand.NoCommand;
    }
}
