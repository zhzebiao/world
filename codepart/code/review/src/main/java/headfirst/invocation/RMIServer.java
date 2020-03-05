package headfirst.invocation;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author zhengzebiao
 * @date 2020/2/28 16:22
 */
public class RMIServer {

    public static void main(String[] args) {
        try{
            // 注册远程对象,向客户端提供远程对象服务。
            // 远程对象是在远程服务上创建的，你无法确切地知道远程服务器上的对象的名称，
            // 但是,将远程对象注册到RMI Registry之后,
            // 客户端就可以通过RMI Registry请求到该远程服务对象的stub，
            // 利用stub代理就可以访问远程服务对象了。
            IRemoteMath remoteMath = new RemoteMath();
            LocateRegistry.createRegistry(1099);
            Naming.bind("rmi://127.0.0.1:1099/RMIServerInterface",remoteMath);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Compute",remoteMath);
            System.out.println("Math server ready");
            // 如果不想再让该对象被继续调用，使用下面一行
            // UnicastRemoteObject.unexportObject(remoteMath, false);
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}