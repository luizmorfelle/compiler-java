package gals;

import structures.DATA_TYPE;
import structures.Symbol;

import java.util.*;

import static structures.Symbol.IdType.*;

public class Semantico implements Constants {
    private Stack<Map<String, Symbol>> scopeToSymbols = new Stack();

    private Integer scopeCounter = 0;
    private Integer tagCounter = 0;
    private Stack<String> tagsQueue =  new Stack<>();
    private String currentDeclarationType;
    private String currentDeclarationSymbolId;
    private Object currentDeclarationValue;
    private Boolean currentDeclarationIsArray = false;
    private List<Object> currentDeclarationArrayValues = new ArrayList<>();

    public Queue<String> warnings = new LinkedList<>();
    private boolean isAddingArray = false;
    private Integer currentDeclarationArraySize = 0;
    private List<Symbol> unusedSymbols = new ArrayList<>();

    public List<Symbol> symbolsTable = new ArrayList<>();
    public List<String> codesData = new ArrayList<>();
    public List<String> codesText = new ArrayList<>();

    private Object firstValue;
    private Object secondValue;

    private String operation;
    private String relationalOperation;

    private DATA_TYPE resultDataType;
    private Symbol accessedSymbol;
    private Symbol atribuitionSymbol;

    private boolean isReading = false;
    private boolean isPrinting = false;
    private boolean inOperation = false;

    private Symbol accessingArray = null;

    private boolean isAccessingArray = false;

    private Integer currentArrayIndex = null;

    private Object temp0 = null;
    private Object tempOperation = null;

    private List<Object> arrayValues = new ArrayList<>();


    public Semantico() {
    }


    public void executeAction(int action, Token token) throws Exception {
        System.out.println("Ação #" + action + ", Token: " + token);

        switch (action) {
            case 0 -> currentDeclarationType = token.getLexeme();
            case 1 -> currentDeclarationSymbolId = token.getLexeme();
            case 2 -> {
                Symbol symbol = findSymbol(token);
                if (symbol == null) {
                    currentDeclarationValue = token.getLexeme();
                } else {
                    currentDeclarationValue = symbol;
                }
            }
            case 3 -> currentDeclarationIsArray = true;
            case 4 -> currentDeclarationArraySize = Integer.parseInt(token.getLexeme());
            case 5 -> currentDeclarationArrayValues.add(token.getLexeme());
            case 6 -> addVariableToScope();
            case 7 -> {
                Symbol symbol = findSymbol(token);
                if (accessingArray != null && token.getLexeme().equals("]")) {
                    codesText.add("STO $indr");
                    codesText.add("LDV " + accessingArray.getId());
                    return;
                }
                if (accessingArray != null && inOperation) {
                    codesText.add("STO 1000");
                }
                if (symbol == null) {
                    firstValue = token.getLexeme();
                    codesText.add("LDI " + token.getLexeme());
                } else {
                    firstValue = symbol;
                    codesText.add("LD " + token.getLexeme());
                }
            }
            case 8 -> {
                Symbol symbol = findSymbol(token);
                String operationBipCode = getOperationBipCode(operation);
                if (accessingArray != null && token.getLexeme().equals("]") && inOperation) {
                    codesText.add("STO $indr");
                    codesText.add("LDV " + accessingArray.getId());
                    codesText.add("STO 1001");
                    codesText.add("LD 1000");
                    codesText.add(operationBipCode + " 1001");
                    return;
                }
                if (symbol == null) {
                    secondValue = token.getLexeme();
                    codesText.add(operationBipCode + "I " + token.getLexeme());
                } else {
                    secondValue = symbol;
                    codesText.add(operationBipCode + " " + token.getLexeme());
                }
            }
            case 9 -> {
                operation = token.getLexeme();
                inOperation = true;
            }
            case 10 -> accessedSymbol = null;
            case 11 -> {
                accessingArray = findSymbol(token);
                isAccessingArray = true;
            }
            case 12 -> {
                accessedSymbol = getSymbolIfDeclaredInProgramOrThrow(token.getLexeme());
            }
            case 13 -> {
                codesText.add("STO " + accessedSymbol.getId());
            }
            case 14 -> {
                accessingArray = getSymbolIfDeclaredInProgramOrThrow(token.getLexeme());
            }
            case 15 -> {
                codesText.add("STO 1000");

            }
            case 16 -> {
                codesText.add("STO 1001");
                codesText.add("LD 1000");
                codesText.add("STO $indr");
                codesText.add("LD 1001");
                codesText.add("STOV " + accessingArray.getId());
            }
            case 17 -> {
                isReading = true;
            }
            case 18 -> {
                codesText.add("LD $in_port");
                codesText.add("STO " + token.getLexeme());
            }
            case 19 -> {
                codesText.add("STO $out_port");
            }
            case 20 -> {
                codesText.add("STO 1000");
                relationalOperation = token.getLexeme();
            }
            case 21 -> {
                codesText.add("STO 1001");
                codesText.add("LD 1000");
                codesText.add("SUB 1001");
            }
            case 22 -> {
                tagCounter += 1;
                String newTag = "R" + tagCounter;
                tagsQueue.push(newTag);
                codesText.add(getReverseCode() + " " + newTag);
            }
            case 23, 25 -> {
                codesText.add(tagsQueue.pop() + ":");
            }
            case 24 -> {
                String tagIf = tagsQueue.pop();
                tagCounter += 1;
                String newTag = "R" + tagCounter;
                codesText.add("JMP " + newTag);
                codesText.add(tagIf + ":");
                tagsQueue.push(newTag);
            }
            case 26 -> {
                tagCounter += 1;
                String newTag = "R" + tagCounter;
                tagsQueue.push(newTag);
                codesText.add(newTag);
            }
            case 27 -> {
                String endTag = tagsQueue.pop();
                String startTag = tagsQueue.pop();
                codesText.add("JMP " + startTag);
                codesText.add(endTag);
            }
            case 28 -> {
                String code = "";
                switch (relationalOperation) {
                    case ">" -> code = "BGT";
                    case ">=" -> code = "BGE";
                    case "<" -> code = "BLT";
                    case "<=" -> code = "BLE";
                    case "==" -> code = "BEQ";
                    case "!=" -> code = "BNE";
                }
                codesText.add(code + " " + tagsQueue.pop());
            }
            case 29 -> {

            }
            case 30 -> {
                codesText.add("LD " + token.getLexeme());
                codesText.add("ADDI 1");
                codesText.add("STO " + token.getLexeme());
            }
            case 31 -> {
                codesText.add("LD " + token.getLexeme());
                codesText.add("SUBI 1");
                codesText.add("STO " + token.getLexeme());
            }
        }
    }

    private String getReverseCode() {
        String reverseCode = "";
        switch (relationalOperation) {
            case ">" -> reverseCode = "BLE";
            case ">=" -> reverseCode = "BLT";
            case "<" -> reverseCode = "BGE";
            case "<=" -> reverseCode = "BGT";
            case "==" -> reverseCode = "BNE";
            case "!=" -> reverseCode = "BEQ";
        }
        return reverseCode;
    }

    private Symbol findSymbol(Token token) {
        return symbolsTable.stream().filter(it -> it.getId().equals(token.getLexeme())).findFirst().orElse(null);
    }

    private void verifyOperationValues(Token token) throws Exception {
        if (firstValue == null) {
//            if (token.getLexeme().equals("]")) {
//                firstValue = accessingArray;
//            } else {
            firstValue = token.getLexeme();
//            }

        } else {
            if (operation == null) {
                if (isPrinting) {
                    if (firstValue.getClass().equals(Symbol.class)) {
                        codesText.add((((Symbol) firstValue).getIdType().equals(ARRAY) ? "LDV " : "LD ") + ((Symbol) firstValue).getId());
                    } else {
                        codesText.add("LDI " + firstValue);
                    }
                    codesText.add("STO $out_port");
                    isPrinting = false;
                }
                return;
            }
            if (secondValue == null) {
                secondValue = token.getLexeme();
            }

            if (secondValue == null) {
                return;
            }

            Symbol firstValueSymbol = firstValue.getClass().equals(Symbol.class) ? (Symbol) firstValue : null;
            Symbol secondValueSymbol = secondValue.getClass().equals(Symbol.class) ? (Symbol) secondValue : null;

            if (firstValue.getClass().equals(Symbol.class) && ((Symbol) firstValue).getIdType() == ARRAY) {
                firstValue = ((Symbol) firstValue).getValues().get(currentArrayIndex == null ? 0 : currentArrayIndex);
            }

            if (secondValue.getClass().equals(Symbol.class) && ((Symbol) secondValue).getIdType() == ARRAY) {
                secondValue = ((Symbol) secondValue).getValues().get(currentArrayIndex == null ? 0 : currentArrayIndex);
            }

//            int operationValue = getOperationValue();
//            DATA_TYPE firstValueType = getValueType(firstValue.getClass().equals(Symbol.class) ? ((Symbol) firstValue).getValue().toString() : firstValue.toString());
//            DATA_TYPE secValueType = getValueType(secondValue.getClass().equals(Symbol.class) ? ((Symbol) secondValue).getValue().toString() : secondValue.toString());
//
//            int result = firstValueType == DATA_TYPE.UND || secValueType == DATA_TYPE.UND ? SemanticTable.ERR : SemanticTable.resultType(firstValueType.getVal(), secValueType.getVal(), operationValue);
//
//            if (result == SemanticTable.ERR) {
//                throw new SemanticError("Operation error: Invalid operation " + firstValueType.toString() + " " + operation + " " + secValueType.toString());
//            }
//
//            resultDataType = DATA_TYPE.getEnum(result);

            boolean store01 = false;
            boolean store02 = false;

            String operatorCode = inOperation ? getOperationBipCode(operation) : "LD";
            operatorCode += firstValueSymbol != null && firstValueSymbol.getIdType().equals(ARRAY) ? "V" : "";

            if (!inOperation) {
                if (firstValueSymbol != null) {
                    codesText.add(operatorCode + " " + firstValueSymbol.getId());
                } else {
                    codesText.add(operatorCode + "I " + firstValue);
                }
                if (secondValueSymbol != null && secondValueSymbol.getIdType().equals(ARRAY)) {
                    codesText.add("STO 1001");
                }
            }

            operatorCode = getOperationBipCode(operation);

            if (secondValueSymbol != null) {
                if (secondValueSymbol.getIdType().equals(ARRAY)) {
                    codesText.add("LDI " + currentArrayIndex);
                    codesText.add("STO $indr");
                    codesText.add("LDV " + secondValueSymbol.getId());
                    codesText.add("STO 1002");
                    codesText.add("LD 1001");
                    codesText.add(operatorCode + " 1002");

                } else {
                    codesText.add(operatorCode + " " + secondValueSymbol.getId());
                }

            } else {
                codesText.add(operatorCode + "I " + secondValue);
            }

            inOperation = true;
            secondValue = null;
            operation = null;

        }

    }

    String getOperationBipCode(String operation) {
        return switch (operation) {
            case "+" -> "ADD";
            case "-" -> "SUB";
            case "|" -> "OR";
            case "^" -> "XOR";
            default -> "";
        };
    }


    int getOperationValue() {
        return switch (operation) {
            case "+" -> SemanticTable.SUM;
            case "-" -> SemanticTable.SUB;
            case "*" -> SemanticTable.MUL;
            case "/" -> SemanticTable.DIV;
            default -> SemanticTable.REL;
        };
    }

    DATA_TYPE getValueType(String value) {
        try {
            Integer.parseInt(value);
            return DATA_TYPE.INT;
        } catch (Exception e) {
            try {
                Double.parseDouble(value);
                return DATA_TYPE.FLO;
            } catch (Exception e2) {
                if (value.equals("true") || value.equals("false")) {
                    return DATA_TYPE.BOO;
                } else {
                    return value.contains("'") ? DATA_TYPE.CHA : value.contains("\"") ? DATA_TYPE.STR : DATA_TYPE.UND;
                }
            }

        }
    }

    Symbol findSymbolInCurrentScope(String symbolId) {
        return findSymbolInScope(symbolId, scopeToSymbols.size() - 1);
    }

    Symbol findSymbolInScope(String symbolId, Integer scopeIndex) {
        Map<String, Symbol> currentScope = scopeToSymbols.get(scopeIndex);
        return currentScope != null ? currentScope.get(symbolId) : null;
    }

    Symbol findSymbolInProgram(String symbolId) {
        for (int i = scopeToSymbols.size() - 1; i >= 0; i--) {
            Symbol foundSymbol = findSymbolInScope(symbolId, i);
            if (foundSymbol != null) return foundSymbol;
        }
        return null;
    }

    boolean isDeclaredInCurrentScope(String checkId) {
        Symbol foundSymbol = findSymbolInCurrentScope(checkId);
        return foundSymbol != null;
    }

    boolean isDeclaredInProgram(String checkId) {
        Symbol foundSymbol = findSymbolInProgram(checkId);
        return foundSymbol != null;
    }

    boolean isInitializedInScope(String checkId) {
        Symbol foundSymbol = findSymbolInCurrentScope(checkId);
        return foundSymbol != null && foundSymbol.isInitialized();
    }

    void ensureSymbolIsNotDeclaredInScopeOrThrow() throws Exception {
        if (isDeclaredInCurrentScope(currentDeclarationSymbolId))
            throw new SemanticError(currentDeclarationSymbolId + " is already declared in scope;");
    }

    void ensureSymbolIsInitializedOrWarn(Symbol symbol) {
        if (!isInitializedInScope(symbol.getId()))
            addNewWarning(symbol.getIdType() + " '" + symbol.getId() + "' is being used while not initialized;");
    }

    Symbol getSymbolIfDeclaredInProgramOrThrow(String symbolId) throws Exception {
        Symbol foundSymbol = symbolsTable.stream().filter(it -> it.getId().equals(symbolId) && Objects.equals(it.getScope(), scopeCounter)).findFirst().orElse(null);
        if (foundSymbol == null) throw new SemanticError(symbolId + " is being used without being declared;");
        return foundSymbol;
    }

    void markSymbolAsUsed(Symbol symbol) {
        if (!symbol.isUsed()) scopeToSymbols.get(symbol.getScope() - 1).get(symbol.getId()).setUsed(true);
    }

    void handleSymbolAccess(Symbol accessedSymbol) {
        markSymbolAsUsed(accessedSymbol);
        ensureSymbolIsInitializedOrWarn(accessedSymbol);

        switch (accessedSymbol.getIdType()) {
            case VARIABLE:

                break;
            case ARRAY:

                break;
            case FUNCTION:

                break;
            case PARAM:

                break;
        }
    }

    void addVariableToScope() {
        Symbol newSymbol = new Symbol(
                currentDeclarationSymbolId,
                currentDeclarationType,
                scopeCounter,
                currentDeclarationValue != null || !currentDeclarationArrayValues.isEmpty(),
                currentDeclarationIsArray ? ARRAY : VARIABLE,
                currentDeclarationArrayValues.isEmpty() ? currentDeclarationValue : null,
                DATA_TYPE.INT,
                currentDeclarationArraySize == null || currentDeclarationArraySize == 0 ? currentDeclarationArrayValues.size() : currentDeclarationArraySize,
                currentDeclarationArrayValues
        );
        symbolsTable.add(newSymbol);

        if (newSymbol.isInitialized() && !newSymbol.getIdType().equals(ARRAY)) {
            codesText.add("STO " + newSymbol.getId());
        }

        resetDeclarationFlags();
    }

    void addVariableToScope(Token token, boolean isInitialized, Symbol.IdType idType) throws Exception {
        Object symbolValue = isAddingArray ? null : isInitialized ? token.getLexeme() : null;

        if (arrayValues.size() > currentDeclarationArraySize && currentDeclarationArraySize != 0) {
            throw new SemanticError("The number of array elements exceeds the specified value.");
        }

        Symbol newSymbol = new Symbol(
                currentDeclarationSymbolId,
                idType == PARAM ? "let" : currentDeclarationType,
                scopeCounter,
                isInitialized,
                idType,
                symbolValue,
                isAddingArray && !arrayValues.isEmpty() ? getValueType(arrayValues.get(0).toString()) : isInitialized ? getValueType(token.getLexeme()) : DATA_TYPE.UND,
                isAddingArray ? currentDeclarationArraySize == 0 ? arrayValues.size() : currentDeclarationArraySize : 0,
                isAddingArray ? arrayValues : null
        );

        if (isInitialized && !inOperation && !idType.equals(ARRAY) && accessingArray == null) {
            codesText.add("LDI " + symbolValue);
            codesText.add("STO " + currentDeclarationSymbolId);
        } else if (isInitialized && !inOperation && !idType.equals(ARRAY)) {
            codesText.add("LDV " + ((Symbol) accessingArray).getId());
            codesText.add("STO " + currentDeclarationSymbolId);
        } else if (inOperation) {
            codesText.add("STO " + currentDeclarationSymbolId);
        }
        inOperation = false;

        scopeToSymbols.peek().put(currentDeclarationSymbolId, newSymbol);
        symbolsTable.add(newSymbol);
        resetDeclarationFlags();
        firstValue = null;
        secondValue = null;
        operation = null;
    }

    void resetDeclarationFlags() {
        currentDeclarationValue = null;
        currentDeclarationArrayValues = new ArrayList<>();
        currentDeclarationArraySize = null;
        currentDeclarationIsArray = false;
        currentDeclarationType = null;
        currentDeclarationSymbolId = null;
    }

    void addInitializedSymbolToScope(Token token, Symbol.IdType idType) throws Exception {
        addVariableToScope(token, true, idType);
    }

    void addUninitializedSymbolToScope(Token token, Symbol.IdType idType) throws Exception {
        addVariableToScope(token, false, idType);
    }

    List<Symbol> getUnusedSymbolsFromScope(Integer scope) {
        List<Symbol> unusedSymbolsFromScope = new ArrayList<>();
        for (Map.Entry<String, Symbol> symbol : scopeToSymbols.peek().entrySet()) {
            if (!symbol.getValue().isUsed()) unusedSymbolsFromScope.add(symbol.getValue());
        }
        return unusedSymbolsFromScope;
    }

    void addWarningForUnusedSymbols() {
        for (Symbol symbol : unusedSymbols) {
            addNewWarning("\"" + symbol.getId() + "\" was declared in scope " + symbol.getScope() + " but was not utilized;");
        }
    }

    void addNewWarning(String newWarning) {
        System.out.println("WARNING: " + newWarning);
        warnings.add(newWarning);
    }

}
