package eighty4.akimbo.compile.resolution.expression.visitor;

import eighty4.akimbo.expressions.AkimboExpression;
import eighty4.akimbo.expressions.AkimboExpressionsParser;
import eighty4.akimbo.expressions.AkimboExpressionsParserListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Ignore;
import org.junit.Test;

public class DebugExpressionVisitor implements AkimboExpressionsParserListener {

    @Test
    @Ignore
    public void asdf() {
        AkimboExpression akimboExpression = AkimboExpression.fromString("new @foo.Bar(#dependency).build()");
        akimboExpression.evaluate(new DebugExpressionVisitor());
    }

    @Override
    public void enterExpression(AkimboExpressionsParser.ExpressionContext ctx) {
        System.out.println("enterExpression: " + ctx.getText());
    }

    @Override
    public void exitExpression(AkimboExpressionsParser.ExpressionContext ctx) {
        System.out.println("exitExpression: " + ctx.getText());
    }

    @Override
    public void enterOngoingExpression(AkimboExpressionsParser.OngoingExpressionContext ctx) {
        System.out.println("enterOngoingExpression: " + ctx.getText());
    }

    @Override
    public void exitOngoingExpression(AkimboExpressionsParser.OngoingExpressionContext ctx) {
        System.out.println("exitOngoingExpression: " + ctx.getText());
    }

    @Override
    public void enterMethodOrProperty(AkimboExpressionsParser.MethodOrPropertyContext ctx) {
        System.out.println("enterMethodOrProperty: " + ctx.getText());
    }

    @Override
    public void exitMethodOrProperty(AkimboExpressionsParser.MethodOrPropertyContext ctx) {
        System.out.println("exitMethodOrProperty: " + ctx.getText());
    }

    @Override
    public void enterInstanceCreation(AkimboExpressionsParser.InstanceCreationContext ctx) {
        System.out.println("enterInstanceCreation: " + ctx.getText());
    }

    @Override
    public void exitInstanceCreation(AkimboExpressionsParser.InstanceCreationContext ctx) {
        System.out.println("exitInstanceCreation: " + ctx.getText());
    }

    @Override
    public void enterComponentReference(AkimboExpressionsParser.ComponentReferenceContext ctx) {
        System.out.println("enterComponentReference: " + ctx.getText());
    }

    @Override
    public void exitComponentReference(AkimboExpressionsParser.ComponentReferenceContext ctx) {
        System.out.println("exitComponentReference: " + ctx.getText());
    }

    @Override
    public void enterTypeReference(AkimboExpressionsParser.TypeReferenceContext ctx) {
        System.out.println("enterTypeReference: " + ctx.getText());
    }

    @Override
    public void exitTypeReference(AkimboExpressionsParser.TypeReferenceContext ctx) {
        System.out.println("exitTypeReference: " + ctx.getText());
    }

    @Override
    public void enterAkimboTypeReference(AkimboExpressionsParser.AkimboTypeReferenceContext ctx) {
        System.out.println("enterAkimboTypeReference: " + ctx.getText());
    }

    @Override
    public void exitAkimboTypeReference(AkimboExpressionsParser.AkimboTypeReferenceContext ctx) {
        System.out.println("exitAkimboTypeReference: " + ctx.getText());
    }

    @Override
    public void enterUserTypeReference(AkimboExpressionsParser.UserTypeReferenceContext ctx) {
        System.out.println("enterUserTypeReference: " + ctx.getText());
    }

    @Override
    public void exitUserTypeReference(AkimboExpressionsParser.UserTypeReferenceContext ctx) {
        System.out.println("exitUserTypeReference: " + ctx.getText());
    }

    @Override
    public void enterPackageQualifiedTypeName(AkimboExpressionsParser.PackageQualifiedTypeNameContext ctx) {
        System.out.println("enterPackageQualifiedTypeName: " + ctx.getText());
    }

    @Override
    public void exitPackageQualifiedTypeName(AkimboExpressionsParser.PackageQualifiedTypeNameContext ctx) {
        System.out.println("exitPackageQualifiedTypeName: " + ctx.getText());
    }

    @Override
    public void enterArguments(AkimboExpressionsParser.ArgumentsContext ctx) {
        System.out.println("enterArguments: " + ctx.getText());
    }

    @Override
    public void exitArguments(AkimboExpressionsParser.ArgumentsContext ctx) {
        System.out.println("exitArguments: " + ctx.getText());
    }

    @Override
    public void enterArgumentList(AkimboExpressionsParser.ArgumentListContext ctx) {
        System.out.println("enterArgumentList: " + ctx.getText());
    }

    @Override
    public void exitArgumentList(AkimboExpressionsParser.ArgumentListContext ctx) {
        System.out.println("exitArgumentList: " + ctx.getText());
    }

    @Override
    public void enterPropertyValue(AkimboExpressionsParser.PropertyValueContext ctx) {
        System.out.println("enterPropertyValue: " + ctx.getText());
    }

    @Override
    public void exitPropertyValue(AkimboExpressionsParser.PropertyValueContext ctx) {
        System.out.println("exitPropertyValue: " + ctx.getText());
    }

    @Override
    public void enterLiteralValue(AkimboExpressionsParser.LiteralValueContext ctx) {
        System.out.println("enterLiteralValue: " + ctx.getText());
    }

    @Override
    public void exitLiteralValue(AkimboExpressionsParser.LiteralValueContext ctx) {
        System.out.println("exitLiteralValue: " + ctx.getText());
    }

    @Override
    public void enterNumberValue(AkimboExpressionsParser.NumberValueContext ctx) {
        System.out.println("enterNumberValue: " + ctx.getText());
    }

    @Override
    public void exitNumberValue(AkimboExpressionsParser.NumberValueContext ctx) {
        System.out.println("exitNumberValue: " + ctx.getText());
    }

    @Override
    public void enterStringValue(AkimboExpressionsParser.StringValueContext ctx) {
        System.out.println("enterStringValue: " + ctx.getText());
    }

    @Override
    public void exitStringValue(AkimboExpressionsParser.StringValueContext ctx) {
        System.out.println("exitStringValue: " + ctx.getText());
    }

    @Override
    public void enterBooleanValue(AkimboExpressionsParser.BooleanValueContext ctx) {
        System.out.println("enterBooleanValue: " + ctx.getText());
    }

    @Override
    public void exitBooleanValue(AkimboExpressionsParser.BooleanValueContext ctx) {
        System.out.println("exitBooleanValue: " + ctx.getText());
    }

    @Override
    public void enterNullValue(AkimboExpressionsParser.NullValueContext ctx) {
        System.out.println("enterNullValue: " + ctx.getText());
    }

    @Override
    public void exitNullValue(AkimboExpressionsParser.NullValueContext ctx) {
        System.out.println("exitNullValue: " + ctx.getText());
    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }
}
