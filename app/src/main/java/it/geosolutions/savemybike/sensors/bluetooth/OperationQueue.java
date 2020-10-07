package it.geosolutions.savemybike.sensors.bluetooth;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A queue for characterstics read and write operations that helps to avoid race conditions.
 */
public class OperationQueue {

    private Queue<Runnable> operations;

    private boolean queueBusy;

    public OperationQueue(){
        this.operations=new ArrayDeque<>();
    }

    /**
     * Execute the first runnable in the queue
     */
    private void executeOperation(){
        if(!operations.isEmpty()) {
            operations.peek().run();
            queueBusy = true;
        }
    }

    /**
     * Unlock the queue and allows to run the next runnable.
     * This method is meant to be invoked in the onCharacteristicWrite and onCharacteristicRead
     * of the GattCallback.
     */
    public void completeOperation() {
        if (!queueBusy) {
            operations.poll();
            queueBusy = false;
        }
    }

    public boolean isQueueBusy() {
        return queueBusy;
    }

    /**
     * Run the first operation in the queue
     */
    public void nextOperation() {
        if(!isQueueBusy()) executeOperation();
    }

    public void addCommand(Runnable command){
        this.operations.add(command);
    }

    public boolean isQueueEmpty (){
        return operations.isEmpty();
    }
}
