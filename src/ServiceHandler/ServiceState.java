package ServiceHandler;

/**
 * Created by degin on 2016/7/2.
 */
public enum ServiceState {
    Sleep(0),Running(1),Rebooting(2);

    ServiceState(int state){
        this.state=state;
    }

    public int getState() {
        return state;
    }

    private int state;
}
