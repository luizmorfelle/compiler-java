package gals;

import static structures.DATA_TYPE.BOO;
import static structures.DATA_TYPE.FLO;
import static structures.DATA_TYPE.INT;
import static structures.DATA_TYPE.STR;
import static structures.DATA_TYPE.CHA;

public class SemanticTable {

    public static final int ERR = -1;
    public static final int OK_ = 0;
    public static final int WAR = 1;

    public static final int SUM = 0;
    public static final int SUB = 1;
    public static final int MUL = 2;
    public static final int DIV = 3;
    public static final int REL = 4; // qualquer operador relacional

    // TIPO DE RETORNO DAS EXPRESSOES ENTRE TIPOS
    // 5 x 5 X 5  = TIPO X TIPO X OPER
    static int[][][] expTable =
            {/*       INT       */ /*       FLOAT     */ /*      CHAR       */ /*      STRING     */ /*     BOOL        */
                    /*   INT*/ {{INT.getVal(), INT.getVal(), INT.getVal(), FLO.getVal(), BOO.getVal()}, {FLO.getVal(), FLO.getVal(), FLO.getVal(), FLO.getVal(), BOO.getVal()}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}},
                    /* FLOAT*/ {{FLO.getVal(), FLO.getVal(), FLO.getVal(), FLO.getVal(), BOO.getVal()}, {FLO.getVal(), FLO.getVal(), FLO.getVal(), FLO.getVal(), BOO.getVal()}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}},
                    /*  CHAR*/ {{INT.getVal(), INT.getVal(), INT.getVal(), FLO.getVal(), BOO.getVal()}, {FLO.getVal(), FLO.getVal(), FLO.getVal(), FLO.getVal(), BOO.getVal()}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}},
                    /*STRING*/ {{STR.getVal(), ERR, ERR, ERR, ERR}, {STR.getVal(), ERR, ERR, ERR, ERR}, {STR.getVal(), ERR, ERR, ERR, BOO.getVal()}, {STR.getVal(), ERR, ERR, ERR, BOO.getVal()}, {STR.getVal(), ERR, ERR, ERR, ERR}},
                    /*  BOO L*/ {{INT.getVal(), INT.getVal(), INT.getVal(), FLO.getVal(), BOO.getVal()}, {FLO.getVal(), FLO.getVal(), FLO.getVal(), FLO.getVal(), BOO.getVal()}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}, {ERR, ERR, ERR, ERR, ERR}}
            };

    // atribuicoes compativeis
    // 5 x 5 = TIPO X TIPO
    static int[][] atribTable = {/* INT FLO CHA STR BOO  */
            /*INT*/ {OK_, WAR, WAR, ERR, WAR},
            /*FLO*/ {OK_, OK_, ERR, ERR, ERR},
            /*CHA*/ {OK_, ERR, OK_, ERR, OK_},
            /*STR*/ {ERR, ERR, ERR, OK_, ERR},
            /*BOO*/ {OK_, ERR, WAR, WAR, OK_}
    };

    static int resultType(int TP1, int TP2, int OP) {
        return (expTable[TP1][TP2][OP]);
    }

    static int atribType(int TP1, int TP2) {
        return (atribTable[TP1][TP2]);
    }
}
