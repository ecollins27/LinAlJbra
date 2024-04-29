package lin_aljbra.aljbra.trig;

import lin_aljbra.aljbra.*;

class Tan extends Trig {

    protected static Expression[] LOOKUP_TABLE;

    static {
        Expression pi12 = new Scalar(6).sqrt().subtract(Scalar.TWO.sqrt()).divide(new Scalar(6).sqrt().add(Scalar.TWO.sqrt()));
        Expression pi6 = new Scalar(3).sqrt();
        Expression pi4 = Scalar.ONE;
        Expression pi3 = pi6.invert();
        Expression pi512 = pi12.invert();
        LOOKUP_TABLE = new Expression[]{
                Scalar.ZERO, pi12, pi6, pi4, pi3, pi512,
                Decimal.NAN, pi512.negate(), pi3.negate(), pi4.negate(), pi6.negate(), pi12.negate(),
                Scalar.ZERO, pi12, pi6, pi4, pi3, pi512,
                Decimal.NAN, pi512.negate(), pi3.negate(), pi4.negate(), pi6.negate(), pi12.negate(),
        };
    }

    Tan(Expression e){
        super(e,Math::tan,Tan.class);
    }

    @Override
    public Expression derivative(Variable v) {
        return Trig.cos(operand).pow(Scalar.TWO).invert().multiply(operand.derivative(v));
    }

    @Override
    public Expression withDecimals() {
        return Trig.tan(operand.withDecimals());
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
