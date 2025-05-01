ruleset {

    description '''
        Custom CodeNarc ruleset tailored for SAP CPI Groovy scripts.
        Focuses on maintainability, readability, and safe patterns within integration-centric code.
    '''

    // Basic hygiene and syntax rules
    DeadCode
    EmptyMethod
    EmptyIfStatement
    EmptyCatchBlock
    DuplicateStringLiteral
    DuplicateMapKey
    DuplicateSetValue
    ReturnFromFinallyBlock
    ThrowExceptionFromFinallyBlock

    // Convention and design
    // ImplicitReturnStatement
    IfStatementCouldBeTernary
    CouldBeElvis
    MethodParameterTypeRequired
    MethodReturnTypeRequired
    ParameterReassignment
    PublicMethodsBeforeNonPublicMethods
    StaticMethodsBeforeInstanceMethods
    VariableTypeRequired
    // NoDef

    // Logging and exception safety
    Println
    PrintStackTrace
    SystemOutPrint
    SystemErrPrint
    LoggingSwallowsStacktrace

    // Formatting
    Indentation
    LineLength
    BracesForIfElse
    BracesForMethod
    TrailingWhitespace
    SpaceAroundOperator
    ConsecutiveBlankLines

    // Unnecessary constructs
    UnnecessaryCast
    UnnecessaryDefInVariableDeclaration
    UnnecessarySafeNavigationOperator
    UnnecessaryParenthesesForMethodCallWithClosure
    UnnecessaryBooleanExpression
    // UnnecessaryReturnKeyword

    // Naming
    MethodName
    VariableName
    ParameterName
    ClassName
    FieldName

    // Security and safety
    InsecureRandom
    SystemExit

    // Grails/Spock/Jenkins-specific rules excluded
    // Test-specific rules excluded (JUnit, Spock, etc.)

    // Optional: comment in if script sizes become a problem
    // MethodSize
    // CyclomaticComplexity
}
