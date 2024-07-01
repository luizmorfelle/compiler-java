package structures;

import java.util.ArrayList;
import java.util.List;

public class Symbol {

    private String id;
    private String declarationType; // OR private DataType dataType;

    private DATA_TYPE dataType;
    private Integer scope;
    private boolean isInitialized;
    private boolean isUsed;
    private IdType idType;
    private Object value;
    private int arraySize;
    private List<Object> values = new ArrayList<>();


    public enum IdType {VARIABLE, ARRAY, FUNCTION, PARAM}


    public Symbol() {
    }

    public Symbol(String id, String declarationType, Integer scope, boolean isInitialized, IdType idType, Object value, DATA_TYPE dataType, int arraySize, List<Object> values) {
        this.id = id;
        this.declarationType = declarationType;
        this.scope = scope;
        this.isInitialized = isInitialized;
        this.isUsed = false;
        this.idType = idType;
        this.value = value;
        this.dataType = dataType;
        this.arraySize = arraySize;
        this.values = values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeclarationType() {
        return declarationType;
    }

    public void setDeclarationType(String declarationType) {
        this.declarationType = declarationType;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public void setDataType(DATA_TYPE dataType) {
        this.dataType = dataType;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
