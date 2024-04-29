package lin_aljbra.aljbra.trig;

import lin_aljbra.aljbra.Constant;
import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Scalar;
import lin_aljbra.aljbra.Variable;

class Cos extends Trig {

    protected static Expression[] LOOKUP_TABLE;

    static {
        Expression pi12 = new Scalar(6).sqrt().add(Scalar.TWO.sqrt()).divide(new Scalar(4));
        Expression pi6 = new Scalar(3).sqrt().divide(Scalar.TWO);
        Expression pi4 = Scalar.TWO.sqrt().invert();
        Expression pi3 = Scalar.TWO.invert();
        Expression pi512 = new Scalar(6).sqrt().subtract(Scalar.TWO.sqrt()).divide(new Scalar(4));
        LOOKUP_TABLE = new Expression[]{
                Scalar.ONE, pi12, pi6, pi4, pi3, pi512,
                Scalar.ZERO, pi512.negate(), pi3.negate(), pi4.negate(), pi6.negate(), pi12.negate(),
                Scalar.NEG_ONE, pi12.negate(), pi6.negate(), pi4.negate(), pi3.negate(), pi512.negate(),
                Scalar.ZERO, pi512, pi3, pi4, pi6, pi12
        };
    }

    Cos(Expression e){
        super(e,Math::cos,Cos.class);
    }

    @Override
    public Expression derivative(Variable v) {
        return Trig.sin(operand).negate().multiply(operand.derivative(v));
    }

    @Override
    public Expression withDecimals() {
        return Trig.cos(operand.withDecimals());
    }

    protected static Expression lookup(Expression e){
        if (!e.isEvaluable()){
            return null;
        }
        Expression expressionIndex = e.divide(Constant.PI).multiply(new Scalar(12));
        if (!(expressionIndex instanceof Scalar)){
            return null;
        }
        int index = (int) ((Scalar) expressionIndex).getValue();
        while (index < 0){
            index += 24;
        }
        return LOOKUP_TABLE[index % 24];
    }
}
