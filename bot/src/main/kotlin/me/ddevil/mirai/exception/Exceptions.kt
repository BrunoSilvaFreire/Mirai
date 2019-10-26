package me.ddevil.mirai.exception


class ValueNotFoundException : IllegalStateException {

    constructor(cause: Throwable, desiredObject: String) : super(generateMessage(desiredObject), cause)

    constructor(desiredObject: String) : super(generateMessage(desiredObject))

    companion object {
        fun generateMessage(desiredObject: String) = "Couldn't find object $desiredObject!"
    }

}

class IllegalValueTypeException(val expectedType: Class<*>, val typeFound: Class<*>) :
    IllegalStateException(generateMessage(expectedType, typeFound)) {
    companion object {
        fun generateMessage(expectedType: Class<*>, typeFound: Class<*>) =
            "Expected value of type ${expectedType.name}, but got ${typeFound.name}!"
    }

}

class WTFException : IllegalStateException("This shouldn't happen")
