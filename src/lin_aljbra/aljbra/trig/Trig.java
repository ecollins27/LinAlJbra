package lin_aljbra.aljbra.trig;

import lin_aljbra.aljbra.*;

import java.lang.reflect.InvocationTargetException;

public abstract class Trig extends Expression {

    public static Expression cos(Expression e){
        if (e instanceof ACos){
            return ((ACos) e).operand;
        } else if (e instanceof Decimal){
            return new Decimal(Math.cos(((Decimal) e).getValue()));
        }
        return new Cos(e);
    }
    public static Expression sin(Expression e){
        if (e instanceof ASin){
            return ((ASin) e).operand;
        } else if (e instanceof Decimal){
            return new Decimal(Math.sin(((Decimal) e).getValue()));
        }
        return new Sin(e);
    }
    public static Expression tan(Expression e){
        if (e instanceof ATan){
            return ((ATan) e).operand;
        } else if (e instanceof Decimal){
            return new Decimal(Math.tan(((Decimal) e).getValue()));
        }
        return new Tan(e);
    }
    public static Expression acos(Expression e){
        if (e instanceof Cos){
            return ((Cos) e).operand;
        } else if (e instanceof Decimal){
            return new Decimal(Math.acos(((Decimal) e).getValue()));
        }
        return new ACos(e);
    }
    public static Expression asin(Expression e){
        if (e instanceof Sin){
            return ((Sin) e).operand;
        } else if (e instanceof Decimal){
            return new Decimal(Math.asin(((Decimal) e).getValue()));
        }
        return new ASin(e);
    }
    public static Expression atan(Expression e){
        if (e instanceof Tan){
            return ((Tan) e).operand;
        } else if (e instanceof Decimal){
            return new Decimal(Math.atan(((Decimal) e).getValue()));
        }
        return new ATan(e);
    }

    Function function;
    String name;
    Class<? extends Trig> clazz;
    Class<? extends Trig> inverseClass;
    protected Expression operand;
    Trig(Expression operand, Function function, Class<? extends Trig> clazz) {
        this.operand = operand;
        this.function = function;
        this.clazz = clazz;
        this.name = clazz.getName();
        String inverseName;
        int index = name.lastIndexOf(".") + 1;
        if (name.charAt(name.lastIndexOf(".") + 1) == 'A'){
            inverseName = name.substring(0,index) + name.substring(index + 1);
        } else {
            inverseName = name.substring(0,index) + "A" + name.substring(index);
        }
        try {
            inverseClass = (Class<? extends Trig>) Class.forName(inverseName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.name = this.name.toLowerCase();
        this.name = this.name.substring(this.name.lastIndexOf(".") + 1);
    }

    @Override
    public Expression negate() {
        return this.multiply(Scalar.NEG_ONE);
    }

    @Override
    public Expression invert() {
        return this.pow(Scalar.NEG_ONE);
    }

    @Override
    public boolean contains(Expression e) {
        if (this.equals(e)){
            return true;
        }
        return operand.contains(e);
    }

    @Override
    public boolean isEvaluable() {
        return operand.isEvaluable();
    }

    @Override
    protected Expression __add__(Expression e) {
        return null;
    }

    @Override
    protected Expression __multiply__(Expression e) {
        return null;
    }

    @Override
    protected Expression __pow__(Expression e) {
        return null;
    }

    @Override
    protected boolean isAdditionCompatible(Expression e) {
        return false;
    }

    @Override
    protected boolean isMultiplicationCompatible(Expression e) {
        return false;
    }

    @Override
    protected boolean isPowCompatible(Expression e) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return clazz.isInstance(o) && clazz.cast(o).operand.equals(operand);
    }

    @Override
    public double eval(VariableMap values) {
        return function.eval(operand.eval(values));
    }

    @Override
    public Expression replace(Expression e, Expression with) {
        if (this.equals(e)){
            return with;
        }
        return newInstance(operand.replace(e,with));
    }

    @Override
    public String toString() {
        return name + "(" + operand.toString() + ")";
    }

    @Override
    public String toLaTeX() {
        return "\\" + name + "(" + operand.toLaTeX() + ")";
    }

    @Override
    public Expression simplify() {
        Expression simplifiedOperand = operand.simplify();
        if (inverseClass.isInstance(simplifiedOperand)) {
            return simplifiedOperand;
        } else if (simplifiedOperand instanceof Decimal){
            return new Decimal(function.eval(((Decimal) simplifiedOperand).getValue()));
        } else {
            return newInstance(simplifiedOperand);
        }
    }

    private Expression newInstance(Expression e){
        try {
            return (Expression) Trig.class.getMethod(name,Expression.class).invoke(null,e);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    static interface Function {
        double eval(double n);
    }
}
