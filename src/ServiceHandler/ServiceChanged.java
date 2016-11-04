package ServiceHandler;

import java.sql.SQLException;
import java.util.Hashtable;

import Database.DatabaseHandler;
import Database.Where;
import Service.Service;
import ErrorLog.ErrorLog;

/**
 * Created by degin on 2016/7/2.
 */
public enum ServiceChanged {
    NoChange(0), Added(1), Modified(2), Deleted(-1);

    public int getChanged() {
        return changed;
    }

    private int changed;

    ServiceChanged(int changed) {
        this.changed = changed;
    }

    /**
     * 根据单个服务的changed，对服务进行处理。
     * @param hashtable
     * @param service
     */
    public static void handlerChanged(DatabaseHandler handler, Hashtable<Integer, Service> hashtable, Service service) {
        ServiceChanged changed = parseFromInt(service.getChanged());
        Service oldService = null;
        try {
            switch (changed) {
                case Added:
                    oldService = hashtable.put(service.getId(), service);
                    if (oldService != null) {
                        oldService.serviceStop();
                        handler.update(oldService);
                    }
                    service.serviceStart();
                    service.setChanged(ServiceChanged.NoChange.getChanged());
                    handler.update(service);
                    break;
                case Modified:
                    oldService = hashtable.put(service.getId(), service);
                    if (oldService != null) {
                        oldService.serviceStop();
                        handler.update(oldService);
                    }
                    service.serviceStart();
                    service.setChanged(ServiceChanged.NoChange.getChanged());
                    handler.update(service);
                    break;
                case Deleted:
                    oldService = hashtable.remove(service.getId());
                    if (oldService != null) {
                        oldService.serviceStop();
                        oldService.setChanged(ServiceChanged.Deleted.getChanged());
                        handler.update(oldService);
                    }else {
                        service.setState(ServiceState.Sleep.getState());
                        handler.update(service);
                    }
                    break;
                default:
                    break;
            }
        }catch (SQLException e) {
            ErrorLog.writeLog(e);
        }
    }

    public static ServiceChanged parseFromInt(int change) {
        for (ServiceChanged changed : ServiceChanged.values()
                ) {
            if (changed.getChanged() == change) {
                return changed;
            }
        }
        return NoChange;
    }


}
