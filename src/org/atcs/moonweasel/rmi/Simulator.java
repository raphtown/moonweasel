package org.atcs.moonweasel.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Simulator extends Remote {
    int doStuff() throws RemoteException;
}