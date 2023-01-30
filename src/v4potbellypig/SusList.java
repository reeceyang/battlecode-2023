package v4potbellypig;

public class SusList {
    public final int LENGTH;

    public final Message[] list;
    private final int OUTDATED_TURNS_AMOUNT;
    private int size = 0;

    public SusList(int outdatedTurnsAmount, int length) {
        this.LENGTH = length;
        this.OUTDATED_TURNS_AMOUNT = outdatedTurnsAmount;
        this.list = new Message[length];
    }

    public int size() {
        return this.size;
    }

    public void clearOutdated() {
        for (int i = 0; i < LENGTH; i++) {
            Message msg = list[i];
            if (msg != null && msg.turnAdded + OUTDATED_TURNS_AMOUNT < RobotPlayer.turnCount) {
                list[i] = null;
                size--;
            }
        }
    }

    public void add(Message m) {
        for (int i = 0; i < LENGTH; i++) {
            Message msg = list[i];
            if (msg == null || msg.turnAdded + OUTDATED_TURNS_AMOUNT < RobotPlayer.turnCount) {
                list[i] = m;
                return;
            }
        }
        System.out.println("WARNING: failed to write message " + m + " because queue full");
    }

    public void addMany(Message[] ms) {
        int ms_i = 0;
        for (int i = 0; i < LENGTH; i++) {
            if (ms_i >= ms.length) {
                return;
            }
            Message msg = list[i];
            if (msg == null || msg.turnAdded + OUTDATED_TURNS_AMOUNT < RobotPlayer.turnCount) {
                list[i] = ms[ms_i];
                ms_i += 1;
            }
        }
        System.out.println("WARNING: failed to write " + (ms.length - ms_i) + " messages because queue full");
    }
}
