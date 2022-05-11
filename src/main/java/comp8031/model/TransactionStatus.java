package comp8031.model;

public enum TransactionStatus {
    TransactionBegin("Transaction Begin"),
    TransactionEnd("Transaction End"),
    TransactionComlete("Transaction Complete"),
    DeviceDisconnected("Device Disconnected");
    private String name;
    TransactionStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
