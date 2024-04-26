package lin_aljbra.aljbra.trig;

import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Scalar;
import lin_aljbra.aljbra.Variable;

class Tan extends Trig {

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
}
