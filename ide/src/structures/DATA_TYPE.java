package structures;

import java.util.Arrays;

public enum DATA_TYPE {
   INT(0),
   FLO(1),
   CHA(2),
   STR(3),
   BOO(4),

   UND(5);

    DATA_TYPE(int val) {
        this.val = val;
    }

    private int val;

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public static DATA_TYPE getEnum(int value) {
        return Arrays.stream(DATA_TYPE.values()).filter(dataType -> dataType.val == value).toList().get(0);
    }

}
