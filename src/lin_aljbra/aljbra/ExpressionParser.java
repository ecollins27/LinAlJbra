package lin_aljbra.aljbra;

import lin_aljbra.aljbra.trig.Trig;

import java.lang.reflect.InvocationTargetException;

public final class ExpressionParser {

    public static Expression parse(String str) {
        str = str.replace(" ", "");
        if (str.equals("")) {
            return Scalar.ZERO;
        } else if (str.charAt(0) == '(' && Expression.getMatchingDelimeter(str, 0) == str.length() - 1) {
            return parse(str.substring(1, str.length() - 1));
        } else if (Sum.isSum(str)){
            return Sum.parseSum(str);
        } else if (Product.isProduct(str)){
            return Product.parseProduct(str);
        } else if (Exponential.isExponential(str)){
            return Exponential.parseExponential(str);
        } else if (isTrig(str)){
            return parseTrig(str);
        } else if (Log.isLog(str)){
            return Log.parseLog(str);
        } else if (Abs.isAbs(str)){
            return Abs.parseAbs(str);
        } else if (Scalar.isScalar(str)){
            return Scalar.parseScalar(str);
        } else if (Decimal.isDecimal(str)){
            return Decimal.parseDecimal(str);
        } else if (Constant.isConstant(str)){
            return Constant.parseConstant(str);
        } else if (Variable.isVariable(str)) {
            return Variable.parseVariable(str);
        }
        throw new RuntimeException("Cannot convert " + str + " to Expression");
    }

    protected static boolean isTrig(String str){
        String[] names = new String[]{"sin","cos","tan","asin","acos","atan"};
        for (String name: names) {
            if (str.length() < name.length() + 3) {
                return false;
            } else if (str.substring(0, name.length()).equals(name) && str.charAt(name.length()) == '(' && Expression.getMatchingDelimeter(str, name.length()) == str.length() - 1) {
                return true;
            }
        }
        return false;
    }

    protected static Expression parseTrig(String str){
        String name = str.substring(0,str.indexOf('('));
        Expression operand = ExpressionParser.parse(str.substring(str.indexOf('(') + 1,str.length() - 1));
        try {
            return (Expression) Trig.class.getMethod(name,Expression.class).invoke(null,operand);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
