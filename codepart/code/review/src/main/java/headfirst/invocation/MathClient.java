package headfirst.invocation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author zhengzebiao
 * @date 2020/2/28 16:26
 */
public class MathClient {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            IRemoteMath remoteMath = (IRemoteMath) registry.lookup("Compute");
            double addResult = remoteMath.add(5.0, 3.0);
            System.out.println("5.0 + 3.0 = " + addResult);
            double subResult = remoteMath.subtract(5.0,3.0);
            System.out.println("5.0 - 3.0 = " + subResult);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}