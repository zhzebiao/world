package headfirst.invocation;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author zhengzebiao
 * @date 2020/2/28 16:17
 */
public interface IRemoteMath extends Remote {

    public double add(double a, double b) throws RemoteException;

    public double subtract(double a, double b) throws RemoteException;
}
