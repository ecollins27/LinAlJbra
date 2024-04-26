package lin_aljbra.aljbra.trig;

import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Scalar;
import lin_aljbra.aljbra.Variable;

class ASin extends Trig {

    ASin(Expression e){
        super(e,Math::asin, ASin.class);
    }

    @Override
    public Expression derivative(Variable v) {
        return Scalar.ONE.subtract(operand.pow(Scalar.TWO)).sqrt().invert().multiply(operand.derivative(v));
    }

    @Override
    public Expression withDecimals() {
        return Trig.asin(operand.withDecimals());
    }
}
