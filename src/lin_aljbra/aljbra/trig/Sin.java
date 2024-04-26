package lin_aljbra.aljbra.trig;

import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Variable;

class Sin extends Trig {
    Sin(Expression e) {
        super(e, Math::sin, Sin.class);
    }

    @Override
    public Expression derivative(Variable v) {
        return Trig.cos(operand).multiply(operand.derivative(v));
    }

    @Override
    public Expression withDecimals() {
        return Trig.sin(operand.withDecimals());
    }
}
