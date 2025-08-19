package Condition;

/**
 * Defines a cost for a certain parameter. This is needed to be used in graphs for figuring out, how expensive
 * a given path is.
 */
public class ParameterCost {
    private Number _value;
    private String _parameterName;

    public Number getValue() {
        return _value;
    }

    public void setValue(Number value) {
        _value = value;
    }

    public String getParameterName() {
        return _parameterName;
    }

    public void setParameterName(String name) {
        _parameterName = name;
    }

    /**
     * This class allows you store costs for a given string (namely a parameter). The costs have to be
     * represented in int. For the moment you need to change the unit (e.g. nanosecond not second). This has to be
     * the same unit for the same parameters!
     * @param value The value of the cost
     * @param label The label to identify the cost
     */
    public ParameterCost(Number value, String label) {
        _value = value;
        _parameterName = label;
    }

    /**
     * Copy constructor. The values of each parameter cost will be summed up.
     * @param copyOne Object one to copy
     * @param copyTwo Object two to copy
     */
    public ParameterCost(ParameterCost copyOne, ParameterCost copyTwo) {
        _parameterName = copyOne.getParameterName();
        if(copyOne.getValue() instanceof Integer && copyTwo.getValue() instanceof Integer)
        {
            _value = copyOne.getValue().intValue() + copyTwo.getValue().intValue();
        }
        else if(copyOne.getValue() instanceof Long && copyTwo.getValue() instanceof Long) {
            _value = copyOne.getValue().longValue() + copyTwo.getValue().longValue();
        }
    }

    public boolean canBeCompared(ParameterCost condition) {
        return condition.getParameterName().equals(_parameterName);
    }

    public void addCost(ParameterCost costsToAdd) {
        if(_value instanceof Integer) {
            if(!(costsToAdd.getValue() instanceof  Integer)){
                throw new RuntimeException("Trying to add Non-Integer-Value to Integer-Value.");
            }
            _value = _value.intValue() + costsToAdd.getValue().intValue();
        } else if(_value instanceof Long) {
            if(!(costsToAdd.getValue() instanceof  Long)){
                throw new RuntimeException("Trying to add Non-Long-Value to Long-Value.");
            }
            _value = _value.longValue() + costsToAdd.getValue().longValue();
        }
    }

    public void multiplyPercentageCost(ParameterCost costToMultiply) {
        if(_value instanceof Integer) {
            if(!(costToMultiply.getValue() instanceof  Integer)){
                throw new RuntimeException("Trying to add Non-Integer-Value to Integer-Value.");
            }
            _value = _value.intValue() * costToMultiply.getValue().intValue()/100;
        } else if(_value instanceof Long) {
            if(!(costToMultiply.getValue() instanceof  Long)){
                throw new RuntimeException("Trying to add Non-Long-Value to Long-Value.");
            }
            _value = _value.longValue() * costToMultiply.getValue().longValue()/100;
        } else if(_value instanceof Double) {
            if(!(costToMultiply.getValue() instanceof  Double)){
                throw new RuntimeException("Trying to add Non-Double-Value to Long-Value.");
            }
            _value = _value.doubleValue() * costToMultiply.getValue().doubleValue()/100d;
        } else if(_value instanceof Short ) {
            if(!(costToMultiply.getValue() instanceof  Short)){
                throw new RuntimeException("Trying to add Non-Short-Value to Long-Value.");
            }
            _value = _value.shortValue() * costToMultiply.getValue().shortValue()/100;
        }

    }

    public ParameterCost Copy() {
        return new ParameterCost(_value, _parameterName);
    }
}
